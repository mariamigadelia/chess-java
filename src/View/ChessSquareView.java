package View;

import Model.Piece;
import Model.Position;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ChessSquareView extends JPanel {
    private static final Color LIGHT_SQUARE_COLOR = new Color(240, 217, 181);
    private static final Color DARK_SQUARE_COLOR = new Color(181, 136, 99);
    private static final Color SELECTED_COLOR = new Color(106, 168, 79, 128);
    private static final Color LEGAL_MOVE_COLOR = new Color(106, 168, 79, 90);
    private static final Color HIGHLIGHT_COLOR = new Color(246, 246, 105, 128);
    private static final Color CHECK_COLOR = new Color(220, 56, 56, 128);

    private Position position;
    private boolean isLightSquare;
    private Piece piece;
    private boolean isSelected;
    private boolean isHighlighted;
    private boolean isLegalMove;
    private boolean isInCheck;
    private ChessBoardUI boardUI;

    public ChessSquareView(Position position, boolean isLightSquare, ChessBoardUI boardUI) {
        this.position = position;
        this.isLightSquare = isLightSquare;
        this.boardUI = boardUI;
        this.isSelected = false;
        this.isHighlighted = false;
        this.isLegalMove = false;
        this.isInCheck = false;

        setPreferredSize(new Dimension(60, 60));
        setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                boardUI.handleSquareClick(position);
            }
        });
    }

    public void setPiece(Piece piece) {
        this.piece = piece;
        repaint();
    }

    public void setSelected(boolean selected) {
        this.isSelected = selected;
        repaint();
    }

    public void setHighlighted(boolean highlighted) {
        this.isHighlighted = highlighted;
        repaint();
    }

    public void setLegalMove(boolean legalMove) {
        this.isLegalMove = legalMove;
        repaint();
    }

    public void setInCheck(boolean inCheck) {
        this.isInCheck = inCheck;
        repaint();
    }

    public void clearState() {
        isSelected = false;
        isHighlighted = false;
        isLegalMove = false;
        isInCheck = false;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // Draw base square color
        g2d.setColor(isLightSquare ? LIGHT_SQUARE_COLOR : DARK_SQUARE_COLOR);
        g2d.fillRect(0, 0, getWidth(), getHeight());

        // Draw state highlights
        if (isSelected) {
            g2d.setColor(SELECTED_COLOR);
            g2d.fillRect(0, 0, getWidth(), getHeight());
        } else if (isHighlighted) {
            g2d.setColor(HIGHLIGHT_COLOR);
            g2d.fillRect(0, 0, getWidth(), getHeight());
        } else if (isInCheck) {
            g2d.setColor(CHECK_COLOR);
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }

        // Draw legal move indicator
        if (isLegalMove) {
            if (piece == null) {
                // Draw circle for empty square legal move
                g2d.setColor(LEGAL_MOVE_COLOR);
                int diameter = getWidth() / 3;
                int x = (getWidth() - diameter) / 2;
                int y = (getHeight() - diameter) / 2;
                g2d.fillOval(x, y, diameter, diameter);
            } else {
                // Draw border for capture legal move
                g2d.setColor(LEGAL_MOVE_COLOR);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        }

        // Draw piece
        if (piece != null) {
            try {
                ImageIcon icon = new ImageIcon(getClass().getResource("/resources/" + piece.getPieceImage()));
                Image image = icon.getImage();
                int x = (getWidth() - icon.getIconWidth()) / 2;
                int y = (getHeight() - icon.getIconHeight()) / 2;
                g2d.drawImage(image, x, y, this);
            } catch (Exception e) {
                // Fallback text rendering if image fails to load
                String pieceType = piece.getType();
                g2d.setColor(Color.BLACK);
                g2d.setFont(new Font("Arial", Font.BOLD, 24));
                FontMetrics metrics = g2d.getFontMetrics();
                int x = (getWidth() - metrics.stringWidth(pieceType)) / 2;
                int y = ((getHeight() - metrics.getHeight()) / 2) + metrics.getAscent();
                g2d.drawString(pieceType, x, y);
            }
        }
    }
}