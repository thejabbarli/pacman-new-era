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
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                maze[i][j] = WALL;
            }
        }
        recursiveBacktracking();
        addMorePaths();
        addDots();
        addPowerPellets();
        createGhostSpawnWithExit();
        setStartingPosition();
        return maze;
    }

    private void recursiveBacktracking() {
        Stack<int[]> stack = new Stack<>();
        int[] start = {1, 1};
        stack.push(start);
        maze[start[0]][start[1]] = PATH;
        int[][] directions = {{-2, 0}, {0, 2}, {2, 0}, {0, -2}};
        while (!stack.isEmpty()) {
            int[] current = stack.peek();
            int currentY = current[0];
            int currentX = current[1];
            List<int[]> unvisitedNeighbors = new ArrayList<>();
            for (int[] dir : directions) {
                int newY = currentY + dir[0];
                int newX = currentX + dir[1];
                if (newY > 0 && newY < height - 1 && newX > 0 && newX < width - 1 && maze[newY][newX] == WALL) {
                    unvisitedNeighbors.add(new int[]{newY, newX});
                }
            }
            if (!unvisitedNeighbors.isEmpty()) {
                int[] next = unvisitedNeighbors.get(random.nextInt(unvisitedNeighbors.size()));
                int nextY = next[0];
                int nextX = next[1];
                maze[currentY + (nextY - currentY) / 2][currentX + (nextX - currentX) / 2] = PATH;
                maze[nextY][nextX] = PATH;
                stack.push(next);
            } else {
                stack.pop();
            }
        }
    }

    private void addMorePaths() {
        int pathsToAdd = (width * height) / 8;
        for (int i = 0; i < pathsToAdd; i++) {
            int y = random.nextInt(height - 2) + 1;
            int x = random.nextInt(width - 2) + 1;
            if (maze[y][x] == WALL) {
                maze[y][x] = PATH;
            }
        }
        for (int y = height / 4; y < height; y += height / 3) {
            for (int x = 1; x < width - 1; x++) {
                if (random.nextInt(100) < 70) {
                    maze[y][x] = PATH;
                }
            }
        }
        for (int x = width / 4; x < width; x += width / 3) {
            for (int y = 1; y < height - 1; y++) {
                if (random.nextInt(100) < 70) {
                    maze[y][x] = PATH;
                }
            }
        }
    }

    private void addDots() {
        for (int y = 1; y < height - 1; y++) {
            for (int x = 1; x < width - 1; x++) {
                if (maze[y][x] == PATH) {
                    maze[y][x] = DOT;
                }
            }
        }
    }

    private void addPowerPellets() {
        int pelletsToAdd = Math.max(4, width / 20);
        if (maze[2][2] == DOT) maze[2][2] = POWERUP;
        if (maze[2][width - 3] == DOT) maze[2][width - 3] = POWERUP;
        if (maze[height - 3][2] == DOT) maze[height - 3][2] = POWERUP;
        if (maze[height - 3][width - 3] == DOT) maze[height - 3][width - 3] = POWERUP;
        for (int i = 4; i < pelletsToAdd; i++) {
            int attempts = 0;
            boolean placed = false;
            while (!placed && attempts < 100) {
                int y = random.nextInt(height - 4) + 2;
                int x = random.nextInt(width - 4) + 2;
                if (maze[y][x] == DOT) {
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
        int centerY = height / 2;
        int centerX = width / 2;
        int spawnSize = 3;
        for (int y = centerY - spawnSize / 2; y <= centerY + spawnSize / 2; y++) {
            for (int x = centerX - spawnSize / 2; x <= centerX + spawnSize / 2; x++) {
                if (y > 0 && y < height - 1 && x > 0 && x < width - 1) {
                    maze[y][x] = PATH;
                }
            }
        }
        maze[centerY][centerX] = GHOST;
        for (int y = centerY - spawnSize / 2; y >= 1; y--) {
            maze[y][centerX] = DOT;
        }
        for (int y = centerY + spawnSize / 2; y < height - 1; y++) {
            maze[y][centerX] = DOT;
        }
        for (int x = centerX - spawnSize / 2; x >= 1; x--) {
            maze[centerY][x] = DOT;
        }
        for (int x = centerX + spawnSize / 2; x < width - 1; x++) {
            maze[centerY][x] = DOT;
        }
    }

    private void setStartingPosition() {
        int pacmanY = -1;
        int pacmanX = -1;
        int ghostY = -1;
        int ghostX = -1;
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
        if (pacmanY >= 0 && pacmanX >= 0) {
            maze[pacmanY][pacmanX] = PACMAN;
        } else {
            maze[1][1] = PACMAN;
        }
    }

    private void breakThickWalls() {
        boolean foundThickWall;
        do {
            foundThickWall = false;
            for (int y = 1; y < height - 1; y++) {
                for (int x = 1; x < width - 2; x++) {
                    if (maze[y][x] == WALL && maze[y][x + 1] == WALL) {
                        int count = 0;
                        while (x + count < width - 1 && maze[y][x + count] == WALL) {
                            count++;
                        }
                        if (count > 1) {
                            for (int i = 1; i < count; i += 2) {
                                maze[y][x + i] = PATH;
                            }
                            foundThickWall = true;
                        }
                    }
                }
            }
            for (int x = 1; x < width - 1; x++) {
                for (int y = 1; y < height - 2; y++) {
                    if (maze[y][x] == WALL && maze[y + 1][x] == WALL) {
                        int count = 0;
                        while (y + count < height - 1 && maze[y + count][x] == WALL) {
                            count++;
                        }
                        if (count > 1) {
                            for (int i = 1; i < count; i += 2) {
                                maze[y + i][x] = PATH;
                            }
                            foundThickWall = true;
                        }
                    }
                }
            }
        } while (foundThickWall);
    }
}
