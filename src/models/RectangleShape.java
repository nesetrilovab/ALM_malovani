package models;

import rasterizers.Rasterizer;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

//Třída reprezentující obdélník definovaný dvěma body

public class RectangleShape implements Drawable {
    private Point p1, p2;
    private LineStyle style;
    private Color color;
    private int thickness;

    public RectangleShape(Point p1, Point p2, LineStyle style, Color color, int thickness) {
        this.p1 = p1;
        this.p2 = p2;
        this.style = style;
        this.color = color;
        this.thickness = thickness;
    }

    @Override
    public void rasterize(Rasterizer r) {
        // aby se obdélník správně vykreslil i při tažení myši doleva nebo nahoru
        int x1 = Math.min(p1.getX(), p2.getX());
        int x2 = Math.max(p1.getX(), p2.getX());
        int y1 = Math.min(p1.getY(), p2.getY());
        int y2 = Math.max(p1.getY(), p2.getY());

        r.setColor(this.color);

        // Obdélník je složen ze čtyř instancí Line
        r.rasterize(new Line(new Point(x1, y1), new Point(x2, y1), style, color, thickness));
        r.rasterize(new Line(new Point(x2, y1), new Point(x2, y2), style, color, thickness));
        r.rasterize(new Line(new Point(x2, y2), new Point(x1, y2), style, color, thickness));
        r.rasterize(new Line(new Point(x1, y2), new Point(x1, y1), style, color, thickness));
    }

    @Override
    public void move(int dx, int dy) {
        p1.setX(p1.getX() + dx); p1.setY(p1.getY() + dy);
        p2.setX(p2.getX() + dx); p2.setY(p2.getY() + dy);
    }

    @Override
    public boolean contains(int x, int y) {
        int xMin = Math.min(p1.getX(), p2.getX());
        int xMax = Math.max(p1.getX(), p2.getX());
        int yMin = Math.min(p1.getY(), p2.getY());
        int yMax = Math.max(p1.getY(), p2.getY());
        return x >= xMin && x <= xMax && y >= yMin && y <= yMax;
    }

    @Override
    public List<Point> getControlPoints() {
        // Obdélník lze natahovat za jeho dva diagonální rohy
        List<Point> pts = new ArrayList<>();
        pts.add(p1); pts.add(p2);
        return pts;
    }

    @Override
    public void movePoint(Point p, int x, int y) {
        p.setX(x); p.setY(y);
    }

    @Override public Color getColor() { return color; }
    @Override public int getThickness() { return thickness; }
}