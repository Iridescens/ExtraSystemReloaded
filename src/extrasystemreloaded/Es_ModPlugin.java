package extrasystemreloaded;

import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.Global;
import extrasystemreloaded.campaign.Es_BaseAchievementsUnlockPlugin;
import extrasystemreloaded.campaign.Es_CampaignRenderPlugin;
import extrasystemreloaded.campaign.Es_ExtraSystemController;
import extrasystemreloaded.campaign.Es_ShipLevelFleetData;
import extrasystemreloaded.util.AchievementData;
import org.json.JSONException;

import java.io.IOException;
import java.util.HashMap;


public class Es_ModPlugin extends BaseModPlugin {
	private static final String Es_LEVEL_FUNCTION_ID = "Es_ShipLevelUp";
	private static final String SHIP_TRADE_SAVE_ID = "Es_ShipTradeSaveData";
	private static final String Es_LEVEL_SHIPLIST_ID = "Es_LEVEL_SHIPLIST";	
	private static final String Es_LEVEL_SHIPLMAP_ID = "Es_LEVEL_SHIPMAP";	
	private static final boolean ACHIEVEMENTSENABLED = Global.getSettings().getBoolean("enableAchievements");
	private static final String ACHIEVEMENT_ID = "AchievementData";
	private static final String ACHIEVEMENT_CREDITS_ID = "Achievement_Credits";
	public static String VARIANT_PREFIX = "es_";

	@Override
	public void onApplicationLoad() {
		if (Global.getSettings().getModManager().isModEnabled("sun_starship_legends")) {
			VARIANT_PREFIX = "sun_sl_";  // Compatibility hack for Sundog's Starship Legends
		}
	}

    public void onGameLoad(boolean newGame){
    	Global.getSector().addTransientScript(new Es_ExtraSystemController());
    	if(ACHIEVEMENTSENABLED){
    		Global.getSector().addTransientScript(new Es_CampaignRenderPlugin());
    		Global.getSector().addTransientScript(new Es_BaseAchievementsUnlockPlugin());
    		if (!Global.getSector().getPersistentData().containsKey(ACHIEVEMENT_ID)) {
    			AchievementData data = new AchievementData();
    			Global.getSector().getPersistentData().put(ACHIEVEMENT_ID, data);
    			boolean BOOL[] = {false,false,false,false,false,false};
    			data.getCustomData().put(ACHIEVEMENT_CREDITS_ID, BOOL);
    		}else {
    			AchievementData data = (AchievementData) Global.getSector().getPersistentData().get(ACHIEVEMENT_ID);
    			try {
    				data.loadAndCheck();
    			} catch (IOException e) {
    				e.printStackTrace();
    			} catch (JSONException e) {
    				e.printStackTrace();
    			}
    		}
    	}
    	if (newGame) {
    		if (Global.getSector().getPersistentData().get(Es_LEVEL_SHIPLIST_ID)==null ||Global.getSector().getPersistentData().get(Es_LEVEL_SHIPLMAP_ID)==null ) {
    			Global.getSector().getPersistentData().put(Es_LEVEL_SHIPLIST_ID ,new HashMap<>());
    			Global.getSector().getPersistentData().put(Es_LEVEL_SHIPLMAP_ID ,new HashMap<>());
    			Es_ShipLevelFleetData.loadagain();
    		}
//            if (Global.getSector().getPersistentData().get(SHIP_TRADE_SAVE_ID)==null) {
//            	Es_ShipTradeSaveData data = new Es_ShipTradeSaveData();
//            	Global.getSector().getPersistentData().put(SHIP_TRADE_SAVE_ID, data);
//            	data.init();
//    		}
		}else {			
			if (Global.getSector().getPersistentData().get(Es_LEVEL_SHIPLIST_ID)==null ||Global.getSector().getPersistentData().get(Es_LEVEL_SHIPLMAP_ID)==null ) {
				Global.getSector().getPersistentData().put(Es_LEVEL_SHIPLIST_ID ,new HashMap<>());
				Global.getSector().getPersistentData().put(Es_LEVEL_SHIPLMAP_ID ,new HashMap<>());
			}
//			Es_ShipTradeSaveData data = (Es_ShipTradeSaveData) Global.getSector().getPersistentData().get(SHIP_TRADE_SAVE_ID);
//			if (data==null) {
//				data = new Es_ShipTradeSaveData();
//				Global.getSector().getPersistentData().put(SHIP_TRADE_SAVE_ID, data);
//			}
//			data.init();
			Es_ShipLevelFleetData.loadagain();
		}
	}
}
