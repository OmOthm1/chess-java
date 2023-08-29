package com.oo.games;

import com.oo.games.constants.FEN;
import com.oo.games.constants.PieceCode;

import java.util.Stack;

public class ChessGame {
    private Player.Color turn;
    public Player white;
    public Player black;
    public Board board;
    public int moveNumber;
    public boolean viewFlipped = false;
    public Square enPassantTarget;
    public Move lastMove;
    int halfMoveClock;

    public Stack<String> positions;

    public static final String STARTING_POSITION = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
//        String play = "e2e4,e7e5,f2f4,d8h4,g2g3,h4g3,h2g3,h7h5,g3g4,h5g4,g1h3,g4h3,f1g2,h3g2,e1f2,g2h1q";

    ChessGame() {
        this(FEN.STARTING_POSITION);
    }

    public ChessGame(String fen) {
        this(fen, new Stack<>());
        this.positions.push(FEN.STARTING_POSITION);
    }

    public ChessGame(String fen, Stack<String> positions) {
        String[] fenTokens = fen.split(" ");
        String piecePlacement = fenTokens[0];
        String playerToPlay = fenTokens[1];
        String castleRights = fenTokens[2];
        String enPassantTarget = fenTokens[3];
        String halfMoveClock = fenTokens[4];
        String fullMoveNumber = fenTokens[5];

        this.positions = positions;

        white = new Player(Player.Color.WHITE, this, castleRights);
        black = new Player(Player.Color.BLACK, this, castleRights);

        // -- turn
        turn = playerToPlay.equals(String.valueOf(FEN.WHITE)) ? Player.Color.WHITE : Player.Color.BLACK;

        // -- piece placement
        board = new Board(this, piecePlacement);

        // -- move number
        moveNumber = (Integer.parseInt(fullMoveNumber) * 2) + (turn == Player.Color.WHITE ? 0 : 1);

        // -- half move clock
        this.halfMoveClock = Integer.parseInt(halfMoveClock);

        // -- emPassant
        if (!enPassantTarget.equals("-")) {
            this.enPassantTarget = board.getSquareAt(enPassantTarget);
        }
    }

    public String toFen() {
        StringBuilder fen = new StringBuilder();

        // -- piece placement
        for (int y = 0; y < 8; y++) {
            int emptyConsecutiveSquares = 0;
            for (int x = 0; x < 8; x++) {
                Square square = board.getSquareAt(x, y);

                if (square.hasPiece()) {
                    if (emptyConsecutiveSquares > 0) {
                        fen.append(emptyConsecutiveSquares);
                        emptyConsecutiveSquares = 0;
                    }

                    fen.append(square.getPiece().getLetter());
                } else {
                    emptyConsecutiveSquares++;
                }
            }
            if (emptyConsecutiveSquares > 0) {
                fen.append(emptyConsecutiveSquares);
            }
            if (y < 7) {
                fen.append('/');
            }
        }

        // -- player to play
        fen.append(' ');
        fen.append(turn == Player.Color.WHITE ? FEN.WHITE : FEN.BLACK);

        // -- castle rights
        fen.append(' ');
        StringBuilder castleRights = new StringBuilder();
        if (white.castleRights.contains(Player.Side.KING)) {
            castleRights.append(PieceCode.WHITE_KING);
        }
        if (white.castleRights.contains(Player.Side.QUEEN)) {
            castleRights.append(PieceCode.WHITE_QUEEN);
        }
        if (black.castleRights.contains(Player.Side.KING)) {
            castleRights.append(PieceCode.BLACK_KING);
        }
        if (black.castleRights.contains(Player.Side.QUEEN)) {
            castleRights.append(PieceCode.BLACK_QUEEN);
        }
        fen.append(castleRights.length() > 0 ? castleRights : '-');

        // -- En Passant target
        fen.append(' ');
        fen.append(enPassantTarget == null ? '-' : enPassantTarget.pos);

        // -- Halfmove Clock: if this is equal to 99 then the next move is a draw.
        fen.append(' ');
        fen.append(0);

        // -- Fullmove Number: number of completed turns in the game, it increases when black moves.
        fen.append(' ');
        fen.append(moveNumber / 2);

        return fen.toString();
    }

    @SuppressWarnings("MethodDoesntCallSuperMethod")
    @Override
    public ChessGame clone() {
        return new ChessGame(toFen());
    }

    public void changeTurn() {
        turn = turn == Player.Color.WHITE ? Player.Color.BLACK : Player.Color.WHITE;
    }

    public Player.Color getTurn() {
        return turn;
    }
}
