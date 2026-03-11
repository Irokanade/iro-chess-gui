package com.iro.board;

public class NativeMoveGen {
    static {
        System.loadLibrary("iro_chess_movegen");
    }

    // Position lifecycle
    public static native long createPosition();
    public static native void destroyPosition(long handle);

    // State
    public static native void setFen(long handle, String fen);
    public static native String getFen(long handle);
    public static native int getTurn(long handle);            // 0=WHITE, 1=BLACK
    public static native int pieceAt(long handle, int square); // C++ Piece enum values

    // Move generation — returns raw move ints: flags[15:12] | from[11:6] | to[5:0]
    public static native int[] generateLegalMoves(long handle);

    // Play/undo (pass the raw move int from generateLegalMoves)
    public static native void playMove(long handle, int move);
    public static native void undoMove(long handle, int move);

    // Check detection
    public static native boolean isInCheck(long handle);

    // Position info
    public static native long getHash(long handle);
    public static native int getPly(long handle);
}
