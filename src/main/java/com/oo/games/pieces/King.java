package com.oo.games.pieces;

import com.oo.games.ChessGame;
import com.oo.games.Player;
import com.oo.games.Square;
import com.oo.games.constants.PieceCode;
import com.oo.games.movement.Movement;
import com.oo.games.movement.MovementFactory;

import java.util.Set;

public class King extends Piece {
    public King(Player player, ChessGame game) {
        super(player, game);
    }

    @Override
    public void defineMovement() {
        movement = MovementFactory.combine(
                MovementFactory.createDiagonalMovement(this, 1),
                MovementFactory.createStraightMovement(this)
        );
    }

    @Override
    public MoveType performMoveTo(Square newSquare) {
        Square oldSquare = getSquare();
        MoveType moveType = super.performMoveTo(newSquare);
        if (moveType != null) {
            int deltaX = oldSquare.x - getSquare().x;
            boolean isCastle = Math.abs(deltaX) == 2;
            Player.Side side = deltaX < 0 ? Player.Side.KING : Player.Side.QUEEN;

            if (isCastle) {
                Rock rock = (Rock) game.board.getSquareAt(side == Player.Side.KING ? 7 : 0, getSquare().y).getPiece();
                rock.moveTo(game.board.getSquareAt(side == Player.Side.KING ? 5 : 3, getSquare().y));
            }

            getPlayer().removeCastleRight();
        }
        return moveType;
    }

    @Override
    public Set<Square> getAvailableSquares() {
        Set<Square> availableSquares = movement.getAvailableSquares();

        // add castling squares
        if (getPlayer().canCastle(Player.Side.KING)) {
            availableSquares.add(getSquare().getRelative(2, 0));
        }

        if (getPlayer().canCastle(Player.Side.QUEEN)) {
            availableSquares.add(getSquare().getRelative(-2, 0));
        }

        return availableSquares;
    }

    @Override
    public char getLetter() {
        return color() == Player.Color.WHITE ? PieceCode.WHITE_KING : PieceCode.BLACK_KING;
    }
}
