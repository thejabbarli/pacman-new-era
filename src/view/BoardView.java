package view;

import model.BoardModel;
import model.utils.ResourceManager;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class BoardView extends JPanel {
    private JTable gameTable;
    private BoardModel boardModel;
    private JLabel scoreLabel;
    private JLabel timeLabel;
    private JLabel livesLabel;
    private JScrollPane scrollPane;

    private static final int MIN_CELL_SIZE = 10;

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

        // Create the game table
        gameTable = new JTable(boardModel);
        gameTable.setDefaultRenderer(Object.class, new BoardCellRenderer());
        gameTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        gameTable.setShowGrid(false);
        gameTable.setIntercellSpacing(new Dimension(0, 0));
        gameTable.setTableHeader(null);
        gameTable.setFocusable(false);



        scrollPane = new JScrollPane(gameTable); // Prepare scroll pane

        // Add status panel
        add(statusPanel, BorderLayout.NORTH);
    }
    public void updatePacmanRenderState(int direction, int frame) {
        BoardCellRenderer renderer = (BoardCellRenderer) gameTable.getDefaultRenderer(Object.class);
        renderer.setPacmanDirection(direction);
        renderer.setPacmanFrame(frame);
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
        if (scrollPane.getParent() == this) {
            remove(scrollPane);
        }
        if (gameTable.getParent() == this) {
            remove(gameTable);
        }

        int cellSize = calculateCellSize();
        int boardSize = boardModel.getSize();

        gameTable.setRowHeight(cellSize);
        for (int i = 0; i < boardSize; i++) {
            gameTable.getColumnModel().getColumn(i).setPreferredWidth(cellSize);
        }

        if (cellSize < MIN_CELL_SIZE) {
            // Use scrollpane if cells would be too small
            add(scrollPane, BorderLayout.CENTER);
        } else {
            // Show table directly
            add(gameTable, BorderLayout.CENTER);
        }

        revalidate();
        repaint();
        requestFocusInWindow(); // ensure this component gets keyboard focus

    }

    private int calculateCellSize() {
        Dimension viewSize = getSize();
        int boardSize = boardModel.getSize();

        int widthPerCell = viewSize.width / boardSize;
        int heightPerCell = (viewSize.height - 50) / boardSize; // Leave space for status panel

        return Math.min(widthPerCell, heightPerCell);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        resizeBoard();
    }
}
