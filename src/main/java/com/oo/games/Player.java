package com.oo.games;

import com.oo.games.pieces.King;
import com.oo.games.pieces.Piece;
import com.oo.games.pieces.Rock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class Player {
    public Color playerColor;
    HashMap<Character, Set<Piece>> piecesMap = new HashMap<>();
    Board board;
    HashSet<Side> castleRights;
    ChessGame game;

    public enum Side {
        KING, QUEEN
    }

    Player(Player.Color playerColor, ChessGame game) {
        this.game = game;
        this.board = game.board;
        this.playerColor = playerColor;
        "RNBQKP".chars().forEach((pChar -> piecesMap.put((char) pChar, new HashSet<>())));

        initCastleRights();
    }

    Player(Player.Color playerColor, ChessGame game, String castleRights) {
        this.game = game;
        this.board = game.board;
        this.playerColor = playerColor;
        "RNBQKP".chars().forEach((pChar -> piecesMap.put((char) pChar, new HashSet<>())));

        initCastleRights(castleRights);
    }

    public boolean canCastle(Side side) {
        if (castleObstructed(side)) return false;

        return castleRights.contains(side);
    }

    public void removeCastleRight(Side side) {
        castleRights.remove(side);
    }

    public void removeCastleRight() {
        castleRights.clear();
    }

    public boolean isMyTurn() {
        return playerColor == game.getTurn();
    }

    private boolean castleObstructed(Side side) {
        int deltaX = side == Side.KING ? 1 : -1;

        if (castleRights.contains(side)) {
            Square castleSquare1 = getKing().getSquare().getRelative(deltaX, 0);
            Square castleSquare2 = getKing().getSquare().getRelative(deltaX * 2, 0);

            if (side == Side.QUEEN) {
                Square castleSquare3 = getKing().getSquare().getRelative(deltaX * 3, 0);
                if (castleSquare3.hasPiece()) {
                    return true;
                }
            }

            Set<Square> opponentCoveredSquares = getOpponentCoveredSquares();

            // king in check
            return castleSquare1.hasPiece() || // castling square 1 has a piece
                    castleSquare2.hasPiece() || // castling square 2 has a piece
                    opponentCoveredSquares.contains(castleSquare1) || // castling square 1 covered
                    opponentCoveredSquares.contains(castleSquare2) || // castling square 2 covered
                    opponentCoveredSquares.contains(getKing().getSquare());
        }
        return false;
    }


    private void initCastleRights(String castleRights) {
        this.castleRights = new HashSet<>();

        if (playerColor == Color.BLACK) {
            if (castleRights.contains("k")) {
                this.castleRights.add(Player.Side.KING);
            }
            if (castleRights.contains("q")) {
                this.castleRights.add(Player.Side.QUEEN);
            }
        } else {
            if (castleRights.contains("K")) {
                this.castleRights.add(Player.Side.KING);
            }
            if (castleRights.contains("Q")) {
                this.castleRights.add(Player.Side.QUEEN);
            }
        }
    }

    private void initCastleRights() {
        initCastleRights("KQkq");
    }

    public boolean inCheck() {
        return getOpponentCoveredSquares().contains(getKing().getSquare());
    }

//    public boolean isCheckMated() {return inCheck() && isStaleMated();}

    public boolean isStaleMated() {
        for (Piece piece : getAllPieces()) {
            if (!piece.getAvailableSquares().isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public Set<Square> getOpponentCoveredSquares() {
        ArrayList<Piece> opponentPieces = opponent().getAllPieces();
        Set<Square> coveredSquares = new HashSet<>();

        for (Piece piece : opponentPieces) {
            coveredSquares.addAll(piece.getCoveredSquares());
        }

        return coveredSquares;
    }

    private ArrayList<Piece> getAllPieces() {
        ArrayList<Piece> pieces = new ArrayList<>();

        for (Set<Piece> pieceList : piecesMap.values()) {
            pieces.addAll(pieceList);
        }

        return pieces;
    }

    public Player opponent() {
        return playerColor == Color.WHITE ? game.black : game.white;
    }

    public King getKing() {
        return (King) piecesMap.get('K').iterator().next();
    }

    public void addPiece(Piece piece) {
        if (piece == null) {
            return;
        }

        piecesMap.get(piece.getUpperCaseLetter()).add(piece);
    }

    public void removePiece(Piece piece) {
        if (piece == null) {
            return;
        }

        // remove castling rights if a rock was captured
        if (piece.isRock() && !((Rock) piece).hasMoved()) {
            if (piece.getSquare().x == 7) {
                removeCastleRight(Player.Side.KING);
            } else {
                removeCastleRight(Player.Side.QUEEN);
            }
        }

        piecesMap.get(piece.getUpperCaseLetter()).remove(piece);
    }

    public enum Color {
        BLACK, WHITE
    }
}
