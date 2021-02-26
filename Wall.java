import java.util.List;
import java.util.*;

/**
 * the orientation (vertical or horizontal) of a wall
 */
enum Orientation {
	VERTICAL, HORIZONTAL;
}

/**
 * a Wall object exists while it is in construction
 */
public class Wall {
	private Orientation orientation;
	private WallExtension extension1;
	private WallExtension extension2;
	private CapturedArea area1;
	private CapturedArea area2;

	public Wall(double x, double y, Orientation orientation) {
		x = ((int)x / 16) * 16;
		y = ((int)y / 16) * 16;
		switch (orientation) {
		case VERTICAL:
			this.extension1 = new WallExtension(x, y, Direction.NORTH);
			this.extension2 = new WallExtension(x, y, Direction.SOUTH);
			break;
		case HORIZONTAL:
			this.extension1 = new WallExtension(x, y, Direction.WEST);
			this.extension2 = new WallExtension(x, y, Direction.EAST);
			break;
		}
		this.area1 = null;
		this.area2 = null;
		this.orientation = orientation;
	}

	public Orientation getOrientation() {
		return orientation;
	}

	public List<WallExtension> getWallExtensions() {
		List<WallExtension> list = new ArrayList<WallExtension>();

		if (extension1 != null)
			list.add(extension1);
		if (extension2 != null)
			list.add(extension2);
		return list;
	}

	public List<CapturedArea> getWallAreas() {
		List<CapturedArea> list = new ArrayList<CapturedArea>();

		if (area1 != null)
			list.add(area1);
		if (area2 != null)
			list.add(area2);
		return list;
	}

	public void construct(WallExtension extension) {
		double x = extension.getX();
		double y = extension.getY();
		double w = extension.getWidth();
		double h = extension.getHeight();

		if (extension == extension1) {
			area1 = new CapturedArea(x, y, w, h);
			extension1 = null;
		} else {
			area2 = new CapturedArea(x, y, w, h);
			extension2 = null;
		}
	}

	public boolean isConstructed() {
		if (area1 != null && area2 != null)
			return true;
		return false;
	}

	public double getX() {
		if (extension1 != null) {
			return extension1.getX();
		} else {
			return area1.getX();
		}
	}

	public double getY() {
		if (extension1 != null) {
			return extension1.getY();
		} else {
			return area1.getY();
		}
	}

	public double getWidth() {
		double w = 0;
		switch (orientation) {
		case VERTICAL:
			if (extension1 != null) {
				w = extension1.getWidth();
			} else {
				w = area1.getWidth();
			}
			break;
		case HORIZONTAL:
			if (extension1 != null) {
				w = extension1.getWidth();
			} else {
				w = area1.getWidth();
			}
			if (extension2 != null) {
				w += extension2.getWidth();
			} else {
				w += area2.getWidth();
			}
		}
		return w;
	}

	public double getHeight() {
		double h = 0;
		switch (orientation) {
		case VERTICAL:
			if (extension1 != null) {
				h = extension1.getHeight();
			} else {
				h = area1.getHeight();
			}
			if (extension2 != null) {
				h += extension2.getHeight();
			} else {
				h += area2.getHeight();
			}
			break;
		case HORIZONTAL:
			if (extension1 != null) {
				h = extension1.getHeight();
			} else {
				h = area1.getHeight();
			}
		}
		return h;
	}
}
