import java.util.ArrayList;
import java.util.Collections;
public class GrabBag 
{

	private ArrayList<Piece> Pieces=new ArrayList<Piece>();
	GrabBag()
	{
		createPieces();
		Collections.shuffle(Pieces);
	}
	public void shuffle(){
		Collections.shuffle(Pieces);
	}
	public void createPieces()
	{
		//creates the pieces and adds to pieces arraylist
		for (int counter=5;counter>=0;counter--)
		{
			for (int counter2=counter;counter2>=0;counter2--)
			{
				if (counter!=counter2)
				{
			
					for (int counter3=0;counter3<6;counter3++)
					{
						Piece piece=new Piece (counter + 1,counter2 + 1);
						Pieces.add(piece);
					}
				}
				else
				{
					for (int counter3=0;counter3<5;counter3++)
					{
						Piece piece = new Piece(counter + 1,counter2 + 1);
						Pieces.add(piece);
					}
				}

			}
		}

	} 
	public Piece drawPiece(int i)
	{
		return (Pieces.remove(i));
	}
	public ArrayList<Piece> getBag()
	{
		return Pieces;
	}
	public void addPiece(Piece p)
	{
		Pieces.add(p);
	}
}
