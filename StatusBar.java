import java.awt.*;
import javax.swing.*;

/**
 * The status bar displaying gmae information
 */
public class StatusBar extends JPanel {
	private JLabel levelLabel;
	private JLabel filledLabel;
	private JLabel livesLabel;

	public StatusBar() {
		levelLabel = new JLabel("Level: 0");
		filledLabel = new JLabel("Filled: 0");
		livesLabel = new JLabel("Lives: 0");

		add(levelLabel);
		add(filledLabel);
		add(livesLabel);
	}

	public JLabel getLevelLabel() {
		return levelLabel;
	}

	public JLabel getLivesLabel() {
		return livesLabel;
	}

	public JLabel getFilledLabel() {
		return filledLabel;
	}
}
