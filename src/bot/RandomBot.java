package bot;

import java.util.Random;

import logic.GameState;
import logic.GameState.Player;

public class RandomBot {

	public final GameState game;
	public final Player player;
	
	public RandomBot (GameState g, Player pl) {
		player = pl;
		game = g;
	}
	
	public void play () {
		int index = (int) (Math.random() * game.size);
		game.play(game.validMoves().get(index));
	}
	
}
