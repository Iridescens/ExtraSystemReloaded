package extrasystemreloaded.util;

import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.Global;
//	import org.apache.log4j.Logger;
//	import org.apache.log4j.Level;

public class Es_GameSetPausePlugin implements EveryFrameScript{
//	private static final Logger Log = Global.getLogger(Es_GameSetPausePlugin.class);
	
	@Override
	public boolean isDone() {
		if(Global.getSector().isPaused()) {
//			Log.log(Level.INFO,"Es_GameSetPausePlugin in -isDone-Paused- state");
			return true;
		}
		return false;
		
	}

	@Override
	public boolean runWhilePaused() {
		return false;
	}

	@Override
	public void advance(float amount) {
//		Log.log(Level.INFO,"Es_GameSetPausePlugin in -advance- state");
		if(!Global.getSector().isPaused()) {
			Global.getSector().setPaused(true);
		}
	}
}