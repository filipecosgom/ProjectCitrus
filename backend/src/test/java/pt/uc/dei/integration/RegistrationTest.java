package pt.uc.dei.integration;

import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import java.time.Duration;
import java.util.List;

class RegistrationTest {

    @Test
    void testRegistrationSuccessAndActivation() throws InterruptedException {
        driver.get("https://localhost:3000/register");
        Thread.sleep(1000);

        // Step 2: Enter email
        WebElement emailInput = driver.findElement(By.id("register-email"));
        emailInput.sendKeys("norm.alperson.citrus@gmail.com");
        Thread.sleep(500);

        // Step 3: Enter initial invalid password
        WebElement passwordInput = driver.findElement(By.id("register-password"));
        passwordInput.sendKeys("******");
        Thread.sleep(500);

        // Step 4-5: Click on form container (optional, for focus)
        WebElement formContainer = driver.findElement(By.className("register-form-container"));
        formContainer.click();
        Thread.sleep(500);
        formContainer.click();
        Thread.sleep(500);

        // Step 6: Enter valid password
        passwordInput.clear();
        passwordInput.sendKeys("#Citrus202512345");
        Thread.sleep(500);

        // Step 7: Enter valid confirm password
        WebElement confirmPasswordInput = driver.findElement(By.id("register-confirm-password"));
        confirmPasswordInput.clear();
        confirmPasswordInput.sendKeys("#Citrus202512345");
        Thread.sleep(500);

        // Step 8: Click on "Create account" using class name 'main-button'
        WebElement createAccountButton = driver.findElement(By.className("main-button"));
        createAccountButton.click();
        Thread.sleep(1500);

        // Step 9: Wait for activation card to appear (explicit wait)
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement activationCard = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("activation-card")));
        Assertions.assertTrue(activationCard.isDisplayed(), "Activation card should be visible");
    }
    @Test
    void testRegistrationInvalidPassword() throws InterruptedException {
        driver.get("https://localhost:3000/register");
        Thread.sleep(1000);

        // Step 2: Enter email
        WebElement emailInput = driver.findElement(By.id("register-email"));
        emailInput.sendKeys("norm.alperson.citrus@gmail.com");
        Thread.sleep(500);

        // Step 3: Enter invalid password
        WebElement passwordInput = driver.findElement(By.id("register-password"));
        passwordInput.sendKeys("admin");
        Thread.sleep(500);

        // Step 4: Enter mismatched confirm password
        WebElement confirmPasswordInput = driver.findElement(By.id("register-confirm-password"));
        confirmPasswordInput.sendKeys("******");
        Thread.sleep(500);

        // Step 5: Click on "Criar conta" using class name 'main-button'
        WebElement createAccountButton = driver.findElement(By.className("main-button"));
        createAccountButton.click();
        Thread.sleep(1000);

        // Step 6-7: Select error messages by class name and order (language-independent)
        List<WebElement> errorSpans = driver.findElements(By.cssSelector(".error-message"));
        WebElement passwordErrorSpan = errorSpans.get(1); // Password error
        WebElement confirmPasswordErrorSpan = errorSpans.get(2); // Confirm password error
        passwordErrorSpan.click();
        Thread.sleep(500);
        confirmPasswordErrorSpan.click();
        Thread.sleep(500);

        // Assert that both error spans are displayed
        Assertions.assertTrue(passwordErrorSpan.isDisplayed(), "Password error should be visible");
        Assertions.assertTrue(confirmPasswordErrorSpan.isDisplayed(), "Confirm password error should be visible");
    }
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
        driver.get("https://localhost:3000/");
        Thread.sleep(1000);
        driver.get("https://localhost:3000/register");
        Thread.sleep(1000);

        // Step 3: Click on "Criar conta" using class name 'main-button'
        WebElement createAccountButton = driver.findElement(By.className("main-button"));
        createAccountButton.click();
        Thread.sleep(1000);

        // Step 4-6: Select error messages by class name and order (language-independent)
        List<WebElement> errorSpans = driver.findElements(By.cssSelector(".error-message"));
        WebElement emailRequiredSpan = errorSpans.get(0);
        WebElement passwordRequiredSpan = errorSpans.get(1);
        WebElement confirmPasswordRequiredSpan = errorSpans.get(2);
        emailRequiredSpan.click();
        Thread.sleep(1000);
        passwordRequiredSpan.click();
        Thread.sleep(1000);
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

    