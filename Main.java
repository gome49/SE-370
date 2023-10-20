package org.example;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        // URL of the MLB stats page
        String url = "https://www.mlb.com/stats";

        try {
            // Connect to the website and fetch the HTML content
            Document document = Jsoup.connect(url).get();

            // Select and scrape data
            if (document != null) {
                Elements rows = document.select("tr"); // Assuming the columns are within table rows

                int printCounter = 0; // Initialize a counter

                for (Element row : rows) {
                    Elements columns = row.select("td"); // Assuming the columns are represented by <td> elements

                    for (Element column : columns) {
                        // Extract the data from the current column
                        String columnData = column.text();
                        System.out.print(columnData); // Print the data without a new line
                        printCounter++;

                        if (printCounter % 17 == 0) {
                            System.out.println(); // Insert a new line after every 16 elements
                        } else {
                            System.out.print("\t"); // Add a tab between elements within the same row
                        }
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
