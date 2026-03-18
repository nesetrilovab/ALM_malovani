package models;

//Základní třída pro reprezentaci bodu

public class Point {
    private int x, y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() { return x; }
    public void setX(int x) { this.x = x; }
    public int getY() { return y; }
    public void setY(int y) { this.y = y; }

    //Vypočítá euklidovskou vzdálenost mezi tímto bodem a jiným bodem - pro detekci výběru objektů
    public double distance(Point p) {
        // Pythagorova věta
        return Math.sqrt(Math.pow(this.x - p.x, 2) + Math.pow(this.y - p.y, 2));
    }
}