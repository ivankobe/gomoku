package threat;

import java.util.BitSet;
import java.util.HashSet;
import java.util.Set;

import static util.Util.*;

public class ThreatSearch {

    private static BitMasks masks = new BitMasks(); 
    
    // Finds out whether there are any sequence of positive
    // values of lenght 5 in any direction on the bitboard b.
    // The bitboard e represents empty fields. shl is the logical
    // left shift. It does not modify its argument. b1.and(b2) is 
    // a bitwise operator that does modify the object it is performed on.
    // On the other hand, and(b1, b2) doesn't modify its arguments.
    // The method returns a set of starting (left-most) indexes of five-stone strings.

    public Set<Integer> getFives(BitSet b) {

        // Search horisontally (1), on the diagonal(14) and on the
        // off-diagonal (16). Search will utilise the shift-left
        // operator.
        
        int[] inc = {1,14,16};
        Set<Integer> fives = new HashSet<Integer>();
        for (int i : inc) {

            // Shift left four times
            BitSet tempB = (BitSet) b.clone();
            BitSet shifted1 = shl(tempB, i);
            BitSet shifted2 = shl(shifted1, i);
            BitSet shifted3 = shl(shifted2, i);
            BitSet shifted4 = shl(shifted3, i);
            tempB.and(shifted1); tempB.and(shifted2); tempB.and(shifted3); tempB.and(shifted4);         
        
            // Some positions need to be discarded because of line-breaks.
            BitSet mask;
            if (i == 1) {mask = masks.horisontal.get(5);}
            else if (i == 14) {mask = masks.diagonal.get(5);}
            else {mask = masks.counterdiagonal.get(5);}
            tempB.and(mask);

            // Filter out continuous set strings of length > 5.
            int pos = 0;
            // Iterate throug set bits.
            while (true) {
                pos = tempB.nextSetBit(pos);
                // If there are no set bit left, break.
                if (pos == -1) {
                    break;
                }
                // Check if the set bit is isolated or if is a part of a thread of lenght >= 2.
                // In the first case, add it to the set of 5-strings, in the second case,
                // clear the whole thread.
                if (tempB.get(pos + i)) {
                    int k = 2;
                    tempB.clear(pos + i);
                    while (true) {
                        if (tempB.get(pos + k * i)) {
                            tempB.clear(pos + k * i);
                            k++;
                        }
                        else {
                            break;
                        }
                    }
                }
                else {
                    fives.add(pos);
                }
                pos++;
            }
        }
        return fives;
    }
    

    public static void main(String[] args) {
        BitSet bb = new BitSet(225);
        bb.set(1);bb.set(2);bb.set(3);bb.set(4);bb.set(5);
        printBS(bb);
        ThreatSearch ts = new ThreatSearch();
        System.out.println(ts.getFives(bb));
        // seems to me that it works.
    }

}
