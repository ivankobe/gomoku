package controller;

import logika.Igra;
import logika.Igra.GameState;
import logika.Igra.Player;
import splosno.Koordinati;
import view.GameView;

/**
 * Game controller synchronises game view with the view.
 */

public class GameController implements IGameController {
	/**
	 * The view that this controller is in control of.
	 */
	private GameView view;
	
	/**
	 * The game state.
	 */
	private Igra game;
	
	/**
	 * References to the palyer classes.
	 */
	private IPlayer black;
	private IPlayer white;
	
	// MARK: - Contructor
	
	public GameController(IPlayer black, IPlayer white) {
		this.black = black;
		this.white = white;
		
		// Start
		this.tick();
	}
	
	// MARK: - Methods
	
	/**
	 * Represents a single round on the board. Events are handled
	 * so that we give away control to the player until the player performs a move.
	 */
	private void tick() {
		while (this.game.state() == GameState.IN_PROGRESS) {
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
	public boolean setActive(Koordinati koodrinati) {
		
		
		this.view.repaint();
		
		return false;
	}

	@Override
	public void confirm() {
		this.game.play(5);
		
		this.view.repaint();
		
		// Next turn.
		this.tick();
	}

	@Override
	public IGameInfo state() {
		// TODO Auto-generated method stub
		return this.game;
	}

	@Override
	public IGameViewInfo view() {
		// TODO Auto-generated method stub
		return this.view;
	}
	
}
