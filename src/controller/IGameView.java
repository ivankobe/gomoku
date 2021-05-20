package controller;

import java.awt.Point;

import javax.swing.JPanel;

/**
 * This view defines properties that other classes may
 * use to get information about how the game is presented on the screen.
 */

public interface IGameView {
	/**
	 * Returns the view containing the board.
	 * @return
	 */
	public JPanel board();

	/**
	 * Returns the point on the screen that the center of the stone
	 * occupies.
	 * 
	 * @param n
	 * @return
	 */
	public Point point(int n);
	
	/**
	 * Rerenders the view.
	 */
	public void repaint();
}
