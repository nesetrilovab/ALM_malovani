package rasterizers;

import models.Line;
import models.Point;
import rasters.Raster;
import java.awt.Color;

//Základní implementace rasterizeru - alm pro kreslení úsečky, logiku pro simulaci tloušťky čáry
public class TrivialRasterizer implements Rasterizer {
    protected Raster raster;
    protected Color defaultColor;

    public TrivialRasterizer(Raster raster, Color defaultColor) {
        this.raster = raster;
        this.defaultColor = defaultColor;
    }

    @Override
    public void setColor(Color color) {
        this.defaultColor = color;
    }

    //Vykreslí "tlustý" pixel - pokud je tloušťka > 1, vykreslí kolem středu čtverec pixelů

    @Override
    public void drawPixel(int x, int y, Color color, int thickness) {
        if (x < 0 || x >= raster.getWidth() || y < 0 || y >= raster.getHeight()) return;

        if (thickness <= 1) {
            raster.setPixel(x, y, color.getRGB());
            return;
        }

        // Výpočet odsazení
        int half = thickness / 2;
        for (int dx = -half; dx < -half + thickness; dx++) {
            for (int dy = -half; dy < -half + thickness; dy++) {
                int px = x + dx;
                int py = y + dy;

                // hranice pole
                if (px >= 0 && px < raster.getWidth() && py >= 0 && py < raster.getHeight()) {
                    raster.setPixel(px, py, color.getRGB());
                }
            }
        }
    }

    //alm pro převod úsečky na pixely - používá lineární rovnici y = kx + q.

    @Override
    public void rasterize(Line line) {
        beforeRasterize();

        Color colorToUse = line.getColor();
        int thickness = line.getThickness();

        Point p1 = line.getP1();
        Point p2 = line.getP2();

        int x1 = p1.getX();
        int y1 = p1.getY();
        int x2 = p2.getX();
        int y2 = p2.getY();

        //Svislá čára
        if (x1 == x2) {
            int startY = Math.min(y1, y2);
            int endY = Math.max(y1, y2);
            for (int y = startY; y <= endY; y++) {
                drawPixel(x1, y, colorToUse, thickness);
            }
            return;
        }

        //směrnice úsečky
        double k = (y2 - y1) / (double) (x2 - x1);
        double q = y1 - k * x1;

        // Pokud je úsečka spíš vodorovná, iterace podle osy X
        if (Math.abs(k) < 1) {
            if (x1 > x2) { int tmp = x1; x1 = x2; x2 = tmp; }
            for (int x = x1; x <= x2; x++) {
                int y = (int) Math.round(k * x + q);
                drawPixel(x, y, colorToUse, thickness);
            }
        }
        // Pokud je úsečka spíše svislá, iteracw podle osy Y
        else {
            if (y1 > y2) { int tmp = y1; y1 = y2; y2 = tmp; }
            for (int y = y1; y <= y2; y++) {
                int x = (int) Math.round((y - q) / k);
                drawPixel(x, y, colorToUse, thickness);
            }
        }
    }

    protected void beforeRasterize() {}
}