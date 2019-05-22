import java.util.ArrayList;

public abstract class Player {
	Hand hand; 
	String name;  
	int[] score;
	boolean isCurrentTurn;
	boolean isTurnComplete;
	int orientation;
	Piece currentPiece;
	int pieceX;
	int pieceY;
	int lowestScore;
	boolean isHuman;
	public Player(String name1, Hand hand1, boolean isHuman){ 
		hand = hand1;
		name = name1;
		score = new int[6];
		isTurnComplete = false;
		orientation = 0;
		this.isHuman = isHuman;
		for (int a = 0; a < 6; a ++){
			score[a] = 0;
		}
	}
	public boolean isHuman(){
		return isHuman;
	}
	public abstract void move();
	public String getName(){
		return name;
	} 

	public boolean checkHand() { // make sure that if the player has a bunch of tied scores for the lower; create second array of all the lowest colors, go through and check for each of those colors in the hand
		int lowestScore = score[0];
		// red = 0, green = 1, blue = 2, orange = 3, yellow = 4, purple = 5
		int lowestColor = 0; //lowest scoring color
		//checks for lowest scoring color
		//int lowestColors[] = new int[6];
		ArrayList<Integer> lowestColors = new ArrayList<Integer>();
		for (int count = 0; count < 6; count++) {
			if (score[count] < lowestScore) {
				lowestScore = score[count];
				lowestColor = count;
			}
		} 
		for (int count = 0; count < 6; count++) {
			if(score[count] == lowestScore)
				lowestColors.add(count);
		}
		//lowestColor (lowest scoring color) is finally determined
		for (int count = 0; count < hand.getSize(); count++) {
			for(int a = 0; a < lowestColors.size(); a++){
				//check to see if the lowest scoring color (lowestColor) is NOT in the hand/rack
				//if lowestColor is in the hand, checkHand returns false;
				if (hand.getPiece(count).getPrimaryHexagon().getColor() - 1 == lowestColors.get(a)) 
					return false;
				if (hand.getPiece(count).getSecondaryHexagon().getColor() - 1== lowestColors.get(a)) 
					return false;
			}
			
		}
		return true;
	}
	public void tradeHand(){ 
		for(int a = 0; a < 6; a ++){
			hand.getBag().addPiece(hand.removePiece(0));
		}
		hand.getBag().shuffle();
		for(int a = 0; a < 6; a ++){
			hand.addNewPiece(hand.getBag().drawPiece(0));
		}
	}
	public void tradeHandAndMaxOut(){ 
		for(int a = 0; a < 5; a ++){
			hand.getBag().addPiece(hand.removePiece(0));
		}
		hand.getBag().shuffle();
		for(int a = 0; a < 6; a ++){
			hand.addNewPiece(hand.getBag().drawPiece(0));
		}
	}
	public void setLowestScore(int s) {
		lowestScore = s;
	}
	public int getLowestScore() {
		return lowestScore;
	}
	public Hand getHand(){
		return hand;
	}
	public void updateScore(int[] score){
		for(int a = 0; a < this.score.length; a ++){
			this.score[a] += score[a];
			if(this.score[a] > 18){
				this.score[a] = 18;
			}
		}
	}
	public int[] getScores(){return score;}
	public boolean getCurrentTurn()
	{
		return isCurrentTurn;
	}
	public void setCurrentTurn(boolean bool){
		isCurrentTurn = bool;
	}
	public boolean getTurnComplete()
	{
		return isTurnComplete;
	}
	public void setTurnComplete(boolean bool){
		isTurnComplete = bool;
	}

	public int getOrientation(){
		return orientation;
	}
	public void setOrientation(int orientation){
		this.orientation = orientation;
	}
	public int getPieceX(){
		return pieceX;
	}
	public int getPieceY(){
		return pieceY;
	}
	public Piece getCurrentPiece(){
		return currentPiece;
	}
	public void removeCurrentPiece(){
		currentPiece = null;
	}
	public void addNewPiece(){
		getHand().addNewPiece(getHand().getBag().drawPiece(0));
	}
	public void resetDefault(){
		pieceX = -1;
		pieceY = -1;
		
	}

}
