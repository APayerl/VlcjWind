package se.payerl.vlcjwind;

import java.util.ArrayList;
import java.util.List;

import uk.co.caprica.vlcj.component.DirectMediaPlayerComponent;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.direct.BufferFormatCallback;
import uk.co.caprica.vlcj.player.direct.RenderCallback;
import uk.co.caprica.vlcj.player.direct.format.RV32BufferFormat;

public class Player
{
    private final DirectMediaPlayerComponent mediaPlayerComponent;
    private List<FrameListener> listeners;
    private MediaPlayer mp;
   
    /**
     * Initialize Player object with the max video dimensions to support.
     * Usually set to the dimensions of the biggest screen.
     * @param width max video width to support
     * @param height max video height to support
     */
	public Player(final int width, final int height)
	{
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
    
    public MediaPlayer getMediaPlayer()
    {
    	return mp;
    }
}