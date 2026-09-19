package chess;

import java.util.ArrayList;

public class KingMoveBehavior extends MoveBehavior {
    public KingMoveBehavior(ChessBoard board, ChessPosition position) {
        super(board, position);
    }

    public ArrayList<ChessMove> getValidMoves() {
        ArrayList<ChessMove> validMoves = new ArrayList<>();

        validMoves.addAll(checkDirection(1, 0, false));
        validMoves.addAll(checkDirection(1, 1, false));
        validMoves.addAll(checkDirection(0, 1, false));
        validMoves.addAll(checkDirection(-1, -1, false));
        validMoves.addAll(checkDirection(-1, 0, false));
        validMoves.addAll(checkDirection(0, -1, false));
        validMoves.addAll(checkDirection(-1, 1, false));
        validMoves.addAll(checkDirection(1, -1, false));

        return validMoves;
    }
}
