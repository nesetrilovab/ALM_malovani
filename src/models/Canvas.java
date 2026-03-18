package models;

import java.util.ArrayList;
import java.util.List;

//slouží jako datový kontejner pro uchování všech geometrických útvarů, drží seznam objektů k vykreslení

public class Canvas {

    private List<Drawable> shapes = new ArrayList<>();

    public void addShape(Drawable shape) {
        shapes.add(shape);
    }

    public List<Drawable> getShapes() {
        return shapes;
    }

    public void clear() {
        shapes.clear();
    }
}