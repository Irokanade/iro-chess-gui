package com.iro.piece;

import com.iro.board.PieceEnum;
import com.iro.board.SquareEnum;

public class BlackBishop extends Piece {
    public BlackBishop(SquareEnum square) {
        super(square);
        piece = PieceEnum.b;

        image = getImage("/piece/b-bishop");
    }

    public BlackBishop(int col, int row) {
        super(col, row);
        piece = PieceEnum.b;

        image = getImage("/piece/b-bishop");
    }
}
