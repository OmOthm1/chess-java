package com.oo.games;

import com.oo.games.pieces.Piece;

public class Square {
    private Piece piece;
    public int x;
    public int y;
    public Board.Pos pos;
    Board board;

    public Square(Board board, int x, int y) {
        this.board = board;
        this.x = x;
        this.y = y;
        pos = new Board.Pos(x, y);
    }

    public Square getClone(Board cloneBoard) {
        return new Square(cloneBoard, x, y);
    }

    public Square getRelative(int deltaX, int deltaY) {
        return board.getSquareAt(x + deltaX, y + deltaY);
    }

    public boolean hasPiece() {
        return piece != null;
    }

    public Piece getPiece() {
        return piece;
    }

    public void setPiece(Piece piece) {
        this.piece = piece;

        if (piece != null) {
            piece.setSquare(this);
        }
    }

    public void removePiece() {
        this.piece = null;
    }

    public int getViewX() {
        return board.game.viewFlipped ? 7 - x : x;
    }

    public int getViewY() {
        return board.game.viewFlipped ? 7 - y : y;
    }

    @Override
    public String toString() {
        if (piece == null) {
            return " - ";
        } else
            return " " + piece.getLetter() + " ";
    }
}
