package chess;

import java.util.ArrayList;

public class BishopMoveBehavior extends MoveBehavior {
    public BishopMoveBehavior(ChessBoard board, ChessPosition position) {
        super(board, position);
    }

    public ArrayList<ChessMove> getValidMoves() {
        ArrayList<ChessMove> validMoves = new ArrayList<>();

        validMoves.addAll(checkDirection(1, 1, true));
        validMoves.addAll(checkDirection(-1, 1, true));
        validMoves.addAll(checkDirection(1, -1, true));
        validMoves.addAll(checkDirection(-1, -1, true));

        return validMoves;
    }
}
