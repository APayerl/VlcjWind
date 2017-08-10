/*
VlcjWind makes it easier to use VLCJ and it's DirectMediaPlayer for multiple frames
Copyright (C) 2017  Anders Payerl

VlcjWind is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

VlcjWind is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with VlcjWind.  If not, see <http://www.gnu.org/licenses/>.

For contact use email anders{dot}payerl{at}gmail{dot}com
*/

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
	private int width;
	private int height;
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
    
    @Override
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