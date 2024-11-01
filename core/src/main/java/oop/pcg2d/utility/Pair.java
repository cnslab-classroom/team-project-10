package oop.pcg2d.utility;

public class Pair {
    private final int X;
    private final int Y;

    public Pair(int x, int y) {
        this.X = x;
        this.Y = y;
    }

    public int getX() {
        return X;
    }

    public int getY() {
        return Y;
    }

    public boolean equals(Pair other) {
        return (this.X == other.X && this.Y == other.Y);
    }

    public boolean equals(int x, int y) {
        return (this.X == x && this.Y == y);
    }
}
