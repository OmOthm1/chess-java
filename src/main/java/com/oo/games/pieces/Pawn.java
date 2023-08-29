package com.oo.games.pieces;

import com.oo.games.ChessGame;
import com.oo.games.Player;
import com.oo.games.Square;
import com.oo.games.constants.PieceCode;

import java.util.HashSet;
import java.util.Set;

public class Pawn extends Piece {
    public Pawn(Player player, ChessGame game) {
        super(player, game);
    }

    @Override
    public void defineMovement() {
    }

    @Override
    public MoveType performMoveTo(Square newSquare) {
        boolean newSquareHadPiece = newSquare.hasPiece();

        MoveType moveType = super.performMoveTo(newSquare);
        if (
                moveType != null &&
                        (color() == Player.Color.WHITE && newSquare.y == 0 ||
                                color() == Player.Color.BLACK && newSquare.y == 7)
        ) {
            convertTo(new Queen(getPlayer(), game));
            game.lastMove.promotion = 'q';
        }

        // set En Passant target if the pawn moved 2 squares.
        int deltaY = getSquare().y - game.lastMove.source.y;
        if (Math.abs(deltaY) == 2) {
            game.enPassantTarget = getSquare().getRelative(0, deltaY * -1 / 2);
        }

        // check if the move was En Passant, if so remove the captured piece.
        int deltaX = game.lastMove.destination.x - game.lastMove.source.x;
        if (!newSquareHadPiece && Math.abs(deltaY) == 1 && Math.abs(deltaX) == 1) {
            getSquare().getRelative(0, -deltaY).getPiece().remove();
            moveType = MoveType.CAPTURE;
        }

        return moveType;
    }

    public void convertTo(Piece piece) {
        piece.setSquare(getSquare());
        getSquare().setPiece(piece);

        getPlayer().removePiece(this);
        getPlayer().addPiece(piece);
    }

    @Override
    public Set<Square> getCoveredSquares() {
        HashSet<Square> coveredSquares = new HashSet<>();
        int x = getSquare().x;
        int y = getSquare().y;
        int moveDirection = getMoveDirection();

        // --- captures
        Square topLeftSquare = game.board.getSquareAt(x + 1, y + moveDirection);
        if (topLeftSquare != null) {
            coveredSquares.add(topLeftSquare);
        }

        Square topRightSquare = game.board.getSquareAt(x - 1, y + moveDirection);
        if (topRightSquare != null) {
            coveredSquares.add(topRightSquare);
        }

        return coveredSquares;
    }

    private int getMoveDirection() {
        return color() == Player.Color.WHITE ? -1 : 1;
    }

    @Override
    public Set<Square> getAvailableSquares() {
        HashSet<Square> availableSquares = new HashSet<>();
        int movesAhead = 1;
        int moveDirection = getMoveDirection();

        if (color() == Player.Color.WHITE) {
            if (getSquare().y == 6) {
                movesAhead = 2;
            }
        } else {
            if (getSquare().y == 1) {
                movesAhead = 2;
            }
        }

        // -- forward squares
        Square nextSquare = getSquare();
        for (int i = 0; i < movesAhead; i++) {
            nextSquare = nextSquare.getRelative(0, moveDirection);
            if (nextSquare == null || nextSquare.hasPiece()) {
                break;
            }
            availableSquares.add(nextSquare);
        }

        // -- covered squares
        Set<Square> coveredSquares = getCoveredSquares();
        for (Square square : coveredSquares) {
            if (square.hasPiece() && square.getPiece().color() == opposingColor()) {
                availableSquares.add(square);
            } else if (square == game.enPassantTarget) { // -- en passant
                availableSquares.add(square);
            }
        }

        availableSquares.removeIf(square -> !isLegalMove(square));

        return availableSquares;
    }

    @Override
    public char getLetter() {
        return color() == Player.Color.WHITE ? PieceCode.WHITE_PAWN : PieceCode.BLACK_PAWN;
    }
}
