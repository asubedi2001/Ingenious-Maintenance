
public class ComputerPlayer extends Player{
	String compName;
	Strategy compStrategy;
	public ComputerPlayer(String name, Strategy s, Hand h) {
		super(name, h, false);
		compName = name;
		compStrategy = s;
	}
	public void move(){
		compStrategy.calculateMove(hand, score);
		currentPiece = compStrategy.getPiece();
		hand.removePiece(compStrategy.getPieceIndex());
		pieceX = compStrategy.getXCoordinate();
		pieceY = compStrategy.getYCoordinate();
		orientation = compStrategy.getOrientation();
	}
	public Piece getCurrentPiece(){
		return currentPiece;
	}
	public int getPieceX(){
		return pieceX;
	}
	public int getY(){
		return pieceY;
	}
	public int getOrientation(){
		return orientation;
	}
}


