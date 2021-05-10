package main;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

import main.Game.Player;

// GUI -----------------------------------------------------------------------

public class GUI {
	public static void main(String[] args) {
		Okno okno = new Okno();
		okno.pack();
		okno.setVisible(true);
	}
}

// Okno ----------------------------------------------------------------------

/**
 * 
 * Okno is in charge of creating a new game and managing the games played.
 *
 */

@SuppressWarnings("serial")
class Okno extends JFrame implements ActionListener {
	
	// MARK: - State

	private Platno platno;

	// MARK: - Constructor

	public Okno() {
		super();

		this.setTitle("Gomoku");
		this.setLayout(new BorderLayout());
		
		/**
		 * The layout of the app is first split into two sections:
		 *  - the left one contains the game,
		 *  - the right one contains game state and settings.
		 *  
		 * The right section is then further split into multiple sections
		 * that allow players to customize the game. 
		 */
		JPanel main = new JPanel();
		main.setLayout(new BoxLayout(main, BoxLayout.X_AXIS));		

		/**
		 * We start with a preconfigured game when the user opens
		 * the GUI. Then, they may configure it and restart.
		 */
		// Game
		Platno platno = new Platno(new Game());
		
		main.add(platno);
		this.platno = platno;
		
		// Options
		JPanel options = new JPanel();
		options.setLayout(new BoxLayout(options, BoxLayout.Y_AXIS));
		
		

		// Complete
		this.add(main, BorderLayout.CENTER);
	}
	

	// MARK: - Methods
	
	private void start() {
		// Create a new game.
		Game game = new Game();
				
		// Construct a new view.
		this.platno = new Platno(game); 
		this.repaint();
	}

	/**
	 * Adds a new button to the panel with a given label.
	 * 
	 * @param panel
	 * @param label
	 * @return
	 */
	private JButton button(JPanel panel, String label) {
		JButton button = new JButton(label);

		button.addActionListener(this);
		panel.add(button);

		return button;
	}

	// MARK: - Events

	@Override
	public void actionPerformed(ActionEvent e) {
//		Object source = e.getSource();
//		
//		if (source == gumbRdeca) {}
//		else if (source == gumbRumena) {}
//		else if (source == gumbZelena) {}
	}
}

// Platno --------------------------------------------------------------------

/**
 * Platno is in charge of displaying a single game and listening for internal
 * events related to the gameplay.
 */

@SuppressWarnings("serial")
class Platno extends JPanel implements MouseListener, MouseMotionListener {

	// MARK: - Static

	private final static int PADDING = 30;

	// MARK: - State

	/**
	 * The game that we are playing.
	 */
	private Game game;

	/**
	 * The index of the stone we are hovering over.
	 */
	private Integer active;

	private Color black;
	private Color white;

	// MARK: - Constructor

	public Platno(Game game) {
		super();

		// State
		this.game = game;

		this.black = Color.BLACK;
		this.white = Color.WHITE;

		// Size
		this.setPreferredSize(new Dimension(400, 800));

		// Events
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
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

				Coordinates coord = this.point(n);

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

	// MARK: - Events

	@Override
	public void mousePressed(MouseEvent e) {
		/**
		 * If the value of the active stone is not null we play that stone as the next
		 * move.
		 */
		if (this.active == null)
			return;

		this.game.play(this.active);
		this.active = null;

		// Repain the canvas.
		this.repaint();
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		this.active = null;

		// Input Data
		int x = e.getX();
		int y = e.getY();

		// Stones
		int stones = this.game.size() * this.game.size();

		for (int n = 0; n < stones; n++) {
			if (!this.game.isValidMove(n))
				continue;

			/**
			 * We draw each stone to the center of their xy-coordinates. Then, based on the
			 * type of the stone, we change the radius and the color of the stone.
			 */
			Coordinates cord = this.point(n);

			if (d(cord.x, cord.y, x, y) < this.spacing() / 2) {
				this.active = n;
			}
		}

		// Redraw the canvas.
		this.repaint();
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
	 * Returns the x and y coordinate of the center of the point with index n. We
	 * try to get max spacing for the points considering the padding and the size of
	 * the window.
	 * 
	 * @param n
	 * @return A pair of coordinates.
	 */
	private Coordinates point(int n) {
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

		return new Coordinates(cx, cy);
	}

	class Coordinates {
		public int x;
		public int y;

		Coordinates(int x, int y) {
			this.x = x;
			this.y = y;
		}
	}

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
