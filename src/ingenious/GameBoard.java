/*
 * In case of Connor's untimely death:
 * 100:87 is the ratio between radius of a hexagon from the point to the radius from the side
 * 45 seems to be the functional radius of the current hexagons, even though it is set to 50
 * The makeHexagon method is borrowed from the internet, doesn't work very well, and should be repealed and replaced.
 * The Polygon array is patterned to have null spaces to represent absent hexagons, i.e. 0,null,2,null...
 *  orange = 1, yellow = 2, purple = 3, red = 4, green = 5, blue = 6
 *
//getGridType()
/*To do
 * Yellow Highlighting:DONE
 * Red outlines: slow mode: DONE?
 * Fix bolded hex borders:DONE
 * Colored Pieces in corners:DONE
 * Make it able to paint any color: DONE
 * UpdateGrid: take 2d array and setHex color to it and reset the grid:DONE
 * Make 2d array of hand hexes and detect clicks on them
 * Piece, Score
 */
//getScore() pass x coordinate
//primary hexagon


import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Rectangle2D;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

public class GameBoard extends JPanel implements Runnable,MouseListener,MouseMotionListener{
	private Polygon[][] hexagon = new Polygon[30][15];
	private Polygon[][] handPieces = new Polygon[6][2];
	private int[][] gameBoardTempGrid;
	private Polygon piece;
	private int[][]hexColor = new int[30][15]; //Contains value representing color of a hex on the grid - for actual game, not initializing board
	private int width = 1500;
	private int length = 800;
	private int X, Y, stoX, stoY;
	private boolean isHumanPlayer;
	private Rectangle2D rotateClockwise = new Rectangle2D.Double(width - 175, length -175, 175, 175);
	private Rectangle2D rotateCounterClockwise = new Rectangle2D.Double(width/3 , length -175, 175, 175);
	private Rectangle2D returnPiece = new Rectangle2D.Double(width - 175, 0, 175, 175);
	private Rectangle2D scoreBox = new Rectangle2D.Double(width/3, 0, 175, 175);
	private Game game;
	private int orientation;
	private Color[] colors=new Color[]{Color.ORANGE,Color.YELLOW,Color.MAGENTA,Color.RED,Color.GREEN,Color.BLUE};
	private int[] colorcoord=new int[]{1,2,3,4,5,6};
	private int score1;
	private int score2;
	private int[][] computerGrid;
	boolean enabled = true;
	private boolean slowMode;
	private static Action returnAction;
	private static Action rotateClockwiseAction;
	private static Action rotateCounterClockwiseAction;

	// keyboard inputs caused a lot of new bugs, so this is disabled by default
	private final boolean keyboard_inputs = false;

	public GameBoard(Game game){ //JFrame made in tester class
		this.game = game;
		setBackground(Color.WHITE);
		setVisible(true);
		setPreferredSize(new Dimension(width,length));
		addMouseListener(this);
		addMouseMotionListener(this);

		if (keyboard_inputs)
		{

		//Add keyboard shortcuts to rotate and return piece start
		//Using key Bindings, first to get current window focus
		int mapName = JComponent.WHEN_IN_FOCUSED_WINDOW;

		returnAction = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				getGame().deselect();
				getGame().getCurrentPlayer().setOrientation(0);
				getGame().getCurrentPlayer().resetDefault();
				repaint();
			}
		};

		rotateClockwiseAction = new AbstractAction(){
			public void actionPerformed(ActionEvent e) {
				rotate(1);
				getGame().getCurrentPlayer().resetDefault();
				repaint();
			}
		};

		rotateCounterClockwiseAction = new AbstractAction(){
			public void actionPerformed(ActionEvent e) {
				rotate(-1);
				getGame().getCurrentPlayer().resetDefault();
				repaint();
			}
		};

		this.getInputMap(mapName).put( KeyStroke.getKeyStroke( "F1" ), "doReturnAction" );
		this.getActionMap().put( "doReturnAction", returnAction );
		this.getInputMap(mapName).put( KeyStroke.getKeyStroke( "F2" ), "doRotateClockwiseAction" );
		this.getActionMap().put( "doRotateClockwiseAction", rotateClockwiseAction );
		this.getInputMap(mapName).put( KeyStroke.getKeyStroke( "F3" ), "doRotateCounterClockwiseAction" );
		this.getActionMap().put( "doRotateCounterClockwiseAction", rotateCounterClockwiseAction );
		//Add keyboard shortcuts to rotate and return piece end

		}

		initializeGrid();
		makeBoard(game.numPlayers() - 2);

		makeHand();
		//		for(int y = 0; y < 15; y++){ // For debugging prints backwards because I done goofed.
		//			System.out.println("");
		//			for(int x = 0; x < 30;x++){
		//				//System.out.print(hexColor[x][y]);
		//			}
		//		}
		computerGrid = new int[30][15];
		for(int y = 0; y < 15; y++){ // For debugging prints backwards because I done goofed.
			for(int x = 0; x < 30;x++){
				computerGrid[x][y] = 0;
			}
		}
	}

	public void setSlowMode(boolean b){
		slowMode = b;
	}
	public boolean getSlowMode(){
		return slowMode;
	}
	//gmae.getCUrrentPiece.getPrimaryHexagon.getCOlor();
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		try{
			Graphics2D g2d = (Graphics2D)g;
			g.setColor(Color.BLACK);
			g2d.setFont(new Font("Futura", Font.BOLD,14));
			g2d.draw(rotateClockwise);
			g2d.drawString("Rotate Clockwise", width - 175, length - 175);
			g2d.draw(rotateCounterClockwise);
			g2d.drawString("Rotate Counter Clockwise", width/3 , length - 175);
			g2d.draw(returnPiece);
			g2d.drawString("Return Piece", width - 175, 175);
			g2d.draw(scoreBox);
			g2d.drawString(game.getCurrentPlayer().getName(), width - 1450, length - 160);
			g.drawLine(500,0,500,800);//separates 3rd of board from rest. should paint the side components to the left of it.
			g.drawRect(50,650,400,140);
			paintScore(g);
			g.setColor(pickColor(game.getCurrentPlayer().getCurrentPiece().getPrimaryHexagon().getColor()));
			g.drawPolygon(makeHex(width/3 + 40, 50,30)); //getColor or whatever for hexagon
			g.fillPolygon(makeHex(width/3 + 40, 50,30)); //getColor or whatever for hexagon
			if(game.getCurrentPlayer().getCurrentPiece().getPrimaryHexagon().getColor() == game.getCurrentPlayer().getCurrentPiece().getSecondaryHexagon().getColor()){
				score1 = score1 + score2;
				g.setColor(Color.BLACK);
				g2d.drawString(Integer.toString(score1), width/3 +80, 54); //getScore for the string or whatever
				score1 = 0;
				score2 = 0;
			}else{
				g.setColor(Color.BLACK);
				g2d.drawString(Integer.toString(score1), width/3 +80, 54); //getScore for the string or whatever
				g2d.drawString(Integer.toString(score2), width/3 +80, 129);
				g.setColor(pickColor(game.getCurrentPlayer().getCurrentPiece().getSecondaryHexagon().getColor()));
				g.drawPolygon(makeHex(width/3 + 40, 125,30));
				g.fillPolygon(makeHex(width/3 + 40, 125,30));
				score1 = 0;
				score2 = 0;
			}
		}catch(Exception e){}
		paintBoard(g);
		//horizontalLines(g);
		//verticalLines(g);
		//paintHand(g);
	}
	public int[][] getHexColor(){
		return hexColor;
	}
	public void rotate(int direction){
		game.rotate(direction);
	}
	private void makeHand(){
		//score drawing below
		int c=85;
		for(int x = 0; x < 6; x++){
			handPieces[x][0] = makeScoreHex(c, 693,30);
			c+=65;
		}
		c = 85;
		for(int x = 0; x < 6; x++){
			handPieces[x][1] = makeScoreHex(c, 745,30);
			c+=65;
		}
	}
	private void paintScore(Graphics g){
		horizontalLines(g);
		verticalLines(g);
		//score drawing below
		g.drawRect(50,650,400,140);
		int c=85;
		if(game.getCurrentPlayer().isHuman()){
			for(int counter = 0; counter < game.getCurrentPlayer().getHand().getSize(); counter++){ // <--GET HUMAN PLAYERS HAND
				int color=game.getCurrentPlayer().getHand().getPiece(counter).getSecondaryHexagon().getColor();
				g.setColor(pickColor(color));
				g.fillPolygon(makeScoreHex(c,693,30));
				color=game.getCurrentPlayer().getHand().getPiece(counter).getPrimaryHexagon().getColor();
				g.setColor(pickColor(color));
				g.fillPolygon(makeScoreHex(c,745,30));
				c+=65;
			}
		}else if(!getSlowMode()){		//it's a computer player
			int count = 0;
			for(int ind = 0; ind < game.players.length; ind++){//need to find the indice of the current computer player
				if(game.players[ind] == game.getCurrentPlayer()){
					count = ind;
				}
			}
			//if there is a computer player in p1, then find = -1 and for loop doesn't go
			for(int find = count; find >= 0; find--){//find the most recent human player
				//NOT WORKING WHEN P4 IS HUMAN, and P1 is computer
				if(game.players[find].isHuman()){
					for(int counter = 0; counter < game.players[find].getHand().getSize(); counter++){//DOES NOT GET STUCK IN THIS FOR LOOP
						int color=game.players[find].getHand().getPiece(counter).getSecondaryHexagon().getColor();
						g.setColor(pickColor(color));
						//Fixing #4 issue in wishList: Don�t display computer players� tiles during their turns
						//g.fillPolygon(makeScoreHex(c,693,30));
						color=game.players[find].getHand().getPiece(counter).getPrimaryHexagon().getColor();
						g.setColor(pickColor(color));
						//Fixing #4 issue in wishList: Don�t display computer players� tiles during their turns
						//g.fillPolygon(makeScoreHex(c,745,30));
						c+=65;
					}
					find = -1;
				}else if(find == 0){
					find = game.players.length; //if its on p1
				}
			}
		}else{
			for(int counter = 0; counter < game.getCurrentPlayer().getHand().getSize(); counter++){ // <--GET HUMAN PLAYERS HAND
				int color=game.getCurrentPlayer().getHand().getPiece(counter).getSecondaryHexagon().getColor();
				g.setColor(pickColor(color));
				g.fillPolygon(makeScoreHex(c,693,30));
				color=game.getCurrentPlayer().getHand().getPiece(counter).getPrimaryHexagon().getColor();
				g.setColor(pickColor(color));
				g.fillPolygon(makeScoreHex(c,745,30));
				c+=65;
			}
		}
	}

	private void verticalLines(Graphics g){
		int change=50;
		int yInit = 80;
		int yInit2=93;
		for(int boxes = 0; boxes < game.getPlayers().length; boxes++){
			for (int counter2=0;counter2<19;counter2++){
				g.drawLine(change,yInit,change,yInit + 105);
				g.drawString(""+counter2 ,change +5,yInit2);
				change+=23;
			}
			yInit2+=140;
			yInit += 140;
			change = 50;
		}
	}
	//draw 7 equally spaced apart black lines
	private void horizontalLines(Graphics g){
		int change = 80;
		//    	int changey1=240,changey2=330;
		//
		for (int counter=0;counter< game.getPlayers().length;counter++){
			g.drawString(game.getPlayers()[counter].getName(),50,change-15);
			g.drawRect(50,change,435,105);//380 * 10
			g.setColor(Color.RED);
			int constant=50;
			for (int c=0;c<=game.getPlayers()[counter].getScores()[3] && c <= 18;c++)
			{
				g.fillRect(constant+(23 * c),change+15,24,15);
			}

			g.setColor(Color.BLUE);
			for (int c=0;c<=game.getPlayers()[counter].getScores()[5] && c <= 18;c++)
			{
				g.fillRect(constant+(23 * c),change+30,24,15);
			}
			g.setColor(Color.GREEN);
			for (int c=0;c<=game.getPlayers()[counter].getScores()[4] && c <= 18;c++)
			{
				g.fillRect(constant+(23 * c),change+45,24,15);
			}
			g.setColor(new Color(255,128,0));//orange
			for (int c=0;c<=game.getPlayers()[counter].getScores()[0] && c <= 18;c++)
			{
				g.fillRect(constant+(23 * c),change+60,24,15);
			}
			g.setColor(Color.YELLOW);
			for (int c=0;c<=game.getPlayers()[counter].getScores()[1] && c <= 18;c++)
			{
				g.fillRect(constant+(23 * c),change+75,24,15);
			}
			g.setColor(Color.MAGENTA);
			for (int c=0;c<=game.getPlayers()[counter].getScores()[2] && c <= 18;c++)
			{
				g.fillRect(constant+(23 * c),change+90,24,15);
			}
			g.setColor(Color.BLACK);
			g.drawLine(50,change+15,483,change+15);//x1, y1, x2, y2
			g.drawLine(50,change+30,483,change+30);
			g.drawLine(50,change+45,483,change+45);
			g.drawLine(50,change+60,483,change+60);
			g.drawLine(50,change+75,483,change+75);
			g.drawLine(50,change+90,483,change+90);
			change+=140;
		}
	}

	private Polygon makeScoreHex(int x, int y, int z){
		Polygon hex = new Polygon();
		double init,value;
		for(int a = 0; a<=6; a++){
			init = Math.PI/6;
			value = Math.PI / 3.0 * a;
			hex.addPoint((int)(Math.round(x + Math.sin(value+init) * z)), (int)(Math.round(y + Math.cos(value+init) * z)));
		}
		return hex;
	}
	private void initializeGrid(){ //Mess. Could be made more efficient
		for(int x = 0; x < 30; x++){
			for(int y = 0; y < 15;y++){
				if((y == 0 || y == 14) && (x< 8 || x >23)){
					hexagon[x][y] = null;
				}else if((y == 1 || y == 13) && (x< 6 || x >24)){
					hexagon[x][y] = null;
				}else if((y == 2 || y == 12) && (x< 5 || x >25)){
					hexagon[x][y] = null;
				}else if((y == 3 || y == 11) && (x< 4 || x >26)){
					hexagon[x][y] = null;
				}else if((y == 4 || y == 10) && (x< 3 || x >27)){
					hexagon[x][y] = null;
				}else if((y == 5 || y == 9) && (x< 2 || x >28)){
					hexagon[x][y] = null;
				}else if((y == 6 || y == 8) && (x< 1 || x >29)){
					hexagon[x][y] = null;
				}else{
					if(x % 2 == 0 && y % 2 == 0){
						if(x >= 10 && x < 20)
							hexagon[x][y] = makeHex((int)((width/3 + 110)+x*87*.6*.5) - 1, (y*45 + 80), 30);
						else if(x>=20)
							hexagon[x][y] = makeHex((int)((width/3 + 110)+x*87*.6*.5) - 2, (y*45 + 80), 30);
						else
							hexagon[x][y] = makeHex((int)((width/3 + 110)+x*87*.6*.5), (y*45 + 80), 30);
						hexColor[x][y] = -1;
					}
					else if(!(x % 2 == 0) && !(y % 2 == 0)){
						if(x >= 11 && x < 21)
							hexagon[x][y] = makeHex((int)(((width/3 + 110)+x*87*.6 *.5)) - 1 , (y*45 + 80), 30);
						else if(x>=21)
							hexagon[x][y] = makeHex((int)(((width/3 + 110)+x*87*.6 *.5)) - 2, (y*45 + 80), 30);
						else
							hexagon[x][y] = makeHex((int)(((width/3 + 110)+x*87*.6 *.5)) , (y*45 + 80), 30);
						hexColor[x][y] = -1;
					}
				}
			}
		}
	}
	private void makePiece(/*Piece myPiece*/int x, int y){
		piece = makeHex(x,y,30);
	}
	private Polygon tileChecker(int myX, int myY){// will check every polygon for set of coordinates
		return piece;
	}
	private void orientPiece(Graphics g){
		if(game.getCurrentPlayer().getCurrentPiece()!= null && game.getCurrentPlayer().getClass() == HumanPlayer.class){
			makePiece(X,Y);
			g.setColor(pickColor(game.getCurrentPlayer().getCurrentPiece().getPrimaryHexagon().getColor()));
			g.fillPolygon(piece);
			g.drawPolygon(piece);
			if(orientation == 0)
				makePiece((int)(X - 87*.6 *.5),Y - 45);
			if(orientation == 1)
				makePiece((int)(X + 87*.6 *.5),Y - 45);
			if(orientation == 2)
				makePiece(X + 50,Y );
			if(orientation == 3)
				makePiece((int)(X + 87*.6 *.5),Y + 45);
			if(orientation == 4)
				makePiece((int)(X - 87*.6 *.5),Y + 45);
			if(orientation == 5)
				makePiece(X - 50,Y );
			g.setColor(pickColor(game.getCurrentPlayer().getCurrentPiece().getSecondaryHexagon().getColor()));
			g.fillPolygon(piece);
			g.drawPolygon(piece);
		}

	}
	private Color pickColor(int myColor){
		//  orange = 1, yellow = 2, purple = 3, red = 4, green = 5, blue = 6
		if(myColor == 1)
			return new Color(255,128,0);
		else if(myColor == 2)
			return Color.YELLOW;
		else if(myColor == 3)
			return Color.MAGENTA;
		else if(myColor == 4)
			return Color.RED;
		else if(myColor == 5)
			return Color.GREEN;
		else if(myColor == 6)
			return Color.BLUE;
		else
			return null;
	}
	private void makeGameBoardTempGrid(int x, int y, int o){
		gameBoardTempGrid = new int[30][15];
		for (int X = 0; X < 30; X ++){
			for (int Y = 0; Y < 15; Y ++){
				if(game.twoHexGrid(o,x,y)[X][Y] == 0){
					gameBoardTempGrid[X][Y] = game.grid[X][Y];
				}else{
					gameBoardTempGrid[X][Y] = game.twoHexGrid(o,x,y)[X][Y];
				}
			}
		}
	}
	//Make a piece follow the mouse method: input - piece
	private void paintBoard(Graphics g){ //paints the board
		boolean onSpace = false;
		boolean strategy = false; //for testing purposes
		if(game != null && game.getCurrentPlayer() !=null)
		orientation = game.getCurrentPlayer().getOrientation();
		for(int x = 1; x<30; x++){
			for(int y = 0; y<15;y++){
				if(!(hexagon[x][y] == null)){
					if(x == 1 || x == 29 || x==28 || y == 0 ||  y == 14 || hexagon[x-2][y] == null || hexagon[x+2][y] == null)
						g.setColor(Color.GRAY);
					else if(y==1 || y == 13 || x == 3 || x == 27 || x== 26 || hexagon[x-4][y] == null || hexagon[x+4][y] == null)
						g.setColor(Color.LIGHT_GRAY);
					if(hexagon[x][y].contains(X,Y) ){//&&checkLegalMove()
						onSpace = true;
						stoX = x;
						stoY = y;
						try{
							if(game.checkLegalMove(orientation, x, y)){
								makeGameBoardTempGrid(x,y, orientation);
								score1 = game.score(x,y,gameBoardTempGrid);
								score2 = game.score(game.getSecondX(orientation, x, y), game.getSecondY(orientation, x, y) , gameBoardTempGrid);
							}
						}catch(Exception e){}
					}if(computerGrid[x][y]!=0 ){//for computer score display
						//onSpace = true;
						try{
							if(game.checkLegalMove(orientation, x, y)){
								makeGameBoardTempGrid(x,y,orientation);
								score1 = game.score(x,y , gameBoardTempGrid);
								score2 = game.score(game.getSecondX(orientation, x, y), game.getSecondY(orientation, x, y) ,gameBoardTempGrid);
							}
						}catch(Exception e){}
					}
					if(hexColor[x][y] != -1)
						g.setColor(pickColor(hexColor[x][y]));
					g.fillPolygon(hexagon[x][y]); //draws hex

					g.setColor(Color.BLACK);
					g.drawPolygon(hexagon[x][y]); // draws hex outline
					//                	if(game.getCurrentPlayer().getClass() == ComputerPlayer.class && (x == game.getCurrentPlayer().getPieceX() &&
					//                			y == game.getCurrentPlayer().getPieceY()) || (x == game.getSecondX(game.getCurrentPlayer().getOrientation(), game.getCurrentPlayer().getPieceX(), game.getCurrentPlayer().getPieceY()) &&
					//                			y == game.getSecondY(game.getCurrentPlayer().getOrientation(), game.getCurrentPlayer().getPieceX(), game.getCurrentPlayer().getPieceY()))){
					//                		g.setColor(Color.WHITE);
					//                    	g.drawPolygon(hexagon[x][y]);
					//                	}
					//                	if(computerGrid[x][y] != 0){
					//                		g.setColor(Color.RED);
					//                		((Graphics2D)g).setStroke(new BasicStroke(5));
					//                		g.drawPolygon(hexagon[x][y]);
					//                	}else{
					//                		g.setColor(Color.BLACK);
					//                    	g.drawPolygon(hexagon[x][y]);
					//                	}
					((Graphics2D)g).setStroke(new BasicStroke(1));
					g.setColor(Color.WHITE);
				}
			}

		}
		for(int x = 1; x<30; x++){
			for(int y = 0; y<15;y++){
				if(!(hexagon[x][y] == null)){
					if(computerGrid[x][y] != 0){
						g.setColor(pickColor(computerGrid[x][y]));
						g.fillPolygon(hexagon[x][y]);
						g.setColor(Color.RED);
						((Graphics2D)g).setStroke(new BasicStroke(5));
						g.drawPolygon(hexagon[x][y]);
					}
				}
			}
		}
		shadeCyan(g, onSpace);

		if(strategy){
			g.setColor(Color.RED);
			g.drawPolygon(hexagon[9/*Game.getX()*/][9/*Game.getY()*/]);
		}
		g.setColor(Color.BLACK);
		orientPiece(g);

	}
	public void shadeCyan(Graphics g, boolean onSpace){
		g.setColor(Color.CYAN);
		if(onSpace && game.getCurrentPlayer().getCurrentPiece() != null && game.getCurrentPlayer().getClass() == HumanPlayer.class){
			try{
				if(orientation == 0){
					if(hexColor[stoX-1][stoY-1] == -1 && hexColor[stoX][stoY] == -1){
						g.fillPolygon(hexagon[stoX][stoY]);
						g.drawPolygon(hexagon[stoX][stoY]);
						g.fillPolygon(hexagon[stoX-1][stoY-1]);
						g.drawPolygon(hexagon[stoX-1][stoY-1]);

					}
				}else if(orientation == 1){
					if(hexColor[stoX+1][stoY-1] == -1 && hexColor[stoX][stoY] == -1){
						g.fillPolygon(hexagon[stoX][stoY]);
						g.drawPolygon(hexagon[stoX+1][stoY-1]);
						g.fillPolygon(hexagon[stoX+1][stoY-1]);
						g.drawPolygon(hexagon[stoX+1][stoY-1]);
					}
				}else if(orientation == 2){
					if(hexColor[stoX+2][stoY] == -1 && hexColor[stoX][stoY] == -1){
						g.fillPolygon(hexagon[stoX][stoY]);
						g.drawPolygon(hexagon[stoX][stoY]);
						g.fillPolygon(hexagon[stoX+2][stoY]);
						g.drawPolygon(hexagon[stoX+2][stoY]);
					}
				}else if(orientation == 3){
					if(hexColor[stoX+1][stoY+1] == -1 && hexColor[stoX][stoY] == -1){
						g.fillPolygon(hexagon[stoX][stoY]);
						g.drawPolygon(hexagon[stoX][stoY]);
						g.fillPolygon(hexagon[stoX+1][stoY+1]);
						g.drawPolygon(hexagon[stoX+1][stoY+1]);

					}
				}else if(orientation == 4){
					if(hexColor[stoX-1][stoY+1] == -1 && hexColor[stoX][stoY] == -1){
						g.fillPolygon(hexagon[stoX][stoY]);
						g.drawPolygon(hexagon[stoX][stoY]);
						g.fillPolygon(hexagon[stoX-1][stoY+1]);
						g.drawPolygon(hexagon[stoX-1][stoY+1]);
					}
				}else if(orientation == 5){
					if(hexColor[stoX-2][stoY] == -1 && hexColor[stoX][stoY] == -1){
						g.fillPolygon(hexagon[stoX][stoY]);
						g.drawPolygon(hexagon[stoX][stoY]);
						g.fillPolygon(hexagon[stoX-2][stoY]);
						g.drawPolygon(hexagon[stoX-2][stoY]);

					}
				}
			}catch(Exception e){}
		}
	}
	public void paintOutline(Graphics g){

	}
	public void computerGrid(int[][] newGrid){
		computerGrid = newGrid;
	}
	public void updateGrid(int[][] newGrid){
		for(int x = 0; x<30; x++){
			for(int y = 0; y<15;y++){
				hexColor[x][y] = newGrid [x][y];
			}
		}
	}
	private void setGrid(int x, int y, int c){ //sets intitial colors
		hexColor[x][y] = c;
	}
	private void makeBoard (int players){ // <-- Mess. Does same thing as paintBoard, could be made more efficient
		for(int x = 1; x<30; x++){
			for(int y = 0; y<15;y++){
				if(players == 0 ){
					if(!(hexagon[x][y] == null)){
						if(x == 1 || x == 29 || x==28 || y == 0 ||  y == 14 || hexagon[x-2][y] == null || hexagon[x+2][y] == null)
							hexColor[x][y] = 0;
						else if(y==1 || y == 13 || x == 3 || x == 27 || x== 26 || hexagon[x-4][y] == null || hexagon[x+4][y] == null)
							hexColor[x][y] = 0;
						else
							hexColor[x][y] = -1;
					}
				}else if(players == 1){
					if(!(hexagon[x][y] == null)){
						if(x == 1 || x == 29 || x==28 || y == 0 ||  y == 14 || hexagon[x-2][y] == null || hexagon[x+2][y] == null)
							hexColor[x][y] = 0;
						else
							hexColor[x][y] = -1;
					}
				}
			}
		}
		setGrid(10,2,4);
		setGrid(20,2,5);
		setGrid(25,7,6);
		setGrid(20,12,1);
		setGrid(10,12,2);
		setGrid(5,7,3);
	}
	private Polygon makeHex(int x, int y, int z){ //z is currently radius of hex
		Polygon hex = new Polygon();
		double value;
		for(int a = 0; a<=6; a++){
			value = Math.PI / 3.0 * a;
			hex.addPoint((int)(Math.round(x + Math.sin(value) * z)), (int)(Math.round(y + Math.cos(value) * z)));
		}
		return hex;
	}
	public void setIsHumanPlayer(boolean human){
		isHumanPlayer = human;
	}
	public void getIsHumanPlayer(boolean human){
		isHumanPlayer = human;
	}
	public void run() {//update mouse x and y location, or something.
		while(game.isMoveRemaining() || game.isWinner()){
			repaint();
		}
	}
	public void setEnabled(boolean b){
		enabled = b;
	}
	public void mouseClicked(MouseEvent e) {//Currently for debugging
		if(enabled){ //<---- ENABLED?!
			X = e.getX();
			Y = e.getY();
			for(int x = 0; x < 30; x++){
				for(int y = 0; y < 15;y++){
					try{
						if(hexagon[x][y]!= null && hexagon[x][y].contains(e.getX(),e.getY())){

							if(game.getCurrentPlayer().getCurrentPiece() != null){
								game.setPiece(x,y);
							}
						}
						if(x < 6 && y < 2){
							if(handPieces[x][y]!= null && handPieces[x][y].contains(e.getX(),e.getY())){
								if(game.getCurrentPlayer().getCurrentPiece() == null){
									game.select(x);
								}
							}
						}
					}catch(Exception exception){}
				}
			}
			if(game.getCurrentPlayer().getCurrentPiece() != null){
				if(rotateClockwise.contains(X,Y)){
					rotate(1);
					game.getCurrentPlayer().resetDefault();
				}else if(rotateCounterClockwise.contains(X,Y)){
					rotate(-1);
					game.getCurrentPlayer().resetDefault();
				}else if(returnPiece.contains(X,Y)){
					game.deselect();
					game.getCurrentPlayer().setOrientation(0);
					game.getCurrentPlayer().resetDefault();
				}
			}
			repaint();
		}
	}

	public void mouseMoved(MouseEvent e) {
		X = e.getX();
		Y = e.getY();
		repaint();
	}
	@Override
	public void mouseEntered(MouseEvent e) {X = e.getX();
	Y = e.getY();
	repaint();}
	@Override
	public void mouseExited(MouseEvent e) {}
	@Override
	public void mousePressed(MouseEvent e) {
		X = e.getX();
		Y = e.getY();
		repaint();
	}
	@Override
	public void mouseReleased(MouseEvent e) {X = e.getX();
	Y = e.getY();
	repaint();}
	@Override
	public void mouseDragged(MouseEvent e) {X = e.getX();
	Y = e.getY();
	repaint();}
	public Game getGame() {
		return game;
	}
	public void setGame(Game game) {
		this.game = game;
	}
}
/*	private void paintScore(Graphics g){
		horizontalLines(g);
		verticalLines(g);
		//score drawing below
		g.drawRect(50,650,400,140);
		int c=85;
		for(int counter = 0;counter < game.getCurrentPlayer().getHand().getSize(); counter++){ // <--GET HUMAN PLAYERS HAND
			if(game.getCurrentPlayer().isHuman()){
				game.getCurrentPlayer().getHand().getPiece(counter);
				int color=game.getCurrentPlayer().getHand().getPiece(counter).getSecondaryHexagon().getColor();
				g.setColor(pickColor(color));
				g.fillPolygon(makeScoreHex(c,693,30));
				color=game.getCurrentPlayer().getHand().getPiece(counter).getPrimaryHexagon().getColor();
				g.setColor(pickColor(color));
				g.fillPolygon(makeScoreHex(c,745,30));
				c+=65;
			}else{		//it's a computer player
				int count = 0;
				for(int ind = 0; ind < game.players.length; ind++){//need to find the indice of the current computer player
						if(game.players[ind] == game.getCurrentPlayer()){
							count = ind;
						}
				}
				//if there is a computer player in p1, then find = -1 and for loop doesnt go
				for(int find = count; find >= 0; find--){//find the most recent human player
					//NOT WORKING WHEN P4 IS HUMAN, and P1 is computer
					if(game.players[find].isHuman()){
						game.players[find].getHand().getPiece(counter);
						int color=game.players[find].getHand().getPiece(counter).getSecondaryHexagon().getColor();
						g.setColor(pickColor(color));
						g.fillPolygon(makeScoreHex(c,693,30));
						color=game.players[find].getHand().getPiece(counter).getPrimaryHexagon().getColor();
						g.setColor(pickColor(color));
						g.fillPolygon(makeScoreHex(c,745,30));
						c+=65;
						find = -1;
					}else if(find == 0){
						find = game.players.length; //if its on p1
					}
				}
			}
		}
	}*/
