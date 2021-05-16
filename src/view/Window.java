package view;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import logika.Igra;

/**
 * 
 * Window displays a single game. There might be multiple games, each in
 * its own window in a single open program.
 *
 */

@SuppressWarnings("serial")
public class Window extends JFrame implements ActionListener {
	
	// MARK: - State

	private GameSetupView setup;
	private GameView game;

	// MARK: - Constructor

	public Window() {
		super();

		this.setTitle("Gomoku");
		this.setLayout(new BorderLayout());
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		
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
//		Platno platno = new Platno(new Igra());
		
//		main.add(platno);
//		this.platno = platno;
		
		// Options
		JPanel options = new JPanel();
		options.setLayout(new BoxLayout(options, BoxLayout.Y_AXIS));
		
		

		// Complete
		this.add(main, BorderLayout.CENTER);
	}
	

	// MARK: - Methods
	
	/**
	 * Starts a new game with the given settings.
	 */
	private void start() {
		this.removeAll();
		
		// Create a new game.
		Igra game = new Igra();
				
		// Construct a new view.
//		this.platno = new Platno(game); 
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

