package Controller;

import Model.Piece;
import Model.board.Board;
import Model.board.Square;
import Model.pieces.King;
import Model.rules.GameRulesEngine;

import java.util.LinkedList;
import java.util.List;

/**
 * Controller component of the Chess game that detects check and checkmate conditions
 * and provides this information to the user interface.
 *
 * @author Jussi Lundstedt
 */
public class CheckmateDetector {
    private GameRulesEngine rulesEngine;
    private LinkedList<Square> movableSquares;

    /**
     * Constructs a new instance of Controller.CheckmateDetector on a given board.
     *
     * @param board The board which the detector monitors
     * @param whitePieces White pieces on the board
     * @param blackPieces Black pieces on the board
     * @param whiteKing Piece object representing the white king
     * @param blackKing Piece object representing the black king
     */
    public CheckmateDetector(Board board, LinkedList<Piece> whitePieces,
                             LinkedList<Piece> blackPieces, King whiteKing, King blackKing) {
        this.rulesEngine = new GameRulesEngine(board, whitePieces, blackPieces, whiteKing, blackKing);
        this.movableSquares = new LinkedList<Square>();
    }

    /**
     * Updates the detector with the current state of the game.
     */
    public void update() {
        rulesEngine.update();
    }

    /**
     * Checks if the black king is threatened
     * @return boolean representing whether the black king is in check.
     */
    public boolean blackInCheck() {
        return rulesEngine.isBlackInCheck();
    }

    /**
     * Checks if the white king is threatened
     * @return boolean representing whether the white king is in check.
     */
    public boolean whiteInCheck() {
        return rulesEngine.isWhiteInCheck();
    }

    /**
     * Checks whether black is in checkmate.
     * @return boolean representing if black player is checkmated.
     */
    public boolean blackCheckMated() {
        return rulesEngine.isBlackCheckmated();
    }

    /**
     * Checks whether white is in checkmate.
     * @return boolean representing if white player is checkmated.
     */
    public boolean whiteCheckMated() {
        return rulesEngine.isWhiteCheckmated();
    }

    /**
     * Method to get a list of allowable squares that the player can move.
     * Defaults to all squares, but limits available squares if player is in
     * check.
     * @param isWhite boolean representing whether it's white player's turn
     * @return List of squares that the player can move into.
     */
    public List<Square> getAllowableSquares(boolean isWhite) {
        movableSquares.clear();

        if (isWhite) {
            movableSquares.addAll(rulesEngine.getCheckEscapeMoves(true));
        } else {
            movableSquares.addAll(rulesEngine.getCheckEscapeMoves(false));
        }

        return movableSquares;
    }

    /**
     * Tests a move a player is about to make to prevent making an illegal move
     * that puts the player in check.
     * @param p Piece moved
     * @param sq Square to which p is about to move
     * @return false if move would cause a check
     */
    public boolean testMove(Piece p, Square sq) {
        return rulesEngine.testMove(p, sq);
    }
}