package rasterizers;

import models.Line;
import java.awt.Color;

//Rozhraní definující  operace pro věechny algoritmy vykreslování do rastru
public interface Rasterizer {

    void setColor(Color color);

    void rasterize(Line line);

    void drawPixel(int x, int y, Color color, int thickness);
}