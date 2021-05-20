package controller;

import logika.Igra;
import logika.Igra.Field;
import logika.Igra.GameState;

public interface IGameController {
	/**
	 * Returns the black player in the game.
	 * 
	 * @return
	 */
	public IPlayer black();

	/**
	 * Returns the white player of the game.
	 * 
	 * @return
	 */
	public IPlayer white();
	
	/**
	 * Returns the currently active player.
	 * 
	 * @return
	 */
	public Igra.Player player();
	
	/**
	 * Returns information about the stone at index n.
	 * 
	 * @param n
	 * @return
	 */
	public Field field(int n);
	
	/**
	 * Returns the active stone integer.
	 * 
	 * @return
	 */
	public Integer active();
	
	/**
	 * Returns the size of the game.
	 * @return
	 */
	public int size();
	
	/**
	 * Returns the state of the game.
	 */
	public GameState state();
}
