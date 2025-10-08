package com.iro.piece;

import com.iro.board.PieceEnum;
import com.iro.board.SquareEnum;

public class WhiteKnight extends Piece {
    public WhiteKnight(SquareEnum square) {
        super(square);
        piece = PieceEnum.N;

        image = getImage("/piece/w-knight");
    }

    public WhiteKnight(int col, int row) {
        super(col, row);
        piece = PieceEnum.N;

        image = getImage("/piece/w-knight");
    }
}
