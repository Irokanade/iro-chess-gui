package com.iro.piece;

import com.iro.board.PieceEnum;
import com.iro.board.SquareEnum;

public class BlackPawn extends Piece {
    public BlackPawn(SquareEnum square) {
        super(square);
        piece = PieceEnum.p;

        image = getImage("/piece/b-pawn");
    }
}
