package Testers;
import Model.pieces.*;
import Model.*;
import org.junit.Before;
import org.junit.Test;
import java.util.List;

import static org.junit.Assert.*;

public class KnightTest {

    private Board board;

    @Before
    public void setUp() {
        board = new Board();
        // Clear the board for isolated testing
        board.clearBoard();
    }

    @Test
    public void testKnightInitialState() {
        // Place a knight on the board
        Knight knight = new Knight(PieceColor.WHITE, new Position(1, 7));
        board.placePieceForTesting(knight);

        // Verify knight properties
        assertEquals(PieceColor.WHITE, knight.getColor());
        assertEquals("Knight", knight.getType());
        assertEquals(new Position(1, 7), knight.getPosition());
        assertFalse(knight.hasMoved());
    }

    @Test
    public void testKnightMovementEmptyBoard() {
        // Place a knight in the middle of an empty board
        Knight knight = new Knight(PieceColor.WHITE, new Position(3, 3));
        board.placePieceForTesting(knight);

        // Get legal moves
        List<Move> legalMoves = knight.getLegalMoves(board);

        // Knight should have 8 possible moves from the center
        assertEquals(8, legalMoves.size());

        // Verify all L-shaped moves
        assertTrue(containsMove(legalMoves, new Position(1, 2))); // 2 left, 1 up
        assertTrue(containsMove(legalMoves, new Position(1, 4))); // 2 left, 1 down
        assertTrue(containsMove(legalMoves, new Position(2, 1))); // 1 left, 2 up
        assertTrue(containsMove(legalMoves, new Position(2, 5))); // 1 left, 2 down
        assertTrue(containsMove(legalMoves, new Position(4, 1))); // 1 right, 2 up
        assertTrue(containsMove(legalMoves, new Position(4, 5))); // 1 right, 2 down
        assertTrue(containsMove(legalMoves, new Position(5, 2))); // 2 right, 1 up
        assertTrue(containsMove(legalMoves, new Position(5, 4))); // 2 right, 1 down
    }

    @Test
    public void testKnightMovementFromCorner() {
        // Place a knight in the corner of an empty board
        Knight knight = new Knight(PieceColor.WHITE, new Position(0, 0));
        board.placePieceForTesting(knight);

        // Get legal moves
        List<Move> legalMoves = knight.getLegalMoves(board);

        // Knight should have only 2 possible moves from the corner
        assertEquals(2, legalMoves.size());

        // Verify the two possible L-shaped moves
        assertTrue(containsMove(legalMoves, new Position(1, 2))); // 1 right, 2 down
        assertTrue(containsMove(legalMoves, new Position(2, 1))); // 2 right, 1 down
    }

    @Test
    public void testKnightMovementFromEdge() {
        // Place a knight on the edge of an empty board
        Knight knight = new Knight(PieceColor.WHITE, new Position(0, 3));
        board.placePieceForTesting(knight);

        // Get legal moves
        List<Move> legalMoves = knight.getLegalMoves(board);

        // Knight should have 4 possible moves from this edge position
        assertEquals(4, legalMoves.size());

        // Verify the four possible L-shaped moves
        assertTrue(containsMove(legalMoves, new Position(1, 1))); // 1 right, 2 up
        assertTrue(containsMove(legalMoves, new Position(1, 5))); // 1 right, 2 down
        assertTrue(containsMove(legalMoves, new Position(2, 2))); // 2 right, 1 up
        assertTrue(containsMove(legalMoves, new Position(2, 4))); // 2 right, 1 down
    }

    @Test
    public void testKnightObstructedByPieces() {
        // Place a knight on the board
        Knight knight = new Knight(PieceColor.WHITE, new Position(4, 4));
        board.placePieceForTesting(knight);

        // Place some pieces that don't obstruct the knight (knights jump over pieces)
        Pawn friendlyPawn1 = new Pawn(PieceColor.WHITE, new Position(3, 4));
        board.placePieceForTesting(friendlyPawn1);

        Pawn enemyPawn1 = new Pawn(PieceColor.BLACK, new Position(5, 4));
        board.placePieceForTesting(enemyPawn1);

        // Place pieces at destinations
        Pawn friendlyPawn2 = new Pawn(PieceColor.WHITE, new Position(6, 5));
        board.placePieceForTesting(friendlyPawn2);

        Pawn enemyPawn2 = new Pawn(PieceColor.BLACK, new Position(6, 3));
        board.placePieceForTesting(enemyPawn2);

        // Get legal moves
        List<Move> legalMoves = knight.getLegalMoves(board);

        // Knight should be able to jump over the pieces
        assertTrue(containsMove(legalMoves, new Position(2, 3)));
        assertTrue(containsMove(legalMoves, new Position(2, 5)));

        // Cannot move to square with friendly piece
        assertFalse(containsMove(legalMoves, new Position(6, 5)));

        // Can capture enemy piece
        assertTrue(containsMove(legalMoves, new Position(6, 3)));

        // Verify that move to capture includes the captured piece
        Move captureMove = findMove(legalMoves, new Position(6, 3));
        assertNotNull(captureMove);
        assertEquals(enemyPawn2, captureMove.getTakenPiece());
    }

    @Test
    public void testKnightCapture() {
        // Place a knight on the board
        Knight knight = new Knight(PieceColor.WHITE, new Position(3, 3));
        board.placePieceForTesting(knight);

        // Place enemy pieces to capture
        Pawn enemyPawn1 = new Pawn(PieceColor.BLACK, new Position(5, 4));
        board.placePieceForTesting(enemyPawn1);

        Pawn enemyPawn2 = new Pawn(PieceColor.BLACK, new Position(1, 2));
        board.placePieceForTesting(enemyPawn2);

        // Get legal moves
        List<Move> legalMoves = knight.getLegalMoves(board);

        // Verify capture moves
        Move captureMove1 = findMove(legalMoves, new Position(5, 4));
        assertNotNull("Knight should be able to capture enemy pawn at (5, 4)", captureMove1);
        assertEquals(enemyPawn1, captureMove1.getTakenPiece());

        Move captureMove2 = findMove(legalMoves, new Position(1, 2));
        assertNotNull("Knight should be able to capture enemy pawn at (1, 2)", captureMove2);
        assertEquals(enemyPawn2, captureMove2.getTakenPiece());
    }

    @Test
    public void testKnightMovementPreventingCheck() {
        // Place a white king and knight on the board
        King whiteKing = new King(PieceColor.WHITE, new Position(4, 7));
        board.placePieceForTesting(whiteKing);

        Knight whiteKnight = new Knight(PieceColor.WHITE, new Position(3, 6));
        board.placePieceForTesting(whiteKnight);

        // Place a black rook that is blocked by the knight
        Rook blackRook = new Rook(PieceColor.BLACK, new Position(3, 0));
        board.placePieceForTesting(blackRook);

        // Get legal moves for the knight
        List<Move> legalMoves = whiteKnight.getLegalMoves(board);

        // Knight should not be able to move away as it would leave the king in check
        assertFalse("Knight should not be able to move to (1, 5) as it would expose king to check",
                containsMove(legalMoves, new Position(1, 5)));
        assertFalse("Knight should not be able to move to (1, 7) as it would expose king to check",
                containsMove(legalMoves, new Position(1, 7)));

        // Knight should be able to capture the rook to prevent check
        assertTrue("Knight should be able to capture the rook at (3, 0)",
                containsMove(legalMoves, new Position(3, 0)));

        // Knight should be able to move to positions that don't expose the king
        assertTrue("Knight should be able to move to (5, 5) safely",
                containsMove(legalMoves, new Position(5, 5)));
    }

    @Test
    public void testKnightJumpingAbilityUnaffectedByPiecesBetween() {
        // Place a knight on the board
        Knight knight = new Knight(PieceColor.WHITE, new Position(4, 4));
        board.placePieceForTesting(knight);

        // Surround the knight with pieces that don't block its path
        for (int row = 3; row <= 5; row++) {
            for (int col = 3; col <= 5; col++) {
                if (!(row == 4 && col == 4)) { // Skip the knight's position
                    Pawn pawn = new Pawn(PieceColor.BLACK, new Position(col, row));
                    board.placePieceForTesting(pawn);
                }
            }
        }

        // Get legal moves
        List<Move> legalMoves = knight.getLegalMoves(board);

        // Knight should still have all 8 L-moves available despite being surrounded
        // because it jumps over pieces
        assertEquals(8, legalMoves.size());
    }

    @Test
    public void testKnightAttackPositions() {
        // Place a knight on the board
        Knight knight = new Knight(PieceColor.WHITE, new Position(3, 3));
        board.placePieceForTesting(knight);

        // Place some pieces that don't affect knight's attack positions
        Pawn friendlyPawn = new Pawn(PieceColor.WHITE, new Position(3, 4));
        board.placePieceForTesting(friendlyPawn);

        Pawn enemyPawn = new Pawn(PieceColor.BLACK, new Position(4, 3));
        board.placePieceForTesting(enemyPawn);

        // Get attack positions
        List<Position> attackPositions = knight.getAttackPositions(board);

        // Knight should attack all 8 L-pattern positions, regardless of piece occupancy
        assertEquals(8, attackPositions.size());

        // Verify all L-shaped attack positions
        assertTrue(attackPositions.contains(new Position(1, 2)));
        assertTrue(attackPositions.contains(new Position(1, 4)));
        assertTrue(attackPositions.contains(new Position(2, 1)));
        assertTrue(attackPositions.contains(new Position(2, 5)));
        assertTrue(attackPositions.contains(new Position(4, 1)));
        assertTrue(attackPositions.contains(new Position(4, 5)));
        assertTrue(attackPositions.contains(new Position(5, 2)));
        assertTrue(attackPositions.contains(new Position(5, 4)));
    }

    @Test
    public void testKnightDuplicate() {
        // Create a knight and mark it as moved
        Knight original = new Knight(PieceColor.BLACK, new Position(1, 0));
        original.setHasMoved(true);

        // Duplicate the knight
        Piece duplicate = original.duplicate();

        // Verify duplicate properties
        assertTrue(duplicate instanceof Knight);
        assertEquals(PieceColor.BLACK, duplicate.getColor());
        assertEquals(new Position(1, 0), duplicate.getPosition());
        assertTrue(duplicate.hasMoved());
        assertEquals("Knight", duplicate.getType());
    }

    @Test
    public void testKnightUniqueSituations() {
        // Place a knight on the board
        Knight knight = new Knight(PieceColor.WHITE, new Position(4, 4));
        board.placePieceForTesting(knight);

        // Place a king on the board
        King whiteKing = new King(PieceColor.WHITE, new Position(0, 0));
        board.placePieceForTesting(whiteKing);

        // Place an enemy queen that would put the king in check if the knight moves
        Queen blackQueen = new Queen(PieceColor.BLACK, new Position(0, 7));
        board.placePieceForTesting(blackQueen);

        // Place the knight so it's pinned against the king by the queen
        knight.setPosition(new Position(0, 3));

        // Get legal moves
        List<Move> legalMoves = knight.getLegalMoves(board);

        // Knight should be able to capture the queen to remove the pin
        assertTrue(containsMove(legalMoves, new Position(0, 7)));

        // Knight should not be able to move elsewhere as it would expose king to check
        assertEquals(1, legalMoves.size());
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