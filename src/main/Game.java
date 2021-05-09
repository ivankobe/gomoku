package main;

import java.util.BitSet;
import java.util.Set;

import threat.ThreatSearch;

public class Game {

	// MARK: - State

	private BitSet blacks;
	private BitSet whites;
	private BitSet empties;

	private ThreatSearch ts;
	
	/**
	 * Number of stones in a row and column.
	 */
	private int size;
	
	/**
	 * Returns the size of the board.
	 * @return
	 */
	public int size() {
		return this.size;
	}

	/**
	 * Player that is currently on the move.
	 */
	private Player player;

	public enum Player {
		White, Black;

		/**
		 * Returns the next player based on the current one.
		 * 
		 * @return Player.
		 */
		public Player next() {
			if (this == Black)
				return White;
			return Black;
		}
	}

	/**
	 * Returns the player that is on the turn.
	 * 
	 * @return
	 */
	public Player player() {
		return this.player;
	}

	// MARK: - Constructor

	public Game(Player starting) {
		/**
		 * The board is represented as a triplet of bitsets in order to make
		 * pattern-recognition faster. - blacks is a bitset representing black stones -
		 * whites is a bitset representing white stones (i.e. bb.get(n) == true iff the
		 * n-th square on the board is populated with a black stone) - empties is the
		 * complement of bb and bw
		 * 
		 * 0 is the top-left corner, 14 is the top-right, 210 is the bottom-left, and
		 * 224 is the bottom-right.
		 */
		this.blacks = new BitSet(225);
		this.whites = new BitSet(225);
		this.empties = new BitSet(225);
		
		this.size = 15;

		empties.set(0, 225); // bb and bw are initially empty, be is initially full.

		this.player = starting;
	}

	// MARK: - Field

	enum Field {
		Black, White, EMPTY;
	}

	/**
	 * Returns information about the value of a field at n.
	 * 
	 * @param n
	 * @return
	 */
	public Field field(int n) {
		if (blacks.get(n)) {
			return Field.Black;
		}
		if (whites.get(n)) {
			return Field.White;
		}

		return Field.EMPTY;
	}

	/**
	 * Tells whether a move is valid. A move is valid if the
	 * field is still empty.
	 * 
	 * @return A boolean whether the move is valid.
	 */
	public boolean isValidMove(int n) {
		return empties.get(n);
	}

	/**
	 * Use this function to get a list of valid moves in the game.
	 * 
	 * @return A list of valid moves.
	 */
	public BitSet validMoves() {
		return empties;
	}

	// MARK: - Play

	/**
	 * Places a stone on the board.
	 * 
	 * @return Whether the move was possible.
	 */
	public boolean play(int n) {
		if (!this.isValidMove(n))
			return false;

		// Update the board.
		if (this.player == Player.Black) {
			this.blacks.set(n);
		} else {
			this.whites.set(n);
		}

		// Clear the complement.
		empties.clear(n);

		// Update the player.
		this.player = this.player.next();

		return true;
	}

	// MARK: - State

	public enum GameState {
		IN_PROGRESS, WIN_Black, WIN_White, DRAW;
	}

	/**
	 * Tells the state of the game.
	 * 
	 * @return Winner, draw or in_progress.
	 */
	public GameState state() {
		Set<Integer> black = ts.getFives(this.blacks);
		Set<Integer> white = ts.getFives(this.whites);

		if (!black.isEmpty()) {
			return GameState.WIN_Black;
		}

		if (!white.isEmpty()) {
			return GameState.WIN_White;
		}

//		if (!fivesB.isEmpty() & !fivesW.isEmpty()) {
//			throw new Exception("OOps!");
//		}

		if (empties.isEmpty()) {
			return GameState.DRAW;
		}

		return GameState.IN_PROGRESS;
	}
}
