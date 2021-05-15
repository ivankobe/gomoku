package controller;

import splosno.Koordinati;

/**
 * Specifies the methods available for the player to use to communicate with the game.
 */

public interface IGameController {
	/**
	 * Sets the active stone.
	 * 
	 * @param koodrinati
	 * @return boolean indecating if the move was valid.
	 */
	public boolean setActive(Koordinati koodrinati);
	
	/**
	 * Submits the active play.
	 */
	public void confirm();
	
	/**
	 * Returns the current state of the game so that the player
	 * may investigate the best move.
	 * @return
	 */
	public IGameInfo state();
	
	/**
	 * Contains information about how the game is being displayed.
	 * 
	 * @return
	 */
	public IGameViewInfo view();
}