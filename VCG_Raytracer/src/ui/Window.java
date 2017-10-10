package ui;

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
    private void setOutputLabel(String computationTime, int recursions, AntiAliasing antiAliasing, int multiThreading) {

        String description = "Rendering time: " + computationTime + " min, Recursions: " + recursions + ", " + antiAliasing + ", Threads: " + multiThreading;

        Graphics graphic = mBufferedImage.getGraphics();
        graphic.setColor(Color.black);
        graphic.fill3DRect(0, mHeight - 30, (int) (description.length() * 5.8f), mHeight, true);
        graphic.setColor(Color.green);
//        graphic.setFont();
        graphic.drawString(description, 10, mHeight - 10);

        mFrame.repaint();
    }

    /**
     * Draw pixel to our render frame
     **/
    public void setPixel(BufferedImage bufferedImage, RgbColor color, Vec2 screenPosition) {

//        Log.print(this, "Pixel (" + (int) screenPosition.x + "," + (int) screenPosition.y + ") has color " + color);
//        System.out.println("");

//        if(screenPosition.equals(new Vec2(5, 420))) {
//            Log.print(this,"Pixel 5, 420 has color " + color);
//        }
//
//        if(screenPosition.equals(new Vec2(5, 510))) {
//            Log.print(this,"Pixel 5, 510 has color " + color);
//        }

        bufferedImage.setRGB((int) screenPosition.x, (int) screenPosition.y, color.getRGB());
        mFrame.repaint();
    }

    /**
     * Exports the rendering to a PNG image "raytracing.png".
     */
    public void exportRendering(){
        exportRendering("raytracing");
    }

    /**
     * Exports the rendering to a PNG image with the given name (file extension will be added automaticly).
     */
    public void exportRendering(String fileName) {
        DataExporter.exportImageToPng(mBufferedImage, fileName + ".png");
    }

    /**
     * Exports the rendering to a PNG image with rendering information
     **/
    public void exportRendering(double time, int recursions, AntiAliasing antiAliasing, int multiThreading) {
        setOutputLabel(TimeFormater(time), recursions, antiAliasing, multiThreading);
        DataExporter.exportImageToPng(mBufferedImage, "raytracing.png");
    }

    /**
     * Formats the given time in seconds to a MM:SS:MS format.
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
