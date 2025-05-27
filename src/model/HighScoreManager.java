package model;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class HighScoreManager {
    private static final String FILE_NAME = "highscores.ser";
    private List<HighScoreEntry> highScores;

    public HighScoreManager() {
        loadScores();
    }
    public void addScore(String name, int score) {
        highScores.add(new HighScoreEntry(name, score));
        highScores.sort(Comparator.comparingInt(HighScoreEntry::getScore).reversed());
        saveScores();
    }
    public List<HighScoreEntry> getHighScores() {
        return new ArrayList<>(highScores);
    }
    private void loadScores() {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
            highScores = (List<HighScoreEntry>) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            highScores = new ArrayList<>();
        }
    }
    private void saveScores() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            out.writeObject(highScores);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
