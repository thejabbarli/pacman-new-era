package model.utils;

import java.util.Random;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

public class MazeGenerator {
    private int[][] maze;
    private int width;
    private int height;
    private Random random;

    // Cell types
    public static final int WALL = 1;
    public static final int PATH = 0;
    public static final int DOT = 2;
    public static final int PACMAN = 3;
    public static final int GHOST = 4;
    public static final int POWERUP = 5;

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
                maze[i][j] = WALL;
            }
        }

        // Generate maze using recursive backtracking
        recursiveBacktracking();

        // Add more paths to make the maze less dense
        addMorePaths();

        // Add dots to empty spaces
        addDots();

        // Add power pellets
        addPowerPellets();

        // Create ghost spawn point with proper exit
        createGhostSpawnWithExit();

        // Set Pacman starting position
        setStartingPosition();

        return maze;
    }

    private void recursiveBacktracking() {
        // Start at 1,1 to keep the border walls intact
        Stack<int[]> stack = new Stack<>();
        int[] start = {1, 1};
        stack.push(start);

        // Mark the starting cell as a path
        maze[start[0]][start[1]] = PATH;

        // Define possible directions: up, right, down, left
        int[][] directions = {{-2, 0}, {0, 2}, {2, 0}, {0, -2}};

        while (!stack.isEmpty()) {
            int[] current = stack.peek();
            int currentY = current[0];
            int currentX = current[1];

            // Get unvisited neighbors
            List<int[]> unvisitedNeighbors = new ArrayList<>();

            for (int[] dir : directions) {
                int newY = currentY + dir[0];
                int newX = currentX + dir[1];

                // Check if the new position is valid and still a wall
                if (newY > 0 && newY < height - 1 && newX > 0 && newX < width - 1 && maze[newY][newX] == WALL) {
                    unvisitedNeighbors.add(new int[]{newY, newX});
                }
            }

            if (!unvisitedNeighbors.isEmpty()) {
                // Choose a random unvisited neighbor
                int[] next = unvisitedNeighbors.get(random.nextInt(unvisitedNeighbors.size()));
                int nextY = next[0];
                int nextX = next[1];

                // Remove the wall between current and next
                maze[currentY + (nextY - currentY) / 2][currentX + (nextX - currentX) / 2] = PATH;

                // Mark the next cell as a path
                maze[nextY][nextX] = PATH;

                // Push the next cell to the stack
                stack.push(next);
            } else {
                // Backtrack
                stack.pop();
            }
        }
    }

    private void addMorePaths() {
        // Add more paths to make the maze less dense
        int pathsToAdd = (width * height) / 8; // 12.5% of the total cells

        for (int i = 0; i < pathsToAdd; i++) {
            int y = random.nextInt(height - 2) + 1;
            int x = random.nextInt(width - 2) + 1;

            // Only replace walls, not existing paths
            if (maze[y][x] == WALL) {
                // Convert this wall to a path
                maze[y][x] = PATH;
            }
        }

        // Add some horizontal and vertical passages to improve gameplay
        // Horizontal passages
        for (int y = height / 4; y < height; y += height / 3) {
            for (int x = 1; x < width - 1; x++) {
                if (random.nextInt(100) < 70) { // 70% chance
                    maze[y][x] = PATH;
                }
            }
        }

        // Vertical passages
        for (int x = width / 4; x < width; x += width / 3) {
            for (int y = 1; y < height - 1; y++) {
                if (random.nextInt(100) < 70) { // 70% chance
                    maze[y][x] = PATH;
                }
            }
        }
    }

    private void addDots() {
        // Add dots to all empty spaces
        for (int y = 1; y < height - 1; y++) {
            for (int x = 1; x < width - 1; x++) {
                if (maze[y][x] == PATH) {
                    maze[y][x] = DOT;
                }
            }
        }
    }

    private void addPowerPellets() {
        // Add power pellets at strategic locations
        int pelletsToAdd = Math.max(4, width / 20); // At least 4 power pellets

        // Add power pellets near the corners but not at the very edge
        if (maze[2][2] == DOT) maze[2][2] = POWERUP;
        if (maze[2][width - 3] == DOT) maze[2][width - 3] = POWERUP;
        if (maze[height - 3][2] == DOT) maze[height - 3][2] = POWERUP;
        if (maze[height - 3][width - 3] == DOT) maze[height - 3][width - 3] = POWERUP;

        // Add additional power pellets for larger mazes
        for (int i = 4; i < pelletsToAdd; i++) {
            int attempts = 0;
            boolean placed = false;

            while (!placed && attempts < 100) {
                int y = random.nextInt(height - 4) + 2;
                int x = random.nextInt(width - 4) + 2;

                if (maze[y][x] == DOT) {
                    // Check if it's far enough from other power pellets
                    boolean tooClose = false;
                    for (int cy = 0; cy < height; cy++) {
                        for (int cx = 0; cx < width; cx++) {
                            if (maze[cy][cx] == POWERUP) {
                                int distance = Math.abs(cy - y) + Math.abs(cx - x);
                                if (distance < Math.min(width, height) / 4) {
                                    tooClose = true;
                                    break;
                                }
                            }
                        }
                        if (tooClose) break;
                    }

                    if (!tooClose) {
                        maze[y][x] = POWERUP;
                        placed = true;
                    }
                }

                attempts++;
            }
        }
    }

    private void createGhostSpawnWithExit() {
        // Create a central area for ghost spawn
        int centerY = height / 2;
        int centerX = width / 2;
        int spawnSize = 3; // Small, fixed size for ghost box

        // Create a small box for ghost spawn
        for (int y = centerY - spawnSize / 2; y <= centerY + spawnSize / 2; y++) {
            for (int x = centerX - spawnSize / 2; x <= centerX + spawnSize / 2; x++) {
                if (y > 0 && y < height - 1 && x > 0 && x < width - 1) {
                    maze[y][x] = PATH; // Make all cells paths first
                }
            }
        }

        // Add ghost to the center
        maze[centerY][centerX] = GHOST;

        // Ensure there's a path out in all four directions
        // These paths should connect to the main maze
        // Top exit
        for (int y = centerY - spawnSize / 2; y >= 1; y--) {
            maze[y][centerX] = DOT;
        }

        // Bottom exit
        for (int y = centerY + spawnSize / 2; y < height - 1; y++) {
            maze[y][centerX] = DOT;
        }

        // Left exit
        for (int x = centerX - spawnSize / 2; x >= 1; x--) {
            maze[centerY][x] = DOT;
        }

        // Right exit
        for (int x = centerX + spawnSize / 2; x < width - 1; x++) {
            maze[centerY][x] = DOT;
        }
    }

    private void setStartingPosition() {
        // Find a good starting position for Pacman (away from ghosts)
        int pacmanY = -1;
        int pacmanX = -1;
        int ghostY = -1;
        int ghostX = -1;

        // Find ghost position
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (maze[y][x] == GHOST) {
                    ghostY = y;
                    ghostX = x;
                    break;
                }
            }
            if (ghostY >= 0) break;
        }

        // Place Pacman far from the ghost
        int maxDistance = 0;
        for (int y = 1; y < height - 1; y++) {
            for (int x = 1; x < width - 1; x++) {
                if (maze[y][x] == DOT || maze[y][x] == POWERUP) {
                    int distance = Math.abs(y - ghostY) + Math.abs(x - ghostX);
                    if (distance > maxDistance) {
                        maxDistance = distance;
                        pacmanY = y;
                        pacmanX = x;
                    }
                }
            }
        }

        // Place Pacman at the chosen position
        if (pacmanY >= 0 && pacmanX >= 0) {
            maze[pacmanY][pacmanX] = PACMAN;
        } else {
            // Fallback to a default position
            maze[1][1] = PACMAN;
        }
    }

    // Method to check if there are 2-thickness walls and break them
    private void breakThickWalls() {
        boolean foundThickWall;
        do {
            foundThickWall = false;

            // Check horizontal thick walls
            for (int y = 1; y < height - 1; y++) {
                for (int x = 1; x < width - 2; x++) {
                    if (maze[y][x] == WALL && maze[y][x+1] == WALL) {
                        // Found potential thick wall, check further
                        int count = 0;
                        while (x+count < width-1 && maze[y][x+count] == WALL) {
                            count++;
                        }

                        if (count > 1) {
                            // Break the thick wall by making alternating cells paths
                            for (int i = 1; i < count; i += 2) {
                                maze[y][x+i] = PATH;
                            }
                            foundThickWall = true;
                        }
                    }
                }
            }

            // Check vertical thick walls
            for (int x = 1; x < width - 1; x++) {
                for (int y = 1; y < height - 2; y++) {
                    if (maze[y][x] == WALL && maze[y+1][x] == WALL) {
                        // Found potential thick wall, check further
                        int count = 0;
                        while (y+count < height-1 && maze[y+count][x] == WALL) {
                            count++;
                        }

                        if (count > 1) {
                            // Break the thick wall by making alternating cells paths
                            for (int i = 1; i < count; i += 2) {
                                maze[y+i][x] = PATH;
                            }
                            foundThickWall = true;
                        }
                    }
                }
            }
        } while (foundThickWall);
    }
}