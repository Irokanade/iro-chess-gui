package com.iro.board;

import java.util.Arrays;

public class BoardState {
    long[] bitboards_copy;
    long[] occupancies_copy;
    int side_copy;
    SquareEnum enpassant_copy;
    int castle_copy;

    public BoardState(long[] bitboards,
                      long[] occupancies,
                      int side,
                      SquareEnum enpassant,
                      int castle) {
        this.bitboards_copy = Arrays.copyOf(bitboards, 12);
        this.occupancies_copy = Arrays.copyOf(occupancies, 3);
        this.side_copy = side;
        this.enpassant_copy = enpassant;
        this.castle_copy = castle;
    }
}
