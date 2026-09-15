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
        if (row > 7 || row < 1) {
            throw new IllegalArgumentException("Row must be between 1-7");
        }

        if (col > 7 || col < 1) {
            throw new IllegalArgumentException("Col must be between 1-7");
        }

        this.row = row;
        this.col = col;
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
