#include <jni.h>
#include "position.h"
#include "tables.h"
#include <string>

static bool initialized = false;

static void ensure_init() {
    if (!initialized) {
        initialise_all_databases();
        zobrist::initialise_zobrist_keys();
        initialized = true;
    }
}

extern "C" {

// Position lifecycle

JNIEXPORT jlong JNICALL Java_com_iro_board_NativeMoveGen_createPosition(
    JNIEnv *env, jclass cls)
{
    ensure_init();
    return reinterpret_cast<jlong>(new Position());
}

JNIEXPORT void JNICALL Java_com_iro_board_NativeMoveGen_destroyPosition(
    JNIEnv *env, jclass cls, jlong handle)
{
    delete reinterpret_cast<Position*>(handle);
}

// State

JNIEXPORT void JNICALL Java_com_iro_board_NativeMoveGen_setFen(
    JNIEnv *env, jclass cls, jlong handle, jstring fen_str)
{
    Position *pos = reinterpret_cast<Position*>(handle);
    const char *fen_cstr = env->GetStringUTFChars(fen_str, nullptr);
    Position::set(std::string(fen_cstr), *pos);
    env->ReleaseStringUTFChars(fen_str, fen_cstr);
}

JNIEXPORT jstring JNICALL Java_com_iro_board_NativeMoveGen_getFen(
    JNIEnv *env, jclass cls, jlong handle)
{
    Position *pos = reinterpret_cast<Position*>(handle);
    return env->NewStringUTF(pos->fen().c_str());
}

// Returns side to play: 0 = WHITE, 1 = BLACK
JNIEXPORT jint JNICALL Java_com_iro_board_NativeMoveGen_getTurn(
    JNIEnv *env, jclass cls, jlong handle)
{
    Position *pos = reinterpret_cast<Position*>(handle);
    return static_cast<jint>(pos->turn());
}

// Returns piece at square (C++ Piece enum values, 14 = NO_PIECE)
JNIEXPORT jint JNICALL Java_com_iro_board_NativeMoveGen_pieceAt(
    JNIEnv *env, jclass cls, jlong handle, jint square)
{
    Position *pos = reinterpret_cast<Position*>(handle);
    return static_cast<jint>(pos->at(static_cast<Square>(square)));
}

// Move generation — returns raw move uint16_t values as int[]
// Each move encodes: flags[15:12] | from[11:6] | to[5:0]
JNIEXPORT jintArray JNICALL Java_com_iro_board_NativeMoveGen_generateLegalMoves(
    JNIEnv *env, jclass cls, jlong handle)
{
    Position *pos = reinterpret_cast<Position*>(handle);

    Move moves[218];
    Move *end;

    if (pos->turn() == WHITE) {
        end = pos->generate_legals<WHITE>(moves);
    } else {
        end = pos->generate_legals<BLACK>(moves);
    }

    int count = static_cast<int>(end - moves);
    jintArray result = env->NewIntArray(count);
    jint buf[218];
    for (int i = 0; i < count; i++) {
        buf[i] = static_cast<jint>(moves[i].to_from());
    }

    env->SetIntArrayRegion(result, 0, count, buf);
    return result;
}

// Play a move (pass the raw uint16_t move value)
JNIEXPORT void JNICALL Java_com_iro_board_NativeMoveGen_playMove(
    JNIEnv *env, jclass cls, jlong handle, jint move)
{
    Position *pos = reinterpret_cast<Position*>(handle);
    Move m(static_cast<uint16_t>(move));

    if (pos->turn() == WHITE) {
        pos->play<WHITE>(m);
    } else {
        pos->play<BLACK>(m);
    }
}

// Undo a move
JNIEXPORT void JNICALL Java_com_iro_board_NativeMoveGen_undoMove(
    JNIEnv *env, jclass cls, jlong handle, jint move)
{
    Position *pos = reinterpret_cast<Position*>(handle);
    Move m(static_cast<uint16_t>(move));

    // After play, turn has flipped — undo with the opposite of current turn
    if (pos->turn() == WHITE) {
        pos->undo<BLACK>(m);
    } else {
        pos->undo<WHITE>(m);
    }
}

// Check if current side is in check
JNIEXPORT jboolean JNICALL Java_com_iro_board_NativeMoveGen_isInCheck(
    JNIEnv *env, jclass cls, jlong handle)
{
    Position *pos = reinterpret_cast<Position*>(handle);
    if (pos->turn() == WHITE) {
        return static_cast<jboolean>(pos->in_check<WHITE>());
    } else {
        return static_cast<jboolean>(pos->in_check<BLACK>());
    }
}

// Returns the zobrist hash of the position
JNIEXPORT jlong JNICALL Java_com_iro_board_NativeMoveGen_getHash(
    JNIEnv *env, jclass cls, jlong handle)
{
    Position *pos = reinterpret_cast<Position*>(handle);
    return static_cast<jlong>(pos->get_hash());
}

// Returns the current game ply
JNIEXPORT jint JNICALL Java_com_iro_board_NativeMoveGen_getPly(
    JNIEnv *env, jclass cls, jlong handle)
{
    Position *pos = reinterpret_cast<Position*>(handle);
    return static_cast<jint>(pos->ply());
}

} // extern "C"
