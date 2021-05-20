package view;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.Point;

import javax.swing.JLabel;
import javax.swing.JPanel;

import controller.IGameController;
import controller.IGameView;

/**
 * GameView is in charge of displaying a single game and listens for user input.
 */

@SuppressWarnings("serial")
public class GameView extends JPanel implements IGameView {

	// MARK: - State

	private IGameController controller;

	// MARK: - Components

	private BoardView board;
	private JLabel status;

	// MARK: - Constructor

	public GameView(IGameController controller) {
		// Status
		this.status = new JLabel();
		this.status.setFont(new Font(status.getFont().getName(), status.getFont().getStyle(), 20));

		GridBagConstraints status_layout = new GridBagConstraints();
		status_layout.gridx = 0;
		status_layout.gridy = 0;
		status_layout.anchor = GridBagConstraints.CENTER;
		this.add(this.status, status_layout);

		// Board
		this.board = new BoardView(controller);

		GridBagConstraints board_layout = new GridBagConstraints();
		board_layout.gridx = 0;
		board_layout.gridy = 1;
		board_layout.fill = GridBagConstraints.BOTH;
		board_layout.weightx = 1.0;
		board_layout.weighty = 1.0;

		this.add(this.board, board_layout);
	}

	// MARK: - Accessors

	/**
	 * Returns information about the stone with a given index.
	 */
	public Point point(int n) {
		return this.board.point(n);
	}

	/**
	 * Returns the board view.
	 */
	public JPanel board() {
		return this.board;
	}

	// MARK: - Methods

	@Override
	public void repaint() {
		if (this.controller == null)
			return;

		// Update status
		String message = "";

		status: switch (this.controller.state()) {
		case IN_PROGRESS:
			switch (this.controller.player()) {
			case Black:
				message = this.controller.black().name() + " na potezi...";
				break status;
			case White:
				message = this.controller.white().name() + " na potezi...";
				break status;
			}
		case WIN_Black:
			message = this.controller.black().name() + " zmagal!";
			break;
		case WIN_White:
			message = this.controller.white().name() + " zmagal!";
			break;
		case DRAW:
			message = "Igra neodločena.";
			break;
		}

		System.out.println(message);
		this.status.setText(message);
	}
}
