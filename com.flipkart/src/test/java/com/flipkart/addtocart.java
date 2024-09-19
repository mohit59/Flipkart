package com.flipkart;

import static org.testng.Assert.assertEquals;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

public class addtocart {
	WebDriver driver;
    WebDriverWait wait;
   
    
    @BeforeClass
    @Parameters("browser")
    public void setUp(String browser) {
        driver = WebDriverManager.getDriver(browser);
        driver.get("https://www.example.com");
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        
    }
    
    @DataProvider(name = "cartData")
    public Object[][] getSortData() {
        return new Object[][]{
                {"shoes", "Price -- Low to High"}
        };
    }
    
    
    @Test(dataProvider = "cartData")
    public void validateProductSort(String searchTerm, String sortOption) throws InterruptedException {
        driver.get("https://www.flipkart.com");

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
       
       List<WebElement> products = driver.findElements(By.xpath("//div[@class='hCKiGj']/a[1]")); // Product list
       List<WebElement> pricelist = driver.findElements(By.xpath("//div[@class='hCKiGj']/a[2]")); // price list
       ArrayList<String> expectedProductNames=new ArrayList<String>();
       ArrayList<String> expectedProductPrices= new ArrayList<String>();
       
       WebElement secondProduct = products.get(1);
       expectedProductNames.add(secondProduct.getText());
       expectedProductPrices.add(pricelist.get(1).getText());
       secondProduct.click();
       
       String parentwin=driver.getWindowHandle();
       Thread.sleep(1000);
       addtocartt();
       Thread.sleep(1000);
       driver.close();
       Thread.sleep(1000);
       driver.switchTo().window(parentwin);
       Thread.sleep(1000);
       WebElement thirdproduct = products.get(2);
       expectedProductNames.add(thirdproduct.getText());
       expectedProductPrices.add(pricelist.get(2).getText());
       thirdproduct.click();
       Thread.sleep(1000);
       addtocartt();
       Thread.sleep(1000);
       
      
       Thread.sleep(1000);
       ValidateCart(expectedProductNames, expectedProductPrices);
       Thread.sleep(1000);
       
       
       

	

}
    
    public void ValidateCart(ArrayList<String> expectedProductNames, ArrayList<String> expectedProductPrices) {
    	List<WebElement> cartitem=driver.findElements(By.xpath("//div[@class='gE4Hlh']"));
    	List<WebElement> prices=driver.findElements(By.xpath("//span[@class='LAlF6k re6bBo']"));
    	
    	 ArrayList<String> cartItemsList = new ArrayList<>();
         ArrayList<String> pricesList = new ArrayList<>();

         // Add text from cartitem WebElements to cartItemsList
         for (WebElement item : cartitem) {
             cartItemsList.add(item.getText());
         }

         // Add text from prices WebElements to pricesList
         for (WebElement price : prices) {
             pricesList.add(price.getText());
         }

         // Print the cart items and prices
         System.out.println("Cart Items: " + cartItemsList);
         System.out.println("Prices: " + pricesList);
    	
         Assert.assertTrue(compareListsBySorting(cartItemsList, expectedProductNames),"cart item not same");
         Assert.assertTrue(compareListsBySorting(expectedProductPrices,pricesList), "price not match");
         int tp=validateTotal(pricesList);
         
         WebElement uiprice=driver.findElement(By.xpath("//div[@class='uJ4ZKF']//span[text()]"));
         int actualtp=Integer.parseInt(uiprice.getText().replaceAll("[^0-9]", ""));
         Assert.assertEquals(actualtp, tp);
    	
    }
    
    
    public int validateTotal(ArrayList<String> pricesList)
    {
    	int totalprice=0;
    	for (String priceElement : pricesList) {
            String priceText = priceElement.replaceAll("[^0-9]", "");
          totalprice=totalprice+Integer.parseInt(priceText);
            
        }
    	
    	return totalprice;
    }
    
    public static boolean compareListsBySorting(ArrayList<String> list1, ArrayList<String> list2) {
        // If the sizes are not the same, return false
        if (list1.size() != list2.size()) {
            return false;
        }

        // Sort both lists
        Collections.sort(list1);
        Collections.sort(list2);

        // Compare sorted lists
        return list1.equals(list2);
    }
    
    
    public void addtocartt() {
    	switchToNewTab();
        WebElement addtocart=driver.findElement(By.xpath("//button[normalize-space()='Add to cart']"));
        addtocart.click();
    }
    
    
    public void switchToNewTab() {
        for (String tab : driver.getWindowHandles()) {
            driver.switchTo().window(tab);
        }
    }
    
    @AfterMethod
    public void tearDown() {
        WebDriverManager.quitDriver();
    }
}

