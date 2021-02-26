import java.awt.*;
import java.awt.geom.*;
import java.util.*;
import javax.swing.*;

/*
 * control the game logic
 */
public class Game extends Thread {
	private Grid grid;
	private int level = 1;
	private int lives;
	private double filled = 0;
	private StatusBar statusBar;

	private static final int UPDATE_RATE = 30;
	private static final double MIN_AREA = 0.75;
	private static final int MAX_BALLS = 50;

	public Game(Grid grid, StatusBar statusBar) {
		this.grid = grid;
		this.statusBar = statusBar;
	}

	public void run() {
		int nballs;
		Random rand = new Random();
		long timeBegin, timeTaken, timeLeft;
		boolean win;

		while (true) {
			win = false;
			grid.clear();
			setLives(level);
			nballs = Math.min(level, MAX_BALLS);
			setFilled(0);

			for (int i = 0; i < nballs; i++) {
				double x = rand.nextDouble() * (grid.getWidth() - Ball.getSize() * 2) + Ball.getSize();
				double y = rand.nextDouble() * (grid.getHeight() - Ball.getSize() * 2) + Ball.getSize();

				grid.addBall(new Ball(x, y, rand.nextBoolean(), rand.nextBoolean()));
			}

			while (lives > 0 && !win) {
				timeBegin = System.currentTimeMillis();

				/* execute one game step and refresh the display */
				win = update();
				grid.repaint();

				/* provide the necessary delay to meet the target rate */
				timeTaken = System.currentTimeMillis() - timeBegin;
				timeLeft = Math.max(5, 1000L / UPDATE_RATE - timeTaken);

				/* delay */
				pause(timeLeft);
			}
			if (!win) {
				int answer = JOptionPane.showConfirmDialog(null, "Game Over. Continue?", "Game Over", JOptionPane.YES_NO_OPTION);
				if (answer == JOptionPane.NO_OPTION) {
					System.exit(0);
				}
			}
			setLevel(win ? level + 1 : 1);
		}
	}

	public void setLives(int lives) {
		this.lives = lives;
		statusBar.getLivesLabel().setText("Lives: " + lives);
	}

	public void setLevel(int level) {
		this.level = level;
		statusBar.getLevelLabel().setText("Level: " + level);
	}

	public void setFilled(double filled) {
		this.filled = filled;
		statusBar.getFilledLabel().setText("Filled: " + String.format("%.2f", filled * 100) + "%");
	}

	public int getLevel() {
		return level;
	}

	public int getBalls() {
		return Math.min(level, MAX_BALLS);
	}

	public int getLives() {
		return lives;
	}

	private void pause(long time) {
		try {
			sleep(time);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private boolean update() {
		boolean walldeleted = false;
		Iterator<Wall> wallIt = grid.getWalls().iterator();

		while (wallIt.hasNext()) {
			Wall wall = wallIt.next();
			Iterator<WallExtension> extensionIt = wall.getWallExtensions().iterator();
			while (extensionIt.hasNext()) {
				WallExtension extension = extensionIt.next();
				extension.extend();
				if (looseLife(extension)) {
					walldeleted = true;
					for (CapturedArea capturedArea : wall.getWallAreas()) {
						grid.addCapturedArea(capturedArea);
					}
					setLives(lives - 1);
					wallIt.remove();
					break;
				}
				reachExtension(wall, extension);
			}
			if (!walldeleted && wall.isConstructed()) {
				captureArea(wall);
				wallIt.remove();
			}
		}
		for (Ball ball : grid.getBalls()) {
			collision(ball);
			ball.step();
		}
		return checkWin();
	}

	private boolean checkWin() {
		double area = 0;
		double totalArea = grid.getWidth() * grid.getHeight();

		for (CapturedArea capturedArea : grid.getCapturedAreas()) {
			double areaX = capturedArea.getX();
			double areaY = capturedArea.getY();
			double areaW = capturedArea.getWidth();
			double areaH = capturedArea.getHeight();

			if (areaX + areaW > grid.getWidth())
				areaW = grid.getWidth() - areaX;
			if (areaY + areaH > grid.getHeight())
				areaH = grid.getHeight() - areaY;
			if (areaX < 0)
				areaW += areaX;
			if (areaY < 0)
				areaH += areaY;

			area += areaW * areaH;
		}
		setFilled(area / totalArea);
		if (filled >= MIN_AREA)
			return true;
		return false;
	}

	private boolean looseLife(WallExtension extension) {
		for (Ball ball : grid.getBalls()) {
			if (ball.intersects(extension)) {
				return true;
			}
		}
		return false;
	}

	private void captureArea(Wall wall) {
		Orientation orientation = wall.getOrientation();
		double wallX = wall.getX();
		double wallY = wall.getY();
		double wallW = wall.getWidth();
		double wallH = wall.getHeight();
		boolean captureNorth = true;
		boolean captureSouth = true;
		boolean captureWest = true;
		boolean captureEast = true;
		boolean checkNorth = true;
		boolean checkSouth = true;
		boolean checkWest = true;
		boolean checkEast = true;

		switch (orientation) {
		case VERTICAL:
			captureNorth = false;
			captureSouth = false;
			break;
		case HORIZONTAL:
			captureWest = false;
			captureEast = false;
			break;
		}

		Iterator<CapturedArea> it = grid.getCapturedAreas().iterator();
		while (it.hasNext()) {
			CapturedArea capturedArea = it.next();
			boolean removeArea = false;
			double areaX = capturedArea.getX();
			double areaY = capturedArea.getY();
			double areaW = capturedArea.getWidth();
			double areaH = capturedArea.getHeight();
			if (orientation == Orientation.VERTICAL && areaX == wallX && areaW == wallW) {
				if (areaY < wallY) {
					wallY = areaY;
				}
				wallH += areaH;
				removeArea = true;
			}
			if (orientation == Orientation.HORIZONTAL && areaY == wallY && areaH == wallH) {
				if (areaX < wallX) {
					wallX = areaX;
				}
				wallW += areaW;
				removeArea = true;
			}
			if (removeArea)
				it.remove();
		}

		for (Ball ball : grid.getBalls()) {
			double centerX = ball.getCenterX();
			double centerY = ball.getCenterY();
			double radius = ball.getRadius();
			switch (orientation) {
			case VERTICAL:
				if (centerY + radius >= wallY && centerY - radius <= wallY + wallH) {
					if (centerX > wallX + wallW) {
						boolean checked = false;
						captureEast = false;
						for (CapturedArea capturedArea : grid.getCapturedAreas()) {
							double areaX = capturedArea.getX();
							double areaY = capturedArea.getY();
							double areaW = capturedArea.getWidth();
							double areaH = capturedArea.getHeight();

							if (centerY + radius >= areaY &&
							    centerY - radius <= areaY + areaH &&
							    centerX > areaX + areaW/2 &&
							    areaX + areaW/2 > wallX + wallW) {
								checked = true;
								break;
							}
						}
						checkEast &= checked;
					} else if (centerX < wallX) {
						boolean checked = false;
						captureWest = false;
						for (CapturedArea capturedArea : grid.getCapturedAreas()) {
							double areaX = capturedArea.getX();
							double areaY = capturedArea.getY();
							double areaW = capturedArea.getWidth();
							double areaH = capturedArea.getHeight();

							if (centerY + radius >= areaY &&
							    centerY - radius <= areaY + areaH &&
							    centerX < areaX + areaW/2 &&
							    areaX + areaW/2 < wallX) {
								checked = true;
								break;
							}
						}
						checkWest &= checked;
					}
				}
				break;
			case HORIZONTAL:
				if (centerX + radius >= wallX && centerX - radius <= wallX + wallW) {
					if (centerY > wallY + wallH) {
						boolean checked = false;
						captureSouth = false;
						for (CapturedArea capturedArea : grid.getCapturedAreas()) {
							double areaX = capturedArea.getX();
							double areaY = capturedArea.getY();
							double areaW = capturedArea.getWidth();
							double areaH = capturedArea.getHeight();

							if (centerX + radius >= areaX &&
							    centerX - radius <= areaX + areaW &&
							    centerY > areaY + areaH/2 &&
							    areaY + areaH/2 > wallY + wallH) {
								checked = true;
								break;
							}
						}
						checkSouth &= checked;
					} else if (centerY < wallY) {
						boolean checked = false;
						captureNorth = false;
						for (CapturedArea capturedArea : grid.getCapturedAreas()) {
							double areaX = capturedArea.getX();
							double areaY = capturedArea.getY();
							double areaW = capturedArea.getWidth();
							double areaH = capturedArea.getHeight();

							if (centerX + radius >= areaX &&
							    centerX - radius <= areaX + areaW &&
							    centerY < areaY + areaH/2 &&
							    areaY + areaH/2 < wallY) {
								checked = true;
								break;
							}
						}
						checkNorth &= checked;
					}
				}
				break;
			}
		}

		switch (orientation) {
		case VERTICAL:
			if (!captureWest)
				captureWest = checkWest;
			if (!captureEast)
				captureEast = checkEast;
			break;
		case HORIZONTAL:
			if (!captureNorth)
				captureNorth = checkNorth;
			if (!captureSouth)
				captureSouth = checkSouth;
			break;
		}

		if (captureWest) {
			double x = 0;
			double y = wallY;
			double w = wallX + wallW;
			double h = wallH;

			for (CapturedArea capturedArea : grid.getCapturedAreas()) {
				double areaX = capturedArea.getX();
				double areaY = capturedArea.getY();
				double areaW = capturedArea.getWidth();
				double areaH = capturedArea.getHeight();

				if (((areaY < wallY && areaY + areaH > wallY) ||
				     (areaY < wallY + wallH && areaY + areaH > wallY) ||
				     (areaY > wallY && areaY + areaH < wallY + wallH)) &&
				     (areaX + areaW < wallX)) {
					x = Math.max(x, areaX + areaW);
					w = wallX + wallW - x;
				}
			}
			grid.addCapturedArea(new CapturedArea(x, y, w, h));
		}
		if (captureEast) {
			double x = wallX;
			double y = wallY;
			double w = grid.getWidth() - wallX + wallW;
			double h = wallH;

			for (CapturedArea capturedArea : grid.getCapturedAreas()) {
				double areaX = capturedArea.getX();
				double areaY = capturedArea.getY();
				double areaW = capturedArea.getWidth();
				double areaH = capturedArea.getHeight();

				if (((areaY < wallY && areaY + areaH > wallY) ||
				     (areaY < wallY + wallH && areaY + areaH > wallY) ||
				     (areaY > wallY && areaY + areaH < wallY + wallH)) &&
				     (areaX > wallX + wallW)) {
					w = Math.min(w, areaX - wallX + wallW);
				}
			}
			grid.addCapturedArea(new CapturedArea(x, y, w, h));
		}
		if (captureNorth) {
			double x = wallX;
			double y = 0;
			double w = wallW;
			double h = wallY + wallH;

			for (CapturedArea capturedArea : grid.getCapturedAreas()) {
				double areaX = capturedArea.getX();
				double areaY = capturedArea.getY();
				double areaW = capturedArea.getWidth();
				double areaH = capturedArea.getHeight();

				if (((areaX < wallX && areaX + areaW > wallX) ||
				     (areaX < wallX + wallW && areaX + areaW > wallX) ||
				     (areaX > wallX && areaX + areaW < wallX + wallW)) &&
				     (areaY + areaH < wallY)) {
					y = Math.max(y, areaY + areaH);
					h = wallY + wallH - y;
				}
			}
			grid.addCapturedArea(new CapturedArea(x, y, w, h));
		}
		if (captureSouth) {
			double x = wallX;
			double y = wallY;
			double w = wallW;
			double h = grid.getHeight() - wallY + wallH;

			for (CapturedArea capturedArea : grid.getCapturedAreas()) {
				double areaX = capturedArea.getX();
				double areaY = capturedArea.getY();
				double areaW = capturedArea.getWidth();
				double areaH = capturedArea.getHeight();

				if (((areaX < wallX && areaX + areaW > wallX) ||
				     (areaX < wallX + wallW && areaX + areaW > wallX) ||
				     (areaX > wallX && areaX + areaW < wallX + wallW)) &&
				     (areaY > wallY + wallH)) {
					h = Math.min(h, areaY - wallY + wallH);
				}
			}
			grid.addCapturedArea(new CapturedArea(x, y, w, h));
		}
		if (!captureWest && !captureEast && !captureSouth && !captureNorth) {
			grid.addCapturedArea(new CapturedArea(wallX, wallY, wallW, wallH));
		}

	}

	private void reachExtension(Wall wall, WallExtension extension) {
		double extensionX = extension.getX();
		double extensionY = extension.getY();
		double extensionW = extension.getWidth();
		double extensionH = extension.getHeight();

		for (CapturedArea capturedArea : grid.getCapturedAreas()) {
			double x = capturedArea.getX();
			double y = capturedArea.getY();
			double w = capturedArea.getWidth();
			double h = capturedArea.getHeight();

			if (extension.intersects(capturedArea)) {
				wall.construct(extension);
				return;
			}
		}
	}

	private void collision(Ball ball) {
		boolean switchX = false;
		boolean switchY = false;
		double radius = ball.getRadius();
		double centerX = ball.getCenterX();
		double centerY = ball.getCenterY();

		for (CapturedArea capturedArea : grid.getCapturedAreas()) {
			double areaX = capturedArea.getX();
			double areaY = capturedArea.getY();
			double areaW = capturedArea.getWidth();
			double areaH = capturedArea.getHeight();

			if (centerY >= areaY && centerY <= areaY + areaH && centerX + radius >= areaX && centerX + radius < areaX + areaW) {
				switchX = true;
			}
			if (centerY >= areaY && centerY <= areaY + areaH && centerX - radius <= areaX + areaW && centerX - radius > areaX) {
				switchX = true;
			}
			if (centerX >= areaX && centerX <= areaX + areaW && centerY + radius >= areaY && centerY + radius < areaY + areaH) {
				switchY = true;
			}
			if (centerX >= areaX && centerX <= areaX + areaW && centerY - radius <= areaY + areaH && centerY - radius > areaY) {
				switchY = true;
			}
		}

		for (Wall wall : grid.getWalls()) {
			for (CapturedArea capturedArea : wall.getWallAreas()) {
				double areaX = capturedArea.getX();
				double areaY = capturedArea.getY();
				double areaW = capturedArea.getWidth();
				double areaH = capturedArea.getHeight();

				if (centerY >= areaY && centerY <= areaY + areaH && centerX + radius >= areaX && centerX + radius < areaX + areaW) {
					switchX = true;
				}
				if (centerY >= areaY && centerY <= areaY + areaH && centerX - radius <= areaX + areaW && centerX - radius > areaX) {
					switchX = true;
				}
				if (centerX >= areaX && centerX <= areaX + areaW && centerY + radius >= areaY && centerY + radius < areaY + areaH) {
					switchY = true;
				}
				if (centerX >= areaX && centerX <= areaX + areaW && centerY - radius <= areaY + areaH && centerY - radius > areaY) {
					switchY = true;
				}
			}
		}

		if (switchX || switchY) {
			ball.unstep();
		}
		if (switchX) {
			ball.switchDirectionX();
		}
		if (switchY) {
			ball.switchDirectionY();
		}
	}
}
