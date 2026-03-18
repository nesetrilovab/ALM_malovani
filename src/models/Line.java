package models;

import rasterizers.Rasterizer;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

//Třída reprezentující úsečku definovanou dvěma body.

public class Line implements Drawable {
    private Point p1, p2;
    private Color color;
    private LineStyle style;
    private int thickness;

    public Line(Point p1, Point p2, LineStyle style, Color color, int thickness) {
        this.p1 = p1;
        this.p2 = p2;
        this.style = style;
        this.color = color;
        this.thickness = thickness;
    }

    @Override
    public void rasterize(Rasterizer r) {
        // Předání barvy a samotného objektu k vykreslení
        r.setColor(this.color);
        r.rasterize(this);
    }

    @Override
    public void move(int dx, int dy) {
        // Posunutí obou koncových bodů
        p1.setX(p1.getX() + dx);
        p1.setY(p1.getY() + dy);
        p2.setX(p2.getX() + dx);
        p2.setY(p2.getY() + dy);
    }

    @Override
    public boolean contains(int x, int y) {
        // Tolerance 5 pixelů pro snadnější kliknutí na tenkou čáru
        return distanceFromLine(x, y) < 5;
    }

    //metoda pro výpočet nejkratší vzdálenosti bodu od úsečky - pro select mód
    private double distanceFromLine(int px, int py) {
        double x1 = p1.getX(), y1 = p1.getY();
        double x2 = p2.getX(), y2 = p2.getY();

        // Vektorové řešení průmětu bodu na úsečku
        double A = px - x1, B = py - y1, C = x2 - x1, D = y2 - y1;
        double dot = A * C + B * D;
        double lenSq = C * C + D * D;
        double param = lenSq != 0 ? dot / lenSq : -1;

        double xx, yy;
        if (param < 0) { xx = x1; yy = y1; } // Bod je před začátkem
        else if (param > 1) { xx = x2; yy = y2; } // Bod je za koncem
        else { xx = x1 + param * C; yy = y1 + param * D; } // Kolmý průmět na úsečku

        return new Point(px, py).distance(new Point((int)xx, (int)yy));
    }

    @Override
    public List<Point> getControlPoints() {
        //kontrolní body (start a konec)
        List<Point> pts = new ArrayList<>();
        pts.add(p1);
        pts.add(p2);
        return pts;
    }

    @Override
    public void movePoint(Point p, int x, int y) {
        // Umožňuje uchopit a táhnout pouze jeden konec úsečky
        if (p == p1) { p1.setX(x); p1.setY(y); }
        if (p == p2) { p2.setX(x); p2.setY(y); }
    }


    @Override public Color getColor() { return color; }
    public LineStyle getStyle() { return style; }
    public Point getP1() { return p1; }
    public Point getP2() { return p2; }
    @Override public int getThickness() { return thickness; }
}