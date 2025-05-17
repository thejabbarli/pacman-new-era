package view;

import model.BoardModel;

import javax.swing.*;
import java.awt.*;

public class BoardView extends JPanel {
    private JTable gameTable;
    private BoardModel boardModel;
    private JLabel scoreLabel;
    private JLabel timeLabel;
    private JLabel livesLabel;

    public BoardView(BoardModel boardModel) {
        this.boardModel = boardModel;

        setLayout(new BorderLayout());

        // Create the game status panel (top)
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        scoreLabel = new JLabel("Score: 0");
        timeLabel = new JLabel("Time: 0");
        livesLabel = new JLabel("Lives: 3");

        statusPanel.add(scoreLabel);
        statusPanel.add(timeLabel);
        statusPanel.add(livesLabel);

        // Create the game table (center)
        gameTable = new JTable(boardModel);
        gameTable.setDefaultRenderer(Object.class, new BoardCellRenderer());
        gameTable.setRowHeight(20); // Initial cell size
        gameTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        gameTable.setShowGrid(false);
        gameTable.setIntercellSpacing(new Dimension(0, 0));
        gameTable.setTableHeader(null); // Hide the header

        // Make all columns the same width
        for (int i = 0; i < boardModel.getColumnCount(); i++) {
            gameTable.getColumnModel().getColumn(i).setPreferredWidth(20);
        }

        // Add components to the panel
        add(statusPanel, BorderLayout.NORTH);
        add(new JScrollPane(gameTable), BorderLayout.CENTER);

        // Make the components not focusable to allow keyboard input to the panel
        gameTable.setFocusable(false);
        setFocusable(true);
    }

    public void updateScore(int score) {
        scoreLabel.setText("Score: " + score);
    }

    public void updateTime(int time) {
        timeLabel.setText("Time: " + time);
    }

    public void updateLives(int lives) {
        livesLabel.setText("Lives: " + lives);
    }

    public JTable getGameTable() {
        return gameTable;
    }

    public void resizeBoard() {
        int cellSize = calculateCellSize();
        gameTable.setRowHeight(cellSize);

        for (int i = 0; i < boardModel.getColumnCount(); i++) {
            gameTable.getColumnModel().getColumn(i).setPreferredWidth(cellSize);
        }
    }

    private int calculateCellSize() {
        // Calculate cell size based on available space
        Dimension viewSize = getSize();
        int boardSize = boardModel.getSize();

        int widthPerCell = viewSize.width / boardSize;
        int heightPerCell = viewSize.height / boardSize;

        return Math.min(widthPerCell, heightPerCell);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        resizeBoard();
    }
}