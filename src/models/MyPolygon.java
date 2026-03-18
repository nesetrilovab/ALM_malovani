package models;

import rasterizers.Rasterizer;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

//Třída reprezentující polygon složený z libovolného počtu bodů.

public class MyPolygon implements Drawable {
    private List<Point> points;
    private LineStyle style;
    private Color color;
    private int thickness;

    public MyPolygon(LineStyle style, Color color, int thickness) {
        this.points = new ArrayList<>();
        this.style = style;
        this.color = color;
        this.thickness = thickness;
    }

    public void addPoint(Point p) {
        points.add(p);
    }

    public List<Point> getPoints() {
        return points;
    }

    public int size() {
        return points.size();
    }

    @Override
    public void rasterize(Rasterizer r) {
        // Polygon lze vykreslit, pouze pokud má min 3 vrcholy
        if (points.size() < 3) return;

        // Vykreslení jednotlivých hran polygonu
        for (int i = 0; i < points.size() - 1; i++) {
            r.rasterize(new Line(points.get(i), points.get(i + 1), style, this.color, this.thickness));
        }
        // spojení posledního bodu s 1.
        r.rasterize(new Line(points.get(points.size() - 1), points.get(0), style, this.color, this.thickness));
    }

    @Override
    public void move(int dx, int dy) {
        // Posunutí všech vrcholů
        for (Point p : points) {
            p.setX(p.getX() + dx);
            p.setY(p.getY() + dy);
        }
    }

    @Override
    public boolean contains(int x, int y) {
        if (points.isEmpty()) return false;

        // Výpočet ohraničujícího obdélníku pro detekci výběru
        int minX = Integer.MAX_VALUE, maxX = Integer.MIN_VALUE;
        int minY = Integer.MAX_VALUE, maxY = Integer.MIN_VALUE;

        for (Point p : points) {
            minX = Math.min(minX, p.getX()); maxX = Math.max(maxX, p.getX());
            minY = Math.min(minY, p.getY()); maxY = Math.max(maxY, p.getY());
        }
        return x >= minX && x <= maxX && y >= minY && y <= maxY;
    }

    @Override
    public List<Point> getControlPoints() {
        return points;
    }

    @Override
    public void movePoint(Point p, int x, int y) {
        p.setX(x);
        p.setY(y);
    }

    @Override public Color getColor() { return color; }

    @Override public int getThickness() { return thickness; }
}