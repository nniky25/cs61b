package timingtest;
import edu.princeton.cs.algs4.Stopwatch;

/**
 * Created by hug.
 */
public class TimeSLList {
    private static void printTimingTable(AList<Integer> Ns, AList<Double> times, AList<Integer> opCounts) {
        System.out.printf("%12s %12s %12s %12s\n", "N", "time (s)", "# ops", "microsec/op");
        System.out.printf("------------------------------------------------------------\n");
        for (int i = 0; i < Ns.size(); i += 1) {
            int N = Ns.get(i);
            double time = times.get(i);
            int opCount = opCounts.get(i);
            double timePerOp = time / opCount * 1e6;
            System.out.printf("%12d %12.2f %12d %12.2f\n", N, time, opCount, timePerOp);
        }
    }

    public static void main(String[] args) {
        timeGetLast();
    }

    public static void timeGetLast() {
        AList<Integer> Ns = new AList<>();
        AList<Integer> ops = new AList<>();
        AList<Double> times = new AList<>();

        int testTimes = 8;
        int callTimes = 10000;

        for (int i = 0; i < testTimes; i++) {
            int size = 1000 * (int) Math.pow(2, i);
            Ns.addLast(size);
            ops.addLast(callTimes);

            // Create and fill the SLList
            SLList<Integer> list = new SLList<>();
            for (int j = 0; j < size; j++) {
                list.addLast(1);
            }

            // Time getLast() calls
            Stopwatch sw = new Stopwatch();
            for (int j = 0; j < callTimes; j++) {
                list.getLast();
            }
            double timeInSeconds = sw.elapsedTime();
            times.addLast(timeInSeconds);
        }

        printTimingTable(Ns, times, ops);
    }
}
