package pt.uc.dei.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tika.Tika;

import java.io.*;
import java.nio.file.*;
import java.util.List;
import java.util.Optional;

public class FileService {
    private static final Logger LOGGER = LogManager.getLogger(FileService.class);
    private static final List<String> MIME_TYPES_ALLOWED = List.of(
            "image/jpeg",
            "image/jpg",
            "image/png",
            "image/webp"
    );
    private static final int MAX_BYTES = 5 * 1024 * 1024;

    public static Path getAvatarStoragePath() {
        String configuredPath = System.getProperty("avatar.storage.path");
        if (configuredPath == null || configuredPath.isBlank()) {
            LOGGER.error("System property 'avatar.storage.path' not configured");
            throw new IllegalStateException("System property 'avatar.storage.path' not set!");
        }

        Path path = Paths.get(configuredPath).toAbsolutePath().normalize();
        LOGGER.debug("Resolved avatar storage path: {}", path);
        return path;
    }

    public static boolean isValidMimeType(InputStream inputStream) {
        try (BufferedInputStream bufferedStream = new BufferedInputStream(inputStream)) {
            bufferedStream.mark(MAX_BYTES + 1);  // Allow Tika to re-read the stream
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
                    LOGGER.warn("Unsupported MIME type for avatar: {}", mimeType);
                    yield "";
                }
            };
            return id + extension;
        } catch (Exception e) {
            LOGGER.error("Failed to generate filename for ID {}: {}", id, e.getMessage());
            throw new RuntimeException("Failed to generate filename", e);
        }
    }

    /**
     * Saves a file with a size limit, ensuring it does not exceed the maximum allowed bytes.
     *
     * @param inputStream The input stream of the file to be saved
     * @param filename    The desired filename for the saved file
     * @return true if the file was saved successfully, false if it exceeds the size limit
     */
    public static boolean saveFileWithSizeLimit(InputStream inputStream, String filename) {
        Path filePath = null;
        try {
            Path uploadDir = getAvatarStoragePath();
            Files.createDirectories(uploadDir);
            filePath = uploadDir.resolve(filename);
            LOGGER.info("Attempting to save avatar to: {}", filePath);
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

                LOGGER.debug("File saved successfully. Loops: {}, Total bytes: {}", loopCount, total);
                return true;
            }
        } catch (IOException e) {
            LOGGER.error("Failed to save file {}: {}", filePath, e.getMessage());
            // Clean up partially written file
            if (filePath != null) {
                try {
                    Files.deleteIfExists(filePath);
                } catch (IOException ex) {
                    LOGGER.warn("Failed to clean up partially written file {}: {}", filePath, ex.getMessage());
                }
            }
            return false;
        }
    }

    /**
     * Resolves the avatar file path for a user by ID, checking all supported extensions.
     *
     * @param id The user ID
     * @return The Path to the avatar file if found, or null if not found
     */
    public static Path resolveAvatarPath(Long id) {
        Path avatarDir = getAvatarStoragePath();
        List<String> extensions = List.of(".jpg", ".jpeg", ".png", ".webp");

        for (String ext : extensions) {
            Path candidate = avatarDir.resolve(id + ext);
            if (Files.exists(candidate)) {
                return candidate;
            }
        }

        return null;
    }

    /**
     * Removes all existing avatar files for a user by ID.
     *
     * @param id The user ID
     * @return true if all files were removed successfully, false otherwise
     */
    public static boolean removeExistingFiles(Long id) {
        try {
            Path avatarDir = getAvatarStoragePath();
            List<String> extensions = List.of(".jpg", ".jpeg", ".png", ".webp");
            for (String ext : extensions) {
                Path candidate = avatarDir.resolve(id + ext);
                if (Files.exists(candidate)) {
                    Files.deleteIfExists(candidate);
                }
            }
            return true;
        } catch (IOException e) {
            LOGGER.error("Failed to remove existing file: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Gets the MIME type of a file by its path.
     *
     * @param filePath The path to the file
     * @return The MIME type string, or null if not detected
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
     * Gets the MIME type of a user's avatar file by user ID.
     *
     * @param id The user ID
     * @return The MIME type string, or null if not found
     */
    public static String getMimeTypeForUser(Long id) {
        Path path = resolveAvatarPath(id);
        if (path != null) {
            return getMimeType(path);
        }
        return null;
    }

    /**
     * Gets cache metadata for a file, including last modified time, size, and MIME type.
     *
     * @param filePath The path to the file
     * @return CacheData object with file metadata
     * @throws IOException if file attributes cannot be read
     */
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