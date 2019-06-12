package ingenious;
import java.util.ArrayList;
import java.util.Arrays;

public class VeryGreedyStrategy extends Strategy
{
	private Piece piece;
	private int xCoord;
	private int yCoord;
	private int orientation;
	private int pieceIndex;
	private int[][] tempGrid;

	VeryGreedyStrategy(Game g) {
		super(g);
	}

	private void makeTempGrid(int o, int x, int y, int color1, int color2) {
		tempGrid = new int[30][15];
		for (int X = 0; X < 30; X++) {
			for (int Y = 0; Y < 15; Y++) {
				if (game.twoHexGrid(o, x, y, color1, color2)[X][Y] == 0) {
					tempGrid[X][Y] = game.grid[X][Y];
				} else {
					tempGrid[X][Y] = game.twoHexGrid(o, x, y, color1, color2)[X][Y];
				}
			}
		}
	}

	public void calculateMove(Hand h, int[] score) {
		int highestScore = 0;
		int highestX = 0;
		int highestY = 0;
		int highestOrientation = 0;
		int highestPieceIndex = 0;

		Hand hand = game.getCurrentPlayer().getHand();
		game.getCurrentPlayer().tradeHand();
		for (int x = 0; x < 30; x++) {
			for (int y = 0; y < 15; y++) {
				for (int o = 0; o < 6; o++) {
					for (int piece = 0; piece < game.currentPlayer.getHand().getSize(); piece++) {
						int color1 = game.currentPlayer.getHand().getPiece(piece).getPrimaryHexagon().getColor();
						int color2 = game.currentPlayer.getHand().getPiece(piece).getSecondaryHexagon().getColor();
						// makeTempGrid(o, x, y, color1, color2);
						if (game.checkLegalMove(o, x, y, color1, color2))
						{
							makeTempGrid(o, x, y, color1, color2);
							int newScore = game.score(x, y, tempGrid);
							if(newScore > highestScore)
							{
								highestScore = newScore;
								highestX = x;
								highestY = y;
								highestOrientation = o;
								highestPieceIndex = piece;
							}
						}
					}
				}
			}
		}

		// System.out.println("High Score" + highestScore);
		pieceIndex = highestPieceIndex;
		piece = hand.getPiece(pieceIndex);
		xCoord = highestX;
		yCoord = highestY;
		orientation = highestOrientation;
//		makeTempGrid(highestOrientation, highestX, highestY, hand.getPiece(pieceIndex).getPrimaryHexagon().getColor(),
//				hand.getPiece(pieceIndex).getSecondaryHexagon().getColor());
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
