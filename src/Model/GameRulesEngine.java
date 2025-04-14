package Model.rules;

import Model.pieces.Piece;
import Model.Board;
import Model.Square;
import Model.pieces.Bishop;
import Model.pieces.King;
import Model.pieces.Queen;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * Component of the Chess game that handles game rules, particularly check and checkmate detection.
 *
 * @author Jussi Lundstedt
 */
public class GameRulesEngine {
    private Board board;
    private LinkedList<Piece> whitePieces;
    private LinkedList<Piece> blackPieces;
    private final LinkedList<Square> allSquares;
    private King blackKing;
    private King whiteKing;
    private HashMap<Square, List<Piece>> whiteMoves;
    private HashMap<Square, List<Piece>> blackMoves;

    /**
     * Constructs a new instance of GameRulesEngine on a given board.
     * By convention should be called when the board is in its initial state.
     *
     * @param board The board which the engine monitors
     * @param whitePieces White pieces on the board
     * @param blackPieces Black pieces on the board
     * @param whiteKing Model.Piece object representing the white king
     * @param blackKing Model.Piece object representing the black king
     */
    public GameRulesEngine(Board board, LinkedList<Piece> whitePieces,
                           LinkedList<Piece> blackPieces, King whiteKing, King blackKing) {
        this.board = board;
        this.whitePieces = whitePieces;
        this.blackPieces = blackPieces;
        this.blackKing = blackKing;
        this.whiteKing = whiteKing;

        // Initialize other fields
        allSquares = new LinkedList<Square>();
        whiteMoves = new HashMap<Square, List<Piece>>();
        blackMoves = new HashMap<Square, List<Piece>>();

        Square[][] boardArray = board.getSquareArray();

        // add all squares to squares list and as hashmap keys
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                allSquares.add(boardArray[y][x]);
                whiteMoves.put(boardArray[y][x], new LinkedList<Piece>());
                blackMoves.put(boardArray[y][x], new LinkedList<Piece>());
            }
        }

        // update situation
        update();
    }

    /**
     * Updates the object with the current situation of the game.
     */
    public void update() {
        // Iterators through pieces
        Iterator<Piece> whiteIterator = whitePieces.iterator();
        Iterator<Piece> blackIterator = blackPieces.iterator();

        // empty moves at each update
        for (List<Piece> pieces : whiteMoves.values()) {
            pieces.removeAll(pieces);
        }

        for (List<Piece> pieces : blackMoves.values()) {
            pieces.removeAll(pieces);
        }

        // Add each move white and black can make to map
        while (whiteIterator.hasNext()) {
            Piece p = whiteIterator.next();

            if (!p.getClass().equals(King.class)) {
                if (p.getPosition() == null) {
                    whiteIterator.remove();
                    continue;
                }

                List<Square> moves = p.getLegalMoves(board);
                Iterator<Square> iter = moves.iterator();
                while (iter.hasNext()) {
                    List<Piece> pieces = whiteMoves.get(iter.next());
                    pieces.add(p);
                }
            }
        }

        while (blackIterator.hasNext()) {
            Piece p = blackIterator.next();

            if (!p.getClass().equals(King.class)) {
                if (p.getPosition() == null) {
                    blackIterator.remove();
                    continue;
                }

                List<Square> moves = p.getLegalMoves(board);
                Iterator<Square> iter = moves.iterator();
                while (iter.hasNext()) {
                    List<Piece> pieces = blackMoves.get(iter.next());
                    pieces.add(p);
                }
            }
        }
    }

    /**
     * Checks if the black king is threatened
     * @return boolean representing whether the black king is in check.
     */
    public boolean isBlackInCheck() {
        update();
        Square sq = blackKing.getPosition();
        return !whiteMoves.get(sq).isEmpty();
    }

    /**
     * Checks if the white king is threatened
     * @return boolean representing whether the white king is in check.
     */
    public boolean isWhiteInCheck() {
        update();
        Square sq = whiteKing.getPosition();
        return !blackMoves.get(sq).isEmpty();
    }

    /**
     * Checks whether black is in checkmate.
     * @return boolean representing if black player is checkmated.
     */
    public boolean isBlackCheckmated() {
        // Check if black is in check
        if (!this.isBlackInCheck()) return false;

        // Get squares that can help escape checkmate
        List<Square> escapeMoves = getCheckEscapeMoves(false);

        // If there are escape moves, not checkmate
        return escapeMoves.isEmpty();
    }

    /**
     * Checks whether white is in checkmate.
     * @return boolean representing if white player is checkmated.
     */
    public boolean isWhiteCheckmated() {
        // Check if white is in check
        if (!this.isWhiteInCheck()) return false;

        // Get squares that can help escape checkmate
        List<Square> escapeMoves = getCheckEscapeMoves(true);

        // If there are escape moves, not checkmate
        return escapeMoves.isEmpty();
    }

    /**
     * Gets all squares that can be used to escape a check situation
     *
     * @param isWhite true for white player, false for black player
     * @return List of squares that can be used to escape check
     */
    public List<Square> getCheckEscapeMoves(boolean isWhite) {
        LinkedList<Square> movableSquares = new LinkedList<Square>();

        if (isWhite) {
            // If not in check, all squares are valid
            if (!isWhiteInCheck()) {
                movableSquares.addAll(allSquares);
                return movableSquares;
            }

            // Check if king can evade
            if (canEvade(blackMoves, whiteKing, movableSquares)) {
                // Evade moves already added to movableSquares
            }

            // Check if threat can be captured
            List<Piece> threats = blackMoves.get(whiteKing.getPosition());
            if (canCapture(whiteMoves, threats, whiteKing, movableSquares)) {
                // Capture moves already added to movableSquares
            }

            // Check if threat can be blocked
            if (canBlock(threats, whiteMoves, whiteKing, movableSquares)) {
                // Block moves already added to movableSquares
            }
        } else {
            // If not in check, all squares are valid
            if (!isBlackInCheck()) {
                movableSquares.addAll(allSquares);
                return movableSquares;
            }

            // Check if king can evade
            if (canEvade(whiteMoves, blackKing, movableSquares)) {
                // Evade moves already added to movableSquares
            }

            // Check if threat can be captured
            List<Piece> threats = whiteMoves.get(blackKing.getPosition());
            if (canCapture(blackMoves, threats, blackKing, movableSquares)) {
                // Capture moves already added to movableSquares
            }

            // Check if threat can be blocked
            if (canBlock(threats, blackMoves, blackKing, movableSquares)) {
                // Block moves already added to movableSquares
            }
        }

        return movableSquares;
    }

    /**
     * Tests a move to prevent making an illegal move that puts the player in check.
     * @param p Model.Piece to be moved
     * @param sq Square to which p is about to move
     * @return false if move would cause a check
     */
    public boolean testMove(Piece p, Square sq) {
        Piece captured = sq.getOccupyingPiece();
        boolean moveValid = true;
        Square initial = p.getPosition();

        p.move(sq);
        update();

        if (p.getColor() == 0 && isBlackInCheck()) moveValid = false;
        else if (p.getColor() == 1 && isWhiteInCheck()) moveValid = false;

        p.move(initial);
        if (captured != null) sq.put(captured);

        update();

        return moveValid;
    }

    /*
     * Helper method to determine if the king can evade the check.
     * Returns true if king can move to a safe square.
     */
    private boolean canEvade(Map<Square, List<Piece>> threats, King king,
                             List<Square> movableSquares) {
        boolean canEvade = false;
        List<Square> kingsMoves = king.getLegalMoves(board);
        Iterator<Square> iterator = kingsMoves.iterator();

        // If king is not threatened at some square, it can evade
        while (iterator.hasNext()) {
            Square sq = iterator.next();
            if (!testMove(king, sq)) continue;
            if (threats.get(sq).isEmpty()) {
                movableSquares.add(sq);
                canEvade = true;
            }
        }

        return canEvade;
    }

    /*
     * Helper method to determine if the threatening piece can be captured.
     */
    private boolean canCapture(Map<Square, List<Piece>> defenders,
                               List<Piece> threats, King king,
                               List<Square> movableSquares) {

        boolean canCapture = false;
        if (threats.size() == 1) {
            Square threatSquare = threats.get(0).getPosition();

            // Check if king can capture the threat
            if (king.getLegalMoves(board).contains(threatSquare)) {
                if (testMove(king, threatSquare)) {
                    movableSquares.add(threatSquare);
                    canCapture = true;
                }
            }

            // Check if other pieces can capture the threat
            List<Piece> capturers = defenders.get(threatSquare);
            ConcurrentLinkedDeque<Piece> capturerQueue = new ConcurrentLinkedDeque<Piece>();
            capturerQueue.addAll(capturers);

            if (!capturerQueue.isEmpty()) {
                for (Piece p : capturerQueue) {
                    if (testMove(p, threatSquare)) {
                        movableSquares.add(threatSquare);
                        canCapture = true;
                    }
                }
            }
        }

        return canCapture;
    }

    /*
     * Helper method to determine if check can be blocked by a piece.
     */
    private boolean canBlock(List<Piece> threats,
                             Map<Square, List<Piece>> blockMoves, King king,
                             List<Square> movableSquares) {
        boolean blockable = false;

        if (threats.size() == 1) {
            Square threatSquare = threats.get(0).getPosition();
            Square kingSquare = king.getPosition();
            Square[][] boardArray = board.getSquareArray();

            // Check vertical line between king and threat
            if (kingSquare.getXNum() == threatSquare.getXNum()) {
                int max = Math.max(kingSquare.getYNum(), threatSquare.getYNum());
                int min = Math.min(kingSquare.getYNum(), threatSquare.getYNum());

                for (int i = min + 1; i < max; i++) {
                    List<Piece> blockers =
                            blockMoves.get(boardArray[i][kingSquare.getXNum()]);
                    ConcurrentLinkedDeque<Piece> blockerQueue =
                            new ConcurrentLinkedDeque<Piece>();
                    blockerQueue.addAll(blockers);

                    if (!blockerQueue.isEmpty()) {
                        Square blockSquare = boardArray[i][kingSquare.getXNum()];

                        for (Piece p : blockerQueue) {
                            if (testMove(p, blockSquare)) {
                                movableSquares.add(blockSquare);
                                blockable = true;
                            }
                        }
                    }
                }
            }

            // Check horizontal line between king and threat
            if (kingSquare.getYNum() == threatSquare.getYNum()) {
                int max = Math.max(kingSquare.getXNum(), threatSquare.getXNum());
                int min = Math.min(kingSquare.getXNum(), threatSquare.getXNum());

                for (int i = min + 1; i < max; i++) {
                    List<Piece> blockers =
                            blockMoves.get(boardArray[kingSquare.getYNum()][i]);
                    ConcurrentLinkedDeque<Piece> blockerQueue =
                            new ConcurrentLinkedDeque<Piece>();
                    blockerQueue.addAll(blockers);

                    if (!blockerQueue.isEmpty()) {
                        Square blockSquare = boardArray[kingSquare.getYNum()][i];

                        for (Piece p : blockerQueue) {
                            if (testMove(p, blockSquare)) {
                                movableSquares.add(blockSquare);
                                blockable = true;
                            }
                        }
                    }
                }
            }

            // Check diagonal lines (Bishop and Queen attacks)
            Class<? extends Piece> threatClass = threats.get(0).getClass();

            if (threatClass.equals(Queen.class) || threatClass.equals(Bishop.class)) {
                int kX = kingSquare.getXNum();
                int kY = kingSquare.getYNum();
                int tX = threatSquare.getXNum();
                int tY = threatSquare.getYNum();

                // Diagonal: bottom-left to top-right
                if (kX > tX && kY > tY) {
                    for (int i = tX + 1, j = tY + 1; i < kX && j < kY; i++, j++) {
                        List<Piece> blockers = blockMoves.get(boardArray[j][i]);
                        ConcurrentLinkedDeque<Piece> blockerQueue = new ConcurrentLinkedDeque<Piece>();
                        blockerQueue.addAll(blockers);

                        if (!blockerQueue.isEmpty()) {
                            Square blockSquare = boardArray[j][i];

                            for (Piece p : blockerQueue) {
                                if (testMove(p, blockSquare)) {
                                    movableSquares.add(blockSquare);
                                    blockable = true;
                                }
                            }
                        }
                    }
                }

                // Diagonal: top-left to bottom-right
                if (kX > tX && tY > kY) {
                    for (int i = tX + 1, j = tY - 1; i < kX && j > kY; i++, j--) {
                        List<Piece> blockers = blockMoves.get(boardArray[j][i]);
                        ConcurrentLinkedDeque<Piece> blockerQueue = new ConcurrentLinkedDeque<Piece>();
                        blockerQueue.addAll(blockers);

                        if (!blockerQueue.isEmpty()) {
                            Square blockSquare = boardArray[j][i];

                            for (Piece p : blockerQueue) {
                                if (testMove(p, blockSquare)) {
                                    movableSquares.add(blockSquare);
                                    blockable = true;
                                }
                            }
                        }
                    }
                }

                // Diagonal: bottom-right to top-left
                if (tX > kX && kY > tY) {
                    for (int i = tX - 1, j = tY + 1; i > kX && j < kY; i--, j++) {
                        List<Piece> blockers = blockMoves.get(boardArray[j][i]);
                        ConcurrentLinkedDeque<Piece> blockerQueue = new ConcurrentLinkedDeque<Piece>();
                        blockerQueue.addAll(blockers);

                        if (!blockerQueue.isEmpty()) {
                            Square blockSquare = boardArray[j][i];

                            for (Piece p : blockerQueue) {
                                if (testMove(p, blockSquare)) {
                                    movableSquares.add(blockSquare);
                                    blockable = true;
                                }
                            }
                        }
                    }
                }

                // Diagonal: top-right to bottom-left
                if (tX > kX && tY > kY) {
                    for (int i = tX - 1, j = tY - 1; i > kX && j > kY; i--, j--) {
                        List<Piece> blockers = blockMoves.get(boardArray[j][i]);
                        ConcurrentLinkedDeque<Piece> blockerQueue = new ConcurrentLinkedDeque<Piece>();
                        blockerQueue.addAll(blockers);

                        if (!blockerQueue.isEmpty()) {
                            Square blockSquare = boardArray[j][i];

                            for (Piece p : blockerQueue) {
                                if (testMove(p, blockSquare)) {
                                    movableSquares.add(blockSquare);
                                    blockable = true;
                                }
                            }
                        }
                    }
                }
            }
        }

        return blockable;
    }
}