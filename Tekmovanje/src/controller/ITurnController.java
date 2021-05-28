package controller;

import logika.Igra;

/**
 * Specifies the methods available for the player to use to communicate with the game
 * when that player is on turn.
 */

public interface ITurnController {
	/**
	 * Sets the active stone.
	 * 
	 * @param n
	 * @return boolean indecating if the move was valid.
	 */
	public void setActive(Integer n);
	
	/**
	 * Submits the active play.
	 */
	public boolean confirm();
	
	/**
	 * Returns the current state of the game so that the player
	 * may investigate the best move.
	 * @return
	 */
	public Igra game();
	
	/**
	 * Contains information about how the game is being displayed.
	 * 
	 * @return
	 */
	public IGameView view();
}