package controller;

import java.util.BitSet;

public interface IGameInfo {
	/**
	 * Tells the size of the board.
	 * @return
	 */
	public int size();
	
	/**
	 * A list of empty fields that the player may still play.
	 */
	public BitSet validMoves();
	
	/**
	 * A list of stones on the board.
	 * @return
	 */
//	public String board();
}
