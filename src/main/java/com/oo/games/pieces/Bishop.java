package com.oo.games.pieces;

import com.oo.games.ChessGame;
import com.oo.games.Player;
import com.oo.games.constants.PieceCode;
import com.oo.games.movement.MovementFactory;

public class Bishop extends Piece {
    public Bishop(Player player, ChessGame game) {
        super(player, game);
    }

    @Override
    public void defineMovement() {
        movement = MovementFactory.createDiagonalMovement(this);
    }

    @Override
    public char getLetter() {
        return color() == Player.Color.WHITE ? PieceCode.WHITE_BISHOP : PieceCode.BLACK_BISHOP;
    }
}
