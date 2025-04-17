package View;

import Model.Piece;
import Model.Position;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

/**
 * Represents a single square on the chess board UI.
 * This class handles the rendering of squares, pieces, and visual indicators.
 */
public class ChessSquareView extends JPanel {
    // Colors for the chess board
    private static final Color LIGHT_SQUARE = new Color(240, 217, 181);
    private static final Color DARK_SQUARE = new Color(181, 136, 99);
    private static final Color SELECTED_OVERLAY = new Color(100, 255, 100, 80);
    private static final Color LEGAL_MOVE_OVERLAY = new Color(0, 0, 255, 60);
    private static final Color LAST_MOVE_OVERLAY = new Color(255, 255, 0, 60);

    private Position position;
    private boolean isLight;
    private Piece occupyingPiece;
    private boolean isSelected;
    private boolean isLegalMove;
    private boolean isLastMove;

    /**
     * Constructs a chess square view.
     *
     * @param position The board position this square represents
     * @param isLight Whether this is a light-colored square
     */
    public ChessSquareView(Position position, boolean isLight) {
        this.position = position;
        this.isLight = isLight;
        this.occupyingPiece = null;
        this.isSelected = false;
        this.isLegalMove = false;
        this.isLastMove = false;

        setPreferredSize(new Dimension(75, 75));
        setBorder(BorderFactory.createEmptyBorder());
    }

    /**
     * Sets the piece occupying this square.
     *
     * @param piece The piece to place on this square, or null to clear
     */
    public void setOccupyingPiece(Piece piece) {
        this.occupyingPiece = piece;
        repaint();
    }

    /**
     * Sets whether this square is currently selected.
     *
     * @param selected Whether this square is selected
     */
    public void setSelected(boolean selected) {
        this.isSelected = selected;
        repaint();
    }

    /**
     * Sets whether this square represents a legal move for the selected piece.
     *
     * @param legalMove Whether this square is a legal move destination
     */
    public void setLegalMove(boolean legalMove) {
        this.isLegalMove = legalMove;
        repaint();
    }

    /**
     * Sets whether this square was involved in the last move.
     *
     * @param lastMove Whether this square was part of the last move
     */
    public void setLastMove(boolean lastMove) {
        this.isLastMove = lastMove;
        repaint();
    }

    /**
     * Resets the visual state of this square.
     */
    public void resetVisualState() {
        this.isSelected = false;
        this.isLegalMove = false;
        this.isLastMove = false;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // Draw the base square color
        g2d.setColor(isLight ? LIGHT_SQUARE : DARK_SQUARE);
        g2d.fillRect(0, 0, getWidth(), getHeight());

        // Draw overlays for special states
        if (isLastMove) {
            g2d.setColor(LAST_MOVE_OVERLAY);
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }

        if (isSelected) {
            g2d.setColor(SELECTED_OVERLAY);
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }

        if (isLegalMove) {
            g2d.setColor(LEGAL_MOVE_OVERLAY);
            g2d.fillRect(0, 0, getWidth(), getHeight());

            // If it's a legal move and empty, draw a circle
            if (occupyingPiece == null) {
                g2d.setColor(new Color(0, 0, 0, 120));
                int diameter = getWidth() / 4;
                int x = (getWidth() - diameter) / 2;
                int y = (getHeight() - diameter) / 2;
                g2d.fillOval(x, y, diameter, diameter);
            }
        }

        // Draw the piece if present
        if (occupyingPiece != null) {
            drawPiece(g2d);
        }

        // Draw coordinates for debugging (can be removed in final version)
        /*
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.PLAIN, 10));
        g2d.drawString(position.toString(), 5, 12);
        */
    }

    /**
     * Draws the chess piece on this square.
     */
    private void drawPiece(Graphics2D g2d) {
        try {
            // Load the piece image
            String imagePath = occupyingPiece.getPieceImage();
            File imageFile = new File(imagePath);

            if (imageFile.exists()) {
                BufferedImage pieceImage = ImageIO.read(imageFile);

                // Calculate position to center the piece
                int x = (getWidth() - pieceImage.getWidth()) / 2;
                int y = (getHeight() - pieceImage.getHeight()) / 2;

                // Draw the piece
                g2d.drawImage(pieceImage, x, y, this);
            } else {
                // Fallback if image not found
                String pieceType = occupyingPiece.getType().substring(0, 1);
                g2d.setColor(occupyingPiece.getColor() == Model.PieceColor.WHITE ? Color.WHITE : Color.BLACK);
                g2d.setFont(new Font("Serif", Font.BOLD, 32));
                g2d.drawString(pieceType, getWidth() / 3, getHeight() / 2 + 10);
            }
        } catch (IOException e) {
            // Fallback if image loading fails
            String pieceType = occupyingPiece.getType();
            g2d.setColor(occupyingPiece.getColor() == Model.PieceColor.WHITE ? Color.WHITE : Color.BLACK);
            g2d.setFont(new Font("Serif", Font.BOLD, 20));
            g2d.drawString(pieceType.substring(0, 1), getWidth() / 3, getHeight() / 2 + 5);
        }
    }
}