import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Color;
import java.awt.Dimension;

public class GameOver extends JFrame implements ActionListener{
	//takes a string of the length the # of players,
	//and is order of players: [0] == first place, [1] == second place etc
	boolean cancel;
	String[] players;
	Player[] orderOfPlayers;
	int[] Scores;
	JPanel pan, pan1;
	public GameOver(Player[] orderOfPlayers, int[] scores) {
		this.orderOfPlayers = orderOfPlayers;
		int numPlayers = orderOfPlayers.length;
		players = new String[orderOfPlayers.length];
		for(int a = 0; a < orderOfPlayers.length; a ++){
			players[a]=orderOfPlayers[a].getName();
		}
		Scores=scores;
		pan = new JPanel();
		pan1 = new JPanel();
		ScorePanel scorePanel = new ScorePanel();
		pan.setSize(2000,2000);
		pan.setLayout(new GridLayout(3+numPlayers,2));
		pan.setBackground(Color.BLACK);
		JLabel title = new JLabel("Game Over");
		title.setFont(new Font(Font.MONOSPACED, Font.BOLD, 48));
		title.setForeground(Color.RED);
		JLabel ranks = new JLabel("Rankings");
		ranks.setFont(new Font(Font.MONOSPACED, Font.BOLD, 36));
		ranks.setForeground(Color.WHITE);
		JLabel points = new JLabel("Points");
		points.setFont(new Font(Font.MONOSPACED, Font.BOLD, 36));
		points.setForeground(Color.WHITE);
		//Dismiss close = new Dismiss();
		JButton dismiss = new JButton("Dismiss");
		dismiss.setActionCommand("Dismiss");
		dismiss.addActionListener(this);

		pan.add(title);
		pan.add(new JLabel(""));
		pan.add(ranks);
		pan.add(points);



		Color[] colors = colorRanks(numPlayers);

		for(int i = 0; i < numPlayers; i++){
			JLabel player = new JLabel(orderOfPlayers[i].getName());
			player.setFont(new Font(Font.MONOSPACED, Font.BOLD, 24));
			player.setForeground(colors[i]);
			JLabel score = new JLabel("" + scores[i]);
			score.setFont(new Font(Font.MONOSPACED, Font.BOLD, 24));
			score.setForeground(colors[i]);

			pan.add(player);
			pan.add(score);
		}
		pan.add(dismiss);

		pan1.setLayout(new GridLayout(1,2));
		pan1.add(pan);
		pan1.add(scorePanel);
		scorePanel.repaint();
		this.setPreferredSize(new Dimension(1000,800));
		this.setContentPane(pan1);
		this.pack();
		this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	}
	

	private Color[] colorRanks(int numPlayers){
		Color[] colors = new Color[numPlayers];
		for(int p = 0; p < colors.length; p++){//wont go beyond length	
			if(p == 0){
				colors[p] = Color.YELLOW;
			}else if(p == 1){
				colors[p] = Color.BLUE;
			}else if(p == 2){
				colors[p] = Color.RED;
			}else if(p == 3){
				colors[p] = Color.GREEN;
			}
		}
		return colors;
	}
	public boolean cancel(){
		return cancel;
	}
	public void actionPerformed(ActionEvent arg0) {
		if(arg0.getActionCommand().equals("Dismiss")){
			cancel = true;
			this.dispose();
			System.exit(0);
		}
	}
	public class ScorePanel extends JPanel{
		public void paintComponent(Graphics g)
		{
			horizontalLines(g);
			int change=15;   	 
			int yInit = 80;
			int yInit2=93;
			for(int boxes = 0; boxes < players.length; boxes++){
				for (int counter2=0;counter2<19;counter2++){           	 
					g.drawLine(change,yInit,change,yInit + 105);
					g.drawString(""+counter2 ,change +5,yInit2);       	 
					change+=23;
				}
				yInit2+=140;
				yInit += 140;
				change = 15;
			}
		}
		private void horizontalLines(Graphics g){
			int change = 80;
			//    	int changey1=240,changey2=330;
			//
			for (int counter=0;counter< players.length;counter++){
				//g.drawString(game.getPlayers()[counter].getName(),50,change-15);
				g.drawString(players[counter],15,change-15);
				g.drawRect(15,change,435,105);//380 * 10
				g.setColor(Color.RED);
				int constant=15;
				for (int c=0;c<=orderOfPlayers[counter].getScores()[3];c++)
				{
					g.fillRect(constant+(23 * c),change+15,24,15);
				}

				g.setColor(Color.BLUE);
				for (int c=0;c<=orderOfPlayers[counter].getScores()[5];c++)
				{
					g.fillRect(constant+(23 * c),change+30,24,15);
				}
				g.setColor(Color.GREEN);
				for (int c=0;c<=orderOfPlayers[counter].getScores()[4];c++)
				{
					g.fillRect(constant+(23 * c),change+45,24,15);
				}
				g.setColor(new Color(255,128,0));
				for (int c=0;c<=orderOfPlayers[counter].getScores()[0];c++)
				{
					g.fillRect(constant+(23 * c),change+60,24,15);
				}
				g.setColor(Color.YELLOW);
				for (int c=0;c<=orderOfPlayers[counter].getScores()[1];c++)
				{
					g.fillRect(constant+(23 * c),change+75,24,15);
				}
				g.setColor(Color.MAGENTA);
				for (int c=0;c<=orderOfPlayers[counter].getScores()[2];c++)
				{
					g.fillRect(constant+(23 * c),change+90,24,15);
				}
				g.setColor(Color.BLACK);
				g.drawLine(15,change+15,433 + 15,change+15);//x1, y1, x2, y2
				g.drawLine(15,change+30,433 + 15,change+30);
				g.drawLine(15,change+45,433 + 15,change+45);
				g.drawLine(15,change+60,433 + 15,change+60);
				g.drawLine(15,change+75,433 + 15,change+75);
				g.drawLine(15,change+90,433 + 15,change+90);
				change+=140;
			}    
		}
	}
	
	
	
}




