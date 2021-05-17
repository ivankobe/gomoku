package controller;

import javax.swing.JPanel;

import logika.Igra;
import logika.Igra.GameState;
import logika.Igra.Field;
import view.GameView;

/**
 * Game controller synchronises game view with the view.
 */

public class GameController implements IGameController {
	/**
	 * Back reference to the view that this controller is in control of.
	 */
	private GameView view;

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
	 * 
	 * @return
	 */
	public Integer active() {
		return this.active;
	}
	
	/**
	 * Returns the size of the game.
	 * @return
	 */
	public int size() {
		return this.game.size();
	}
	
	/**
	 * Returns the view of the game.
	 */
	public JPanel panel() {
		return this.view;
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
				this.black.move(this);
				break;
			case White:
				this.white.move(this);
			}
		}

	}

	// MARK: - IGameViewController

	@Override
	public void setActive(Integer n) {
		this.active = n;
		this.view.repaint();
	}

	@Override
	public boolean confirm() {
		if (this.active == null)
			return false;

		boolean successful = this.game.play(active);

		//
		this.view.repaint();
		if (successful)
			this.tick();

		return successful;
	}

	@Override
	public Igra state() {
		return this.game;
	}

	@Override
	public IGameViewInfo view() {
		return this.view;
	}

}
