package controller;

import logika.GameSettings;

import view.Window;
import view.GameSettingsView;

public class WindowController {
	/**
	 * Game settings.
	 */
	private GameSettings settings;
	
	/**
	 * Game controller.
	 */
	private GameController game;
	
	/**
	 * Window containing the application.
	 */
	private Window window;
	
	// MARK: - Constructor
	
	public WindowController() {
		this.game = null;
		this.settings = new GameSettings();
		this.window = new Window(this);
	}
	
	// MARK: - Methods
	
	/**
	 * Presents the window on the screen.
	 */
	public void show() {
		this.window.pack();
		this.window.setVisible(true);
	}
	
	/**
	 * Starts creating a new game by opening creating new settings.
	 */
	public void setup() {
		this.settings = new GameSettings();
		this.window.setView(new GameSettingsView(this.settings));
		this.game = null;
	}
	
	/**
	 * Starts a new game with current settings.
	 */
	public void start() {
		IPlayer white = null;
		IPlayer black = null;
		
		this.game = new GameController(black, white);
		
		this.window.setView(this.game.panel());
	}
}
