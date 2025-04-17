package Testers;
import Model.pieces.*;
import Model.*;
import org.junit.Before;
import org.junit.Test;
import java.util.List;

import static org.junit.Assert.*;

public class BishopTest {

    private Board board;

    @Before
    public void setUp() {
        board = new Board();
        // Clear the board for isolated testing
        board.clearBoard();
    }

    @Test
    public void testBishopInitialState() {
        // Place a bishop on the board
        Bishop bishop = new Bishop(PieceColor.WHITE, new Position(2, 7));
        board.placePieceForTesting(bishop);

        // Verify bishop properties
        assertEquals(PieceColor.WHITE, bishop.getColor());
        assertEquals("Bishop", bishop.getType());
        assertEquals(new Position(2, 7), bishop.getPosition());
        assertFalse(bishop.hasMoved());
    }

    @Test
    public void testBishopMovementEmptyBoard() {
        // Place a bishop in the middle of an empty board
        Bishop bishop = new Bishop(PieceColor.WHITE, new Position(3, 3));
        board.placePieceForTesting(bishop);

        // Get legal moves
        List<Move> legalMoves = bishop.getLegalMoves(board);

        // Bishop should have 13 possible moves on an empty board from the center
        // 7 moves in the two positive diagonals, 7 moves in the two negative diagonals, minus the current position
        assertEquals(13, legalMoves.size());

        // Verify diagonal moves in all directions
        // Northeast diagonal
        assertTrue(containsMove(legalMoves, new Position(4, 2)));
        assertTrue(containsMove(legalMoves, new Position(5, 1)));
        assertTrue(containsMove(legalMoves, new Position(6, 0)));

        // Southeast diagonal
        assertTrue(containsMove(legalMoves, new Position(4, 4)));
        assertTrue(containsMove(legalMoves, new Position(5, 5)));
        assertTrue(containsMove(legalMoves, new Position(6, 6)));
        assertTrue(containsMove(legalMoves, new Position(7, 7)));

        // Southwest diagonal
        assertTrue(containsMove(legalMoves, new Position(2, 4)));
        assertTrue(containsMove(legalMoves, new Position(1, 5)));
        assertTrue(containsMove(legalMoves, new Position(0, 6)));

        // Northwest diagonal
        assertTrue(containsMove(legalMoves, new Position(2, 2)));
        assertTrue(containsMove(legalMoves, new Position(1, 1)));
        assertTrue(containsMove(legalMoves, new Position(0, 0)));
    }

    @Test
    public void testBishopObstructedByPieces() {
        // Place a bishop on the board
        Bishop bishop = new Bishop(PieceColor.WHITE, new Position(2, 2));
        board.placePieceForTesting(bishop);

        // Place some pieces to obstruct the bishop's path
        Pawn friendlyPawn = new Pawn(PieceColor.WHITE, new Position(4, 4));
        board.placePieceForTesting(friendlyPawn);

        Pawn enemyPawn = new Pawn(PieceColor.BLACK, new Position(0, 0));
        board.placePieceForTesting(enemyPawn);

        // Get legal moves
        List<Move> legalMoves = bishop.getLegalMoves(board);

        // Check what moves should and shouldn't be allowed

        // Can't move past friendly piece
        assertTrue(containsMove(legalMoves, new Position(3, 3)));
        assertFalse(containsMove(legalMoves, new Position(4, 4))); // occupied by friendly piece
        assertFalse(containsMove(legalMoves, new Position(5, 5))); // beyond friendly piece

        // Can capture enemy piece but not move past it
        assertTrue(containsMove(legalMoves, new Position(0, 0))); // can capture enemy pawn

        // Other diagonals should be unaffected
        assertTrue(containsMove(legalMoves, new Position(3, 1)));
        assertTrue(containsMove(legalMoves, new Position(4, 0)));
        assertTrue(containsMove(legalMoves, new Position(1, 3)));
        assertTrue(containsMove(legalMoves, new Position(0, 4)));
    }

    @Test
    public void testBishopCapture() {
        // Place a bishop on the board
        Bishop bishop = new Bishop(PieceColor.WHITE, new Position(3, 3));
        board.placePieceForTesting(bishop);

        // Place enemy pieces to capture
        Pawn enemyPawn1 = new Pawn(PieceColor.BLACK, new Position(5, 5));
        board.placePieceForTesting(enemyPawn1);

        Pawn enemyPawn2 = new Pawn(PieceColor.BLACK, new Position(1, 5));
        board.placePieceForTesting(enemyPawn2);

        // Get legal moves
        List<Move> legalMoves = bishop.getLegalMoves(board);

        // Verify capture moves
        Move captureMove1 = findMove(legalMoves, new Position(5, 5));
        assertNotNull("Bishop should be able to capture enemy pawn at (5, 5)", captureMove1);
        assertEquals(enemyPawn1, captureMove1.getTakenPiece());

        Move captureMove2 = findMove(legalMoves, new Position(1, 5));
        assertNotNull("Bishop should be able to capture enemy pawn at (1, 5)", captureMove2);
        assertEquals(enemyPawn2, captureMove2.getTakenPiece());
    }

    @Test
    public void testBishopMovementPreventingCheck() {
        // Place a white king and bishop on the board
        King whiteKing = new King(PieceColor.WHITE, new Position(4, 7));
        board.placePieceForTesting(whiteKing);

        Bishop whiteBishop = new Bishop(PieceColor.WHITE, new Position(5, 6));
        board.placePieceForTesting(whiteBishop);

        // Place a black rook that can check the white king if the bishop moves
        Rook blackRook = new Rook(PieceColor.BLACK, new Position(4, 0));
        board.placePieceForTesting(blackRook);

        // Get legal moves for the bishop
        List<Move> legalMoves = whiteBishop.getLegalMoves(board);

        // Bishop should not be able to move along the diagonal that would leave the king in check
        assertFalse("Bishop should not be able to move to (6, 5) as it would expose king to check",
                containsMove(legalMoves, new Position(6, 5)));
        assertFalse("Bishop should not be able to move to (7, 4) as it would expose king to check",
                containsMove(legalMoves, new Position(7, 4)));

        // Bishop should be able to move to block the check or in other directions
        assertTrue("Bishop should be able to move to (4, 5) to block check",
                containsMove(legalMoves, new Position(4, 5)));
        assertTrue("Bishop should be able to move to (3, 4) in another direction",
                containsMove(legalMoves, new Position(3, 4)));
    }

    @Test
    public void testBishopAttackPositions() {
        // Place a bishop on the board
        Bishop bishop = new Bishop(PieceColor.WHITE, new Position(3, 3));
        board.placePieceForTesting(bishop);

        // Place some pieces to obstruct
        Pawn friendlyPawn = new Pawn(PieceColor.WHITE, new Position(5, 5));
        board.placePieceForTesting(friendlyPawn);

        Pawn enemyPawn = new Pawn(PieceColor.BLACK, new Position(1, 1));
        board.placePieceForTesting(enemyPawn);

        // Get attack positions
        List<Position> attackPositions = bishop.getAttackPositions(board);

        // Bishop should attack along diagonals, including positions with pieces
        // but not beyond them

        // Northeast diagonal - up to friendly pawn
        assertTrue(attackPositions.contains(new Position(4, 2)));

        // Southeast diagonal - up to and including friendly pawn position
        assertTrue(attackPositions.contains(new Position(4, 4)));
        assertTrue(attackPositions.contains(new Position(5, 5)));
        assertFalse(attackPositions.contains(new Position(6, 6)));

        // Southwest diagonal
        assertTrue(attackPositions.contains(new Position(2, 4)));
        assertTrue(attackPositions.contains(new Position(1, 5)));
        assertTrue(attackPositions.contains(new Position(0, 6)));

        // Northwest diagonal - up to and including enemy pawn position
        assertTrue(attackPositions.contains(new Position(2, 2)));
        assertTrue(attackPositions.contains(new Position(1, 1)));
        assertFalse(attackPositions.contains(new Position(0, 0)));
    }

    @Test
    public void testBishopDuplicate() {
        // Create a bishop and mark it as moved
        Bishop original = new Bishop(PieceColor.BLACK, new Position(2, 0));
        original.setHasMoved(true);

        // Duplicate the bishop
        Piece duplicate = original.duplicate();

        // Verify duplicate properties
        assertTrue(duplicate instanceof Bishop);
        assertEquals(PieceColor.BLACK, duplicate.getColor());
        assertEquals(new Position(2, 0), duplicate.getPosition());
        assertTrue(duplicate.hasMoved());
        assertEquals("Bishop", duplicate.getType());
    }

    @Test
    public void testBishopFromCorner() {
        // Place a bishop in the corner
        Bishop bishop = new Bishop(PieceColor.WHITE, new Position(0, 0));
        board.placePieceForTesting(bishop);

        // Get legal moves
        List<Move> legalMoves = bishop.getLegalMoves(board);

        // Bishop should have 7 possible moves on the diagonal from the corner
        assertEquals(7, legalMoves.size());

        // Verify all moves are on the diagonal
        assertTrue(containsMove(legalMoves, new Position(1, 1)));
        assertTrue(containsMove(legalMoves, new Position(2, 2)));
        assertTrue(containsMove(legalMoves, new Position(3, 3)));
        assertTrue(containsMove(legalMoves, new Position(4, 4)));
        assertTrue(containsMove(legalMoves, new Position(5, 5)));
        assertTrue(containsMove(legalMoves, new Position(6, 6)));
        assertTrue(containsMove(legalMoves, new Position(7, 7)));
    }

    // Helper method to check if a move to the specified position exists in the list
    private boolean containsMove(List<Move> moves, Position destination) {
        for (Move move : moves) {
            if (move.getDestination().equals(destination)) {
                return true;
            }
        }
        return false;
    }

    // Helper method to find a move to the specified position
    private Move findMove(List<Move> moves, Position destination) {
        for (Move move : moves) {
            if (move.getDestination().equals(destination)) {
                return move;
            }
        }
        return null;
    }
}