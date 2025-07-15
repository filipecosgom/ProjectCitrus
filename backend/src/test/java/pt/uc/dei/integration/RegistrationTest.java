package pt.uc.dei.integration;

import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import java.time.Duration;

class RegistrationTest {
    private WebDriver driver;

    @BeforeEach
    void setUp() {
        System.setProperty("webdriver.chrome.driver", "drivers/chromedriver.exe");
        ChromeOptions options = new ChromeOptions();
        options.setAcceptInsecureCerts(true);
        driver = new ChromeDriver(options);
    }

    @Test
    void testRegistrationRequiredFields() throws InterruptedException {
        driver.get("https://localhost:3000/register");
        Thread.sleep(1000);

        // Step 2: Click on "Junte-se à CITRUS hoje"
        WebElement joinLink = driver.findElement(By.xpath("//a[contains(text(),'Junte-se à CITRUS hoje')]"));
        joinLink.click();
        Thread.sleep(1000);

        // Step 3: Click on "Criar conta"
        WebElement createAccountButton = driver.findElement(By.xpath("//button[normalize-space()='Criar conta']"));
        createAccountButton.click();
        Thread.sleep(1000);

        // Step 4: Click on "O email é necessário"
        WebElement emailRequiredSpan = driver.findElement(By.xpath("//span[normalize-space()='O email é necessário']"));
        emailRequiredSpan.click();
        Thread.sleep(1000);

        // Step 5: Click on "A palavra-passe é necessária"
        WebElement passwordRequiredSpan = driver.findElement(By.xpath("//span[normalize-space()='A palavra-passe é necessária']"));
        passwordRequiredSpan.click();
        Thread.sleep(1000);

        // Step 6: Click on "A confirmação da palavra-passe é necessária"
        WebElement confirmPasswordRequiredSpan = driver.findElement(By.xpath("//span[normalize-space()='A confirmação da palavra-passe é necessária']"));
        confirmPasswordRequiredSpan.click();
        Thread.sleep(1000);

        // Assert that all three error spans are displayed
        Assertions.assertTrue(emailRequiredSpan.isDisplayed(), "Email required error should be visible");
        Assertions.assertTrue(passwordRequiredSpan.isDisplayed(), "Password required error should be visible");
        Assertions.assertTrue(confirmPasswordRequiredSpan.isDisplayed(), "Confirm password required error should be visible");
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
