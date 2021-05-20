package view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;

import javax.swing.JPanel;

import controller.IGameController;
import controller.IGameView;

@SuppressWarnings("serial")
public class BoardView extends JPanel implements IGameView {

	// MARK: - Static

	private final static int PADDING = 30;

	// MARK: - State

	private IGameController controller;

	// MARK: - Constructor

	public BoardView(IGameController controller) {
		this.controller = controller;
		this.setFocusable(true);

		this.setPreferredSize(new Dimension(500, 500));
	}

	// MARK: - View

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		// Canvas

		Graphics2D g2 = (Graphics2D) g;
		g2.setStroke(new BasicStroke(3));

		// Points
		for (int y = 0; y < this.controller.size(); y++) {
			for (int x = 0; x < this.controller.size(); x++) {
				int n = y * this.controller.size() + x;

				Point coord = this.point(n);

				// Calculated properties
				int r = 10;
				Color color = Color.BLACK;

				switch (this.controller.field(n)) {
				case White:
//					System.out.println(n + " white");
					color = this.controller.white().color();
					break;
				case Black:
//					System.out.println(n + " black");
					color = this.controller.black().color();
					break;
				case EMPTY:
					if (this.controller.active() == null || this.controller.active() != n) {
						r = 3;
						color = Color.DARK_GRAY;
						break;
					}

					// Show the active stone.
					r = 7;
					switch (this.controller.player()) {
					case Black:
						color = this.controller.black().color();
						break;
					case White:
						color = this.controller.white().color();
						break;
					}
					color = brighten(color, 0.10);
				}

				// Draw a stone.
				g.setColor(color);
				g.fillOval(coord.x - r, coord.y - r, 2 * r, 2 * r);
			}
		}
	}

	/**
	 * Make a color brighten.
	 * 
	 * https://stackoverflow.com/questions/18648142/creating-brighter-color-java
	 *
	 * @param color    Color to make brighten.
	 * @param fraction Darkness fraction.
	 * @return Lighter color.
	 */
	public static Color brighten(Color color, double fraction) {

		int red = (int) Math.round(Math.min(255, color.getRed() + 255 * fraction));
		int green = (int) Math.round(Math.min(255, color.getGreen() + 255 * fraction));
		int blue = (int) Math.round(Math.min(255, color.getBlue() + 255 * fraction));

		int alpha = color.getAlpha();

		return new Color(red, green, blue, alpha);
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
		return (container - 2 * PADDING) / this.controller.size();
	}

	/**
	 * Returns the x and y coordinate of the center of the point with index n on the
	 * screen. We try to get max spacing for the points considering the padding and
	 * the size of the window.
	 * 
	 * @param n
	 * @return A pair of coordinates.
	 */
	public Point point(int n) {
		int width = this.getWidth();
		int height = this.getHeight();

		int container = Math.min(width, height);
		int spacing = this.spacing();

		int x = n % this.controller.size();
		int y = n / this.controller.size();

		/**
		 * We calculate the center of the stone by considering all the margins from the
		 * left-top border.
		 */
		int cx = Math.max((width - container) / 2, 0) + PADDING + (spacing / 2) + x * spacing;
		int cy = Math.max((height - container) / 2, 0) + PADDING + (spacing / 2) + y * spacing;

		return new Point(cx, cy);
	}

	@Override
	public JPanel board() {
		return this;
	}
}
