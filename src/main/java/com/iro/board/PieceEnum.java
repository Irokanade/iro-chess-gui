package com.iro.board;

public enum PieceEnum {
    P, N, B, R, Q, K, p, n, b, r, q, k;

    private static final PieceEnum[] VALUES = values();

    public static PieceEnum intToPiece(int index) {
        if (index < 0 || index >= VALUES.length) {
            return P;
        }
        return VALUES[index];
    }
}
