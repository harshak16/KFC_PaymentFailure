import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.And;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class KFCOrderSteps {
    private WebDriver driver;

    @Before
    public void setUp() {
        //System.setProperty is not needed as using the latest chrome version - 116, but need to be updated for older versions
        driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
    }

    @Given("the user is on the KFC website")
    public void userIsOnKFCWebsite() {
        // Navigate to the KFC website
        driver.get("https://www.kfc.com.au/");
    }

    @When("the user clicks on the 'Start oder' button")
    public void userClicksStartOrderButton() {
        findelement("id","startOrderItemButton").click();
    }

    @And("user selects order type as 'Pick up'")
    public void userSelectsPickupOrderType() {
        // Select "Pick up" as the order type
        findelement("xpath","//button[contains(@data-testid,'Pickup')]").click();

    }

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

    @And("user enters {string}, {string}, {string}, {string} on the checkout page")
    public void userEntersCheckoutInfo(String firstname, String lastname, String phone, String email) {
        // Enter the guest details which are shared from Feature file
        findelement("id","mt-input-firstNameLabel").sendKeys(firstname);
        findelement("id","mt-input-lastNameLabel").sendKeys(lastname);
        findelement("id","mt-input-phone").sendKeys(phone);
        findelement("id","mt-input-email").sendKeys(email);
    }

    @And("user continues to the payment method using 'Card' option")
    public void userSelectsPaymentMethod() {
        // Select "Card" as the payment method (replace with actual selection)
        findelement("xpath","//button[@data-testid='pay-button']").click();
        findelement("xpath","//div[@aria-label='Paying with Card']").click();
        //hardingcoding dummy card details for this scenario

        driver.switchTo().frame(findelement("xpath","//iframe[@title='Secure Credit Card Frame - Credit Card Number']"));
        findelement("xpath","//input[@id='credit-card-number']").sendKeys("2222405343248877");
        driver.switchTo().defaultContent();
        driver.switchTo().frame(findelement("xpath","//iframe[@title='Secure Credit Card Frame - Expiration Date']"));
        findelement("id","expiration").sendKeys("1224");
        driver.switchTo().defaultContent();
        driver.switchTo().frame(findelement("xpath","//iframe[@title='Secure Credit Card Frame - CVV']"));
        findelement("id","cvv").sendKeys("123");
        driver.switchTo().defaultContent();
      //  driver.switchTo().defaultContent();
        findelement("xpath","//*[@id='Continue to Payment']").click();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        wait.until(ExpectedConditions.visibilityOf(findelement("xpath","//div[@data-braintree-id='methods-edit']")));
        findelement("xpath","//*[@id='Continue to Payment']").click();

    }

    @Then("Verify the error message for the failed payment")
    public void verifyFailedPaymentErrorMessage() {
        // Add code to verify the error message for the failed payment
        WebElement errorMessage = findelement("xpath","//div[@class='paymentFailed']");
        assert(errorMessage.isDisplayed());
    }

    public WebElement findelement(String type, String locator){
        try {
            WebElement element;

            switch (type.toLowerCase()) {
                case "id":
                    element = driver.findElement(By.id(locator));
                    break;
                case "name":
                    element = driver.findElement(By.name(locator));
                    break;
                case "xpath":
                    element = driver.findElement(By.xpath(locator));
                    break;
                case "cssselector":
                    element = driver.findElement(By.cssSelector(locator));
                    break;
                default:
                    throw new IllegalArgumentException("Invalid element type: " + type);
            }
            return element;
        } catch (org.openqa.selenium.NoSuchElementException e) {
            System.err.println("Element not found: " + locator);
            throw e;
        }
    }


    @After
    public void tearDown() {
        // Close the browser after the scenario
        driver.quit();
    }
}
