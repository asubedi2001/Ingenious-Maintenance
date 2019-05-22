public abstract class Strategy {
	Game game;
	Strategy(Game g){
		game = g;
	}
	abstract public void calculateMove(Hand h, int[] score);
	abstract public Piece getPiece();
	abstract public int getPieceIndex();
	abstract public int getXCoordinate();
	abstract public int getYCoordinate();
	abstract public int getOrientation();
}

