package com.oo.games;

import com.oo.games.gui.ChessGamePanel;

import javax.swing.*;
import java.awt.*;

public class Main {
    public static final String WINDOW_TITLE = "Chess Game by Omar Othman";

    public static void main(String[] args) {
        ChessGame game = new ChessGame();

        System.out.println(game.board);

        JFrame frame = new JFrame();
        frame.setTitle(WINDOW_TITLE);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        frame.setLayout(new FlowLayout());
        frame.add(new ChessGamePanel(game), BorderLayout.CENTER);

        frame.pack();
        frame.setVisible(true);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
    }
}

