
import java.util.Arrays;

public class Game
{
	Player[] players;
	Player currentPlayer;
	int[][] grid;
	int[][] tempGrid;
	int[][] emptyGrid;
	GameBoard gameBoard;
	GrabBag grabBag;
	private boolean isGameOver;
	Strategy[] gameStrategies;
	String[] playerNames;
	int sleepTimer = 500;
	Player[] p;
	int[] sortedScores;

	Game(String[] names, int[] playerTypes, int[] strategies) {
		grabBag = new GrabBag();
		isGameOver = false;
		players = new Player[names.length];
		isGameOver = false;
		playerNames = names;
		initializeStrategies();
		grid = new int[30][15];
		emptyGrid = new int[30][15];

		for (int a = 0; a < names.length; a++) {
			if (playerTypes[a] == 0) {
				players[a] = new HumanPlayer(names[a], new Hand(grabBag));
			} else if (playerTypes[a] == 1) {
				players[a] = new ComputerPlayer(names[a], getStrategy(strategies[a] - 1), new Hand(grabBag));
			}
		}
		gameBoard = new GameBoard(this);
		for (int x = 0; x < 30; x++) {
			for (int y = 0; y < 15; y++) {
				grid[x][y] = gameBoard.getHexColor()[x][y];
				emptyGrid[x][y] = 0;
			}
		}
	}

	public void play(int lagTime) throws InterruptedException {

		lagTime = 0;
		sleepTimer = 0;

		int[] startScores = new int[] { 0, 0, 0, 0, 0, 0 };
		boolean isSecondPlay = false;
		PlayAgain playAgain = null;
		while (!isGameOver) {// player turn manager
			for (int a = 0; a < players.length && !isGameOver; a++) {
				if (isSecondPlay) {
					if (currentPlayer.getClass() == HumanPlayer.class) {
						playAgain = new PlayAgain();
					}
					if (a == 0) {
						a = players.length - 1;
					} else {
						a -= 1;
					}
				}
				currentPlayer = players[a];
				currentPlayer.setTurnComplete(false);
				for (int i = 0; i < 6; i++) {
					startScores[i] = currentPlayer.getScores()[i];
				}
				if (currentPlayer.checkHand() && currentPlayer.getClass() == HumanPlayer.class) {
					// JFrame fram = new JFrame("Ingenious");
					HandTrade handTrade = new HandTrade();
					while (!handTrade.getIsClosed()) {
						gameBoard.setEnabled(false);
					}
					gameBoard.setEnabled(true);
					if (handTrade.getIsTrade()) {
						if (isSecondPlay) {
							currentPlayer.tradeHandAndMaxOut();
						} else {
							currentPlayer.tradeHand();
						}
					}
				}
				isSecondPlay = false;
				if (currentPlayer.getClass() == ComputerPlayer.class) {
					//Thread.sleep(lagTime * 1000);
				}
				do {
					if (currentPlayer.getClass() == ComputerPlayer.class) {
						if (currentPlayer.getCurrentPiece() != null) {
							currentPlayer.getHand().addNewPiece(currentPlayer.getCurrentPiece());
							currentPlayer.removeCurrentPiece();
						}
					}
					// currentPlayer.resetDefault();
					currentPlayer.move();
					gameBoard.repaint();
				} while (!checkLegalMove());
				gameBoard.computerGrid(emptyGrid);
				if (currentPlayer.getClass() == ComputerPlayer.class) {
					gameBoard.computerGrid(twoHexGrid(currentPlayer.getOrientation(), currentPlayer.getPieceX(),
							currentPlayer.getPieceY()));
					//Thread.sleep(sleepTimer);
				}
				currentPlayer.updateScore(getTurnScore(currentPlayer.getOrientation(), currentPlayer.getPieceX(),
						currentPlayer.getPieceY()));
				updateGrid(twoHexGrid(currentPlayer.getOrientation(), currentPlayer.getPieceX(),
						currentPlayer.getPieceY()));

				currentPlayer.removeCurrentPiece();

				for (int i = 0; i < 6; i++) {
					if (currentPlayer.getScores()[i] == 18 && startScores[i] < 18) {
						isSecondPlay = true;
					}
				}
				if (!isSecondPlay) {
					do {
						currentPlayer.addNewPiece();
					} while (currentPlayer.getHand().getSize() < 6);
				}
				if (playAgain != null) {
					playAgain.dispose();
				}
				currentPlayer.setOrientation(0);
				if (!isMoveRemaining() || isWinner()) {
					isGameOver = true;
				}
			}

			if (!isMoveRemaining() || isWinner()) {
				isGameOver = true;
			}
		}
	}

	public void setSleepTimer(int a) {
		sleepTimer = a;
	}

	public String[] playerNames() {
		return playerNames;
	}

	public int[] scoreOrder() {// needs to check second lowest score if both
								// players have a score of zero somewhere
		int[] scoreOrder = new int[players.length];
		int[] lowScore = new int[players.length];
		for (int a = 0; a < players.length; a++) {
			lowScore[a] = players[a].getScores()[0];
			for (int score = 0; score < players[a].getScores().length; score++) {
				if (players[a].getScores()[score] < lowScore[a]) {
					lowScore[a] = players[a].getScores()[score];
				}
			}
		}
		for (int a = 0; a < players.length; a++) {
			scoreOrder[a] = a;
		}
		for (int i = 0; i < players.length; i++) {
			for (int a = i; a < players.length; a++) {
				if (lowScore[i] < lowScore[a]) {
					scoreOrder[i] = scoreOrder[a];
				}
			}
		}
		return scoreOrder;
	}

	public int[] scores() {
		int[] lowScore = new int[players.length];
		int[] scores = new int[players.length];
		for (int a = 0; a < players.length; a++) {
			lowScore[a] = players[a].getScores()[0];
			for (int score = 0; score < players[a].getScores().length; score++) {
				if (players[a].getScores()[score] < lowScore[a]) {
					lowScore[a] = players[a].getScores()[score];
				}
			}
		}
		for (int a = 0; a < players.length; a++) {
			scores[scoreOrder()[a]] = lowScore[a];
		}
		return scores;

	}

	public String[] nameOrder() {
		String[] nameOrder = new String[playerNames.length];
		for (int a = 0; a < players.length; a++) {
			nameOrder[scoreOrder()[a]] = playerNames[a];
		}
		return nameOrder;
	}

	public Player[] sortPlayers() {

		if (players.length == 2) {

			p = new Player[2];
			sortedScores = new int[2];
			int[][] score = new int[2][6];
			int[] a = new int[6];
			for (int i = 0; i < 6; i++) {
				a[i] = players[0].getScores()[i];
			}
			Arrays.sort(a);
			players[0].setLowestScore(a[0]);
			int[] b = new int[6];
			for (int i = 0; i < 6; i++) {
				b[i] = players[1].getScores()[i];
			}
			Arrays.sort(b);
			players[1].setLowestScore(b[0]);
			for (int i = 0; i < 2; i++) {
				for (int j = 0; j < 6; j++) {
					if (i == 0) {
						score[i][j] = a[j];
					} else if (i == 1) {
						score[i][j] = b[j];
					}
				}
			}

			if (score[0][0] > score[1][0]) {
				p[0] = players[0];
				p[1] = players[1];
			} else if (score[0][0] > score[1][0]) {
				p[0] = players[1];
				p[1] = players[0];
			} else {
				if (score[0][1] > score[1][1]) {
					p[0] = players[0];
					p[1] = players[1];
				} else {
					p[0] = players[1];
					p[1] = players[0];
				}
			}
			sortedScores[0] = p[0].getLowestScore();
			sortedScores[1] = p[1].getLowestScore();
		}

		else if (players.length == 3) {

			p = new Player[3];
			sortedScores = new int[3];
			int[][] score = new int[3][6];
			int[] a = new int[6];
			;
			for (int i = 0; i < 6; i++) {
				a[i] = players[0].getScores()[i];
			}
			Arrays.sort(a);
			players[0].setLowestScore(a[0]);
			int[] b = new int[6];
			for (int i = 0; i < 6; i++) {
				b[i] = players[1].getScores()[i];
			}
			Arrays.sort(b);
			players[1].setLowestScore(b[0]);
			int[] c = new int[6];
			for (int i = 0; i < 6; i++) {
				c[i] = players[2].getScores()[i];
			}
			Arrays.sort(c);
			players[2].setLowestScore(c[0]);
			for (int i = 0; i < 3; i++) {
				for (int j = 0; j < 6; j++) {
					if (i == 0) {
						score[i][j] = a[j];
					} else if (i == 1) {
						score[i][j] = b[j];
					} else if (i == 2) {
						score[i][j] = c[j];
					}
				}
			}
			int highest = 0;
			for (int i = 0; i < 3; i++) {
				if (score[i][0] > score[highest][0]) {
					highest = i;
				}
			}

			p[0] = players[highest];
			int secondHighest = Math.abs(highest - 1);
			for (int i = 0; i < 3; i++) {
				if (score[i][0] > score[secondHighest][0] && i != highest) {
					secondHighest = i;
				}
			}
			int thirdHighest = 5;

			p[1] = players[secondHighest];
			for (int i = 0; i < 3; i++) {
				if (i != highest && i != secondHighest) {
					thirdHighest = i;
				}
			}

			p[2] = players[thirdHighest];
			for (int i = 0; i < 2; i++) {
				if (score[i][0] == score[i + 1][0]) {

				}
			}
			sortedScores[0] = p[0].getLowestScore();
			sortedScores[1] = p[1].getLowestScore();
			sortedScores[2] = p[2].getLowestScore();
		} else if (players.length == 4) {

			p = new Player[4];
			sortedScores = new int[4];
			int[][] score = new int[4][6];
			int[] a = new int[6];
			for (int i = 0; i < 6; i++) {
				a[i] = players[0].getScores()[i];
			}
			Arrays.sort(a);
			players[0].setLowestScore(a[0]);
			int[] b = new int[6];
			for (int i = 0; i < 6; i++) {
				b[i] = players[1].getScores()[i];
			}
			Arrays.sort(b);
			players[1].setLowestScore(b[0]);
			int[] c = new int[6];
			for (int i = 0; i < 6; i++) {
				c[i] = players[2].getScores()[i];
			}
			Arrays.sort(c);
			players[2].setLowestScore(c[0]);
			int[] d = new int[6];
			for (int i = 0; i < 6; i++) {
				d[i] = players[3].getScores()[i];
			}
			Arrays.sort(d);
			players[3].setLowestScore(d[0]);
			for (int i = 0; i < 4; i++) {
				for (int j = 0; j < 6; j++) {
					if (i == 0) {
						score[i][j] = a[j];
					} else if (i == 1) {
						score[i][j] = b[j];
					} else if (i == 2) {
						score[i][j] = c[j];
					} else if (i == 3) {
						score[i][j] = d[j];
					}
				}
			}

			int highest = 0;
			for (int i = 0; i < 4; i++) {
				if (score[i][0] > score[highest][0]) {
					highest = i;
				}
			}

			p[0] = players[highest];
			int secondHighest = 0;
			for (int i = 0; i < 4; i++) {
				//Fix problem with final scores – winning score is listed twice under player 1 is a winner
				if(secondHighest == highest && secondHighest < 3)
					secondHighest = i+1;
				
				if (score[i][0] > score[secondHighest][0] && i != highest) {
					secondHighest = i;
				}
			}

			p[1] = players[secondHighest];
			int firstRemaining = 5;
			int secondRemaining = 5;
			for (int i = 0; i < 4; i++) {
				if (i != highest && i != secondHighest) {
					firstRemaining = i;
				}
			}
			for (int i = 0; i < 4; i++) {
				if (i != highest && i != secondHighest && i != firstRemaining) {
					secondRemaining = i;
				}
			}

			if (score[firstRemaining][0] > score[secondRemaining][0]) {
				p[2] = players[firstRemaining];
				p[3] = players[secondRemaining];
			} else {
				p[3] = players[firstRemaining];
				p[2] = players[secondRemaining];
			}
			sortedScores[0] = p[0].getLowestScore();
			sortedScores[1] = p[1].getLowestScore();
			sortedScores[2] = p[2].getLowestScore();
			sortedScores[3] = p[3].getLowestScore();
		}

		return p;
	}

	public int[] getSortedScores() {
		return sortedScores;
	}

	public void updateGrid(int[][] newGrid) {
		for (int x = 0; x < grid[0].length; x++) {
			for (int y = 0; y < grid.length; y++) {
				if (newGrid[y][x] != 0) {
					grid[y][x] = newGrid[y][x];
				}
			}
		}
		gameBoard.updateGrid(grid);
	}

	public GameBoard getGameBoard() {
		return gameBoard;
	}

	public void setCurrentTurn(boolean bool) {
		currentPlayer.setCurrentTurn(bool);
	}

	public void setTurnComplete(boolean bool) {
		currentPlayer.setTurnComplete(bool);
	}

	public Strategy getStrategy(int a) {
		return gameStrategies[a];
	}

	public Player getCurrentPlayer() {
		return currentPlayer;
	}

	public void initializeStrategies() {
		gameStrategies = new Strategy[2];
		gameStrategies[0] = new RandomStrategy(this);
		gameStrategies[1] = new GreedyStrategy(this);
	}

	public void rotate(int direction) {
		((HumanPlayer) currentPlayer).rotate(direction);
	}

	public void deselect() {
		((HumanPlayer) currentPlayer).deselect();
	}

	public void select(int piece) {
		((HumanPlayer) currentPlayer).selectPiece(piece);
	}

	public void setPiece(int x, int y) {// maybe have a method check legal move
										// and then set turn complete
		((HumanPlayer) currentPlayer).setPieceX(x);
		((HumanPlayer) currentPlayer).setPieceY(y);
		currentPlayer.setTurnComplete(true);
	}

	public int[] getTurnScore(int o, int x, int y) {
		int[] newScore = { 0, 0, 0, 0, 0, 0 };
		makeTempGrid(o, x, y);
		newScore[currentPlayer.getCurrentPiece().getPrimaryHexagon().getColor() - 1] += score(x, y);
		newScore[currentPlayer.getCurrentPiece().getSecondaryHexagon().getColor() - 1] += score(getSecondX(o, x, y),
				getSecondY(o, x, y));
		return newScore;
	}

	public void makeTempGrid(int o, int x, int y) {
		tempGrid = new int[30][15];
		for (int X = 0; X < 30; X++) {
			for (int Y = 0; Y < 15; Y++) {
				if (twoHexGrid(o, x, y)[X][Y] == 0) {
					tempGrid[X][Y] = grid[X][Y];
				} else {
					tempGrid[X][Y] = twoHexGrid(o, x, y)[X][Y];
				}
			}
		}
	}

	public int score(int xInit, int yInit) {
		// makeTempGrid(currentPlayer.getOrientation(),xInit,yInit);
		int score = 0;
		int x = xInit;
		int y = yInit;
		int color = tempGrid[x][y];
		while ((x - 2) >= 0 && grid[x - 2][y] == color) {
			x -= 2;
			score += 1;
		}
		x = xInit;
		y = yInit;
		while ((x + 2) < 30 && grid[x + 2][y] == color) {
			x += 2;
			score += 1;
		}
		x = xInit;
		y = yInit;
		while ((x - 1) >= 0 && (y - 1) >= 0 && grid[x - 1][y - 1] == color) {
			x -= 1;
			y -= 1;
			score += 1;
		}
		x = xInit;
		y = yInit;
		while ((x + 1) < 30 && (y - 1) >= 0 && grid[x + 1][y - 1] == color) {
			x += 1;
			y -= 1;
			score += 1;
		}
		x = xInit;
		y = yInit;
		while ((x - 1) >= 0 && (y + 1) < 15 && grid[x - 1][y + 1] == color) {
			x -= 1;
			y += 1;
			score += 1;
		}
		x = xInit;
		y = yInit;
		while ((x + 1) < 30 && (y + 1) < 15 && grid[x + 1][y + 1] == color) {
			x += 1;
			y += 1;
			score += 1;
		}
		return score;
	}

	public int score(int xInit, int yInit, int[][] spareGrid) {
		int score = 0;
		int x = xInit;
		int y = yInit;
		int color = spareGrid[x][y];
		while ((x - 2) >= 0 && grid[x - 2][y] == color) {
			x -= 2;
			score += 1;
		}
		x = xInit;
		y = yInit;
		while ((x + 2) < 30 && grid[x + 2][y] == color) {
			x += 2;
			score += 1;
		}
		x = xInit;
		y = yInit;
		while ((x - 1) >= 0 && (y - 1) >= 0 && grid[x - 1][y - 1] == color) {
			x -= 1;
			y -= 1;
			score += 1;
		}
		x = xInit;
		y = yInit;
		while ((x + 1) < 30 && (y - 1) >= 0 && grid[x + 1][y - 1] == color) {
			x += 1;
			y -= 1;
			score += 1;
		}
		x = xInit;
		y = yInit;
		while ((x - 1) >= 0 && (y + 1) < 15 && grid[x - 1][y + 1] == color) {
			x -= 1;
			y += 1;
			score += 1;
		}
		x = xInit;
		y = yInit;
		while ((x + 1) < 30 && (y + 1) < 15 && grid[x + 1][y + 1] == color) {
			x += 1;
			y += 1;
			score += 1;
		}
		return score;
	}

	// UPDATED CHECKLEGALMOVE
	public boolean checkLegalMove() {
		if (currentPlayer.getCurrentPiece() != null) { // if there is a piece on
														// the mouse
			int CoordX = currentPlayer.getPieceX();
			int CoordY = currentPlayer.getPieceY();
			// try{
			int color1 = currentPlayer.getCurrentPiece().getPrimaryHexagon().getColor();
			int color2 = currentPlayer.getCurrentPiece().getSecondaryHexagon().getColor();
			if (CoordX > -1 && CoordY > -1) {
				if (currentPlayer.getOrientation() == 0) {
					if (CoordX > 0 && CoordY > 0) {
						if (grid[CoordX][CoordY] == -1 && grid[(CoordX - 1)][(CoordY - 1)] == -1) {
							if (checkAround(color1, CoordX, CoordY) || checkAround(color2, CoordX - 1, CoordY - 1))
								return true;
						}
					}
				} else if (currentPlayer.getOrientation() == 1) {
					if (CoordX < 29 && CoordY > 0) {
						if (grid[CoordX][CoordY] == -1 && grid[(CoordX + 1)][(CoordY - 1)] == -1) {
							if (checkAround(color1, CoordX, CoordY) || checkAround(color2, CoordX + 1, CoordY - 1))
								return true;
						}
					}
				} else if (currentPlayer.getOrientation() == 2) {
					if (CoordX < 28) {
						if (grid[CoordX][CoordY] == -1 && grid[(CoordX + 2)][(CoordY)] == -1) {
							if (checkAround(color1, CoordX, CoordY) || checkAround(color2, CoordX + 2, CoordY))
								return true;
						}
					}
				} else if (currentPlayer.getOrientation() == 3) {
					if (CoordX < 29 && CoordY < 14)
						if (grid[CoordX][CoordY] == -1 && grid[(CoordX + 1)][(CoordY + 1)] == -1) {
							if (checkAround(color1, CoordX, CoordY) || checkAround(color2, CoordX + 1, CoordY + 1))
								return true;
						}

				} else if (currentPlayer.getOrientation() == 4) {
					if (CoordX > 0 && CoordY < 14) {
						if (grid[CoordX][CoordY] == -1 && grid[(CoordX - 1)][(CoordY + 1)] == -1) {
							if (checkAround(color1, CoordX, CoordY) || checkAround(color2, CoordX - 1, CoordY + 1))
								return true;
						}
					}
				} else if (currentPlayer.getOrientation() == 5) {
					if (CoordX > 1) {
						if (grid[CoordX][CoordY] == -1 && grid[(CoordX - 2)][(CoordY)] == -1) {
							if (checkAround(color1, CoordX, CoordY) || checkAround(color2, CoordX - 2, CoordY))
								return true;
						}
					}
				}
			}
			// }catch(Exception ex){
			// return true;
			// }
		} // else{}
		return false;
	}

	public boolean checkLegalMove(int o, int x, int y) {
		if (currentPlayer.getCurrentPiece() != null) {
			int CoordX = x;
			int CoordY = y;
			int color1 = currentPlayer.getCurrentPiece().getPrimaryHexagon().getColor();
			int color2 = currentPlayer.getCurrentPiece().getSecondaryHexagon().getColor();
			if (CoordX > -1 && CoordY > -1) {
				if (currentPlayer.getOrientation() == 0) {
					if (CoordX > 0 && CoordY > 0) {
						if (grid[CoordX][CoordY] == -1 && grid[(CoordX - 1)][(CoordY - 1)] == -1) {
							if (checkAround(color1, CoordX, CoordY) || checkAround(color2, CoordX - 1, CoordY - 1))
								return true;
						}
					}
				} else if (currentPlayer.getOrientation() == 1) {
					if (CoordX < 29 && CoordY > 0) {
						if (grid[CoordX][CoordY] == -1 && grid[(CoordX + 1)][(CoordY - 1)] == -1) {
							if (checkAround(color1, CoordX, CoordY) || checkAround(color2, CoordX + 1, CoordY - 1))
								return true;
						}
					}
				} else if (currentPlayer.getOrientation() == 2) {
					if (CoordX < 28) {
						if (grid[CoordX][CoordY] == -1 && grid[(CoordX + 2)][(CoordY)] == -1) {
							if (checkAround(color1, CoordX, CoordY) || checkAround(color2, CoordX + 2, CoordY))
								return true;
						}
					}
				} else if (currentPlayer.getOrientation() == 3) {
					if (CoordX < 29 && CoordY < 14)
						if (grid[CoordX][CoordY] == -1 && grid[(CoordX + 1)][(CoordY + 1)] == -1) {
							if (checkAround(color1, CoordX, CoordY) || checkAround(color2, CoordX + 1, CoordY + 1))
								return true;
						}

				} else if (currentPlayer.getOrientation() == 4) {
					if (CoordX > 0 && CoordY < 14) {
						if (grid[CoordX][CoordY] == -1 && grid[(CoordX - 1)][(CoordY + 1)] == -1) {
							if (checkAround(color1, CoordX, CoordY) || checkAround(color2, CoordX - 1, CoordY + 1))
								return true;
						}
					}
				} else if (currentPlayer.getOrientation() == 5) {
					if (CoordX > 1) {
						if (grid[CoordX][CoordY] == -1 && grid[(CoordX - 2)][(CoordY)] == -1) {
							if (checkAround(color1, CoordX, CoordY) || checkAround(color2, CoordX - 2, CoordY))
								return true;
						}
					}
				}
			}
		}
		return false;
	}

	public boolean checkLegalMove(int o, int x, int y, int color1, int color2) {
		int CoordX = x;
		int CoordY = y;
		if (CoordX > -1 && CoordY > -1) {
			if (o == 0) {
				if (CoordX > 0 && CoordY > 0) {
					if (grid[CoordX][CoordY] == -1 && grid[(CoordX - 1)][(CoordY - 1)] == -1) {
						if (checkAround(color1, CoordX, CoordY) || checkAround(color2, CoordX - 1, CoordY - 1))
							return true;
					}
				}
			} else if (o == 1) {
				if (CoordX < 29 && CoordY > 0) {
					if (grid[CoordX][CoordY] == -1 && grid[(CoordX + 1)][(CoordY - 1)] == -1) {
						if (checkAround(color1, CoordX, CoordY) || checkAround(color2, CoordX + 1, CoordY - 1))
							return true;
					}
				}
			} else if (o == 2) {
				if (CoordX < 28) {
					if (grid[CoordX][CoordY] == -1 && grid[(CoordX + 2)][(CoordY)] == -1) {
						if (checkAround(color1, CoordX, CoordY) || checkAround(color2, CoordX + 2, CoordY))
							return true;
					}
				}
			} else if (o == 3) {
				if (CoordX < 29 && CoordY < 14)
					if (grid[CoordX][CoordY] == -1 && grid[(CoordX + 1)][(CoordY + 1)] == -1) {
						if (checkAround(color1, CoordX, CoordY) || checkAround(color2, CoordX + 1, CoordY + 1))
							return true;
					}

			} else if (o == 4) {
				if (CoordX > 0 && CoordY < 14) {
					if (grid[CoordX][CoordY] == -1 && grid[(CoordX - 1)][(CoordY + 1)] == -1) {
						if (checkAround(color1, CoordX, CoordY) || checkAround(color2, CoordX - 1, CoordY + 1))
							return true;
					}
				}
			} else if (o == 5) {
				if (CoordX > 1) {
					if (grid[CoordX][CoordY] == -1 && grid[(CoordX - 2)][(CoordY)] == -1) {
						if (checkAround(color1, CoordX, CoordY) || checkAround(color2, CoordX - 2, CoordY))
							return true;
					}
				}
			}
		}

		return false;
	}

	private boolean checkAround(int color, int x, int y) {
		boolean legal = false;
		for (int i = 0; i < 6; i++) {
			if (((i == 0 || i == 1) && y < 1) || ((i == 3 || i == 4) && y > 13) || ((i == 0 || i == 4) && x < 1)
					|| ((i == 1 || i == 3) && x > 28) || (i == 2 && x > 27) || (i == 5 && x < 3)) {
			} else {
				if (grid[getSecondX(i, x, y)][getSecondY(i, x, y)] == color) {
					legal = true;
				}
			}
		}
		return legal;
	}

	public Player[] getPlayers() {
		return players;
	}

	public int numPlayers() {
		return players.length;
	}

	public int getSecondX(int o, int x, int y) {
		if (o == 0) {
			return x - 1;
		} else if (o == 1) {
			return x + 1;
		} else if (o == 2) {
			return x + 2;
		} else if (o == 3) {
			return x + 1;
		} else if (o == 4) {
			return x - 1;
		} else if (o == 5) {
			return x - 2;
		}
		return -1;
	}

	public int getSecondY(int o, int x, int y) {
		if (o == 0) {
			return y - 1;
		} else if (o == 1) {
			return y - 1;
		} else if (o == 2) {
			return y;
		} else if (o == 3) {
			return y + 1;
		} else if (o == 4) {
			return y + 1;
		} else if (o == 5) {
			return y;
		}
		return -1;
	}

	public int[][] twoHexGrid(int o, int x, int y) {
		int[][] grid = new int[30][15];
		for (int i = 0; i < 30; i++) {
			for (int j = 0; j < 15; j++) {
				grid[i][j] = 0;
			}
		}
		// int CoordX = currentPlayer.getPieceX();
		// int CoordY = currentPlayer.getPieceY();
		int CoordX = x;
		int CoordY = y;
		grid[x][y] = currentPlayer.getCurrentPiece().getPrimaryHexagon().getColor();
		if (o == 0) {
			grid[(CoordX - 1)][(CoordY - 1)] = currentPlayer.getCurrentPiece().getSecondaryHexagon().getColor();
		} else if (o == 1) {
			grid[(CoordX + 1)][(CoordY - 1)] = currentPlayer.getCurrentPiece().getSecondaryHexagon().getColor();
		} else if (o == 2) {
			grid[(CoordX + 2)][(CoordY)] = currentPlayer.getCurrentPiece().getSecondaryHexagon().getColor();
		} else if (o == 3) {
			grid[(CoordX + 1)][(CoordY + 1)] = currentPlayer.getCurrentPiece().getSecondaryHexagon().getColor();
		} else if (o == 4) {
			grid[(CoordX - 1)][(CoordY + 1)] = currentPlayer.getCurrentPiece().getSecondaryHexagon().getColor();
		} else if (o == 5) {
			grid[(CoordX - 2)][(CoordY)] = currentPlayer.getCurrentPiece().getSecondaryHexagon().getColor();
		}
		return grid;
	}

	public int[][] twoHexGrid(int o, int x, int y, int color1, int color2) {
		int[][] grid = new int[30][15];
		for (int i = 0; i < 30; i++) {
			for (int j = 0; j < 15; j++) {
				grid[i][j] = 0;
			}
		}
		// int CoordX = currentPlayer.getPieceX();
		// int CoordY = currentPlayer.getPieceY();
		int CoordX = x;
		int CoordY = y;
		grid[x][y] = color1;
		if (o == 0) {
			grid[(CoordX - 1)][(CoordY - 1)] = color2;
		} else if (o == 1) {
			grid[(CoordX + 1)][(CoordY - 1)] = color2;
		} else if (o == 2) {
			grid[(CoordX + 2)][(CoordY)] = color2;
		} else if (o == 3) {
			grid[(CoordX + 1)][(CoordY + 1)] = color2;
		} else if (o == 4) {
			grid[(CoordX - 1)][(CoordY + 1)] = color2;
		} else if (o == 5) {
			grid[(CoordX - 2)][(CoordY)] = color2;
		}
		return grid;
	}

	public boolean isWinner() {
		try {
			boolean isWinner = true;
			for (int s = 0; s < 6; s++) {
				if (currentPlayer.getScores()[s] < 18) {
					isWinner = false;
				}
			}
			return isWinner;
		} catch (Exception e) {
			return false;
		}
	}

	public boolean isMoveRemaining() {
		boolean isMove = false;// need boolean in loop
		try {
			for (int x = 0; x < 30 && !isMove; x++) {
				for (int y = 0; y < 15 && !isMove; y++) {
					for (int o = 0; o < 6 && !isMove; o++) {
						for (int piece = 0; piece < currentPlayer.getHand().getSize(); piece++) {
							if (checkLegalMove(o, x, y,
									currentPlayer.getHand().getPiece(piece).getPrimaryHexagon().getColor(),
									currentPlayer.getHand().getPiece(piece).getSecondaryHexagon().getColor())) {
								isMove = true;
							}
						}
					}
				}
			}
			return isMove;
		} catch (Exception e) {
			return isMove;
		}

	}

}
