import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * Main window for the game and main() entry point
 */
public class JBounce extends JFrame {
	private Grid grid;
	private Game game;
	private StatusBar statusBar;

	private static final String TITLE = "JBounce";
	private static final int WIDTH = 640;
	private static final int HEIGHT = 480;

	public JBounce() {
		Container cp = getContentPane();

		/* instantiate stuff */
		grid = new Grid(WIDTH, HEIGHT);
		statusBar = new StatusBar();
		game = new Game(grid, statusBar);

		/* add Panels to content pane */
		cp.setLayout(new BorderLayout());
		cp.add(grid, BorderLayout.CENTER);
		cp.add(statusBar, BorderLayout.SOUTH);

		/* set main window */
		setTitle(TITLE);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
		setVisible(true);

		/* start game */
		game.start();
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new JBounce();
			}
		});
	}
}
