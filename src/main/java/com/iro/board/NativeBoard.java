package com.iro.board;

public class NativeBoard {

    public static final int SIDE_WHITE = 0;
    public static final int SIDE_BLACK = 1;

    public static final String START_POSITION =
        "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1 ";

    private long positionPtr;

    // C++ Piece enum → Java PieceEnum mapping
    // C++: WHITE_PAWN=0..WHITE_KING=5, BLACK_PAWN=8..BLACK_KING=13, NO_PIECE=14
    // Java: P=0..K=5, p=6..k=11
    private static final PieceEnum[] CPP_TO_JAVA_PIECE = {
        PieceEnum.P, PieceEnum.N, PieceEnum.B, PieceEnum.R, PieceEnum.Q, PieceEnum.K,
        null, null,
        PieceEnum.p, PieceEnum.n, PieceEnum.b, PieceEnum.r, PieceEnum.q, PieceEnum.k,
        null
    };

    public void createPosition() {
        positionPtr = NativeMoveGen.createPosition();
    }

    public void destroyPosition() {
        if (positionPtr != 0) {
            NativeMoveGen.destroyPosition(positionPtr);
            positionPtr = 0;
        }
    }

    public void setFen(String fen) {
        NativeMoveGen.setFen(positionPtr, fen);
    }

    public String getFen() {
        return NativeMoveGen.getFen(positionPtr);
    }

    public int getSide() {
        return NativeMoveGen.getTurn(positionPtr);
    }

    public void generateMoves(Moves moveList) {
        int[] raw = NativeMoveGen.generateLegalMoves(positionPtr);
        moveList.count = raw.length;
        System.arraycopy(raw, 0, moveList.moves, 0, raw.length);
    }

    public void makeMove(int move) {
        NativeMoveGen.playMove(positionPtr, move);
    }

    public boolean isInCheck() {
        return NativeMoveGen.isInCheck(positionPtr);
    }

    public boolean hasLegalMoves(Moves moveList) {
        return moveList.count > 0;
    }

    public PieceEnum pieceAt(int square) {
        int cppPiece = NativeMoveGen.pieceAt(positionPtr, square);
        if (cppPiece < 0 || cppPiece >= CPP_TO_JAVA_PIECE.length) {
            return null;
        }
        return CPP_TO_JAVA_PIECE[cppPiece];
    }

    public int parseMove(String uci) {
        int from = (uci.charAt(0) - 'a') + (uci.charAt(1) - '1') * 8;
        int to   = (uci.charAt(2) - 'a') + (uci.charAt(3) - '1') * 8;

        int[] moves = NativeMoveGen.generateLegalMoves(positionPtr);
        for (int move : moves) {
            if ((move & 0x3f) == to && ((move >> 6) & 0x3f) == from) {
                // if move has promotion
                if (uci.length() == 5) {
                    int promoType = Moves.getPromotionPieceType(move);
                    char promo = uci.charAt(4);
                    if (promo == 'n' && promoType == 0) return move;
                    if (promo == 'b' && promoType == 1) return move;
                    if (promo == 'r' && promoType == 2) return move;
                    if (promo == 'q' && promoType == 3) return move;
                    continue;
                }
                return move;
            }
        }
        return 0;
    }

    public void addMove(Moves moveList, int move) {
        moveList.moves[moveList.count++] = move;
    }
}
