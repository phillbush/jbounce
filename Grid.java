import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.*;
import javax.swing.*;

public class Grid extends JPanel {
	private List<Ball> balls;
	private List<CapturedArea> capturedAreas;
	private List<Wall> walls;
	private Player player;
	private int width;
	private int height;

	public Grid(int width, int height) {
		player = new Player(this);
		this.balls = new ArrayList<Ball>();
		this.capturedAreas = new ArrayList<CapturedArea>();
		this.walls = new ArrayList<Wall>();
		this.width = width;
		this.height = height;
		this.addMouseListener(player);

		this.setOpaque(true);
		this.setSize(width, height);
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public List<Ball> getBalls() {
		return balls;
	}

	public List<CapturedArea> getCapturedAreas() {
		return capturedAreas;
	}

	public List<Wall> getWalls() {
		return walls;
	}

	public void addBall(Ball ball) {
		balls.add(ball);
	}

	public void addCapturedArea(CapturedArea capturedArea) {
		capturedAreas.add(capturedArea);
	}

	public void addWall(Wall wall) {
		walls.add(wall);
	}

	public void delWall(Wall wall) {
		walls.remove(wall);
	}

	public void clear() {
		balls.clear();
		capturedAreas.clear();
		walls.clear();

		capturedAreas.add(new CapturedArea(0, -100, width, 100));
		capturedAreas.add(new CapturedArea(-100, 0, 100, height));
		capturedAreas.add(new CapturedArea(0, height, width, 100));
		capturedAreas.add(new CapturedArea(width, 0, 100, height));
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.setColor(Color.ORANGE);
		g.fillRect(0, 0, width, height);
		g.setColor(Color.BLACK);
		g.drawRect(0, 0, width, height);
		for (Ball ball : balls) {
			ball.draw(g);
		}
		for (CapturedArea capturedArea : capturedAreas) {
			capturedArea.draw(g);
		}
		for (Wall wall : walls) {
			for (CapturedArea capturedArea : wall.getWallAreas()) {
				capturedArea.draw(g);
			}
			for (WallExtension extension : wall.getWallExtensions()) {
				extension.draw(g);
			}
		}
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(width, height);
	}
}
