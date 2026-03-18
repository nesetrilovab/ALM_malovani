package models;

import rasterizers.Rasterizer;
import java.awt.*;
import java.util.List;

//Rozhraní Drawable definuje základní schopnosti pro jakýkoliv objekt,který má být vykreslen na plátno a se kterým má uživatel manipulovat.
public interface Drawable {


    void rasterize(Rasterizer r);

    //Posune celý tvar o zadaný rozdíl souřadnic.
    void move(int dx, int dy);

    boolean contains(int x, int y);

    List<Point> getControlPoints();

    //Změní pozici konkrétního kontrolního bodu.
    void movePoint(Point p, int newX, int newY);

    Color getColor();

    int getThickness();
}