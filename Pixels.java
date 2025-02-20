import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/*
 * Pixel graphics driver for Chip8 emulator
 *
 * @author m33ls
 * @version 1.0.0
 */
class Pixels extends JPanel {

    private Graphics g;
    private int scale = 10; //10 pixels for each emulated-system pixel.
    private int width = 64 * scale;
    private int height = 32 * scale;

    private int[][] gfx;


    public Pixels(int[][] gfx) {
        this.gfx = gfx;

    }

    public Dimension getPreferredSize() {
        return new Dimension(width, height);
    }


    /**
     * Draw one pixel.
     */
    public void drawPixel(boolean white, int x, int y) {
        if (white) {
            g.setColor(Color.WHITE);
        } else {
            g.setColor(Color.BLACK);
        }

        g.fillRect(x * scale, y * scale, scale, scale);

    }

    /**
     * Draws full screen
     */
    private void drawFullScreen() {

        for (int y = 0; y < 32; y++) {
            for (int x = 0; x < 64; x++) {
                boolean value;

		if (gfx[y][x] != 0) { 
			value = true;
		} else {
			value = false;
		}
                drawPixel(value, x, y);
            }
        }
    }


    /**
     * Draw screen
     */
    public void drawScreen() {
        repaint();
    }


    /**
     * Paints the component. It has to be called through paintScreen().
     */
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        this.g = g;

        // Draw background
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, width, height);

        drawFullScreen();

    }
}
