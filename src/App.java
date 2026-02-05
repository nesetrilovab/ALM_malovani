import models.Line;
import models.LineCanvas;
import models.Point;
import rasterizers.CanvasRasterizer;
import rasterizers.DottedRasterizer;
import rasterizers.Rasterizer;
import rasterizers.TrivialRasterizer;
import rasters.Raster;
import rasters.RasterBufferedImage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.Serial;

public class App {

    private final JPanel panel;
    private final Raster raster;

    private Rasterizer normalRasterizer;
    private Rasterizer dottedRasterizer;
    private CanvasRasterizer canvasRasterizer;

    private MouseAdapter mouseAdapter;
    private KeyAdapter keyAdapter;

    private Point pPomocny;
    private LineCanvas lineCanvas;

    private boolean dottedMode = false;
    private boolean shiftMode = false; // nové pro zarovnání

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new App(800, 600).start());
    }

    public void clear(int color) {
        raster.setClearColor(color);
        raster.clear();
    }

    public void present(Graphics graphics) {
        raster.repaint(graphics);
    }

    public void start() {
        clear(0xaaaaaa);
        panel.repaint();
    }

    public App(int width, int height) {
        JFrame frame = new JFrame();

        frame.setLayout(new BorderLayout());
        frame.setTitle("Delta : " + this.getClass().getName());
        frame.setResizable(true);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        // RASTER
        raster = new RasterBufferedImage(width, height);

        // RASTERIZERY
        normalRasterizer = new TrivialRasterizer(raster, Color.GREEN);
        dottedRasterizer = new DottedRasterizer(raster, Color.GREEN);
        canvasRasterizer = new CanvasRasterizer(normalRasterizer, dottedRasterizer);

        // CANVAS
        lineCanvas = new LineCanvas();

        panel = new JPanel() {
            @Serial
            private static final long serialVersionUID = 1L;

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                present(g);
            }
        };

        panel.setPreferredSize(new Dimension(width, height));
        frame.add(panel, BorderLayout.CENTER);

        createAdapters();

        panel.addMouseListener(mouseAdapter);
        panel.addMouseMotionListener(mouseAdapter);
        panel.addKeyListener(keyAdapter);

        frame.pack();
        frame.setVisible(true);

        panel.requestFocusInWindow();
    }


    private Point snapTo45Degrees(Point start, Point end) {
        double dx = end.getX() - start.getX();
        double dy = end.getY() - start.getY();

        double angle = Math.atan2(dy, dx); // aktuální úhel
        double deg = Math.toDegrees(angle);

        // zaokrouhlení na nejbližší násobek 45
        double snappedDeg = Math.round(deg / 45.0) * 45;

        // zpět na radiány
        double snappedRad = Math.toRadians(snappedDeg);

        double length = Math.hypot(dx, dy);

        int newX = start.getX() + (int)Math.round(length * Math.cos(snappedRad));
        int newY = start.getY() + (int)Math.round(length * Math.sin(snappedRad));

        return new Point(newX, newY);
    }


    private void createAdapters() {
        mouseAdapter = new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                pPomocny = new Point(e.getX(), e.getY());
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                Point p2 = new Point(e.getX(), e.getY());

                if (shiftMode) { // snap při uvolnění
                    p2 = snapTo45Degrees(pPomocny, p2);
                }

                Line line = new Line(pPomocny, p2, dottedMode);

                raster.clear();
                lineCanvas.addLine(line);
                canvasRasterizer.rasterize(lineCanvas);

                panel.repaint();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                Point p2 = new Point(e.getX(), e.getY());

                if (shiftMode) { // snap během drag pro náhled
                    p2 = snapTo45Degrees(pPomocny, p2);
                }

                Line preview = new Line(pPomocny, p2, dottedMode);

                raster.clear();
                canvasRasterizer.rasterize(lineCanvas);

                if (dottedMode) {
                    dottedRasterizer.rasterize(preview);
                } else {
                    normalRasterizer.rasterize(preview);
                }

                panel.repaint();
            }
        };

        keyAdapter = new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_CONTROL) {
                    dottedMode = true;
                }

                if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
                    shiftMode = true; // držení shiftu
                }

                if (e.getKeyCode() == KeyEvent.VK_C) {
                    lineCanvas.getLines().clear();
                    raster.clear();
                    panel.repaint();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_CONTROL) {
                    dottedMode = false;
                }

                if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
                    shiftMode = false;
                }
            }
        };
    }
}
