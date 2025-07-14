package pt.uc.dei.unit.utils;

import pt.uc.dei.utils.TwoFactorUtil;

import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

class TwoFactorUtilTest {
    @Test
    void testGenerateSecretKeyAndGetSecretKeyString() {
        GoogleAuthenticatorKey key = TwoFactorUtil.generateSecretKey();
        assertNotNull(key);
        String secret = TwoFactorUtil.getSecretKeyString(key);
        assertNotNull(secret);
        assertFalse(secret.isEmpty());
    }

    @Test
    void testValidateCode() {
        assertTrue(TwoFactorUtil.validateCode("123456"));
        assertFalse(TwoFactorUtil.validateCode("12345"));
        assertFalse(TwoFactorUtil.validateCode("1234567"));
        assertFalse(TwoFactorUtil.validateCode("   "));
        assertFalse(TwoFactorUtil.validateCode("abc123"));
    }

    @Test
    void testVerifyTwoFactorCode_ValidAndInvalid() {
        // Mock GoogleAuthenticator to control authorize() result
        try (MockedStatic<TwoFactorUtil> utilMock = Mockito.mockStatic(TwoFactorUtil.class, Mockito.CALLS_REAL_METHODS)) {
            utilMock.when(() -> TwoFactorUtil.verifyTwoFactorCode(Mockito.anyString(), Mockito.eq("123456"))).thenReturn(true);
            utilMock.when(() -> TwoFactorUtil.verifyTwoFactorCode(Mockito.anyString(), Mockito.eq("000000"))).thenReturn(false);
            assertTrue(TwoFactorUtil.verifyTwoFactorCode("SOMESECRET", "123456"));
            assertFalse(TwoFactorUtil.verifyTwoFactorCode("SOMESECRET", "000000"));
        }
    }

    @Test
    void testVerifyTwoFactorCode_NullOrEmpty() {
        // Should throw NumberFormatException for null/empty userCode
        assertThrows(NumberFormatException.class, () -> TwoFactorUtil.verifyTwoFactorCode("SOMESECRET", null));
        assertThrows(NumberFormatException.class, () -> TwoFactorUtil.verifyTwoFactorCode("SOMESECRET", ""));
    }
}
