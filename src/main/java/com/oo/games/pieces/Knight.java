package com.oo.games.pieces;

import com.oo.games.ChessGame;
import com.oo.games.Player;
import com.oo.games.constants.PieceCode;
import com.oo.games.movement.Movement;
import com.oo.games.movement.MovementFactory;

public class Knight extends Piece {
    public Knight(Player player, ChessGame game) {
        super(player, game);
    }

    @Override
    public void defineMovement() {
        movement = MovementFactory.createKnightMovement(this);
    }

    @Override
    public char getLetter() {
        return color() == Player.Color.WHITE ? PieceCode.WHITE_KNIGHT : PieceCode.BLACK_KNIGHT;
    }
}
