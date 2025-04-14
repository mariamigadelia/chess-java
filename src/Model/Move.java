package Model;
import Model.Piece;
/**
 * Represents a chess move between two squares, tracking the piece
 * and any captured pieces.
 */
public class Move {
    private Piece movingPiece;
    private Square fromSquare;
    private Square toSquare;
    private Piece capturedPiece;
    private boolean isSpecialMove; // Castle, en passant, promotion
    private String specialMoveType; // "castle", "en-passant", "promotion"
    private long timestamp;

    public Move(Piece movingPiece, Square fromSquare, Square toSquare) {
        this.movingPiece = movingPiece;
        this.fromSquare = fromSquare;
        this.toSquare = toSquare;
        this.capturedPiece = toSquare.getOccupyingPiece();
        this.timestamp = System.currentTimeMillis();
        this.isSpecialMove = false;
    }

    public Move(Piece movingPiece, Square fromSquare, Square toSquare,
                String specialMoveType) {
        this(movingPiece, fromSquare, toSquare);
        this.isSpecialMove = true;
        this.specialMoveType = specialMoveType;
    }

    public Piece getMovingPiece() {
        return movingPiece;
    }

    public Square getFromSquare() {
        return fromSquare;
    }

    public Square getToSquare() {
        return toSquare;
    }

    public Piece getCapturedPiece() {
        return capturedPiece;
    }

    public boolean isCapture() {
        return capturedPiece != null;
    }

    public boolean isSpecialMove() {
        return isSpecialMove;
    }

    public String getSpecialMoveType() {
        return specialMoveType;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String toChessNotation() {
        // Basic algebraic notation implementation
        String pieceLetter = getPieceLetter(movingPiece);
        String captureNotation = isCapture() ? "x" : "";
        String destination = toSquare.getPositionName();

        return pieceLetter + captureNotation + destination;
    }

    private String getPieceLetter(Piece piece) {
        // Return chess notation letter based on piece type
        String className = piece.getClass().getSimpleName();
        switch (className) {
            case "Pawn":
                return "";
            case "Knight":
                return "N";
            case "Bishop":
                return "B";
            case "Rook":
                return "R";
            case "Queen":
                return "K";
            default:
                return "";

        }
    }
}