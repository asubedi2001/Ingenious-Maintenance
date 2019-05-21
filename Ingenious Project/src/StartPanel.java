import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.JTextField;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Color;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

//import GameWindow.SliderListener;

public class StartPanel extends JPanel{
	private InputHandler handle;
	private JRadioButton[][] topButtons;
	private ButtonGroup[] buttonGroups;
	private JTextField[] names;
	private JComboBox[] strategies;
	private String[] strategy;
	private JButton play;
	private JButton cancel;
	private boolean stratScreen;
	private boolean isCancelled;
	private boolean isPlay;
	private JRadioButton fastMode;
	private JRadioButton slowMode;
	private int games;
	private JTextField numOfGames;
	private StrategyAnalysisMode stratListener;
	private JLabel error;
	private int errorCounter;
	JPanel sliderPanel;
	
	private int lagTimeS = 0;
	boolean isContinueClicked = false;
	private GameWindow gameWindow;

	StartPanel(boolean first, GameWindow g){
		if(first){
			gameWindow = g;
			//this.setBackground(Color.BLUE);
			this.setSize(600,500);
			this.setLayout(new GridLayout(7,4));
			handle = new InputHandler();
			setRadioButtons();  //handles ALL BUTTONS
			setNameBoxes();		//handles ALL NAMES
			setStrategyBoxes(); //handles ALL scrolldown strats
			JLabel blank = new JLabel("");
			this.add(blank);
			play = new JButton("Play");
			this.add(play);
			play.setActionCommand("Play");
			play.addActionListener(handle);
			cancel = new JButton("Cancel");
			cancel.setActionCommand("Cancel");
			cancel.addActionListener(handle);
			this.add(cancel);
			isCancelled= false;
			isPlay = false;
			stratScreen = false;
			disableAhead();
		}
	}

	public JPanel getSliderPanel(){
		return sliderPanel;
	}
	private void disableAhead(){
		int ind = -1;
		for(int i = 0; i < 4 && ind == -1; i++){
			if(topButtons[0][i].isSelected()){
				ind = i;
			}
		}
		if(ind != -1){
			for(int col = 0; col <= ind; col++){
				for(int row = 0; row < 3; row++){
					topButtons[row][col].setVisible(true);
				}
			}
			for(int col = ind + 1; col < 4; col++){
				for(int row = 0; row < 3; row++){
					topButtons[row][col].setVisible(false);
				}
			}
		}
	}
	//INITIALIZES AND POSITIONS FIRST 3 ROWS OF BUTTONS
	private void setRadioButtons(){//CALLED FROM CONSTRUCTROR
		topButtons = new JRadioButton[3][4];
		buttonGroups = new ButtonGroup[4];
		for(int i = 0; i < 4; i++){
			buttonGroups[i] = new ButtonGroup();
		}
		for(int row = 0; row < 3; row++){
			for(int col = 0; col < 4; col++){
				if(row == 0){
					topButtons[row][col] = new JRadioButton("None");
				}else if(row == 1){
					topButtons[row][col] = new JRadioButton("Human");
				}else if(row == 2){
					topButtons[row][col] = new JRadioButton("Computer");
				}
				buttonGroups[col].add(topButtons[row][col]);
				this.add(topButtons[row][col]);
				topButtons[row][col].addActionListener(handle);
			}
		}
		topButtons[1][0].setSelected(true);
		topButtons[2][1].setSelected(true);
		topButtons[0][2].setSelected(true);
		topButtons[0][3].setSelected(true);
	}
	//INITIALIZES AND POSITIONS NAME INPUT BOXES
	private void setNameBoxes(){
		names = new JTextField[4];
		for(int i = 0; i < 4; i++){
			names[i] = new JTextField("Player " + (i+1));
			names[i].setPreferredSize(new Dimension(10, 10));
			if(i >= 2){
				names[i].disable();
			}
			this.add(names[i]);
		}
	}
	//INITIALIZES AND POSITIONS STRATEGY SCROLLDOWN WINDOW
	private void setStrategyBoxes(){
		strategies = new JComboBox[4];
		strategy = new String[2];
		strategy[0] = "Random Strategy";
		strategy[1] = "Greedy Strategy";
		for(int i = 0; i < 4; i++){
			strategies[i] = new JComboBox(strategy);
			strategies[i].setSelectedIndex(1);
			if(i != 1){	
				strategies[i].setForeground(Color.GRAY);
				strategies[i].disable();
			}
			this.add(strategies[i]);
		}
	}
	//RETURNS TRUE WHEN CANCEL IS CLICKED
	public boolean isCancelled(){
		return isCancelled;
	}
	//returns an array of the size of players (so all except none)
	//and the type of the player: will be 0 for humans, 1 for computers
	public int[] getPlayerTypes(){
		int numPlayers = numPlayers();
		int[] ret = new int[numPlayers];
		for(int i = 0; i < numPlayers; i++){
			if(topButtons[1][i].isSelected()){//if human
				ret[i] = 0;
			}else if(topButtons[2][i].isSelected()){
				ret[i] = 1;
			}
		}
		return ret;
	}
	//RETURNS TRUE WHEN PLAY IS CLICKED _AND_ ENOUGH SETTIGNS CHECKED
	public boolean isGameStart(){
		return isPlay;
	}
	public boolean isAnalysisStart(){
		return stratScreen;
	}
	//returns an array of 4, value of 0 will indicate not a computer
	//value of [1] default, [2] strategy 1, [3] strategy 2
	public int[] getStrategies(){
		int[] ret = new int[4];
		for(int c = 0; c < 4; c++){
			if(strategies[c].isEnabled()){
				if(strategies[c].getSelectedIndex() == 0){
					ret[c] = 1;
				}else if(strategies[c].getSelectedIndex() == 1){
					ret[c] = 2;
				}else if(strategies[c].getSelectedIndex() == 2){
					ret[c] = 3;
				}
			}else{ //if not selected
				ret[c] = 0;
			}
		}
		return ret;
	}
	//returns a string of names, null if not a player or a computer
	public String[] getNames(){
		int numPlayers = numPlayers();
		String[] ret = new String[numPlayers];
		for(int i = 0 ; i < numPlayers; i++){
			if(names[i].isEnabled()){
				ret[i] = names[i].getText();
			}else{
				ret[i] = null;
			}
		}
		return ret;
	}
	//returns how many players there are 
	public int numPlayers(){
		int numPlayers = 0;
		for(int col = 0; col < 4; col++){
			if(!topButtons[0][col].isSelected()){//if NONE is NOT SELECTED
				numPlayers++; //more players
			}
		}
		return numPlayers;
	}
	public boolean strategyScreen(){
		return stratScreen;
	}
	private class InputHandler implements ActionListener{

		public void actionPerformed(ActionEvent arg0) {
			String clicked = arg0.getActionCommand();
			int numPlayers = numPlayers();
			if(numPlayers < 2){//if enough players
				play.disable();
				play.setForeground(Color.GRAY);
			}else if(numPlayers >= 2){
				play.enable();
				play.setForeground(Color.BLACK);
			}
			if(clicked.equals("Cancel")){
				isCancelled = true;
			}else if(clicked.equals("Play")){
				if(numPlayers >= 2){
					if(allComputers()){
						stratScreen = true;
					}else{
						isPlay = true;
					}
				}
			}
			boolean hide = false;
			for(int i=0;i<4;i++){
				if(topButtons[2][i].isSelected()){//if COMPUTER
					hide = true;
				}
			}
			gameWindow.showSlider(hide);
			for(int col = 0;  col < 4; col++){
				if(topButtons[0][col].isSelected()){ //if NONE
					names[col].disable();			//no NAME
					strategies[col].disable();		//no STRATEGY
					names[col].setForeground(Color.GRAY);
					strategies[col].setForeground(Color.GRAY);
				}else if(topButtons[1][col].isSelected()){//if HUMAN
					strategies[col].disable();		//no STRATEGY
					strategies[col].setForeground(Color.GRAY);
					names[col].enable();					//can TYPE NAME, no STRATEGY
					names[col].setForeground(Color.BLACK);
				}else{//if COMPUTER
					names[col].enable();					//can TYPE NAME
					strategies[col].enable();				//can HAVE STRATEGY
					names[col].setForeground(Color.BLACK);
					strategies[col].setForeground(Color.BLACK);
				}
			}
			disableAhead();
		}
		private boolean allComputers(){
			for(int col = 0; col < 4; col++){
				if(topButtons[1][col].isSelected()){//if there is a human
					return false;
				}
			}
			return true;
		}
	}
	//acts on an open frame, elements of analysis query
	public void openAnalysisMode(){
		this.removeAll();
		this.setLayout(new GridLayout(5,2));
		stratListener = new StrategyAnalysisMode();

		ButtonGroup fastOrSlow = new ButtonGroup();
		fastMode = new JRadioButton("Fast Mode");
		slowMode= new JRadioButton("Slow Mode");
		fastOrSlow.add(fastMode);
		fastOrSlow.add(slowMode);
		fastMode.setSelected(true);

		JLabel title = new JLabel("Strategy Analysis Mode");
		JLabel prompt = new JLabel("Number of Games: ");
		numOfGames = new JTextField();

		JButton cont = new JButton("Continue");
		cont.setActionCommand("Continue");
		cont.addActionListener(stratListener);
		error = new JLabel("Input error: please input an integer");
		error.setVisible(false);
		this.add(title);
		this.add(new JLabel(""));
		this.add(prompt);
		this.add(numOfGames);
		this.add(fastMode);
		this.add(new JLabel(""));
		this.add(slowMode);
		this.add(error);
		this.setVisible(true);
		this.add(cont);	
	}
	public int fastOrSlow(){// 0 if fastmode, 1 if slowmode,
		if(fastMode.isSelected()){
			return 0;
		}
		return 1;
	}
	//returns numOfGames parsed from int,-1 if not entered yet 
	public int getGames(){
		if(games > 0){
			return games;
		}else{
			return -1;
		}
	}
	public boolean isContinueClicked(){
		return isContinueClicked;
	}
	private class StrategyAnalysisMode implements ActionListener{
		public void actionPerformed(ActionEvent arg0) {
			if(arg0.getActionCommand().equals("Continue")){
				//get the number from whatever inputted
				try{//if not a clear int
					games = Integer.parseInt(numOfGames.getText()); 
					if(games <= 0){
						errorCounter++;
						error.setText("Input error: please input an integer greater than 0");
						if(errorCounter % 2 == 0){
							error.setForeground(Color.BLUE);
						}else{
							error.setVisible(true);
							error.setForeground(Color.RED);
						}
					}
					isContinueClicked = true;
				}catch(Exception ex){//try again yo
					errorCounter++;
					error.setText("Input error: please input an integer");
					if(errorCounter % 2 == 0){
						error.setForeground(Color.BLUE);
					}else{
						error.setVisible(true);
						error.setForeground(Color.RED);
					}
				}
			}
		}
	}

	public int getLagTime(){
		return lagTimeS;
	}
}
