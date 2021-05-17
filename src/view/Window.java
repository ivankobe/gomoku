package view;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

import controller.WindowController;
import logika.Igra;

/**
 * 
 * Window displays a single game settings and view. There might be multiple games, 
 * each in its own window in a single open program.
 *
 */

@SuppressWarnings("serial")
public class Window extends JFrame implements ActionListener {

	// MARK: - State
	
	private WindowController controller;

	// MARK: - Components
	
	private JMenuItem startGameMenuItem;

	// MARK: - Constructor

	public Window(WindowController controller) {
		super();
		
		this.controller = controller;

		// Set up
		this.setTitle("Gomoku");
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);

		// MenuBar
		JMenuBar menu_bar = new JMenuBar();
		this.setJMenuBar(menu_bar);
		
		JMenu igra_menu = new JMenu("Nova igra");
		menu_bar.add(igra_menu);

		this.startGameMenuItem = new JMenuItem("Začni novo igro.");
		igra_menu.add(this.startGameMenuItem);
		
		this.startGameMenuItem.addActionListener(this);
		
		// View
		this.setLayout(new BorderLayout());
	}

	// MARK: - Methods

	/**
	 * Sets the view to a given view.
	 * @param view
	 */
	public void setView(JPanel view) {		
		this.removeAll();
		this.add(view);
		this.validate();
	}

	// MARK: - Events

	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		
		if (source == this.startGameMenuItem) {
			this.controller.setup();
		}
	}
}
