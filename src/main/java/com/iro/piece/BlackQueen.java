package com.iro.piece;

import com.iro.board.PieceEnum;
import com.iro.board.SquareEnum;

public class BlackQueen extends Piece {
    public BlackQueen(SquareEnum square) {
        super(square);
        piece = PieceEnum.q;

        image = getImage("/piece/b-queen");
    }

    public BlackQueen(int col, int row) {
        super(col, row);
        piece = PieceEnum.q;

        image = getImage("/piece/b-queen");
    }
}
