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
            loadImage("wall", "res/tiles/wallBlue.png");
            loadImage("dot", "res/edibles/point-l.png");
            loadImage("powerup", "res/edibles/tileYemBig.png");
            loadImage("pacman_1", "res/pacman/pacman1.png");
            loadImage("pacman_2", "res/pacman/pacman2.png");
            loadImage("pacman_3", "res/pacman/pacman3.png");
            loadImage("enemy_red_1", "res/ghosts/enemy_red_1.png");
            loadImage("enemy_red_2", "res/ghosts/enemy_red_2.png");
            loadImage("enemy_blue_1", "res/ghosts/enemy_blue_1.png");
            loadImage("enemy_blue_2", "res/ghosts/enemy_blue_2.png");
            loadImage("enemy_green_1", "res/ghosts/enemy_green_1.png");
            loadImage("enemy_green_2", "res/ghosts/enemy_green_2.png");
            loadImage("enemy_yellow_1", "res/ghosts/enemy_yellow_1.png");
            loadImage("enemy_yellow_2", "res/ghosts/enemy_yellow_2.png");
            loadImage("boostHealth", "res/boosts/boostHealth.png");
            loadImage("boostThunder", "res/boosts/boostThunder.png");
            loadImage("boostIce", "res/boosts/boostIce.png");
            loadImage("boostPoison", "res/boosts/boostPoison.png");
            loadImage("boostShield", "res/boosts/boostShield.png");
            loadImage("menu_background", "res/ghosts/danczakSlawomir.png");
        } catch (IOException e) {
            System.err.println("Error loading images: " + e.getMessage());
        }
    }

    private void loadImage(String imageKey, String imagePath) throws IOException {
        boolean loaded = false;

        File file = new File(imagePath);
        if (file.exists()) {
            images.put(imageKey, ImageIO.read(file));
            loaded = true;
        }

        if (!loaded) {
            try {
                var url = getClass().getClassLoader().getResource(imagePath);
                if (url != null) {
                    images.put(imageKey, ImageIO.read(url));
                    loaded = true;
                }
            } catch (Exception e) {
            }
        }

        if (!loaded) {
            File imageFile = new File(".", imagePath);
            if (imageFile.exists()) {
                images.put(imageKey, ImageIO.read(imageFile));
                loaded = true;
            }
        }

        if (!loaded) {
            System.err.println("[WARN] Failed to load image: " + imagePath + " — using placeholder.");
            images.put(imageKey, createPlaceholderImage(imageKey));
        }
    }

    private BufferedImage createPlaceholderImage(String name) {
        BufferedImage placeholder = new BufferedImage(20, 20, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = placeholder.createGraphics();

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
            g.fillRect(0, 0, 20, 15);
            g.fillOval(0, 0, 20, 15);
        } else if (name.contains("dot")) {
            g.setColor(Color.WHITE);
            g.fillOval(8, 8, 4, 4);
        } else if (name.contains("powerup")) {
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

    public Image getRotatedImage(String name, int degrees) {
        Image original = getImage(name);

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

    public Image getFlippedImage(String name, boolean horizontally, boolean vertically) {
        Image original = getImage(name);

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

    public Image getPacmanImage(int direction, int frame) {
        String baseName = "pacman_" + (frame % 3 + 1);
        Image baseImage = getImage(baseName);

        switch (direction) {
            case 0:
                return baseImage;
            case 1:
                return getRotatedImage(baseName, 90);
            case 2:
                return getFlippedImage(baseName, true, false);
            case 3:
                return getRotatedImage(baseName, 270);
            default:
                return baseImage;
        }
    }

    public Image getScaledPacmanImage(int direction, int frame, int width, int height) {
        Image pacmanImage = getPacmanImage(direction, frame);
        return pacmanImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
    }

    public ImageIcon getScaledBoostIcon(String name, int size) {
        Image img = getImage(name);
        return new ImageIcon(img.getScaledInstance(size, size, Image.SCALE_SMOOTH));
    }
}
