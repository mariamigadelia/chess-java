package Model;

import View.PieceView;

import java.util.List;

// Model/Piece.java
public abstract class Piece {
    private final int color;
    private Square currentSquare;
    private PieceView view;

    public Piece(int color, Square square) {
        this.color = color;
        this.currentSquare = square;
    }

    public int getColor() { return color; }
    public Square getPosition() { return currentSquare; }
    public void setPosition(Square square) { this.currentSquare = square; }

    public void setView(PieceView view) { this.view = view; }
    public PieceView getView() { return view; }

    public boolean move(Square destination) {
        Piece occup = destination.getOccupyingPiece();
        if (occup != null) {
            if (occup.getColor() == this.color) return false;
            else destination.capture(this);
        }
        currentSquare.removePiece();
        destination.put(this);
        return true;
    }

    public abstract List<Square> getLegalMoves(Board board);
}
