package controller;

import logika.GameSettings;

/**
 * Outlines the methods that a settings controller should implement.
 */

public interface IGameSettingsController {
	/**
	 * Returns the current settings.
	 * 
	 * @return
	 */
	public GameSettings settings();
	
	/**
	 * Starts the game.
	 */
	public void start();
}
