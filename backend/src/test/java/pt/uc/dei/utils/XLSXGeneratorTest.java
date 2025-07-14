package pt.uc.dei.utils;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import pt.uc.dei.dtos.UserDTO;
import pt.uc.dei.enums.Language;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class XLSXGeneratorTest {
    @Test
    void testGenerateUserXLSX_Portuguese_Admin() {
        UserDTO user = TestUserFactory.createUser();
        List<UserDTO> users = Collections.singletonList(user);
        byte[] xlsx = XLSXGenerator.generateUserXLSX(users, Language.PORTUGUESE, true);
        assertNotNull(xlsx);
        assertTrue(xlsx.length > 0);
        assertIsValidXLSX(xlsx);
    }

    @Test
    void testGenerateUserXLSX_English_NonAdmin() {
        UserDTO user = TestUserFactory.createUser();
        List<UserDTO> users = Collections.singletonList(user);
        byte[] xlsx = XLSXGenerator.generateUserXLSX(users, Language.ENGLISH, false);
        assertNotNull(xlsx);
        assertTrue(xlsx.length > 0);
        assertIsValidXLSX(xlsx);
    }

    @Test
    void testGenerateUserXLSX_EmptyList() {
        byte[] xlsx = XLSXGenerator.generateUserXLSX(Collections.emptyList(), Language.ENGLISH, false);
        assertNotNull(xlsx);
        assertTrue(xlsx.length > 0);
        assertIsValidXLSX(xlsx);
    }

    @Test
    void testGenerateUserXLSX_NullFields() {
        UserDTO user = TestUserFactory.createUserWithNulls();
        List<UserDTO> users = Collections.singletonList(user);
        byte[] xlsx = XLSXGenerator.generateUserXLSX(users, Language.PORTUGUESE, false);
        assertNotNull(xlsx);
        assertTrue(xlsx.length > 0);
        assertIsValidXLSX(xlsx);
    }

    private void assertIsValidXLSX(byte[] xlsx) {
        try (Workbook workbook = new XSSFWorkbook(new java.io.ByteArrayInputStream(xlsx))) {
            assertNotNull(workbook.getSheet("Users"));
        } catch (Exception e) {
            fail("Not a valid XLSX file: " + e.getMessage());
        }
    }
}

// Helper factory for test UserDTOs
class TestUserFactory {
    static UserDTO createUser() {
        UserDTO user = new UserDTO();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setName("John");
        user.setSurname("Doe");
        user.setPhone("123456789");
        user.setOffice(null);
        user.setRole(null);
        user.setAccountState(null);
        user.setManager(null);
        return user;
    }
    static UserDTO createUserWithNulls() {
        UserDTO user = new UserDTO();
        user.setId(null);
        user.setEmail(null);
        user.setName(null);
        user.setSurname(null);
        user.setPhone(null);
        user.setOffice(null);
        user.setRole(null);
        user.setAccountState(null);
        user.setManager(null);
        return user;
    }
}
