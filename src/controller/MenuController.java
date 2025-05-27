package controller;

import view.MainMenuView;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MenuController {
    private GameController gameController;
    private MainMenuView menuView;

    public MenuController(GameController gameController, MainMenuView menuView) {
        this.gameController = gameController;
        this.menuView = menuView;

        this.menuView.addNewGameListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showBoardSizeDialog();
            }
        });

        this.menuView.addHighScoresListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gameController.showHighScores();
            }
        });

        this.menuView.addExitListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gameController.exit();
            }
        });
    }

    private void showBoardSizeDialog() {
        int boardSize = menuView.showBoardSizeDialog();
        if (boardSize >= 10 && boardSize <= 100) {
            gameController.startNewGame(boardSize);
        }
    }
}
