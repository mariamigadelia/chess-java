package View;

import Model.Piece;
import Model.Square;

import javax.swing.JComponent;
import java.awt.Color;
import java.awt.Graphics;

public class SquareView extends JComponent {
    private static final long serialVersionUID = 1L;

    private final Square square;

    public SquareView(Square square) {
        this.square = square;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw square background color
        int color = square.getColor();
        if (color == 1) {
            g.setColor(new Color(221, 192, 127)); // light square
        } else {
            g.setColor(new Color(101, 67, 33)); // dark square
        }
        g.fillRect(0, 0, getWidth(), getHeight());

        // Draw piece if present
        Piece piece = square.getOccupyingPiece();
        if (piece != null && piece.getView() != null) {
            piece.getView().draw(g, 0, 0, getWidth(), getHeight());
        }
    }
}
