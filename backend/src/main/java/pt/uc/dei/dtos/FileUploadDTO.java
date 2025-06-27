package pt.uc.dei.dtos;

import jakarta.ws.rs.FormParam;
import org.jboss.resteasy.annotations.providers.multipart.PartType;

import java.io.InputStream;

public class FileUploadDTO {
    @FormParam("file")
    @PartType("application/octet-stream")
    private InputStream fileStream;

    /**
     * Retrieves the file stream for the uploaded file.
     * @return the file input stream.
     */
    public InputStream getFileStream() {
        return fileStream;
    }

    /**
     * Sets the file stream for the uploaded file.
     * @param fileStream the file input stream to set.
     */
    public void setFileStream(InputStream fileStream) {
        this.fileStream = fileStream;
    }

}