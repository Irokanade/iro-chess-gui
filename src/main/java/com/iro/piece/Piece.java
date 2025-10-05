package com.iro.piece;

import com.iro.board.Board;
import com.iro.board.PieceEnum;
import com.iro.board.SquareEnum;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Objects;

public class Piece {

    protected PieceEnum piece;
    protected BufferedImage image;
    private SquareEnum square;
    public int x;
    public int y;
    private SquareEnum preSquare;
    public Piece hittingPiece;
    public boolean moved;
    public boolean twoStepped;

    public Piece(SquareEnum square) {
        PieceEnum piece = null;
        this.square = square;
        x = getX(square);
        y = getY(square);
        preSquare = square;
        moved = false;
    }

    public BufferedImage getImage(String imagePath) {
        BufferedImage image = null;
        try {
            image = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(imagePath + ".png")));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return image;
    }

    public int getX(SquareEnum square) {
        int col = square.ordinal() % 8;
        return col * Board.SQUARE_SIZE;
    }

    public int getY(SquareEnum square) {
        int row = square.ordinal() / 8;
        return row * Board.SQUARE_SIZE;
    }

    public int getCol(int x) {
        return (x + Board.HALF_SQUARE_SIZE)/ Board.SQUARE_SIZE;
    }

    public int getRow(int y) {
        return (y + Board.HALF_SQUARE_SIZE)/ Board.SQUARE_SIZE;
    }

    public PieceEnum getPiece() {
        return piece;
    }

    public SquareEnum getSquare() {
        return square;
    }

    public void setSquare(int x, int y) {
        square = SquareEnum.intToSquare(getRow(y) * 8 + getCol(x));
    }

    public void setPosition(SquareEnum square) {
        this.square = square;
        this.x = getX(square);
        this.y = getY(square);
    }

    public SquareEnum getPreSquare() {
        return preSquare;
    }

    public void setPreSquare(SquareEnum preSquare) {
        this.preSquare = preSquare;
    }

    public int getColor() {
        switch (piece) {
            case PieceEnum.P:
            case PieceEnum.N:
            case PieceEnum.B:
            case PieceEnum.R:
            case PieceEnum.Q:
            case PieceEnum.K:
                return Board.SIDE_WHITE;
            case PieceEnum.p:
            case PieceEnum.b:
            case PieceEnum.r:
            case PieceEnum.q:
            case PieceEnum.k:
                return Board.SIDE_BLACK;
            default:
                return Board.SIDE_BOTH;
        }
    }

    public void draw(Graphics2D graphics2d) {
        graphics2d.drawImage(image, x, y, Board.SQUARE_SIZE, Board.SQUARE_SIZE, null);
    }
}
