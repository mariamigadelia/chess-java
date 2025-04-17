package View;

import Controller.GameController;
import Model.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

/**
 * UI component that renders the chess board and handles user interactions with it.
 */
public class ChessBoardUI extends JPanel {
    private static final int BOARD_SIZE = 8;
    private static final int SQUARE_SIZE = 75; // Size of each square in pixels

    private GameController controller;
    private ChessSquareView[][] squares;
    private Position selectedPosition;
    private List<Position> legalMovePositions;
    private Move lastMove;

    /**
     * Constructs a ChessBoardUI with the given controller.
     *
     * @param controller The chess game controller
     */
    public ChessBoardUI(GameController controller) {
        this.controller = controller;
        this.selectedPosition = null;
        this.legalMovePositions = null;
        this.lastMove = null;

        // Set layout and appearance
        setLayout(new GridLayout(BOARD_SIZE, BOARD_SIZE));
        setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        setPreferredSize(new Dimension(BOARD_SIZE * SQUARE_SIZE, BOARD_SIZE * SQUARE_SIZE));

        // Create the chess board squares
        initializeBoard();
    }

    /**
     * Initialize the chess board with square views.
     */
    private void initializeBoard() {
        squares = new ChessSquareView[BOARD_SIZE][BOARD_SIZE];

        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                boolean isLight = (row + col) % 2 == 0;

                Position position = new Position(col, row);
                squares[row][col] = new ChessSquareView(position, isLight);

                // Add click listener to the square
                squares[row][col].addMouseListener(new SquareClickListener(position));

                add(squares[row][col]);
            }
        }
    }

    /**
     * Updates the board UI to reflect the current game state.
     */
    public void updateBoard() {
        Board board = controller.getBoard();
        lastMove = board.getLastMove();

        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                Position pos = new Position(col, row);
                Piece piece = board.getPiece(pos);
                squares[row][col].setOccupyingPiece(piece);

                // Reset visual state first
                squares[row][col].resetVisualState();

                // Check if this square is selected
                boolean isSelected = (selectedPosition != null &&
                        selectedPosition.getColumn() == col &&
                        selectedPosition.getRow() == row);
                squares[row][col].setSelected(isSelected);

                // Check if this square is a legal move
                boolean isLegalMove = false;
                if (legalMovePositions != null) {
                    for (Position move : legalMovePositions) {
                        if (move != null && move.getColumn() == col && move.getRow() == row) {
                            isLegalMove = true;
                            break;
                        }
                    }
                }
                squares[row][col].setLegalMove(isLegalMove);

                // Highlight last move
                if (lastMove != null) {
                    Position origin = lastMove.getOrigin();
                    Position destination = lastMove.getDestination();

                    if ((origin.getColumn() == col && origin.getRow() == row) ||
                            (destination.getColumn() == col && destination.getRow() == row)) {
                        squares[row][col].setLastMove(true);
                    }
                }
            }
        }

        repaint();
    }

    /**
     * Gets the last move made on the board.
     *
     * @return The last move, or null if no moves have been made
     */
    public Move getLastMove() {
        return lastMove;
    }

    /**
     * Handles a click on a chess square.
     *
     * @param position The position of the clicked square
     */
    private void handleSquareClick(Position position) {
        // If game is over, do nothing
        if (controller.isGameOver()) {
            return;
        }

        // Get the piece at the clicked position
        Piece clickedPiece = controller.getPieceAt(position);

        // If a piece is already selected
        if (selectedPosition != null) {
            // If clicking the same piece, deselect it
            if (position.equals(selectedPosition)) {
                selectedPosition = null;
                legalMovePositions = null;
                updateBoard();
                return;
            }

            // Check if the clicked position is a legal move
            boolean isLegalMove = false;
            if (legalMovePositions != null) {
                for (Position movePos : legalMovePositions) {
                    if (movePos.equals(position)) {
                        isLegalMove = true;
                        break;
                    }
                }
            }

            // If it's a legal move, make the move
            if (isLegalMove) {
                boolean moveSuccessful = controller.makeMove(selectedPosition, position);

                if (moveSuccessful) {
                    // Move was successful, update the last move
                    lastMove = controller.getBoard().getLastMove();
                }

                selectedPosition = null;
                legalMovePositions = null;
                updateBoard();
                return;
            }

            // If clicking another piece of the same color, select that piece instead
            if (clickedPiece != null && clickedPiece.getColor() == controller.getCurrentTurn()) {
                selectedPosition = position;
                legalMovePositions = controller.getLegalMovePositions(position);
                updateBoard();
                return;
            }

            // Otherwise, deselect
            selectedPosition = null;
            legalMovePositions = null;
            updateBoard();
            return;
        }

        // If no piece is selected and a piece of the current player's color is clicked
        if (clickedPiece != null && clickedPiece.getColor() == controller.getCurrentTurn()) {
            selectedPosition = position;
            legalMovePositions = controller.getLegalMovePositions(position);
            updateBoard();
        }
    }

    /**
     * Clears any selections on the board.
     */
    public void clearSelection() {
        selectedPosition = null;
        legalMovePositions = null;
        updateBoard();
    }

    /**
     * Mouse listener for chess squares.
     */
    private class SquareClickListener extends MouseAdapter {
        private Position position;

        public SquareClickListener(Position position) {
            this.position = position;
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            handleSquareClick(position);
        }
    }

    /**
     * Makes the board flipped (from black's perspective) or normal (from white's perspective)
     *
     * @param flipped True to show board from black's perspective
     */
    public void setFlipped(boolean flipped) {
        removeAll();

        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                int displayRow = flipped ? (BOARD_SIZE - 1 - row) : row;
                int displayCol = flipped ? (BOARD_SIZE - 1 - col) : col;

                add(squares[displayRow][displayCol]);
            }
        }

        revalidate();
        repaint();
    }
}