package Model.board;

import Model.Piece;

import java.util.LinkedList;
import java.util.List;

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

    public static class Pawn extends Piece {
        private boolean wasMoved;

        public Pawn(int color, Square initSq, String img_file) {
            super(color, initSq, img_file);
        }

        @Override
        public boolean move(Square fin) {
            boolean b = super.move(fin);
            wasMoved = true;
            return b;
        }

        @Override
        public List<Square> getLegalMoves(Board b) {
            LinkedList<Square> legalMoves = new LinkedList<Square>();

            Square[][] board = b.getSquareArray();

            int x = this.getPosition().getXNum();
            int y = this.getPosition().getYNum();
            int c = this.getColor();

            if (c == 0) {
                if (!wasMoved) {
                    if (!board[y+2][x].isOccupied()) {
                        legalMoves.add(board[y+2][x]);
                    }
                }

                if (y+1 < 8) {
                    if (!board[y+1][x].isOccupied()) {
                        legalMoves.add(board[y+1][x]);
                    }
                }

                if (x+1 < 8 && y+1 < 8) {
                    if (board[y+1][x+1].isOccupied()) {
                        legalMoves.add(board[y+1][x+1]);
                    }
                }

                if (x-1 >= 0 && y+1 < 8) {
                    if (board[y+1][x-1].isOccupied()) {
                        legalMoves.add(board[y+1][x-1]);
                    }
                }
            }

            if (c == 1) {
                if (!wasMoved) {
                    if (!board[y-2][x].isOccupied()) {
                        legalMoves.add(board[y-2][x]);
                    }
                }

                if (y-1 >= 0) {
                    if (!board[y-1][x].isOccupied()) {
                        legalMoves.add(board[y-1][x]);
                    }
                }

                if (x+1 < 8 && y-1 >= 0) {
                    if (board[y-1][x+1].isOccupied()) {
                        legalMoves.add(board[y-1][x+1]);
                    }
                }

                if (x-1 >= 0 && y-1 >= 0) {
                    if (board[y-1][x-1].isOccupied()) {
                        legalMoves.add(board[y-1][x-1]);
                    }
                }
            }

            return legalMoves;
        }
    }
}
