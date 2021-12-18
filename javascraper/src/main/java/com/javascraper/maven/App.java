package com.javascraper.maven;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.opencsv.CSVWriter;

import java.io.FileWriter;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.*;

public final class App {
    CssSelector cssSelector = new CssSelector();

    public static void main(String[] args) throws Exception, InterruptedException {
        // init array list to store upcoming list of product data and convert to csv
        List<String[]> productArrayList = new ArrayList<>();

        // getting html of product page 1 and parse it (first 55 products) and then add
        // to productArrayList
        WebDriver driver = new ChromeDriver();
        driver.get("https://www.tokopedia.com/p/handphone-tablet/handphone?page=1");
        Thread.sleep(9000);
        scrollSmooth(driver);
        jsoupParser(driver, productArrayList);

        // switch to new tab to open page 2 and parse it (45 products) and then add to
        // productArrayList
        driver.switchTo().newWindow(WindowType.TAB);
        driver.get("https://www.tokopedia.com/p/handphone-tablet/handphone?page=2");
        Thread.sleep(9000);
        scrollSmooth(driver);
        jsoupParser(driver, productArrayList);

        // creating csv file from productArrayList
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        String timeNow = dtf.format(now);
        String fileName = "./csv/tokopedia-handphone-" + timeNow + ".csv";
        csvWriterAll(productArrayList, fileName);
        System.out.println("successfully creating csv file");
        driver.quit();
    }

    // Because tokopedia has lazy load, we need scroll to bottom to render other
    // products
    private static void scrollSmooth(WebDriver driver) {
        for (int i = 0; i < 1500; i++) {
            ((JavascriptExecutor) driver).executeScript("window.scrollBy(0,1)", "");
        }
    }

    private static void csvWriterAll(List<String[]> stringArray, String path) throws Exception {
        CSVWriter writer = new CSVWriter(new FileWriter(path));
        writer.writeAll(stringArray);
        writer.close();
    }

    private static void jsoupParser(WebDriver driver, List<String[]> productArrayList)
            throws InterruptedException, IOException {
        Document doc = Jsoup.parse(driver.getPageSource());
        CssSelector cssSelector = new CssSelector();

        Elements body = doc.select(cssSelector.DOC);

        // init header for the upcoming csv
        if (productArrayList.size() == 0) {
            productArrayList.add(new String[] { "No", "Title", "Rating", "Price", "Image", "Store", "Description" });
        }

        Integer counter = 1;
        System.out.println("start looping and making request for every product detail url...");
        for (Element e : body.select(cssSelector.BODY)) {
            // the first 5 of their product list is like promoted ones so it's not valid
            // list
            if (counter <= 5) {
                counter++;
                continue;
            }
            // for numbering
            Integer NumInt = productArrayList.size();
            String Number = Integer.toString(NumInt);

            // getting title
            String title = e.select(cssSelector.TITLE).text().trim();

            // getting
            String price = e.select(cssSelector.PRICE).text().trim();

            // getting image Link
            String image = e.select(cssSelector.IMAGE).attr("src");

            // getting Name Of Store or merchant
            String store = e.select(cssSelector.STORE).text().trim();

            // count stars in every product list
            Elements ratingSelect = e.select(cssSelector.RATINGBODY);
            Integer ratingCount = 0;

            for (Element el : ratingSelect.select(cssSelector.RATING)) {
                ratingCount++;
            }
            String rating = Integer.toString(ratingCount);

            // DESCRIPTION LINK - in order to get desc we need to open their product page
            String descLink = e.select(cssSelector.DESCRIPTION_LINK).attr("href");

            // Getting Description and Rating from every product detail
            char firstChar = descLink.charAt(10);
            if (firstChar != 'w') {
                String url = URLDecoder.decode(descLink, StandardCharsets.UTF_8.name());

                Map<String, String> params = getQueryMap(url);
                String redirectUrl = (String) params.get("r");

                redirectUrl = StringUtils.substringBefore(redirectUrl, "?");

                descLink = redirectUrl;

            }

            // init for DESCRIPTION
            String description = "";

            if (descLink != null) {
                Document docInside = Jsoup.connect(descLink).timeout(15000).get();
                description = docInside.select(cssSelector.DESCRIPTION).first().text();
            }

            productArrayList
                    .add(new String[] { Number, title, rating, price, image, store, description });

            // stop when productArrayList size is 101 (100 + the header)
            if (productArrayList.size() == 101) {
                break;
            }

            counter++;
        }

    }

    // method for convert query param to Map
    public static Map<String, String> getQueryMap(String query) {
        String[] params = query.split("&");
        Map<String, String> map = new HashMap<String, String>();
        for (String param : params) {
            String[] p = param.split("=");
            String name = p[0];
            if (p.length > 1) {
                String value = p[1];
                map.put(name, value);
            }
        }
        return map;
    }
}
