package randomizedtest;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;
import static org.junit.Assert.*;

public class testThreeAddThreeRemove {
    @Test
    public void test(){
        AListNoResizing<Integer> noResizing = new AListNoResizing<>();
        BuggyAList<Integer> buggyAList = new BuggyAList<>();
        int N = 500;
        for(int i = 0; i < 5000; i++){
            int a = StdRandom.uniform(0,4);
            if(a == 0){
                int b = StdRandom.uniform(0,100);
                noResizing.addLast(b);
                buggyAList.addLast(b);
                //System.out.println("addLast:"+b);
                assertEquals(noResizing.size(),buggyAList.size());
            }
            else if(a == 1){
                //System.out.println("size:"+noResizing.size());
            } else if (a == 2 && noResizing.size()>0) {
                //System.out.println("getLast:"+noResizing.getLast());
            } else if (a == 3 && noResizing.size()>0) {
                noResizing.removeLast();
                buggyAList.removeLast();
                //System.out.println("removeLast");
                assertEquals(noResizing.size(),buggyAList.size());
            }
        }
    }
}
