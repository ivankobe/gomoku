package logika;

import java.awt.Color;

/**
 * Represents settings for a signle player participating in the game.
 */

public class Player {
	/**
	 * The name of the palyer.
	 */
	public String name; 
	/**
	 * Color of player's stones.
	 */
	public Color color;
	/**
	 * Tells whether a player is a computer.
	 */
	public boolean computer;
	
	
	// MARK: - Constructor
	
	public Player(String name, Color color) {
		this.name = name;
		this.color = color;
		this.computer = false;
	}
}
