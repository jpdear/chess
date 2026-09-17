package chess;

import java.util.Map;

public class MovementRules {
    private final ChessBoard board;
    private final ChessPosition position;

    public MovementRules(ChessBoard board, ChessPosition position) {
        this.board = board;
        this.position = position;
    }

    public MoveBehavior getMoves(ChessPiece.PieceType pieceType) {
        Map<ChessPiece.PieceType, MoveBehavior> moveBehaviors = Map.of(
                ChessPiece.PieceType.BISHOP, new BishopMoveBehavior(board, position)
        );

        return moveBehaviors.get(pieceType);
    }
}
