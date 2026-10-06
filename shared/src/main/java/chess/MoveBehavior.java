package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

public abstract class MoveBehavior {
    protected final ChessBoard board;
    protected final ChessPosition position;
    protected final ChessGame.TeamColor color;
    protected static final List<Direction> ORTHOGONAL = List.of(
            new Direction(1, 0), new Direction(-1, 0),
            new Direction(0, 1), new Direction(0, -1)
    );
    protected static final List<Direction> DIAGONAL = List.of(
            new Direction(1, 1), new Direction(1, -1),
            new Direction(-1, 1), new Direction(-1, -1)
    );
    protected static final List<Direction> ALL_DIRECTIONS = Stream.concat(ORTHOGONAL.stream(), DIAGONAL.stream()).toList();
    protected static final List<Direction> KNIGHT_JUMPS = List.of(
            new Direction(2, 1), new Direction(2, -1),
            new Direction(-2, 1), new Direction(-2, -1),
            new Direction(1, 2), new Direction(1, -2),
            new Direction(-1, 2), new Direction(-1, -2)
    );

    public MoveBehavior(ChessBoard board, ChessPosition position, ChessGame.TeamColor color) {
        this.board = board;
        this.position = position;
        this.color = color;
    }

    protected record Direction(int rowStep, int colStep) {}

    protected boolean isEmpty(ChessPosition p) {
        return board.getPiece(p) == null;
    }

    protected boolean isEnemy(ChessPosition p) {
        ChessPiece piece = board.getPiece(p);

        return piece != null && piece.getTeamColor() != color;
    }

    protected boolean isEmptyOrEnemy(ChessPosition p) {
        return isEmpty(p) || isEnemy(p);
    }

    protected List<ChessMove> slide(int rowStep, int colStep) {
        ArrayList<ChessMove> moves = new ArrayList<>();
        int checkRow = position.getRow() + rowStep;
        int checkCol = position.getColumn() + colStep;

        while (ChessPosition.isValidPosition(checkRow, checkCol)) {
            ChessPosition target = new ChessPosition(checkRow, checkCol);

            if (isEmpty(target)) {
                moves.add(new ChessMove(position, target));
            } else {
                if (isEnemy(target)) {
                    moves.add(new ChessMove(position, target));
                }

                break;
            }

            checkRow += rowStep;
            checkCol += colStep;
        }

        return moves;
    }

    protected List<ChessMove> step(int rowStep, int colStep) {
        ArrayList<ChessMove> moves = new ArrayList<>();
        int checkRow = position.getRow() + rowStep;
        int checkCol = position.getColumn() + colStep;

        if (!ChessPosition.isValidPosition(checkRow, checkCol)) {
            return moves;
        }

        ChessPosition target = new ChessPosition(checkRow, checkCol);

        if (isEmptyOrEnemy(target)) {
            moves.add(new ChessMove(position, target));
        }

        return moves;
    }

    protected List<ChessMove> slideAll(List<Direction> directions) {
        List<ChessMove> moves = new ArrayList<>();

        for (Direction d : directions) {
            moves.addAll(slide(d.rowStep(), d.colStep()));
        }

        return moves;
    }

    protected List<ChessMove> stepAll(List<Direction> directions) {
        List<ChessMove> moves = new ArrayList<>();

        for (Direction d : directions) {
            moves.addAll(step(d.rowStep(), d.colStep()));
        }

        return moves;
    }

    /**
     * Gets all valid moves based on the position on the board.
     *
     * @return ArrayList of valid positions to move to
     */
    public abstract Collection<ChessMove> getValidMoves();
}
