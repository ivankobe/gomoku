package util;

import java.util.BitSet;

public class Util {

    public static BitSet shl(BitSet b, int n) {
        return b.get(n, Math.max(n, b.length()));
        
    }

    public static BitSet and(BitSet b, BitSet d) {
        b.and(d);
        return b;
    }

    public static void printBS(BitSet b) {
        for (int i = 0; i < 225; i++) {
            boolean t = b.get(i);
            if (t) {System.out.print("1 ");}
            else {System.out.print("0 ");}
            if (i % 15 == 14) {System.out.println("");}
            }
        System.out.println("");
        }

}
