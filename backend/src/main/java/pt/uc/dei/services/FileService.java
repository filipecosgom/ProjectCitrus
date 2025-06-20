package pt.uc.dei.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tika.Tika;

import java.io.*;
import java.nio.file.*;
import java.util.List;

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
}