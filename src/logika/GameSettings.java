package logika;

import java.awt.Color;

/**
 * Contains customizable parts of each game. We use data in this class
 * to create a new game after setup.
 */

public class GameSettings {
	/**
	 * Settings for the white player.
	 */
	public Player white;
	/**
	 * Settings for the black player.
	 */
	public Player black;
	
	/**
	 * Tells the board size.
	 */
	public int size;
	
	// MARK: - Constructor
	
	/**
	 * Creates a default game setting.
	 */
	public GameSettings() {
		this.black = new Player("White", Color.WHITE);
		this.white = new Player("Black", Color.BLACK);
		this.size = 15;
	}
}
