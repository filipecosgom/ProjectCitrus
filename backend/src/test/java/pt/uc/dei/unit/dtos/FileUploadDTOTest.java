package pt.uc.dei.dtos;

import org.junit.jupiter.api.Test;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link FileUploadDTO}.
 */
class FileUploadDTOTest {
    @Test
    void testGettersAndSetters() {
        FileUploadDTO dto = new FileUploadDTO();
        InputStream stream = new ByteArrayInputStream(new byte[]{1,2,3});
        dto.setFileStream(stream);
        assertEquals(stream, dto.getFileStream());
    }
}
