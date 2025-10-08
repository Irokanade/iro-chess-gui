package com.iro.piece;

import com.iro.board.PieceEnum;
import com.iro.board.SquareEnum;

public class WhiteQueen extends Piece {
    public WhiteQueen(SquareEnum square) {
        super(square);
        piece = PieceEnum.Q;

        image = getImage("/piece/w-queen");
    }

    public WhiteQueen(int col, int row) {
        super(col, row);
        piece = PieceEnum.Q;

        image = getImage("/piece/w-queen");
    }
}
