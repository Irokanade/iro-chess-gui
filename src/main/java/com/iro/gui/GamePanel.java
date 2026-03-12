package com.iro.gui;

import com.iro.board.Board;
import com.iro.board.CapturedPieces;
import com.iro.board.Moves;
import com.iro.board.NativeBoard;
import com.iro.board.PieceEnum;
import com.iro.board.SquareEnum;
import com.iro.engine.UciClient;
import com.iro.piece.BlackBishop;
import com.iro.piece.BlackKnight;
import com.iro.piece.BlackQueen;
import com.iro.piece.BlackRook;
import com.iro.piece.Piece;
import com.iro.piece.PieceFactory;
import com.iro.piece.WhiteBishop;
import com.iro.piece.WhiteKnight;
import com.iro.piece.WhiteQueen;
import com.iro.piece.WhiteRook;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.io.IOException;
import java.util.ArrayList;

public class GamePanel extends JPanel implements Runnable {

    public static final int WIDTH = 1100;
    public static final int HEIGHT = 800;

    public final int MAX_COL = 8;
    public final int MAX_ROW = 8;
    public static final int SQUARE_SIZE = 100;
    public static final int HALF_SQUARE_SIZE = SQUARE_SIZE / 2;

    public final int FPS = 60;
    public Thread gameThread;
    private final NativeBoard board = new NativeBoard();
    public Mouse mouse = new Mouse();

    public static ArrayList<Piece> simPieces = new ArrayList<Piece>();
    public ArrayList<Piece> promoPieces = new ArrayList<Piece>();
    public CapturedPieces capturedPieces = new CapturedPieces();
    public Piece activePiece;

    public static Moves moveList = new Moves();
    public static Moves historyMoveList = new Moves();

    public SquareEnum promotionSource;
    public SquareEnum promotionTarget;
    public boolean gameOver;
    public boolean stalemate;

    private boolean playAgainstComputer = true;
    public UciClient uciClient;
    public static boolean boardFlipped = false;

    public GamePanel(String computerSide) {
        boardFlipped = computerSide.equals("white");
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.BLACK);
        addMouseMotionListener(mouse);
        addMouseListener(mouse);

        board.createPosition();
        board.setFen(NativeBoard.START_POSITION);

        if (playAgainstComputer) {
            uciClient = new UciClient();
            try {
                uciClient.start();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }

            if (computerSide.equals("white")) {
                computerMakeMove();
            }
        }

        board.generateMoves(moveList);
        copyPieces(board, simPieces);
    }

    public void launchGame() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    public void copyPieces(Board board, ArrayList<Piece> target) {
        target.clear();
        PieceFactory pieceFactory = new PieceFactory();

        for (PieceEnum pieceEnum : PieceEnum.values()) {
            long bitboardCopy = board.getBitboards()[pieceEnum.ordinal()];

            while (bitboardCopy != 0) {
                int currentPieceSquare = board.getLsbIndex(bitboardCopy);

                target.add(pieceFactory.createPiece(pieceEnum, SquareEnum.intToSquare(currentPieceSquare)));

                bitboardCopy = board.popBit(bitboardCopy, SquareEnum.intToSquare(currentPieceSquare));
            }
        }
    }

    public void copyPieces(NativeBoard board, ArrayList<Piece> target) {
        target.clear();
        PieceFactory pieceFactory = new PieceFactory();

        for (int square = 0; square < 64; square++) {
            PieceEnum piece = board.pieceAt(square);
            if (piece != null) {
                target.add(pieceFactory.createPiece(piece, SquareEnum.intToSquare(square)));
            }
        }
    }

    private void playMove(int move) {
        if (Moves.isCapture(move)) {
            PieceEnum captured;
            if (Moves.isEnPassant(move)) {
                // En passant captured pawn is on the same file as target, same rank as source
                captured = board.getSide() == NativeBoard.SIDE_WHITE ? PieceEnum.p : PieceEnum.P;
            } else {
                SquareEnum target = Moves.getMoveTarget(move);
                captured = board.pieceAt(target.ordinal());
            }

            if (captured != null) {
                capturedPieces.add(board.getSide(), captured);
            }
        }

        board.makeMove(move);
        historyMoveList.moves[historyMoveList.count++] = move;
    }

    public void computerMakeMove() {
        String bestMove = uciClient.getBestMove(historyMoveList);
        System.out.println(bestMove);
        int engineMove = board.parseMove(bestMove.substring(9));
        playMove(engineMove);
    }

    @Override
    public void run() {
        double drawInterval = (double) 1000000000 / FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;

        while(gameThread != null) {
            currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            lastTime = currentTime;
            if (delta >= 1) {
                update();
                repaint();
                delta--;
            }
        }
    }

    private void update() {
        if (promotionSource != null) {
            promoting();
            return;
        }

        if (mouse.pressed) {
            if (activePiece == null) {
                for (Piece piece : simPieces) {
                    if (piece.getSquare() ==
                            SquareEnum.fromScreen(
                                boardFlipped ? 7 - mouse.x / SQUARE_SIZE : mouse.x / SQUARE_SIZE,
                                boardFlipped ? 7 - mouse.y / SQUARE_SIZE : mouse.y / SQUARE_SIZE)) {
                        activePiece = piece;
                        activePiece.setPreSquare(piece.getSquare());
                    }
                }
            } else {
                simulate();
            }
        }

        if (!mouse.pressed && activePiece != null) {
            boolean legalMove = false;
            for (int i = 0; i < moveList.count; i++) {
                SquareEnum sourceSquare = Moves.getMoveSource(moveList.moves[i]);
                SquareEnum targetSquare = Moves.getMoveTarget(moveList.moves[i]);

                if (activePiece.getPreSquare() == sourceSquare &&
                    activePiece.getSquare() == targetSquare) {

                    if (Moves.isPromotion(moveList.moves[i])) {
                        promotionSource = sourceSquare;
                        promotionTarget = targetSquare;

                        promoPieces.clear();
                        if (board.getSide() == NativeBoard.SIDE_WHITE) {
                            promoPieces.add(new WhiteRook(9, 2));
                            promoPieces.add(new WhiteKnight(9, 3));
                            promoPieces.add(new WhiteBishop(9, 4));
                            promoPieces.add(new WhiteQueen(9, 5));
                        } else {
                            promoPieces.add(new BlackRook(9, 2));
                            promoPieces.add(new BlackKnight(9, 3));
                            promoPieces.add(new BlackBishop(9, 4));
                            promoPieces.add(new BlackQueen(9, 5));
                        }

                        return;
                    }

                    playMove(moveList.moves[i]);
                    legalMove = true;

                    if (playAgainstComputer) {
                        computerMakeMove();
                    }

                    board.generateMoves(moveList);
                    copyPieces(board, simPieces);

                    if (!board.hasLegalMoves(moveList)) {
                        if (board.isInCheck()) {
                            gameOver = true;
                        } else {
                            stalemate = true;
                        }
                    }

                    break;
                }
            }

            if (!legalMove && promotionSource == null) {
                activePiece.setPosition(activePiece.getPreSquare());
            }

            activePiece = null;
        }
    }

    private void simulate() {
        activePiece.x = mouse.x - HALF_SQUARE_SIZE;
        activePiece.y = mouse.y - HALF_SQUARE_SIZE;
        activePiece.setSquare(activePiece.x, activePiece.y);
    }

    private void promoting() {
        if (mouse.pressed) {
            for (Piece piece : promoPieces) {
                if (piece.getCol() == mouse.x / SQUARE_SIZE &&
                    piece.getRow() == mouse.y / SQUARE_SIZE) {

                    int promoType = pieceToPromoType(piece.getPiece());
                    for (int i = 0; i < moveList.count; i++) {
                        int move = moveList.moves[i];
                        if (Moves.getMoveSource(move) == promotionSource &&
                            Moves.getMoveTarget(move) == promotionTarget &&
                            Moves.isPromotion(move) &&
                            Moves.getPromotionPieceType(move) == promoType) {

                            playMove(move);

                            if (playAgainstComputer) {
                                computerMakeMove();
                            }

                            board.generateMoves(moveList);
                            copyPieces(board, simPieces);

                            promotionSource = null;
                            promotionTarget = null;
                            activePiece = null;
                            break;
                        }
                    }
                }
            }
        }
    }

    private int pieceToPromoType(PieceEnum piece) {
        switch (piece) {
            case N:
            case n:
                return 0;
            case B:
            case b:
                return 1;
            case R:
            case r:
                return 2;
            case Q:
            case q:
                return 3;
            default: return -1;
        }
    }

    public void cleanup() {
        board.destroyPosition();

        if (uciClient != null) {
            uciClient.close();
            uciClient = null;
        }

        if (gameThread != null) {
            gameThread = null;
        }
    }

    private void drawCapturedPieces(Graphics2D graphics2d, int side, int startX, int startY) {
        int size = SQUARE_SIZE / 2;
        PieceFactory pieceFactory = new PieceFactory();

        for (int i = 0; i < capturedPieces.count()[side]; i++) {
            int x = startX + (i % 8) * (size + 5);
            int y = startY + (i / 8) * (size + 5);
            Piece p = pieceFactory.createPiece(capturedPieces.pieces()[side][i], SquareEnum.A1);
            graphics2d.drawImage(p.getImage(), x, y, size, size, null);
        }
    }

    public void drawBoard(Graphics2D graphics2d) {
        int c = 0;

        for (int i = 0; i < MAX_ROW; i++) {
            for (int j = 0; j < MAX_COL; j++) {
                if (c == 0) {
                    graphics2d.setColor(new Color(210, 165, 125));
                    c = 1;
                } else {
                    graphics2d.setColor(new Color(175, 115, 70 ));
                    c = 0;
                }

                graphics2d.fillRect(j * SQUARE_SIZE, i * SQUARE_SIZE, SQUARE_SIZE, SQUARE_SIZE);
            }

            if (c == 0) {
                c = 1;
            } else {
                c = 0;
            }
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D graphics2d = (Graphics2D) g;

        drawBoard(graphics2d);

        for (Piece p : simPieces) {
            p.draw(graphics2d);
        }

        drawCapturedPieces(graphics2d, NativeBoard.SIDE_WHITE, 820, 30);
        drawCapturedPieces(graphics2d, NativeBoard.SIDE_BLACK, 820, HEIGHT - 90);

//        if (activePiece != null) {
//            if (canMove) {
//                if(isIllegal(activePiece) || opponentCanCaptureKing()) {
//                    graphics2d.setColor(Color.gray);
//                    graphics2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
//                    graphics2d.fillRect(activePiece.col*SQUARE_SIZE, activePiece.row*SQUARE_SIZE,
//                        SQUARE_SIZE, SQUARE_SIZE);
//                    graphics2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
//                } else {
//                    graphics2d.setColor(Color.white);
//                    graphics2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
//                    graphics2d.fillRect(activePiece.col*SQUARE_SIZE, activePiece.row*SQUARE_SIZE,
//                        SQUARE_SIZE, SQUARE_SIZE);
//                    graphics2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
//                }
//            }
//
//            // Draw the active piece in the end so it won't be hidden
//            // by the board or the colored square
//            activePiece.draw(graphics2d);
//        }

        graphics2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        graphics2d.setFont(new Font("Book Antique", Font.PLAIN, 40));
        graphics2d.setColor(Color.WHITE);

        if (promotionSource != null) {
            graphics2d.drawString("Promoting to:", 840, 150);
            for (Piece piece : promoPieces) {
                graphics2d.drawImage(piece.getImage(), piece.getX(), piece.getY(),
                    SQUARE_SIZE, SQUARE_SIZE, null);
            }
        }
//        else {
//            if (board.getSide() == NativeBoard.SIDE_WHITE) {
//                graphics2d.drawString("White's turn", 840, 550);
//                if(checkingPiece != null && checkingPiece.color == BLACK) {
//                    graphics2d.setColor(Color.red);
//                    graphics2d.drawString("The King", 840, 650);
//                    graphics2d.drawString("is in checkK!", 840, 700);
//                }
//            } else {
//                graphics2d.drawString("Black's turn", 840, 250);
//                if(checkingPiece != null && checkingPiece.color == WHITE) {
//                    graphics2d.setColor(Color.red);
//                    graphics2d.drawString("The King", 840, 100);
//                    graphics2d.drawString("is in checkK!", 840, 150);
//                }
//            }
//        }

        if (gameOver) {
            String s = board.getSide() == NativeBoard.SIDE_BLACK ? "White won!" : "Black won!";
            graphics2d.setFont(new Font("Arial", Font.PLAIN, 90));
            graphics2d.setColor(Color.green);
            graphics2d.drawString(s, 200, 420);
        }

        if (stalemate) {
            graphics2d.setFont(new Font("Arial", Font.PLAIN, 90));
            graphics2d.setColor(Color.lightGray);
            graphics2d.drawString("Stalemate", 200, 420);
        }
    }
}
