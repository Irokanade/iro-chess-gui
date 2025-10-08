package com.iro.piece;

import com.iro.board.PieceEnum;
import com.iro.board.SquareEnum;

public class BlackRook extends Piece {
    public BlackRook(SquareEnum square) {
        super(square);
        piece = PieceEnum.r;

        image = getImage("/piece/b-rook");
    }

    public BlackRook(int col, int row) {
        super(col, row);
        piece = PieceEnum.r;

        image = getImage("/piece/b-rook");
    }
}
