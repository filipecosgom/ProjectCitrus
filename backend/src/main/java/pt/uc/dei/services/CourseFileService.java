package pt.uc.dei.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tika.Tika;

import java.io.*;
import java.nio.file.*;
import java.util.List;

/**
 * Service class for handling course image file operations such as storage, validation, retrieval, and deletion.
 * <p>
 * Supports MIME type validation, file size limits, and file system operations for course images.
 * Uses Apache Tika for MIME type detection and Java NIO for file management.
 * </p>
 */
public class CourseFileService {
    private static final Logger LOGGER = LogManager.getLogger(CourseFileService.class);
    private static final List<String> MIME_TYPES_ALLOWED = List.of(
            "image/jpeg",
            "image/jpg",
            "image/png",
            "image/webp"
    );
    private static final int MAX_BYTES = 5 * 1024 * 1024;

    /**
     * Retrieves the configured storage path for course image files from the system property 'course.storage.path'.
     *
     * @return The absolute, normalized path to the course image storage directory.
     * @throws IllegalStateException if the system property is not set or blank.
     */
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

    /**
     * Checks if the MIME type of the provided input stream is allowed for course image uploads.
     *
     * @param inputStream The input stream of the file to check.
     * @return true if the MIME type is allowed, false otherwise.
     */
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

    /**
     * Generates a filename for the course image based on course ID and detected MIME type.
     *
     * @param id        The course ID.
     * @param fileBytes The byte array of the file to detect MIME type.
     * @return The generated filename with appropriate extension, or ID with no extension if unsupported.
     * @throws RuntimeException if MIME type detection fails.
     */
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

    /**
     * Saves a file to the course image storage directory, enforcing a maximum file size limit.
     *
     * @param inputStream The input stream of the file to be saved.
     * @param filename    The desired filename for the saved file.
     * @return true if the file was saved successfully and within size limit, false otherwise.
     */
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

    /**
     * Resolves the course image file path for a course by ID, checking all supported file extensions.
     *
     * @param id The course ID.
     * @return The Path to the course image file if found, or null if not found.
     */
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

    /**
     * Removes all existing course image files for a course by ID, across all supported extensions.
     *
     * @param id The course ID.
     * @return true if all files were removed successfully, false otherwise.
     */
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

    /**
     * Gets the MIME type of a file by its path using the system's file type detector.
     *
     * @param filePath The path to the file.
     * @return The MIME type string, or null if not detected.
     */
    public static String getMimeType(Path filePath) {
        try {
            return Files.probeContentType(filePath);
        } catch (IOException e) {
            LOGGER.warn("Could not detect MIME type for {}: {}", filePath, e.getMessage());
            return null;
        }
    }

    /**
     * Gets the MIME type of a course image file by course ID.
     *
     * @param id The course ID.
     * @return The MIME type string, or null if not found.
     */
    public static String getMimeTypeForCourse(Long id) {
        Path path = resolveCourseImagePath(id);
        if (path != null) {
            return getMimeType(path);
        }
        return null;
    }

    /**
     * Gets cache metadata for a file, including last modified time, size, and MIME type.
     *
     * @param filePath The path to the file.
     * @return CacheData object with file metadata.
     * @throws IOException if file attributes cannot be read.
     */
    public static CacheData getCacheData(Path filePath) throws IOException {
        return new CacheData(
                Files.getLastModifiedTime(filePath).toMillis(),
                Files.size(filePath),
                Files.probeContentType(filePath)
        );
    }

    /**
     * Data class representing cache metadata for a file, including last modified time, file size, and MIME type.
     */
    public static class CacheData {
        /** The last modified time of the file in milliseconds since epoch. */
        public final long lastModified;
        /** The size of the file in bytes. */
        public final long fileSize;
        /** The MIME type of the file. */
        public final String mimeType;
        /**
         * Constructs a CacheData object with the given metadata.
         *
         * @param lastModified The last modified time in milliseconds since epoch.
         * @param fileSize     The size of the file in bytes.
         * @param mimeType     The MIME type of the file.
         */
        public CacheData(long lastModified, long fileSize, String mimeType) {
            this.lastModified = lastModified;
            this.fileSize = fileSize;
            this.mimeType = mimeType;
        }
    }
}
