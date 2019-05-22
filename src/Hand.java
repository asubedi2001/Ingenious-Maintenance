import java.util.ArrayList;
import java.lang.Math;
public class Hand {
	//player refills hand and creates 
	private ArrayList<Piece> pieces=new ArrayList<Piece>(); 
	GrabBag bag;
	Hand(GrabBag bag)
	{
		this.bag=bag;
		for (int counter=0;counter<6;counter++)
		{
			this.addNewPiece(bag.drawPiece(counter));
		}
	}
	public Piece removePiece(int index) {
		return pieces.remove(index);
	} 
	public void addNewPiece(Piece piece) {
		pieces.add(piece);
	}
	public Hand getHand() {

		return this;
	}
	public Piece getPiece(int pieceIndex) {
		return pieces.get(pieceIndex);
	}
	public ArrayList<Piece> getPieces()
	{
		return pieces;
	}
	public GrabBag getBag(){
		return bag;
	}
	public int getSize(){
		return pieces.size();
	}

}



