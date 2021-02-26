import java.awt.*;
import java.awt.geom.*;
import javax.swing.*;

public class Sprite {
	private RectangularShape shape;
	private Color color;

	public Sprite(RectangularShape shape, Color color) {
		this.shape = shape;
		this.color = color;
	}

	public RectangularShape getShape() {
		return shape;
	}

	public double getX() {
		return shape.getX();
	}

	public double getY() {
		return shape.getY();
	}

	public double getWidth() {
		return shape.getWidth();
	}

	public double getHeight() {
		return shape.getHeight();
	}

	public void draw(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		g2.setColor(color);
		g2.fill(shape);
	}

	public void moveRelative(double dx, double dy) {
		shape.setFrame(shape.getX() + dx, shape.getY() + dy, shape.getWidth(), shape.getHeight());
	}

	public void moveResize(double x, double y, double w, double h) {
		shape.setFrame(x, y, w, h);
	}

	public boolean intersects(Sprite other) {
		return this.shape.intersects((Rectangle2D.Double)other.shape);
	}
}
