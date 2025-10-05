package com.iro.piece;

import com.iro.board.PieceEnum;
import com.iro.board.SquareEnum;

public class PieceFactory {
    public Piece createPiece(PieceEnum piece, SquareEnum square) {
        switch (piece) {
            case PieceEnum.P:
                return new WhitePawn(square);
            case PieceEnum.N:
                return new WhiteKnight(square);
            case PieceEnum.B:
                return new WhiteBishop(square);
            case PieceEnum.R:
                return new WhiteRook(square);
            case PieceEnum.Q:
                return new WhiteQueen(square);
            case PieceEnum.K:
                return new WhiteKing(square);
            case PieceEnum.p:
                return new BlackPawn(square);
            case PieceEnum.n:
                return new BlackKnight(square);
            case PieceEnum.b:
                return new BlackBishop(square);
            case PieceEnum.r:
                return new BlackRook(square);
            case PieceEnum.q:
                return new BlackQueen(square);
            case PieceEnum.k:
                return new BlackKing(square);
            default:
                return null;
        }
    }
}
