package view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;

import javax.swing.JPanel;

import controller.IGameViewInfo;
import logika.Igra;

/**
* GameView is in charge of displaying a single game and listens for user input.
*/

@SuppressWarnings("serial")
public class GameView extends JPanel implements IGameViewInfo {

	// MARK: - Static

	private final static int PADDING = 30;

	// MARK: - State

	/**
	 * The game that we are playing.
	 */
	private Igra game;

	/**
	 * The index of the stone we are hovering over.
	 */
	private Integer active;

	private Color black;
	private Color white;

	// MARK: - Constructor

	public GameView(Igra game) {
		super();

		// State
		this.game = game;

		this.black = Color.BLACK;
		this.white = Color.WHITE;

		// Size
		this.setPreferredSize(new Dimension(400, 800));

		// Events
		this.setFocusable(true);
	}

	// MARK: - View

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		// Canvas

		Graphics2D g2 = (Graphics2D) g;
		g2.setStroke(new BasicStroke(3));

		// Points
		for (int y = 0; y < this.game.size(); y++) {
			for (int x = 0; x < this.game.size(); x++) {
				int n = y * this.game.size() + x;

				Point coord = this.point(n);

				// Calculated properties
				int r = 3;

				switch (this.game.field(n)) {
				case White:
					g.setColor(this.white);
					r = 10;
					break;
				case Black:
					g.setColor(this.black);
					r = 10;
					break;
				case EMPTY:
					if (this.active == null || this.active != n) {
						g.setColor(Color.DARK_GRAY);
						break;
					}

					// Show the active stone.
					r = 8;

					switch (this.game.player()) {
					case White:
						g.setColor(this.white);
						break;
					case Black:
						g.setColor(this.black);
					}
				}

				// Draw a stone.
				g.fillOval(coord.x - r, coord.y - r, 2 * r, 2 * r);
			}
		}
	}


	/**
	 * Returns the spacing between the centers of two points.
	 * 
	 * @return
	 */
	private int spacing() {
		int width = this.getWidth();
		int height = this.getHeight();

		int container = Math.min(width, height);
		return (container - 2 * PADDING) / this.game.size();
	}

	/**
	 * Returns the x and y coordinate of the center of the point with index n on the screen. We
	 * try to get max spacing for the points considering the padding and the size of
	 * the window.
	 * 
	 * @param n
	 * @return A pair of coordinates.
	 */
	public Point point(int n) {
		int width = this.getWidth();
		int height = this.getHeight();

		int container = Math.min(width, height);
		int spacing = this.spacing();

		int x = n % this.game.size();
		int y = n / this.game.size();

		/**
		 * We calculate the center of the stone by considering all the margins from the
		 * left-top border.
		 */
		int cx = Math.max((width - container) / 2, 0) + PADDING + (spacing / 2) + x * spacing;
		int cy = Math.max((height - container) / 2, 0) + PADDING + (spacing / 2) + y * spacing;

		return new Point(cx, cy);
	}
}
