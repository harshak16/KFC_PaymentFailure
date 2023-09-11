package com.au.kfc;

import io.cucumber.core.logging.Logger;
import io.cucumber.core.logging.LoggerFactory;
import io.cucumber.java.Scenario;
import io.qameta.allure.Allure;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.And;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.Properties;
import java.util.concurrent.TimeUnit;


/** Considerations during the framework creation:
 * This class contains Cucumber step definitions for ordering food on the KFC website.
 * hardcoding testdata and locators which can be further parameterised for scalable automation framework
 * test data can  be managed using other data sources as well eg: csv, excel, xml and others
 * For this sample framework I have used flat file as one of the data sources to showcase
 * To make it more robust and easy test data management, these data can also be managed through CI/CD pipeline
 * POM(page object model) or other strategies can be implemented for better management/re-usability of methods, locators and the common code can be separated.
 * Code is written within Stepdef file so that the validation of code can be performed in a single file.
 * Readme file can be added to complex projects to describe the usage and to provide instructions
 */
public class KFCOrderSteps {
    private WebDriver driver;
    public Scenario scenario;
    private final Logger logger = LoggerFactory.getLogger(KFCOrderSteps.class);
    public static Properties properties = new Properties();


    /**
     * Setup method executed before each scenario.
     * Launches browser, sets implicit wait and load test data for the scenario to execute.
     * This framework is created with the use of ChromeDrive, it can be further scaled with other drivers.
     */
    @Before
    public void setUp(Scenario scenario) {
        //System.setProperty is not needed as using the latest chrome version - 116, but need to be updated for older versions
        driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
        loadProperties();
        this.scenario = scenario;
    }

    /**
     * This method is launching the url fetched from properties file
     */
    @Given("the user is on the KFC website")
    public void userIsOnKFCWebsite() {
        // Navigate to the KFC website
        driver.get(properties.getProperty("url"));
        //Verify the KFC page loaded
    }

    /**
     * This method is to showcase the direct use of feature line as a step def
     */
    @When("the user clicks on the 'Start oder' button")
    public void userClicksStartOrderButton() {
        javaScriptClick(findelement("id","startOrderItemButton"));

    }

    @And("user selects order type as 'Pick up'")
    public void userSelectsPickupOrderType() {
        // Select "Pick up" as the order type
        javaScriptClick(findelement("xpath","//button[contains(@data-testid,'Pickup')]"));

    }

    /**
     * This method is to showcase the use of fetching the data from feature file.
     * this can be implemented as a common practice and have the code to perform action based on the value received.
     * With that approach, code re-usability can be improved.
     */
    @And("user selects {string} as the pickup location")
    public void userSelectsPickupLocation(String location) {

        WebElement pickupLocation = findelement("xpath","//input[@data-testid='store-search-input']");
        pickupLocation.sendKeys(location);
        pickupLocation.sendKeys(Keys.ENTER);
        //Selecting the first Order here from the list.
        findelement("xpath","//button[@data-testid='schedule-order']").click();

    }

    @And("user clicks on 'View menu'")
    public void userClicksViewMenu() {
        // Click the "View menu" button
        findelement("xpath","//button[@aria-label='View Menu']").click();
    }

    /**
     * Using feature line as is..
     * "Bucket for One" can be parameterised so order any item/multiple items as per the scenario.
     */
    @Then("user selects 'Bucket for One' to the cart")
    public void userSelectsItemToCart() {
        // Select an item to add to the cart
        findelement("xpath","//div[text()='Bucket for One']").click();
        //Click on Add to order
        findelement("xpath","//button[@data-testid='add-to-cart-handler']").click();
        //Click on Continue on the popup
        findelement("xpath","//button[@data-testid='continue-to-cart']").click();
        //Navigate to cart by clicking on the basket on top right corer
        findelement("xpath","//div[@class='shopping-cart ']").click();
    }

    @And("the user clicks on 'Checkout'")
    public void userClicksCheckout() {
        // Click the "Checkout" button
        findelement("xpath","//button[@data-testid='navigation-checkout-desktop']").click();

    }

    @Then("user checkouts as 'Guest'")
    public void userChecksOutAsGuest() {
        // Select "Guest" checkout option
        findelement("xpath","//button[@data-testid='continue-as-a-gust']").click();

    }

    /**
     * Fetching the below params from the feature file and passing to the method
     * @param firstname - first name to be entered in the guest section
     * @param lastname - last name to be entered in the guest section
     * @param phone - phone number to be entered in the guest section
     * @param email - email to be entered in the guest section
     */
    @And("user enters {string}, {string}, {string}, {string} on the checkout page")
    public void userEntersCheckoutInfo(String firstname, String lastname, String phone, String email) {
        // Entering the guest details which are fetched from Feature file
        findelement("id","mt-input-firstNameLabel").sendKeys(firstname);
        findelement("id","mt-input-lastNameLabel").sendKeys(lastname);
        findelement("id","mt-input-phone").sendKeys(phone);
        findelement("id","mt-input-email").sendKeys(email);
    }


    @And("user continues to the payment method using 'Card' option")
    public void userSelectsPaymentMethod() {
        findelement("xpath","//button[@data-testid='pay-button']").click();
        findelement("xpath","//div[@aria-label='Paying with Card']").click();
        //Fetching card values from properties file to display the data fetched from data sources.
        /*
          Card details are required to enter inside iframes so switching the driver to iframe and perform actions.
          Without this switch the script fails with no such element exception.
          And to proceed after performing actions in iframe, we have to switch back to parent DOM.
          The above mention steps are performed for each of the field in the card payment section
         */
        driver.switchTo().frame(findelement("xpath","//iframe[@title='Secure Credit Card Frame - Credit Card Number']"));
        findelement("xpath","//input[@id='credit-card-number']").sendKeys(properties.getProperty("creditCardNumber"));
        driver.switchTo().defaultContent();
        driver.switchTo().frame(findelement("xpath","//iframe[@title='Secure Credit Card Frame - Expiration Date']"));
        findelement("id","expiration").sendKeys(properties.getProperty("expiry"));
        driver.switchTo().defaultContent();
        driver.switchTo().frame(findelement("xpath","//iframe[@title='Secure Credit Card Frame - CVV']"));
        findelement("id","cvv").sendKeys(properties.getProperty("cvv"));
        driver.switchTo().defaultContent();
        findelement("xpath","//*[@id='Continue to Payment']").click();
        //Both continue ordering and proceed to payment button has the same locators.
        // Waiting for edit button to be displayed after adding the card to show the usage of explicit wait.
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        wait.until(ExpectedConditions.visibilityOf(findelement("xpath","//div[@data-braintree-id='methods-edit']")));
        findelement("xpath","//*[@id='Continue to Payment']").click();

    }

    /**
     * This method verifies the error message displayed, fails the scenario if the message is not displayed
     * Adding logger for it to display the message in the console
     * Adding scenario.log to display the message in the cucumber report.
     */
    @Then("Verify the error message for the failed payment")
    public void verifyFailedPaymentErrorMessage() {
        // Add code to verify the error message for the failed payment
        WebElement errorMessage = findelement("xpath","//div[@class='paymentFailed']//div[contains(text(),'Check your payment')]");
        assert(errorMessage.isDisplayed());
        logger.info(errorMessage::getText);
        scenario.log(errorMessage.getText());
    }
    /**
     * Generic method to find elements based on the type and locator.
     *
     * @param type    The type of the locator (e.g., "id", "name", "xpath").
     * @param locator The locator string.
     * @return The found WebElement.
     */

    public WebElement findelement(String type, String locator){
        try {

            return switch (type.toLowerCase()) {
                case "id" -> driver.findElement(By.id(locator));
                case "name" -> driver.findElement(By.name(locator));
                case "xpath" -> driver.findElement(By.xpath(locator));
                case "cssselector" -> driver.findElement(By.cssSelector(locator));
                default -> throw new IllegalArgumentException("Invalid element type: " + type);
            };
        } catch (org.openqa.selenium.NoSuchElementException e) {
            logger.error(() -> "Element not found: " + locator);
            throw e;
        }
        finally {
            byte[] screenshot=((TakesScreenshot)driver).getScreenshotAs(OutputType.BYTES);
            Allure.addAttachment("Finding element", new ByteArrayInputStream(screenshot));

        }
    }
    /**
     * This method will fetch the data from properties file and
     * load it into the global properties variable.
     */
    public void loadProperties() {

        FileInputStream inputStream = null;

        try {
            // Define the path to your properties file
            String filePath = "src/test/resources/testData/testData.properties";

            // Open the properties file using FileInputStream
            inputStream = new FileInputStream(filePath);

            // Load the properties from the file
            properties.load(inputStream);
        } catch (IOException e) {
            logger.error(() -> "Io exception occurred");
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    logger.error(() -> " exception occurred while closing the sheet");
                }
            }
        }

    }

    public void javaScriptClick(WebElement elementToClick){
        JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;
        jsExecutor.executeScript("arguments[0].click();", elementToClick);

    }
    /**
     * Teardown method to mark the scenario status, take screenshot at the end of the scenario and close the browser
     */
    @After
    public void tearDown(Scenario scenario) {
        // Close the browser after the scenario
        final byte[] screenshot = ((TakesScreenshot) driver)
                .getScreenshotAs(OutputType.BYTES);
        scenario.attach(screenshot, "image/png","Scenario complete");
        if(scenario.isFailed()){
            scenario.log("Scenario failed");
        }
        driver.quit();
    }
}
