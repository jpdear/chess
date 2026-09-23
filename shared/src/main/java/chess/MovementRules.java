package chess;

import java.util.Map;

public class MovementRules {
    private final Map<ChessPiece.PieceType, MoveBehavior> moveBehaviors;

    public MovementRules(ChessBoard board, ChessPosition position) {
        moveBehaviors = Map.of(
                ChessPiece.PieceType.BISHOP, new BishopMoveBehavior(board, position),
                ChessPiece.PieceType.KING, new KingMoveBehavior(board, position),
                ChessPiece.PieceType.KNIGHT, new KnightMoveBehavior(board, position),
                ChessPiece.PieceType.QUEEN, new QueenMoveBehavior(board, position),
                ChessPiece.PieceType.ROOK, new RookMoveBehavior(board, position),
                ChessPiece.PieceType.PAWN, new PawnMoveBehavior(board, position)
        );
    }

    public MoveBehavior getMoves(ChessPiece.PieceType pieceType) {
        return moveBehaviors.get(pieceType);
    }
}
