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

import java.util.ArrayList;
import java.util.List;

import com.sun.jna.NativeLibrary;

import uk.co.caprica.vlcj.binding.LibVlc;
import uk.co.caprica.vlcj.binding.internal.libvlc_media_t;
import uk.co.caprica.vlcj.component.DirectMediaPlayerComponent;
import uk.co.caprica.vlcj.discovery.NativeDiscovery;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.MediaPlayerEventListener;
import uk.co.caprica.vlcj.player.direct.BufferFormatCallback;
import uk.co.caprica.vlcj.player.direct.RenderCallback;
import uk.co.caprica.vlcj.player.direct.format.RV32BufferFormat;
import uk.co.caprica.vlcj.player.list.MediaListPlayerEventAdapter;
import uk.co.caprica.vlcj.runtime.RuntimeUtil;

public class VlcjWind
{
    private DirectMediaPlayerComponent mediaPlayerComponent;
    private List<FrameListener> listeners;
    private MediaPlayer mp;
	private VlcDetectionListener vdl;
   
    /**
     * Initialize Player object with the max video dimensions to support.
     * Usually set to the dimensions of the biggest screen.
     * @param width max video width to support
     * @param height max video height to support
     */
	public VlcjWind(final int width, final int height, VlcDetectionListener vdl)
	{
		this.vdl = vdl;
		System.out.println("Requested player dimensions: " + width + " x " + height);
		if(!new NativeDiscovery().discover()) {
			String path = vdl.getVlcPath();
			if(path == null) {
				if(System.getProperty("os.arch").contains("64")) {
					throw new RuntimeException("No 64bit VLC library found.");
				}
				else if(System.getProperty("os.arch").contains("86")) {
					throw new RuntimeException("No 32bit VLC library found.");
				} else {
					throw new RuntimeException("No VLC library found and unable to detect JVM architecture.");
				}
			}
			NativeLibrary.addSearchPath(RuntimeUtil.getLibVlcLibraryName(), path);
		}
		System.out.println(LibVlc.INSTANCE.libvlc_get_version());
		
		this.listeners = new ArrayList<FrameListener>();
		BufferFormatCallback bufferFormatCallback = (sourceWidth, sourceHeight) -> {
			return new RV32BufferFormat(width, height);
		};		
		mediaPlayerComponent = new DirectMediaPlayerComponent(bufferFormatCallback)
		{
		    @Override
		    protected RenderCallback onGetRenderCallback()
		    {
		        return new MyRenderCallback(listeners, width, height);
		    }
		};
		mp = mediaPlayerComponent.getMediaPlayer();
	}
	
	public VlcjWind(VlcDetectionListener vdl)
	{
		this.vdl = vdl;
		System.out.println("Requested player dimensions: " + "1920" + " x " + "1080");
		if(!new NativeDiscovery().discover()) {
			String path = vdl.getVlcPath();
			if(path == null) {
				if(System.getProperty("os.arch").contains("64")) {
					throw new RuntimeException("No 64bit VLC library found.");
				}
				else if(System.getProperty("os.arch").contains("86")) {
					throw new RuntimeException("No 32bit VLC library found.");
				} else {
					throw new RuntimeException("No VLC library found and unable to detect JVM architecture.");
				}
			}
			NativeLibrary.addSearchPath(RuntimeUtil.getLibVlcLibraryName(), path);
		}
		System.out.println(LibVlc.INSTANCE.libvlc_get_version());
		
		this.listeners = new ArrayList<FrameListener>();
		BufferFormatCallback bufferFormatCallback = (sourceWidth, sourceHeight) -> {
			return new RV32BufferFormat(1920, 1080);
		};		
		mediaPlayerComponent = new DirectMediaPlayerComponent(bufferFormatCallback)
		{
		    @Override
		    protected RenderCallback onGetRenderCallback()
		    {
		        return new MyRenderCallback(listeners, 1920, 1080);
		    }
		};
		mp = mediaPlayerComponent.getMediaPlayer();
		mp.addMediaPlayerEventListener(new MediaPlayerEventAdapter(){
			@Override
			public void videoOutput(MediaPlayer mediaPlayer, int newCount) {
				updateBufferSize((int)Math.round(mediaPlayer.getVideoDimension().getWidth()), (int)Math.round(mediaPlayer.getVideoDimension().getHeight()));
				mp.removeMediaPlayerEventListener(this);
			}
    	});
	}
    
    public void registerListener(FrameListener video)
    {
    	listeners.add(video);
    }
    
    public void unregisterListener(FrameListener video)
    {
    	listeners.remove(video);
    	if(listeners.isEmpty())
    	{
    		mp.stop();
    		mp.release();
    	}
    }
    
    private long currentPlaybackTime;
    private String fileMrl;
    
    public void updateBufferSize(int width, int height) {
    	boolean wasPlaying = mp.isPlaying();
		fileMrl = mp.mrl();
    	currentPlaybackTime = mp.getTime();

    	if(wasPlaying) {
    		mp.pause();
    	}
    	
    	mp.release();
    	mediaPlayerComponent.release();
    	
    	BufferFormatCallback bufferFormatCallback = (sourceWidth, sourceHeight) -> {
			return new RV32BufferFormat(width, height);
		};
		mediaPlayerComponent = new DirectMediaPlayerComponent(bufferFormatCallback)
		{
		    @Override
		    protected RenderCallback onGetRenderCallback()
		    {
		        return new MyRenderCallback(listeners, width, height);
		    }
		};
		mp = mediaPlayerComponent.getMediaPlayer();

		mp.prepareMedia(fileMrl);
		mp.addMediaPlayerEventListener(new MediaPlayerEventAdapter() {
			@Override
			public void playing(MediaPlayer mediaPlayer) {
				mp.setTime(currentPlaybackTime);
				if(!wasPlaying) {
					mp.pause();
				}
				mp.removeMediaPlayerEventListener(this);
			}
		});
		mp.play();
    }
    
    public MediaPlayer getMediaPlayer()
    {
    	return mp;
    }
}