package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PawnMoveBehavior extends MoveBehavior {
    private final int forward;
    private final int startRow;
    private final int promotionRow;
    private final List<ChessPiece.PieceType> promotionTypes = List.of(
            ChessPiece.PieceType.QUEEN, ChessPiece.PieceType.ROOK,
            ChessPiece.PieceType.BISHOP, ChessPiece.PieceType.KNIGHT
    );

    public PawnMoveBehavior(ChessBoard board, ChessPosition position, ChessGame.TeamColor color) {
        super(board, position, color);

        int whiteStartRow = 2;
        int blackStartRow = 7;
        int whitePromotionRow = 8;
        int blackPromotionRow = 1;

        forward = color == ChessGame.TeamColor.WHITE ? 1 : -1;
        startRow = color == ChessGame.TeamColor.WHITE ? whiteStartRow : blackStartRow;
        promotionRow = color == ChessGame.TeamColor.WHITE ? whitePromotionRow : blackPromotionRow;
    }

    private void addPawnMove(List<ChessMove> moves, ChessPosition target) {
        if (target.getRow() == promotionRow) {
            for (ChessPiece.PieceType type : promotionTypes) {
                moves.add(new ChessMove(position, target, type));
            }
        } else {
            moves.add(new ChessMove(position, target));
        }
    }

    private List<ChessMove> forwardMoves() {
        ArrayList<ChessMove> moves = new ArrayList<>();
        int oneAheadRow = position.getRow() + forward;
        int col = position.getColumn();

        if (!ChessPosition.isValidPosition(oneAheadRow, col)) {
            return moves;
        }

        ChessPosition oneAhead = new ChessPosition(oneAheadRow, col);

        if (!isEmpty(oneAhead)) {
            return moves;
        }

        addPawnMove(moves, oneAhead);

        if (position.getRow() == startRow) {
            ChessPosition twoAhead = new ChessPosition(oneAheadRow + forward, col);

            if (isEmpty(twoAhead)) {
                moves.add(new ChessMove(position, twoAhead));
            }
        }

        return moves;
    }

    private List<ChessMove> captureMoves(int colStep) {
        ArrayList<ChessMove> moves = new ArrayList<>();
        int checkRow = position.getRow() + forward;
        int checkCol = position.getColumn() + colStep;

        if (!ChessPosition.isValidPosition(checkRow, checkCol)) {
            return moves;
        }

        ChessPosition target = new ChessPosition(checkRow, checkCol);

        if (isEnemy(target)) {
            addPawnMove(moves, target);
        }

        return moves;
    }

    @Override
    public Collection<ChessMove> getValidMoves() {
        ArrayList<ChessMove> validMoves = new ArrayList<>();

        validMoves.addAll(forwardMoves());
        validMoves.addAll(captureMoves(-1));
        validMoves.addAll(captureMoves(1));

        return validMoves;
    }
}
