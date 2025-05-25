package view;

import model.HighScoreEntry;
import model.HighScoreManager;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class HighScoresView extends JFrame {
    public HighScoresView() {
        setTitle("High Scores");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(300, 400);
        setLocationRelativeTo(null);

        HighScoreManager manager = new HighScoreManager();
        List<HighScoreEntry> highScores = manager.getHighScores();

        DefaultListModel<String> listModel = new DefaultListModel<>();
        for (HighScoreEntry entry : highScores) {
            listModel.addElement(entry.toString());
        }

        JList<String> scoreList = new JList<>(listModel);
        scoreList.setFont(new Font("Monospaced", Font.PLAIN, 16));
        scoreList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(scoreList);
        add(scrollPane, BorderLayout.CENTER);

        setVisible(true);
    }
}
