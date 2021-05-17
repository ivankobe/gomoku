package logika;

import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import splosno.Koordinati;

import static util.Util.*;

public class Igra {

	// MARK: - State

	private BitSet blacks;
	private BitSet whites;
	private BitSet empties;

	/**
	 * Safely retrieve (the clone of) the bitboard representing the @player's stones
	 * 
	 * @param player
	 * 
	 * @return
	 */
	public BitSet getBoard(Player player) {
		if (player == Player.Black) {
			return (BitSet) this.blacks.clone();
		}
		return (BitSet) this.whites.clone();
	}

	public BitSet getEmpties() {
		return (BitSet) this.empties.clone();
	}

	/**
	 * Number of stones in a row and column.
	 */
	private int size;

	/**
	 * Returns the size of the board.
	 * 
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

	// MARK: - bitmasks

	/**
	 * Bitmasks to filter out irregular positions when checkin if the game is over.
	 * In order to avoid calculating the masks at each turn, we store them as a
	 * static final field.
	 */
	private static final Map<Integer, BitSet> masks;

	// Initialize masks
	static {
		masks = new HashMap<Integer, BitSet>();
		int[] inc = { 1, 14, 15, 16 };
		for (int i : inc) {
			masks.put(i, getMask(i, 5));
		}
	}

	// MARK: - hashmap

	/**
	 * A custom static class for storing hash values
	 */
	static class PairMovePlayer {

		int move;
		Player player;

		protected PairMovePlayer(int move, Player player) {
			this.move = move;
			this.player = player;
		}

		/**
		 * Since we intend to use pairMovePlayer object as keys in a HashMap, we have to
		 * override the hashCode() and equals() methods.
		 */
		@Override
		public int hashCode() {
			int hash = this.move;
			if (this.player == Player.Black)
				hash = hash * 2;
			return hash;
		}

		@Override
		public boolean equals(Object o) {
			if (o == this)
				return true;
			if (!(o instanceof PairMovePlayer))
				return false;
			PairMovePlayer pair = (PairMovePlayer) o;
			return this.move == pair.move && this.player == pair.player;
		}
	}

	/**
	 * Hashing is done incrementally using Zobrist's method. For each square on the
	 * board and for each player, an unsigned 64-bit integer (long) is generated.
	 * Thus, to update the game's hash, we simply XOR the last move's hash into it.
	 * This ensures that the game's hash is determined solely by the stones on the
	 * board and not by the order in which they were played. Since @hashmap is a
	 * static field, all instances of the class Game, created during the same
	 * session will calculate hash based on the same values. ? Does GUI use
	 * multithreading? ? If so, does each thread operate with its own @hashmap? ?
	 * Does this produce collisions?
	 */
	private static final Map<PairMovePlayer, Long> hashmap;

	// Initialize hashmap
	static {
		hashmap = new HashMap<PairMovePlayer, Long>();
		Random rand = new Random();
		for (int i = 0; i < 225; i++) {
			long hashBlack = Math.abs(rand.nextLong());
			hashmap.put(new PairMovePlayer(i, Player.Black), hashBlack);
			long hashWhite = Math.abs(rand.nextLong());
			hashmap.put(new PairMovePlayer(i, Player.White), hashWhite);
		}
	}

	/**
	 * Sadly, we cannot override Object.hashCode() to implement Zobrist's method,
	 * since it's return type is int instad of long. Thus, we store the game's hash
	 * in a special field.
	 */
	private long hash;

	/**
	 * Returns the game's hash.
	 * 
	 * @return
	 */
	public long hash() {
		return this.hash;
	}

	// MARK: - candidates

	/**
	 * An additional data structure that thracks all empty fields on the board whose
	 * distance (in any direction) to the nearest non-empty field is not greater
	 * than one. @candidates are calculated incrementally. This is used to cut down
	 * the branching factor in the game-tree.
	 */
	private Set<Integer> candidates;

	/**
	 * Returns the set of candidates.
	 * 
	 * @return
	 */
	public Set<Integer> candidates() {
		return this.candidates;
	}

	// MARK: - Constructor

	public Igra(Player starting, int size) {
		/**
		 * The board is represented as a triplet of bitsets in order to make
		 * pattern-recognition faster. - blacks is a bitset representing black stones -
		 * whites is a bitset representing white stones (i.e. bb.get(n) == true iff the
		 * n-th square on the board is populated with a black stone) - empties is the
		 * complement of bb and bw
		 */
		this.blacks = new BitSet(225);
		this.whites = new BitSet(225);
		this.empties = new BitSet(225);

		this.size = 15;

		empties.set(0, 225); // bb and bw are initially empty, be is initially full.

		this.player = starting;

		this.hash = 0;

		this.candidates = new HashSet<Integer>();
	}

	public Igra() {
		/**
		 * By default, black is first to move and the board is 15x15.
		 */
		this.blacks = new BitSet(225);
		this.whites = new BitSet(225);
		this.empties = new BitSet(225);

		this.size = 15;

		empties.set(0, 225); // bb and bw are initially empty, be is initially full.

		this.player = Player.Black;

		this.hash = 0;

		this.candidates = new HashSet<Integer>();
	}

	public Igra(Igra game) {
		/**
		 * Constructor for cloning purposes
		 */
		this.blacks = (BitSet) game.blacks.clone();
		this.whites = (BitSet) game.whites.clone();
		this.empties = (BitSet) game.empties.clone();

		this.size = game.size();

		this.player = game.player();

		this.hash = game.hash();

		// It is important to clone the set
		this.candidates = new HashSet<Integer>(game.candidates());
	}

	// MARK: - Field

	public enum Field {
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
	 * Tells whether a move is valid. A move is valid if the field is still empty.
	 * 
	 * @return A boolean whether the move is valid.
	 */
	public boolean isValidMove(int n) {
		if (n >= 225 || n < 0)
			return false;
		return this.validMoves().contains(n);
	}

	/**
	 * Use this function to get a list of valid moves in the game.
	 * 
	 * @return A list of valid moves. j
	 */
	public Set<Integer> validMoves() {
		Set<Integer> moves = new HashSet<Integer>();

		if (this.state() != GameState.IN_PROGRESS)
			return moves;

		int n = this.size() * this.size();
		for (int i = 0; i < n; i++) {
			if (this.field(n) == Field.EMPTY)
				moves.add(i);
		}
		return moves;
	}

	// MARK: - Play

	/**
	 * Places a stone on the board.
	 * 
	 * @return Whether the move was possible.
	 */
	public boolean play(int move) {
		if (!this.isValidMove(move))
			return false;

		// Update the board.
		if (this.player == Player.Black)
			this.blacks.set(move);
		else
			this.whites.set(move);

		// Clear the complement.
		this.empties.clear(move);
		// Update hash
		this.hash = this.hash ^ hashmap.get(new PairMovePlayer(move, this.player()));
		// Update candidates
		this.updateCandidates(move);

		// Update the player.
		this.player = this.player.next();
		return true;
	}

	/**
	 * Incrementally updating the set of candidates after a move is played.
	 * 
	 * @param move
	 */
	private void updateCandidates(int move) {
		this.candidates.remove(move);
		// Search two steps in all eight directions
		// Horisontally
		int horD = move % 15; // Space in the decreasing (D) horisontal (hor) direction
		if (horD > 0 & isValidMove(move - 1))
			this.candidates.add(move - 1);
		// if (horD > 1 & isValidMove(move - 2))
		// this.candidates.add(move - 2);
		int horI = 14 - horD;
		if (horI > 0 & isValidMove(move + 1))
			this.candidates.add(move + 1);
		// if (horI > 1 & isValidMove(move + 2))
		// this.candidates.add(move + 2);
		// Vertically
		int verD = move / 15;
		if (verD > 0 & isValidMove(move - 15))
			this.candidates.add(move - 15);
		// if (verD > 1 & isValidMove(move - 2 * 15))
		// this.candidates.add(move - 2 * 15);
		int verI = 14 - verD;
		if (verI > 0 & isValidMove(move + 15))
			this.candidates.add(move + 15);
		// if (verI > 1 & isValidMove(move + 2 * 15))
		// this.candidates.add(move + 2 * 15);
		// On the diagonal
		int diagD = Math.min(horI, verD);
		if (diagD > 0 & isValidMove(move - 14))
			this.candidates.add(move - 14);
		// if (diagD > 1 & isValidMove(move - 2 * 14))
		// this.candidates.add(move - 2 * 14);
		int diagI = Math.min(horD, verI);
		if (diagI > 0 & isValidMove(move + 14))
			this.candidates.add(move + 14);
		// if (diagI > 1 & isValidMove(move + 2 * 14))
		// this.candidates.add(move + 2 * 14);
		// On the counterdiagonal
		int counterdiagD = Math.min(horD, verD);
		if (counterdiagD > 0 & isValidMove(move - 16))
			this.candidates.add(move - 16);
		// if (counterdiagD > 1 & isValidMove(move - 2 * 16))
		// this.candidates.add(move - 2 * 16);
		int counterdiagI = Math.min(horI, verI);
		if (counterdiagI > 0 & isValidMove(move + 16))
			this.candidates.add(move + 16);
		// if (counterdiagI > 1 & isValidMove(move + 2 * 16))
		// this.candidates.add(move + 2 * 16);
	}

	/**
	 * Places a stone on the board with the given coordinates and tells whether the
	 * move is valid.
	 * 
	 * @param koordinati
	 * @return
	 */
	public boolean odigraj(Koordinati koordinati) {
		return this.play(koordinati.getX() + koordinati.getY() * this.size);
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
		if (this.hasWon(Player.Black))
			return GameState.WIN_Black;
		if (this.hasWon(Player.White))
			return GameState.WIN_White;
		if (empties.isEmpty())
			return GameState.DRAW;
		return GameState.IN_PROGRESS;
	}

	/**
	 * Checks whether the player has a continuous string of five or more set bits.
	 * 
	 * @return boolean
	 */
	private boolean hasWon(Player player) {
		int[] inc = { 1, 14, 15, 16 }; // directions
		for (int i : inc) {
			// Select the appropriate bitboard and clone it
			BitSet stones = this.getBoard(player);
			// Shift left four times
			BitSet shifted1 = shl(stones, i);
			BitSet shifted2 = shl(shifted1, i);
			BitSet shifted3 = shl(shifted2, i);
			BitSet shifted4 = shl(shifted3, i);
			// AND all five boards togeather
			stones.and(shifted1);
			stones.and(shifted2);
			stones.and(shifted3);
			stones.and(shifted4);
			BitSet mask = masks.get(i); // Retrieve the appropriate mask
			stones.and(mask); // Apply it
			if (!stones.isEmpty())
				return true;
		}
		return false;
	}

	// MARK: - Overrides

	@Override
	public String toString() {
		String str = "0 1 2 3 4 5 6 7 8 9 10 11 12 13 14 \n";
		int row = 0;
		for (int i = 0; i < 225; i++) {
			if (this.blacks.get(i))
				str = str + "X ";
			else if (this.whites.get(i))
				str = str + "O ";
			else
				str = str + "_ ";
			if (i % 15 == 14) {
				str = str + " " + String.valueOf(row) + "\n";
				row++;
			}

		}
		return str;
	}

}
