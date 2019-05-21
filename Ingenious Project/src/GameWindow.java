import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class GameWindow{
	JFrame frame; 
	StartPanel pan;
	JPanel sliderPanel;
	Game game;
	GameBoard gameBoard;
	int lagTime = 0;
	GameWindow(){
		frame = new JFrame("Ingenious");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JPanel container = new JPanel();
		pan = new StartPanel(true,this);
		sliderPanel = new JPanel();
		final int FPS_MIN = 0;
		final int FPS_MAX = 9;
		final int FPS_INIT = 0; 
		JSlider slider = new JSlider(JSlider.HORIZONTAL,
                FPS_MIN, FPS_MAX, FPS_INIT);

		//Turn on labels at major tick marks.
		slider.setMajorTickSpacing(1);
		slider.setPaintTicks(true);
		slider.setPaintLabels(true);
		slider.addChangeListener(new SliderListener());
		container.setPreferredSize(new Dimension(600,500));
		sliderPanel.setLayout(new BoxLayout(sliderPanel, BoxLayout.PAGE_AXIS));
		JLabel label = new JLabel("Choose Computer Player Lag Time:");
		label.setHorizontalAlignment((int) JPanel.CENTER_ALIGNMENT);
		sliderPanel.add(label);
		sliderPanel.add(slider);
		sliderPanel.setVisible(true);
		Title ingenious = new Title();
		container.add(ingenious, BorderLayout.PAGE_START);
		container.add(pan);
		container.add(sliderPanel);
		frame.setContentPane(container);
		
		

		//frame.add(pan, BorderLayout.CENTER);
		
		
		frame.pack();
		frame.setVisible(true);
		while(!pan.isGameStart() && !pan.isAnalysisStart()){
			if(pan.isCancelled()){
				frame.dispose();
			}
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(pan.isGameStart()){
			play(0);
		}else if(pan.isAnalysisStart()){
			play(1);
		}
	}
	class SliderListener implements ChangeListener {
	    public void stateChanged(ChangeEvent e) {
	    	 JSlider source = (JSlider)e.getSource();
	    	  if (!source.getValueIsAdjusting()) {
	    		  lagTime = (int)source.getValue();
	    	  }
	    }
	}
	public void showSlider(boolean b){
		if(b){
			sliderPanel.setVisible(true);
		}else{
			sliderPanel.setVisible(false);
			lagTime = 0;
		}
	}

	public void play(int a){
		if(a == 0){
			Game game = new Game(pan.getNames(), pan.getPlayerTypes(), pan.getStrategies());
			GameBoard gameBoard = game.getGameBoard();
			new Thread(gameBoard).start();


			frame.setContentPane(gameBoard);
			frame.pack();
			frame.setVisible(true);
			try {
				game.play(lagTime);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			frame.dispose();
			frame = new GameOver(game.sortPlayers(), game.getSortedScores());
			frame.setPreferredSize(new Dimension(1500,800));
			frame.pack();
			frame.setVisible(true);
			while(!((GameOver) frame).cancel()){
				try {
					Thread.sleep(0);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			frame.dispose();
		}else if(a == 1){
			sliderPanel.setVisible(false);
			pan.openAnalysisMode();
			frame.pack();
			frame.setVisible(true);
			while(!pan.isContinueClicked()){
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(pan.fastOrSlow() == 0){

				frame.pack();
				frame.setVisible(true);
			}else{
				//				frame.setContentPane(gameBoard);
				//				frame.pack();
				//				frame.setVisible(true);
			}


			Game game;
			GameBoard gameBoard;
			int[] wins = new int[pan.numPlayers()];
			for(int player = 0; player < pan.numPlayers(); player ++){
				wins[player] = 0;
			}
			for(int i = 0; i < pan.getGames(); i ++ ){
				game = new Game(pan.getNames(), pan.getPlayerTypes(), pan.getStrategies());
				gameBoard = game.getGameBoard();
				new Thread(gameBoard).start();
				if(pan.fastOrSlow() == 1){
					gameBoard.setSlowMode(true);
					frame.setContentPane(gameBoard);
					frame.pack();
					frame.setVisible(true);
				}else{
					game.setSleepTimer(0);
				}
				try {
					lagTime = pan.getLagTime();
					game.play(lagTime);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				for(int player = 0; player < game.numPlayers(); player ++){
					if(game.sortPlayers()[0] == game.getPlayers()[player]){
						wins[player] += 1;
					}

				}
				game = null;
				gameBoard = null;

			}
			StrategyResults strats = new StrategyResults(wins[0],wins[1]);
			//			frame.removeAll();
			frame.setContentPane(strats);
			frame.pack();
			frame.setVisible(true);
			while(!strats.end()){
				try {
					Thread.sleep(0);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			frame.dispose();

		}
	}
}
