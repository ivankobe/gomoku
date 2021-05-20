package controller;

import javax.swing.JPanel;

import inteligenca.Clovek;
import inteligenca.Inteligenca;
import logika.GameSettings;
import logika.Player;
import view.Window;
import view.GameSettingsView;

public class WindowController implements IGameSettingsController {
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

		this.setup();
	}

	// MARK: - Views

	/**
	 * Starts creating a new game by opening creating new settings.
	 */
	public void setup() {
		this.settings = new GameSettings();
		this.game = null;

		GameSettingsView view = new GameSettingsView(this);

		this.window.setContentPane(view);
		this.show();
	}

	/**
	 * Starts a new game with current settings.
	 */
	public void start() {
		// Create players from settings.
		IPlayer white = this.player(settings.white);
		IPlayer black = this.player(settings.black);

		// Construct a game.
		this.game = new GameController(white, black);
		JPanel panel = this.game.panel();

		// Add event listeners.
		panel.addMouseListener(white);
		panel.addMouseMotionListener(white);
		panel.addMouseListener(black);
		panel.addMouseMotionListener(black);

		this.window.setContentPane(panel);
		this.show();
	}

	/**
	 * Creates a new player instance from settings.
	 * 
	 * @param settings
	 * @return
	 */
	private IPlayer player(Player settings) {
		System.out.print(settings.computer);
		IPlayer player;
		if (settings.computer)
			player = new Inteligenca(settings.name, settings.color);
		else
			player = new Clovek(settings.name, settings.color);

		return player;
	}

	// MARK: - Methods

	/**
	 * Presents the window on the screen.
	 */
	public void show() {
		this.window.pack();
		this.window.setVisible(true);
	}

	// MARK: - IGameSettingsController

	/**
	 * Returns current game settings.
	 */
	@Override
	public GameSettings settings() {
		return this.settings;
	}
}
