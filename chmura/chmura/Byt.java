package chmura;

public class Byt {
    private int x, y;

    public Byt(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void przesun(int x, int y) {
        this.x += x;
        this.y += y;
    }
}
