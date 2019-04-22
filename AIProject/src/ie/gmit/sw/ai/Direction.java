package ie.gmit.sw.ai;

// enum for players movement 
public enum Direction {
	UP (0), 
	DOWN (1), 
	LEFT (2), 
	RIGHT (3);

	private final int orientation;

	// getter and setter 
    private Direction(int orientation) {
        this.orientation = orientation;
    }
    
    public int getOrientation() {
        return this.orientation;
    }
}