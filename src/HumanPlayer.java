
public class HumanPlayer extends Player{
	static int CLOCKWISE = 1;
	static int COUNTERCLOCKWISE = -1;
	
	

	HumanPlayer(String name1, Hand hand1){
		super(name1, hand1, true);
		setOrientation(0);
	}


	public void move() {
//		try {
//			Thread.sleep(0);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}

	public void rotate(int direction){
		if(direction == CLOCKWISE){
			if(getOrientation() < 5){
				setOrientation(getOrientation() + 1);
			}else{
				setOrientation(0);
			}
		}else if(direction == COUNTERCLOCKWISE){
			if(getOrientation() > 0){
				setOrientation(getOrientation() - 1);
			}else{
				setOrientation(5);
			}
		}
	}
	public void selectPiece(int index){
		currentPiece = getHand().removePiece(index);
		
	}
	public void deselect(){
		getHand().addNewPiece(currentPiece);
		currentPiece = null;
	}
	public void setPieceX(int x){
		pieceX = x;
	}
	public void setPieceY(int y){
		pieceY = y;
	}
	public void setPieceCoordinate(int x, int y){
		pieceX = x;
		pieceY = y;
	}
	
}
