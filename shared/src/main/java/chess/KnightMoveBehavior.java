package chess;

import java.util.ArrayList;

public class KnightMoveBehavior extends MoveBehavior {
    public KnightMoveBehavior (ChessBoard board, ChessPosition position) {
        super(board, position);
    }

    public ArrayList<ChessMove> getValidMoves() {
        ArrayList<ChessMove> validMoves = new ArrayList<>();

        validMoves.addAll(checkDirection(2, 1, false));
        validMoves.addAll(checkDirection(2, -1, false));
        validMoves.addAll(checkDirection(-1, 2, false));
        validMoves.addAll(checkDirection(1, 2, false));
        validMoves.addAll(checkDirection(-2, 1, false));
        validMoves.addAll(checkDirection(-2, -1, false));
        validMoves.addAll(checkDirection(-1, -2, false));
        validMoves.addAll(checkDirection(1, -2, false));

        return validMoves;
    }
}
