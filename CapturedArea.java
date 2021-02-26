import java.awt.*;
import java.awt.geom.*;
import javax.swing.*;

/**
 * area captured by a successfuly constructed wall
 */
public class CapturedArea extends Sprite {
	public CapturedArea(double x, double y, double w, double h) {
		super(new Rectangle2D.Double(x, y, w, h), Color.BLACK);
	}

	/**
	 * returns true if a given point is inside the captured area
	 */
	public boolean isPointCaptured(double x, double y) {
		double minX = getX();
		double minY = getY();
		double maxX = getX() + getWidth();
		double maxY = getY() + getHeight();

		if (x + 16 >= minX && x - 16 <= maxX && y + 16 >= minY && y - 16 <= maxY) {
			return true;
		}
		return false;
	}
}
