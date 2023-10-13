package org.example;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {
    public static void main(String[] args) {
        // URL of the MLB stats page
        String url = "https://www.mlb.com/stats/";

        try {
            // Connect to the website and fetch the HTML content
            Document document = Jsoup.connect(url).get();

            // Select and scrape data
            Elements statRows = document.select("table.stats-table tr"); // Assuming stats are in table rows

            for (Element row : statRows) {
                Elements columns = row.select("td"); // Assuming stats are in table data cells
                if (columns.size() >= 3) { // Check if it's a valid data row
                    String playerName = columns.get(0).text();
                    String team = columns.get(1).text();
                    String statValue = columns.get(2).text();

                    System.out.println("Player: " + playerName);
                    System.out.println("Team: " + team);
                    System.out.println("Stat: " + statValue);
                    System.out.println();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}