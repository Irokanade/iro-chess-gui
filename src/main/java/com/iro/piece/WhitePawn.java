package com.iro.piece;

import com.iro.board.PieceEnum;
import com.iro.board.SquareEnum;

public class WhitePawn extends Piece {
    public WhitePawn(SquareEnum square) {
        super(square);
        piece = PieceEnum.P;

        image = getImage("/piece/w-pawn");
    }
}
