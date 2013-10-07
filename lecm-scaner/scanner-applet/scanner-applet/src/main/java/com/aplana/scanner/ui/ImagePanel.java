package com.aplana.scanner.ui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JComponent;

/**
 * <code>Component</code> to display an image.
 *
 * @author <a href="mailto:ogalkin@aplana.com">Oleg Galkin</a>
 */
public class ImagePanel extends JComponent {
	private static final long serialVersionUID = 1699783786819629637L;
	
	private Image image;

	/**
	 * Sets the image to display.
	 *
	 * @param image the image to set
	 */
	public void setImage(Image image) {
		this.image = image;
		revalidate();
		repaint();
	}

	/* (non-Javadoc)
	 * @see javax.swing.JComponent#getPreferredSize()
	 */
	@Override
	public Dimension getPreferredSize() {
		return image != null ?
						new Dimension(image.getWidth(null), image.getHeight(null)) : new Dimension();
	}

	/* (non-Javadoc)
	 * @see javax.swing.JComponent#paint(java.awt.Graphics)
	 */
	@Override
	public void paint(Graphics g) {
		if (image != null)
			g.drawImage(image, 0, 0, null);
		else
			super.paint(g);
	}
}
