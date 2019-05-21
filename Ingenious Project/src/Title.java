import java.awt.Color;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;


public class Title extends JPanel{
	JLabel ingenious;
	Title(){
		ingenious = new JLabel("INGENIOUS");
		ingenious.setForeground(Color.RED);
		ingenious.setFont(new Font(Font.MONOSPACED, Font.BOLD, 36));		
		this.add(ingenious);
		this.setVisible(true);
	}
}
