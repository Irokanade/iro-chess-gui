package com.iro.gui;

import com.iro.board.Board;
import com.iro.board.BoardState;
import com.iro.board.MoveTypeEnum;
import com.iro.board.Moves;
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
    public final int FPS = 60;
    public Thread gameThread;
    private final Board board;
    public Mouse mouse = new Mouse();

    public static ArrayList<Piece> simPieces = new ArrayList<Piece>();
    public ArrayList<Piece> promoPieces = new ArrayList<Piece>();
    public Piece activePiece;

    public static Moves moveList = new Moves();
    public static Moves historyMoveList = new Moves();

    public int promotionMove;
    public boolean gameOver;
    public boolean stalemate;

    private boolean playAgainstComputer = true;
    public UciClient uciClient;

    public GamePanel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.BLACK);
        addMouseMotionListener(mouse);
        addMouseListener(mouse);

        board = new Board();
        copyPieces(board, simPieces);
        board.generateMoves(moveList);

        uciClient = new UciClient();
        try {
            uciClient.start();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
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
        if (promotionMove != 0) {
            promoting();
            return;
        }

        if (mouse.pressed) {
            if (activePiece == null) {
                for (Piece piece : simPieces) {
                    if (piece.getSquare() ==
                            SquareEnum.coordToSquare(mouse.x / Board.SQUARE_SIZE,
                                mouse.y / Board.SQUARE_SIZE)) {
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
                SquareEnum sourceSquare = board.getMoveSource(moveList.moves[i]);
                SquareEnum targetSquare = board.getMoveTarget(moveList.moves[i]);

                if (activePiece.getPreSquare() == sourceSquare &&
                    activePiece.getSquare() == targetSquare) {

                    if (board.getMovePromoted(moveList.moves[i]).ordinal() != 0) {
                        // handle promotion
                        BoardState boardState = board.copyBoard();
                        promotionMove = board.makeMove(moveList.moves[i], MoveTypeEnum.ALL_MOVES) ?
                            moveList.moves[i] : 0;
                        board.takeBack(boardState);

                        // setup promotion pieces ui
                        if (promotionMove != 0) {
                            promoPieces.clear();
                            if (board.getSide() == Board.SIDE_WHITE) {
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
                        }

                        return;
                    }

                    legalMove = board.makeMove(moveList.moves[i], MoveTypeEnum.ALL_MOVES);

                    if (legalMove) {
                        board.addMove(historyMoveList, moveList.moves[i]);

                        if (playAgainstComputer) {
                            String bestMove = uciClient.getBestMove(historyMoveList);
                            System.out.println(bestMove);
                            int engineMove = board.parseMove(bestMove.substring(9));
                            board.makeMove(engineMove, MoveTypeEnum.ALL_MOVES);
                            board.addMove(historyMoveList, engineMove);
                        }

                        board.generateMoves(moveList);
                        copyPieces(board, simPieces);

                        if (!board.hasLegalMoves(moveList)) {
                            if (board.isSquareAttacked(
                                    (board.getSide() == Board.SIDE_WHITE) ?
                                            board.getLsbIndex(board.getBitboards()[PieceEnum.K.ordinal()]):
                                            board.getLsbIndex(board.getBitboards()[PieceEnum.k.ordinal()]),
                            board.getSide()^1)) {
                                gameOver = true;
                            } else {
                                stalemate = true;
                            }
                        }
                    }
                }
            }

            if (!legalMove) {
                activePiece.setPosition(activePiece.getPreSquare());
            }

            activePiece = null;
        }
    }

    private void simulate() {
        // If a piece is being held, update its position
        activePiece.x = mouse.x - Board.HALF_SQUARE_SIZE;
        activePiece.y = mouse.y - Board.HALF_SQUARE_SIZE;
        activePiece.setSquare(activePiece.x, activePiece.y);
    }

    private void promoting() {
        if(mouse.pressed) {
            for(Piece piece : promoPieces) {
                if(piece.getCol() == mouse.x / Board.SQUARE_SIZE &&
                    piece.getRow() == mouse.y / Board.SQUARE_SIZE) {

                    int move = board.encodeMove(
                        board.getMoveSource(promotionMove).ordinal(),
                        board.getMoveTarget(promotionMove).ordinal(),
                        board.getMovePiece(promotionMove),
                        piece.getPiece().ordinal(),
                        board.getMoveCapture(promotionMove)? 1 : 0,
                        board.getMoveDouble(promotionMove)? 1 : 0,
                        board.getMoveEnpassant(promotionMove)? 1 : 0,
                        board.getMoveCastling(promotionMove)? 1 : 0
                    );

                    board.makeMove(move, MoveTypeEnum.ALL_MOVES);
                    board.addMove(historyMoveList, move);

                    if (playAgainstComputer) {
                        String bestMove = uciClient.getBestMove(historyMoveList);
                        System.out.println(bestMove);
                        int engineMove = board.parseMove(bestMove.substring(9));
                        board.makeMove(engineMove, MoveTypeEnum.ALL_MOVES);
                        board.addMove(historyMoveList, engineMove);
                    }

                    copyPieces(board, simPieces);
                    board.generateMoves(moveList);

                    promotionMove = 0;
                    activePiece = null;
                }
            }
        }
    }

    public void cleanup() {
        if (uciClient != null) {
            uciClient.close();
            uciClient = null;
        }

        if (gameThread != null) {
            gameThread = null;
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D graphics2d = (Graphics2D) g;

        // Board
        board.draw(graphics2d);

        // Pieces
        for (Piece p : simPieces) {
            p.draw(graphics2d);
        }
//
//        if (activePiece != null) {
//            if (canMove) {
//                if(isIllegal(activePiece) || opponentCanCaptureKing()) {
//                    graphics2d.setColor(Color.gray);
//                    graphics2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
//                    graphics2d.fillRect(activePiece.col*Board.SQUARE_SIZE, activePiece.row*Board.SQUARE_SIZE,
//                        Board.SQUARE_SIZE, Board.SQUARE_SIZE);
//                    graphics2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
//                } else {
//                    graphics2d.setColor(Color.white);
//                    graphics2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
//                    graphics2d.fillRect(activePiece.col*Board.SQUARE_SIZE, activePiece.row*Board.SQUARE_SIZE,
//                        Board.SQUARE_SIZE, Board.SQUARE_SIZE);
//                    graphics2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
//                }
//            }
//
//
//            // Draw the active piece in the end so it won't be hidden
//            // by the board or the colored square
//            activePiece.draw(graphics2d);
//        }
//
        // status messages
        graphics2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        graphics2d.setFont(new Font("Book Antique", Font.PLAIN, 40));
        graphics2d.setColor(Color.WHITE);

        if(promotionMove != 0) {
            graphics2d.drawString("Promoting to:", 840, 150);
            for(Piece piece : promoPieces) {
                graphics2d.drawImage(piece.getImage(), piece.getX(), piece.getY(),
                    Board.SQUARE_SIZE, Board.SQUARE_SIZE, null);
            }
        } else {
            if (board.getSide() == Board.SIDE_WHITE) {
                graphics2d.drawString("White's turn", 840, 550);
//                if(checkingPiece != null && checkingPiece.color == BLACK) {
//                    graphics2d.setColor(Color.red);
//                    graphics2d.drawString("The King", 840, 650);
//                    graphics2d.drawString("is in checkK!", 840, 700);
//                }
            } else {
                graphics2d.drawString("Black's turn", 840, 250);
//                if(checkingPiece != null && checkingPiece.color == WHITE) {
//                    graphics2d.setColor(Color.red);
//                    graphics2d.drawString("The King", 840, 100);
//                    graphics2d.drawString("is in checkK!", 840, 150);
//                }
            }
        }

        if(gameOver) {
            String s = "";
            if(board.getSide() == Board.SIDE_BLACK) {
                s = "White won!";
            } else {
                s = "Black won!";
            }
            graphics2d.setFont(new Font("Arial", Font.PLAIN, 90));
            graphics2d.setColor(Color.green);
            graphics2d.drawString(s, 200, 420);
        }

        if(stalemate) {
            graphics2d.setFont(new Font("Arial", Font.PLAIN, 90));
            graphics2d.setColor(Color.lightGray);
            graphics2d.drawString("Stalemate", 200, 420);
        }
    }
}
