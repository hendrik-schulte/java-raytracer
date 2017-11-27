package ui;

import raytracer.Settings;
import utils.AntiAliasing;
import utils.RgbColor;
import utils.algebra.Vec2;
import utils.io.DataExporter;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Window {

    private int mWidth;
    private int mHeight;

    private BufferedImage mBufferedImage;

    private JFrame mFrame;

    /**
     * Create render window with the given dimensions
     **/
    public Window(int width, int height) {
        mWidth = width;
        mHeight = height;

        // we are using only one frame
        mBufferedImage = new BufferedImage(mWidth, mHeight, BufferedImage.TYPE_INT_RGB);

        createFrame();
    }

    public BufferedImage getBufferedImage() {
        return mBufferedImage;
    }

    /**
     * Setup render frame with given parameters
     **/
    private void createFrame() {
        JFrame frame = new JFrame();

        frame.getContentPane().add(new JLabel(new ImageIcon(mBufferedImage)));
        frame.setSize(mBufferedImage.getHeight() + frame.getSize().height, mBufferedImage.getWidth() + frame.getSize().width);
        frame.pack();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);

        mFrame = frame;
    }

    /**
     * Draw debug information
     **/
    private void setOutputLabel(String computationTime, Settings settings) {

        String description = "Rendering time: " + computationTime + " min, Recursions: " + settings.RECURSIONS + ", " + settings.ANTIALIASING + ", Threads: " + settings.MULTI_THREADING;

        Graphics graphic = mBufferedImage.getGraphics();
        graphic.setColor(Color.black);
        graphic.fill3DRect(0, mHeight - 30, (int) (description.length() * 5.8f), mHeight, true);
        graphic.setColor(Color.green);
        graphic.drawString(description, 10, mHeight - 10);

        mFrame.repaint();
    }

    /**
     * Draw pixel to our render frame
     **/
    public void setPixel(BufferedImage bufferedImage, RgbColor color, Vec2 screenPosition) {

        bufferedImage.setRGB((int) screenPosition.x, (int) screenPosition.y, color.getRGB());
        mFrame.repaint();
    }

    /**
     * Exports the rendering to a PNG image with rendering information.
     **/
    public void exportRendering(String fileName, double time, Settings settings) {
        if (settings.DRAW_STATS) setOutputLabel(TimeFormater(time), settings);
        if (settings.SAVE_FILE) DataExporter.exportImageToPng(mBufferedImage, fileName + ".png");
    }

    /**
     * Formats the given time in seconds to a MM:SS:MS format.
     *
     * @param timeInSeconds
     * @return
     */
    private String TimeFormater(double timeInSeconds) {
        int minutes = (int) (timeInSeconds / 60);
        int seconds = (int) (timeInSeconds - minutes * 60);
        int milliseconds = (int) (((timeInSeconds - minutes * 60) - seconds) * 100);

        return minutes + ":" +
                String.format("%02d", seconds) + ":" +
                String.format("%02d", milliseconds);
    }
}
