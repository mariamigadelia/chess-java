package Model;

public class Square {
    private final int xNum;
    private final int yNum;
    private final int color;
    private Piece occupyingPiece;
    private final Board board;

    public Square(Board board, int color, int xNum, int yNum) {
        this.board = board;
        this.color = color;
        this.xNum = xNum;
        this.yNum = yNum;
    }

    public int getColor() { return color; }
    public int getXNum() { return xNum; }
    public int getYNum() { return yNum; }
    public boolean isOccupied() { return occupyingPiece != null; }
    public Piece getOccupyingPiece() { return occupyingPiece; }

    public void put(Piece p) {
        this.occupyingPiece = p;
        p.setPosition(this);
    }

    public Piece removePiece() {
        Piece p = this.occupyingPiece;
        this.occupyingPiece = null;
        return p;
    }

    public void capture(Piece p) {
        Piece k = getOccupyingPiece();
        if (k.getColor() == 0) board.Bpieces.remove(k);
        if (k.getColor() == 1) board.Wpieces.remove(k);
        this.occupyingPiece = p;
    }

    @Override
    public int hashCode() {
        return 31 * xNum + yNum;
    }
}
