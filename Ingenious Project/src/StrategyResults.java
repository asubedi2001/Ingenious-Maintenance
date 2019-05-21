
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
public class StrategyResults extends JPanel{
	//takes two ints, the number of wins for each strategy 
	private boolean back;
	public StrategyResults(int stratOneWins, int stratTwoWins) {
//		this.setSize(500,400);
		Back backButton = new Back();
		this.setLayout(new GridLayout(4,1));
		this.setBackground(Color.BLACK);
		JLabel title = new JLabel("Strategy Analysis");
		title.setFont(new Font(Font.MONOSPACED, Font.BOLD, 48));
		title.setForeground(Color.GREEN);
		JLabel strat1 = new JLabel("Strategy 1: " + stratOneWins + " games won");
		strat1.setForeground(Color.RED);
		JLabel strat2 = new JLabel("Strategy 2: " + stratTwoWins + " games won");
		strat2.setForeground(Color.BLUE);
		JButton cont = new JButton("Dismiss");
		cont.setActionCommand("Back");
		cont.addActionListener(backButton);
		this.add(title);
		this.add(strat1);
		this.add(strat2);
		this.add(cont);
	}
	public boolean end(){
		return back;
	}
	private class Back implements ActionListener{
		public void actionPerformed(ActionEvent arg0) {
			if(arg0.getActionCommand().equals("Back")){
				back = true;
				System.exit(0);
			}
		}		
	}
}
