package chess;

import java.util.ArrayList;

public interface MoveBehavior {
    /**
     * Gets all valid moves based on the position on the board.
     *
     * @param board    The current game board
     * @param position The current position of the pice
     * @return ArrayList of valid positions to move to
     */
    ArrayList<ChessMove> getValidMoves(ChessBoard board, ChessPosition position);
}
