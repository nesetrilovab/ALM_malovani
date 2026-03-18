package models;

import rasterizers.Rasterizer;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

//Třída reprezentující elipsu definovanou dvěma protilehlými body (obdélníkem).
public class EllipseShape implements Drawable {
    private Point topLeft, bottomRight;
    private LineStyle style;
    private Color color;
    private int thickness;

    public EllipseShape(Point tl, Point br, LineStyle style, Color color, int thickness) {
        this.topLeft = tl;
        this.bottomRight = br;
        this.style = style;
        this.color = color;
        this.thickness = thickness;
    }

    @Override
    public void rasterize(Rasterizer r) {
        // Normalizace souřadnic (aby fungovalo tažení z jakékoliv strany)
        int x1 = Math.min(this.topLeft.getX(), this.bottomRight.getX());
        int y1 = Math.min(this.topLeft.getY(), this.bottomRight.getY());
        int x2 = Math.max(this.topLeft.getX(), this.bottomRight.getX());
        int y2 = Math.max(this.topLeft.getY(), this.bottomRight.getY());

        // Výpočet poloos (a, b) a středu elipsy
        double a = (x2 - x1) / 2.0;
        double b = (y2 - y1) / 2.0;
        double centerX = x1 + a;
        double centerY = y1 + b;

        // Výpočet počtu kroků pro hladké vykreslení
        int steps = (int) (Math.PI * Math.max(a, b) * 2);
        if (steps < 50) steps = 50;

        for (int i = 0; i < steps; i++) {
            // Styly
            if (style == LineStyle.DOTTED && i % 4 != 0) continue;
            if (style == LineStyle.DASHED && i % 12 >= 6) continue;

            // výpočet souřadnic bodu na obvodu
            double angle = 2.0 * Math.PI * i / steps;
            int px = (int) (centerX + a * Math.cos(angle));
            int py = (int) (centerY + b * Math.sin(angle));


            r.drawPixel(px, py, this.color, this.thickness);
        }
    }

    @Override
    public void move(int dx, int dy) {
        // Posun obou definičních bodů najednou
        topLeft.setX(topLeft.getX() + dx); topLeft.setY(topLeft.getY() + dy);
        bottomRight.setX(bottomRight.getX() + dx); bottomRight.setY(bottomRight.getY() + dy);
    }

    @Override
    public boolean contains(int x, int y) {
        // Detekce kliknutí
        int minX = Math.min(topLeft.getX(), bottomRight.getX());
        int maxX = Math.max(topLeft.getX(), bottomRight.getX());
        int minY = Math.min(topLeft.getY(), bottomRight.getY());
        int maxY = Math.max(topLeft.getY(), bottomRight.getY());

        return x >= minX && x <= maxX && y >= minY && y <= maxY;
    }

    @Override
    public List<Point> getControlPoints() {
        // Vrátí body, za které lze elipsu natahovat
        List<Point> pts = new ArrayList<>();
        pts.add(topLeft); pts.add(bottomRight);
        return pts;
    }

    @Override
    public void movePoint(Point p, int x, int y) { p.setX(x); p.setY(y); }

    @Override
    public Color getColor() { return color; }

    @Override
    public int getThickness() { return thickness; }
}