package View;

import Model.Piece;
import Model.Square;

import javax.swing.*;
import java.awt.*;

public class SquareView extends JComponent {
    private final Square square;

    public SquareView(Square square) {
        this.square = square;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int color = square.getColor();
        if (color == 1) g.setColor(new Color(221, 192, 127));
        else g.setColor(new Color(101, 67, 33));

        g.fillRect(0, 0, getWidth(), getHeight());

        Piece piece = square.getOccupyingPiece();
        if (piece != null && piece.getView() != null) {
            piece.getView().draw(g, 0, 0, getWidth(), getHeight());
        }
    }
}
