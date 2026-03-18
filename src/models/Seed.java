package models;

import rasterizers.Rasterizer;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

//Třída Seed reprezentuje počáteční bod pro Flood Fill - ukládá se do seznamu shapes, aby výplň zůstala i po překreslení plátna.

public class Seed implements Drawable {
    private Point point; // zde uživatel klikl pro vylití barvy
    private Color color;

    public Seed(Point p, Color c) {
        this.point = p;
        this.color = c;
    }

    @Override
    public void rasterize(Rasterizer r) {
        // Flood Fill je řešeno v redrawAll()
    }

    @Override
    public Color getColor() { return color; }

    public Point getPoint() { return point; }

    //Metody z Drawable, které pro Seed nemají význam

    @Override public void move(int dx, int dy) {}

    @Override public boolean contains(int x, int y) {
        return false;
    }

    @Override public List<Point> getControlPoints() {
        return new ArrayList<>();
    }

    @Override public void movePoint(Point p, int x, int y) {}

    @Override public int getThickness() {
        return 1;
    }
}