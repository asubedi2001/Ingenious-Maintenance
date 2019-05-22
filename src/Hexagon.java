// red = 0, green = 1, blue = 2, orange = 3, yellow = 4, purple = 5, white = 6, grey = 7, dark grey = 8
public class Hexagon {
	private int color; 
	Hexagon(int colorNum) {
		color = colorNum;
	}
	public int getColor() {
		return color;
	}
	public void setColor(int colorNum) {
		color = colorNum;
	}
}
