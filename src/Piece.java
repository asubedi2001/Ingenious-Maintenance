public class Piece {
	Hexagon hex1, hex2;
	final int  red = 0, green = 1, blue = 2, orange = 3, yellow = 4, purple = 5;
	Piece(Hexagon hexOne, Hexagon hexTwo) {
		hex1 = hexOne;
		hex2 = hexTwo;
	}
	Piece(int color1,int color2){
		hex1 = new Hexagon(color1);
		hex2 = new Hexagon(color2);
	}
	public Hexagon getPrimaryHexagon() {
		return hex1;
	}
	public Hexagon getSecondaryHexagon() {
		return hex2;
	}
}
