package com.javascraper.maven;

import java.io.IOException;
import java.time.Duration;
import java.io.File;

// import com.gargoylesoftware.htmlunit.WebClient;
// import com.gargoylesoftware.htmlunit.html.HtmlPage;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
// import org.openqa.selenium.WebDriver;
// import org.openqa.selenium.chrome.ChromeDriver;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.*;
import org.openqa.selenium.support.ui.WebDriverWait;

// import org.junit.Test;
import java.util.concurrent.TimeUnit;
import java.io.FileWriter;

/**
 * Hello world!
 */
public final class App {

    public static void scrollSmooth(WebDriver driver) {
        for (int i = 0; i < 2000; i++) {
            ((JavascriptExecutor) driver).executeScript("window.scrollBy(0,1)", "");
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        // https://www.youtube.com/watch?v=BEvRZUEQ3Dc&list=PL9ac6isfrJo3PqrhtSJox9leOhZkahHrH&index=4
        // Document doc =
        // Jsoup.connect("https://en.wikipedia.org/wiki/List_of_official_languages_by_country_and_territory").timeout(6000).get();
        // Elements body = doc.select("tbody");
        // for(Element e : body.select("tr"))
        // {
        // String img = e.select("td.posterColumn img").attr("src");
        // System.out.println(img);
        // String title = e.select("td").text();
        // System.out.println(title);
        // String year = e.select("td.titleColumn
        // span.secondaryInfo").text().replaceAll("[^\\d]","");
        // // System.out.println(year);
        // String rate = e.select("td.ratingColumn.imdbRating").text().trim();
        // System.out.println(rate);

        // System.setProperty("webdriver.chrome.driver", "/Applications/Google
        // Chrome.app");
        // WebElement searchBox = driver.findElement(By.name("q"));
        // searchBox.sendKeys("ChromeDriver");
        // searchBox.submit();

        // driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

        WebDriver driver = new ChromeDriver();
        driver.get("https://www.tokopedia.com/p/handphone-tablet/handphone?page=1");
        Thread.sleep(3000); // Let the user actually see something!
        scrollSmooth(driver);
        Document doc = Jsoup.parse(driver.getPageSource());
        // System.out.println(doc);

        // 1. Name of Product
        // 2. Description
        // 3. Image Link
        // 4. Price
        // 5. Rating (out of 5 stars)
        // 6. Name of store or merchant

        // write to a file
        try {
            File myObj = new File("tokopedia.html");
            if (myObj.createNewFile()) {
                System.out.println("File created: " + myObj.getName());
            } else {
                System.out.println("File already exists.");
            }
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        try {
            FileWriter myWriter = new FileWriter("tokopedia.html");
            myWriter.write(driver.getPageSource());
            myWriter.close();
            System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        Thread.sleep(3000); // Let the user actually see something!
        Elements body = doc.select("div[data-testid='lstCL2ProductList']");
        //
        
        Integer counter = 1;
        for (Element e : body.select("div.css-bk6tzz")) {
            if(counter <= 5){
                counter++;
                continue;
            }
            // String title = e.select("div:nth-child(2) > div > span").text();
            // 1. Name of Product
            // 2. Description
            // 3. Image Link
            // 4. Price
            // 5. Rating (out of 5 stars)
            // 6. Name of store or merchant
            System.out.println("---------------");
            System.out.println("counter: "+ counter);
            // TITLE
            String title = e.select("span.css-1bjwylw").text().trim();
            System.out.println(title);

            // DESCRIPTION
            
            // PRICE
            String price = e.select("span.css-o5uqvq").text().trim();
            System.out.println(price);

            // Image Link
            String image = e.select("div.css-t8frx0 > img > span").attr("src").trim();
            System.out.println(image);

            // Rating
            String rating = e.select("div.css-153qjw7 > div > span").text().trim();
            System.out.println(rating);

            // Name Of Store or merchant
            String store = e.select("span.css-1kr22w3").text().trim();
            System.out.println(store);
            System.out.println("---------------");
            counter++;

            // String title = e.select("td").text();
            // System.out.println(title);
            // String year = e.select("td.titleColumn
            // span.secondaryInfo").text().replaceAll("[^\\d]", "");
            // System.out.println(year);
            // String rate = e.select("td.ratingColumn.imdbRating").text().trim();
            // System.out.println(rate);

        }
        driver.quit();

    }

}
