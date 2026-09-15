package chess;

/**
 * Represents a single square position on a chess board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPosition {

    private final int row;
    private final int col;

    public ChessPosition(int row, int col) {
        if (!isValidPosition(row, col)) {
            throw new IllegalArgumentException("Invalid coordinates");
        }

        this.row = row;
        this.col = col;
    }

    public ChessPosition(ChessPosition other) {
        this.row = other.row;
        this.col = other.col;
    }

    public static boolean isValidPosition(int row, int col) {
        if (row > 8 || row < 1) {
            return false;
        }

        if (col > 8 || col < 1) {
            return false;
        }

        return true;
    }

    /**
     * @return which row this position is in
     * 1 codes for the bottom row
     */
    public int getRow() {
        int copy = this.row;

        return copy;
    }

    /**
     * @return which column this position is in
     * 1 codes for the left column
     */
    public int getColumn() {
        int copy = this.col;

        return copy;
    }
}
