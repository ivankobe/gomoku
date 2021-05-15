package inteligenca;

import controller.IGameController;
import controller.IPlayer;
import logika.Igra;

import splosno.KdoIgra;
import splosno.Koordinati;

/**
 * This file contains a class that plays by itself the Gomoku game.
 */

public class Inteligenca extends KdoIgra implements IPlayer {
	// MARK: - Contructor
	
	public Inteligenca(String ime) {
		super(ime);		
	}

	
	// MARK: - Methods
	
	/**
	 * Makes a move.
	 * 
	 * @param igra
	 * @return
	 */
	Koordinati izberiPotezo(Igra igra) {
		return new Koordinati(1, 2);
	}

	// MARK: - Player
	
	public String name() {
		return "CPU";
	}
	
	/**
	 * The control of the game that we get during our turn.
	 */
	@Override
	public void move(IGameController controller) {
		int poteza = 5;
		controller.setActive(null);
		controller.confirm();
	}
}
