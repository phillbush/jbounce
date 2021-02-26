import java.awt.*;
import java.awt.event.*;

public class Player implements MouseListener {
	private boolean vertical;
	private Grid grid;
	private Cursor verticalCursor;
	private Cursor horizontalCursor;

	public Player(Grid grid) {
		vertical = true;
		this.grid = grid;
		verticalCursor = new Cursor(Cursor.N_RESIZE_CURSOR);
		horizontalCursor = new Cursor(Cursor.E_RESIZE_CURSOR);
		grid.setCursor(verticalCursor);
	}

	@Override
	public void mouseClicked(MouseEvent ev) {
		int button = ev.getButton();

		if (button == ev.BUTTON1) {
			if (canAddWall(ev.getX(), ev.getY())) {
				grid.addWall(new Wall(ev.getX(), ev.getY(), vertical ? Orientation.VERTICAL : Orientation.HORIZONTAL));
			}
		} else if (button == ev.BUTTON3) {
			vertical = !vertical;
		}
		grid.setCursor(vertical ? verticalCursor : horizontalCursor);
	}

	private boolean canAddWall(int x, int y) {
		for (CapturedArea capturedArea : grid.getCapturedAreas()) {
			if (capturedArea.isPointCaptured(x, y)) {
				return false;
			}
		}
		return true;
	}

	/** not used, but need to provide an empty body to compile */
	@Override public void mousePressed(MouseEvent ev) { }
	@Override public void mouseReleased(MouseEvent ev) { }
	@Override public void mouseEntered(MouseEvent ev) { }
	@Override public void mouseExited(MouseEvent ev) { }
}
