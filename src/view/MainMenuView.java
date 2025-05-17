package view;

import model.utils.ResourceManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class MainMenuView extends JFrame {
    private JButton newGameButton;
    private JButton highScoresButton;
    private JButton exitButton;
    private JPanel mainPanel;
    private Image backgroundImage;

    public MainMenuView() {
        setTitle("Pacman Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Fully exit application when main menu is closed

        // Get the background image
        ResourceManager resourceManager = ResourceManager.getInstance();
        backgroundImage = resourceManager.getImage("menu_background");

        // Create a custom panel with background image
        mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Draw the background image
                if (backgroundImage != null) {
                    g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                } else {
                    g.setColor(Color.BLACK);
                    g.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };

        mainPanel.setLayout(new GridLayout(3, 1, 10, 10));

        // Create stylish buttons
        newGameButton = createStyledButton("New Game");
        highScoresButton = createStyledButton("High Scores");
        exitButton = createStyledButton("Exit");

        // Add components to panel
        mainPanel.add(newGameButton);
        mainPanel.add(highScoresButton);
        mainPanel.add(exitButton);

        // Set up the frame
        add(mainPanel);
        setSize(400, 300);
        setLocationRelativeTo(null); // Center on screen
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(0, 0, 100));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        return button;
    }

    public void addNewGameListener(ActionListener listener) {
        newGameButton.addActionListener(listener);
    }

    public void addHighScoresListener(ActionListener listener) {
        highScoresButton.addActionListener(listener);
    }

    public void addExitListener(ActionListener listener) {
        exitButton.addActionListener(listener);
    }

    public int showBoardSizeDialog() {
        String sizeStr = JOptionPane.showInputDialog(
                this,
                "Enter the size of the board (10-100):",
                "Board Size",
                JOptionPane.QUESTION_MESSAGE
        );

        try {
            int size = Integer.parseInt(sizeStr);
            if (size >= 10 && size <= 100) {
                return size;
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "Please enter a number between 10 and 100",
                        "Invalid Input",
                        JOptionPane.ERROR_MESSAGE
                );
                return -1;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please enter a valid number",
                    "Invalid Input",
                    JOptionPane.ERROR_MESSAGE
            );
            return -1;
        }
    }
}