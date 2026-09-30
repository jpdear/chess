package chess;

import java.util.Collection;

public class QueenMoveBehavior extends MoveBehavior {
    public QueenMoveBehavior(ChessBoard board, ChessPosition position, ChessGame.TeamColor color) {
        super(board, position, color);
    }

    @Override
    public Collection<ChessMove> getValidMoves() {
        return slideAll(allDirections);
    }
}
