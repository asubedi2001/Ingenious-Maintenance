import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
public class HandTrade extends JFrame implements ActionListener{

	JFrame frame;
	JPanel pan;
	JPanel pan1;
	JPanel pan2;
	JLabel label;
	JButton trade;
	JButton cancel;
	public boolean isTrade;
	public boolean isClosed;
	HandTrade(){
		frame = new JFrame("Ingenious");
		frame.setAlwaysOnTop(true);
		pan = new JPanel();
		pan1 = new JPanel();
		pan2 = new JPanel();
		isTrade = false;
		isClosed = false;
		label = new JLabel("Would you like to trade your hand?");
		trade = new JButton("Trade");
		trade.setActionCommand("Trade");
		trade.addActionListener(this);
		cancel = new JButton("Cancel");
		cancel.setActionCommand("Cancel");
		cancel.addActionListener(this);
		pan.setLayout(new GridLayout(1,1));
		pan2.setLayout(new GridLayout(2,0));
		pan1.add(label);
		pan2.add(trade);
		pan2.add(cancel);
		pan.add(pan1);
		pan.add(pan2);
		frame.setContentPane(pan);
		frame.pack();
		frame.setVisible(true);
		
	}
	
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals("Trade")){
			isTrade = true;
			isClosed = true;
			frame.dispose();
		}else if(e.getActionCommand().equals("Cancel")){
			isClosed = true;
			frame.dispose();
		}
		
	}
	public boolean getIsTrade(){
		return isTrade;
	}
	public boolean getIsClosed(){
		return isClosed;
	}
}
