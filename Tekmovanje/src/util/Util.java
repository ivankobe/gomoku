package util;

import java.util.BitSet;

public class Util {

    /**
     * Whean searching for patterns, certain positions have to be ignored. Since
     * searching is done by shifting the board left a number of times, bits representing
     * a found pattern will be located at the left-most bit of the initial pattern. But
     * some strings found this way do not actually represent a continuous string of fields
     * because of the line breaks. For example, when searching for 5 consecutive stones in
     * horisontal direction, the positions we are interested in are only these:
     * 
     * 0 0 0 0 0 0 0 0 0 0 0 _ _ _ _ 
     * 0 0 0 0 0 0 0 0 0 0 0 _ _ _ _ 
     * 0 0 0 0 0 0 0 0 0 0 0 _ _ _ _ 
     * 0 0 0 0 0 0 0 0 0 0 0 _ _ _ _ 
     * 0 0 0 0 0 0 0 0 0 0 0 _ _ _ _ 
     * 0 0 0 0 0 0 0 0 0 0 0 _ _ _ _ 
     * 0 0 0 0 0 0 0 0 0 0 0 _ _ _ _ 
     * 0 0 0 0 0 0 0 0 0 0 0 _ _ _ _ 
     * 0 0 0 0 0 0 0 0 0 0 0 _ _ _ _ 
     * 0 0 0 0 0 0 0 0 0 0 0 _ _ _ _ 
     * 0 0 0 0 0 0 0 0 0 0 0 _ _ _ _ 
     * 0 0 0 0 0 0 0 0 0 0 0 _ _ _ _ 
     * 0 0 0 0 0 0 0 0 0 0 0 _ _ _ _ 
     * 0 0 0 0 0 0 0 0 0 0 0 _ _ _ _ 
     * 0 0 0 0 0 0 0 0 0 0 0 _ _ _ _ 
     * 
     * @param direction integer value, contained in {1, 14, 15, 16}. It tells us whether we
     * are searching horisontally, vertically, on the diagonal or on the counterdiagonal
     * 
     * @param length the length of the pattern we are searching for
     * 
     * @return a bitmask that filters out irregular positions when combined with the AND operator
     */
    public static BitSet getMask(int direction, int length) {
        BitSet mask = new BitSet(225);
        for (int i = 0; i < 225; i++) {
            int row = i / 15;
            int col = i % 15;
            switch (direction) {
                case 1:
                    if (col < 16 - length) {
                        mask.set(i);
                    }
                    break;
                case 14:
                    if (row < 16 - length & col > length - 2) {
                        mask.set(i);
                    }
                    break;
                case 15:
                    if (row < 16 - length) {
                        mask.set(i);
                    }
                    break;
                case 16: // case 16
                    if (row < 16 - length & col < 16 - length) {
                        mask.set(i);
                    }
                    break;
            }
        }
        return mask;
    }

    /**
     * Shifts the board left a number of times. Left-shift is a logical
     * operator that forgets first n-bits of a bitset. It is important
     * to note that shl does not modify the initial bitset and creates a copy of it.
     * b = {3,6};
     * shl(b,1) ----> b = {2,5} 
     */
    public static BitSet shl(BitSet board, int times) {
        return board.get(times, Math.max(times, board.length()));
    }

    /**
     * prints the bitset @param b
     * Mainly for debugging purpouses
     */
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