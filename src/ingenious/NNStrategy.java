package ingenious;
import java.lang.Math;

public class NNStrategy extends Strategy
{
	private Piece piece;
	private int xCoord;
	private int yCoord;
	private int orientation;
	private int pieceIndex;

	NNStrategy(Game g) {
		super(g);
	}

	public void calculateMove(Hand h, int[] score) {
		boolean legalMove = false;
		if (game.getCurrentPlayer().checkHand() && game.getCurrentPlayer().getHand().getSize() == 6) {
			game.getCurrentPlayer().tradeHand();
		}

		xCoord = (int) (Math.random() * 30);
		yCoord = (int) (Math.random() * 15);
		orientation = ((int) (Math.random() * 6));
		pieceIndex = (int) (Math.random() * h.getSize());
		piece = h.getPiece(pieceIndex);
		System.out.println("d");
	}

	public int getPieceIndex() {
		return pieceIndex;
	}

	public Piece getPiece() {
		return piece;
	}

	public int getXCoordinate() {
		return xCoord;
	}

	public int getYCoordinate() {
		return yCoord;
	}

	public int getOrientation() {
		return orientation;
	}
}
