package view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JColorChooser;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class ColorView extends JPanel implements MouseListener {

	public interface Delegate {
		/**
		 * Handles the new color event.
		 * 
		 * @param color
		 */
		public void colorChanged(Color color);
	}

	// MARK: - State

	private String label;
	private Color color;
	private boolean focused;

	private Delegate delegate;

	// MARK: - Constructor

	public ColorView(String label, Color color, Delegate delegate) {
		this.label = label;
		this.color = color;
		this.focused = false;
		this.delegate = delegate;

		this.addMouseListener(this);
	}

	// MARK: - View

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		// Canvas

		Graphics2D g2 = (Graphics2D) g;
		g2.setStroke(new BasicStroke(3));

		int width = this.getWidth();
		int height = this.getHeight();

		int size = Math.min(width, height);
		int x = Math.max(0, width - size) / 2;
		int y = Math.max(0, height - size) / 2;

		g.setColor(this.color);
		g.fillOval(x, y, size, size);
	}

	@Override
	public void mouseClicked(MouseEvent e) {

	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (!this.focused)
			return;

		Color color = JColorChooser.showDialog(this, this.label, this.color);

		this.delegate.colorChanged(color);
		this.color = color;

		this.repaint();
	}

	@Override
	public void mouseReleased(MouseEvent e) {

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		this.focused = true;
	}

	@Override
	public void mouseExited(MouseEvent e) {
		this.focused = false;
	}
}
