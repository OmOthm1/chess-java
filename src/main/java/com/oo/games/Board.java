package com.oo.games;

import com.oo.games.constants.FEN;
import com.oo.games.pieces.*;
import com.oo.games.constants.PieceCode;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class Board {
    Square[][] squares;
    public ChessGame game;
    private static final Map<Character, Class<? extends Piece>> charToPieceTypeMap = new HashMap<>();

    static {
        charToPieceTypeMap.put(PieceCode.WHITE_ROCK, Rock.class);
        charToPieceTypeMap.put(PieceCode.WHITE_KNIGHT, Knight.class);
        charToPieceTypeMap.put(PieceCode.WHITE_BISHOP, Bishop.class);
        charToPieceTypeMap.put(PieceCode.WHITE_QUEEN, Queen.class);
        charToPieceTypeMap.put(PieceCode.WHITE_KING, King.class);
        charToPieceTypeMap.put(PieceCode.WHITE_PAWN, Pawn.class);
    }


    Board(ChessGame game) {
        this(game, FEN.STARTING_POSITION.split(" ")[0]);
    }

    Board(ChessGame game, String fenPiecePlacement) {
        this.game = game;
        squares = new Square[8][8];
        initializeSquares();
        initializePieces(fenPiecePlacement);
    }

    void initializePieces(@SuppressWarnings("SameParameterValue") String fenPieces) {
        Square square = getSquareAt(0, 0);

        for (char ch : fenPieces.toCharArray()) {
            if (ch >= '0' && ch <= '8') {
                Square relativeSquare = square.getRelative(ch - '0', 0);
                if (relativeSquare != null) {
                    square = relativeSquare;
                }
            } else if (ch == '/') {
                square = getSquareAt(0, square.y + 1);
            } else {
                Class<? extends Piece> pieceClass = charToPieceTypeMap.get(Character.toUpperCase(ch));
                if (pieceClass != null) {
                    try {
                        Piece piece = pieceClass.getDeclaredConstructor(Player.class, ChessGame.class)
                                .newInstance(Character.isLowerCase(ch) ? game.black : game.white, game);
                        square.setPiece(piece);
                    } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                             NoSuchMethodException e) {
                        throw new RuntimeException(e);
                    }
                }

                // move to the next square
                if (square.x < 7)
                    square = square.getRelative(1, 0);
            }
        }
    }

    private void initializeSquares() {
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                squares[y][x] = new Square(this, x, y);
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                stringBuilder.append(squares[y][x]);
            }
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }

    public Square getSquareAt(int x, int y) {
        if (!isWithinBounds(x, y)) {
            return null;
        }

        return squares[y][x];
    }

    public Square getSquareAt(Pos pos) {
        return getSquareAt(pos.x, pos.y);
    }

    public Square getSquareAt(String notation) {
        return getSquareAt(new Pos(notation));
    }

    public Square getSquareViewAt(Pos pos) {
        return getSquareViewAt(pos.x, pos.y);
    }

    public Square getSquareViewAt(int x, int y) {
        if (game.viewFlipped) {
            return getSquareAt(7 - x, 7 - y);
        } else {
            return getSquareAt(x, y);
        }
    }

    private static boolean isWithinBounds(int x, int y) {
        return (x >= 0 && x <= 7) && (y >= 0 && y <= 7);
    }

    public static class Pos {
        public final int x;
        public final int y;

        Pos(int x, int y) {
            this.x = x;
            this.y = y;
        }

        /**
         * @param notation like: h1, b3, f5
         */
        Pos(String notation) {
            this.x = 7 - ('h' - notation.charAt(0));
            this.y = '8' - notation.charAt(1);
        }

        @Override
        public String toString() {
            return (char) ('a' + x) + String.valueOf(8 - y);
        }
    }
}
