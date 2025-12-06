package randomizedtest;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by hug.
 */
public class TestBuggyAList {
    // YOUR TESTS HERE
    @Test
    public void testThreeAddThreeRemove() {
        AListNoResizing<Integer> correct = new AListNoResizing<>();
        BuggyAList<Integer> buggy = new BuggyAList<>();
        correct.addLast(12);
        correct.addLast(24);
        correct.addLast(36);
        buggy.addLast(12);
        buggy.addLast(24);
        buggy.addLast(36);
        assertEquals(correct.size(), buggy.size());
        assertEquals(correct.removeLast(), buggy.removeLast());
        assertEquals(correct.removeLast(), buggy.removeLast());
        assertEquals(correct.removeLast(), buggy.removeLast());
    }

    @Test
    public void randomizedTest() {
        AListNoResizing<Integer> correct = new AListNoResizing<>();
        BuggyAList<Integer> buggy = new BuggyAList<>();
        int N = 500;
        for (int i = 0; i < N; i++) {
            int operationNumber = StdRandom.uniform(0, 4);
            if (operationNumber == 0) {
                // addLast the same value to both lists
                int randVal = StdRandom.uniform(0, 100);
                correct.addLast(randVal);
                buggy.addLast(randVal);
            } else if (operationNumber == 1) {
                // assert if size are equal
                assertEquals(correct.size(), buggy.size());
            } else if (operationNumber == 2) {
                // assert if getLast is equal
                if (correct.size() > 0 && buggy.size() > 0){
                    assertEquals(correct.getLast(), buggy.getLast());
                }
            } else if (operationNumber == 3) {
                // assert if removeLast is equal
                if (correct.size() > 0 && buggy.size() > 0) {
                    assertEquals(correct.removeLast(), buggy.removeLast());
                }
            }
        }
    }
}
