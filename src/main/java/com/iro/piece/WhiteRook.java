package com.iro.piece;

import com.iro.board.PieceEnum;
import com.iro.board.SquareEnum;

public class WhiteRook extends Piece {
    public WhiteRook(SquareEnum square) {
        super(square);
        piece = PieceEnum.R;

        image = getImage("/piece/w-rook");
    }
}
