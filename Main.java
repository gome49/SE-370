import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import org.json.JSONObject;
import org.json.JSONArray;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class Main {
    private static JFrame frame;
    private static JPanel mainPanel;
    private static JPanel statsPanel;
    private static JPanel topPlayersPanel;
    private static CardLayout cardLayout;
    private static JTextArea gptResponseArea; // New JTextArea for GPT response

    // Declare the textArea as an instance variable
    private static JTextArea textArea;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            createAndShowGUI();
        });
    }
    private static JPanel createGraphPanel() {
        JPanel graphPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawGraph(g);
            }
        };

        // Set the preferred size to make the graph panel larger
        graphPanel.setPreferredSize(new Dimension(1000, 400));

        return graphPanel;
    }

    private static void drawGraph(List<String> statsList, Graphics g) {
        int xOffset = 50;
        int yOffset = 50;

        g.setColor(Color.BLUE);

        // Example: Drawing a line for each player's HR (home run) statistic
        for (int i = 1; i < statsList.size(); i++) {
            String[] playerStats = statsList.get(i).split("\t");
            int hrStat = Integer.parseInt(playerStats[8]); // Assuming HR is at index 8
            g.drawLine(xOffset + i * 20, yOffset, xOffset + i * 20, yOffset + hrStat);
        }
    }

    private static void createAndShowGUI() {
        frame = new JFrame("MLB Stats Viewer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(1400, 800));

        mainPanel = new JPanel();
        cardLayout = new CardLayout();
        mainPanel.setLayout(cardLayout);

        // Create the stats panel (default panel)
        statsPanel = createStatsPanel();
        mainPanel.add(statsPanel, "StatsPanel");

        // Create the top players panel
        topPlayersPanel = createTopPlayersPanel();
        mainPanel.add(topPlayersPanel, "TopPlayersPanel");

        // Show the default panel (stats panel)
        cardLayout.show(mainPanel, "StatsPanel");
        // Create the graph panel
        JPanel graphPanel = createGraphPanel();
        mainPanel.add(graphPanel, "GraphPanel");


        frame.add(mainPanel);

        frame.pack();
        frame.setVisible(true);
    }

    private static JPanel createStatsPanel() {

        // Create a JPanel to hold the column names
        JPanel columnNamesPanel = new JPanel();
        // Add a JTextField for entering the year
        JTextField yearInput = new JTextField(4); // 4-character width for the year

        // Create a JComboBox to hold the buttons

        // Create a JTextArea to display the stats
        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setFont(new Font("Arial", Font.BOLD, 20)); // Set the font to Arial, bold, size 16
        textArea.setBackground(Color.LIGHT_GRAY); // Set the background color to a shade of white
        JScrollPane scrollPane = new JScrollPane(textArea);


        // Create a JLabel to hold the image
        JLabel imageLabel = new JLabel();
        ImageIcon imageIcon = new ImageIcon("mlblogo.png"); // Replace with the path to your image
        imageLabel.setIcon(imageIcon);

        // Create a JButton to change the year input
        JButton changeYearButton = new JButton("Change Year");
        changeYearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Retrieve the new year from the input field
                String newYear = yearInput.getText();

                // Update the URL with the new year
                String updatedUrl = "https://www.mlb.com/stats/" + newYear;

                // Scrape data from the updated URL
                String stats = fetchDataFromURL(updatedUrl);
                textArea.setText(stats);
            }
        });
        // Create a JPanel to hold the button at the bottom
        JPanel bottomButtonPanel = new JPanel();
        bottomButtonPanel.add(changeYearButton);

        // Initialize gptResponseArea
        gptResponseArea = new JTextArea();
        gptResponseArea.setEditable(false);
        gptResponseArea.setFont(new Font("Arial", Font.BOLD, 16));
        gptResponseArea.setBackground(Color.WHITE);
        // Wrap the JTextArea in a JScrollPane with a different variable name (e.g., gptScrollPane)
        JScrollPane gptScrollPane = new JScrollPane(gptResponseArea);
        gptScrollPane.setPreferredSize(new Dimension(300, 700));

        // Create a JPanel to hold the image at the top left
        JPanel imagePanel = new JPanel(new BorderLayout());
        imagePanel.add(imageLabel, BorderLayout.WEST);

        // Create a JButton to fetch and display the stats
        JButton fetchButton = new JButton("Player Hitting Stats");
        fetchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String stats = fetchMLBStats();
                textArea.setText(stats);
            }
        });
        // Create a JButton to fetch and display the pitching stats
        JButton fetchPitchingButton = new JButton("Player Pitching Stats");
        fetchPitchingButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String stats = fetchStats("https://www.mlb.com/stats/pitching?sortState=asc");
                textArea.setText(stats);
            }
        });

        // Create a JButton to switch to the Top Players panel
        JButton fetchHittingStatsTeam = new JButton("Team Hitting Stats");
        fetchHittingStatsTeam.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String stats = fetchStatsTeam("https://www.mlb.com/stats/team");
                textArea.setText(stats);
            }
        });

        // Create a JButton to switch to the Graph panel
        JButton showGraphButton = new JButton("Show Graph");
        showGraphButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Switch to the Graph panel when the button is clicked
                cardLayout.show(mainPanel, "GraphPanel");
                // Repaint the graph panel to ensure it reflects the latest data
                mainPanel.getComponent(1).repaint();  // Assumes the graph panel is the second component
            }
        });


        // Create a JButton to switch to the Top Players panel
        JButton topPlayersButton = new JButton("Top Players");
        topPlayersButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String stats = fetchPitchingStats();
                textArea.setText(stats);
                cardLayout.show(mainPanel, "TopPlayersPanel");
            }
        });
        JButton teamPitchingStats = new JButton("Team Pitching Stats");
        teamPitchingStats.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String stats = fetchStatsTeam("https://www.mlb.com/stats/team/pitching?sortState=asc");
                textArea.setText(stats);
            }
        });

        // Create a JPanel to hold the buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(fetchButton);
        //buttonPanel.add(topPlayersButton);
        buttonPanel.add(fetchPitchingButton);
        buttonPanel.add(fetchHittingStatsTeam);
        buttonPanel.add(teamPitchingStats);

        // Create a JPanel to hold the column names and text area
        JPanel statsPanel = new JPanel(new BorderLayout());
        statsPanel.add(bottomButtonPanel, BorderLayout.SOUTH);
        statsPanel.add(scrollPane, BorderLayout.CENTER);
        statsPanel.add(buttonPanel, BorderLayout.NORTH);
        // Added a JTextField for side input
        JTextField sideInputField = new JTextField(10);
        sideInputField.setFont(new Font("Arial", Font.PLAIN, 10)); // Set the font size // Set the width as needed
        statsPanel.add(gptResponseArea, BorderLayout.SOUTH); // Add GPT response area

        // Create a JButton
        JButton askButton = new JButton("Press for a Random Fact About the Data!");
        askButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Call the gptResponse function when the button is clicked
                //callChatGPTAPIcallChatGPTAPI(sideInputField.getText());
                String userInput = sideInputField.getText();
                String gptResponse = callChatGPTAPI(userInput);
                gptResponseArea.setText(gptResponse);
            }
        });

        // Added a JPanel for side input
        JPanel sideInputPanel = new JPanel();
        sideInputPanel.add(new JLabel("Show Graph"));
        sideInputPanel.add(showGraphButton);

        // Added the sideInputPanel to the statsPanel
        statsPanel.add(sideInputPanel, BorderLayout.WEST);
        // Add ActionListener to sideInputField for Enter key
        sideInputField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // This method is called when Enter is pressed in the sideInputField
                String userInput = sideInputField.getText();
                String gptResponse = callChatGPTAPI(userInput);
                gptResponseArea.setText(gptResponse);
            }
        });

        return statsPanel;
    }

    private static String callChatGPTAPI(String prompt) {
        String apiKey = "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";
        String apiEndpoint = "https://api.openai.com/v1/chat/completions";

        try {
            HttpClient client = HttpClient.newHttpClient();
            // Get the MLB stats
            String mlbStats = fetchMLBStats();
            System.out.println(mlbStats);


            // Concatenate the MLB stats with the rest of the prompt
            String fullPrompt = "Here are some stats I found: " + mlbStats + " Based on these stats answer this question: " + prompt;
            System.out.println(fullPrompt);
            // Specify the model parameter and prompt in the request body
            // Construct the JSON payload using JSONObject
            JSONObject requestBodyJson = new JSONObject()
                    .put("model", "gpt-3.5-turbo-1106")
                    .put("messages", new JSONArray()
                            .put(new JSONObject().put("role", "system").put("content", "You are a helpful assistant."))
                            .put(new JSONObject().put("role", "user").put("content", fullPrompt)))
                    .put("temperature", 0.7)
                    .put("max_tokens", 500);

            // Convert JSONObject to a string
            String requestBody = requestBodyJson.toString();

            // Build the HTTP request as before
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiEndpoint))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + apiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                // Parse the JSON response and extract the generated text
                JSONObject jsonResponse = new JSONObject(response.body());
                JSONArray choices = jsonResponse.getJSONArray("choices");
                if (choices.length() > 0) {
                    JSONObject firstChoice = choices.getJSONObject(0);
                    return firstChoice.getString("text");
                } else {
                    return "No text generated by GPT.";
                }
            } else {
                return "API Request failed with status code: " + response.statusCode() + "\n" +
                        "Response body: " + response.body();
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }
    private static JPanel createTopPlayersPanel() {
        // Create a panel for the Top Players content
        JPanel topPlayersPanel = new JPanel();
        // Customize this panel with the content you want to display for Top Players

        // Create a button to go back to the Stats panel
        JButton backButton = new JButton("Back to Stats");
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(mainPanel, "StatsPanel");
            }
        });

        topPlayersPanel.add(backButton);

        return topPlayersPanel;
    }

    private static String fetchMLBStats() {
        StringBuilder stats = new StringBuilder();
        // URL of the MLB stats page
        String url = "https://www.mlb.com/stats";

        try {
            // Connect to the website and fetch the HTML content
            Document document = Jsoup.connect(url).get();

            if (document != null) {
                Elements elements = document.select(".full-3fV3c9pF"); // Select player names
                Elements rows = document.select("tr"); // Select rows of stats data
                String topNames = ("Player\tTeam\tG\tAB\tR\tH\t2B\t3B\tHR\tRBI\tBB\tSO\tSB\tCS\tAVG\tOBP\tSLG\tOPS");
                stats.append(topNames);

                if (!elements.isEmpty()) {
                    for (int i = 0; i < rows.size(); i++) {
                        Element row = rows.get(i);
                        Elements columns = row.select("td"); // Assuming the columns are represented by <td> elements

                        if (i < elements.size()) {
                            String playerName = elements.get(i).text(); // Get the player name for the current row
                            stats.append(playerName).append("\t");
                            for (Element column : columns) {
                                // Extract the data from the current column
                                String columnData = column.text();
                                stats.append(columnData).append("\t");

                            }
                            stats.append("\n"); // Add a new line after each row
                        }
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return stats.toString();
    }
    private static String fetchStats(String site_url) {
        StringBuilder stats = new StringBuilder();
        // URL of the MLB stats page
        //String url = site_url;

        try {
            // Connect to the website and fetch the HTML content
            Document document = Jsoup.connect(site_url).get();

            if (document != null) {
                Elements elements = document.select(".full-3fV3c9pF"); // Select player names
                Elements rows = document.select("tr"); // Select rows of stats data
                String topNames = ("Player\tTeam\tG\tAB\tR\tH\t2B\t3B\tHR\tRBI\tBB\tSO\tSB\tCS\tAVG\tOBP\tSLG\tOPS");
                stats.append(topNames);

                if (!elements.isEmpty()) {
                    for (int i = 0; i < rows.size(); i++) {
                        Element row = rows.get(i);
                        Elements columns = row.select("td"); // Assuming the columns are represented by <td> elements

                        if (i < elements.size()) {
                            String playerName = elements.get(i).text(); // Get the player name for the current row
                            stats.append(playerName).append("\t");
                            for (Element column : columns) {
                                // Extract the data from the current column
                                String columnData = column.text();
                                stats.append(columnData).append("\t");

                            }
                            stats.append("\n"); // Add a new line after each row
                        }
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return stats.toString();
    }
    private static String fetchStatsTeam(String site_url) {
        StringBuilder stats = new StringBuilder();
        // URL of the MLB stats page
        //String url = site_url;

        try {
            // Connect to the website and fetch the HTML content
            Document document = Jsoup.connect(site_url).get();

            if (document != null) {
                Elements elements = document.select(".full-3fV3c9pF"); // Select player names
                Elements rows = document.select("tr"); // Select rows of stats data
                String topNames = ("Location\tTeam\tG\tAB\tR\tH\t2B\t3B\tHR\tRBI\tBB\tSO\tSB\tCS\tAVG\tOBP\tSLG\tOPS");
                stats.append(topNames);

                if (!elements.isEmpty()) {
                    for (int i = 0; i < rows.size(); i++) {
                        Element row = rows.get(i);
                        Elements columns = row.select("td"); // Assuming the columns are represented by <td> elements

                        if (i < elements.size()) {
                            String playerName = elements.get(i).text(); // Get the player name for the current row
                            stats.append(playerName).append("\t");
                            for (Element column : columns) {
                                // Extract the data from the current column
                                String columnData = column.text();
                                stats.append(columnData).append("\t");

                            }
                            stats.append("\n"); // Add a new line after each row
                        }
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return stats.toString();
    }
    private static String fetch() {
        StringBuilder stats = new StringBuilder();
        // URL of the MLB stats page
        String url = "https://www.mlb.com/stats/pitching?sortState=asc";

        try {
            // Connect to the website and fetch the HTML content
            Document document = Jsoup.connect(url).get();

            if (document != null) {
                Elements elements = document.select(".full-3fV3c9pF"); // Select player names
                Elements rows = document.select("tr"); // Select rows of stats data
                String topNames = ("Player\tTeam\tG\tAB\tR\tH\t2B\t3B\tHR\tRBI\tBB\tSO\tSB\tCS\tAVG\tOBP\tSLG\tOPS");
                stats.append(topNames);

                if (!elements.isEmpty()) {
                    for (int i = 0; i < rows.size(); i++) {
                        Element row = rows.get(i);
                        Elements columns = row.select("td"); // Assuming the columns are represented by <td> elements

                        if (i < elements.size()) {
                            String playerName = elements.get(i).text(); // Get the player name for the current row
                            stats.append(playerName).append("\t");
                            for (Element column : columns) {
                                // Extract the data from the current column
                                String columnData = column.text();
                                stats.append(columnData).append("\t");

                            }
                            stats.append("\n"); // Add a new line after each row
                        }
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return stats.toString();
    }

    private static String fetchPitchingStats() {
        StringBuilder stats = new StringBuilder();
        // URL for pitching stats
        String url = "https://www.mlb.com/stats/pitching?sortState=asc"; // Replace with the actual URL

        try {
            // Connect to the website and fetch the HTML content
            Document document = Jsoup.connect(url).get();

            // Select and scrape data
            if (document != null) {
                Elements rows = document.select("tr"); // Assuming the columns are within table rows
                String topNames = ("Player\tTeam\tG\tAB\tR\tH\t2B\t3B\tHR\tRBI\tBB\tSO\tSB\tCS\tAVG\tOBP\tSLG\tOPS");
                stats.append(topNames);

                if (!rows.isEmpty()) {
                    for (int i = 0; i < rows.size(); i++) {
                        Element row = rows.get(i);
                        Elements columns = row.select("td"); // Assuming the columns are represented by <td> elements

                        if (i < rows.size()) {
                            String playerName1 = rows.get(i).text(); // Get the player name for the current row
                            stats.append(playerName1).append("\t"); // Append the player name to the row
                            for (Element column : columns) {
                                // Extract the data from the current column
                                String columnData = column.text();
                                stats.append(columnData).append("\t"); // Append the stats data
                            }
                            stats.append("\n"); // Add a new line after each row
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return stats.toString();
    }


    private static String fetchDataFromURL(String url) {
        StringBuilder stats = new StringBuilder();

        try {
            // Connect to the website and fetch the HTML content from the provided URL
            Document document = Jsoup.connect(url).get();

            // Select and scrape data
            if (document != null) {
                Elements rows = document.select("tr"); // Assuming the columns are within table rows
                String topNames = ("Player\tTeam\tG\tAB\tR\tH\t2B\t3B\tHR\tRBI\tBB\tSO\tSB\tCS\tAVG\tOBP\tSLG\tOPS");
                stats.append(topNames);

                for (Element row : rows) {
                    Elements columns = row.select("td"); // Assuming the columns are represented by <td> elements

                    for (Element column : columns) {
                        // Extract the data from the current column
                        String columnData = column.text();
                        stats.append(columnData).append("\t"); // Separate elements with tabs
                    }
                    stats.append("\n"); // Add a new line after each row
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return stats.toString();
    }


}
