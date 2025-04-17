package Testers;

import Model.Board;
import Model.GameState;
import Model.Move;
import Model.PieceColor;
import Model.Position;
import Model.pieces.*;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class GameStateTest {
    private Board board;
    private GameState gameState;

    @Before
    public void setUp() {
        // Create a fresh board for each test
        board = new Board();
        gameState = new GameState();

        // Clear the board to set up specific positions
        board.clearBoard();
    }

    @Test
    public void testCheckDetection() {
        // Set up a simple check position
        // White king at e1, Black queen at e2 giving check
        King whiteKing = new King(PieceColor.WHITE, new Position(4, 7));
        Queen blackQueen = new Queen(PieceColor.BLACK, new Position(4, 6));

        board.placePieceForTesting(whiteKing);
        board.placePieceForTesting(blackQueen);

        // Verify the king is in check
        assertTrue("White king should be in check", board.isKingInCheck(PieceColor.WHITE));

        // Add a piece to block the check
        Rook whiteRook = new Rook(PieceColor.WHITE, new Position(4, 6));
        board.placePieceForTesting(whiteRook);

        // King should no longer be in check
        assertFalse("White king should not be in check after blocking", board.isKingInCheck(PieceColor.WHITE));
    }

    @Test
    public void testMultipleCheckingPieces() {
        // Set up a position with multiple checking pieces
        King whiteKing = new King(PieceColor.WHITE, new Position(4, 7));
        Queen blackQueen = new Queen(PieceColor.BLACK, new Position(4, 5));
        Bishop blackBishop = new Bishop(PieceColor.BLACK, new Position(6, 5));

        board.placePieceForTesting(whiteKing);
        board.placePieceForTesting(blackQueen);
        board.placePieceForTesting(blackBishop);

        // Verify the king is in check
        assertTrue("White king should be in check from multiple pieces", board.isKingInCheck(PieceColor.WHITE));

        // Blocking one attacker shouldn't remove check
        Rook whiteRook = new Rook(PieceColor.WHITE, new Position(4, 6));
        board.placePieceForTesting(whiteRook);

        // King should still be in check from the bishop
        assertTrue("White king should still be in check from bishop", board.isKingInCheck(PieceColor.WHITE));
    }

    @Test
    public void testCheckmatePosition() {
        // Set up a simple checkmate position (back rank mate)
        King blackKing = new King(PieceColor.BLACK, new Position(4, 0));
        Pawn blackPawn1 = new Pawn(PieceColor.BLACK, new Position(3, 1));
        Pawn blackPawn2 = new Pawn(PieceColor.BLACK, new Position(4, 1));
        Pawn blackPawn3 = new Pawn(PieceColor.BLACK, new Position(5, 1));
        Rook whiteRook = new Rook(PieceColor.WHITE, new Position(0, 0));

        board.placePieceForTesting(blackKing);
        board.placePieceForTesting(blackPawn1);
        board.placePieceForTesting(blackPawn2);
        board.placePieceForTesting(blackPawn3);
        board.placePieceForTesting(whiteRook);

        // Verify the king is in checkmate
        assertTrue("Black king should be in check", board.isKingInCheck(PieceColor.BLACK));
        assertTrue("Black king should be in checkmate", board.isCheckmate(PieceColor.BLACK));
    }

    @Test
    public void testNonCheckmatePosition() {
        // Set up a check position that is not checkmate
        King blackKing = new King(PieceColor.BLACK, new Position(4, 0));
        Queen whiteQueen = new Queen(PieceColor.WHITE, new Position(4, 2));

        board.placePieceForTesting(blackKing);
        board.placePieceForTesting(whiteQueen);

        // Verify the king is in check but not checkmate (king can move)
        assertTrue("Black king should be in check", board.isKingInCheck(PieceColor.BLACK));
        assertFalse("Black king should not be in checkmate", board.isCheckmate(PieceColor.BLACK));
    }

    @Test
    public void testStalematePosition() {
        // Set up a simple stalemate position
        // Black king at a8, White queen at c7
        King blackKing = new King(PieceColor.BLACK, new Position(0, 0));
        Queen whiteQueen = new Queen(PieceColor.WHITE, new Position(2, 1));

        board.placePieceForTesting(blackKing);
        board.placePieceForTesting(whiteQueen);

        // Verify it's stalemate (king not in check but has no legal moves)
        assertFalse("Black king should not be in check", board.isKingInCheck(PieceColor.BLACK));
        assertTrue("Position should be stalemate", board.isStalemate(PieceColor.BLACK));
    }

    @Test
    public void testNonStalematePosition() {
        // Set up a position where the king has legal moves
        King blackKing = new King(PieceColor.BLACK, new Position(4, 4));
        Queen whiteQueen = new Queen(PieceColor.WHITE, new Position(0, 0));

        board.placePieceForTesting(blackKing);
        board.placePieceForTesting(whiteQueen);

        // Verify it's not stalemate
        assertFalse("Position should not be stalemate", board.isStalemate(PieceColor.BLACK));
    }

    @Test
    public void testEscapingCheck() {
        // Set up a position where the king can escape check
        King blackKing = new King(PieceColor.BLACK, new Position(4, 4));
        Rook whiteRook = new Rook(PieceColor.WHITE, new Position(4, 0));

        board.placePieceForTesting(blackKing);
        board.placePieceForTesting(whiteRook);

        // Verify the king is in check
        assertTrue("Black king should be in check", board.isKingInCheck(PieceColor.BLACK));

        // King can move to escape
        assertFalse("Black king should not be in checkmate", board.isCheckmate(PieceColor.BLACK));
    }

    @Test
    public void testCapturingCheckingPiece() {
        // Set up a position where a checking piece can be captured
        King blackKing = new King(PieceColor.BLACK, new Position(4, 4));
        Bishop blackBishop = new Bishop(PieceColor.BLACK, new Position(3, 3));
        Bishop whiteRook = new Bishop(PieceColor.WHITE, new Position(4, 6));

        board.placePieceForTesting(blackKing);
        board.placePieceForTesting(blackBishop);
        board.placePieceForTesting(whiteRook);

        // Verify the king is in check
        assertTrue("Black king should be in check", board.isKingInCheck(PieceColor.BLACK));

        // Check can be escaped by capturing
        assertFalse("Black king should not be in checkmate", board.isCheckmate(PieceColor.BLACK));
    }

    @Test
    public void testBlockingCheck() {
        // Set up a position where check can be blocked
        King blackKing = new King(PieceColor.BLACK, new Position(4, 4));
        Queen blackQueen = new Queen(PieceColor.BLACK, new Position(0, 0));
        Rook whiteRook = new Rook(PieceColor.WHITE, new Position(4, 0));

        board.placePieceForTesting(blackKing);
        board.placePieceForTesting(blackQueen);
        board.placePieceForTesting(whiteRook);

        // Verify the king is in check
        assertTrue("Black king should be in check", board.isKingInCheck(PieceColor.BLACK));

        // Check can be blocked
        assertFalse("Black king should not be in checkmate", board.isCheckmate(PieceColor.BLACK));
    }
}