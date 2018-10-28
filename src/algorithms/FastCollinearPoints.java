package algorithms;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;
import plane.LineSegment;
import plane.Point;

import java.awt.Color;
import java.util.Arrays;

public class FastCollinearPoints {

    private int noSegments;
    private LineSegment[] segments;
    private Point[] pointsStart;
    private Point[] pointsEnd;
    private double[] slopes;
    private int noCandidates;

    public FastCollinearPoints(Point[] points) {
        if (points == null) {
            throw new IllegalArgumentException("Argument is null!!!");
        }
        int capacity = points.length;
        int length = 0;
        for (int i = 0; i < capacity; i++) {
            if (points[i] == null) {
                throw new IllegalArgumentException("Null element in input array!!!");
            }
            for (int j = 0; j < length; j++) {
                if (points[j].slopeTo(points[i]) == Double.NEGATIVE_INFINITY) {
                    throw new IllegalArgumentException("Element already in array!!!");
                }
            }
            length++;
        }
        this.noSegments = 0;
        this.noCandidates = 0;
        this.segments = new LineSegment[1];
        this.pointsStart = new Point[1];
        this.pointsEnd = new Point[1];
        this.slopes = new double[1];
        findSegments(points);
    }

    public int numberOfSegments() {
        return noSegments;
    }

    public LineSegment[] segments() {
        LineSegment[] ls = new LineSegment[noSegments];
        for (int i = 0; i < noSegments; i++) {
            ls[i] = segments[i];
        }
        return ls;
    }

    private void findSegments(Point[] points) {
        int capacity = points.length;
        for (int i = 0; i < capacity - 1; i++) {
            Point[] pointsArray = new Point[capacity - 1 - i];
            int index = 0;
            for (int j = i + 1; j < capacity; j++) {
                pointsArray[index++] = points[j];
            }
            pointsArray = sort(points[i], pointsArray);
            /* for (int j = 0; j < pointsArray.length; j++) {
                StdOut.printf("Slope between %s and %s ---> %f\n", points[i], pointsArray[j], points[i].slopeTo(pointsArray[j]));
            } */
            int repetitions = 1;
            int noRepetitions = 1;
            double prevSlope = points[i].slopeTo(pointsArray[0]);
            int lastIndex = 0;
            for (int k = 1; k < pointsArray.length; k++) {
                boolean breakSlope = false;
                if (points[i].slopeTo(pointsArray[k]) == prevSlope) {
                    repetitions++;
                } else {
                    if (repetitions > 1) {
                        breakSlope = true;
                        noRepetitions = repetitions;
                        lastIndex = k - 1;
                    }
                    prevSlope = points[i].slopeTo(pointsArray[k]);
                    repetitions = 1;
                }
                if (k == pointsArray.length - 1 && repetitions >= 3) {
                    breakSlope = true;
                    noRepetitions = repetitions;
                    lastIndex = k;
                }
                if (breakSlope && noRepetitions >= 3) {
                    Point[] segment = new Point[noRepetitions + 1];
                    segment[0] = points[i];
                    int current = 1;
                    for (int j = 0; j < noRepetitions; j++) {
                        segment[current++] = pointsArray[lastIndex - j];
                    }
                    processSegment(segment);
                }
            }
        }
        arrangeSegments();
    }

    private static Point[] sort(Point point, Point[] points) {
        Point[] temp = new Point[points.length];
        return sort(point, points, temp, 0, points.length - 1);
    }

    private static Point[] sort(Point point, Point[] points, Point[] temp, int lo, int hi) {
        if (hi <= lo) {
            return points;
        }
        int mid = lo + (hi - lo) / 2;
        sort(point, points, temp, lo, mid);
        sort(point, points, temp, mid + 1, hi);
        return merge(point, points, temp, lo, mid, hi);
    }

    private static Point[] merge(Point point, Point[] points, Point[] temp, int lo, int mid, int hi) {
        for (int i = lo; i <= hi; i++) {
            temp[i] = points[i];
        }
        int i = lo;
        int j = mid + 1;
        for (int k = lo; k <= hi; k++) {
            if (i > mid) {
                points[k] = temp[j++];
            } else if (j > hi) {
                points[k] = temp[i++];
            } else if (less(point, temp[j], temp[i])) {
                points[k] = temp[j++];
            } else {
                points[k] = temp[i++];
            }
        }
        return points;
    }

    private static boolean less(Point p, Point p1, Point p2) {
        return p.slopeTo(p1) < p.slopeTo(p2);
    }

    private void addSegment(Point start, Point end, double slope) {
        if (noCandidates == pointsStart.length) {
            arrayRecordsResize(2 * pointsStart.length);
        }
        pointsStart[noCandidates] = start;
        pointsEnd[noCandidates] = end;
        slopes[noCandidates] = slope;
        noCandidates++;
    }

    private void arrayRecordsResize(int length) {
        Point[] newArrayStart = new Point[length];
        Point[] newArrayEnd = new Point[length];
        double[] newArraySlopes = new double[length];
        for (int i = 0; i < pointsStart.length; i++) {
            newArrayStart[i] = pointsStart[i];
            newArrayEnd[i] = pointsEnd[i];
            newArraySlopes[i] = slopes[i];
        }
        pointsStart = newArrayStart;
        pointsEnd = newArrayEnd;
        slopes = newArraySlopes;
    }

    private void processSegment(Point[] points) {
        Arrays.sort(points);
        Point start = points[0];
        Point end = points[points.length - 1];
        double slope = start.slopeTo(end);
        if (noCandidates == 0) {
            addSegment(start, end, slope);
            return;
        }
        boolean found = false;
        for (int i = 0; i < noCandidates; i++) {
            // StdOut.printf("start.slopeTo(pointsStart[i]) ---> %f, slope ---> %f\nstart ---> %s, pointsStart[i] ---> %s\n\n", start.slopeTo(pointsStart[i]), slope, start, pointsStart[i]);
            if (slopes[i] == slope && end.slopeTo(pointsStart[i]) == slope) {
                found = true;
                if (start.compareTo(pointsStart[i]) < 0) {
                    pointsStart[i] = start;
                }
                if (end.compareTo(pointsEnd[i]) > 0) {
                    pointsEnd[i] = end;
                }
            }
        }
        // StdOut.printf("start ---> %s, end ---> %s, slope ---> %f, found ---> %s\n", start, end, slope, found);
        if (!found) {
            addSegment(start, end, slope);
        }
    }

    private void arrangeSegments() {
        LineSegment[] segmentList = new LineSegment[noCandidates];
        int index = 0;
        for (int i = 0; i < noCandidates; i++) {
            LineSegment ls = new LineSegment(pointsStart[i], pointsEnd[i]);
            segmentList[index++] = ls;
        }
        segments = segmentList;
        noSegments = noCandidates;
    }

    public static void main(String[] args) {

        In in = new In("_data/rs1423.txt");
        int n = in.readInt();
        Point[] points = new Point[n];
        for (int i = 0; i < n; i++) {
            int x = in.readInt();
            int y = in.readInt();
            points[i] = new Point(x, y);
        }
        StdDraw.enableDoubleBuffering();
        StdDraw.setPenColor(Color.orange);
        StdDraw.setXscale(0, 32768);
        StdDraw.setYscale(0, 32768);
        for (Point p : points) {
            p.draw();
        }
        StdDraw.show();
        FastCollinearPoints collinear = new FastCollinearPoints(points);
        for (LineSegment segment : collinear.segments()) {
            StdOut.println(segment);
            segment.draw();
        }
        StdDraw.show();
        StdOut.printf("\nNumber of found segments ---> %d\n<<<---------END--------->>>\n", collinear.numberOfSegments());
    }

}
