package platformer.world;

import java.io.Serializable;
import java.util.Objects;

public class Location implements Serializable {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Location location = (Location) o;
        return Double.compare(location.x, x) == 0 &&
                Double.compare(location.y, y) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
