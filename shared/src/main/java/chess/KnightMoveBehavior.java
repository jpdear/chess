package chess;

import java.util.Collection;

public class KnightMoveBehavior extends MoveBehavior {
    public KnightMoveBehavior (ChessBoard board, ChessPosition position, ChessGame.TeamColor color) {
        super(board, position, color);
    }

    @Override
    public Collection<ChessMove> getValidMoves() {
        return stepAll(knightJumps);
    }
}
