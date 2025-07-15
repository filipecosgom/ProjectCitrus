package pt.uc.dei.integration;

import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

class LoginTest {

    @BeforeEach
    void setUp() {
        // Use relative path to chromedriver.exe for portability
        System.setProperty("webdriver.chrome.driver", "drivers/chromedriver.exe");
        ChromeOptions options = new ChromeOptions();
        options.setAcceptInsecureCerts(true);
        driver = new ChromeDriver(options);
    }

    @Test
    void testLoginSuccess() throws InterruptedException {
        driver.get("https://localhost:3000/");
        Thread.sleep(1000);

        // Step 2: Click on "Endereço de e-mail"
        WebElement emailInput = driver.findElement(By.xpath("//div[@class='login-field']//input[@id='login-email']"));
        emailInput.click();
        Thread.sleep(1000);

        // Step 3: Enter email
        emailInput.sendKeys("citrus.apiteam@gmail.com");
        Thread.sleep(1000);

        // Step 4: Enter password
        WebElement passwordInput = driver.findElement(By.xpath("//div[@class='login-field']//input[@id='login-password']"));
        passwordInput.sendKeys("admin");
        Thread.sleep(1000);

        // Step 5: Click on authenticationCode (focus input)
        WebElement authCodeInput = driver.findElement(By.xpath("//input[@id='login-authenticationCode']"));
        authCodeInput.click();
        Thread.sleep(1000);

        // Step 6: Enter authentication code
        authCodeInput.sendKeys("000000");
        Thread.sleep(1000);

        // Step 7: Click on "Entrar"
        WebElement signInButton = driver.findElement(By.className("main-button"));
        signInButton.click();

        // Step 8: Wait for success toast notification (id="2")
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        WebElement toast = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("2")));
        Assertions.assertTrue(toast.isDisplayed(), "Login success toast should be visible");
    }

    @Test
    void testLoginFailWrongPassword() throws InterruptedException {
        driver.get("https://localhost:3000/");
        Thread.sleep(1000);

        // Step 2: Click on "E-mail address"
        WebElement emailInput = driver.findElement(By.xpath("//div[@class='login-field']//input[@id='login-email']"));
        emailInput.click();
        Thread.sleep(1000);

        // Step 3: Enter email
        emailInput.sendKeys("citrus.apiteam@gmail.com");
        Thread.sleep(1000);

        // Step 4: Enter password
        WebElement passwordInput = driver.findElement(By.xpath("//div[@class='login-field']//input[@id='login-password']"));
        passwordInput.sendKeys("******"); // Replace with actual wrong password
        Thread.sleep(1000);

        // Step 5: Click on authenticationCode (focus input)
        WebElement authCodeInput = driver.findElement(By.xpath("//input[@id='login-authenticationCode']"));
        authCodeInput.click();
        Thread.sleep(1000);

        // Step 6: Enter authentication code
        authCodeInput.sendKeys("000000");
        Thread.sleep(1000);

        // Step 7: Scroll down to authenticationCode input (if needed)
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", authCodeInput);
        Thread.sleep(1000);

        // Step 8: Click on "Sign in"
        WebElement signInButton = driver.findElement(By.className("main-button"));
        signInButton.click();
        Thread.sleep(1000);

        // Step 10: Check for toast notification (Login failed)
        WebElement toast = driver.findElement(By.id("1"));
        Assertions.assertTrue(toast.isDisplayed(), "Login failed toast should be visible");
    }
    private WebDriver driver;


    @Test
    void testLoginFailNoInput() throws InterruptedException {
        driver.get("https://localhost:3000/");
        Thread.sleep(1000);

        // Step 2: Click on "Endereço de e-mail" (focus input)
        WebElement emailInput = driver.findElement(By.xpath("//div[@class='login-field']//input[@id='login-email']"));
        emailInput.click();
        Thread.sleep(1000);

        // Step 3: Click on authenticationCode (focus input)
        WebElement authCodeInput = driver.findElement(By.xpath("//input[@id='login-authenticationCode']"));
        authCodeInput.click();
        Thread.sleep(1000);

        // Step 4: Click on "Entrar" using class name 'main-button'
        WebElement entrarButton = driver.findElement(By.className("main-button"));
        entrarButton.click();
        Thread.sleep(1000);

        // Step 5: Click on email required error span (language-independent)
        WebElement emailRequiredSpan = driver.findElement(By.cssSelector(".login-field .error-message"));
        emailRequiredSpan.click();
        Thread.sleep(1000);

        // Step 6: Click on password required error span (language-independent)
        WebElement passwordRequiredSpan = driver.findElements(By.cssSelector(".login-field .error-message")).get(1);
        passwordRequiredSpan.click();
        Thread.sleep(1000);

        // Step 7: Click on 2FA code required error span (language-independent)
        WebElement codeRequiredSpan = driver.findElements(By.cssSelector(".login-field .error-message")).get(2);
        codeRequiredSpan.click();
        Thread.sleep(1000);

        // Assert that all three error spans are displayed
        Assertions.assertTrue(emailRequiredSpan.isDisplayed(), "Email required error should be visible");
        Assertions.assertTrue(passwordRequiredSpan.isDisplayed(), "Password required error should be visible");
        Assertions.assertTrue(codeRequiredSpan.isDisplayed(), "2FA code required error should be visible");
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
