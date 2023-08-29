package com.oo.games.movement;

import com.oo.games.pieces.Piece;

import java.util.HashMap;
import java.util.stream.Stream;

public class MovementFactory {
    private static final int MAX_DISTANCE = 8;
    private static final HashMap<Direction, MovesModel> moveModelMap = new HashMap<>();

    static {
        initializeMoveModels();
    }

    private static void initializeMoveModels() {
        for (Direction direction : Direction.values()) {
            moveModelMap.put(direction, new MovesModel(direction.deltaX, direction.deltaY));
        }
    }

    public static Movement createStraightMovement(Piece piece) {
        return new Movement(piece,
                getModel(Direction.TOP),
                getModel(Direction.RIGHT),
                getModel(Direction.BOTTOM),
                getModel(Direction.LEFT)
        );
    }

    public static Movement createDiagonalMovement(Piece piece, int distance) {
        return new Movement(piece, distance,
                getModel(Direction.TOP_LEFT),
                getModel(Direction.TOP_RIGHT),
                getModel(Direction.BOTTOM_RIGHT),
                getModel(Direction.BOTTOM_LEFT)
        );
    }

    public static Movement createDiagonalMovement(Piece piece) {
        return createDiagonalMovement(piece, MAX_DISTANCE);
    }


    public static Movement createKnightMovement(Piece piece) {
        MovesModel[] movesModels = new MovesModel[8];
        int[][] knightDeltas = {
                {-1, 2},
                {-1, -2},
                {1, 2},
                {1, -2},
                {-2, 1},
                {-2, -1},
                {2, 1},
                {2, -1},
        };

        for (int i = 0; i < 8; i++) {
            movesModels[i] = new MovesModel(knightDeltas[i][0], knightDeltas[i][1]);
        }

        return new Movement(piece, 2, movesModels);
    }

    public static Movement combine(Movement... movements) {
        return new Movement(
                movements[0].piece, movements[0].distance,
                Stream.of(movements)
                        .flatMap(mover -> mover.movesModels.stream())
                        .toArray(MovesModel[]::new)
        );
    }

    private static MovesModel getModel(Direction direction) {
        return moveModelMap.get(direction);
    }
}
