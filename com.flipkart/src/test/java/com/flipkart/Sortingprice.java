package com.flipkart;


import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;


public class Sortingprice {

    WebDriver driver;
    WebDriverWait wait;
   
    
    @BeforeClass
    @Parameters("browser")
    public void setUp(String browser) {
        driver = WebDriverManager.getDriver(browser);
        driver.get("https://www.example.com");
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        
    }

    @DataProvider(name = "sortData")
    public Object[][] getSortData() {
        return new Object[][]{
                {"shoes", "Price -- Low to High", 2}
        };
    }

    @Test(dataProvider = "sortData")
    public void validateProductSort(String searchTerm, String sortOption, int pageLimit) throws InterruptedException {
        driver.get("https://www.flipkart.com");

        // Close login popup if displayed
        try {
            WebElement closeLoginPopup = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button._2KpZ6l._2doB4z")));
            closeLoginPopup.click();
        } catch (Exception e) {
           System.out.println("pop up did not appeared");
        }

        // Search for the product
        WebElement searchBox = driver.findElement(By.name("q"));
        WebElement searchbutton=driver.findElement(By.xpath("//button[@aria-label='Search for Products, Brands and More']"));
        searchBox.sendKeys(searchTerm);
        Thread.sleep(500);
        searchbutton.click();

        // Wait for search results to load
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[text()='Sort By']")));

        // Click on sort option: "Price -- Low to High"
        WebElement sortOptionElement = driver.findElement(By.xpath("//div[text()='"+sortOption+"']"));
        sortOptionElement.click();

        // Wait for the sort results to update
       Thread.sleep(1000);

        // Validate prices are sorted for all products till the specified page limit
        List<Integer> allPrices = new ArrayList<>();
        for (int i = 1; i <=pageLimit; i++) {
            List<WebElement> priceElements = driver.findElements(By.xpath("//div[@class='Nx9bqj']")); 
            
            for (WebElement priceElement : priceElements) {
                String priceText = priceElement.getText().replaceAll("[^0-9]", "");
                allPrices.add(Integer.parseInt(priceText));
                
            }

            // Move to the next page if available and within the limit
            if (i < pageLimit) {
            	WebElement nextPageButton = driver.findElement(By.xpath("//span[text()='Next']//parent::a"));
            	scrollToElement(nextPageButton);
                nextPageButton.click();
                  Thread.sleep(100);
            }
        }

        // Validate that prices are sorted in ascending order
        List<Integer> sortedPrices = new ArrayList<>(allPrices);
        Collections.sort(sortedPrices);

        org.testng.Assert.assertEquals(allPrices, sortedPrices, "The products are not sorted by price in ascending order.");
        System.out.println("products prices are sorted");
    }

    public void scrollToElement(WebElement element) {
        Actions action= new Actions(driver);
        action.moveToElement(element).build().perform();
    }
    
    @AfterMethod
    public void tearDown() {
        WebDriverManager.quitDriver();
    }
}
