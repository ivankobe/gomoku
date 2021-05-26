package inteligenca;

import static util.Util.getMask;
import static util.Util.shl;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.SwingWorker;

import controller.ITurnController;
import controller.IPlayer;
import logika.Igra;
import logika.Igra.GameState;
import logika.Igra.Player;
import splosno.KdoIgra;
import splosno.Koordinati;

/**
 * This file contains a class that plays by itself the Gomoku game.
 */

public class Inteligenca extends KdoIgra implements IPlayer {

	// MARK: - Static

	/**
	 * Computer player.
	 */

	/**
	 * Since evaluation of non-terminal and terminal positions should be independent
	 * of each other, the latter are stored seperately.
	 */
	private static final Map<String, Integer> scores;

	/**
	 * The scores for win and lose are symmetric. The score for draw is 0.
	 */
	static {
		scores = new HashMap<String, Integer>();
		scores.put("win", 1000000);
		scores.put("lose", -1000000);
		scores.put("draw", 0);
	}

	/**
	 * A transposition table to store static evaluations of already seen positions
	 * in. This way we prevent evaluating the same position (at which one can arrive
	 * with different sequences of moves) more than once. We need two transposition
	 * tables, once for when computer is playing black and one for when it is
	 * playing white. In a single game, a single table would suffice, since scores
	 * are always calculated for the same player, however, the same Bot object is
	 * meant to be used in several games (in order to accumulate as big
	 * transposition tables as possible).
	 */
	private Map<Long, Integer> transpositionTableBlack;
	private Map<Long, Integer> transpositionTableWhite;

	// MARK: - State

	private Color color;

	// MARK: - Contructor

	public Inteligenca(String ime, Color color) {
		super(ime);

		this.color = color;
		this.transpositionTableBlack = new HashMap<Long, Integer>();
		this.transpositionTableWhite = new HashMap<Long, Integer>();
	}

	// MARK: - Accessors

	/**
	 * Returns the name of the player.
	 */
	public String name() {
		return this.ime;
	}

	/**
	 * Returns the color of the stones.
	 */
	public Color color() {
		return this.color;
	}

	// MARK: - Methods

	/**
	 * Makes a move.
	 * 
	 * @param igra
	 * @return
	 */
	Koordinati izberiPotezo(Igra igra) {
		int n = this.calculate(igra);

		int x = n % igra.size();
		int y = n / igra.size();

		return new Koordinati(x, y);
	}

	/**
	 * Takes control of the game.
	 */
	@Override
	public void take(ITurnController controller) {
		// Save the initial state of the game.
		Igra game = controller.game();

		SwingWorker<Integer, Void> worker = new SwingWorker<Integer, Void>() {
			@Override
			protected Integer doInBackground() {
				int n = calculate(controller.game());

				return n;
			}

			@Override
			protected void done() {
				Integer n = null;

				try {
					n = get();
				} catch (Exception e) {
				}

				if (controller.game() != game)
					return;

				controller.setActive(n);
				controller.confirm();

			}
		};

		// Start executing.
		worker.execute();
	}

	/**
	 * Releases the control of the game.
	 */
	public void release() {

	}

	// MARK: - Minimax

	/**
	 * Chooses the best move it can find using iterative deepening search.
	 */
	public int calculate(Igra game) {
		long startTime = System.currentTimeMillis();
		// Check that the gamestate is not terminal
		if (game.state() != GameState.IN_PROGRESS) {
			throw new IllegalArgumentException("Position is terminal. I cannot choose a move!");
		}
		// If this is the first move, play at the center.
		else if (game.getEmpties().cardinality() == 225) {
			Random rand = new Random();
			int[] center = { 6 + 6 * 15, 7 + 6 * 15, 8 + 6 * 15, 6 + 7 * 15, 7 + 7 * 15, 8 + 7 * 15, 6 + 8 * 15,
					7 + 8 * 15, 8 + 8 * 15, };
			return center[rand.nextInt(center.length)];
		}
		// If this is the second move, also play at the center.
		else if (game.getEmpties().cardinality() == 224) {
			Random rand = new Random();
			int[] center = { 6 + 6 * 15, 7 + 6 * 15, 8 + 6 * 15, 6 + 7 * 15, 7 + 7 * 15, 8 + 7 * 15, 6 + 8 * 15,
					7 + 8 * 15, 8 + 8 * 15, };
			while (true) {
				int choice = center[rand.nextInt(center.length)];
				if (game.getEmpties().get(choice))
					return choice;
			}
		}
		// Else, apply the minimax algorithm the the game.
		else {
			EvaluatedMove bestMove = minimaxAB(game, 1, 1, -Integer.MAX_VALUE, Integer.MAX_VALUE, game.player(), 5000);
			for (int i = 2; i <= 4; i++) {
				long timeElapsed = System.currentTimeMillis() - startTime;
				EvaluatedMove deeper = minimaxAB(game, i, i, -Integer.MAX_VALUE, Integer.MAX_VALUE, game.player(), 5000 - timeElapsed);
				if (deeper == null) {
					break;
				}
				else {
					bestMove = deeper;
				}
			}
			return bestMove.move();
		}
	}

	/**
	 * Retrieve the appropriate transposition table
	 * 
	 * @param player
	 * @return
	 */
	private Map<Long, Integer> getTranspositionTable(Player player) {
		return (player == Player.Black) ? this.transpositionTableBlack : this.transpositionTableWhite;
	}

	/**
	 * A custom class describing evaluated moves, basically a pair (move,
	 * evaluation)
	 */
	class EvaluatedMove {

		private int move;
		private int eval;

		public EvaluatedMove(int move, int eval) {
			this.move = move;
			this.eval = eval;
		}

		public int move() {
			return this.move;
		}

		public int eval() {
			return this.eval;
		}
	}

	/**
	 * Minimax with alpha-beta pruning to cut off unreachable branches. At each
	 * step, candidates for next move are first evaluated and sorted. This should
	 * speed up the algorithm significantly.
	 * 
	 * @param game
	 * @param depth  It is a depth-bounded algorithm
	 * @param alpha  To be initialized as negative infinity
	 * @param beta   To be initialized as positive infinity
	 * @param player Whether it is a minimizing or a maximizing branch. To be
	 *               initialized as game.toplay()
	 * @return
	 */
	private EvaluatedMove minimaxAB(Igra game, int depth, int maxDepth, Integer alpha, Integer beta, Player player, long timeLeft) {
		long startTime = System.currentTimeMillis();
		// Retrieve the appropriate transposition table
		Map<Long, Integer> transpositionTable = this.getTranspositionTable(player);
		// If @player is the maximizer, the starting maxEval is negative "infinity",
		// and if @player is the minimizer, the starting maxEval is "negative infinity".
		Integer maxEval = (game.player() == player) ? -Integer.MAX_VALUE : Integer.MAX_VALUE;
		// Search only moves that are not too far from stones that are already on the
		// board
		Set<Integer> candidates = game.candidates();
		// A map for storing the evaluations of child nodes
		Map<Integer, Integer> evaluations = new HashMap<Integer, Integer>();
		// Since, for evaluation of a child node, the game must be copied,
		// the copied games are stored in a map so as to avoid having to
		// copy each game twice
		Map<Integer, Igra> clonedGames = new HashMap<Integer, Igra>();
		// Evaluate each candidate
		for (int move : candidates) {
			if (depth == maxDepth && System.currentTimeMillis() - startTime > timeLeft) {
				return null;
			}
			// Copy the game
			Igra gameCopy = new Igra(game);
			// Apply move
			gameCopy.play(move);
			// If this board was already evaluated, retrieve evaluation from table
			Integer staticEvaluation = transpositionTable.get(gameCopy.hash());
			// If not, calculate static evaluation and store it in the table for future
			// reference
			if (staticEvaluation == null) {
				GameState state = gameCopy.state();
				if ((state == GameState.WIN_Black && player == Player.Black)
						|| (state == GameState.WIN_White && player == Player.White)) {
					staticEvaluation = scores.get("win");
				} else if ((state == GameState.WIN_Black && player == Player.White)
						|| (state == GameState.WIN_White && player == Player.Black)) {
					staticEvaluation = -scores.get("win");
				} else if (state == GameState.DRAW) {
					staticEvaluation = scores.get("draw");
				} else {
					Evaluator evaluator = new Evaluator(gameCopy);
					staticEvaluation = evaluator.evaluate(player);
				}
				transpositionTable.put(gameCopy.hash(), staticEvaluation);
			}
			// Store the cloned game
			clonedGames.put(move, gameCopy);
			// Store the evaluation of child node
			evaluations.put(move, staticEvaluation);
		}
		// Transform the set of candidates into a list (for sorting)
		List<Integer> sorted = new ArrayList<Integer>(candidates);
		// Sort the list
		if (game.player() == player) {
			sorted.sort((x, y) -> evaluations.get(y).compareTo(evaluations.get(x))); // Yay, lambda expressions!
		}
		else {
			sorted.sort((x, y) -> evaluations.get(x).compareTo(evaluations.get(y))); // Yay, lambda expressions!
		}
		if (depth != maxDepth) {
			sorted = sorted.stream().limit(8).collect(Collectors.toList());
		}
		int bestMove = sorted.get(0); // Start with the best candidate
		for (int move : sorted) {
			if (depth == maxDepth && System.currentTimeMillis() - startTime > timeLeft) {
				return null;
			}
			// Retrieve the cloned game
			Igra clone = clonedGames.get(move);
			Integer eval;
			// If position is terminal or maximum depth was reached, retrieve evaluation
			// from table. We can be sure that eval != null.
			if (!(clone.state() == GameState.IN_PROGRESS) || depth == 0) {
				eval = transpositionTable.get(clone.hash());
			}
			// Else, make a recursive call
			else {
				eval = minimaxAB(clone, depth - 1, maxDepth, alpha, beta, player, timeLeft - (System.currentTimeMillis() - startTime)).eval();
			}
			// Maximizer
			if (game.player() == player) {
				if (eval > maxEval) {
					maxEval = eval;
					bestMove = move;
					alpha = Math.max(alpha, maxEval);
				}
			}
			// Minimizer
			else {
				if (eval < maxEval) {
					maxEval = eval;
					bestMove = move;
					beta = Math.min(beta, maxEval);
				}
			}
			// If position is unreachable, terminate loop and return
			if (alpha >= beta)
				return new EvaluatedMove(bestMove, maxEval);

		}
		return new EvaluatedMove(bestMove, maxEval);
	}

	// MARK: - Utility functions

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mouseDragged(MouseEvent e) {
	}

	@Override
	public void mouseMoved(MouseEvent e) {
	}
}

// MARK: - Evaluator

class Evaluator {
	/**
	 * A class that provides static evaluation of a given position
	 */

	// MARK: - static fields (statically pre-computed bitmasks)

	static class PairDirectionLength {
		/**
		 * A custom class whose instances are intended to be used as keys in a HashMap
		 * in which BitMask objects are stored.
		 * 
		 * @param direction
		 * @param length
		 */

		private int direction;
		private int length;

		public PairDirectionLength(int length, int direction) {
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
			if (!(o instanceof PairDirectionLength))
				return false;
			PairDirectionLength pair = (PairDirectionLength) o;
			return this.length == pair.length && this.direction == pair.direction;
		}
	}

	/**
	 * Masks are stored in a map and indexed by pairs (direction, length)
	 */
	private static final Map<PairDirectionLength, BitSet> masks;

	/**
	 * Inizialize @masks
	 */
	static {
		masks = new HashMap<PairDirectionLength, BitSet>();
		int[] inc = { 1, 14, 15, 16 }; // Search-directions
		int[] lengths = { 2, 5, 6 }; // Lengths of patterns
		for (int i : inc) {
			for (int len : lengths) {
				PairDirectionLength pair = new PairDirectionLength(len, i);
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
		scores.put("broken three", 5000);
		scores.put("dead four", 2000);
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
			BitSet mask = masks.get(new PairDirectionLength(6, i));
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
			BitSet mask = masks.get(new PairDirectionLength(5, i));
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
			BitSet mask = masks.get(new PairDirectionLength(6, i));
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
			BitSet mask = masks.get(new PairDirectionLength(5, i));
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
			BitSet mask = masks.get(new PairDirectionLength(6, i));
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
			BitSet mask = masks.get(new PairDirectionLength(2, i));
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
	 *         positive, the position is favorable for the @player and vice versa.
	 */
	public int evaluate(Player player) {
		int plus = evaluateSingleSidedNonterminal(player);
		int minus = evaluateSingleSidedNonterminal(player.next());
		return plus - minus;
	}

	public static void main(String[] args) {
        Igra game = new Igra();
        Inteligenca bot = new Inteligenca("bla", Color.black);
        boolean flag = false;
        System.out.println(game);
        while (true) {
            GameState state = game.state();
            if (state == GameState.WIN_Black) {
                System.out.println("X has won!");
                break;
            }
            else if (state == GameState.WIN_White) {
                System.out.println("O has won!");
                break;
            }
            else if (state == GameState.DRAW) {
                System.out.println("Draw!");
                break;
            }
            else {
                if (flag) {
                    long start = System.currentTimeMillis();
                    int move = bot.calculate(game); // If depth were three, move-selection would take far longer than 5 seconds
                    long end = System.currentTimeMillis();
                    game.play(move);
                    System.out.println(game);
                    System.out.println(end - start);                    
                    flag = false;
                    continue;   
                }
                else {
                    long start = System.currentTimeMillis();
                    int move = bot.calculate(game); // If depth were three, move-selection would take far longer than 5 seconds
                    long end = System.currentTimeMillis();
                    game.play(move);
                    System.out.println(game);
                    System.out.println(end - start);                    
                    flag = true;
                    continue;   
                }
            }
        }
    }

}
