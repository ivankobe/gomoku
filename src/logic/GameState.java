package logic;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public class GameState {
	
	private enum StoneColor {
		BLACK, WHITE, EMPTY;
	}
	
	private enum Winner {
		BLACK, WHITE, NONE;
	}
	
	private enum ToPlay {
		BLACK, WHITE, NONE;
	}
	
	private ToPlay otherPlayer (ToPlay player) {
		if (player == ToPlay.BLACK) {return ToPlay.WHITE;}
		else {return ToPlay.BLACK;}
	}
	
	
	private final int size;
	private Map<Point, StoneColor> fields;
	private Winner winner;
	private ToPlay toPlay;
	
	public GameState (int size) {
		this.size = size;
		fields = new HashMap<Point, StoneColor>();
		this.populateBoard();
		winner = Winner.NONE;
		toPlay = ToPlay.BLACK;
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
		List<Point> points = new LinkedList<Point>();
		for (Map.Entry<Point, StoneColor> field : fields.entrySet()) {
			if (field.getValue() == StoneColor.EMPTY) {
				points.add(field.getKey());
			}
		}
		return points;
	}
	
	private boolean moveIsWinning (Point p) {
		StoneColor color;
		if (toPlay == ToPlay.BLACK) {color = StoneColor.BLACK;}
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
		if (toPlay == ToPlay.BLACK) {stone = StoneColor.BLACK;}
		else {stone = StoneColor.WHITE;}
		fields.put(p, stone);
	}
	
	public boolean play (int n, int m) {
		Point p = new Point(n,m);
		if (moveIsValid(p)) {
			placeStone(p);
			if (moveIsWinning(p)) {
				Winner w;
				if (toPlay == ToPlay.BLACK) {w = Winner.BLACK;}
				else {w = Winner.WHITE;}
				this.winner = w;
				toPlay = ToPlay.NONE;
			} else {
				toPlay = otherPlayer(toPlay);			
			}
			return true;
		}
		else {return false;}
	}
	
	
}
