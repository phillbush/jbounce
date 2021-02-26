import java.awt.*;
import java.awt.geom.*;
import javax.swing.*;

/**
 * ball that bounces about the grid
 */
public class Ball extends Sprite {
	private boolean directionX;
	private boolean directionY;

	private static final double SPEED = 10;
	private static final double SIZE = 20;

	/**
	 * Constructor: directionX and directionY are booleans that
	 * specifies whether the speed in the given direction is
	 * positive.
	 */
	public Ball(double x, double y, boolean directionX, boolean directionY) {
		super(new Ellipse2D.Double(x, y, SIZE, SIZE), Color.GREEN);
		this.directionX = directionX;
		this.directionY = directionY;
	}

	public static double getSize() {
		return SIZE;
	}

	public double getCenterX() {
		return getX() + getWidth() / 2;
	}

	public double getCenterY() {
		return getY() + getWidth() / 2;
	}

	public double getRadius() {
		return getWidth() / 2;
	}

	public boolean getDirectionX() {
		return directionX;
	}

	public boolean getDirectionY() {
		return directionY;
	}

	public void setDirectionX(boolean direction) {
		directionX = direction;
	}

	public void setDirectionY(boolean direction) {
		directionY = direction;
	}

	public void switchDirectionX() {
		directionX = !directionX;
	}

	public void switchDirectionY() {
		directionY = !directionY;
	}

	public void step() {
		double dx = (directionX) ? SPEED : -SPEED;
		double dy = (directionY) ? SPEED : -SPEED;

		moveRelative(dx, dy);
	}

	public void unstep() {
		double dx = (directionX) ? -SPEED : SPEED;
		double dy = (directionY) ? -SPEED : SPEED;

		moveRelative(dx, dy);
	}
}
