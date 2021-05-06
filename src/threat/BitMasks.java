package threat;

import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;

import util.Util;

public class BitMasks {

    public Map<Integer, BitSet> horisontal;
    public Map<Integer, BitSet> diagonal;
    public Map<Integer, BitSet> counterdiagonal;

    public BitMasks() {
        horisontal = new HashMap<Integer, BitSet>();
        diagonal = new HashMap<Integer, BitSet>();
        counterdiagonal = new HashMap<Integer, BitSet>();
        int[] len = {5,6,7};
        for (int j : len) {
            BitSet bh = new BitSet(225);
            BitSet bd = new BitSet(225);
            BitSet bcd = new BitSet(225);
            for (int i = 0; i < 225; i++) {
                int row = i / 15;
                int col = i % 15;
                if (col < 16 - j) {bh.set(i);}
                if (row < 16 - j & col > j - 2) {bd.set(i);}
                if (row < 16 - j & col < 16 - j) {bcd.set(i);}
            }
            this.horisontal.put(j, bh);
            this.diagonal.put(j, bd);
            this.counterdiagonal.put(j, bcd);
        }
    }

    public static void main(String[] args) {
        BitMasks m = new BitMasks();
        Util.printBS(m.counterdiagonal.get(5));
    }

}
