package com.oo.games.pieces;

import com.oo.games.ChessGame;
import com.oo.games.Player;
import com.oo.games.constants.PieceCode;
import com.oo.games.movement.Movement;
import com.oo.games.movement.MovementFactory;

public class Queen extends Piece {
    public Queen(Player player, ChessGame game) {
        super(player, game);
    }

    @Override
    public void defineMovement() {
        this.movement = MovementFactory.combine(
                MovementFactory.createStraightMovement(this),
                MovementFactory.createDiagonalMovement(this)
        );
    }

    @Override
    public char getLetter() {
        return color() == Player.Color.WHITE ? PieceCode.WHITE_QUEEN : PieceCode.BLACK_QUEEN;
    }
}
