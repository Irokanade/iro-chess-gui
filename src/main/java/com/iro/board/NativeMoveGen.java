package com.iro.board;

public final class NativeMoveGen {
    private NativeMoveGen() {}

    static {
        System.loadLibrary("iro_chess_movegen");
    }

    // Position lifecycle
    public static native long createPosition();
    public static native void destroyPosition(long positionPtr);

    // State
    public static native void setFen(long positionPtr, String fen);
    public static native String getFen(long positionPtr);
    public static native int getTurn(long positionPtr);            // 0=WHITE, 1=BLACK
    public static native int pieceAt(long positionPtr, int square); // C++ Piece enum values

    // Move generation — returns raw move ints: flags[15:12] | from[11:6] | to[5:0]
    public static native int[] generateLegalMoves(long positionPtr);

    // Play/undo (pass the raw move int from generateLegalMoves)
    public static native void playMove(long positionPtr, int move);
    public static native void undoMove(long positionPtr, int move);

    // Check detection
    public static native boolean isInCheck(long positionPtr);

    // Position info
    public static native long getHash(long positionPtr);
    public static native int getPly(long positionPtr);
}
