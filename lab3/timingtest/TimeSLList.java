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
        int N = 1000;
        int count = 0;
        AList<Integer> Ns = new AList();
        AList<Double> times = new AList<>();
        AList<Integer> opCounts = new AList<>();
        while(count < 8){
            SLList<Integer> lst = new SLList<>();
            for(int i = 0; i < N; i++){
                lst.addLast(1);
            }
            Stopwatch sw = new Stopwatch();
            for(int i = 0; i < 10000; i++) {
                lst.getLast();
            }
            double time = sw.elapsedTime();
            Ns.addLast(N);
            times.addLast(time);
            opCounts.addLast(10000);
            N *= 2;
            count += 1;
        }
        printTimingTable(Ns,times,opCounts);
    }
}
