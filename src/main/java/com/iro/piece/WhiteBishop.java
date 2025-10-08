package com.iro.piece;

import com.iro.board.PieceEnum;
import com.iro.board.SquareEnum;

public class WhiteBishop extends Piece {
    public WhiteBishop(SquareEnum square) {
        super(square);
        piece = PieceEnum.B;

        image = getImage("/piece/w-bishop");
    }

    public WhiteBishop(int col, int row) {
        super(col, row);
        piece = PieceEnum.B;

        image = getImage("/piece/w-bishop");
    }
}
