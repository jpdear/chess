package chess;

import java.util.ArrayList;

public class QueenMoveBehavior extends MoveBehavior {
    public QueenMoveBehavior(ChessBoard board, ChessPosition position) {
        super(board, position);
    }

    public ArrayList<ChessMove> getValidMoves() {
        ArrayList<ChessMove> validMoves = new ArrayList<>();

        validMoves.addAll(checkDirection(1, 0, true));
        validMoves.addAll(checkDirection(1, 1, true));
        validMoves.addAll(checkDirection(0, 1, true));
        validMoves.addAll(checkDirection(-1, -1, true));
        validMoves.addAll(checkDirection(-1, 0, true));
        validMoves.addAll(checkDirection(0, -1, true));
        validMoves.addAll(checkDirection(-1, 1, true));
        validMoves.addAll(checkDirection(1, -1, true));

        return validMoves;    }
}
