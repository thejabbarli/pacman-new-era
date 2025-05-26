package view;

import model.BoardModel;
import model.utils.ResourceManager;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class BoardCellRenderer extends DefaultTableCellRenderer {
    private ResourceManager resourceManager;
    private int pacmanDirection = 0; // 0: right, 1: down, 2: left, 3: up
    private int pacmanFrame = 0;

    public BoardCellRenderer() {
        super();
        setHorizontalAlignment(JLabel.CENTER);
        resourceManager = ResourceManager.getInstance();
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
        cell.setIcon(null); // ✅ Always clear icon first to prevent ghosting

        int cellWidth = table.getColumnModel().getColumn(column).getWidth();
        int cellHeight = table.getRowHeight(row);

        if (value instanceof Integer) {
            int cellType = (Integer) value;

            switch (cellType) {
                case BoardModel.EMPTY:
                    break;

                case BoardModel.WALL:
                    cell.setIcon(new ImageIcon(
                            resourceManager.getScaledImage("wall", cellWidth, cellHeight)));
                    break;

                case BoardModel.DOT:
                    cell.setIcon(new ImageIcon(
                            resourceManager.getScaledImage("dot", cellWidth, cellHeight)));
                    break;

                case BoardModel.PACMAN:
                    cell.setIcon(new ImageIcon(
                            resourceManager.getScaledPacmanImage(pacmanDirection, pacmanFrame, cellWidth, cellHeight)));
                    break;

                case BoardModel.GHOST:
                    cell.setIcon(new ImageIcon(
                            resourceManager.getScaledImage("ghost_red", cellWidth, cellHeight)));
                    break;

                case BoardModel.BOOST_HEALTH:
                    cell.setIcon(resourceManager.getScaledBoostIcon("boostHealth", cellWidth));
                    break;

                case BoardModel.BOOST_THUNDER:
                    cell.setIcon(resourceManager.getScaledBoostIcon("boostThunder", cellWidth));
                    break;

                case BoardModel.BOOST_ICE:
                    cell.setIcon(resourceManager.getScaledBoostIcon("boostIce", cellWidth));
                    break;

                case BoardModel.BOOST_POISON:
                    cell.setIcon(resourceManager.getScaledBoostIcon("boostPoison", cellWidth));
                    break;

                case BoardModel.BOOST_SHIELD:
                    cell.setIcon(resourceManager.getScaledBoostIcon("boostShield", cellWidth));
                    break;

                default:
                    cell.setIcon(null);
                    break;
            }
        }

        return cell;
    }



}