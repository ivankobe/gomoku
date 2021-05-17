package inteligenca;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import controller.IGameController;
import controller.IPlayer;
import splosno.KdoIgra;
import view.GameView;

public class Clovek implements IPlayer, MouseListener, MouseMotionListener {
	
	private String name;
	private Color color;

	// MARK: - Contructor

	public Clovek(String name, Color color) {
		this.name = name;
		this.color = color;
		
	}
	
	// MARK: - Acceessors
	
	/**
	 * Returns the name of the human.
	 */
	public String name() {
		return this.name;
	}
	
	/**
	 * Returns the color of the stones.
	 */
	public Color color() {
		return this.color;
	}
	
	// MARK: - Move
	
	/**
	 * References the controler that we may use to make selection.
	 */
	
	private IGameController controller;
	
	@Override
	public void move(IGameController controller) {
		this.controller = controller;
	}
	
	// MARK: - Events
	

	@Override
	public void mousePressed(MouseEvent e) {
		this.controller.confirm();	
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// Check that we have the control.
		if (this.controller == null) return;
		
		// Reset the current state.
		this.controller.setActive(null);

		// Input Data
		int x = e.getX();
		int y = e.getY();
		
		// Stones
		for (int n: this.controller.state().validMoves()) {
			Point coord = this.controller.view().point(n);
			
			if (d(coord.x, coord.y, x, y) < 10) {
				this.controller.setActive(null);
			}
		}
	}

	// MARK: - Utility functions
	
	/**
	 * Calculates the distance from point (a, b) to (x, y).
	 * 
	 * @param a
	 * @param b
	 * @param x
	 * @param y
	 * @return
	 */
	private double d(int a, int b, int x, int y) {
		return Math.sqrt(Math.pow(x - a, 2) + Math.pow(y - b, 2));
	}

	// MARK: - Extras

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mouseDragged(MouseEvent e) {
	}
}
