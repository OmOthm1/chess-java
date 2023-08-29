package com.oo.games.gui;

import com.oo.games.*;
import com.oo.games.constants.PieceCode;
import com.oo.games.pieces.Piece;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

public class ChessGamePanel extends JPanel {
    Piece selectedPiece;
    Board board;

    HashMap<Character, Integer> piecesMap = new HashMap<>();
    HashSet<Square> squaresOfPossibleMoves = new HashSet<>();
    HashSet<Square> highlightedSquares = new HashSet<>();

    int subImageWidth;
    int subImageHeight;
    ChessGame game;
    Player winner;
    boolean gameOver = false;
    Piece draggedPiece;
    Point draggingPoint;
    SoundManager soundManager;

    HashMap<String, Move> arrows = new HashMap<>();

    BufferedImage piecesImage;

    // -- SIZES
    private final int BOARD_SIZE = 600;
    private final int SQUARE_SIZE = BOARD_SIZE / 8;


    // -- COLORS
    private static final Color LIGHT_SQUARE_COLOR = Color.decode("#f0d9b5");
    private static final Color DARK_SQUARE_COLOR = Color.decode("#b58863");
    private static final Color HIGHLIGHTED_SQUARE_COLOR = new Color(0, 200, 255, 79);
    private static final Color ARROW_COLOR = HIGHLIGHTED_SQUARE_COLOR;
    private static final Color MOVE_SQUARES_COLOR = new Color(255, 255, 0, 100);
    private static final Color CHECKMATED_KING_SQUARE_COLOR = new Color(255, 0, 0, 100);
    private static final Color POSSIBLE_MOVE_SQUARE_COLOR = new Color(0, 255, 0, 100);

    public static final String PIECES_IMAGE_PATH = "src/main/resources/img/pieces.png";

    public ChessGamePanel(ChessGame game) {
        this.game = game;
        this.board = game.board;
        setPreferredSize(new Dimension(BOARD_SIZE, BOARD_SIZE));
        setBackground(Color.WHITE);
        setFocusable(true);
        addMouseListener(new MyMouseListener());
        addMouseMotionListener(new MyMouseMotionListener());
        addKeyListener(new MyKeyListener());


        // -- pieces map
        piecesMap.put(PieceCode.WHITE_KING, 0);
        piecesMap.put(PieceCode.WHITE_QUEEN, 1);
        piecesMap.put(PieceCode.WHITE_BISHOP, 2);
        piecesMap.put(PieceCode.WHITE_KNIGHT, 3);
        piecesMap.put(PieceCode.WHITE_ROCK, 4);
        piecesMap.put(PieceCode.WHITE_PAWN, 5);

        // Load the pieces image
        try {
            File imageFile = new File(PIECES_IMAGE_PATH);
            piecesImage = ImageIO.read(imageFile);
            subImageWidth = piecesImage.getWidth() / 6;
            subImageHeight = piecesImage.getHeight() / 2;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        soundManager = new SoundManager();
    }


    private void drawArrow(Graphics2D g2d, Square source, Square destination) {
        int x1 = (source.x * SQUARE_SIZE) + (SQUARE_SIZE / 2);
        int y1 = (source.y * SQUARE_SIZE) + (SQUARE_SIZE / 2);

        int x2 = (destination.x * SQUARE_SIZE) + (SQUARE_SIZE / 2);
        int y2 = (destination.y * SQUARE_SIZE) + (SQUARE_SIZE / 2);

        int width = 9;

        g2d.setColor(ARROW_COLOR);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setStroke(new BasicStroke(width, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        // draw arrow line
//        g2d.drawLine(x1, y1, x2, y2);


        double dx = x2 - x1, dy = y2 - y1;
        double angle = Math.atan2(dy, dx);
        int h = width * 3;
        int x3 = (int) (x2 - h * Math.cos(angle - Math.PI / 6));
        int y3 = (int) (y2 - h * Math.sin(angle - Math.PI / 6));
        int x4 = (int) (x2 - h * Math.cos(angle + Math.PI / 6));
        int y4 = (int) (y2 - h * Math.sin(angle + Math.PI / 6));

        // draw arrow head
//        g2d.drawLine(x3, y3, x2, y2);
//        g2d.drawLine(x4, y4, x2, y2);

        int[] xPoints = {x1, x2, x3, x2, x4, x2};
        int[] yPoints = {y1, y2, y3, y2, y4, y2};
        g2d.drawPolyline(xPoints, yPoints, 6);
    }


    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g); // clears the canvas

        drawBoard(g);
        highlightSquaresOfPossibleMoves(g);

        if (gameOver) {
            g.setColor(CHECKMATED_KING_SQUARE_COLOR);

            if (winner != null) {
                drawSquare(g, winner.opponent().getKing().getSquare());
            } else {
                drawSquare(g, game.white.getKing().getSquare());
                drawSquare(g, game.black.getKing().getSquare());
            }
        }

        drawPieces(g);
    }

    private void drawPieces(Graphics g) {
        // Graphics 2D
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                Square square = board.getSquareAt(x, y);

                // -- draw image
                if (square.hasPiece()) {
                    Piece piece = square.getPiece();

                    if (piece != draggedPiece) {
                        drawPiece(g2d, square);
                    } else {
                        drawShadowPiece(g2d, square);
                    }
                }
            }
        }

        if (draggedPiece != null) {
            drawPiece(g2d, draggedPiece,
                    draggingPoint.x - (SQUARE_SIZE / 2),
                    draggingPoint.y - (SQUARE_SIZE / 2)
            );
        }

        for (Move move : arrows.values()) {
            drawArrow(g2d, board.getSquareViewAt(move.source), board.getSquareViewAt(move.destination));
        }

        g2d.dispose();
    }

    private void highlightSquaresOfPossibleMoves(Graphics g) {
        g.setColor(POSSIBLE_MOVE_SQUARE_COLOR);
        if (selectedPiece != null) {
            Set<Square> availableSquares = selectedPiece.getAvailableSquares();
            squaresOfPossibleMoves = new HashSet<>(availableSquares);
            for (Square sq : availableSquares) {
                drawSquare(g, sq);
            }
        } else {
            squaresOfPossibleMoves.clear();
        }

        if (game.lastMove != null) {
            g.setColor(MOVE_SQUARES_COLOR);
            drawSquare(g, board.getSquareAt(game.lastMove.source));
            drawSquare(g, board.getSquareAt(game.lastMove.destination));
        }
    }

    private void drawSquare(Graphics g, Square square) {
        g.fillRect(square.getViewX() * SQUARE_SIZE, square.getViewY() * SQUARE_SIZE, SQUARE_SIZE, SQUARE_SIZE);
    }

    private void drawBoard(Graphics g) {
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                // -- set square color
                Color squareColor = (x + y) % 2 == 0 ? LIGHT_SQUARE_COLOR : DARK_SQUARE_COLOR;
                g.setColor(squareColor);

                // -- draw square
                g.fillRect(x * SQUARE_SIZE, y * SQUARE_SIZE, SQUARE_SIZE, SQUARE_SIZE);
            }
        }

        for (Square square : highlightedSquares) {
            g.setColor(HIGHLIGHTED_SQUARE_COLOR);
            drawSquare(g, square);
        }
    }

    private void update() {
        repaint();
    }

    private void drawShadowPiece(Graphics2D g, Square square) {
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f)); // set opacity to 50%
        drawPiece(g, square);
        g.setComposite(AlphaComposite.SrcOver); // reset composite
    }

    private void drawPiece(Graphics2D g, Square square) {
        final int x = square.getViewX() * SQUARE_SIZE;
        final int y = square.getViewY() * SQUARE_SIZE;
        drawPiece(g, square.getPiece(), x, y);
    }

    private void drawPiece(Graphics2D g, Piece piece, int x, int y) {
        g.drawImage(getImage(piece), x, y, SQUARE_SIZE, SQUARE_SIZE, null);
    }

    private BufferedImage getImage(Piece piece) {
        return getImage(piece.color(), piecesMap.get(piece.getUpperCaseLetter()));
    }

    private BufferedImage getImage(Player.Color color, int pieceCode) {
        return piecesImage.getSubimage(pieceCode * subImageWidth, color == Player.Color.WHITE ? 0 : subImageHeight, subImageWidth, subImageHeight);
    }

    private void onClick(Square clickedSquare) {
        if (clickedSquare == null) {
            return;
        }

        if (selectedPiece != null) {
            if (selectedPiece == clickedSquare.getPiece()) { // player clicked on a selected piece
                selectedPiece = null;
                update();
                return;
            } else {
                Set<Square> availableSquares = selectedPiece.getAvailableSquares();

                if (availableSquares.contains(clickedSquare)) {
                    Piece.MoveType moveType = selectedPiece.moveTo(clickedSquare);
                    if (moveType != null) {
                        moved(selectedPiece.getPlayer(), moveType);
                    }
                }
            }
        }

        if (clickedSquare.hasPiece() && clickedSquare.getPiece().getPlayer().isMyTurn()) {
            selectedPiece = clickedSquare.getPiece();
            update();
        }
    }

    private Square getSquareAtPoint(Point clickPoint) {
        return board.getSquareViewAt(clickPoint.x / SQUARE_SIZE, clickPoint.y / SQUARE_SIZE);
    }

    private void moved(Player player, Piece.MoveType moveType) {
        game.moveNumber++;
        game.changeTurn();

        // play sound
        if (moveType == Piece.MoveType.MOVE) {
            soundManager.playMoveSound();
        } else if (moveType == Piece.MoveType.CAPTURE) {
            soundManager.playCaptureSound();
        }

        // unselect piece
        selectedPiece = null;

        if (player.opponent().isStaleMated()) {
            String gameOverTitle = "Game Over.";
            if (player.opponent().inCheck()) {
                String winMessage = player.playerColor + " Won.";
                JOptionPane.showMessageDialog(this, winMessage, gameOverTitle, JOptionPane.INFORMATION_MESSAGE);
                System.out.println(winMessage);
                winner = player;
            } else {
                String staleMateMessage = "Stale mate, the game is a draw.";
                JOptionPane.showMessageDialog(this, staleMateMessage, gameOverTitle, JOptionPane.INFORMATION_MESSAGE);
                System.out.println(staleMateMessage);
            }
            gameOver = true;
        }

        game.positions.push(game.toFen());
        System.out.println(game.board);

        flipBoardAfter(500);

        update();
    }

    private void flipBoardAfter(long millis) {
        new Thread(() -> {
            try {
                Thread.sleep(millis);
                game.viewFlipped = game.black.isMyTurn();
                update();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    private class MyMouseListener extends MouseAdapter {
        Square leftBtnPressedSquare;
        Square rightBtnPressedSquare;
        Square arrowSourceSquare;

        private void leftButtonPressed(Square square, Point point) {
            leftBtnPressedSquare = square;

            if (leftBtnPressedSquare != null && leftBtnPressedSquare.hasPiece()) {
                draggedPiece = leftBtnPressedSquare.getPiece();
                draggingPoint = point;
            }
        }

        private void leftButtonReleased(Square square) {
            onClick(square);

            // remove pressed square
            leftBtnPressedSquare = null;

            // dragged piece
            if (draggedPiece != null) {
                draggedPiece = null;
                draggingPoint = null;
                repaint();
            }
        }

        private void rightButtonReleased(Square square) {
            if (square == null) {
                arrowSourceSquare = null;
                return;
            }

            if (rightBtnPressedSquare != null && square == rightBtnPressedSquare) {
                if (highlightedSquares.contains(square)) {
                    highlightedSquares.remove(square);
                } else {
                    highlightedSquares.add(square);
                }
            }

            if (arrowSourceSquare != null && square != arrowSourceSquare) {
                Move move = new Move(arrowSourceSquare, square);

                if (arrows.containsKey(move.toString())) {
                    arrows.remove(move.toString());
                } else {
                    arrows.put(move.toString(), new Move(arrowSourceSquare, square));
                }
            }
            update();
        }

        @Override
        public void mousePressed(MouseEvent e) {
            Square square = getSquareAtPoint(e.getPoint());

            if (e.getButton() == MouseEvent.BUTTON1) { // left button
                leftButtonPressed(square, e.getPoint());
            } else if (e.getButton() == MouseEvent.BUTTON3) { // right button
                rightBtnPressedSquare = square;
                arrowSourceSquare = square;
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            Square square = getSquareAtPoint(e.getPoint());

            if (e.getButton() == MouseEvent.BUTTON1) { // left button
                leftButtonReleased(square);
            } else if (e.getButton() == MouseEvent.BUTTON3) { // right button
                rightButtonReleased(square);
            }
        }
    }


    private class MyMouseMotionListener extends MouseMotionAdapter {
        @Override
        public void mouseMoved(MouseEvent e) {
            Square square = getSquareAtPoint(e.getPoint());
            if (square != null && (square.hasPiece() || squaresOfPossibleMoves.contains(square))) {
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            } else {
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            }
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            if (draggedPiece != null) {
                if (selectedPiece != draggedPiece && draggedPiece.getPlayer().isMyTurn()) {
                    onClick(draggedPiece.getSquare());
                }
                draggingPoint = e.getPoint();
                update();
            }
        }
    }

    private class MyKeyListener extends KeyAdapter {
        @Override
        public void keyReleased(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_F:
                    game.viewFlipped = !game.viewFlipped;
                    update();
                    break;

                case KeyEvent.VK_Q:
                    System.out.println(game.toFen());
                    break;

                case KeyEvent.VK_LEFT:
                    if (game.positions.size() > 1) {
                        Stack<String> positions = game.positions;
                        positions.pop();
                        game = new ChessGame(positions.peek(), positions);

                        board = game.board;
                        highlightedSquares.clear();
                        selectedPiece = null;
                        draggingPoint = null;
                        draggedPiece = null;
                        gameOver = false;
                        winner = null;
                        update();
                        flipBoardAfter(0);
                        soundManager.playMoveSound();
                    }
                    break;

                case KeyEvent.VK_C:
                    highlightedSquares.clear();
                    arrows.clear();
                    update();

            }
        }
    }
}