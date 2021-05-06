package logika;

import java.util.BitSet;
import java.util.Set;

import splosno.Koordinati;

import threat.ThreatSearch;

public class Igra {
    
    public BitSet bb;
    public BitSet bw;
    public BitSet be;

    private ThreatSearch ts;

    public Player toPlay;

    public Igra() {
        // The board is represented as a triplet of bitsets in order to make pattern-recognition
        // faster. bb is a bitset representing  black stones, i.e. bb.get(n) == true iff the n-th
        // square on the board is populated with a black stone. bw symmetric. be is the complement
        // of (bb and bw). 0 is the top-left corner, 14 is top-right, 210 bottom-left and 224 top-right. 
        bb = new BitSet(225);
        bw = new BitSet(225);
        be = new BitSet(225);
        be.set(0, 225); // bb and bw are initially empty, be is initially full.
        toPlay = Player.X;
    }

    // It will sometimes be useful to have a matrix-representation of the board.
    public Field[][] getMatrix() {
        Field[][] matrix = new Field[15][15];
        for (int i = 0; i < 225; i++) {
            int row = i / 15;
            int col = i % 15;
            if (bb.get(i)) {
                matrix[row][col] = Field.X;
            }
            else if (bw.get(i)) {
                matrix[row][col] = Field.O;
            }
            else {
                matrix[row][col] = Field.EMPTY;
            }
        }
        return matrix;
    }


    // We can check whether a move is valid  with its index.
    private boolean moveIsValid(int n) {
        return bb.get(n) & bw.get(n);
    }

    // Get a set of all valid moves, i.e. a bitset b where
    // b.get(n) == true iff moveIsValid(n). 
    public BitSet validMoves() {
        BitSet b = new BitSet(225);
        for (int i = 0; i < 225; i++) {
            if (moveIsValid(i)) {b.set(i);}
        }
        return  b;
    }

    //If it is possible to place a stone on a given field, this method does so
    //and return true. If it is not possible, it returns false. The argument
    //can be either a bitboard index...
    public boolean odigraj(int n) {
        if (moveIsValid(n)) {
            BitSet b = toPlay == Player.X ? bb : bw;
            b.set(n);
            be.clear(n);
            toPlay = toPlay.nasprotnik();
            return true;
        }
        else {return false;}
    }

    //... or a corresponding Koordinati object.
    public boolean odigraj(Koordinati k) {
        int n = k.getX() * 15 + k.getY();
        return odigraj(n);
    }


    public GameState winner() {
        Set<Integer> fivesB = ts.getFives(bb);
        Set<Integer> fivesW = ts.getFives(bw);
        if (!fivesB.isEmpty()) {
            return GameState.WIN_X;
        }
        else if (!fivesW.isEmpty()) {
            return GameState.WIN_O;
        }
        else if (!fivesB.isEmpty() & !fivesW.isEmpty()) {
            throw new Exception("OOps!")
        }
        else if (be.isEmpty()) {
            return GameState.DRAW;
        }
        else {
            return GameState.IN_PROGRESS;
        } 
    }


    public static void main(String[] args) {
        Igra i = new Igra();
        i.winner();
    }
}
