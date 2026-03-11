package com.iro.board;

public final class NativeBoard {

    private NativeBoard() {}

    private static long positionPtr;

    public static void createPosition() {
        positionPtr = NativeMoveGen.createPosition();
    }

    public static void destroyPosition() {
        NativeMoveGen.destroyPosition(positionPtr);
    }

    public static void setFen(String fen) {
        NativeMoveGen.setFen(positionPtr, fen);
    }

    public static void generateMoves(Moves moveList) {
        int[] raw = NativeMoveGen.generateLegalMoves(positionPtr);
        moveList.count = raw.length;
        System.arraycopy(raw, 0, moveList.moves, 0, raw.length);
    }

    public static boolean makeMove(int move) {
        NativeMoveGen.playMove(positionPtr, move);
        return true;
    }

    public static boolean isInCheck() {
        return NativeMoveGen.isInCheck(positionPtr);
    }

    public static int getSide() {
        return NativeMoveGen.getTurn(positionPtr);  // 0=WHITE, 1=BLACK
    }

    public static int parseMove(String uci) {
        int from = (uci.charAt(0) - 'a') + (uci.charAt(1) - '1') * 8;
        int to   = (uci.charAt(2) - 'a') + (uci.charAt(3) - '1') * 8;

        // Find matching legal move
        int[] moves = NativeMoveGen.generateLegalMoves(positionPtr);
        for (int move : moves) {
            if ((move & 0x3f) == to && ((move >> 6) & 0x3f) == from) {
                // If promotion, match the piece
                if (uci.length() == 5) {
                    int flags = (move >> 12) & 0xf;
                    char promo = uci.charAt(4);
                    if (promo == 'n' && (flags & 0b0011) == 0b0000) return move;
                    if (promo == 'b' && (flags & 0b0011) == 0b0001) return move;
                    if (promo == 'r' && (flags & 0b0011) == 0b0010) return move;
                    if (promo == 'q' && (flags & 0b0011) == 0b0011) return move;
                    continue;
                }
                return move;
            }
        }
        return 0;
    }
}
