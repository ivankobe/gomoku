package inteligenca;

import static util.Util.getMask;
import static util.Util.shl;

import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import logika.Igra;
import logika.Igra.Player;

public class Evaluator {
    /**
     * A class that provides static evaluation of a given position
     */

    // MARK: - static fields (statically pre-computed bitmasks)

    static class pairDirectionLength {
        /**
         * A custom class whose instances are intended to be used as keys in a HashMap
         * in which BitMask objects are stored.
         * 
         * @param direction
         * @param length
         */

        private int direction;
        private int length;

        public pairDirectionLength(int length, int direction) {
            this.direction = direction;
            this.length = length;
        }

        public int direction() {
            return this.direction;
        }

        public int length() {
            return this.length;
        }

        /**
         * Of course, the hashCode() and equals() methods must be overriden.
         */
        @Override
        public int hashCode() {
            int hash = this.length;
            hash = this.direction * 1000 + length;
            return hash;
        }

        @Override
        public boolean equals(Object o) {
            if (o == this)
                return true;
            if (!(o instanceof pairDirectionLength))
                return false;
            pairDirectionLength pair = (pairDirectionLength) o;
            return this.length == pair.length && this.direction == pair.direction;
        }
    }

    /**
     * Masks are stored in a map and indexed by pairs (direction, length)
     */
    private static final Map<pairDirectionLength, BitSet> masks;

    /**
     * Inizialize @masks
     */
    static {
        masks = new HashMap<pairDirectionLength, BitSet>();
        int[] inc = { 1, 14, 15, 16 }; // Search-directions
        int[] lengths = { 2, 5, 6 }; // Lengths of patterns
        for (int i : inc) {
            for (int len : lengths) {
                pairDirectionLength pair = new pairDirectionLength(len, i);
                masks.put(pair, getMask(pair.direction(), pair.length()));
            }
        }
    }

    /**
     * Array of increments representing the four search-directions
     */
    private static final int[] inc = { 1, 14, 15, 16 };

    /**
     * Scores for patterns, as well as for terminal positions, are stored in a
     * hashmap @scores. The values are purely speculative.
     */
    private static final Map<String, Integer> scores;

    /**
     * Initialize @scores
     */
    static {
        scores = new HashMap<String, Integer>();
        scores.put("live four", 10000);
        scores.put("open three", 5000);
        scores.put("dead four", 1000);
        scores.put("broken three", 1000);
        scores.put("closed three", 200);
        scores.put("two", 10);
    }

    // MARK: - dynamic fields

    /**
     * The Game whose position the Evaluator is to evaluate
     */
    private Igra game;

    /**
     * Many different patterns contain the same subpatterns (namely the threes and
     * the fours). That is why, during the evaluation phase, we store the bitboards
     * representing these patterns.
     */
    private Map<Integer, BitSet> cachedThrees;
    private Map<Integer, BitSet> cachedFours;

    /**
     * Methods for setting, retrieving and clearing cache, as well as for enquiring
     * whether there is any cache in the moment
     */
    private void setCachedThrees(int direction, BitSet threes) {
        // Clone when setting
        this.cachedThrees.put(direction, (BitSet) threes.clone());
    }

    private void setCachedFours(int direction, BitSet fours) {
        // Clone when setting
        this.cachedFours.put(direction, (BitSet) fours.clone());
    }

    private boolean haveCachedThrees(int direction) {
        return !(this.cachedThrees.get(direction) == null);
    }

    private boolean haveCachedFours(int direction) {
        return !(this.cachedFours.get(direction) == null);
    }

    private BitSet getCachedThrees(int direction) {
        // But clone when getting
        return (BitSet) this.cachedThrees.get(direction).clone();
    }

    private BitSet getCachedFours(int direction) {
        // But clone when getting
        return (BitSet) this.cachedFours.get(direction).clone();
    }

    private void clearCachedThrees() {
        this.cachedThrees = new HashMap<Integer, BitSet>();
        for (int i : inc) {
            this.cachedThrees.put(i, null);
        }
    }

    private void clearCachedFours() {
        this.cachedFours = new HashMap<Integer, BitSet>();
        for (int i : inc) {
            this.cachedFours.put(i, null);
        }
    }

    // MARK: - constructor

    public Evaluator(Igra game) {
        this.game = game;
        this.cachedThrees = new HashMap<Integer, BitSet>();
        this.cachedFours = new HashMap<Integer, BitSet>();
        for (int i : inc) {
            this.cachedThrees.put(i, null);
            this.cachedFours.put(i, null);
        }
    }

    // MARK: - pattern-searching

    /**
     * A live four is a six-field pattern, where the four bits in the center are set
     * and the outer two are empty: _ X X X X _
     * 
     * @param player
     * @return
     */
    private int numLiveFours(Player player) {
        int num = 0;
        for (int i : inc) {
            // Choose the appropriate bitboard
            BitSet stones = this.game.getBoard(player);
            BitSet fours;
            // If there is cache, retrieve it
            if (haveCachedFours(i)) {
                fours = getCachedFours(i);
            } else {
                // Shift left three times
                BitSet shifted1 = shl(stones, i);
                BitSet shifted2 = shl(shifted1, i);
                BitSet shifted3 = shl(shifted2, i);
                // AND everything togeather
                stones.and(shifted1);
                stones.and(shifted2);
                stones.and(shifted3);
                fours = stones;
                setCachedFours(i, fours); // Set cache
            }
            // Check for space on the left
            stones = shl(stones, i);
            BitSet empties = this.game.getEmpties();
            stones.and(empties);
            // Check for space on the right
            empties = shl(empties, i * 5);
            stones.and(empties);
            // Apply mask
            BitSet mask = masks.get(new pairDirectionLength(6, i));
            stones.and(mask);
            num = num + stones.cardinality();
        }
        return num;
    }

    /**
     * A dead four is a 5-stone pattern, where 4 consecutive bits are set and the
     * remaining bit is clear:
     * 
     * _ X X X X or X X X X _
     * 
     * It is important to note that a live four is also a double dead four. This
     * way, the search is computationally less demanding. This has to be taken into
     * account when defining weights for patterns.
     * 
     * @param player
     * @return
     */
    private int numDeadFours(Player player) {
        int num = 0;
        for (int i : inc) {
            // Choose the appropriate bitboard
            BitSet stones = this.game.getBoard(player);
            BitSet fours;
            // If there is cache, retrieve it
            if (haveCachedFours(i)) {
                fours = getCachedFours(i);
            } else {
                // Shift left three times
                BitSet shifted1 = shl(stones, i);
                BitSet shifted2 = shl(shifted1, i);
                BitSet shifted3 = shl(shifted2, i);
                // AND everything togeather
                stones.and(shifted1);
                stones.and(shifted2);
                stones.and(shifted3);
                fours = stones;
                setCachedFours(i, fours); // Set cache
            }
            // First, count fours with space on the left
            BitSet foursClone = (BitSet) fours.clone(); // We'll need another copy
            BitSet empties = this.game.getEmpties();
            fours = shl(fours, i);
            fours.and(empties);
            BitSet mask = masks.get(new pairDirectionLength(5, i));
            fours.and(mask);
            num = num + fours.cardinality();
            empties = shl(empties, i * 4);
            foursClone.and(empties);
            foursClone.and(mask);
            num = num + foursClone.cardinality();
        }
        return num;
    }

    /**
     * A wide-open three is a three that is one move away from becoming a live four.
     * It is a 6-stone pattern:
     * 
     * _ X X X _ _ or _ _ X X X _
     * 
     * @param player
     * @return
     */
    private int numOpenThrees(Player player) {
        int num = 0;
        for (int i : inc) {
            // Choose the appropriate bitboard
            BitSet stones = this.game.getBoard(player);
            BitSet threes;
            // If there is cache, retrieve it
            if (haveCachedThrees(i)) {
                threes = getCachedThrees(i);
            } else {
                // Shift left twice times
                BitSet shifted1 = shl(stones, i);
                BitSet shifted2 = shl(shifted1, i);
                // AND everything togeather
                stones.and(shifted1);
                stones.and(shifted2);
                threes = stones;
                setCachedThrees(i, threes); // Set cache
            }
            BitSet threesClone = (BitSet) threes.clone();
            BitSet empties = this.game.getEmpties();
            BitSet emptiesClone = this.game.getEmpties();
            threes = shl(threes, i);
            threes.and(empties);
            threes = shl(threes, i);
            threes.and(empties);
            empties = shl(empties, 5 * i);
            threes.and(empties);
            BitSet mask = masks.get(new pairDirectionLength(6, i));
            threes.and(mask);
            num = num + threes.cardinality();
            threesClone = shl(threesClone, i);
            threesClone.and(emptiesClone);
            emptiesClone = shl(emptiesClone, 4 * i);
            threesClone.and(emptiesClone);
            emptiesClone = shl(emptiesClone, i);
            threesClone.and(emptiesClone);
            threesClone.and(mask);
            num = num + threesClone.cardinality();
        }
        return num;
    }

    /**
     * A closed three is a three which can become five but isn't a forcing move:
     * 
     * 1) X X X _ _ 2) _ X X X _ 3) _ _ X X X
     * 
     * Again, it may happen, that another pattern is also counted as a closed three.
     * This must be taken into account when defining weights, probabbly by making
     * differences between higher patterns and lower patterns smaller.
     * 
     * @param player
     * @return
     */
    private int numClosedThrees(Player player) {
        int num = 0;
        for (int i : inc) {
            // Choose the appropriate bitboard
            BitSet stones = this.game.getBoard(player);
            BitSet threes;
            // If there is cache, retrieve it
            if (haveCachedThrees(i)) {
                threes = getCachedThrees(i);
            } else {
                // Shift left twice times
                BitSet shifted1 = shl(stones, i);
                BitSet shifted2 = shl(shifted1, i);
                // AND everything togeather
                stones.and(shifted1);
                stones.and(shifted2);
                threes = stones;
                setCachedThrees(i, threes); // Set cache
            }
            BitSet threesC = (BitSet) threes.clone();
            BitSet threesCC = (BitSet) threes.clone();
            BitSet empties = this.game.getEmpties();
            BitSet emptiesC = this.game.getEmpties();
            /**
             * search for 1st pattern
             */
            threes = shl(threes, i);
            threes.and(empties);
            threes = shl(threes, i);
            threes.and(empties);
            BitSet mask = masks.get(new pairDirectionLength(5, i));
            threes.and(mask);
            num = num + threes.cardinality();
            /**
             * search for 2nd pattern
             */
            threesC = shl(threesC, i);
            threesC.and(empties);
            empties = shl(empties, 4 * i);
            threesC.and(empties);
            threesC.and(mask);
            num = num + threesC.cardinality();
            /**
             * search for 3rd pattern
             */
            emptiesC = shl(emptiesC, 3 * i);
            threesCC.and(emptiesC);
            emptiesC = shl(emptiesC, i);
            threesCC.and(emptiesC);
            threesCC.and(mask);
            num = num + threesCC.cardinality();
        }
        return num;
    }

    /**
     * A broken three is a six-stone pattern. Three nonconsecutive of the center
     * four are set and the other three are clear. A broken three is as valuable as
     * an open three.
     * 
     * 1) _ X X _ X _ 2) _ X _ _ X _
     * 
     * @param player
     * @return
     */
    private int numBrokenThrees(Player player) {
        int num = 0;
        for (int i : inc) {
            BitSet stones = this.game.getBoard(player);
            BitSet stonesC = this.game.getBoard(player);
            BitSet empties = this.game.getEmpties();
            BitSet emptiesC = this.game.getEmpties();
            /**
             * search for 1st pattern
             */
            BitSet shifted1 = shl(stones, 2 * i);
            BitSet shifted2 = shl(shifted1, i);
            stones.and(shifted1);
            stones.and(shifted2);
            stones = shl(stones, i);
            stones.and(empties);
            empties = shl(empties, 2 * i);
            stones.and(empties);
            empties = shl(empties, 3 * i);
            stones.and(empties);
            BitSet mask = masks.get(new pairDirectionLength(6, i));
            stones.and(mask);
            num = num + stones.cardinality();
            /**
             * search for 2nd pattern
             */
            BitSet shifted1C = shl(stonesC, i);
            BitSet shifted2C = shl(shifted1C, 2 * i);
            stonesC.and(shifted1C);
            stonesC.and(shifted2C);
            stonesC = shl(stonesC, i);
            stonesC.and(emptiesC);
            emptiesC = shl(emptiesC, 3 * i);
            stonesC.and(emptiesC);
            emptiesC = shl(emptiesC, 2 * i);
            stonesC.and(emptiesC);
            stonesC.and(mask);
            num = num + stonesC.cardinality();
        }
        return num;
    }

    /**
     * Number of consecutive twos (with repetitions). To be asigned a very small
     * number.
     * 
     * @param player
     * @return
     */
    private int numTwos(Player player) {
        int num = 0;
        for (int i : inc) {
            BitSet stones = this.game.getBoard(player);
            stones.and(shl(stones, i));
            BitSet mask = masks.get(new pairDirectionLength(2, i));
            stones.and(mask);
            num = num + stones.cardinality();
        }
        return num;
    }

    // MARK: - board evaliaton

    /**
     * @return Evaluates the bord for a single player and resets cache.
     */
    private int evaluateSingleSidedNonterminal(Player player) {
        int eval = 0;
        eval += this.numLiveFours(player) * scores.get("live four");
        eval += this.numDeadFours(player) * scores.get("dead four");
        eval += this.numOpenThrees(player) * scores.get("open three");
        eval += this.numClosedThrees(player) * scores.get("closed three");
        eval += this.numBrokenThrees(player) * scores.get("broken three");
        eval += this.numTwos(player) * scores.get("two");
        // Clear cache
        this.clearCachedFours();
        this.clearCachedThrees();
        return eval;
    }

    /**
     * @return The holistic evaluation of the board. If the return value is
     * positive, the position is favorable for the @player and vice versa.
     */
    public int evaluate(Player player) {
        int plus = evaluateSingleSidedNonterminal(player);
        int minus = evaluateSingleSidedNonterminal(player.next());
        return plus - minus;
    }

}
