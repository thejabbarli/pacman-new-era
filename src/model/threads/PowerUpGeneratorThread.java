package model.threads;

import model.BoardModel;
import java.util.Random;

public class PowerUpGeneratorThread extends GameThread {
    private final BoardModel boardModel;
    private final Random random;
    private final int generationInterval; // Milliseconds between generation attempts
    private final int generationChance; // Percentage chance (0-100)

    public PowerUpGeneratorThread(BoardModel boardModel, int generationInterval, int generationChance) {
        super();
        this.boardModel = boardModel;
        this.random = new Random();
        this.generationInterval = generationInterval;
        this.generationChance = generationChance;
    }

    @Override
    protected void doAction() {
        try {
            // Sleep for the specified interval
            Thread.sleep(generationInterval);

            // Every 5 seconds, enemies have a 25% chance to create a powerup
            if (random.nextInt(100) < generationChance) {
                generatePowerUp();
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void generatePowerUp() {
        // Find a ghost position
        int[] ghostPosition = boardModel.findGhostPosition();

        if (ghostPosition != null) {
            int ghostRow = ghostPosition[0];
            int ghostCol = ghostPosition[1];

            // Try to place a powerup in one of the adjacent cells
            int[][] directions = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}};

            for (int[] dir : directions) {
                int newRow = ghostRow + dir[0];
                int newCol = ghostCol + dir[1];

                // Check if the position is valid and is a dot
                if (boardModel.isValidPosition(newRow, newCol) &&
                        boardModel.isDot(newRow, newCol)) {

                    // Place a random powerup type
                    int powerUpType = random.nextInt(5) + 6; // 6-10 for different powerup types
                    boardModel.setValueAt(powerUpType, newRow, newCol);

                    // Only place one powerup
                    break;
                }
            }
        }
    }
}