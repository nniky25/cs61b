package timingtest;
import edu.princeton.cs.algs4.Stopwatch;

/**
 * Created by hug.
 */
public class TimeAList {
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
        timeAListConstruction();
    }

    public static void timeAListConstruction() {
        // TODO: YOUR CODE HERE
        // finish N and # ops
        AList<AList> arrayAList = new AList<>();
        AList<Integer> N = new AList<>();
        int firstSize = 1000;
        int testTimes = 8;
        int generalValue = 1;
        for (int i = 0; i < testTimes; i++) {
            // (create 8 ALists for test)
            AList<Integer> innerList = new AList<>();
            arrayAList.addLast(innerList);
            N.addLast(firstSize);
            firstSize = firstSize * 2;
            //System.out.println(N.get(i));
        }
        AList<Integer> ops = N;

        //finish time
        AList<Double> time = new AList<>();
        for (int i = 0; i < testTimes; i++) {
            AList<Integer> current = arrayAList.get(i);
            Stopwatch sw = new Stopwatch();
            for (int j = 0; j < N.get(i); j++) {
                current.addLast(generalValue);
            }
            double timeInSeconds = sw.elapsedTime();
            time.addLast(timeInSeconds);
        }

        //put them to printTimingTable()
        printTimingTable(N, time, ops);
    }
}
