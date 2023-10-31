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

        // ... (same code as previous for column names)

        // Create a JTextArea to display the stats
        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);

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
        buttonPanel.add(topPlayersButton);
        buttonPanel.add(fetchPitchingButton);
        buttonPanel.add(fetchHittingStatsTeam);
        buttonPanel.add(teamPitchingStats);

        // Create a JPanel to hold the column names and text area
        JPanel statsPanel = new JPanel(new BorderLayout());
        statsPanel.add(columnNamesPanel, BorderLayout.NORTH);
        statsPanel.add(scrollPane, BorderLayout.CENTER);
        statsPanel.add(buttonPanel, BorderLayout.SOUTH);

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

}
