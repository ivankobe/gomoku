package logic;

import java.util.HashMap;
import java.util.Map;

public class Test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Point p = new Point(0,0);
		Point q = new Point(0,0);
		Map<Point, String> h = new HashMap<Point, String>();
		h.put(p,"bla");
		h.put(q,"blabla");
		System.out.println(p.equals(q));
		System.out.println(h.get(q));
		System.out.println(h.get(p));
		GameState g = new GameState(19);
	}

}
