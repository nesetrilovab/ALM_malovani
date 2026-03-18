package rasterizers;

import rasters.Raster;
import java.awt.Color;

//rasterizer pro kreslení dashed čar, upravuje proces zápisu pixelu
public class DashedRasterizer extends TrivialRasterizer {
    //určuje, zda zrovna kreslíme čárku nebo mezeru
    private int counter = 0;

    public DashedRasterizer(Raster raster, Color color) {
        super(raster, color);
    }

    @Override
    protected void beforeRasterize() {
        counter = 0;
    }

    @Override
    public void drawPixel(int x, int y, Color color, int thickness) {
        //10 pixelů nakresleno, 5 pixelů je mezera
        if (counter % 15 < 10) {
            //fyzické vykreslení
            super.drawPixel(x, y, color, thickness);
        }

        counter++;
    }
}