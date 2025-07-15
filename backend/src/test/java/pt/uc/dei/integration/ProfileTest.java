package pt.uc.dei.integration;

import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import java.time.Duration;

class ProfileTest {
    private WebDriver driver;

    @BeforeEach
    void setUp() {
        System.setProperty("webdriver.chrome.driver", "drivers/chromedriver.exe");
        ChromeOptions options = new ChromeOptions();
        options.setAcceptInsecureCerts(true);
        driver = new ChromeDriver(options);
        driver.manage().window().setSize(new Dimension(1400, 900));
        loginSuccessfully();
    }

    private void loginSuccessfully() {
        driver.get("https://localhost:3000/login");
        WebElement emailInput = driver.findElement(By.id("login-email"));
        emailInput.sendKeys("norm.alperson.citrus@gmail.com");
        WebElement passwordInput = driver.findElement(By.id("login-password"));
        passwordInput.sendKeys("#Citrus202512345");
        WebElement authCodeInput = driver.findElement(By.id("login-authenticationCode"));
        authCodeInput.sendKeys("000000");
        WebElement signInButton = driver.findElement(By.className("main-button"));
        signInButton.click();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("2")));
    }

    

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    void testProfileAvatarUploadFlow() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.get("https://localhost:3000/profile");

        // Enter edit mode
        WebElement editButton = wait.until(ExpectedConditions.elementToBeClickable(By.className("edit-btn")));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", editButton);
        try { Thread.sleep(500); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        editButton.click();

        // Wait for address fields to slide in (edit mode)
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".address-edit-fields.slide-in")));

        // Click avatar edit icon
        WebElement avatarEditIcon = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".profile-avatar-edit-icon")));
        avatarEditIcon.click();

        // Upload avatar file
        WebElement avatarInput = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("#avatar-upload")));
        avatarInput.sendKeys("C:\\fakepath\\10.png");

        // Click Save
        WebElement saveButton2 = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".edit-btn[type='submit']")));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", saveButton2);
        try { Thread.sleep(300); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        saveButton2.click();

        // Check for success message (div[id='3'])
        WebElement successMsg = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div[id='3']")));
        Assertions.assertTrue(successMsg.isDisplayed(), "Avatar upload success message should be visible");
    }
    @Test
    void testProfileUpdateFlow() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.get("https://localhost:3000/profile?id=28&tab=profile");

        // Click the edit button (class: edit-btn) to enter edit mode
        WebElement editButton = wait.until(ExpectedConditions.elementToBeClickable(By.className("edit-btn")));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", editButton);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        editButton.click();

        // Wait for address fields to slide in (edit mode)
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".address-edit-fields.slide-in")));

        // Fill name (step 3-4)
        WebElement nameInput = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("input[name='name']")));
        nameInput.clear();
        nameInput.sendKeys("Norm");

        // Fill surname (step 5-6)
        WebElement surnameInput = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("input[name='surname']")));
        surnameInput.clear();
        surnameInput.sendKeys("Alperson");

        // Birthdate field (step 8-17)
        WebElement birthdateInput = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("input[name='birthdate']")));
        birthdateInput.clear();
        birthdateInput.sendKeys("04/12/1989");

        // Select role (step 18)
        WebElement roleSelect = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("select[name='role']")));
        roleSelect.sendKeys("Frontend Developer");

        // Select office (step 19)
        WebElement officeSelect = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("select[name='office']")));
        officeSelect.sendKeys("Coimbra");

        // Fill phone (step 20)
        WebElement phoneInput = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("input[name='phone']")));
        phoneInput.clear();
        phoneInput.sendKeys("123456789");

        // Fill Street (step 21-22)
        WebElement streetInput = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("input[name='street']")));
        streetInput.clear();
        streetInput.sendKeys("R. Aveiro");

        // Fill Postal Code (step 23-24)
        WebElement postalCodeInput = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("input[name='postalCode']")));
        postalCodeInput.clear();
        postalCodeInput.sendKeys("3030-199");

        // Fill Municipality (step 25-26)
        WebElement municipalityInput = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("input[name='municipality']")));
        municipalityInput.clear();
        municipalityInput.sendKeys("Coimbra");

        // Fill Biography (step 27, 29)
        WebElement biographyTextarea = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("textarea[name='biography']")));
        biographyTextarea.clear();
        biographyTextarea.sendKeys("I'm new here.");

        // Click Save (step 30) - use edit-btn with type submit
        WebElement saveButton2 = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".edit-btn[type='submit']")));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", saveButton2);
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        saveButton2.click();


        // Check for success message (step 32)
        WebElement successMsg = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div[id='1']")));
        Assertions.assertTrue(successMsg.isDisplayed(), "Success message should be visible");
    }
}
