package logic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import util.Pair;

public class GameState {
	
	public enum StoneColor {
		BLACK, WHITE, EMPTY;
	}
	
	public enum Winner {
		BLACK, WHITE, NONE;
	}
	
	public enum Player {
		BLACK, WHITE;
	}
	
	private Player otherPlayer (Player player) {
		if (player == Player.BLACK) {return Player.WHITE;}
		else {return Player.BLACK;}
	}
	
	
	public final int size;
	public Map<Point, StoneColor> fields;
	public Winner winner;
	public Player toPlay;
	public LinkedList<Pair<Point,Player>> stones;
	
	public GameState (int size) {
		this.size = size;
		fields = new HashMap<Point, StoneColor>();
		this.populateBoard();
		winner = Winner.NONE;
		toPlay = Player.BLACK;
		stones = new LinkedList<Pair<Point,Player>>();
	}
	
	private void populateBoard () {
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				fields.put(new Point(i,j), StoneColor.EMPTY);
			}
		}
	}
	
	private Point getPoint (int x, int y) {
		return new Point(x,y);
	}
	
	private StoneColor getColor (Point p) {
		return fields.get(p);
	}
	
	private boolean moveIsValid (Point p) {
		return getColor(p) == StoneColor.EMPTY;
	}
	
	public List<Point> validMoves () {
		List<Point> points = new ArrayList<Point>();
		for (Map.Entry<Point, StoneColor> field : fields.entrySet()) {
			if (field.getValue() == StoneColor.EMPTY) {
				points.add(field.getKey());
			}
		}
		return points;
	}
	
	private boolean moveIsWinning (Point p) {
		StoneColor color;
		if (toPlay == Player.BLACK) {color = StoneColor.BLACK;}
		else {color = StoneColor.WHITE;}
		int x = p.getX();
		int y = p.getY();
		int pointerX = p.getX();
		int pointerY = p.getY();
		Function<Integer,Function<Integer,Boolean>> test;
		test = n -> m -> getColor(getPoint(n,m)) == color;		
		int counter = 1;
		// search horizontally
		while (true) {
			while (true) {
				pointerX++;
				if (test.apply(pointerX).apply(pointerY)) {counter++;}
				else {pointerX = x; pointerY = y; break;}
			}
			while (true) {
				pointerX--;
				if (test.apply(pointerX).apply(pointerY)) {counter++;}
				else {pointerX = x; pointerY = y; break;}			
			}
			if (counter == 5) {return true;}
			else {break;}	
		}
		// search vertically
		while (true) {
			while (true) {
				pointerY++;
				if (test.apply(pointerX).apply(pointerY)) {counter++;}
				else {pointerX = x; pointerY = y; break;}
			}
			while (true) {
				pointerY--;
				if (test.apply(pointerX).apply(pointerY)) {counter++;}
				else {pointerX = x; pointerY = y; break;}			
			}
			if (counter == 5) {return true;}
			else {break;}	
		}
		// search first diagonal
		while (true) {
			while (true) {
				pointerX++; pointerY++;
				if (test.apply(pointerX).apply(pointerY)) {counter++;}
				else {pointerX = x; pointerY = y; break;}
			}
			while (true) {
				pointerX--; pointerY--;
				if (test.apply(pointerX).apply(pointerY)) {counter++;}
				else {pointerX = x; pointerY = y; break;}			
			}
			if (counter == 5) {return true;}
			else {break;}	
		}
		// search second diagonal
		while (true) {
			while (true) {
				pointerX++; pointerY--;
				if (test.apply(pointerX).apply(pointerY)) {counter++;}
				else {pointerX = x; pointerY = y; break;}
			}
			while (true) {
				pointerX--; pointerY++;
				if (test.apply(pointerX).apply(pointerY)) {counter++;}
				else {pointerX = x; pointerY = y; break;}			
			}
			if (counter == 5) {return true;}
			else {break;}	
		}
		// if no 5chain is found, return false
		return false;
	}
	
	private void placeStone (Point p) {
		StoneColor stone;
		if (toPlay == Player.BLACK) {stone = StoneColor.BLACK;}
		else {stone = StoneColor.WHITE;}
		fields.put(p, stone);
		stones.addFirst(new Pair<Point,Player>(p,toPlay));
	}
	
	public boolean play (Point p) {
		if (moveIsValid(p)) {
			placeStone(p);
			if (moveIsWinning(p)) {
				Winner w;
				if (toPlay == Player.BLACK) {w = Winner.BLACK;}
				else {w = Winner.WHITE;}
				winner = w;
				toPlay = null;
			} else {
				toPlay = otherPlayer(toPlay);			
			}
			return true;
		}
		else {return false;}
	}
	
	public boolean undo (Player pl) {
		if (pl == toPlay) {
			if (stones.size() < 2) {return false;}
			else {stones.pop(); stones.pop();}
		}
		else {
			if (stones.size() < 1) {return false;}
			else {stones.pop(); toPlay = otherPlayer(toPlay);}

		}
		return true;
	}
	
	
}
