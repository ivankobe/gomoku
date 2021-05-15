import view.Window;

/**
 *	This is the main file that starts the game. Here, we bridge together
 *	the view, the model of our game and the controller that leads the game.
 */

public class Gomoku {
	public static void main(String[] args) {
		Window okno = new Window();
		okno.pack();
		okno.setVisible(true);
	}
}
