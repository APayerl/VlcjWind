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
    
    public void updateBufferSize(int width, int height) {
    	boolean wasPlaying = false;
    	if(mp.isPlaying()) {
    		mp.pause();
    		wasPlaying = true;
    	}
    	long currentPlaybackTime = mp.getTime();
    	String fileMrl = mp.mrl();
    	
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
		mp.setTime(currentPlaybackTime);
		if(wasPlaying) {
			mp.play();
		}
    }
    
    public MediaPlayer getMediaPlayer()
    {
    	return mp;
    }
}