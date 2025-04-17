package Testers;
import Model.pieces.*;
import Model.*;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.List;

public class PawnTest {
    private Board board;

    @Before
    public void setUp() {
        board = new Board();
        board.clearBoard(); // Clear the board for custom test setups
    }

    @Test
    public void testInitialTwoSquareMove() {
        // Place white pawn on starting rank
        Pawn whitePawn = new Pawn(PieceColor.WHITE, new Position(3, 6));
        board.placePieceForTesting(whitePawn);

        List<Move> legalMoves = whitePawn.getLegalMoves(board);

        // Should have 2 legal moves: one and two squares forward
        assertEquals(2, legalMoves.size());

        // Verify one square move
        boolean canMoveOneSquare = legalMoves.stream()
                .anyMatch(move -> move.getDestination().equals(new Position(3, 5)));

        // Verify two square move
        boolean canMoveTwoSquares = legalMoves.stream()
                .anyMatch(move -> move.getDestination().equals(new Position(3, 4)));

        assertTrue("Pawn should be able to move one square forward", canMoveOneSquare);
        assertTrue("Pawn should be able to move two squares forward from starting position", canMoveTwoSquares);

        // Test black pawn
        board.clearBoard();
        Pawn blackPawn = new Pawn(PieceColor.BLACK, new Position(3, 1));
        board.placePieceForTesting(blackPawn);

        legalMoves = blackPawn.getLegalMoves(board);

        // Should have 2 legal moves: one and two squares forward
        assertEquals(2, legalMoves.size());

        // Verify one square move for black
        canMoveOneSquare = legalMoves.stream()
                .anyMatch(move -> move.getDestination().equals(new Position(3, 2)));

        // Verify two square move for black
        canMoveTwoSquares = legalMoves.stream()
                .anyMatch(move -> move.getDestination().equals(new Position(3, 3)));

        assertTrue("Black pawn should be able to move one square forward", canMoveOneSquare);
        assertTrue("Black pawn should be able to move two squares forward from starting position", canMoveTwoSquares);
    }

    @Test
    public void testNoTwoSquareMoveAfterFirstMove() {
        // Place white pawn not on starting rank or mark it as having moved
        Pawn whitePawn = new Pawn(PieceColor.WHITE, new Position(3, 5));
        whitePawn.setHasMoved(true);
        board.placePieceForTesting(whitePawn);

        List<Move> legalMoves = whitePawn.getLegalMoves(board);

        // Should have only 1 legal move (one square forward)
        assertEquals(1, legalMoves.size());

        // Verify the move is one square forward
        boolean canMoveOneSquare = legalMoves.stream()
                .anyMatch(move -> move.getDestination().equals(new Position(3, 4)));

        assertTrue("Pawn should be able to move one square forward", canMoveOneSquare);

        // Verify no two square move is available
        boolean canMoveTwoSquares = legalMoves.stream()
                .anyMatch(move -> move.getDestination().equals(new Position(3, 3)));

        assertFalse("Pawn should not be able to move two squares after first move", canMoveTwoSquares);
    }

    @Test
    public void testNormalForwardMovement() {
        // Place white pawn in middle of board
        Pawn whitePawn = new Pawn(PieceColor.WHITE, new Position(3, 4));
        whitePawn.setHasMoved(true);
        board.placePieceForTesting(whitePawn);

        List<Move> legalMoves = whitePawn.getLegalMoves(board);

        // Should have 1 legal move
        assertEquals(1, legalMoves.size());

        // The move should be one square forward
        Position destination = legalMoves.get(0).getDestination();
        assertEquals(new Position(3, 3), destination);

        // Test black pawn
        board.clearBoard();
        Pawn blackPawn = new Pawn(PieceColor.BLACK, new Position(3, 4));
        blackPawn.setHasMoved(true);
        board.placePieceForTesting(blackPawn);

        legalMoves = blackPawn.getLegalMoves(board);

        // Should have 1 legal move
        assertEquals(1, legalMoves.size());

        // The move should be one square forward (down for black)
        destination = legalMoves.get(0).getDestination();
        assertEquals(new Position(3, 5), destination);
    }

    @Test
    public void testPawnCannotMoveForwardWhenBlocked() {
        // Place white pawn with another piece in front of it
        Pawn whitePawn = new Pawn(PieceColor.WHITE, new Position(3, 4));
        board.placePieceForTesting(whitePawn);

        // Place blocking piece
        Pawn blockingPawn = new Pawn(PieceColor.BLACK, new Position(3, 3));
        board.placePieceForTesting(blockingPawn);

        List<Move> legalMoves = whitePawn.getLegalMoves(board);

        // Pawn should have no legal moves
        assertEquals(0, legalMoves.size());

        // Test initial two square move is also blocked
        board.clearBoard();
        whitePawn = new Pawn(PieceColor.WHITE, new Position(3, 6));
        board.placePieceForTesting(whitePawn);

        // Place blocking piece two squares ahead
        blockingPawn = new Pawn(PieceColor.BLACK, new Position(3, 4));
        board.placePieceForTesting(blockingPawn);

        legalMoves = whitePawn.getLegalMoves(board);

        // Pawn should only have one square move
        assertEquals(1, legalMoves.size());
        assertEquals(new Position(3, 5), legalMoves.get(0).getDestination());
    }

    @Test
    public void testNormalDiagonalCapture() {
        // Place white pawn in middle of board
        Pawn whitePawn = new Pawn(PieceColor.WHITE, new Position(3, 4));
        board.placePieceForTesting(whitePawn);

        // Place enemy pieces diagonally
        Pawn blackPawn1 = new Pawn(PieceColor.BLACK, new Position(2, 3));
        Pawn blackPawn2 = new Pawn(PieceColor.BLACK, new Position(4, 3));
        board.placePieceForTesting(blackPawn1);
        board.placePieceForTesting(blackPawn2);

        List<Move> legalMoves = whitePawn.getLegalMoves(board);

        // Should have 3 legal moves: forward, and two captures
        assertEquals(3, legalMoves.size());

        // Verify the captures
        boolean canCaptureLeft = legalMoves.stream()
                .anyMatch(move -> move.getDestination().equals(new Position(2, 3)));
        boolean canCaptureRight = legalMoves.stream()
                .anyMatch(move -> move.getDestination().equals(new Position(4, 3)));

        assertTrue("Pawn should be able to capture diagonally to the left", canCaptureLeft);
        assertTrue("Pawn should be able to capture diagonally to the right", canCaptureRight);

        // Test black pawn captures
        board.clearBoard();
        Pawn blackPawn = new Pawn(PieceColor.BLACK, new Position(3, 3));
        board.placePieceForTesting(blackPawn);

        // Place white pieces diagonally
        Pawn whitePawn1 = new Pawn(PieceColor.WHITE, new Position(2, 4));
        Pawn whitePawn2 = new Pawn(PieceColor.WHITE, new Position(4, 4));
        board.placePieceForTesting(whitePawn1);
        board.placePieceForTesting(whitePawn2);

        legalMoves = blackPawn.getLegalMoves(board);

        // Verify the captures
        canCaptureLeft = legalMoves.stream()
                .anyMatch(move -> move.getDestination().equals(new Position(2, 4)));
        canCaptureRight = legalMoves.stream()
                .anyMatch(move -> move.getDestination().equals(new Position(4, 4)));

        assertTrue("Black pawn should be able to capture diagonally to the left", canCaptureLeft);
        assertTrue("Black pawn should be able to capture diagonally to the right", canCaptureRight);
    }

    @Test
    public void testPawnCannotCaptureFriendlyPieces() {
        // Place white pawn in middle of board
        Pawn whitePawn = new Pawn(PieceColor.WHITE, new Position(3, 4));
        board.placePieceForTesting(whitePawn);

        // Place friendly pieces diagonally
        Pawn whitePawn1 = new Pawn(PieceColor.WHITE, new Position(2, 3));
        Pawn whitePawn2 = new Pawn(PieceColor.WHITE, new Position(4, 3));
        board.placePieceForTesting(whitePawn1);
        board.placePieceForTesting(whitePawn2);

        List<Move> legalMoves = whitePawn.getLegalMoves(board);

        // Should have only 1 legal move: forward
        assertEquals(1, legalMoves.size());

        // Verify no captures are available
        boolean canCaptureLeft = legalMoves.stream()
                .anyMatch(move -> move.getDestination().equals(new Position(2, 3)));
        boolean canCaptureRight = legalMoves.stream()
                .anyMatch(move -> move.getDestination().equals(new Position(4, 3)));

        assertFalse("Pawn should not be able to capture friendly pieces", canCaptureLeft);
        assertFalse("Pawn should not be able to capture friendly pieces", canCaptureRight);
    }

    @Test
    public void testEnPassantCapture() {
        // Set up the board for an en passant scenario
        board.clearBoard();

        // Place white pawn on the 5th rank
        Pawn whitePawn = new Pawn(PieceColor.WHITE, new Position(4, 3));
        whitePawn.setHasMoved(true);
        board.placePieceForTesting(whitePawn);

        // Place black pawn on starting rank
        Pawn blackPawn = new Pawn(PieceColor.BLACK, new Position(5, 1));
        board.placePieceForTesting(blackPawn);

        // Place kings to avoid errors with check detection
        King whiteKing = new King(PieceColor.WHITE, new Position(0, 7));
        King blackKing = new King(PieceColor.BLACK, new Position(0, 0));
        board.placePieceForTesting(whiteKing);
        board.placePieceForTesting(blackKing);

        // Simulate the black pawn's two-square move
        Move twoSquareMove = Move.createMove(
                new Position(5, 1),
                new Position(5, 3),
                blackPawn,
                null
        );
        board.executeMove(twoSquareMove);

        // Now check the white pawn's legal moves
        List<Move> legalMoves = whitePawn.getLegalMoves(board);

        // Should include an en passant capture
        boolean hasEnPassantMove = legalMoves.stream()
                .anyMatch(move -> move.isEnPassantCapture() &&
                        move.getDestination().equals(new Position(5, 2)));

        assertTrue("White pawn should be able to capture en passant", hasEnPassantMove);
    }

    @Test
    public void testEnPassantOnlyAvailableImmediately() {
        // Set up the board for an en passant scenario
        board.clearBoard();

        // Place white pawn on the 5th rank
        Pawn whitePawn = new Pawn(PieceColor.WHITE, new Position(4, 3));
        whitePawn.setHasMoved(true);
        board.placePieceForTesting(whitePawn);

        // Place black pawn on starting rank
        Pawn blackPawn = new Pawn(PieceColor.BLACK, new Position(5, 1));
        board.placePieceForTesting(blackPawn);

        // Place kings to avoid errors with check detection
        King whiteKing = new King(PieceColor.WHITE, new Position(0, 7));
        King blackKing = new King(PieceColor.BLACK, new Position(0, 0));
        board.placePieceForTesting(whiteKing);
        board.placePieceForTesting(blackKing);

        // Simulate the black pawn's two-square move
        Move twoSquareMove = Move.createMove(
                new Position(5, 1),
                new Position(5, 3),
                blackPawn,
                null
        );
        board.executeMove(twoSquareMove);

        // Simulate another move to make en passant no longer available
        Pawn dummyPawn = new Pawn(PieceColor.WHITE, new Position(0, 6));
        board.placePieceForTesting(dummyPawn);
        Move dummyMove = Move.createMove(
                new Position(0, 6),
                new Position(0, 5),
                dummyPawn,
                null
        );
        board.executeMove(dummyMove);

        // Now check the white pawn's legal moves
        List<Move> legalMoves = whitePawn.getLegalMoves(board);

        // Should not include an en passant capture
        boolean hasEnPassantMove = legalMoves.stream()
                .anyMatch(Move::isEnPassantCapture);

        assertFalse("En passant should not be available after another move", hasEnPassantMove);
    }

    @Test
    public void testPawnPromotion() {
        // Place white pawn about to promote
        Pawn whitePawn = new Pawn(PieceColor.WHITE, new Position(3, 1));
        whitePawn.setHasMoved(true);
        board.placePieceForTesting(whitePawn);

        List<Move> legalMoves = whitePawn.getLegalMoves(board);

        // Should have 1 legal move
        assertEquals(1, legalMoves.size());

        // Verify it's a promotion move
        assertTrue("Move should be a promotion", legalMoves.get(0).isPromotion());
        assertEquals(new Position(3, 0), legalMoves.get(0).getDestination());

        // Test black pawn promotion
        board.clearBoard();
        Pawn blackPawn = new Pawn(PieceColor.BLACK, new Position(3, 6));
        blackPawn.setHasMoved(true);
        board.placePieceForTesting(blackPawn);

        legalMoves = blackPawn.getLegalMoves(board);

        // Should have 1 legal move
        assertEquals(1, legalMoves.size());

        // Verify it's a promotion move
        assertTrue("Move should be a promotion", legalMoves.get(0).isPromotion());
        assertEquals(new Position(3, 7), legalMoves.get(0).getDestination());
    }

    @Test
    public void testPawnPromotionWithCapture() {
        // Place white pawn about to promote
        Pawn whitePawn = new Pawn(PieceColor.WHITE, new Position(3, 1));
        whitePawn.setHasMoved(true);
        board.placePieceForTesting(whitePawn);

        // Place enemy piece diagonally
        Rook blackRook = new Rook(PieceColor.BLACK, new Position(4, 0));
        board.placePieceForTesting(blackRook);

        List<Move> legalMoves = whitePawn.getLegalMoves(board);

        // Should have 2 legal moves: forward promotion and diagonal capture promotion
        assertEquals(2, legalMoves.size());

        // Verify both are promotion moves
        boolean hasForwardPromotion = legalMoves.stream()
                .anyMatch(move -> move.isPromotion() &&
                        move.getDestination().equals(new Position(3, 0)));

        boolean hasCapturePromotion = legalMoves.stream()
                .anyMatch(move -> move.isPromotion() &&
                        move.getDestination().equals(new Position(4, 0)));

        assertTrue("Should have forward promotion", hasForwardPromotion);
        assertTrue("Should have capture promotion", hasCapturePromotion);
    }

    @Test
    public void testPawnCannotMoveOffBoard() {
        // Place white pawn at edge of board
        Pawn whitePawn = new Pawn(PieceColor.WHITE, new Position(0, 1));
        whitePawn.setHasMoved(true);
        board.placePieceForTesting(whitePawn);

        List<Move> legalMoves = whitePawn.getLegalMoves(board);

        // Should have 1 legal move (forward to promotion)
        assertEquals(1, legalMoves.size());

        // Verify no illegal off-board moves
        boolean hasIllegalMove = legalMoves.stream()
                .anyMatch(move -> move.getDestination().equals(new Position(-1, 0)));

        assertFalse("Pawn should not have illegal off-board moves", hasIllegalMove);
    }

    @Test
    public void testGetAttackPositions() {
        // Test white pawn attack positions
        Pawn whitePawn = new Pawn(PieceColor.WHITE, new Position(3, 3));
        board.placePieceForTesting(whitePawn);

        List<Position> attackPositions = whitePawn.getAttackPositions(board);

        // Should attack two diagonally forward squares
        assertEquals(2, attackPositions.size());

        boolean attacksLeftForward = attackPositions.contains(new Position(2, 2));
        boolean attacksRightForward = attackPositions.contains(new Position(4, 2));

        assertTrue("White pawn should attack diagonally left forward", attacksLeftForward);
        assertTrue("White pawn should attack diagonally right forward", attacksRightForward);

        // Test black pawn attack positions
        board.clearBoard();
        Pawn blackPawn = new Pawn(PieceColor.BLACK, new Position(3, 3));
        board.placePieceForTesting(blackPawn);

        attackPositions = blackPawn.getAttackPositions(board);

        // Should attack two diagonally forward (downward) squares
        assertEquals(2, attackPositions.size());

        boolean attacksLeftDown = attackPositions.contains(new Position(2, 4));
        boolean attacksRightDown = attackPositions.contains(new Position(4, 4));

        assertTrue("Black pawn should attack diagonally left downward", attacksLeftDown);
        assertTrue("Black pawn should attack diagonally right downward", attacksRightDown);
    }

    @Test
    public void testPawnAtBoardEdgeAttackPositions() {
        // Test pawn at left edge
        Pawn whitePawn = new Pawn(PieceColor.WHITE, new Position(0, 3));
        board.placePieceForTesting(whitePawn);

        List<Position> attackPositions = whitePawn.getAttackPositions(board);

        // Should attack only one square (diagonally right forward)
        assertEquals(1, attackPositions.size());
        assertTrue(attackPositions.contains(new Position(1, 2)));

        // Test pawn at right edge
        board.clearBoard();
        whitePawn = new Pawn(PieceColor.WHITE, new Position(7, 3));
        board.placePieceForTesting(whitePawn);

        attackPositions = whitePawn.getAttackPositions(board);

        // Should attack only one square (diagonally left forward)
        assertEquals(1, attackPositions.size());
        assertTrue(attackPositions.contains(new Position(6, 2)));
    }

    @Test
    public void testGetType() {
        Pawn pawn = new Pawn(PieceColor.WHITE, new Position(0, 0));
        assertEquals("Pawn", pawn.getType());
    }

    @Test
    public void testDuplicate() {
        Pawn original = new Pawn(PieceColor.WHITE, new Position(3, 3));
        original.setHasMoved(true);

        Piece copy = original.duplicate();

        assertTrue(copy instanceof Pawn);
        assertEquals(original.getColor(), copy.getColor());
        assertEquals(original.getPosition().getColumn(), copy.getPosition().getColumn());
        assertEquals(original.getPosition().getRow(), copy.getPosition().getRow());
        assertEquals(original.hasMoved(), copy.hasMoved());
    }

    @Test
    public void testToString() {
        Pawn whitePawn = new Pawn(PieceColor.WHITE, new Position(0, 0));
        assertEquals("♙", whitePawn.toString());

        Pawn blackPawn = new Pawn(PieceColor.BLACK, new Position(0, 0));
        assertEquals("♟", blackPawn.toString());
    }

    @Test
    public void testCannotMoveWhenKingWouldBeInCheck() {
        // Setup a scenario where moving the pawn would put king in check
        board.clearBoard();

        // Place white king and pawn
        King whiteKing = new King(PieceColor.WHITE, new Position(4, 7));
        board.placePieceForTesting(whiteKing);

        Pawn whitePawn = new Pawn(PieceColor.WHITE, new Position(3, 6));
        board.placePieceForTesting(whitePawn);

        // Place black rook that would check king if pawn moves
        Rook blackRook = new Rook(PieceColor.BLACK, new Position(3, 0));
        board.placePieceForTesting(blackRook);

        List<Move> legalMoves = whitePawn.getLegalMoves(board);

        // Pawn should not be able to move and expose the king to check
        assertEquals(0, legalMoves.size());
    }
}