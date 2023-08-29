package com.oo.games;

public class Move {
    public final Board.Pos source;
    public final Board.Pos destination;
    public Character promotion;


    public Move(Square source, Square destination) {
        this.source = new Board.Pos(source.x, source.y);
        this.destination = new Board.Pos(destination.x, destination.y);
        promotion = null;
    }

    public Move(String move) {
        source = new Board.Pos(move.substring(0, 2));
        destination = new Board.Pos(move.substring(2, 4));

        if (move.length() > 4) {
            promotion = move.charAt(4);
        } else {
            promotion = null;
        }
    }

    @Override
    public String toString() {
        if (promotion == null) {
            return source.toString() + destination.toString();
        } else {
            return source.toString() + destination.toString() + promotion;
        }
    }
}
