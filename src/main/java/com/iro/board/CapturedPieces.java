package com.iro.board;

public record CapturedPieces(PieceEnum[][] pieces, int[] count) {
    public CapturedPieces() {
        this(new PieceEnum[2][15], new int[]{0, 0});
    }

    public void add(int side, PieceEnum piece) {
        pieces[side][count[side]++] = piece;
    }
}
