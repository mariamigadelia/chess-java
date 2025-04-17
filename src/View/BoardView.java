package View;

import Model.Board;
import Model.PieceColor;

/**
 * Interface for chess board views
 */
public interface BoardView {
    /**
     * Updates the view to display the current board state
     *
     * @param board The chess board model to display
     */
    void updateBoard(Board board);

    /**
     * Sets the current player in the view
     *
     * @param color The color of the current player (as defined in PieceColor)
     */
    void setCurrentPlayer(int color);

    /**
     * Displays a game over message
     *
     * @param message The game over message to display
     */
    void showGameOverMessage(String message);
}