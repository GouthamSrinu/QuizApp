package com.example;

import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.text.Document;

public class QuestionFrame extends JFrame {
    private int correctAnswers = 0; // Tracks the number of correct answers
    MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
    MongoDatabase database = mongoClient.getDatabase("questions");

    // Constructor for QuestionFrame
    public QuestionFrame(String username) {
        // Setup the frame
        setTitle("OOPS Examination Client");
        setSize(950, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create a main panel with vertical BoxLayout
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Add heading label with a welcome message
        JLabel heading = new JLabel("Welcome to your test " + username + ". Good Luck!");
        heading.setFont(new Font("Arial", Font.PLAIN, 20));
        heading.setHorizontalAlignment(JLabel.CENTER); // Center-align the heading
        heading.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        panel.add(heading);

        // Retrieve questions from the database and add them to the panel
        retrieveQuestions(panel);

        // Add vertical space before the submit button to push it to the bottom
        panel.add(Box.createVerticalStrut(20));

        // Create and customize the submit button
        JButton submitButton = new JButton("Submit");
        customizeSubmitButton(submitButton);
        submitButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(submitButton);
        final String s = username;

        // Add action listener to the submit button
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleSubmitButtonClick(s);
            }
        });

        // Add a scroll pane to the frame to make it scrollable
        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        getContentPane().add(scrollPane);
    }

    // Function to add a single question with options to the panel
    private void addQuestion(JPanel panel, String questionText, String option1, String option2, String option3,
            String option4, String correct) {
        // Create a new panel for each question
        JPanel questionPanel = new JPanel();
        questionPanel.setLayout(new BoxLayout(questionPanel, BoxLayout.Y_AXIS));

        // Add question label
        JLabel questionLabel = new JLabel(questionText);
        questionLabel.setFont(new Font("Arial", Font.BOLD, 16));

        questionPanel.add(questionLabel);

        // Create options radio buttons
        JRadioButton optionButton1 = new JRadioButton(option1);
        JRadioButton optionButton2 = new JRadioButton(option2);
        JRadioButton optionButton3 = new JRadioButton(option3);
        JRadioButton optionButton4 = new JRadioButton(option4);

        // Customize radio buttons
        customizeRadioButton(optionButton1);
        customizeRadioButton(optionButton2);
        customizeRadioButton(optionButton3);
        customizeRadioButton(optionButton4);

        // Group all the options together
        ButtonGroup group = new ButtonGroup();
        group.add(optionButton1);
        group.add(optionButton2);
        group.add(optionButton3);
        group.add(optionButton4);

        // Add the options to the question panel
        questionPanel.add(optionButton1);
        questionPanel.add(optionButton2);
        questionPanel.add(optionButton3);
        questionPanel.add(optionButton4);

        // Add the question panel to the main panel
        panel.add(questionPanel);

        // Add action listeners for the options to track correct answers
        addOptionActionListeners(optionButton1, optionButton2, optionButton3, optionButton4, correct);
    }

    // Function to add action listeners to the radio buttons (options) of a question
    private void addOptionActionListeners(JRadioButton optionButton1, JRadioButton optionButton2,
            JRadioButton optionButton3, JRadioButton optionButton4, String correctOption) {
        final String finalCorrectOption = correctOption;
        final String op1Txt = optionButton1.getText();
        final String op2Txt = optionButton2.getText();
        final String op3Txt = optionButton3.getText();
        final String op4Txt = optionButton4.getText();

        // Action listener for option 1
        optionButton1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Increment correct answers count if the selected option matches the correct
                // option
                if (op1Txt.equals(finalCorrectOption)) {
                    correctAnswers++;
                }
            }
        });

        // Action listener for option 2
        optionButton2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (op2Txt.equals(finalCorrectOption)) {
                    correctAnswers++;
                }
            }
        });

        // Action listener for option 3
        optionButton3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (op3Txt.equals(finalCorrectOption)) {
                    correctAnswers++;
                }
            }
        });

        // Action listener for option 4
        optionButton4.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (op4Txt.equals(finalCorrectOption)) {
                    correctAnswers++;
                }
            }
        });
    }

    // Function to retrieve questions from the database and add them to the panel
    private void retrieveQuestions(JPanel panel) {
        try {
            // Retrieve the collection containing questions
            MongoCollection<Document> collection = database.getCollection("jpqs");
            int questionCount = 0;

            // Iterate through the documents in the collection and retrieve question data
            for (Document doc : collection.find()) {
                String question = doc.getString("question");
                @SuppressWarnings("unchecked")
                List<String> options = doc.get("options", List.class);
                String option1 = options.get(0);
                String option2 = options.get(1);
                String option3 = options.get(2);
                String option4 = options.get(3);
                String correct = doc.getString("correctOption");

                // Add each question to the panel
                addQuestion(panel, question, option1, option2, option3, option4, correct);
                questionCount++;

                // Only retrieve 5 questions for the quiz
                if (questionCount >= 5) {
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace(); // Print any exceptions that occur
        }
    }

    // Function to customize the submit button
    private void customizeSubmitButton(JButton button) {
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setBackground(new Color(60, 180, 75)); // Set the background color of the button
        button.setOpaque(true); // Make the button opaque
        button.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 2)); // Add a border
        button.setFocusPainted(false); // Remove focus border from the button
        button.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add padding to the button

    }

    // Function to customize radio buttons (options)
    private void customizeRadioButton(JRadioButton button) {
        button.setFont(new Font("Arial", Font.PLAIN, 14));
        button.setForeground(Color.BLACK); // Set the text color of the button
        button.setOpaque(true); // Make the button opaque
        button.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 2)); // Add a border
        button.setFocusPainted(false); // Remove focus border from the button
        button.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5)); // Add padding around the button
    }

    // Function to handle the submit button click event
    private void handleSubmitButtonClick(String uname) {
        // Access the results database and collection
        MongoDatabase resultsDB = mongoClient.getDatabase("results");
        MongoCollection<Document> resultsCollection = resultsDB.getCollection("exam_results");

        // Create a document with the user's name and marks, then insert it into the
        // collection
        Document resultsDocument = new Document()
                .append("name", uname)
                .append("marks", correctAnswers);
        resultsCollection.insertOne(resultsDocument);

        // Display a message based on the user's score
        if (correctAnswers > 2) {
            JOptionPane.showMessageDialog(QuestionFrame.this,
                    "The Quiz Has Ended!\nCongratulations! You passed. Your score is " + correctAnswers + "/5");
        } else {
            JOptionPane.showMessageDialog(QuestionFrame.this,
                    "The Quiz Has Ended!\nSorry, you did not pass. Your score is " + correctAnswers + "/5");
        }
        dispose();
    }
}
