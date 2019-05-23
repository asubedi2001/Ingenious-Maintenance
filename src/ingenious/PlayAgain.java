package ingenious;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

//"You can play again!"
//one button -- "continue"

public class PlayAgain extends JFrame implements ActionListener
{
	private JLabel text;
	private JButton cont;
	public boolean close; // set to true when u need to close

	public PlayAgain() {
		close = false;
		JPanel contentPane = new JPanel();
		this.setLayout(new GridLayout(2, 1));
		text = new JLabel("You can play again!");
		cont = new JButton("continue");
		cont.addActionListener(this);
		cont.setActionCommand("continue");
		contentPane.add(text);
		contentPane.add(cont);
		this.add(contentPane);
		this.setContentPane(contentPane);
		this.pack();
		this.setVisible(true);
	}

	public boolean getClose() {
		return close;
	}

	public void actionPerformed(ActionEvent arg0) {
		if (arg0.getActionCommand().equals("continue")) {
			close = true; // have a boolean close
			this.dispose();
		}
	}
}
