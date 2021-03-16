package extrasystemreloaded;

import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.Global;
import extrasystemreloaded.campaign.*;
import extrasystemreloaded.util.ESUpgrades;

import java.util.HashMap;
import java.util.Map;


public class Es_ModPlugin extends BaseModPlugin {
	public static String VARIANT_PREFIX = "es_";

	public static final String ES_PERSISTENTQUALITYMAP = "Es_LEVEL_SHIPLIST";
	public static final String ES_PERSISTENTUPGRADEMAP = "ES_UPGRADEMAP";

	public static Map<String, ESUpgrades> ShipUpgradeData;
	public static Map<String,Float> ShipQualityData;

	private static boolean debugUpgradeCosts = false;

	@Override
	public void onApplicationLoad() {
		if (Global.getSettings().getModManager().isModEnabled("sun_starship_legends")) {
			VARIANT_PREFIX = "sun_sl_";  // Compatibility hack for Sundog's Starship Legends
		}
	}

    public void onGameLoad(boolean newGame){
		Global.getSector().addTransientScript(new Es_ExtraSystemController());
    	if (newGame) {
    		if (Global.getSector().getPersistentData().get(ES_PERSISTENTUPGRADEMAP)==null
					|| Global.getSector().getPersistentData().get(ES_PERSISTENTQUALITYMAP)==null ) {
				Global.getSector().getPersistentData().put(ES_PERSISTENTUPGRADEMAP, new HashMap<>());
    			Global.getSector().getPersistentData().put(ES_PERSISTENTQUALITYMAP, new HashMap<>());
    		}
		} else {
			if (Global.getSector().getPersistentData().get(ES_PERSISTENTQUALITYMAP)==null ||
					Global.getSector().getPersistentData().get(ES_PERSISTENTUPGRADEMAP)==null) {
				Global.getSector().getPersistentData().put(ES_PERSISTENTQUALITYMAP, new HashMap<>());
				Global.getSector().getPersistentData().put(ES_PERSISTENTUPGRADEMAP, new HashMap<>());
			}
		}
		loadagain();
	}

	@SuppressWarnings("unchecked")
	public static void loadagain(){
		ShipUpgradeData = (Map<String, ESUpgrades>) Global.getSector().getPersistentData().get(ES_PERSISTENTUPGRADEMAP);
		ShipQualityData = (Map<String, Float>) Global.getSector().getPersistentData().get(ES_PERSISTENTQUALITYMAP);
	}


	@Override
	public void beforeGameSave() {
		Es_ShipLevelFleetData.removeESHullmodsFromAutoFitGoalVariants();
	}



	public static void setDebugUpgradeCosts(boolean set) {
		debugUpgradeCosts = set;
	}

	public static boolean isDebugUpgradeCosts() {
		return debugUpgradeCosts;
	}
}
