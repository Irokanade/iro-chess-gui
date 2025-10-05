package com.iro.piece;

import com.iro.board.PieceEnum;
import com.iro.board.SquareEnum;

import java.awt.*;

public class WhiteBishop extends Piece {
    public WhiteBishop(SquareEnum square) {
        super(square);
        piece = PieceEnum.B;

        image = getImage("/piece/w-bishop");
    }
}
