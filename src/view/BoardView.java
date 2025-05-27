package view;

import model.BoardModel;
import model.entity.Ghost;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.List;

public class BoardView extends JPanel {
    private final JTable gameTable;
    private final BoardModel boardModel;
    private final JLabel scoreLabel;
    private final JLabel timeLabel;
    private final JLabel livesLabel;
    private final JScrollPane scrollPane;
    private static final int MIN_CELL_SIZE = 10;

    public BoardView(BoardModel boardModel, java.util.List<Ghost> ghosts){
        this.boardModel = boardModel;

        setLayout(new BorderLayout());

        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        scoreLabel = new JLabel("Score: 0");
        timeLabel = new JLabel("Time: 0");
        livesLabel = new JLabel("Lives: 3");
        statusPanel.add(scoreLabel);
        statusPanel.add(timeLabel);
        statusPanel.add(livesLabel);

        BoardCellRenderer renderer = new BoardCellRenderer(ghosts);
        gameTable = new JTable(boardModel);
        gameTable.setDefaultRenderer(Object.class, renderer);
        gameTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        gameTable.setShowGrid(false);
        gameTable.setIntercellSpacing(new Dimension(0, 0));
        gameTable.setTableHeader(null);
        gameTable.setFocusable(false);

        scrollPane = new JScrollPane(gameTable);

        add(statusPanel, BorderLayout.NORTH);
    }

    public void updatePacmanRenderState(int direction, int frame) {
        DefaultTableCellRenderer renderer = (DefaultTableCellRenderer) gameTable.getDefaultRenderer(Object.class);
        if (renderer instanceof BoardCellRenderer br) {
            br.setPacmanDirection(direction);
            br.setPacmanFrame(frame);
        }
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

    public void resizeBoard() {
        if (scrollPane.getParent() == this) remove(scrollPane);
        if (gameTable.getParent() == this) remove(gameTable);

        int cellSize = calculateCellSize();
        int boardSize = boardModel.getSize();

        gameTable.setRowHeight(cellSize);
        for (int i = 0; i < boardSize; i++) {
            gameTable.getColumnModel().getColumn(i).setPreferredWidth(cellSize);
        }

        if (cellSize < MIN_CELL_SIZE) {
            add(scrollPane, BorderLayout.CENTER);
        } else {
            add(gameTable, BorderLayout.CENTER);
        }

        revalidate();
        repaint();
        requestFocusInWindow();
    }

    private int calculateCellSize() {
        Dimension viewSize = getSize();
        int boardSize = boardModel.getSize();
        int widthPerCell = viewSize.width / boardSize;
        int heightPerCell = (viewSize.height - 50) / boardSize;
        return Math.min(widthPerCell, heightPerCell);
    }
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        resizeBoard();
    }
}
