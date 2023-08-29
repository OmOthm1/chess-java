package com.oo.games.pieces;

import com.oo.games.*;
import com.oo.games.movement.Movement;

import java.util.Set;

public abstract class Piece {
    private final Player player;
    private Square square;
    protected Movement movement;
    protected ChessGame game;

    protected Piece(Player player, ChessGame game) {
        this.player = player;
        this.game = game;
        this.player.addPiece(this);
        defineMovement();
    }

    /**
     * @param newSquare square where the piece is moving.
     * @return false if the King will be in check after the move, true otherwise.
     */
    public boolean isLegalMove(Square newSquare) {
        ChessGame shadowGame = game.clone();
        Board shadowBoard = shadowGame.board;
        Piece shadowSourcePiece = shadowBoard.getSquareAt(square.x, square.y).getPiece();
        Square shadowDistinationSquare = shadowBoard.getSquareAt(newSquare.x, newSquare.y);

        // shadow move
        shadowSourcePiece.performMoveTo(shadowDistinationSquare);

        return !shadowSourcePiece.player.inCheck();
    }

    public MoveType moveTo(Square newSquare) {
        return performMoveTo(newSquare);
    }

    public MoveType performMoveTo(Square newSquare) {
        Square sourceSquare = getSquare();
        MoveType moveType;

        this.square.removePiece();
        if (newSquare.hasPiece()) {
            newSquare.getPiece().remove();
            moveType = MoveType.CAPTURE;
        } else {
            moveType = MoveType.MOVE;
        }

        newSquare.setPiece(this);
        game.enPassantTarget = null;
        game.lastMove = new Move(sourceSquare, newSquare);

        return moveType;
    }

    public enum MoveType {
        MOVE, CAPTURE
    }

    public void remove() {
        player.removePiece(this);
        square.setPiece(null);
    }

    protected abstract void defineMovement();

    public Set<Square> getAvailableSquares() {
        return movement.getAvailableSquares();
    }

    public Set<Square> getCoveredSquares() {
        return movement.getCoveredSquares();
    }

    public abstract char getLetter();
    public char getUpperCaseLetter() {
        return Character.toUpperCase(getLetter());
    }

    public Player.Color color() {
        return player.playerColor;
    }

    public Player.Color opposingColor() {
        return player.playerColor == Player.Color.WHITE ? Player.Color.BLACK : Player.Color.WHITE;
    }

    public boolean isRock() {
        return this instanceof Rock;
    }

    public boolean isSameColor(Piece piece) {
        return color() == piece.color();
    }

    public Player getPlayer() {
        return player;
    }

    public Square getSquare() {
        return square;
    }

    public void setSquare(Square square) {
        this.square = square;
    }
}
