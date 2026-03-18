package rasterizers;

import rasters.Raster;
import java.awt.Color;

// rasterizer pro kreslení dotted čar
public class DottedRasterizer extends TrivialRasterizer {
    //rozestupy mezi jednotlivými tečkami
    private int counter = 0;

    public DottedRasterizer(Raster raster, Color color) {
        super(raster, color);
    }

    @Override
    protected void beforeRasterize() {
        counter = 0;
    }

    public void drawPixel(int x, int y, Color color, int thickness) {
        // výpočet mezery - čím tlustší čára, tím větší rozestup (aby to bylo hezký)
        if (counter % (thickness * 2) == 0) {

            super.drawPixel(x, y, color, thickness);
        }

        counter++;
    }
}