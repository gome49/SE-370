import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class Main {
    private static JFrame frame;
    private static JPanel mainPanel;
    private static JPanel statsPanel;
    private static JPanel topPlayersPanel;
    private static CardLayout cardLayout;
    // Declare the textArea as an instance variable
    private static JTextArea textArea;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            createAndShowGUI();
        });
    }

    private static void createAndShowGUI() {
        frame = new JFrame("MLB Stats Viewer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(800, 600));

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
        textArea.setFont(new Font("Arial", Font.BOLD, 16)); // Set the font to Arial, bold, size 16
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
                String stats = fetchPitchingStats();
                textArea.setText(stats);
            }
        });

        // Create a JButton to switch to the Top Players panel
        JButton fetchHittingStatsTeam = new JButton("Team Hitting Stats");
        fetchHittingStatsTeam.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String stats = fetchHittingStatsTeam();
                textArea.setText(stats);
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
                String stats = fetchPitchingStats();
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
        //statsPanel.add(columnNamesPanel, BorderLayout.NORTH);
        statsPanel.add(scrollPane, BorderLayout.CENTER);
        statsPanel.add(buttonPanel, BorderLayout.NORTH);
        //statsPanel.add(imagePanel, BorderLayout.NORTH);



        return statsPanel;
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

                if (!elements.isEmpty()) {
                    for (int i = 0; i < rows.size(); i++) {
                        Element row = rows.get(i);
                        Elements columns = row.select("td"); // Assuming the columns are represented by <td> elements

                        if (i < elements.size()) {
                            String playerName = elements.get(i).text(); // Get the player name for the current row

                            for (Element column : columns) {
                                // Extract the data from the current column
                                String columnData = column.text();
                                stats.append(playerName).append("\t"); // Append the player name to the row
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
    private static String fetchHittingStatsTeam() {
        StringBuilder stats = new StringBuilder();
        // URL for pitching stats
        String url = "https://www.mlb.com/stats/team"; // Replace with the actual URL

        try {
            // Connect to the website and fetch the HTML content
            Document document = Jsoup.connect(url).get();

            // Select and scrape data
            if (document != null) {
                Elements rows = document.select("tr"); // Assuming the columns are within table rows

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
    private static String teamPitchingStats() {
        StringBuilder stats = new StringBuilder();
        // URL for pitching stats
        String url = "https://www.mlb.com/stats/team/pitching?sortState=asc"; // Replace with the actual URL

        try {
            // Connect to the website and fetch the HTML content
            Document document = Jsoup.connect(url).get();

            // Select and scrape data
            if (document != null) {
                Elements rows = document.select("tr"); // Assuming the columns are within table rows

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
    private static String fetchDataFromURL(String url) {
        StringBuilder stats = new StringBuilder();

        try {
            // Connect to the website and fetch the HTML content from the provided URL
            Document document = Jsoup.connect(url).get();

            // Select and scrape data
            if (document != null) {
                Elements rows = document.select("tr"); // Assuming the columns are within table rows

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
