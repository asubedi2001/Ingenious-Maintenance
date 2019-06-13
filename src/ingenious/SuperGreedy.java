package ingenious;

import java.util.ArrayList;


/* If the board is filled 60% of the way through, use Greedy Strategy,
if the board is less than 60% of the way completed just increase by whatever gives you the most points.*/
public class SuperGreedy extends Strategy{

	private Piece piece;
	private int xCoord;
	private int yCoord;
	private int orientation;
	private int pieceIndex;
	private int[][] tempGrid;

	SuperGreedy(Game g) {
		super(g);
	}

	private void makeTempGrid(int o, int x, int y, int color1, int color2) {
		tempGrid = new int[30][15];
		for (int X = 0; X < 30; X++) {
			for (int Y = 0; Y < 15; Y++) {
				System.out.println("(X,Y): " + game.grid[X][Y]);
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
		int lowestScore = game.getCurrentPlayer().getScores()[0];
	

		for (int a = 0; a < 6; a++) {
			if (game.getCurrentPlayer().getScores()[a] < lowestScore) {
				lowestScore = game.getCurrentPlayer().getScores()[a]; // calculates the lowest score
																	  // from all of the scores of that one player
			}
		}
		
		if(game.getPlayers().length == 2) {
			
		}
		
		ArrayList<Integer> lowestColor = new ArrayList<Integer>();
		ArrayList<Integer> oldColors = new ArrayList<Integer>();
		
		for (int a = 0; a < 6; a++) {
			if (game.getCurrentPlayer().getScores()[a] == lowestScore) {
				lowestColor.add(a + 1); // if there are multiple lowest scores, it adds it to the 
			}
		}
		Hand hand = game.getCurrentPlayer().getHand();
		if (game.getCurrentPlayer().checkHand() && game.getCurrentPlayer().getHand().getSize() == 6) {
			game.getCurrentPlayer().tradeHand();
		}
		boolean isColor1 = false, isColor2 = false;
		boolean isMove = false;
		do {
			isMove = false;
			for (int x = 0; x < 30; x++) {
				for (int y = 0; y < 15; y++) {
					for (int o = 0; o < 6; o++) {
						for (int piece = 0; piece < game.currentPlayer.getHand().getSize(); piece++) {
							int color1 = game.currentPlayer.getHand().getPiece(piece).getPrimaryHexagon().getColor();
							int color2 = game.currentPlayer.getHand().getPiece(piece).getSecondaryHexagon().getColor();
							isColor1 = false;
							isColor2 = false;
							for (int i = 0; i < lowestColor.size(); i++) {
								if (color1 == lowestColor.get(i)) {
									isColor1 = true;
								}
								if (color2 == lowestColor.get(i)) {
									isColor2 = true;
								}
							}
							// makeTempGrid(o, x, y, color1, color2);
							if ((isColor1 || isColor2) && game.checkLegalMove(o, x, y, color1, color2)) {
								isMove = true;
								makeTempGrid(o, x, y, color1, color2);
								highestScore = game.score(x, y, tempGrid);
								highestX = x;
								highestY = y;
								highestOrientation = o;
								highestPieceIndex = piece;
							}
						}
					}
				}
			}
			if (!isMove) {
				ArrayList<Integer> newLowestColor = new ArrayList<Integer>();
				lowestScore = 19;
				
				for (int i = 0; i < lowestColor.size(); i++) {// oldColors is not lowestColor
					oldColors.add(lowestColor.get(i));
				}
				
				boolean use = true;
				for (int a = 0; a < 6; a++) {
					use = true;
					for (int i = 0; i < oldColors.size(); i++) {
						if (a + 1 == oldColors.get(i)) {
							use = false;
						}
					}
					
					if (use && game.getCurrentPlayer().getScores()[a] < lowestScore) {
						lowestScore = game.getCurrentPlayer().getScores()[a];
					}
				}
				
				use = true;
				for (int a = 0; a < 6; a++) {
					use = true;
					for (int i = 0; i < oldColors.size(); i++) {
						if (a + 1 == oldColors.get(i)) {
							use = false;
						}
					}
					if (use && game.getCurrentPlayer().getScores()[a] == lowestScore) {
						newLowestColor.add(a + 1);
					}
				}
				lowestColor = newLowestColor;
			}
		} while (!isMove);

		for (int x = 0; x < 30; x++) {
			for (int y = 0; y < 15; y++) {
				for (int o = 0; o < 6; o++) {
					for (int piece = 0; piece < game.currentPlayer.getHand().getSize(); piece++) {
						int color1 = game.currentPlayer.getHand().getPiece(piece).getPrimaryHexagon().getColor();
						int color2 = game.currentPlayer.getHand().getPiece(piece).getSecondaryHexagon().getColor();
						isColor1 = false;
						isColor2 = false;
						for (int i = 0; i < lowestColor.size(); i++) {
							if (color1 == lowestColor.get(i)) {
								isColor1 = true;
							}
							if (color2 == lowestColor.get(i)) {
								isColor2 = true;
							}
						}
						if ((isColor1 || isColor2) && game.checkLegalMove(o, x, y, color1, color2)) {
							makeTempGrid(o, x, y, color1, color2);
							if (isColor1 && isColor2) {
								if (game.score(x, y, tempGrid) > highestScore || game.score(game.getSecondX(o, x, y),
										game.getSecondY(o, x, y), tempGrid) > highestScore) {
									highestScore = game.score(x, y, tempGrid);
									highestX = x;
									highestY = y;
									highestOrientation = o;
									highestPieceIndex = piece;
								}
							} else if (isColor1) {
								if (game.score(x, y, tempGrid) > highestScore) {
									highestScore = game.score(x, y, tempGrid);
									highestX = x;
									highestY = y;
									highestOrientation = o;
									highestPieceIndex = piece;
								}
							} else if (isColor2) {
								if (game.score(game.getSecondX(o, x, y), game.getSecondY(o, x, y),
										tempGrid) > highestScore) {
									highestScore = game.score(x, y, tempGrid);
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
		}
		pieceIndex = highestPieceIndex;
		piece = hand.getPiece(pieceIndex);
		xCoord = highestX;
		yCoord = highestY;
		orientation = highestOrientation;
		makeTempGrid(highestOrientation, highestX, highestY, hand.getPiece(pieceIndex).getPrimaryHexagon().getColor(),
				hand.getPiece(pieceIndex).getSecondaryHexagon().getColor());
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
