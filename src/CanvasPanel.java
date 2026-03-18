import models.*;

import models.Point;
import rasterizers.*;
import rasters.*;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

//Hlavní kreslicí plocha - logika nástrojů, správa seznamu tvarů a jejich vykreslování do rastru.

public class CanvasPanel extends JPanel {

    public enum Mode { LINE, POLYGON, RECTANGLE, ELLIPSE, SELECT, ERASE, FILL }

    // Aktuální nastavení
    private Mode mode = Mode.LINE;
    private LineStyle currentStyle = LineStyle.SOLID;
    private Color currentColor = Color.GREEN;
    private int currentThickness = 1;

    // Komponenty pro práci s obrazem a alms
    private Raster raster;
    private CanvasRasterizer canvasRasterizer;
    private List<Drawable> shapes = new ArrayList<>();

    // Pomocné proměnné pro interakci
    private Drawable selectedShape = null;
    private Point selectedControlPoint = null;
    private int lastMouseX, lastMouseY;
    private Drawable currentShape = null;
    private Point startPoint;
    private boolean shiftMode = false;

    public CanvasPanel(int width, int height) {
        raster = new RasterBufferedImage(width, height);

        // rasterizery pro různé styly čar
        Rasterizer normal = new TrivialRasterizer(raster, currentColor);
        Rasterizer dotted = new DottedRasterizer(raster, currentColor);
        Rasterizer dashed = new DashedRasterizer(raster, currentColor);
        canvasRasterizer = new CanvasRasterizer(normal, dotted, dashed);

        setPreferredSize(new Dimension(width, height));
        setFocusable(true);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        raster.repaint(g);
    }

    //rce naprvní kliknutí myši
    public void startShape(int x, int y) {
        if (mode == Mode.ERASE) {
            for (int i = shapes.size() - 1; i >= 0; i--) {
                if (shapes.get(i).contains(x, y)) {
                    shapes.remove(i);
                    break;
                }
            }
            redrawAll();
            return;
        }
        else if (mode == Mode.FILL) {
            shapes.add(new Seed(new Point(x, y), currentColor));
            redrawAll();
            return;
        }

        Point clickedPoint = new Point(x, y);

        if (mode == Mode.SELECT) {
            selectAt(x, y); // Pokusí se vybrat tvar nebo jeho kontrolní bod
        } else if (mode == Mode.POLYGON) {
            // Polygon se skládá postupně z více bodů
            if (currentShape == null || !(currentShape instanceof MyPolygon)) {
                currentShape = new MyPolygon(currentStyle, new Color(currentColor.getRGB()), currentThickness);
                shapes.add(currentShape);
            }
            MyPolygon poly = (MyPolygon) currentShape;
            if (shiftMode && poly.size() > 0) {
                clickedPoint = snapTo45(poly.getPoints().get(poly.size() - 1), clickedPoint);
            }
            poly.addPoint(clickedPoint);
            redrawAll();
        } else {
            startPoint = clickedPoint; // Pro ostatní tvary si jen zapamatuje start
        }
    }

    //Aktualizace tvaru během pohybu myši - preview
    public void updateShape(int x, int y) {
        Point endPoint = new Point(x, y);

        // Pokud jsme v módu SELECT, hýbeme s tvarem nebo jeho bodem
        if (mode == Mode.SELECT && selectedShape != null) {
            int dx = x - lastMouseX;
            int dy = y - lastMouseY;
            if (selectedControlPoint != null) {
                selectedShape.movePoint(selectedControlPoint, x, y);
            } else {
                selectedShape.move(dx, dy);
            }
            lastMouseX = x;
            lastMouseY = y;
        } else {
            // Vytváření preview
            switch (mode) {
                case LINE -> {
                    if (shiftMode) endPoint = snapTo45(startPoint, endPoint);
                    currentShape = new Line(startPoint, endPoint, currentStyle, currentColor, currentThickness);
                }
                case RECTANGLE -> {
                    Point p2 = shiftMode ? calculateSquarePoint(startPoint, endPoint) : endPoint;
                    currentShape = new RectangleShape(startPoint, p2, currentStyle, currentColor, currentThickness);
                }
                case ELLIPSE -> {
                    Point p2 = shiftMode ? calculateSquarePoint(startPoint, endPoint) : endPoint;
                    currentShape = new EllipseShape(startPoint, p2, currentStyle, currentColor, currentThickness);
                }
            }
            lastMouseX = x;
            lastMouseY = y;
        }
        redrawAll();
    }

   //uložení tvaru
    public void finishShape(int x, int y) {
        if (currentShape != null && mode != Mode.POLYGON) {
            // Finální snapování (aby čára zůstala rovná i po puštění myši)
            Point endPoint = new Point(x, y);
            if (mode == Mode.LINE && shiftMode) {
                endPoint = snapTo45(startPoint, endPoint);
                currentShape = new Line(startPoint, endPoint, currentStyle, currentColor, currentThickness);
            } else if (shiftMode && (mode == Mode.RECTANGLE || mode == Mode.ELLIPSE)) {
                endPoint = calculateSquarePoint(startPoint, endPoint);
                if (mode == Mode.RECTANGLE) currentShape = new RectangleShape(startPoint, endPoint, currentStyle, currentColor, currentThickness);
                else currentShape = new EllipseShape(startPoint, endPoint, currentStyle, currentColor, currentThickness);
            }

            shapes.add(currentShape);
            currentShape = null;
        }
        redrawAll();
    }

    //Hledá objekt "pod myší" - prioritu mají kontrolní body
    public void selectAt(int x, int y) {
        selectedShape = null;
        selectedControlPoint = null;
        lastMouseX = x; lastMouseY = y;

        // Kontrola
        for (Drawable shape : shapes) {
            for (Point cp : shape.getControlPoints()) {
                if (new Point(x, y).distance(cp) < 15) {
                    selectedControlPoint = cp;
                    selectedShape = shape;
                    return;
                }
            }
        }

        // Pokud ne, zkusíme najít celý tvar
        for (int i = shapes.size() - 1; i >= 0; i--) {
            if (shapes.get(i).contains(x, y)) {
                selectedShape = shapes.get(i);
                return;
            }
        }
    }

    //alm flood fill jde z bodu do okolí, dokud naráží na stejnou barvu
    private void floodFill(int x, int y, Color fill) {
        int targetColor = raster.getPixel(x, y);
        int replacementColor = fill.getRGB();
        if (targetColor == replacementColor) return;

        Stack<Point> stack = new Stack<>();
        stack.push(new Point(x, y));

        while (!stack.isEmpty()) {
            Point p = stack.pop();
            int px = p.getX(); int py = p.getY();

            if (px < 0 || px >= raster.getWidth() || py < 0 || py >= raster.getHeight()) continue;

            if (raster.getPixel(px, py) == targetColor) {
                raster.setPixel(px, py, replacementColor);
                // Kontrola sousedů v okolí
                stack.push(new Point(px + 1, py));
                stack.push(new Point(px - 1, py));
                stack.push(new Point(px, py + 1));
                stack.push(new Point(px, py - 1));
            }
        }
    }

    public void clearCanvas() {
        shapes.clear();
        currentShape = null;
        redrawAll();
    }

    //metoda vymaže rastr a vykreslí úplně všechno znovu ze seznamu shapes - tvary se "nepřemazávají" a zůstávají editovatelné

    public void redrawAll() {
        if (raster == null) return;
        raster.clear();

        //historie všech tvarů
        for (Drawable shape : shapes) {
            if (shape instanceof Seed s) {
                floodFill(s.getPoint().getX(), s.getPoint().getY(), s.getColor());
            } else {
                shape.rasterize(canvasRasterizer);
            }
            drawShapeControlPoints(shape);
        }

        // Vykreslení preview
        if (currentShape != null && mode != Mode.POLYGON) {
            currentShape.rasterize(canvasRasterizer);
        }
        repaint();
    }

    public void finalizePolygon() {
        currentShape = null;
        redrawAll();
    }

    //Vykreslí kontrolní body vybraných tvarů
    private void drawShapeControlPoints(Drawable shape) {
        Graphics g = raster.getGraphics();
        g.setColor(shape.getColor());
        for (Point p : shape.getControlPoints()) {
            g.fillRect(p.getX() - 3, p.getY() - 3, 6, 6);
        }
    }


    public void setMode(Mode mode) {
        this.mode = mode;
        this.currentShape = null;
        setCursor(new Cursor(mode == Mode.ERASE ? Cursor.CROSSHAIR_CURSOR : Cursor.DEFAULT_CURSOR));
    }
    public void setLineStyle(LineStyle style) { this.currentStyle = style; }
    public void setShiftMode(boolean value) { this.shiftMode = value; }
    public Mode getMode() { return mode; }
    public void setCurrentColor(Color color) { this.currentColor = color; }
    public void setCurrentThickness(int thickness) { this.currentThickness = thickness; }
    public Color getCurrentColor() { return currentColor; }

    // Vypočítá bod tak, aby výsledek tvořil čtverec (pro shift + rectangle)
    private Point calculateSquarePoint(Point start, Point end) {
        int dx = end.getX() - start.getX();
        int dy = end.getY() - start.getY();
        int side = Math.max(Math.abs(dx), Math.abs(dy));
        int newX = start.getX() + (int) Math.copySign(side, dx);
        int newY = start.getY() + (int) Math.copySign(side, dy);
        return new Point(newX, newY);
    }

    // Přichytí čáru k nejbližšímu úhlu 45° (pro shift + line)
    private Point snapTo45(Point a, Point b) {
        double dx = b.getX() - a.getX();
        double dy = b.getY() - a.getY();
        double angle = Math.round(Math.toDegrees(Math.atan2(dy, dx)) / 45) * 45;
        double len = Math.hypot(dx, dy);
        return new Point(
                a.getX() + (int) (len * Math.cos(Math.toRadians(angle))),
                a.getY() + (int) (len * Math.sin(Math.toRadians(angle)))
        );
    }
}