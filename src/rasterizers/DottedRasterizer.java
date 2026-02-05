package rasterizers;

import models.Line;
import models.Point;
import rasters.Raster;

import java.awt.Color;

public class DottedRasterizer extends TrivialRasterizer {

    public DottedRasterizer(Raster raster, Color color) {
        super(raster, color);
    }

    @Override
    public void rasterize(Line line) {
        Point p1 = line.getP1();
        Point p2 = line.getP2();
        Raster raster = super.raster; // přístup k rastru z TrivialRasterizer
        Color color = super.defaultColor;

        int counter = 0; // counter pro tečky

        // Svislá čára
        if (p1.getX() == p2.getX()) {
            int x = p1.getX();
            int y1 = Math.min(p1.getY(), p2.getY());
            int y2 = Math.max(p1.getY(), p2.getY());

            for (int y = y1; y <= y2; y++) {
                if (counter % 5 == 0 && x >= 0 && x < raster.getWidth()
                        && y >= 0 && y < raster.getHeight()) {
                    raster.setPixel(x, y, color.getRGB());
                }
                counter++;
            }
            return;
        }

        double k = (p2.getY() - p1.getY()) / (double)(p2.getX() - p1.getX());
        double q = p1.getY() - k * p1.getX();

        if (Math.abs(k) < 1) {
            if (p1.getX() > p2.getX()) {
                Point tmp = p1; p1 = p2; p2 = tmp;
            }

            for (int x = p1.getX(); x <= p2.getX(); x++) {
                int y = (int)Math.round(k * x + q);

                if (counter % 5 == 0 && x >= 0 && x < raster.getWidth()
                        && y >= 0 && y < raster.getHeight()) {
                    raster.setPixel(x, y, color.getRGB());
                }
                counter++;
            }
        } else {
            if (p1.getY() > p2.getY()) {
                Point tmp = p1; p1 = p2; p2 = tmp;
            }

            for (int y = p1.getY(); y <= p2.getY(); y++) {
                int x = (int)Math.round((y - q) / k);

                if (counter % 5 == 0 && x >= 0 && x < raster.getWidth()
                        && y >= 0 && y < raster.getHeight()) {
                    raster.setPixel(x, y, color.getRGB());
                }
                counter++;
            }
        }
    }
}
