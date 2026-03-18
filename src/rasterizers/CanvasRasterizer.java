package rasterizers;

import models.Line;
import models.LineStyle;
import java.awt.Color;

//Hlavní rasterizer, který podle stylu čáry rozděluje práci na konkrétní subrasterizer
public class CanvasRasterizer implements Rasterizer {
    private final Rasterizer solidRasterizer;
    private final Rasterizer dottedRasterizer;
    private final Rasterizer dashedRasterizer;

    public CanvasRasterizer(Rasterizer solid, Rasterizer dotted, Rasterizer dashed) {
        this.solidRasterizer = solid;
        this.dottedRasterizer = dotted;
        this.dashedRasterizer = dashed;
    }

    @Override
    public void setColor(Color color) {
        solidRasterizer.setColor(color);
        dottedRasterizer.setColor(color);
        dashedRasterizer.setColor(color);
    }
    //kreslí jednotlivý bod
    @Override
    public void drawPixel(int x, int y, Color color, int thickness) {
        solidRasterizer.drawPixel(x, y, color, thickness);
    }


    @Override
    public void rasterize(Line line) {
        //výběr stylu
        Rasterizer selected = getRasterizerForStyle(line.getStyle());

        //synchro barvy
        selected.setColor(line.getColor());

        selected.rasterize(line);
    }

    //Pomocná metoda která mapuje enum LineStyle
    public Rasterizer getRasterizerForStyle(LineStyle style) {
        if (style == null) return solidRasterizer;
        return switch (style) {
            case DOTTED -> dottedRasterizer;
            case DASHED -> dashedRasterizer;
            default -> solidRasterizer;
        };
    }
}