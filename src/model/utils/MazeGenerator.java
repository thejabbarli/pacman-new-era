package model.utils;

import java.util.Random;
import java.util.Stack;

public class MazeGenerator {
    private int[][] maze;
    private int width;
    private int height;
    private Random random;

    public MazeGenerator(int width, int height) {
        this.width = width;
        this.height = height;
        this.random = new Random();
        this.maze = new int[height][width];
    }

    public int[][] generate() {
        // Initialize maze with walls
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                maze[i][j] = 1; // 1 represents wall
            }
        }

        // Generate maze using depth-first search
        depthFirstSearch(1, 1);

        // Ensure there are paths of width 1
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                // Keep walls at the edges
                if (i == 0 || j == 0 || i == height - 1 || j == width - 1) {
                    maze[i][j] = 1;
                }
                // Convert empty spaces to dots
                else if (maze[i][j] == 0) {
                    maze[i][j] = 2; // 2 represents a dot
                }
            }
        }

        // Create a central area for ghost spawn point
        int centerY = height / 2;
        int centerX = width / 2;
        int spawnSize = Math.min(5, Math.min(width, height) / 10);

        for (int i = centerY - spawnSize / 2; i <= centerY + spawnSize / 2; i++) {
            for (int j = centerX - spawnSize / 2; j <= centerX + spawnSize / 2; j++) {
                if (i > 0 && j > 0 && i < height - 1 && j < width - 1) {
                    maze[i][j] = 0; // Empty for ghost spawn
                }
            }
        }

        // Set Pacman starting position
        maze[1][1] = 3; // 3 represents Pacman

        // Set ghost starting position
        maze[centerY][centerX] = 4; // 4 represents Ghost

        return maze;
    }

    private void depthFirstSearch(int x, int y) {
        // Mark current cell as path
        maze[y][x] = 0; // 0 represents a path

        // Define directions: up, right, down, left
        int[][] directions = {{0, -2}, {2, 0}, {0, 2}, {-2, 0}};

        // Shuffle directions for randomness
        shuffleDirections(directions);

        // Try each direction
        for (int[] dir : directions) {
            int nx = x + dir[0];
            int ny = y + dir[1];

            // Check if the new position is valid and still a wall
            if (nx > 0 && nx < width - 1 && ny > 0 && ny < height - 1 && maze[ny][nx] == 1) {
                // Create a path between current and next cell
                maze[y + dir[1]/2][x + dir[0]/2] = 0;

                // Continue from the new position
                depthFirstSearch(nx, ny);
            }
        }
    }

    private void shuffleDirections(int[][] directions) {
        for (int i = directions.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            int[] temp = directions[i];
            directions[i] = directions[j];
            directions[j] = temp;
        }
    }
}