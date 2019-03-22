package platformer.world;

public class Location {
    private double x, y;

    public Location(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public boolean inside(double lx, double ux, double ly, double uy) {
        return ux >= x && x >= lx && // x
                uy >= y && y >= ly; // y
    }
}
