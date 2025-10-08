package com.iro.board;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Board {

    private final long[] bitboards;
    private final long[][] pawnAttacks;
    private final long[] knightAttacks;
    private final long[] kingAttacks;
    private final long[] bishopMasks;
    private final long[] rookMasks;
    private final long[][] bishopAttacks;
    private final long[][] rookAttacks;
    private final long[] occupancies;

    private int side;
    private SquareEnum enpassant;
    private int castle;

    // castling rights masks
    private static final int WK = 1;
    private static final int WQ = 2;
    private static final int BK = 4;
    private static final int BQ = 8;

    public static final int SIDE_WHITE = 0;
    public static final int SIDE_BLACK = 1;
    public static final int SIDE_BOTH = 2;

    private static final long NOT_A_FILE = 0xFEFEFEFEFEFEFEFEL;
    private static final long NOT_H_FILE = 0x7F7F7F7F7F7F7F7FL;
    private static final long NOT_HG_FILE = 0x3F3F3F3F3F3F3F3FL;
    private static final long NOT_AB_FILE = 0xFCFCFCFCFCFCFCFCL;

    private static final Map<Character, PieceEnum> CHAR_PIECES = new HashMap<>();
    static {
        CHAR_PIECES.put('P', com.iro.board.PieceEnum.P);
        CHAR_PIECES.put('N', com.iro.board.PieceEnum.N);
        CHAR_PIECES.put('B', com.iro.board.PieceEnum.B);
        CHAR_PIECES.put('R', com.iro.board.PieceEnum.R);
        CHAR_PIECES.put('Q', com.iro.board.PieceEnum.Q);
        CHAR_PIECES.put('K', com.iro.board.PieceEnum.K);
        CHAR_PIECES.put('p', com.iro.board.PieceEnum.p);
        CHAR_PIECES.put('n', com.iro.board.PieceEnum.n);
        CHAR_PIECES.put('b', com.iro.board.PieceEnum.b);
        CHAR_PIECES.put('r', com.iro.board.PieceEnum.r);
        CHAR_PIECES.put('q', com.iro.board.PieceEnum.q);
        CHAR_PIECES.put('k', com.iro.board.PieceEnum.k);
    }

    private static final int[] CASTLING_RIGHTS = {
        7, 15, 15, 15,  3, 15, 15, 11,
        15, 15, 15, 15, 15, 15, 15, 15,
        15, 15, 15, 15, 15, 15, 15, 15,
        15, 15, 15, 15, 15, 15, 15, 15,
        15, 15, 15, 15, 15, 15, 15, 15,
        15, 15, 15, 15, 15, 15, 15, 15,
        15, 15, 15, 15, 15, 15, 15, 15,
        13, 15, 15, 15, 12, 15, 15, 14
    };

    private static final int[] BISHOP_RELEVANT_BITS = {
        6, 5, 5, 5, 5, 5, 5, 6,
        5, 5, 5, 5, 5, 5, 5, 5,
        5, 5, 7, 7, 7, 7, 5, 5,
        5, 5, 7, 9, 9, 7, 5, 5,
        5, 5, 7, 9, 9, 7, 5, 5,
        5, 5, 7, 7, 7, 7, 5, 5,
        5, 5, 5, 5, 5, 5, 5, 5,
        6, 5, 5, 5, 5, 5, 5, 6
    };

    private static final int[] ROOK_RELEVANT_BITS = {
        12, 11, 11, 11, 11, 11, 11, 12,
        11, 10, 10, 10, 10, 10, 10, 11,
        11, 10, 10, 10, 10, 10, 10, 11,
        11, 10, 10, 10, 10, 10, 10, 11,
        11, 10, 10, 10, 10, 10, 10, 11,
        11, 10, 10, 10, 10, 10, 10, 11,
        11, 10, 10, 10, 10, 10, 10, 11,
        12, 11, 11, 11, 11, 11, 11, 12
    };

    private static final long[] ROOK_MAGIC_NUMBERS = {
        0x8a80104000800020L,
        0x140002000100040L,
        0x2801880a0017001L,
        0x100081001000420L,
        0x200020010080420L,
        0x3001c0002010008L,
        0x8480008002000100L,
        0x2080088004402900L,
        0x800098204000L,
        0x2024401000200040L,
        0x100802000801000L,
        0x120800800801000L,
        0x208808088000400L,
        0x2802200800400L,
        0x2200800100020080L,
        0x801000060821100L,
        0x80044006422000L,
        0x100808020004000L,
        0x12108a0010204200L,
        0x140848010000802L,
        0x481828014002800L,
        0x8094004002004100L,
        0x4010040010010802L,
        0x20008806104L,
        0x100400080208000L,
        0x2040002120081000L,
        0x21200680100081L,
        0x20100080080080L,
        0x2000a00200410L,
        0x20080800400L,
        0x80088400100102L,
        0x80004600042881L,
        0x4040008040800020L,
        0x440003000200801L,
        0x4200011004500L,
        0x188020010100100L,
        0x14800401802800L,
        0x2080040080800200L,
        0x124080204001001L,
        0x200046502000484L,
        0x480400080088020L,
        0x1000422010034000L,
        0x30200100110040L,
        0x100021010009L,
        0x2002080100110004L,
        0x202008004008002L,
        0x20020004010100L,
        0x2048440040820001L,
        0x101002200408200L,
        0x40802000401080L,
        0x4008142004410100L,
        0x2060820c0120200L,
        0x1001004080100L,
        0x20c020080040080L,
        0x2935610830022400L,
        0x44440041009200L,
        0x280001040802101L,
        0x2100190040002085L,
        0x80c0084100102001L,
        0x4024081001000421L,
        0x20030a0244872L,
        0x12001008414402L,
        0x2006104900a0804L,
        0x1004081002402L
    };

    private static final long[] BISHOP_MAGIC_NUMBERS = {
        0x40040844404084L,
        0x2004208a004208L,
        0x10190041080202L,
        0x108060845042010L,
        0x581104180800210L,
        0x2112080446200010L,
        0x1080820820060210L,
        0x3c0808410220200L,
        0x4050404440404L,
        0x21001420088L,
        0x24d0080801082102L,
        0x1020a0a020400L,
        0x40308200402L,
        0x4011002100800L,
        0x401484104104005L,
        0x801010402020200L,
        0x400210c3880100L,
        0x404022024108200L,
        0x810018200204102L,
        0x4002801a02003L,
        0x85040820080400L,
        0x810102c808880400L,
        0xe900410884800L,
        0x8002020480840102L,
        0x220200865090201L,
        0x2010100a02021202L,
        0x152048408022401L,
        0x20080002081110L,
        0x4001001021004000L,
        0x800040400a011002L,
        0xe4004081011002L,
        0x1c004001012080L,
        0x8004200962a00220L,
        0x8422100208500202L,
        0x2000402200300c08L,
        0x8646020080080080L,
        0x80020a0200100808L,
        0x2010004880111000L,
        0x623000a080011400L,
        0x42008c0340209202L,
        0x209188240001000L,
        0x400408a884001800L,
        0x110400a6080400L,
        0x1840060a44020800L,
        0x90080104000041L,
        0x201011000808101L,
        0x1a2208080504f080L,
        0x8012020600211212L,
        0x500861011240000L,
        0x180806108200800L,
        0x4000020e01040044L,
        0x300000261044000aL,
        0x802241102020002L,
        0x20906061210001L,
        0x5a84841004010310L,
        0x4010801011c04L,
        0xa010109502200L,
        0x4a02012000L,
        0x500201010098b028L,
        0x8040002811040900L,
        0x28000010020204L,
        0x6000020202d0240L,
        0x8918844842082200L,
        0x4010011029020020L
    };

    public static final String START_POSITION =
        "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1 ";

    public Board() {
        bitboards = new long[12];
        pawnAttacks = new long[2][64];
        knightAttacks = new long[64];
        kingAttacks = new long[64];
        bishopMasks = new long[64];
        rookMasks = new long[64];
        bishopAttacks = new long[64][512];
        rookAttacks = new long[64][4096];
        occupancies = new long[3];

        side = SIDE_WHITE;
        enpassant = SquareEnum.NO_SQUARE;

        initLeaperAttacks();
        initSlidersAttacks(true);
        initSlidersAttacks(false);

        parseFen(START_POSITION);
    }

    private long setBit(long bitboard, SquareEnum square) {
        return bitboard | (1L << square.ordinal());
    }

    public long popBit(long bitboard, SquareEnum square) {
        return bitboard & ~(1L << square.ordinal());
    }

    private long getBit(long bitboard, SquareEnum square) {
        return bitboard & (1L << square.ordinal());
    }

    public BoardState copyBoard() {
        return new BoardState(bitboards, occupancies, side, enpassant, castle);
    }

    public void takeBack(BoardState state) {
        System.arraycopy(state.bitboards_copy, 0, bitboards, 0, 12);
        System.arraycopy(state.occupancies_copy, 0, occupancies, 0, 3);
        side = state.side_copy;
        enpassant = state.enpassant_copy;
        castle = state.castle_copy;
    }

    private int countBits(long bitboard) {
        int count = 0;

        while (bitboard != 0) {
            ++count;

            bitboard &= bitboard - 1;
        }

        return count;
    }

    public int getLsbIndex(long bitboard) {
        if (bitboard != 0) {
            return countBits((bitboard & -bitboard) - 1);
        } else {
            return -1;
        }
    }

    private long maskPawnAttacks(int side, SquareEnum square) {
        long attacks = 0;
        long bitboard = 0;

        bitboard = setBit(bitboard, square);

        if (side == SIDE_WHITE) {
            if (((bitboard >>> 7) & NOT_A_FILE) != 0) {
                attacks |= (bitboard >>> 7);
            }
            if (((bitboard >>> 9) & NOT_H_FILE) != 0) {
                attacks |= (bitboard >>> 9);
            }
        } else {
            if (((bitboard << 7) & NOT_H_FILE) != 0) {
                attacks |= (bitboard << 7);
            }
            if (((bitboard << 9) & NOT_A_FILE) != 0) {
                attacks |= (bitboard << 9);
            }
        }

        return attacks;
    }

    private long maskKnightAttacks(SquareEnum square) {
        long attacks = 0;
        long bitboard = 0;

        bitboard = setBit(bitboard, square);

        if (((bitboard >>> 17) & NOT_H_FILE) != 0) {
            attacks |= (bitboard >>> 17);
        }
        if (((bitboard >>> 15) & NOT_A_FILE) != 0) {
            attacks |= (bitboard >>> 15);
        }
        if (((bitboard >>> 10) & NOT_HG_FILE) != 0) {
            attacks |= (bitboard >>> 10);
        }
        if (((bitboard >>> 6) & NOT_AB_FILE) != 0) {
            attacks |= (bitboard >>> 6);
        }
        if (((bitboard << 17) & NOT_A_FILE) != 0) {
            attacks |= (bitboard << 17);
        }
        if (((bitboard << 15) & NOT_H_FILE) != 0) {
            attacks |= (bitboard << 15);
        }
        if (((bitboard << 10) & NOT_AB_FILE) != 0) {
            attacks |= (bitboard << 10);
        }
        if (((bitboard << 6) & NOT_HG_FILE) != 0) {
            attacks |= (bitboard << 6);
        }

        return attacks;
    }

    private long maskKingAttacks(SquareEnum square) {
        long attacks = 0;
        long bitboard = 0;

        bitboard = setBit(bitboard, square);

        if ((bitboard >>> 8) != 0) {
            attacks |= (bitboard >>> 8);
        }
        if (((bitboard >>> 9) & NOT_H_FILE) != 0) {
            attacks |= (bitboard >>> 9);
        }
        if (((bitboard >>> 7) & NOT_A_FILE) != 0) {
            attacks |= (bitboard >>> 7);
        }
        if (((bitboard >>> 1) & NOT_H_FILE) != 0) {
            attacks |= (bitboard >>> 1);
        }
        if ((bitboard << 8) != 0) {
            attacks |= (bitboard << 8);
        }
        if (((bitboard << 9) & NOT_A_FILE) != 0) {
            attacks |= (bitboard << 9);
        }
        if (((bitboard << 7) & NOT_H_FILE) != 0) {
            attacks |= (bitboard << 7);
        }
        if (((bitboard << 1) & NOT_A_FILE) != 0) {
            attacks |= (bitboard << 1);
        }

        return attacks;
    }

    private long maskBishopAttacks(SquareEnum square) {
        long attacks = 0;

        int rank;
        int file;
        int targetRank = square.ordinal() / 8;
        int targetFile = square.ordinal() % 8;

        for (rank = targetRank + 1, file = targetFile + 1; rank <= 6 && file <= 6; rank++, file++) {
            attacks |= (1L << (rank * 8 + file));
        }
        for (rank = targetRank - 1, file = targetFile + 1; rank >= 1 && file <= 6; rank--, file++) {
            attacks |= (1L << (rank * 8 + file));
        }
        for (rank = targetRank + 1, file = targetFile - 1; rank <= 6 && file >= 1; rank++, file--) {
            attacks |= (1L << (rank * 8 + file));
        }
        for (rank = targetRank - 1, file = targetFile - 1; rank >= 1 && file >= 1; rank--, file--) {
            attacks |= (1L << (rank * 8 + file));
        }

        return attacks;
    }

    private long maskRookAttacks(SquareEnum square) {
        long attacks = 0;

        int rank;
        int file;
        int targetRank = square.ordinal() / 8;
        int targetFile = square.ordinal() % 8;

        for (rank = targetRank + 1; rank <= 6; rank++) {
            attacks |= (1L << (rank * 8 + targetFile));
        }
        for (rank = targetRank - 1; rank >= 1; rank--) {
            attacks |= (1L << (rank * 8 + targetFile));
        }
        for (file = targetFile + 1; file <= 6; file++) {
            attacks |= (1L << (targetRank * 8 + file));
        }
        for (file = targetFile - 1; file >= 1; file--) {
            attacks |= (1L << (targetRank * 8 + file));
        }

        return attacks;
    }

    private long bishopAttacksOnTheFly(SquareEnum square, long block) {
        long attacks = 0;

        int rank;
        int file;
        int targetRank = square.ordinal() / 8;
        int targetFile = square.ordinal() % 8;

        for (rank = targetRank + 1, file = targetFile + 1; rank <= 7 && file <= 7; ++rank, ++file) {
            attacks |= (1L << (rank * 8 + file));
            if (((1L << (rank * 8 + file)) & block) != 0) {
                break;
            }
        }

        for (rank = targetRank - 1, file = targetFile + 1; rank >= 0 && file <= 7; --rank, ++file) {
            attacks |= (1L << (rank * 8 + file));
            if (((1L << (rank * 8 + file)) & block) != 0) {
                break;
            }
        }

        for (rank = targetRank + 1, file = targetFile - 1; rank <= 7 && file >= 0; ++rank, --file) {
            attacks |= (1L << (rank * 8 + file));
            if (((1L << (rank * 8 + file)) & block) != 0) {
                break;
            }
        }

        for (rank = targetRank - 1, file = targetFile - 1; rank >= 0 && file >= 0; --rank, --file) {
            attacks |= (1L << (rank * 8 + file));
            if (((1L << (rank * 8 + file)) & block) != 0) {
                break;
            }
        }

        return attacks;
    }

    private long rookAttacksOnTheFly(SquareEnum square, long block) {
        long attacks = 0;

        int rank;
        int file;
        int targetRank = square.ordinal() / 8;
        int targetFile = square.ordinal() % 8;

        for (rank = targetRank + 1; rank <= 7; ++rank) {
            attacks |= (1L << (rank * 8 + targetFile));
            if (((1L << (rank * 8 + targetFile)) & block) != 0) {
                break;
            }
        }

        for (rank = targetRank - 1; rank >= 0; --rank) {
            attacks |= (1L << (rank * 8 + targetFile));
            if (((1L << (rank * 8 + targetFile)) & block) != 0) {
                break;
            }
        }

        for (file = targetFile + 1; file <= 7; ++file) {
            attacks |= (1L << (targetRank * 8 + file));
            if (((1L << (targetRank * 8 + file)) & block) != 0) {
                break;
            }
        }

        for (file = targetFile - 1; file >= 0; --file) {
            attacks |= (1L << (targetRank * 8 + file));
            if (((1L << (targetRank * 8 + file)) & block) != 0) {
                break;
            }
        }

        return attacks;
    }

    private void initLeaperAttacks() {
        for (int square = 0; square < 64; square++) {
            pawnAttacks[SIDE_WHITE][square] =
                maskPawnAttacks(SIDE_WHITE, SquareEnum.intToSquare(square));
            pawnAttacks[SIDE_BLACK][square] =
                maskPawnAttacks(SIDE_BLACK, SquareEnum.intToSquare(square));

            knightAttacks[square] = maskKnightAttacks(SquareEnum.intToSquare(square));

            kingAttacks[square] = maskKingAttacks(SquareEnum.intToSquare(square));
        }
    }

    private long setOccupancy(int index, int bitsInMask, long attackMask) {
        long occupancy = 0;

        for (int count = 0; count < bitsInMask; count++) {
            SquareEnum square = SquareEnum.intToSquare(getLsbIndex(attackMask));
            attackMask = popBit(attackMask, square);

            if ((index & (1 << count)) != 0) {
                occupancy |= (1L << square.ordinal());
            }
        }

        return occupancy;
    }

    private void initSlidersAttacks(boolean bishop) {
        for (int square = 0; square < 64; square++) {
            bishopMasks[square] = maskBishopAttacks(SquareEnum.intToSquare(square));
            rookMasks[square] = maskRookAttacks(SquareEnum.intToSquare(square));

            long attack_mask = bishop ? bishopMasks[square] : rookMasks[square];

            int relevant_bits_count = countBits(attack_mask);
            int occupancyIndices = (1 << relevant_bits_count);

            for (int index = 0; index < occupancyIndices; index++) {
                if (bishop) {
                    long occupancy = setOccupancy(index, relevant_bits_count, attack_mask);

                    int magicIndex =
                        (int) ((occupancy * BISHOP_MAGIC_NUMBERS[square]) >>> (64 - BISHOP_RELEVANT_BITS[square]));

                    bishopAttacks[square][magicIndex] =
                        bishopAttacksOnTheFly(SquareEnum.intToSquare(square),
                        occupancy);
                } else {
                    long occupancy = setOccupancy(index, relevant_bits_count, attack_mask);

                    int magic_index =
                        (int) ((occupancy * ROOK_MAGIC_NUMBERS[square]) >>> (64 - ROOK_RELEVANT_BITS[square]));

                    rookAttacks[square][magic_index] = rookAttacksOnTheFly(SquareEnum.intToSquare(square),
                        occupancy);
                }
            }
        }
    }

    private long getBishopAttacks(int square, long occupancy) {
        // get bishop attacks assuming current board occupancy
        occupancy &= bishopMasks[square];
        occupancy *= BISHOP_MAGIC_NUMBERS[square];
        occupancy >>>= 64 - BISHOP_RELEVANT_BITS[square];

        // return bishop attacks
        return bishopAttacks[square][(int) occupancy];
    }

    private long getRookAttacks(int square, long occupancy) {
        // get rook attacks assuming current board occupancy
        occupancy &= rookMasks[square];
        occupancy *= ROOK_MAGIC_NUMBERS[square];
        occupancy >>>= 64 - ROOK_RELEVANT_BITS[square];

        // return rook attacks
        return rookAttacks[square][(int) occupancy];
    }

    private long getQueenAttacks(int square, long occupancy) {
        long queenAttacks;

        long bishopOccupancy = occupancy;
        long rookOccupancy = occupancy;

        // get bishop attacks assuming current board occupancy
        bishopOccupancy &= bishopMasks[square];
        bishopOccupancy *= BISHOP_MAGIC_NUMBERS[square];
        bishopOccupancy >>>= 64 - BISHOP_RELEVANT_BITS[square];

        queenAttacks = bishopAttacks[square][(int) bishopOccupancy];

        // get rook attacks assuming current board occupancy
        rookOccupancy &= rookMasks[square];
        rookOccupancy *= ROOK_MAGIC_NUMBERS[square];
        rookOccupancy >>>= 64 - ROOK_RELEVANT_BITS[square];

        queenAttacks |= rookAttacks[square][(int) rookOccupancy];

        return queenAttacks;
    }

    public boolean isSquareAttacked(int square, int side) {
        // attacked by white pawns
        if ((side == SIDE_WHITE) && (pawnAttacks[SIDE_BLACK][square] & bitboards[PieceEnum.P.ordinal()]) != 0) {
            return true;
        }

        // attacked by black pawns
        if ((side == SIDE_BLACK) && (pawnAttacks[SIDE_WHITE][square] & bitboards[PieceEnum.p.ordinal()]) != 0) {
            return true;
        }

        // attacked by knights
        if ((knightAttacks[square] & ((side == SIDE_WHITE) ? bitboards[PieceEnum.N.ordinal()] :
            bitboards[PieceEnum.n.ordinal()])) != 0) {
            return true;
        }

        // attacked by bishops
        if ((getBishopAttacks(square, occupancies[SIDE_BOTH]) & ((side == SIDE_WHITE) ?
            bitboards[PieceEnum.B.ordinal()] : bitboards[PieceEnum.b.ordinal()])) != 0) {
            return true;
        }

        // attacked by rooks
        if ((getRookAttacks(square, occupancies[SIDE_BOTH]) & ((side == SIDE_WHITE) ?
            bitboards[PieceEnum.R.ordinal()] : bitboards[PieceEnum.r.ordinal()])) != 0) {
            return true;
        }

        // attacked by bishops
        if ((getQueenAttacks(square, occupancies[SIDE_BOTH]) & ((side == SIDE_WHITE) ?
            bitboards[PieceEnum.Q.ordinal()] : bitboards[PieceEnum.q.ordinal()])) != 0) {
            return true;
        }

        // attacked by kings
        if ((kingAttacks[square] & ((side == SIDE_WHITE) ? bitboards[PieceEnum.K.ordinal()] :
            bitboards[PieceEnum.k.ordinal()])) != 0) {
            return true;
        }

        // by default return false
        return false;
    }

    public void addMove(Moves moveList, int move) {
        moveList.moves[moveList.count] = move;
        moveList.count++;
    }

    /*
              binary move bits                               hexadecimal constants

        0000 0000 0000 0000 0011 1111    source square       0x3f
        0000 0000 0000 1111 1100 0000    target square       0xfc0
        0000 0000 1111 0000 0000 0000    piece               0xf000
        0000 1111 0000 0000 0000 0000    promoted piece      0xf0000
        0001 0000 0000 0000 0000 0000    capture flag        0x100000
        0010 0000 0000 0000 0000 0000    double push flag    0x200000
        0100 0000 0000 0000 0000 0000    enpassant flag      0x400000
        1000 0000 0000 0000 0000 0000    castling flag       0x800000
    */

    public int encodeMove(int source, int target, PieceEnum piece, int promotedPiece,
                           int capture, int doublePush, int enpassant, int castling) {
        return (source) | (target << 6) | (piece.ordinal() << 12) | (promotedPiece << 16) |
            (capture << 20) | (doublePush << 21) | (enpassant << 22) | (castling << 23);
    }

    public SquareEnum getMoveSource(int move) {
        return SquareEnum.intToSquare(move & 0x3f);
    }

    public SquareEnum getMoveTarget(int move) {
        return SquareEnum.intToSquare((move & 0xfc0) >>> 6);
    }

    public PieceEnum getMovePiece(int move) {
        return PieceEnum.intToPiece((move & 0xf000) >>> 12);
    }

    public PieceEnum getMovePromoted(int move) {
        return PieceEnum.intToPiece((move & 0xf0000) >>> 16);
    }

    public boolean getMoveCapture(int move) {
        return (move & 0x100000) != 0;
    }

    public boolean getMoveDouble(int move) {
        return (move & 0x200000) != 0;
    }

    public boolean getMoveEnpassant(int move) {
        return (move & 0x400000) != 0;
    }

    public boolean getMoveCastling(int move) {
        return (move & 0x800000) != 0;
    }

    public boolean makeMove(int move, MoveTypeEnum move_flag) {
        // quiet moves
        if (move_flag == MoveTypeEnum.ALL_MOVES) {
            // preserve board state
            BoardState boardState = copyBoard();

            // parse move
            SquareEnum source_square = getMoveSource(move);
            SquareEnum target_square = getMoveTarget(move);
            PieceEnum piece = getMovePiece(move);
            PieceEnum promoted_piece = getMovePromoted(move);
            boolean capture = getMoveCapture(move);
            boolean double_push = getMoveDouble(move);
            boolean enpass = getMoveEnpassant(move);
            boolean castling = getMoveCastling(move);

            // move piece
            bitboards[piece.ordinal()] = popBit(bitboards[piece.ordinal()], source_square);
            bitboards[piece.ordinal()] = setBit(bitboards[piece.ordinal()], target_square);

            // handling capture moves
            if (capture) {
                // pick up bitboard piece index ranges depending on side
                int start_piece;
                int end_piece;

                // white to move
                if (side == SIDE_WHITE) {
                    start_piece = PieceEnum.p.ordinal();
                    end_piece = PieceEnum.k.ordinal();
                } else {
                    // black to move
                    start_piece = PieceEnum.P.ordinal();
                    end_piece = PieceEnum.K.ordinal();
                }

                // loop over bitboards opposite to the current side to move
                for (int bb_piece = start_piece; bb_piece <= end_piece; ++bb_piece) {
                    // if there's a piece on the target square
                    if (getBit(bitboards[bb_piece], target_square) != 0) {
                        // remove it from corresponding bitboard
                        bitboards[bb_piece] = popBit(bitboards[bb_piece], target_square);

                        break;
                    }
                }
            }

            // handle pawn promotions
            if (promoted_piece.ordinal() != 0) {
                // erase the pawn from the target square

                // white to move
                if (side == SIDE_WHITE) {
                    // erase the pawn from the target square
                    bitboards[PieceEnum.P.ordinal()] = popBit(bitboards[PieceEnum.P.ordinal()],
                        target_square);
                } else {
                    // black to move
                    // erase the pawn from the target square
                    bitboards[PieceEnum.p.ordinal()] = popBit(bitboards[PieceEnum.p.ordinal()],
                        target_square);
                }

                // set up promoted piece on chess board
                bitboards[promoted_piece.ordinal()] = setBit(bitboards[promoted_piece.ordinal()],
                    target_square);
            }

            // handle enpassant captures
            if (enpass) {
                // erase the pawn depending on side to move

                // white to move
                if (side == SIDE_WHITE) {
                    // remove captured pawn
                    bitboards[PieceEnum.p.ordinal()] = popBit(bitboards[PieceEnum.p.ordinal()],
                        SquareEnum.intToSquare(target_square.ordinal() + 8));
                } else {
                    // black to move
                    // remove captured pawn
                    bitboards[PieceEnum.P.ordinal()] = popBit(bitboards[PieceEnum.P.ordinal()],
                        SquareEnum.intToSquare(target_square.ordinal() - 8));
                }
            }

            // reset enpassant square
            enpassant = SquareEnum.NO_SQUARE;

            // handle double pawn push
            if (double_push) {
                // set enpassant square depending on side to move

                // white to move
                if (side == SIDE_WHITE) {
                    // set enpassant square
                    enpassant = SquareEnum.intToSquare(target_square.ordinal() + 8);
                } else {
                    // black to move
                    // set enpassant square
                    enpassant = SquareEnum.intToSquare(target_square.ordinal() - 8);
                }
            }

            // handle castling moves
            if (castling) {
                // switch target square
                switch (target_square) {
                    // white castles king side
                    case SquareEnum.G1:
                        // move H rook
                        bitboards[PieceEnum.R.ordinal()] =
                            popBit(bitboards[PieceEnum.R.ordinal()], SquareEnum.H1);
                        bitboards[PieceEnum.R.ordinal()] =
                            setBit(bitboards[PieceEnum.R.ordinal()], SquareEnum.F1);
                        break;

                    // white castles queen side
                    case SquareEnum.C1:
                        // move A rook
                        bitboards[PieceEnum.R.ordinal()] =
                            popBit(bitboards[PieceEnum.R.ordinal()], SquareEnum.A1);
                        bitboards[PieceEnum.R.ordinal()] =
                            setBit(bitboards[PieceEnum.R.ordinal()], SquareEnum.D1);
                        break;

                    // black castles king side
                    case SquareEnum.G8:
                        // move H rook
                        bitboards[PieceEnum.r.ordinal()] =
                            popBit(bitboards[PieceEnum.r.ordinal()], SquareEnum.H8);
                        bitboards[PieceEnum.r.ordinal()] =
                            setBit(bitboards[PieceEnum.r.ordinal()], SquareEnum.F8);
                        break;

                    // black castles queen side
                    case SquareEnum.C8:
                        // move A rook
                        bitboards[PieceEnum.r.ordinal()]=
                            popBit(bitboards[PieceEnum.r.ordinal()], SquareEnum.A8);
                        bitboards[PieceEnum.r.ordinal()] =
                            setBit(bitboards[PieceEnum.r.ordinal()], SquareEnum.D8);
                        break;
                }
            }

            // update castling rights
            castle &= CASTLING_RIGHTS[source_square.ordinal()];
            castle &= CASTLING_RIGHTS[target_square.ordinal()];

            // reset occupancies
            Arrays.fill(occupancies, 0L);

            // update white occupancies
            occupancies[SIDE_WHITE] |= bitboards[PieceEnum.P.ordinal()];
            occupancies[SIDE_WHITE] |= bitboards[PieceEnum.N.ordinal()];
            occupancies[SIDE_WHITE] |= bitboards[PieceEnum.B.ordinal()];
            occupancies[SIDE_WHITE] |= bitboards[PieceEnum.R.ordinal()];
            occupancies[SIDE_WHITE] |= bitboards[PieceEnum.Q.ordinal()];
            occupancies[SIDE_WHITE] |= bitboards[PieceEnum.K.ordinal()];

            // update black occupancies
            occupancies[SIDE_BLACK] |= bitboards[PieceEnum.p.ordinal()];
            occupancies[SIDE_BLACK] |= bitboards[PieceEnum.n.ordinal()];
            occupancies[SIDE_BLACK] |= bitboards[PieceEnum.b.ordinal()];
            occupancies[SIDE_BLACK] |= bitboards[PieceEnum.r.ordinal()];
            occupancies[SIDE_BLACK] |= bitboards[PieceEnum.q.ordinal()];
            occupancies[SIDE_BLACK] |= bitboards[PieceEnum.k.ordinal()];

            // update both sides occupancies
            occupancies[SIDE_BOTH] |= occupancies[SIDE_WHITE];
            occupancies[SIDE_BOTH] |= occupancies[SIDE_BLACK];

            // change side
            side ^= 1;

            // make sure that king has not been exposed into a check
            if (isSquareAttacked(
                (side == SIDE_WHITE) ?
                    getLsbIndex(bitboards[PieceEnum.k.ordinal()]) :
                    getLsbIndex(bitboards[PieceEnum.K.ordinal()]), side)) {
                // take move back
                takeBack(boardState);

                // return illegal move
                return false;
            } else {
                // return legal move
                return true;
            }
        } else {
            // capture moves
            // make sure move is the capture
            if (getMoveCapture(move)) {
                return makeMove(move, MoveTypeEnum.ALL_MOVES);
            } else {
                // otherwise the move is not a capture
                // don't make it
                return false;
            }
        }
    }

    public void generateMoves(Moves moveList) {
        moveList.count = 0;

        int sourceSquare;
        int targetSquare;

        // define current piece's bitboard copy & it's attacks
        long bitboard;
        long attacks;

        for (PieceEnum piece: PieceEnum.values()) {
            // init piece bitboard copy
            bitboard = bitboards[piece.ordinal()];

            // generateSIDE_WHITE pawns &SIDE_WHITE king castling moves
            if (side == SIDE_WHITE) {
                // pick upSIDE_WHITE pawn bitboards index
                if (piece == PieceEnum.P) {
                    // loop overSIDE_WHITE pawns withinSIDE_WHITE pawn bitboard
                    while (bitboard != 0) {
                        // init source square
                        sourceSquare = getLsbIndex(bitboard);

                        // init target square
                        targetSquare = sourceSquare - 8;

                        // generate quite pawn moves
                        if (!(targetSquare < SquareEnum.A8.ordinal()) &&
                            getBit(occupancies[SIDE_BOTH],
                                SquareEnum.intToSquare(targetSquare)) == 0) {
                            // pawn promotion
                            if (sourceSquare >= SquareEnum.A7.ordinal() && sourceSquare <= SquareEnum.H7.ordinal()) {
                                addMove(moveList, encodeMove(sourceSquare, targetSquare, piece,
                                    PieceEnum.Q.ordinal(), 0, 0, 0, 0));
                                addMove(moveList, encodeMove(sourceSquare, targetSquare, piece,
                                    PieceEnum.R.ordinal(), 0, 0, 0, 0));
                                addMove(moveList, encodeMove(sourceSquare, targetSquare, piece,
                                    PieceEnum.B.ordinal(), 0, 0, 0, 0));
                                addMove(moveList, encodeMove(sourceSquare, targetSquare, piece,
                                    PieceEnum.N.ordinal(), 0, 0, 0, 0));
                            } else {
                                // one square ahead pawn move
                                addMove(moveList, encodeMove(sourceSquare, targetSquare, piece,
                                    0, 0, 0, 0, 0));

                                // two squares ahead pawn move
                                if ((sourceSquare >= SquareEnum.A2.ordinal() && sourceSquare <= SquareEnum.H2.ordinal()) &&
                                        getBit(occupancies[SIDE_BOTH],
                                            SquareEnum.intToSquare(targetSquare - 8)) == 0) {
                                    addMove(moveList, encodeMove(sourceSquare, (targetSquare - 8), piece,
                                        0, 0, 1, 0, 0));
                                }

                            }
                        }

                        // init pawn attacks bitboard
                        attacks = pawnAttacks[side][sourceSquare] & occupancies[SIDE_BLACK];

                        // generate pawn captures
                        while (attacks != 0) {
                            // init target square
                            targetSquare = getLsbIndex(attacks);

                            // pawn promotion
                            if (sourceSquare >= SquareEnum.A7.ordinal() && sourceSquare <= SquareEnum.H7.ordinal()) {
                                addMove(moveList, encodeMove(sourceSquare, targetSquare, piece,
                                    PieceEnum.Q.ordinal(), 1, 0, 0, 0));
                                addMove(moveList, encodeMove(sourceSquare, targetSquare, piece,
                                    PieceEnum.R.ordinal(), 1, 0, 0, 0));
                                addMove(moveList, encodeMove(sourceSquare, targetSquare, piece,
                                    PieceEnum.B.ordinal(), 1, 0, 0, 0));
                                addMove(moveList, encodeMove(sourceSquare, targetSquare, piece,
                                    PieceEnum.N.ordinal(), 1, 0, 0, 0));
                            } else {
                                // one square ahead pawn capture
                                addMove(moveList, encodeMove(sourceSquare, targetSquare, piece,
                                    0, 1, 0, 0, 0));
                            }

                            // pop lsb of the pawn attacks
                            attacks = popBit(attacks, SquareEnum.intToSquare(targetSquare));
                        }

                        // generate enpassant captures
                        if (enpassant != SquareEnum.NO_SQUARE) {
                            // lookup pawn attacks and bitwise AND with enpassant square (bit)
                            long enpassantAttacks =
                                pawnAttacks[side][sourceSquare] & (1L << enpassant.ordinal());

                            // make sure enpassant capture available
                            if (enpassantAttacks != 0) {
                                // init enpassant capture target square
                                int target_enpassant = getLsbIndex(enpassantAttacks);
                                addMove(moveList, encodeMove(sourceSquare, target_enpassant, piece,
                                    0, 1, 0, 1, 0));
                            }
                        }

                        // pop lsb from piece bitboard copy
                        bitboard = popBit(bitboard, SquareEnum.intToSquare(sourceSquare));
                    }
                }

                // castling moves
                if (piece == PieceEnum.K) {
                    // king side castling is available
                    if ((castle & WK) != 0) {
                        // make sure square between king and king's rook are empty
                        if (getBit(occupancies[SIDE_BOTH], SquareEnum.F1) == 0 &&
                            getBit(occupancies[SIDE_BOTH], SquareEnum.G1) == 0) {
                            // make sure king and the f1 squares are not under attacks
                            if (!isSquareAttacked(SquareEnum.E1.ordinal(), SIDE_BLACK) &&
                                !isSquareAttacked(SquareEnum.F1.ordinal(), SIDE_BLACK)) {
                                addMove(moveList, encodeMove(SquareEnum.E1.ordinal(), SquareEnum.G1.ordinal(),
                                    piece, 0, 0, 0, 0, 1));
                            }
                        }
                    }

                    // queen side castling is available
                    if ((castle & WQ) != 0) {
                        // make sure square between king and queen's rook are empty
                        if (getBit(occupancies[SIDE_BOTH], SquareEnum.D1) == 0 &&
                            getBit(occupancies[SIDE_BOTH], SquareEnum.C1) == 0 &&
                            getBit(occupancies[SIDE_BOTH], SquareEnum.B1) == 0) {
                            // make sure king and the d1 squares are not under attacks
                            if (!isSquareAttacked(SquareEnum.E1.ordinal(), SIDE_BLACK) &&
                                !isSquareAttacked(SquareEnum.D1.ordinal(), SIDE_BLACK)) {
                                addMove(moveList, encodeMove(SquareEnum.E1.ordinal(), SquareEnum.C1.ordinal(),
                                    piece, 0, 0, 0, 0, 1));
                            }
                        }
                    }
                }

            } else {
                // pick up black pawn bitboards index
                if (piece == PieceEnum.p) {
                    // loop overSIDE_WHITE pawns withinSIDE_WHITE pawn bitboard
                    while (bitboard != 0) {
                        // init source square
                        sourceSquare = getLsbIndex(bitboard);

                        // init target square
                        targetSquare = sourceSquare + 8;

                        // generate quite pawn moves
                        if (!(targetSquare > SquareEnum.H1.ordinal()) &&
                            getBit(occupancies[SIDE_BOTH], SquareEnum.intToSquare(targetSquare)) == 0) {
                            // pawn promotion
                            if (sourceSquare >= SquareEnum.A2.ordinal() && sourceSquare <= SquareEnum.H2.ordinal()) {
                                addMove(moveList, encodeMove(sourceSquare, targetSquare, piece,
                                    PieceEnum.q.ordinal(), 0, 0, 0, 0));
                                addMove(moveList, encodeMove(sourceSquare, targetSquare, piece,
                                    PieceEnum.r.ordinal(), 0, 0, 0, 0));
                                addMove(moveList, encodeMove(sourceSquare, targetSquare, piece,
                                    PieceEnum.b.ordinal(), 0, 0, 0, 0));
                                addMove(moveList, encodeMove(sourceSquare, targetSquare, piece,
                                    PieceEnum.n.ordinal(), 0, 0, 0, 0));
                            } else {
                                // one square ahead pawn move
                                addMove(moveList, encodeMove(sourceSquare, targetSquare, piece,
                                    0, 0, 0, 0, 0));

                                // two squares ahead pawn move
                                if ((sourceSquare >= SquareEnum.A7.ordinal() && sourceSquare <= SquareEnum.H7.ordinal()) &&
                                    getBit(occupancies[SIDE_BOTH],
                                        SquareEnum.intToSquare(targetSquare + 8)) == 0) {
                                    addMove(moveList, encodeMove(sourceSquare, (targetSquare + 8),
                                        piece, 0, 0, 1, 0, 0));
                                }
                            }
                        }

                        // init pawn attacks bitboard
                        attacks = pawnAttacks[side][sourceSquare] & occupancies[SIDE_WHITE];

                        // generate pawn captures
                        while (attacks != 0) {
                            // init target square
                            targetSquare = getLsbIndex(attacks);

                            // pawn promotion
                            if (sourceSquare >= SquareEnum.A2.ordinal() && sourceSquare <= SquareEnum.H2.ordinal()) {
                                addMove(moveList, encodeMove(sourceSquare, targetSquare, piece,
                                    PieceEnum.q.ordinal(), 1, 0, 0, 0));
                                addMove(moveList, encodeMove(sourceSquare, targetSquare, piece,
                                    PieceEnum.r.ordinal(), 1, 0, 0, 0));
                                addMove(moveList, encodeMove(sourceSquare, targetSquare, piece,
                                    PieceEnum.b.ordinal(), 1, 0, 0, 0));
                                addMove(moveList, encodeMove(sourceSquare, targetSquare, piece,
                                    PieceEnum.n.ordinal(), 1, 0, 0, 0));
                            } else {
                                // one square ahead pawn move
                                addMove(moveList, encodeMove(sourceSquare, targetSquare,
                                    piece, 0, 1, 0, 0, 0));
                            }

                            // pop lsb of the pawn attacks
                            attacks = popBit(attacks, SquareEnum.intToSquare(targetSquare));
                        }

                        // generate enpassant captures
                        if (enpassant != SquareEnum.NO_SQUARE) {
                            // lookup pawn attacks and bitwise AND with enpassant square (bit)
                            long enpassantAttacks =
                                pawnAttacks[side][sourceSquare] & (1L << enpassant.ordinal());

                            // make sure enpassant capture available
                            if (enpassantAttacks != 0) {
                                // init enpassant capture target square
                                int target_enpassant = getLsbIndex(enpassantAttacks);
                                addMove(moveList, encodeMove(sourceSquare, target_enpassant,
                                    piece, 0, 1, 0, 1, 0));
                            }
                        }

                        // pop lsb from piece bitboard copy
                        bitboard = popBit(bitboard, SquareEnum.intToSquare(sourceSquare));
                    }
                }

                // castling moves
                if (piece == PieceEnum.k) {
                    // king side castling is available
                    if ((castle & BK) != 0) {
                        // make sure square between king and king's rook are empty
                        if (getBit(occupancies[SIDE_BOTH], SquareEnum.F8) == 0 &&
                            getBit(occupancies[SIDE_BOTH], SquareEnum.G8) == 0) {
                            // make sure king and the f8 squares are not under attacks
                            if (!isSquareAttacked(SquareEnum.E8.ordinal(),SIDE_WHITE) &&
                                !isSquareAttacked(SquareEnum.F8.ordinal(), SIDE_WHITE)) {
                                addMove(moveList, encodeMove(SquareEnum.E8.ordinal(), SquareEnum.G8.ordinal(),
                                    piece, 0, 0, 0, 0, 1));
                            }
                        }
                    }

                    // queen side castling is available
                    if ((castle & BQ) != 0) {
                        // make sure square between king and queen's rook are empty
                        if (getBit(occupancies[SIDE_BOTH], SquareEnum.D8) == 0 &&
                            getBit(occupancies[SIDE_BOTH], SquareEnum.C8) == 0 &&
                            getBit(occupancies[SIDE_BOTH], SquareEnum.B8) == 0) {
                            // make sure king and the d8 squares are not under attacks
                            if (!isSquareAttacked(SquareEnum.E8.ordinal(),SIDE_WHITE) &&
                                !isSquareAttacked(SquareEnum.D8.ordinal(),SIDE_WHITE)) {
                                addMove(moveList, encodeMove(SquareEnum.E8.ordinal(), SquareEnum.C8.ordinal(),
                                    piece, 0, 0, 0, 0, 1));
                            }
                        }
                    }
                }
            }

            // generate knight moves
            if ((side ==SIDE_WHITE) ? piece == PieceEnum.N : piece == PieceEnum.n) {
                // loop over source squares of piece bitboard copy
                while (bitboard != 0) {
                    // init source square
                    sourceSquare = getLsbIndex(bitboard);

                    // init piece attacks in order to get set of target squares
                    attacks = knightAttacks[sourceSquare] & ((side ==SIDE_WHITE) ?
                        ~occupancies[SIDE_WHITE] : ~occupancies[SIDE_BLACK]);

                    // loop over target squares available from generated attacks
                    while (attacks != 0) {
                        // init target square
                        targetSquare = getLsbIndex(attacks);

                        // quite move
                        if (getBit(((side ==SIDE_WHITE) ? occupancies[SIDE_BLACK] :
                            occupancies[SIDE_WHITE]), SquareEnum.intToSquare(targetSquare)) == 0) {
                            addMove(moveList, encodeMove(sourceSquare, targetSquare,
                                piece, 0, 0, 0, 0, 0));
                        } else {
                            // capture move
                            addMove(moveList, encodeMove(sourceSquare, targetSquare,
                                piece, 0, 1, 0, 0, 0));
                        }

                        // pop lsb in current attacks set
                        attacks = popBit(attacks, SquareEnum.intToSquare(targetSquare));
                    }


                    // pop lsb of the current piece bitboard copy
                    bitboard = popBit(bitboard, SquareEnum.intToSquare(sourceSquare));
                }
            }

            // generate bishop moves
            if ((side ==SIDE_WHITE) ? piece == PieceEnum.B : piece == PieceEnum.b) {
                // loop over source squares of piece bitboard copy
                while (bitboard != 0) {
                    // init source square
                    sourceSquare = getLsbIndex(bitboard);

                    // init piece attacks in order to get set of target squares
                    attacks = getBishopAttacks(sourceSquare, occupancies[SIDE_BOTH]) & ((side ==SIDE_WHITE) ? ~occupancies[SIDE_WHITE] : ~occupancies[SIDE_BLACK]);

                    // loop over target squares available from generated attacks
                    while (attacks != 0) {
                        // init target square
                        targetSquare = getLsbIndex(attacks);

                        // quite move
                        if (getBit(((side ==SIDE_WHITE) ? occupancies[SIDE_BLACK] :
                            occupancies[SIDE_WHITE]), SquareEnum.intToSquare(targetSquare)) == 0) {
                            addMove(moveList, encodeMove(sourceSquare, targetSquare,
                                piece, 0, 0, 0, 0, 0));
                        } else {
                            // capture move
                            addMove(moveList, encodeMove(sourceSquare, targetSquare,
                                piece, 0, 1, 0, 0, 0));
                        }

                        // pop lsb in current attacks set
                        attacks = popBit(attacks, SquareEnum.intToSquare(targetSquare));
                    }

                    // pop lsb of the current piece bitboard copy
                    bitboard = popBit(bitboard, SquareEnum.intToSquare(sourceSquare));
                }
            }

            // generate rook moves
            if ((side ==SIDE_WHITE) ? piece == PieceEnum.R : piece == PieceEnum.r) {
                // loop over source squares of piece bitboard copy
                while (bitboard != 0) {
                    // init source square
                    sourceSquare = getLsbIndex(bitboard);

                    // init piece attacks in order to get set of target squares
                    attacks = getRookAttacks(sourceSquare, occupancies[SIDE_BOTH]) & ((side ==SIDE_WHITE) ? ~occupancies[SIDE_WHITE] : ~occupancies[SIDE_BLACK]);

                    // loop over target squares available from generated attacks
                    while (attacks != 0) {
                        // init target square
                        targetSquare = getLsbIndex(attacks);

                        // quite move
                        if (getBit(((side ==SIDE_WHITE) ? occupancies[SIDE_BLACK] :
                            occupancies[SIDE_WHITE]), SquareEnum.intToSquare(targetSquare)) == 0) {
                            addMove(moveList, encodeMove(sourceSquare, targetSquare, piece, 0, 0, 0, 0, 0));
                        } else {
                            // capture move
                            addMove(moveList, encodeMove(sourceSquare, targetSquare, piece, 0, 1, 0, 0, 0));
                        }

                        // pop lsb in current attacks set
                        attacks = popBit(attacks, SquareEnum.intToSquare(targetSquare));
                    }


                    // pop lsb of the current piece bitboard copy
                    bitboard = popBit(bitboard, SquareEnum.intToSquare(sourceSquare));
                }
            }

            // generate queen moves
            if ((side == SIDE_WHITE) ? piece == PieceEnum.Q : piece == PieceEnum.q) {
                // loop over source squares of piece bitboard copy
                while (bitboard != 0) {
                    // init source square
                    sourceSquare = getLsbIndex(bitboard);

                    // init piece attacks in order to get set of target squares
                    attacks =
                        getQueenAttacks(sourceSquare, occupancies[SIDE_BOTH]) & ((side == SIDE_WHITE) ? ~occupancies[SIDE_WHITE] : ~occupancies[SIDE_BLACK]);

                    // loop over target squares available from generated attacks
                    while (attacks != 0) {
                        // init target square
                        targetSquare = getLsbIndex(attacks);

                        // quite move
                        if (getBit(((side ==SIDE_WHITE) ? occupancies[SIDE_BLACK] :
                            occupancies[SIDE_WHITE]), SquareEnum.intToSquare(targetSquare)) == 0) {
                            addMove(moveList, encodeMove(sourceSquare, targetSquare, piece, 0, 0, 0, 0, 0));
                        } else {
                            // capture move
                            addMove(moveList, encodeMove(sourceSquare, targetSquare, piece, 0, 1, 0, 0, 0));
                        }

                        // pop lsb in current attacks set
                        attacks = popBit(attacks, SquareEnum.intToSquare(targetSquare));
                    }

                    // pop lsb of the current piece bitboard copy
                    bitboard = popBit(bitboard, SquareEnum.intToSquare(sourceSquare));
                }
            }

            // generate king moves
            if ((side ==SIDE_WHITE) ? piece == PieceEnum.K : piece == PieceEnum.k) {
                // loop over source squares of piece bitboard copy
                while (bitboard != 0) {
                    // init source square
                    sourceSquare = getLsbIndex(bitboard);

                    // init piece attacks in order to get set of target squares
                    attacks = kingAttacks[sourceSquare] & ((side == SIDE_WHITE) ?
                        ~occupancies[SIDE_WHITE] : ~occupancies[SIDE_BLACK]);

                    // loop over target squares available from generated attacks
                    while (attacks != 0) {
                        // init target square
                        targetSquare = getLsbIndex(attacks);

                        // quite move
                        if (getBit(((side ==SIDE_WHITE) ? occupancies[SIDE_BLACK] :
                            occupancies[SIDE_WHITE]), SquareEnum.intToSquare(targetSquare)) == 0) {
                            addMove(moveList, encodeMove(sourceSquare, targetSquare, piece, 0, 0, 0, 0, 0));
                        } else {
                            // capture move
                            addMove(moveList, encodeMove(sourceSquare, targetSquare, piece, 0, 1, 0, 0, 0));
                        }
                        // pop lsb in current attacks set
                        attacks = popBit(attacks, SquareEnum.intToSquare(targetSquare));
                    }

                    // pop lsb of the current piece bitboard copy
                    bitboard = popBit(bitboard, SquareEnum.intToSquare(sourceSquare));
                }
            }
        }
    }

    public void parseFen(String fen) {
        Arrays.fill(bitboards, 0L);
        Arrays.fill(occupancies, 0L);

        // reset game state variables
        side = 0;
        enpassant = SquareEnum.NO_SQUARE;
        castle = 0;

        int fenIndex = 0;

        for (int rank = 0; rank < 8; ++rank) {
            for (int file = 0; file < 8; ++file) {
                int square = rank * 8 + file;

                // match ascii pieces within FEN string
                if ((fen.charAt(fenIndex) >= 'a' && fen.charAt(fenIndex) <= 'z') || (fen.charAt(fenIndex) >= 'A' && fen.charAt(fenIndex) <= 'Z')) {
                    PieceEnum piece = CHAR_PIECES.get(fen.charAt(fenIndex));

                    // set piece on corresponding bitboard
                    bitboards[piece.ordinal()] = setBit(bitboards[piece.ordinal()],
                        SquareEnum.intToSquare(square));

                    fenIndex++;
                }

                // match empty square numbers within FEN string
                if (fen.charAt(fenIndex) >= '0' && fen.charAt(fenIndex) <= '9') {
                    // init offset (convert char 0 to int 0)
                    int offset = fen.charAt(fenIndex) - '0';

                    // define piece variable
                    PieceEnum piece = null;

                    for (PieceEnum bb_piece : PieceEnum.values()) {
                        // if there is a piece on current square
                        if (getBit(bitboards[bb_piece.ordinal()], SquareEnum.intToSquare(square)) != 0) {
                            // get piece code
                            piece = bb_piece;
                        }
                    }

                    // on empty current square
                    if (piece == null) {
                        // decrement file
                        --file;
                    }

                    // adjust file counter
                    file += offset;

                    // increment pointer to FEN string
                    fenIndex++;
                }

                // match rank separator
                if (fen.charAt(fenIndex) == '/') {
                    // increment pointer to FEN string
                    fenIndex++;
                }
            }
        }

        // got to parsing side to move (increment pointer to FEN string)
        fenIndex++;

        // parse side to move
        if (fen.charAt(fenIndex) == 'w') {
            side = SIDE_WHITE;
        } else {
            side = SIDE_BLACK;
        }

        // go to parsing castling rights
        fenIndex += 2;

        // parse castling rights
        while (fen.charAt(fenIndex) != ' ') {
            switch (fen.charAt(fenIndex)) {
                case 'K': castle |= WK; break;
                case 'Q': castle |= WQ; break;
                case 'k': castle |= BK; break;
                case 'q': castle |= BQ; break;
                case '-': break;
            }

            fenIndex++;
        }

        // got to parsing enpassant square (increment pointer to FEN string)
        fenIndex++;

        // parse enpassant square
        if (fen.charAt(fenIndex) != '-') {
            // parse enpassant file & rank
            int file = fen.charAt(fenIndex) - 'a';
            int rank = 8 - (fen.charAt(fenIndex+1) - '0');

            // init enpassant square
            enpassant = SquareEnum.intToSquare(rank * 8 + file);
        } else {
            // no enpassant square
            enpassant = SquareEnum.NO_SQUARE;
        }

        // White pieces (P, N, B, R, Q, K)
        occupancies[SIDE_WHITE] |= bitboards[PieceEnum.P.ordinal()];
        occupancies[SIDE_WHITE] |= bitboards[PieceEnum.N.ordinal()];
        occupancies[SIDE_WHITE] |= bitboards[PieceEnum.B.ordinal()];
        occupancies[SIDE_WHITE] |= bitboards[PieceEnum.R.ordinal()];
        occupancies[SIDE_WHITE] |= bitboards[PieceEnum.Q.ordinal()];
        occupancies[SIDE_WHITE] |= bitboards[PieceEnum.K.ordinal()];

        // Black pieces (p, n, b, r, q, k)
        occupancies[SIDE_BLACK] |= bitboards[PieceEnum.p.ordinal()];
        occupancies[SIDE_BLACK] |= bitboards[PieceEnum.n.ordinal()];
        occupancies[SIDE_BLACK] |= bitboards[PieceEnum.b.ordinal()];
        occupancies[SIDE_BLACK] |= bitboards[PieceEnum.r.ordinal()];
        occupancies[SIDE_BLACK] |= bitboards[PieceEnum.q.ordinal()];
        occupancies[SIDE_BLACK] |= bitboards[PieceEnum.k.ordinal()];

        // init all occupancies
        occupancies[SIDE_BOTH] |= occupancies[SIDE_WHITE];
        occupancies[SIDE_BOTH] |= occupancies[SIDE_BLACK];
    }

    public void testMagicNumbers(boolean bishop) {
        boolean allValid = true;

        for (int square = 0; square < 64; square++) {
            long mask = bishop ? bishopMasks[square] : rookMasks[square];
            int relevantBits = bishop ? BISHOP_RELEVANT_BITS[square] : ROOK_RELEVANT_BITS[square];
            long magic = bishop ? BISHOP_MAGIC_NUMBERS[square] : ROOK_MAGIC_NUMBERS[square];

            int occupancyCount = 1 << countBits(mask);
            long[] usedAttacks = new long[occupancyCount];

            for (int index = 0; index < occupancyCount; index++) {
                long occupancy = setOccupancy(index, countBits(mask), mask);
                long attack = bishop
                    ? bishopAttacksOnTheFly(SquareEnum.intToSquare(square), occupancy)
                    : rookAttacksOnTheFly(SquareEnum.intToSquare(square), occupancy);

                int magicIndex = (int)((occupancy * magic) >>> (64 - relevantBits));

                if (usedAttacks[magicIndex] == 0) {
                    usedAttacks[magicIndex] = attack;
                } else if (usedAttacks[magicIndex] != attack) {
                    System.out.printf("Collision detected at square %d (%s):\n", square, bishop ? "Bishop" : "Rook");
                    System.out.printf("  Occupancy index: %d\n", index);
                    System.out.printf("  Magic index: %d\n", magicIndex);
                    System.out.println("  Existing attack:");
                    printBitboard(usedAttacks[magicIndex]);
                    System.out.println("  New attack:");
                    printBitboard(attack);
                    allValid = false;
                    break;
                }
            }
        }

        if (allValid) {
            System.out.println((bishop ? "Bishop" : "Rook") + " magic numbers are valid.");
        } else {
            System.out.println((bishop ? "Bishop" : "Rook") + " magic numbers are INVALID.");
        }
    }

    public void printBitboard(long bitboard) {
        System.out.println();
        for (int rank = 0; rank < 8; rank++) {
            for (int file = 0; file < 8; file++) {
                SquareEnum square = SquareEnum.intToSquare(rank * 8 + file);

                if (file == 0) {
                    System.out.printf("  %d ", 8 - rank);
                }

                System.out.printf(" %d", getBit(bitboard, square) > 0 ? 1 : 0);
            }
            System.out.println();
        }

        System.out.print("     a b c d e f g h\n\n");
    }

    public void printBoard() {
        System.out.println();
        for (int rank = 0; rank < 8; rank++) {
            for (int file = 0; file < 8; file++) {
                SquareEnum square = SquareEnum.intToSquare(rank * 8 + file);
                PieceEnum pieceEnum = null;

                for (PieceEnum piece : PieceEnum.values()) {
                    if (getBit(bitboards[piece.ordinal()], square) != 0) {
                        pieceEnum = piece;
                    }
                }

                if (file == 0) {
                    System.out.printf("  %d ", 8 - rank);
                }

                System.out.printf(" %s", pieceEnum != null ? pieceEnum.toString() : "0");
            }

            System.out.println();
        }

        System.out.print("     a b c d e f g h\n\n");

        System.out.printf("     Side:     %s\n", side == 0 ? "white" : "black");

        // print enpassant square
        System.out.printf("     Enpassant:   %s\n", (enpassant != SquareEnum.NO_SQUARE) ?
            enpassant.name() : "no");

        // print castling rights
        System.out.printf("     Castling:  %c%c%c%c\n\n", (castle & WK) != 0 ? 'K' : '-',
            (castle & WQ) != 0 ? 'Q' : '-',
            (castle & BK) != 0 ? 'k' : '-',
            (castle & BQ) != 0 ? 'q' : '-');
    }

    public void printMove(int move) {
        if (getMovePromoted(move) != PieceEnum.P) {
            System.out.printf("%s%s%s", getMoveSource(move).name(),
                getMoveTarget(move).name(),
                getMovePromoted(move).name());
        } else {
            System.out.printf("%s%s", getMoveSource(move).name(),
                getMoveTarget(move).name());
        }
    }

    public long[] getBitboards() {
        return bitboards;
    }

    public int getSide() {
        return side;
    }

    public boolean hasLegalMoves(Moves moveList) {
        for (int i = 0; i < moveList.count; i++) {
            BoardState oldState = copyBoard();
            boolean isLegal = makeMove(moveList.moves[i], MoveTypeEnum.ALL_MOVES);
            takeBack(oldState);

            if (isLegal) {
                return true;
            }
        }
        return false;
    }

    // parse user/GUI move string input (e.g. "e7e8q")
    public int parseMove(String moveString) {
        // create move list instance
        Moves moveList = new Moves();

        // generate moves
        generateMoves(moveList);

        int source_square = (moveString.charAt(0) - 'a') + (8 - (moveString.charAt(1) - '0')) * 8;
        int target_square = (moveString.charAt(2) - 'a') + (8 - (moveString.charAt(3) - '0')) * 8;

        // loop over the moves within a move list
        for (int move_count = 0; move_count < moveList.count; ++move_count) {
            // init move
            int move = moveList.moves[move_count];

            // make sure source & target squares are available within the generated move
            if (source_square == getMoveSource(move).ordinal() &&
                target_square == getMoveTarget(move).ordinal()) {
                // init promoted piece
                PieceEnum promoted_piece = getMovePromoted(move);

                // promoted piece is available
                if (promoted_piece.ordinal() != 0) {
                    // promoted to queen
                    if ((promoted_piece == PieceEnum.Q || promoted_piece == PieceEnum.q) &&
                        moveString.charAt(4) == 'q') {
                        // return legal move
                        return move;
                    } else if ((promoted_piece == PieceEnum.R || promoted_piece == PieceEnum.r) &&
                        moveString.charAt(4) == 'r') {
                        // promoted to rook
                        // return legal move
                        return move;
                    } else if ((promoted_piece == PieceEnum.B || promoted_piece == PieceEnum.b) &&
                        moveString.charAt(4) == 'b') {
                        // promoted to bishop
                        // return legal move
                        return move;
                    } else if ((promoted_piece == PieceEnum.N || promoted_piece == PieceEnum.n) &&
                        moveString.charAt(4) == 'n') {
                        // promoted to knight
                        // return legal move
                        return move;
                    }

                    // continue the loop on possible wrong promotions (e.g. "e7e8f")
                    continue;
                }

                // return legal move
                return move;
            }
        }

        // return illegal move
        return 0;
    }

    public final int MAX_COL = 8;
    public final int MAX_ROW = 8;
    public static final int SQUARE_SIZE = 100;
    public static final int HALF_SQUARE_SIZE = SQUARE_SIZE / 2;

    public void draw(Graphics2D graphics2d) {
        int c = 0;

        for (int i = 0; i < MAX_ROW; i++) {
            for (int j = 0; j < MAX_COL; j++) {
                if (c == 0) {
                    graphics2d.setColor(new Color(210, 165, 125));
                    c = 1;
                } else {
                    graphics2d.setColor(new Color(175, 115, 70 ));
                    c = 0;
                }

                graphics2d.fillRect(j * SQUARE_SIZE, i * SQUARE_SIZE, SQUARE_SIZE, SQUARE_SIZE);
            }

            if (c == 0) {
                c = 1;
            } else {
                c = 0;
            }
        }
    }
}
