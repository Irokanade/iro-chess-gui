package com.iro.piece;

import com.iro.board.PieceEnum;
import com.iro.board.SquareEnum;

public class WhiteKing extends Piece {
    public WhiteKing(SquareEnum square) {
        super(square);
        piece = PieceEnum.K;

        image = getImage("/piece/w-king");
    }
}
