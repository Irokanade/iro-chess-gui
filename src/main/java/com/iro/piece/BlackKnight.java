package com.iro.piece;

import com.iro.board.PieceEnum;
import com.iro.board.SquareEnum;

public class BlackKnight extends Piece {
    public BlackKnight(SquareEnum square) {
        super(square);
        piece = PieceEnum.k;

        image = getImage("/piece/b-knight");
    }

    public BlackKnight(int col, int row) {
        super(col, row);
        piece = PieceEnum.k;

        image = getImage("/piece/b-knight");
    }
}
