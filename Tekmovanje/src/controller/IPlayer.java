package controller;

import java.awt.Color;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public interface IPlayer extends MouseListener, MouseMotionListener {
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
	 * Called when player should take control of the game.
	 */
	public void take(ITurnController controller);
	
	/**
	 * Called when the player should release the control of the move.
	 */
	public void release();
}
