package util;

import java.util.HashMap;
import java.util.Map;

import bot.RandomBot;
import logic.GameState;
import logic.GameState.Player;
import logic.GameState.Winner;

public class Test {

	public static void main(String[] args) {
		
		int crni = 0;
		int beli = 0;
		
		for (int i = 0; i < 10000; i++) {
		GameState g = new GameState(19);
		RandomBot bot1 = new RandomBot(g, Player.BLACK);
		RandomBot bot2 = new RandomBot(g, Player.WHITE);
		
		while (g.winner == Winner.NONE) {
			if (g.toPlay == Player.BLACK) {bot1.play();}
			else {bot2.play();}
		}
		
		if (g.winner == Winner.BLACK) {crni++;} else {beli++;}
		
		
		}
		
		System.out.println("Črni : beli = " + crni + " : " + beli);
		
	}

}
