package view;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import logika.GameSettings;
import logika.Player;

/**
 * This file contains code used to customize a game. It consists of two player
 * settings and a start button below them. There's also a larget title at the
 * top.
 */

@SuppressWarnings("serial")
public class GameSettingsView extends JPanel implements ActionListener {

	// MARK: - State
	
	private GameSettings settings;
	
	// MARK: - Components
	
	private PlayerSettingsView white;
	private PlayerSettingsView black;
	
	private JButton start;

	// MARK: - Constructor

	public GameSettingsView(GameSettings settings) {
		super();

		// State
		this.settings = settings;

		// Layout
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		this.add(new JLabel("GOMOKU"));
		
		JPanel players = new JPanel();
		players.setLayout(new BoxLayout(players, BoxLayout.X_AXIS));

		this.white = new PlayerSettingsView(this.settings.white);
		this.black = new PlayerSettingsView(this.settings.black);
		
		players.add(this.white);
		players.add(this.black);
		
		this.add(players);
		
		this.start = this.button(this, "Začni");
	}

	// MARK: - View
	
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
		Object source = e.getSource();
		
		if (source == this.start) {
			
		}
	}
}

// MARK: - Player

@SuppressWarnings("serial")
class PlayerSettingsView extends JPanel implements ActionListener, ChangeListener, ItemListener {

	private Player player;

	private JTextField name;
	private JCheckBox computer;
	private JColorChooser color;

	// MARK: - Constructor

	public PlayerSettingsView(Player player) {
		this.player = player;

		// Layout
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		this.name = this.input(this, this.player.name, "Ime");
		this.computer = this.checkbox(this, "Računalnik");
		this.color = this.color(this, this.player.color, "Barva");
	}

	// MARK: - Events

	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();

		// Sources
		if (source == this.name) {
			this.player.name = this.name.getText();
		}

	}

	public void itemStateChanged(ItemEvent e) {
		Object source = e.getItemSelectable();

		// Sources
		if (source == this.computer) {
			this.player.computer = e.getStateChange() == ItemEvent.SELECTED;
		}

//		this.repaint();
	}

	public void stateChanged(ChangeEvent e) {
		Object source = e.getSource();

		// Sources
		if (source == this.color) {
			this.player.color = this.color.getColor();
		}

//		this.repaint();
	}

	// MARK: - Methods

	/**
	 * Creates a new label and adds it to the view.
	 * 
	 * @param panel
	 * @param label
	 * @return
	 */
	private JLabel label(JPanel panel, String label) {
		JLabel component = new JLabel(label);

		panel.add(component);

		return component;
	}

	/**
	 * Creates a new checkbox with a label.
	 * 
	 * @param panel
	 * @param label
	 * @return
	 */
	private JCheckBox checkbox(JPanel panel, String label) {
		JCheckBox box = new JCheckBox(label);

		box.setSelected(false);
		box.addActionListener(this);
		panel.add(box);

		return box;
	}

	/**
	 * Creates a text input field with a given label.
	 * 
	 * @param panel
	 * @param init
	 * @param label
	 * @return
	 */
	private JTextField input(JPanel panel, String init, String label) {
		JTextField field = new JTextField(init);

		field.addActionListener(this);
		panel.add(field);

		return field;
	}

	/**
	 * Creates a color chooser field with a given label.
	 * 
	 * @param panel
	 * @param init
	 * @param label
	 * @return
	 */
	private JColorChooser color(JPanel panel, Color init, String label) {
		JColorChooser chooser = new JColorChooser(init);

		chooser.getSelectionModel().addChangeListener(this);
		panel.add(chooser);

		return chooser;
	}
}
