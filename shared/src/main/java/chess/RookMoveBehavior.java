package chess;

import java.util.Collection;

public class RookMoveBehavior extends MoveBehavior {
    public RookMoveBehavior(ChessBoard board, ChessPosition position, ChessGame.TeamColor color) {
        super(board, position, color);
    }

    @Override
    public Collection<ChessMove> getValidMoves() {
        return slideAll(orthogonal);
    }
}
