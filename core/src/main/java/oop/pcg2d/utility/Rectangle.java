package oop.pcg2d.utility;

import java.util.Vector;

public class Rectangle {
    private int topLeftX;
    private int topLeftY;
    private int width;
    private int height;

    public Rectangle(int topLeftX, int topLeftY, int width, int height) {
        this.topLeftX = topLeftX;
        this.topLeftY = topLeftY;
        this.width = width;
        this.height = height;
    }

    public boolean isOverlap(Rectangle other) {
        if (this.topLeftX < other.topLeftX + other.width + 1 &&
            this.topLeftX + this.width + 1> other.topLeftX &&
            this.topLeftY < other.topLeftY + other.height + 1 &&
            this.topLeftY + this.height + 1 > other.topLeftY) return true;
        else return false;
    }

    public Vector<Pair> getAllPoints() {
        Vector<Pair> result = new Vector<>();
        for (int y = 0; y < this.height; y++) {
            for (int x = 0; x < this.width; x++) {
                result.add(new Pair(this.topLeftX + x, this.topLeftY + y));
            }
        }
        return result;
    }
}
