package com.iro.piece;

import com.iro.board.PieceEnum;
import com.iro.board.SquareEnum;

public class BlackKing extends Piece {
    public BlackKing(SquareEnum square) {
        super(square);
        piece = PieceEnum.k;

        image = getImage("/piece/b-king");
    }
}
