package controller;

import javax.swing.JPanel;

import logika.Igra;
import logika.Igra.GameState;
import logika.Igra.Field;
import view.GameView;

/**
 * Game controller synchronises game view with the view.
 */

public class GameController implements ITurnController, IGameController {
	/**
	 * Back reference to the view that this controller is in control of.
	 */
	private IGameView view;

	/**
	 * The game state.
	 */
	private Igra game;

	/**
	 * The index of the stone that is currently selected.
	 */
	private Integer active;

	/**
	 * References to the palyer classes.
	 */
	private IPlayer black;
	private IPlayer white;

	// MARK: - Contructor

	public GameController(IPlayer black, IPlayer white) {
		this.black = black;
		this.white = white;

		this.game = new Igra();
		this.view = new GameView(this);

		// Start
		this.tick();
	}

	// MARK: - Accessors

	/**
	 * Returns the black player in the game.
	 * 
	 * @return
	 */
	public IPlayer black() {
		return this.black;
	}

	/**
	 * Returns the white player of the game.
	 * 
	 * @return
	 */
	public IPlayer white() {
		return this.white;
	}

	/**
	 * Returns information about the stone at index n.
	 * 
	 * @param n
	 * @return
	 */
	public Field field(int n) {
		return this.game.field(n);
	}

	/**
	 * Returns the active stone integer. 
	 */
	public Integer active() {
		return this.active;
	}

	/**
	 * Returns the size of the game.
	 */
	public int size() {
		return this.game.size();
	}

	/**
	 * Returns the player that is currently on turn.
	 */
	public Igra.Player player() {
		return this.game.player();
	}
	
	/**
	 * Returns the state of the game.
	 */
	public GameState state() {
		return this.game.state();
	}

	/**
	 * Returns the view of the game.
	 */
	public JPanel panel() {
		return this.view.board();
	}

	// MARK: - Methods

	/**
	 * Represents a single round on the board. Events are handled so that we give
	 * away control to the player until the player performs a move.
	 */
	private void tick() {
		if (this.game.state() == GameState.IN_PROGRESS) {
			switch (this.game.player()) {
			case Black:
				this.white.release();
				this.black.take(this);
				break;
			case White:
				this.black.release();
				this.white.take(this);
				break;
			}
		}
	}

	// MARK: - IGameViewController

	@Override
	/**
	 * Mark a stone as active. Use `confirm` to finish the turn.
	 */
	public void setActive(Integer n) {
		this.active = n;
		this.view.repaint();
	}

	@Override
	/**
	 * Confirm the pick.
	 */
	public boolean confirm() {
		// Make a move in the model.
		if (this.active == null)
			return false;

		boolean successful = this.game.play(active);

		// Reset the view.
		this.active = null;
		this.view.repaint();

		if (successful) {
			// Start the next turn.
			this.tick();
		}

		return successful;
	}

	@Override
	/**
	 * Returns the game state.
	 */
	public Igra game() {
		return this.game;
	}

	@Override
	/**
	 * Returns the view information.
	 */
	public IGameView view() {
		return this.view;
	}

}
