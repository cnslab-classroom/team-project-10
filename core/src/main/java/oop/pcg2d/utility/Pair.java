package oop.pcg2d.utility;

public class Pair {
    private final int x;
    private final int y;

    public Pair(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean equals(Pair other) {
        return (this.x == other.x && this.y == other.y);
    }

    public boolean equals(int x, int y) {
        return (this.x == x && this.y == y);
    }
}
