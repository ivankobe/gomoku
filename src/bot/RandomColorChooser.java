package bot;

import java.util.Random;

import logic.GameState.Player;

public class RandomColorChooser {
	
	private final Random rand = new Random();
	private final int randBin = rand.nextInt(2);
	
	public RandomColorChooser () {}
	
	public Player getColor () {
		if (randBin == 0) {return Player.BLACK;}
		else {return Player.WHITE;}
	}
	
}
