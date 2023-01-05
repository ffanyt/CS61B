package gh2;
import deque.Deque;
import deque.LinkedListDeque;

public class GuitarString {
    private static final int SR = 44100;      // Sampling Rate
    private static final double DECAY = .996; // energy decay factor

    /* Buffer for storing sound data. */
    private Deque<Double> buffer;

    /* Create a guitar string of the given frequency.  */
    public GuitarString(double frequency) {
        buffer = new LinkedListDeque<Double>();
        int len = (int) Math.round(SR / frequency);
        for (int i = 0; i < len; i++) {
            buffer.addLast(0.0);
        }
    }


    /* Pluck the guitar string by replacing the buffer with white noise. */
    public void pluck() {
        int len = buffer.size();
        for (int i = 0; i < len; i++) {
            double rand = 0;
            boolean flag = true;
            buffer.removeFirst();
            while (flag) {
                flag = false;
                rand = Math.random() - 0.5;
                for (int j = 0; j < len - 1; j++) {
                    if (rand == buffer.get(j)) {
                        flag = true;
                        break;
                    }
                }
            }
            buffer.addLast(rand);
        }
    }

    /* Advance the simulation one time step by performing one iteration of
     * the Karplus-Strong algorithm.
     */
    public void tic() {
        double first = buffer.removeFirst();
        double second = buffer.get(0);
        double newDouble = DECAY * (first + second) * 0.5;
        buffer.addLast(newDouble);
    }

    /* Return the double at the front of the buffer. */
    public double sample() {
        return buffer.get(0);
    }
}
