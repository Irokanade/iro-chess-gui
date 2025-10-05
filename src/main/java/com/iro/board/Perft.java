package com.iro.board;

public class Perft {
    private long nodes = 0;
    private final Board board;

    public Perft(Board board) {
        this.board = board;
    }

    private void perftDriver(int depth) {
        if (depth == 0) {
            nodes++;
            return;
        }

        Moves moveList = new Moves();
        board.generateMoves(moveList);

        for (int i = 0; i < moveList.count; i++) {
            BoardState saved = board.copyBoard();

            if (!board.makeMove(moveList.moves[i], MoveTypeEnum.ALL_MOVES)) {
                board.takeBack(saved);
                continue;
            }

            perftDriver(depth - 1);

            board.takeBack(saved);
        }
    }

    public void perftTest(int depth) {
        System.out.println("\n     Performance test\n");

        nodes = 0;

        Moves moveList = new Moves();
        board.generateMoves(moveList);

        long start = System.currentTimeMillis();

        for (int i = 0; i < moveList.count; i++) {
            BoardState saved = board.copyBoard();

            if (!board.makeMove(moveList.moves[i], MoveTypeEnum.ALL_MOVES)) {
                board.takeBack(saved);
                continue;
            }

            long before = nodes;

            perftDriver(depth - 1);

            long moveNodes = nodes - before;

            board.takeBack(saved);

            System.out.printf("     move: %s%s%c  nodes: %d\n",
                board.getMoveSource(moveList.moves[i]).name().toLowerCase(),
                board.getMoveTarget(moveList.moves[i]).name().toLowerCase(),
                board.getMovePromoted(moveList.moves[i]) != PieceEnum.P ?
                    board.getMovePromoted(moveList.moves[i]).name().toLowerCase().charAt(0) : ' ',
                moveNodes);
        }

        long end = System.currentTimeMillis();

        System.out.printf("\n    Depth: %d\n", depth);
        System.out.printf("    Nodes: %d\n", nodes);
        System.out.printf("     Time: %d ms\n\n", end - start);
    }
}
