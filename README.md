# JAVASCRAPER

# Introduction
1. This tokopedia scraper using Java as the programming language
2. Chromedriver and Selenium for waiting the page to load scrolling to bottom and getting html
3. Jsoup to extract info from the html
4. OpenCSV for writing to csv file
4. The main logic on src folder
5. The example output on csv folder

# Flow of the program
1. Initialize arrayList for saving product data
2. Create new ChromeDriver instance
3. Getting html from tokopedia page 1
4. Processing with Jsoup add record to arrayList, 
every loop make request to product detail page to get description, 
that's why it's take a while to produce 100 records
5. The Precise Rating (e.g. 4.8) Only appear on product detail page (same like description) and need 
ajax call so jsoup cannot utilize it (need nested calling selenium that it 
will take so many resource and time, so I skip it for now), so I use the count star image on product listings
6. Getting the title, price, store, image link is straightforward thats appear on every listings
7. Append all record to arrayList, 
8. Proceed open & switch new tab in chromedriver and open page 2
9. Repeat the step 3-7
10. convert data from arrayList with opencsv and write to folder csv, as filename-date_time.csv

