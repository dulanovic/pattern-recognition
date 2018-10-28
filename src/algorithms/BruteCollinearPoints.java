package algorithms;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;
import plane.LineSegment;
import plane.Point;

import java.awt.Color;
import java.util.Arrays;

public class BruteCollinearPoints {

    private int noSegments;
    private LineSegment[] segments;

    public BruteCollinearPoints(Point[] points) {
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
        noSegments = 0;
        segments = new LineSegment[1];
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
        Point[] pointsSorted = Arrays.copyOf(points, points.length);
        Arrays.sort(pointsSorted);
        int arraySize = pointsSorted.length;
        for (int i = 0; i < arraySize - 3; i++) {
            for (int j = i + 1; j < arraySize - 2; j++) {
                for (int k = j + 1; k < arraySize - 1; k++) {
                    if (pointsSorted[i].slopeOrder().compare(pointsSorted[j], pointsSorted[k]) != 0) {
                        continue;
                    }
                    double slope = pointsSorted[i].slopeTo(pointsSorted[k]);
                    for (int m = k + 1; m < arraySize; m++) {
                        if (pointsSorted[i].slopeTo(pointsSorted[m]) != slope) {
                            continue;
                        }
                        LineSegment ls = new LineSegment(pointsSorted[i], pointsSorted[m]);
                        addSegment(ls);
                    }
                }
            }
        }
    }

    private void addSegment(LineSegment ls) {
        if (noSegments == segments.length) {
            arrayResize(2 * noSegments);
        }
        segments[noSegments++] = ls;
    }

    private void arrayResize(int capacity) {
        LineSegment[] newArray = new LineSegment[capacity];
        for (int i = 0; i < segments.length; i++) {
            newArray[i] = segments[i];
        }
        segments = newArray;
    }

    public static void main(String[] args) {

        In in = new In("collinear/rs1423.txt");
        int n = in.readInt();
        Point[] points = new Point[n];
        for (int i = 0; i < n; i++) {
            int x = in.readInt();
            int y = in.readInt();
            points[i] = new Point(x, y);
        }
        StdDraw.enableDoubleBuffering();
        StdDraw.setPenColor(Color.RED);
        StdDraw.setXscale(0, 32768);
        StdDraw.setYscale(0, 32768);
        for (Point p : points) {
            p.draw();
        }
        StdDraw.show();
        BruteCollinearPoints collinear = new BruteCollinearPoints(points);
        for (LineSegment segment : collinear.segments()) {
            StdOut.println(segment);
            segment.draw();
        }
        StdDraw.show();
        // StdOut.printf("\nNumber of found 4-point segments ---> %d\n<<<---------END--------->>>\n", collinear.numberOfSegments());
    }

}
