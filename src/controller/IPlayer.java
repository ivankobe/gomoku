package controller;

import java.awt.Color;

public interface IPlayer {
	/**
	 * Returns the name of the player.
	 * @return
	 */
	public String name();
	
	/**
	 * Returns the color of player's stones.
	 */
	public Color color();
	
	/**
	 * Called when a class, conforming to the player type
	 * should make a move.
	 */
	public void move(IGameController controller);
}
