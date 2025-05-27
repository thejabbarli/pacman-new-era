package view;

import model.BoardModel;
import model.entity.Ghost;
import model.utils.ResourceManager;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.List;

public class BoardCellRenderer extends DefaultTableCellRenderer {
    private final ResourceManager resourceManager;
    private final List<Ghost> ghosts;
    private int pacmanDirection = 0;
    private int pacmanFrame = 0;

    public BoardCellRenderer(List<Ghost> ghosts) {
        super();
        this.ghosts = ghosts;
        this.resourceManager = ResourceManager.getInstance();
        setHorizontalAlignment(JLabel.CENTER);
    }

    public void setPacmanDirection(int direction) {
        this.pacmanDirection = direction;
    }

    public void setPacmanFrame(int frame) {
        this.pacmanFrame = frame;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus,
                                                   int row, int column) {
        JLabel cell = (JLabel) super.getTableCellRendererComponent(table, "", isSelected, hasFocus, row, column);
        cell.setBackground(Color.BLACK);
        cell.setForeground(Color.WHITE);
        cell.setText("");
        cell.setIcon(null); // Clear previous icon

        int cellWidth = table.getColumnModel().getColumn(column).getWidth();
        int cellHeight = table.getRowHeight(row);

        if (value instanceof Integer) {
            int cellType = (Integer) value;

            switch (cellType) {
                case BoardModel.EMPTY -> {
                    // nothing
                }

                case BoardModel.WALL -> {
                    cell.setIcon(new ImageIcon(
                            resourceManager.getScaledImage("wall", cellWidth, cellHeight)));
                }

                case BoardModel.DOT -> {
                    cell.setIcon(new ImageIcon(
                            resourceManager.getScaledImage("dot", cellWidth, cellHeight)));
                }

                case BoardModel.PACMAN -> {
                    cell.setIcon(new ImageIcon(
                            resourceManager.getScaledPacmanImage(pacmanDirection, pacmanFrame, cellWidth, cellHeight)));
                }

                case BoardModel.GHOST -> {
                    Ghost ghost = getGhostAt(row, column);
                    if (ghost != null) {
                        String logicalType = ghost.getType();
                        int frame = ghost.getAnimationFrame();

                        // Map logical ghost type to color-based image prefix
                        String colorType = switch (logicalType.toLowerCase()) {
                            case "blinky" -> "red";
                            case "inky" -> "blue";
                            case "pinky" -> "yellow";
                            case "clyde" -> "green";
                            default -> {
                                System.err.println("[ERROR] Unknown ghost type: " + logicalType);
                                yield "red";
                            }
                        };

                        String imageKey = "enemy_" + colorType + "_" + frame;

                        // Debugging output
                        System.out.println("[DEBUG] Rendering ghost at [" + row + "," + column + "]");
                        System.out.println("[DEBUG] Ghost type: " + logicalType + " mapped to color: " + colorType);
                        System.out.println("[DEBUG] Animation frame: " + frame);
                        System.out.println("[DEBUG] Final image key: " + imageKey);

                        Image scaledImage = resourceManager.getScaledImage(imageKey, cellWidth, cellHeight);
                        if (scaledImage == null) {
                            System.err.println("[ERROR] Scaled image is null for key: " + imageKey);
                        }

                        cell.setIcon(new ImageIcon(scaledImage));
                    } else {
                        System.err.println("[ERROR] No ghost found at [" + row + "," + column + "]");
                    }
                }

                case BoardModel.BOOST_HEALTH -> {
                    cell.setIcon(resourceManager.getScaledBoostIcon("boostHealth", cellWidth));
                }

                case BoardModel.BOOST_THUNDER -> {
                    cell.setIcon(resourceManager.getScaledBoostIcon("boostThunder", cellWidth));
                }

                case BoardModel.BOOST_ICE -> {
                    cell.setIcon(resourceManager.getScaledBoostIcon("boostIce", cellWidth));
                }

                case BoardModel.BOOST_POISON -> {
                    cell.setIcon(resourceManager.getScaledBoostIcon("boostPoison", cellWidth));
                }

                case BoardModel.BOOST_SHIELD -> {
                    cell.setIcon(resourceManager.getScaledBoostIcon("boostShield", cellWidth));
                }

                default -> {
                    System.err.println("[WARN] Unknown tile type at [" + row + "," + column + "]: " + cellType);
                    cell.setIcon(null);
                }
            }

        }

        return cell;
    }

    private Ghost getGhostAt(int row, int col) {
        for (Ghost ghost : ghosts) {
            if (ghost.getX() == col && ghost.getY() == row) {
                return ghost;
            }
        }
        return null;
    }
}
