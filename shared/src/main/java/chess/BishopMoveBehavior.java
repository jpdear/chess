package chess;

import java.util.Collection;

public class BishopMoveBehavior extends MoveBehavior {
    public BishopMoveBehavior(ChessBoard board, ChessPosition position, ChessGame.TeamColor color) {
        super(board, position, color);
    }

    @Override
    public Collection<ChessMove> getValidMoves() {
        return slideAll(DIAGONAL);
    }
}
