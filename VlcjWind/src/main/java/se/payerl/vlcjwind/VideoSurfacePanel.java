package se.payerl.vlcjwind;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class VideoSurfacePanel extends JPanel implements FrameListener
{
	private Dimension size;
	private BufferedImage image;
    
    public VideoSurfacePanel(BufferedImage image, Dimension size)
    {
        setBackground(Color.black);
        setOpaque(true);
        setPreferredSize(size);
        setMinimumSize(size);
        setMaximumSize(size);
        this.size = size;
        this.image = image;
    }
    
    public BufferedImage getImage() {
		return this.image;
	}

	public void setImage(BufferedImage image) {
		this.image = image;;
	}

	public Dimension getSize() {
		return this.size;
	}

	public void setSize(Dimension size) {
		this.size = size;
	}

	@Override
    protected void paintComponent(Graphics g)
    {
        Graphics2D g2 = (Graphics2D)g;
        g2.drawImage(image, 0, 0, this.getWidth(), this.getHeight(), Color.black, null);
    }
	
	public void newFrameRecieved(BufferedImage image) {
		this.image = image;
		repaint();
	}
}