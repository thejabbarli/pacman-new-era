package model.threads;

import model.BoardModel;
import model.entity.Ghost;

import java.util.List;
import java.util.Random;

public class PowerUpGeneratorThread extends GameThread {
    private final BoardModel boardModel;
    private final List<Ghost> ghosts;
    private final Random random;
    private final int generationInterval;
    private final int generationChance;

    public PowerUpGeneratorThread(BoardModel boardModel, List<Ghost> ghosts, int generationInterval, int generationChance) {
        super();
        this.boardModel = boardModel;
        this.ghosts = ghosts;
        this.random = new Random();
        this.generationInterval = generationInterval;
        this.generationChance = generationChance;
    }

    @Override
    protected void doAction() {
        try {
            Thread.sleep(generationInterval);
            for (Ghost ghost : ghosts) {
                if (random.nextInt(100) < generationChance) {
                    int row = ghost.getY();
                    int col = ghost.getX();


                    int[][] directions = {
                            {0, 1}, {1, 0}, {0, -1}, {-1, 0}
                    };

                    for (int[] dir : directions) {
                        int newRow = row + dir[0];
                        int newCol = col + dir[1];

                        if (!boardModel.isValid(newRow, newCol)) continue;

                        int current = (int) boardModel.getValueAt(newRow, newCol);
                        boolean isFree = current == BoardModel.EMPTY || current == BoardModel.DOT;
                        boolean isNotBoost = current < BoardModel.BOOST_HEALTH || current > BoardModel.BOOST_SHIELD;

                        if (isFree && isNotBoost) {
                            int boostType = random.nextInt(5) + 6; // 6 to 10
                            boardModel.setValueAt(boostType, newRow, newCol);
                            break;
                        }
                    }

                }
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
