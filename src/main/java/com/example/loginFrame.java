package com.example;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.text.Document;

public class loginFrame {
    private JTextField usernameField; // Text field for entering the username
    private JPasswordField passwordField; // Password field for entering the password

    // Constructor for loginFrame
    public loginFrame() {
        // Set up the JFrame properties
        setTitle("Login"); // Set the title of the frame
        setSize(300, 140); // Set the size of the frame
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Exit the application on close

        // Create the main panel for the frame
        JPanel panel = new JPanel();

        // Create labels and fields for entering the username and password
        JLabel usernameLabel = new JLabel("Username:"); // Label for username
        JLabel passwordLabel = new JLabel("Password:"); // Label for password
        usernameField = new JTextField(15); // Text field for entering username (15 columns wide)
        passwordField = new JPasswordField(15); // Password field for entering password (15 columns wide)
        // Create the login button
        JButton loginButton = new JButton("Login");

        // Add an action listener to the login button
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Retrieve the username and password from the respective fields
                String username = usernameField.getText(); // Get the entered username
                String password = new String(passwordField.getPassword()); // Get the entered password

                // Authenticate the user with the provided username and password
                boolean isAuthenticated = authenticate(username, password);

                if (isAuthenticated) {
                    // If authentication is successful, close the current frame
                    // and open a new QuestionFrame for the user
                    dispose(); // Close the login frame
                    new QuestionFrame(username).setVisible(true); // Open the question frame
                } else {
                    // If authentication fails, display an error message
                    JOptionPane.showMessageDialog(loginFrame.this, "Invalid username or password");
                }
            }
        });

        // Add labels and fields for username and password to the panel
        panel.add(usernameLabel);
        panel.add(usernameField);
        panel.add(passwordLabel);
        panel.add(passwordField);
        panel.add(loginButton);

        // Customize the login button appearance
        customizeButton(loginButton);

        // Add the panel to the frame
        add(panel);
        setLocationRelativeTo(null); // Center the frame on the screen
    }

    // Function to authenticate the user using the provided username and password
    private boolean authenticate(String username, String password) {
        // Create a connection to the MongoDB server
        try (com.mongodb.client.MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017")) {
            // Access the database containing the user collection
            com.mongodb.client.MongoDatabase database = mongoClient.getDatabase("users");
            com.mongodb.client.MongoCollection<Document> collection = database.getCollection("jprojuser");

            // Create a query document with the provided username and password
            Document query = new Document("username", username).append("password", password);
            // Count the number of documents that match the query (username and password)
            long count = collection.countDocuments(query);

            // Return true if there is exactly one document that matches the query
            // (successful authentication)
            return count == 1;
        } catch (Exception e) {
            // Print any exceptions that occur during authentication
            System.out.println("Caught exception during authentication");
            e.printStackTrace();
            // Return false if an exception occurs
            return false;
        }
    }

    // Function to customize the appearance of the login button
    private void customizeButton(JButton button) {
        // Set the font style and size for the button text
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setBackground(new Color(60, 180, 75)); // Set the background color of the button
        button.setForeground(Color.WHITE); // Set the text color of the button
        button.setOpaque(true); // Make the button opaque (no transparency)
        button.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 2)); // Add a dark gray border
        button.setFocusPainted(false); // Remove the focus border from the button
        button.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add padding around the button
    }

    // Main function to launch the login frame
    public static void main(String[] args) {
        // Create a new login frame instance and make it visible
        new loginFrame().setVisible(true);
    }
}