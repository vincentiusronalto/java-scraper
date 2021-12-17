package com.javascraper.maven;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.opencsv.CSVWriter;

import java.io.FileWriter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.*;

public final class App {
    public static void main(String[] args) throws Exception {
        WebDriver driver = new ChromeDriver();
        driver.get("https://www.tokopedia.com/p/handphone-tablet/handphone?page=1");
        Thread.sleep(9000);
        scrollSmooth(driver);
        jsouParser(driver);

        WebDriver driver2 = new ChromeDriver();
        driver2.get("https://www.tokopedia.com/p/handphone-tablet/handphone?page=2");
        Thread.sleep(9000);
        scrollSmooth(driver2);
        jsouParser(driver2);

    }

    public static void scrollSmooth(WebDriver driver) {
        for (int i = 0; i < 2000; i++) {
            ((JavascriptExecutor) driver).executeScript("window.scrollBy(0,1)", "");
        }
    }

    public static void csvWriterAll(List<String[]> stringArray, String path) throws Exception {
        CSVWriter writer = new CSVWriter(new FileWriter(path));
        writer.writeAll(stringArray);
        writer.close();
    }

    public static void jsouParser(WebDriver driver) throws Exception {
        Document doc = Jsoup.parse(driver.getPageSource());

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
        List<String[]> dataLines = new ArrayList<>();
        dataLines.add(new String[] { "No", "Title", "Price", "Image", "Rating", "Store", "Description" });
        Integer counter = 1;
        for (Element e : body.select("div.css-bk6tzz")) {
            if (counter <= 5) {
                counter++;
                continue;
            }

            // TITLE
            String title = e.select("span.css-1bjwylw").text().trim();

            // PRICE
            String price = e.select("span.css-o5uqvq").text().trim();

            // Image Link
            String image = e.select("img.fade").attr("src");

            // Rating
            String rating = e.select("div.css-153qjw7 > div > span").text().trim();

            // Name Of Store or merchant
            String store = e.select("span.css-1kr22w3").text().trim();

            // Thread.sleep(300);

            // DESCRIPTION
            // String descLink = e.select("a.css-89jnbj").attr("href").trim();
            // System.out.println(descLink);
            // Document docInside = Jsoup.connect(descLink).timeout(600000).get();
            // String description =
            // docInside.select("div[data-testid='lblPDPDescriptionProduk']").first().text().trim();
            // System.out.println(description);
            // docInside.disconnect();
            dataLines
                    .add(new String[] { Integer.toString(counter), title, price, image, rating, store, "Description" });
            counter++;
        }
        for (String[] i : dataLines) {
            System.out.println(Arrays.toString(i));
        }
        driver.quit();

        csvWriterAll(dataLines, "ouput.csv");

    }

}
