/*
 * 
 * Diypa571
 * Github Searcher
 * API Request
 * 
 * 
 */
package javaGitHubApi;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class javaGitHubApi {

    private static final String accessToken = "YOUR_ACCESS_TOKEN";  

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> createAndShowGUI());
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("GitHub API Request");

        JLabel label = new JLabel("Enter GitHub username:");
        JTextField textField = new JTextField(20);
        JButton submitButton = new JButton("Submit");
        JLabel userImageLabel = new JLabel();
        userImageLabel.setHorizontalAlignment(JLabel.CENTER);

        submitButton.addActionListener(e -> handleApiRequest(frame, textField, userImageLabel));

        JPanel panel = new JPanel(new BorderLayout());
        JPanel inputPanel = new JPanel(new FlowLayout());
        inputPanel.add(label);
        inputPanel.add(textField);
        inputPanel.add(submitButton);
        panel.add(inputPanel, BorderLayout.NORTH);
        panel.add(userImageLabel, BorderLayout.CENTER);

        frame.getContentPane().add(panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 800);
        frame.setVisible(true);
    }

    private static void handleApiRequest(JFrame frame, JTextField textField, JLabel userImageLabel) {
        String username = textField.getText();
        if (!username.isEmpty()) {
            SwingWorker<Void, Void> worker = new SwingWorker<>() {
                @Override
                protected Void doInBackground() {
                    try {
                        String apiUrl = "https://api.github.com/users/" + username;
                        URL url = new URL(apiUrl);
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                        try (Scanner scanner = new Scanner(connection.getInputStream())) {
                            StringBuilder response = new StringBuilder();
                            while (scanner.hasNextLine()) {
                                response.append(scanner.nextLine());
                            }

                            String responseString = response.toString();
                            if (responseString.contains("\"avatar_url\":\"")) {
                                String avatarUrl = responseString.split("\"avatar_url\":\"")[1].split("\"")[0];
                                ImageIcon imageIcon = new ImageIcon(new URL(avatarUrl));
                                userImageLabel.setIcon(imageIcon);
                            } else {
                                handleApiRequestError(frame, " Användaren har bild... ");
                            }
                        }
                    } catch (IOException ex) {
                        handleApiRequestError(frame, "Felaktig....");
                    }
                    return null;
                }
            };

            worker.execute();
        } else {
            handleApiRequestError(frame, "Ange din github namn");
        }
    }

    private static void handleApiRequestError(JFrame frame, String errorMessage) {
        JOptionPane.showMessageDialog(frame, errorMessage, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
