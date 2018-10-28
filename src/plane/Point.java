package plane;

import edu.princeton.cs.algs4.StdDraw;
import java.util.Comparator;

public class Point implements Comparable<Point> {

    private final int x;
    private final int y;

    public Point(int x, int y) {
        if (x < 0 && y < 0 && x > 32767 && y > 32767) {
            throw new IllegalArgumentException("Constructor arguments must be between 0 and 32767!!!");
        }
        this.x = x;
        this.y = y;
    }

    public void draw() {
        StdDraw.point(x, y);
    }

    public void drawTo(Point that) {
        StdDraw.line(this.x, this.y, that.x, that.y);
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }

    @Override
    public int compareTo(Point that) {
        if (this.y == that.y) {
            return this.x - that.x;
        } else {
            return this.y - that.y;
        }
    }

    public double slopeTo(Point that) {
        if (this.x == that.x && this.y == that.y) {
            return Double.NEGATIVE_INFINITY;
        }
        double numerator = that.y - this.y;
        if (numerator == 0) {
            return +0.0;
        }
        double denominator = that.x - this.x;
        if (denominator == 0) {
            return Double.POSITIVE_INFINITY;
        }
        return numerator / denominator;
    }

    public Comparator<Point> slopeOrder() {
        return new SlopeOrder();
    }

    private class SlopeOrder implements Comparator<Point> {

        @Override
        public int compare(Point x1, Point x2) {
            double slopex1 = slopeTo(x1);
            double slopex2 = slopeTo(x2);
            if (slopex1 < slopex2) {
                return -1;
            } else if (slopex1 > slopex2) {
                return +1;
            } else {
                return 0;
            }
        }
    }

}
