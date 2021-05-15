package controller;

import java.awt.Point;

/**
 * This view defines properties that other classes may
 * use to get information about how the game is presented on the screen.
 */

public interface IGameViewInfo {

	/**
	 * Returns the point on the screen that the center of the stone
	 * occupies.
	 * 
	 * @param n
	 * @return
	 */
	public Point point(int n);
}
