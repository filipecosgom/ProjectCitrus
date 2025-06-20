package pt.uc.dei.dtos;

import jakarta.ws.rs.FormParam;
import org.jboss.resteasy.annotations.providers.multipart.PartType;

import java.io.InputStream;

public class FileUploadDTO {
    @FormParam("file")
    @PartType("application/octet-stream")
    private InputStream fileStream;

    public InputStream getFileStream() {
        return fileStream;
    }

    public void setFileStream(InputStream fileStream) {
        this.fileStream = fileStream;
    }

}