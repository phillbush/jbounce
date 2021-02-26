import java.awt.*;
import java.awt.geom.*;
import javax.swing.*;

/**
 * direction a WallExtension extends to
 */
enum Direction {
	NORTH, SOUTH, WEST, EAST;

	public boolean isVertical() {
		if (this == NORTH || this == SOUTH)
			return true;
		return false;
	}

	public boolean isNegative() {
		if (this == NORTH || this == WEST)
			return true;
		return false;
	}
}

/**
 * extension of a wall to a given direction
 */
public class WallExtension extends Sprite {
	private Direction direction;

	private static final double HALFWIDTH = 16;
	private static final double SPEED = 20;

	public WallExtension(double x, double y, Direction direction) {
		super(new Rectangle2D.Double(direction.isVertical() ? x - HALFWIDTH : x,
		                             direction.isVertical() ? y : y - HALFWIDTH,
		                             direction.isVertical() ? HALFWIDTH * 2 : 0,
		                             direction.isVertical() ? 0 : HALFWIDTH * 2),
		                             direction.isNegative() ? Color.BLUE : Color.RED);
		this.direction = direction;
	}

	public Direction getDirection() {
		return this.direction;
	}

	public void extend() {
		double x = getX();
		double y = getY();
		double w = getWidth();
		double h = getHeight();

		switch (direction) {
		case NORTH:
			y -= SPEED;
			h += SPEED;
			break;
		case SOUTH:
			h += SPEED;
			break;
		case WEST:
			x -= SPEED;
			w += SPEED;
			break;
		case EAST:
			w += SPEED;
			break;
		}
		moveResize(x, y, w, h);
	}
}
