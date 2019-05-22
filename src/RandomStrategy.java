import java.lang.Math;
public class RandomStrategy extends Strategy{
	private Piece piece;
	private int xCoord;
	private int yCoord;
	private int orientation;
	private int pieceIndex;
	RandomStrategy(Game g) {
		super(g);
	}
	public void calculateMove(Hand h, int[] score) {
		boolean legalMove = false;
		if (game.getCurrentPlayer().checkHand() && game.getCurrentPlayer().getHand().getSize() == 6) {
			game.getCurrentPlayer().tradeHand();
		}
		do {
			xCoord = (int)(Math.random() * 30);
			yCoord = (int)(Math.random() * 15);
			orientation = ((int)(Math.random()*6));
			if (checkLegalMove(xCoord, yCoord)) {
				legalMove = true; 
				pieceIndex = (int)(Math.random()*h.getSize());
				piece = h.getPiece(pieceIndex);
			}
		}while (!legalMove); 
	}
	public boolean checkLegalMove(int CoordX, int CoordY) {
		if (orientation==0) {
			if (CoordX > 0 && CoordY > 0) {
				if (game.grid[CoordX][CoordY]==-1 && game.grid[(CoordX-1)][(CoordY-1)]==-1) {
					return true; 
				}
			}
		}
		else if (orientation==1) {
			if (CoordX < 29 && CoordY > 0) {
				if (game.grid[CoordX][CoordY]==-1 && game.grid[(CoordX+1)][(CoordY-1)]==-1) {
					return true;
				}
			}
		}
		else if (orientation==2) {
			if (CoordX < 28) {
				if (game.grid[CoordX][CoordY]==-1 && game.grid[(CoordX+2)][(CoordY)]==-1) {
					return true;
				}
			}
		}
		else if (orientation==3) {
			if (CoordX < 29 && CoordY < 14)
				if (game.grid[CoordX][CoordY]==-1 && game.grid[(CoordX+1)][(CoordY+1)]==-1) {
					return true;
				}
			
		}
		else if (orientation==4) {
			if (CoordX > 0 && CoordY < 14) {
				if (game.grid[CoordX][CoordY] == -1 && game.grid[(CoordX - 1)][(CoordY + 1)] == -1) {
					return true;
				}
			}
		}
		else if (orientation==5) {
			if (CoordX > 1) {
				if (game.grid[CoordX][CoordY] == -1 && game.grid[(CoordX - 2)][(CoordY)] == -1) {
					return true;
				}
			}
		}
		return false;
	}
	public int getPieceIndex(){
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
