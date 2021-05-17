package inteligenca;

import java.awt.Color;

import controller.IGameController;
import controller.IPlayer;
import logika.Igra;

import splosno.KdoIgra;
import splosno.Koordinati;

/**
 * This file contains a class that plays by itself the Gomoku game.
 */

public class Inteligenca extends KdoIgra implements IPlayer {
	
	// MARK: - State
	
	private Color color;
	
	// MARK: - Contructor
	
	public Inteligenca(String ime, Color color) {
		super(ime);		
		
		this.color = color;
	}
	
	// MARK: - Accessors
	
	/**
	 * Returns the name of the player.
	 */
	public String name() {
		return this.ime;
	}
	
	/**
	 * Returns the color of the stones.
	 */
	public Color color() {
		return this.color;
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
