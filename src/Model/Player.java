package Model;

import Model.pieces.*;
import java.util.LinkedList;
import java.util.List;

/**
 * Represents a chess player, tracking their pieces, time, and game state.
 */
public class Player {
    private final String name;
    private final int color; // 0 for black, 1 for white (matching your existing convention)
    private final LinkedList<Piece> pieces;
    private final LinkedList<Piece> capturedPieces;
    private long timeRemaining; // In milliseconds
    private boolean isInCheck;
    private boolean isInCheckmate;

    public Player(String name, int color, LinkedList<Piece> pieces, long initialTime) {
        this.name = name;
        this.color = color;
        this.pieces = pieces;
        this.capturedPieces = new LinkedList<>();
        this.timeRemaining = initialTime;
        this.isInCheck = false;
        this.isInCheckmate = false;
    }

    public String getName() {
        return name;
    }

    public int getColor() {
        return color;
    }

    public LinkedList<Piece> getPieces() {
        return pieces;
    }

    public void addCapturedPiece(Piece piece) {
        capturedPieces.add(piece);
    }

    public LinkedList<Piece> getCapturedPieces() {
        return capturedPieces;
    }

    public long getTimeRemaining() {
        return timeRemaining;
    }

    public void updateTime(long timeUsed) {
        timeRemaining -= timeUsed;
    }

    public void setCheck(boolean inCheck) {
        this.isInCheck = inCheck;
    }

    public boolean isInCheck() {
        return isInCheck;
    }

    public void setCheckmate(boolean inCheckmate) {
        this.isInCheckmate = inCheckmate;
    }

    public boolean isInCheckmate() {
        return isInCheckmate;
    }

    public int getMaterialAdvantage() {
        // Calculate material advantage based on captured pieces
        int value = 0;
        for (Piece piece : capturedPieces) {
            value += getPieceValue(piece);
        }
        return value;
    }

    private int getPieceValue(Piece piece) {
        // Get the class name instead of using instanceof
        String className = piece.getClass().getSimpleName();
        switch (className) {
            case "Pawn":
                return 1;
            case "Knight":
                return 3;
            case "Bishop":
                return 3;
            case "Rook":
                return 5;
            case "Queen":
                return 9;
            default:
                return 0;
        }
    }
}