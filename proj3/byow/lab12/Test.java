package byow.lab12;
import edu.princeton.cs.introcs.StdDraw;


public class Test {
    public static void main(String[] args) {
        StdDraw.setPenRadius(0.05);
        StdDraw.setPenColor(StdDraw.BLUE);
        StdDraw.point(0.5, 0.5);
        StdDraw.setPenColor(StdDraw.MAGENTA);
        StdDraw.line(0.35, 0.6, 0.35, 0.8);
        StdDraw.line(0.65, 0.6, 0.65, 0.8);
        StdDraw.line(0.25, 0.7, 0.45, 0.7);
        StdDraw.line(0.55, 0.7, 0.75, 0.7);
        StdDraw.line(0.2, 0.2, 0.8, 0.2);
    }
}
