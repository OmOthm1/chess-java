package com.oo.games.pieces;

import com.oo.games.ChessGame;
import com.oo.games.Player;
import com.oo.games.Square;
import com.oo.games.constants.PieceCode;
import com.oo.games.movement.Movement;
import com.oo.games.movement.MovementFactory;

public class Rock extends Piece {
    private boolean hasMoved = false;

    public boolean hasMoved() {
        return hasMoved;
    }

    public Rock(Player player, ChessGame game) {
        super(player, game);
    }

    @Override
    public void defineMovement() {
        movement = MovementFactory.createStraightMovement(this);
    }

    @Override
    public MoveType moveTo(Square newSquare) {
        Square prevSquare = this.getSquare();
        MoveType moveType = super.moveTo(newSquare);

        if (moveType != null && !this.hasMoved) {
            if (prevSquare.x == 7) {
                getPlayer().removeCastleRight(Player.Side.KING);
            } else {
                getPlayer().removeCastleRight(Player.Side.QUEEN);
            }
            this.hasMoved = true;
        }
        return moveType;
    }

    @Override
    public char getLetter() {
        return color() == Player.Color.WHITE ? PieceCode.WHITE_ROCK : PieceCode.BLACK_ROCK;
    }
}
