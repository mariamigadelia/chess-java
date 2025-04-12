package View;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

// View/PieceView.java
public class PieceView {
    private final BufferedImage image;

    public PieceView(String imgFilePath) {
        BufferedImage temp = null;
        try {
            temp = ImageIO.read(getClass().getResource(imgFilePath));
        } catch (IOException e) {
            System.out.println("Image load error: " + e.getMessage());
        }
        this.image = temp;
    }

    public void draw(Graphics g, int x, int y, int width, int height) {
        if (image != null) {
            g.drawImage(image, x, y, width, height, null);
        }
    }
}
