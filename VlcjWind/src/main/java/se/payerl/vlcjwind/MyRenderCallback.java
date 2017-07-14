package se.payerl.vlcjwind;

import java.awt.image.BufferedImage;
import java.util.List;

import com.sun.jna.Memory;

import uk.co.caprica.vlcj.player.direct.BufferFormat;
import uk.co.caprica.vlcj.player.direct.DirectMediaPlayer;
import uk.co.caprica.vlcj.player.direct.RenderCallback;

public class MyRenderCallback implements RenderCallback
{
	private List<FrameListener> videoSurfaces;
	private int width, height;
	private BufferedImage image;
	private int[] rgbBuffer;
	
    public MyRenderCallback(List<FrameListener> listeners, int width, int height)
    {
    	this.width = width;
    	this.height = height;
        this.videoSurfaces = listeners;
		this.image = new BufferedImage(this.width, this.height, BufferedImage.TYPE_INT_ARGB);
		rgbBuffer = new int[image.getWidth() * image.getHeight()];
    }
    
	public void display(DirectMediaPlayer mediaPlayer, Memory[] nativeBuffers, BufferFormat bufferFormat) {
		synchronized (this.rgbBuffer) {
			nativeBuffers[0].getByteBuffer(0L, nativeBuffers[0].size()).asIntBuffer().get(rgbBuffer, 0, bufferFormat.getHeight() * bufferFormat.getWidth());
			image.setRGB(0, 0, image.getWidth(), image.getHeight(), rgbBuffer, 0, image.getWidth());
		}
		
		for(FrameListener fl: videoSurfaces) {
			fl.newFrameRecieved(this.image);
		}
	}
}