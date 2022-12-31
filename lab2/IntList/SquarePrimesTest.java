package IntList;

import static org.junit.Assert.*;

import jh61b.junit.In;
import org.junit.Test;

import javax.naming.InitialContext;

public class SquarePrimesTest {

    /**
     * Here is a test for isPrime method. Try running it.
     * It passes, but the starter code implementation of isPrime
     * is broken. Write your own JUnit Test to try to uncover the bug!
     */
    @Test
    public void testSquarePrimesSimple() {
        IntList lst = IntList.of(14, 15, 16, 17, 18);
        boolean changed = IntListExercises.squarePrimes(lst);
        assertEquals("14 -> 15 -> 16 -> 289 -> 18", lst.toString());
        assertTrue(changed);
    }
    @Test
    public void testSquearePrimesSimple1(){
        IntList lst = IntList.of(1,2,3,4);
        boolean changed = IntListExercises.squarePrimes(lst);
        assertEquals("1 -> 4 -> 9 -> 4",lst.toString());
        assertTrue(changed);
    }
    @Test
    public void testSquearePrimesSimple2(){
        IntList lst = IntList.of(5,11,13,19,23);
        boolean changed = IntListExercises.squarePrimes(lst);
        assertEquals("25 -> 121 -> 169 -> 361 -> 529", lst.toString());
        assertTrue(changed);
    }
}
