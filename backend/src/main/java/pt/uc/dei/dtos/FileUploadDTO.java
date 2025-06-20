package pt.uc.dei.dtos;

import jakarta.ws.rs.FormParam;
import org.jboss.resteasy.annotations.providers.multipart.PartType;

import java.io.InputStream;

public class FileUploadDTO {
    @FormParam("file")
    @PartType("application/octet-stream")
    private InputStream fileStream;

    @FormParam("fileName")
    @PartType("text/plain")
    private String fileName;

    public InputStream getFileStream() {
        return fileStream;
    }

    public void setFileStream(InputStream fileStream) {
        this.fileStream = fileStream;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}