package oop.pcg2d.utility;

public class Pair {
    private int x;
    private int y;

    public Pair(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Pair(Pair other) {
        this.x = other.x;
        this.y = other.y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public boolean equals(Pair other) {
        return (this.x == other.x && this.y == other.y);
    }

    public boolean equals(int x, int y) {
        return (this.x == x && this.y == y);
    }

    @Override
    public String toString() {
        return "(" + x + "," + y + ")";
    }

    public Pair getNorth() {
        return new Pair(this.x, this.y - 1);
    }

    public Pair getSouth() {
        return new Pair(this.x, this.y + 1);
    }

    public Pair getEast() {
        return new Pair(this.x + 1, this.y);
    }

    public Pair getWest() {
        return new Pair(this.x - 1, this.y);
    }

    public Pair getNE() {
        return new Pair(this.x + 1, this.y - 1);
    }

    public Pair getNW() {
        return new Pair(this.x - 1, this.y - 1);
    }

    public Pair getSE() {
        return new Pair(this.x + 1, this.y + 1);
    }

    public Pair getSW() {
        return new Pair(this.x - 1, this.y + 1);
    }
}
