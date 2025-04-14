package Model.pieces;

import Model.Board;
import Model.Square;

import java.util.LinkedList;
import java.util.List;

/**
 * Represents a Pawn chess piece with its specific movement rules
 */
public class Pawn extends Piece {
    private boolean wasMoved;

    /**
     * Constructor for creating a Pawn piece
     *
     * @param color The piece color (0 for white, 1 for black)
     * @param initSq The initial square position
     * @param img_file The image file path for the piece representation
     */
    public Pawn(int color, Square initSq, String img_file) {
        super(color, initSq, img_file);
        this.wasMoved = false;
    }

    /**
     * Moves the pawn to the destination square and updates its movement status
     *
     * @param destination The square to move to
     * @return true if move was successful, false otherwise
     */
    @Override
    public boolean move(Square destination) {
        boolean moveSuccessful = super.move(destination);
        if (moveSuccessful) {
            wasMoved = true;
        }
        return moveSuccessful;
    }

    /**
     * Calculates all legal moves for this pawn on the current board
     *
     * @param board The current game board
     * @return List of squares the pawn can legally move to
     */
    @Override
    public List<Square> getLegalMoves(Board board) {
        LinkedList<Square> legalMoves = new LinkedList<>();
        Square[][] squares = board.getSquareArray();
        int x = this.getPosition().getXNum();
        int y = this.getPosition().getYNum();
        int color = this.getColor();

        // White pawns move up the board (increasing y)
        if (color == 0) {
            addForwardMovesWhite(squares, x, y, legalMoves);
            addDiagonalCapturesWhite(squares, x, y, legalMoves);
        }
        // Black pawns move down the board (decreasing y)
        else {
            addForwardMovesBlack(squares, x, y, legalMoves);
            addDiagonalCapturesBlack(squares, x, y, legalMoves);
        }

        return legalMoves;
    }

    /**
     * Adds forward movement squares for white pawns
     *
     * @param squares The board squares array
     * @param x Current x-coordinate
     * @param y Current y-coordinate
     * @param legalMoves List to add legal moves to
     */
    private void addForwardMovesWhite(Square[][] squares, int x, int y, List<Square> legalMoves) {
        // Check if one square ahead is within bounds and unoccupied
        if (y + 1 < 8 && !squares[y + 1][x].isOccupied()) {
            legalMoves.add(squares[y + 1][x]);

            // If pawn hasn't moved yet, it can move two squares
            if (!wasMoved && y + 2 < 8 && !squares[y + 2][x].isOccupied()) {
                legalMoves.add(squares[y + 2][x]);
            }
        }
    }

    /**
     * Adds diagonal capture squares for white pawns
     *
     * @param squares The board squares array
     * @param x Current x-coordinate
     * @param y Current y-coordinate
     * @param legalMoves List to add legal moves to
     */
    private void addDiagonalCapturesWhite(Square[][] squares, int x, int y, List<Square> legalMoves) {
        // Check right diagonal capture
        if (x + 1 < 8 && y + 1 < 8) {
            if (squares[y + 1][x + 1].isOccupied() &&
                    squares[y + 1][x + 1].getOccupyingPiece().getColor() != this.getColor()) {
                legalMoves.add(squares[y + 1][x + 1]);
            }
        }

        // Check left diagonal capture
        if (x - 1 >= 0 && y + 1 < 8) {
            if (squares[y + 1][x - 1].isOccupied() &&
                    squares[y + 1][x - 1].getOccupyingPiece().getColor() != this.getColor()) {
                legalMoves.add(squares[y + 1][x - 1]);
            }
        }
    }

    /**
     * Adds forward movement squares for black pawns
     *
     * @param squares The board squares array
     * @param x Current x-coordinate
     * @param y Current y-coordinate
     * @param legalMoves List to add legal moves to
     */
    private void addForwardMovesBlack(Square[][] squares, int x, int y, List<Square> legalMoves) {
        // Check if one square ahead is within bounds and unoccupied
        if (y - 1 >= 0 && !squares[y - 1][x].isOccupied()) {
            legalMoves.add(squares[y - 1][x]);

            // If pawn hasn't moved yet, it can move two squares
            if (!wasMoved && y - 2 >= 0 && !squares[y - 2][x].isOccupied()) {
                legalMoves.add(squares[y - 2][x]);
            }
        }
    }

    /**
     * Adds diagonal capture squares for black pawns
     *
     * @param squares The board squares array
     * @param x Current x-coordinate
     * @param y Current y-coordinate
     * @param legalMoves List to add legal moves to
     */
    private void addDiagonalCapturesBlack(Square[][] squares, int x, int y, List<Square> legalMoves) {
        // Check right diagonal capture
        if (x + 1 < 8 && y - 1 >= 0) {
            if (squares[y - 1][x + 1].isOccupied() &&
                    squares[y - 1][x + 1].getOccupyingPiece().getColor() != this.getColor()) {
                legalMoves.add(squares[y - 1][x + 1]);
            }
        }

        // Check left diagonal capture
        if (x - 1 >= 0 && y - 1 >= 0) {
            if (squares[y - 1][x - 1].isOccupied() &&
                    squares[y - 1][x - 1].getOccupyingPiece().getColor() != this.getColor()) {
                legalMoves.add(squares[y - 1][x - 1]);
            }
        }
    }

    @Override
    public String toString() {
        return getColor() == 0 ? "White Pawn" : "Black Pawn";
    }
}