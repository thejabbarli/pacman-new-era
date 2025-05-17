package model.utils;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;

public class ResourceManager {
    private static ResourceManager instance;
    private Map<String, Image> images;

    private ResourceManager() {
        images = new HashMap<>();
        loadImages();
    }

    public static ResourceManager getInstance() {
        if (instance == null) {
            instance = new ResourceManager();
        }
        return instance;
    }

    private void loadImages() {
        try {
            // Load images for game elements
            loadImage("wall", "res/tiles/wallBlue.png");
            loadImage("dot", "res/edibles/point-l.png");
            loadImage("powerup", "res/edibles/tileYemBig.png");

            // Load Pacman animation frames (only right-facing ones)
            loadImage("pacman_1", "res/pacman/pacman1.png");
            loadImage("pacman_2", "res/pacman/pacman2.png");
            loadImage("pacman_3", "res/pacman/pacman3.png");

            // Load Ghost images
            loadImage("ghost_red", "res/ghosts/enemyRed.png");
            loadImage("ghost_pink", "res/ghosts/enemyRed.png");
            loadImage("ghost_blue", "res/ghosts/enemyRed.png");
            loadImage("ghost_orange", "res/ghosts/enemyRed.png");

            // Load powerup images
            loadImage("powerup_speed", "res/boosts/boostThunder.png");
            loadImage("powerup_invulnerability", "res/boosts/boostThunder.png");
            loadImage("powerup_extralife", "res/boosts/boostThunder.png");
            loadImage("powerup_freeze", "res/boosts/boostThunder.png");
            loadImage("powerup_eat", "res/boosts/boostThunder.png");

            // Load menu background
            loadImage("menu_background", "res/ghosts/danczakSlawomir.png");

        } catch (IOException e) {
            System.err.println("Error loading images: " + e.getMessage());
        }
    }

    private void loadImage(String name, String path) throws IOException {
        try {
            // Try several approaches to load the image
            boolean loaded = false;

            // Approach 1: Direct file access
            File file = new File(path);
            if (file.exists()) {
                BufferedImage img = ImageIO.read(file);
                images.put(name, img);
                loaded = true;
            }

            // Approach 2: Class loader
            if (!loaded) {
                try {
                    java.net.URL url = getClass().getClassLoader().getResource(path);
                    if (url != null) {
                        BufferedImage img = ImageIO.read(url);
                        images.put(name, img);
                        loaded = true;
                    }
                } catch (Exception e) {
                    // Continue to next approach
                }
            }

            // Approach 3: Relative to project root
            if (!loaded) {
                try {
                    File projectRoot = new File(".");
                    File imageFile = new File(projectRoot, path);
                    if (imageFile.exists()) {
                        BufferedImage img = ImageIO.read(imageFile);
                        images.put(name, img);
                        loaded = true;
                    }
                } catch (Exception e) {
                    // Continue to next approach
                }
            }

            // If no approach worked, create a placeholder
            if (!loaded) {
                BufferedImage placeholder = createPlaceholderImage(name);
                images.put(name, placeholder);
                System.err.println("Warning: Image file not found: " + path);
            }
        } catch (IOException e) {
            // Create a placeholder colored rectangle if image loading fails
            BufferedImage placeholder = createPlaceholderImage(name);
            images.put(name, placeholder);
            throw e;
        }
    }

    private BufferedImage createPlaceholderImage(String name) {
        BufferedImage placeholder = new BufferedImage(20, 20, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = placeholder.createGraphics();

        // Choose color based on image name
        if (name.contains("wall")) {
            g.setColor(Color.BLUE);
            g.fillRect(0, 0, 20, 20);
        } else if (name.contains("pacman")) {
            g.setColor(Color.YELLOW);
            g.fillOval(0, 0, 20, 20);
        } else if (name.contains("ghost")) {
            if (name.contains("red")) {
                g.setColor(Color.RED);
            } else if (name.contains("pink")) {
                g.setColor(Color.PINK);
            } else if (name.contains("blue")) {
                g.setColor(Color.CYAN);
            } else if (name.contains("orange")) {
                g.setColor(Color.ORANGE);
            } else {
                g.setColor(Color.RED);
            }
            // Draw a ghost-like shape
            g.fillRect(0, 0, 20, 15);
            g.fillOval(0, 0, 20, 15);
        } else if (name.contains("dot")) {
            g.setColor(Color.WHITE);
            g.fillOval(8, 8, 4, 4);
        } else if (name.contains("powerup")) {
            // Different colors for different powerups
            if (name.contains("speed")) {
                g.setColor(Color.YELLOW);
            } else if (name.contains("invulnerability")) {
                g.setColor(Color.BLUE);
            } else if (name.contains("extralife")) {
                g.setColor(Color.RED);
            } else if (name.contains("freeze")) {
                g.setColor(Color.CYAN);
            } else if (name.contains("eat")) {
                g.setColor(Color.MAGENTA);
            } else {
                g.setColor(Color.GREEN);
            }
            g.fillOval(2, 2, 16, 16);
        } else if (name.contains("menu_background")) {
            // Create a simple gradient background
            GradientPaint gradient = new GradientPaint(
                    0, 0, Color.BLACK,
                    20, 20, new Color(0, 0, 100)
            );
            g.setPaint(gradient);
            g.fillRect(0, 0, 20, 20);
        } else {
            g.setColor(Color.GRAY);
            g.fillRect(0, 0, 20, 20);
        }

        g.dispose();
        return placeholder;
    }

    public Image getImage(String name) {
        return images.getOrDefault(name, createPlaceholderImage("default"));
    }

    public Image getScaledImage(String name, int width, int height) {
        Image original = getImage(name);
        return original.getScaledInstance(width, height, Image.SCALE_SMOOTH);
    }

    // Method to get a rotated copy of an image
    public Image getRotatedImage(String name, int degrees) {
        Image original = getImage(name);

        // Convert to BufferedImage if it's not already
        BufferedImage bufferedOriginal;
        if (original instanceof BufferedImage) {
            bufferedOriginal = (BufferedImage) original;
        } else {
            bufferedOriginal = new BufferedImage(
                    original.getWidth(null),
                    original.getHeight(null),
                    BufferedImage.TYPE_INT_ARGB
            );
            Graphics2D g = bufferedOriginal.createGraphics();
            g.drawImage(original, 0, 0, null);
            g.dispose();
        }

        double radians = Math.toRadians(degrees);
        AffineTransform transform = new AffineTransform();
        transform.rotate(radians, bufferedOriginal.getWidth() / 2.0, bufferedOriginal.getHeight() / 2.0);

        AffineTransformOp op = new AffineTransformOp(transform, AffineTransformOp.TYPE_BILINEAR);
        return op.filter(bufferedOriginal, null);
    }

    // Method to get a flipped copy of an image
    public Image getFlippedImage(String name, boolean horizontally, boolean vertically) {
        Image original = getImage(name);

        // Convert to BufferedImage if it's not already
        BufferedImage bufferedOriginal;
        if (original instanceof BufferedImage) {
            bufferedOriginal = (BufferedImage) original;
        } else {
            bufferedOriginal = new BufferedImage(
                    original.getWidth(null),
                    original.getHeight(null),
                    BufferedImage.TYPE_INT_ARGB
            );
            Graphics2D g = bufferedOriginal.createGraphics();
            g.drawImage(original, 0, 0, null);
            g.dispose();
        }

        // Create the transform
        AffineTransform transform = new AffineTransform();
        if (horizontally) {
            transform.concatenate(AffineTransform.getScaleInstance(-1, 1));
            transform.concatenate(AffineTransform.getTranslateInstance(-bufferedOriginal.getWidth(), 0));
        }
        if (vertically) {
            transform.concatenate(AffineTransform.getScaleInstance(1, -1));
            transform.concatenate(AffineTransform.getTranslateInstance(0, -bufferedOriginal.getHeight()));
        }

        AffineTransformOp op = new AffineTransformOp(transform, AffineTransformOp.TYPE_BILINEAR);
        return op.filter(bufferedOriginal, null);
    }

    // Get a Pacman image based on direction and animation frame
    public Image getPacmanImage(int direction, int frame) {
        String baseName = "pacman_" + (frame % 3 + 1);
        Image baseImage = getImage(baseName);

        // 0: right (default), 1: down, 2: left, 3: up
        switch (direction) {
            case 0: // right - use the original image
                return baseImage;
            case 1: // down - rotate right image 90 degrees clockwise
                return getRotatedImage(baseName, 90);
            case 2: // left - flip the right image horizontally
                return getFlippedImage(baseName, true, false);
            case 3: // up - rotate right image 270 degrees clockwise
                return getRotatedImage(baseName, 270);
            default:
                return baseImage;
        }
    }

    // Get a scaled Pacman image based on direction and animation frame
    public Image getScaledPacmanImage(int direction, int frame, int width, int height) {
        Image pacmanImage = getPacmanImage(direction, frame);
        return pacmanImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
    }
}