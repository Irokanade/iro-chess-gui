package com.iro.board;

public class Moves {
    public int[] moves;
    public int count;

    public Moves() {
        moves = new int[256];
        count = 0;
    }

    public static SquareEnum getMoveSource(int move) {
        return SquareEnum.intToSquare(move & 0x3f);
    }

    public static SquareEnum getMoveTarget(int move) {
        return SquareEnum.intToSquare((move & 0xfc0) >>> 6);
    }

    public static PieceEnum getMovePromoted(int move) {
        return PieceEnum.intToPiece((move & 0xf0000) >>> 16);
    }
}
