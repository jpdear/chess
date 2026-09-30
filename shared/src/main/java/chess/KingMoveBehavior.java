package chess;

import java.util.Collection;

public class KingMoveBehavior extends MoveBehavior {
    public KingMoveBehavior(ChessBoard board, ChessPosition position, ChessGame.TeamColor color) {
        super(board, position, color);
    }

    @Override
    public Collection<ChessMove> getValidMoves() {
        return stepAll(allDirections);
    }
}
