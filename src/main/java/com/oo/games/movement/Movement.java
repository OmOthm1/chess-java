package com.oo.games.movement;

import com.oo.games.Square;
import com.oo.games.pieces.Piece;

import java.util.*;

public class Movement {
    int distance;
    Piece piece;
    ArrayList<MovesModel> movesModels;

    Movement(Piece piece, int distance, MovesModel... movesModels) {
        this.piece = piece;
        this.movesModels = new ArrayList<>(Arrays.asList(movesModels));
        this.distance = distance;
    }

    Movement(Piece piece, MovesModel... movesModels) {
        this(piece, 8, movesModels);
    }


    private boolean isWithinDistance(int x, int y) {
        return (Math.abs(piece.getSquare().y - y) <= distance) && (Math.abs(piece.getSquare().x - x) <= distance);
    }

    private boolean isWithinDistance(Square square) {
        return isWithinDistance(square.x, square.y);
    }

    public Set<Square> getAvailableSquares() {
        Set<Square> set = getCoveredSquares();

        // clear moves that result in a position where the King is in check.
        set.removeIf(square -> !piece.isLegalMove(square));

        return set;
    }

    public Set<Square> getCoveredSquares() {
        HashSet<Square> set = new HashSet<>();

        for (MovesModel model : movesModels) {
            Square square = piece.getSquare().getRelative(model.deltaX, model.deltaY);

            while (square != null && isWithinDistance(square)) {
                if (square.hasPiece()) {
                    if (!piece.isSameColor(square.getPiece())) {
                        set.add(square);
                    }
                    break;
                } else {
                    set.add(square);
                }

                // move to the next square
                square = square.getRelative(model.deltaX, model.deltaY);
            }
        }

        return set;
    }
}
