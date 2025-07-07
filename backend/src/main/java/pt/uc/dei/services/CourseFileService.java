package pt.uc.dei.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tika.Tika;

import java.io.*;
import java.nio.file.*;
import java.util.List;

public class CourseFileService {
    private static final Logger LOGGER = LogManager.getLogger(CourseFileService.class);
    private static final List<String> MIME_TYPES_ALLOWED = List.of(
            "image/jpeg",
            "image/jpg",
            "image/png",
            "image/webp"
    );
    private static final int MAX_BYTES = 5 * 1024 * 1024;

    public static Path getCourseStoragePath() {
        System.out.println(System.getProperty("course.storage.path"));
        String configuredPath = System.getProperty("course.storage.path"); // Uses the same property as avatars for now
        if (configuredPath == null || configuredPath.isBlank()) {
            LOGGER.error("System property 'course.storage.path' not configured for courses");
            throw new IllegalStateException("System property 'course.storage.path' not set for courses!");
        }
        Path path = Paths.get(configuredPath).toAbsolutePath().normalize();
        LOGGER.debug("Resolved course image storage path: {}", path);
        return path;
    }

    public static boolean isValidMimeType(InputStream inputStream) {
        try (BufferedInputStream bufferedStream = new BufferedInputStream(inputStream)) {
            bufferedStream.mark(MAX_BYTES + 1);
            String mimeType = new Tika().detect(bufferedStream);
            bufferedStream.reset();
            boolean isValid = MIME_TYPES_ALLOWED.contains(mimeType);
            if (!isValid) {
                LOGGER.warn("Invalid MIME type detected: {}", mimeType);
            }
            return isValid;
        } catch (IOException e) {
            LOGGER.error("Error detecting MIME type: {}", e.getMessage());
            return false;
        }
    }

    public static String getFilename(Long id, byte[] fileBytes) {
        try {
            String mimeType = new Tika().detect(new ByteArrayInputStream(fileBytes));
            String extension = switch (mimeType) {
                case "image/jpeg", "image/jpg" -> ".jpg";
                case "image/png" -> ".png";
                case "image/webp" -> ".webp";
                default -> {
                    LOGGER.warn("Unsupported MIME type for course image: {}", mimeType);
                    yield "";
                }
            };
            return id + extension;
        } catch (Exception e) {
            LOGGER.error("Failed to generate filename for course ID {}: {}", id, e.getMessage());
            throw new RuntimeException("Failed to generate filename", e);
        }
    }

    public static boolean saveFileWithSizeLimit(InputStream inputStream, String filename) {
        Path filePath = null;
        try {
            Path uploadDir = getCourseStoragePath();
            Files.createDirectories(uploadDir);
            filePath = uploadDir.resolve(filename);
            LOGGER.info("Attempting to save course image to: {}", filePath);
            try (InputStream in = new BufferedInputStream(inputStream);
                 OutputStream out = Files.newOutputStream(filePath,
                         StandardOpenOption.CREATE,
                         StandardOpenOption.TRUNCATE_EXISTING)) {
                byte[] buffer = new byte[8192];
                long total = 0;
                int bytesRead;
                int loopCount = 0;
                while ((bytesRead = in.read(buffer)) != -1) {
                    total += bytesRead;
                    if (total > MAX_BYTES) {
                        LOGGER.warn("File size exceeds maximum allowed size ({} bytes)", MAX_BYTES);
                        return false;
                    }
                    loopCount++;
                    out.write(buffer, 0, bytesRead);
                }
                LOGGER.debug("Course image saved successfully. Loops: {}, Total bytes: {}", loopCount, total);
                return true;
            }
        } catch (IOException e) {
            LOGGER.error("Failed to save course image file {}: {}", filePath, e.getMessage());
            if (filePath != null) {
                try {
                    Files.deleteIfExists(filePath);
                } catch (IOException ex) {
                    LOGGER.warn("Failed to clean up partially written course image file {}: {}", filePath, ex.getMessage());
                }
            }
            return false;
        }
    }

    public static Path resolveCourseImagePath(Long id) {
        Path courseDir = getCourseStoragePath();
        List<String> extensions = List.of(".jpg", ".jpeg", ".png", ".webp");
        for (String ext : extensions) {
            Path candidate = courseDir.resolve(id + ext);
            if (Files.exists(candidate)) {
                return candidate;
            }
        }
        return null;
    }

    public static boolean removeExistingCourseImages(Long id) {
        try {
            Path courseDir = getCourseStoragePath();
            List<String> extensions = List.of(".jpg", ".jpeg", ".png", ".webp");
            for (String ext : extensions) {
                Path candidate = courseDir.resolve(id + ext);
                if (Files.exists(candidate)) {
                    Files.deleteIfExists(candidate);
                }
            }
            return true;
        } catch (IOException e) {
            LOGGER.error("Failed to remove existing course image file: {}", e.getMessage());
            return false;
        }
    }

    public static String getMimeType(Path filePath) {
        try {
            return Files.probeContentType(filePath);
        } catch (IOException e) {
            LOGGER.warn("Could not detect MIME type for {}: {}", filePath, e.getMessage());
            return null;
        }
    }

    public static String getMimeTypeForCourse(Long id) {
        Path path = resolveCourseImagePath(id);
        if (path != null) {
            return getMimeType(path);
        }
        return null;
    }

    public static CacheData getCacheData(Path filePath) throws IOException {
        return new CacheData(
                Files.getLastModifiedTime(filePath).toMillis(),
                Files.size(filePath),
                Files.probeContentType(filePath)
        );
    }

    public static class CacheData {
        public final long lastModified;
        public final long fileSize;
        public final String mimeType;
        public CacheData(long lastModified, long fileSize, String mimeType) {
            this.lastModified = lastModified;
            this.fileSize = fileSize;
            this.mimeType = mimeType;
        }
    }
}
