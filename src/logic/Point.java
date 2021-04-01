package logic;

public final class Point {
	
	private final int x;
	private final int y;
	private final int hashCode;
	private final String toString;
	
	public Point (int x, int y) {
		this.x = x; this.y = y;
		this.hashCode = calculateHashCode();
		this.toString = "Point at (" + x + "," + y + ")";
	}
	
	public int getX () {
		return this.x;
	}
	
	public int getY () {
		return this.y;
	}
	
	private int calculateHashCode () {
		return ((x + y) * (x + y + 1) / 2) + y;
	}
	
	@Override
	public boolean equals(Object obj) {
	    if (obj instanceof Point) {
	        Point o = (Point) obj;
	        return this.x == o.x && this.y == o.y;
	    }
	    return false;
	}
	
	@Override 
	public int hashCode () {
		return this.hashCode;
	}
	
	@Override
	public String toString () {
		return toString;
	}
	
}
