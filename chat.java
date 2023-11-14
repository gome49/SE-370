import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.io.IOException;



public class ChatGUI extends JFrame {

    private JTextField promptField;
    private JTextArea messageArea;

    public ChatGUI() {
        // Set up the JFrame
        setTitle("Chat GUI");
        setSize(400, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create components
        promptField = new JTextField();
        JButton submitButton = new JButton("Submit");
        messageArea = new JTextArea();
        messageArea.setEditable(false);

        // Set up layout
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        // Add components to the frame
        add(new JLabel("Enter Prompt:"));
        add(promptField);
        add(submitButton);
        add(new JScrollPane(messageArea));

        // Add action listener to the submit button
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Get the prompt from the text field
                String prompt = promptField.getText();

                // Call ChatGPT API
                String apiResponse = callChatGPTAPI(prompt);

                // Display the API response in the message area
                messageArea.append("User: " + prompt + "\n");
                messageArea.append("ChatGPT: " + apiResponse + "\n");

                // Clear the prompt field
                promptField.setText("");
            }
        });
    }

    // Function to call ChatGPT API

    private String callChatGPTAPI(String prompt) {
        String apiKey = "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";
        String apiEndpoint = "https://api.openai.com/v1/completions";

        try {
            HttpClient client = HttpClient.newHttpClient();

            // Specify the model parameter and prompt in the request body
            String requestBody = "{\"model\": \"text-davinci-003\", \"prompt\": \"" + prompt + "\", \"temperature\": 0.7, \"max_tokens\": 150}";

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


    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ChatGUI().setVisible(true);
            }
        });
    }
}
