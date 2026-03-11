package com.iro.board;

public class Moves {
    public int[] moves;
    public int count;

    public Moves() {
        moves = new int[256];
        count = 0;
    }

    // C++ move format: flags[15:12] | from[11:6] | to[5:0]
    public static SquareEnum getMoveSource(int move) {
        return SquareEnum.intToSquare((move >> 6) & 0x3f);
    }

    public static SquareEnum getMoveTarget(int move) {
        return SquareEnum.intToSquare(move & 0x3f);
    }

    // Move flags (4 bits):
    // 0b0000 = quiet          0b1000 = capture
    // 0b0001 = double push    0b1010 = en passant
    // 0b0010 = OO             0b1100 = PC_KNIGHT
    // 0b0011 = OOO            0b1101 = PC_BISHOP
    // 0b0100 = PR_KNIGHT      0b1110 = PC_ROOK
    // 0b0101 = PR_BISHOP      0b1111 = PC_QUEEN
    // 0b0110 = PR_ROOK
    // 0b0111 = PR_QUEEN
    public static int getMoveFlags(int move) {
        return (move >> 12) & 0xf;
    }

    public static boolean isPromotion(int move) {
        int flags = getMoveFlags(move);
        return (flags & 0b0100) != 0;
    }

    public static boolean isPromotionCapture(int move) {
        int flags = getMoveFlags(move);
        return (flags & 0b1100) == 0b1100;
    }

    // Returns promotion piece type: 0=knight, 1=bishop, 2=rook, 3=queen
    public static int getPromotionPieceType(int move) {
        return getMoveFlags(move) & 0b0011;
    }

    public static String toUci(int move) {
        SquareEnum from = getMoveSource(move);
        SquareEnum to = getMoveTarget(move);
        String uci = from.name().toLowerCase() + to.name().toLowerCase();
        if (isPromotion(move) || isPromotionCapture(move)) {
            String[] promoChars = {"n", "b", "r", "q"};
            uci += promoChars[getPromotionPieceType(move)];
        }
        return uci;
    }
}
