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
        // TODO: YOUR CODE HERE
        // finish N and # ops
        AList<SLList> arraySLList = new AList<>();
        AList<Integer> N = new AList<>();
        AList<Integer> ops = new AList<>();
        int firstSize = 1000;
        int callTimes = 10000;
        int testTimes = 8;
        int generalValue = 1;
        for (int i = 0; i < testTimes; i++) {
            // (create 8 ALists for test)
            SLList<Integer> innerList = new SLList<>();
            arraySLList.addLast(innerList);
            N.addLast(firstSize);
            ops.addLast(callTimes);
            firstSize = firstSize * 2;
        }

        // fill arraySLList
        AList<Double> time = new AList<>();
        for (int i = 0; i < testTimes; i++) {
            SLList<Integer> current = arraySLList.get(i);
            for (int j = 0; j < N.get(i); j++) {
                current.addLast(generalValue);
            }
        }

        // test getLast
        for (int i = 0; i < testTimes; i++) {
            SLList<Integer> testList = arraySLList.get(i);
            Stopwatch sw = new Stopwatch();
            for (int j = 0; j < callTimes; j++) {
                int value = testList.getLast();
            }
            double timeInSeconds = sw.elapsedTime();
            time.addLast(timeInSeconds);
        }

        // put them to printTimingTable()
        printTimingTable(N, time, ops);
    }
}
