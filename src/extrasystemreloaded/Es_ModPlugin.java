package extrasystemreloaded;

import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import extrasystemreloaded.campaign.Es_ShipLevelFleetData;
import extrasystemreloaded.campaign.salvage.SalvageListener;
import extrasystemreloaded.util.ExtraSystems;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class Es_ModPlugin extends BaseModPlugin {
	private static final String ES_UPGRADECOST_MULTS_FILE = "data/config/skill_resource_ratio.csv";
	private static JSONArray UPGRADE_COST_MULTIPLIERS = null;
    private static float UPGRADE_COST_MINFACTOR = Global.getSettings().getFloat("upgradeCostMinFactor");
    private static float UPGRADE_COST_MAXFACTOR = Global.getSettings().getFloat("upgradeCostMaxFactor");
    private static float DIVIDING_RATIO = Global.getSettings().getFloat("dividingRatio");
    private static boolean USE_SHIP_ID_FOR_QUALITY_CALCULATION = Global.getSettings().getBoolean("useShipIdForQualityCalculation");
    private static float BASE_QUALITY = Global.getSettings().getFloat("baseQuality");
	private static float MAX_QUALITY = Global.getSettings().getFloat("maxQuality");
	private static boolean UPGRADE_ALWAYS_SUCCEED = Global.getSettings().getBoolean("upgradeAlwaysSucceed");
	private static float UPGRADE_FAILURE_MINCHANCE = Global.getSettings().getFloat("baseFailureMinFactor");

	public static String VARIANT_PREFIX = "es_";

	public static final String ES_PERSISTENTQUALITYMAP = "Es_LEVEL_SHIPLIST";
	public static final String ES_PERSISTENTUPGRADEMAP = "ES_UPGRADEMAP";

	public static Map<String, ExtraSystems> ShipUpgradeData;

	private static boolean debugUpgradeCosts = false;

	@Override
	public void onApplicationLoad() {
		if (Global.getSettings().getModManager().isModEnabled("sun_starship_legends")) {
			VARIANT_PREFIX = "sun_sl_";  // Compatibility hack for Sundog's Starship Legends
		}
	}

	@Override
    public void onGameLoad(boolean newGame){
		Global.getSector().getListenerManager().addListener(new SalvageListener(), true);

		loadConfig();

    	if (Global.getSector().getPersistentData().get(ES_PERSISTENTUPGRADEMAP)==null) {
			Global.getSector().getPersistentData().put(ES_PERSISTENTUPGRADEMAP, new HashMap<String, ExtraSystems>());
		}

		loadPersistentData();
	}

	public static void applyBuff(FleetMemberAPI fm) {
		if(fm.getBuffManager().getBuff(Es_ShipLevelFleetData.Es_LEVEL_FUNCTION_ID) != null) {
			return;
		}
		fm.getBuffManager().addBuff(new Es_ShipLevelFleetData(fm));
	}

	@SuppressWarnings("unchecked")
	public static void loadPersistentData(){
		ShipUpgradeData = (Map<String, ExtraSystems>) Global.getSector().getPersistentData().get(ES_PERSISTENTUPGRADEMAP);

		for(FleetMemberAPI fm : Global.getSector().getPlayerFleet().getFleetData().getMembersListCopy()) {
			if(!ShipUpgradeData.containsKey(fm.getId())) continue;

			applyBuff(fm);
		}
	}

	public static boolean hasData(String shipId) {
		return ShipUpgradeData.containsKey(shipId);
	}

	public static void saveData(String shipId, ExtraSystems systems) {
		ShipUpgradeData.put(shipId, systems);
	}

	public static void removeData(String shipId) {
		ShipUpgradeData.remove(shipId);
	}

	public static void loadConfig() {
	    try {
            UPGRADE_COST_MULTIPLIERS = Global.getSettings().loadCSV(ES_UPGRADECOST_MULTS_FILE);
            UPGRADE_COST_MINFACTOR = Global.getSettings().getFloat("upgradeCostMinFactor");
            UPGRADE_COST_MAXFACTOR = Global.getSettings().getFloat("upgradeCostMaxFactor");
			UPGRADE_ALWAYS_SUCCEED = Global.getSettings().getBoolean("upgradeAlwaysSucceed");
			UPGRADE_FAILURE_MINCHANCE = Global.getSettings().getFloat("baseFailureMinFactor");
            DIVIDING_RATIO = Global.getSettings().getFloat("dividingRatio");
            USE_SHIP_ID_FOR_QUALITY_CALCULATION = Global.getSettings().getBoolean("useShipIdForQualityCalculation");
            BASE_QUALITY = Global.getSettings().getFloat("baseQuality");
			MAX_QUALITY = Global.getSettings().getFloat("maxQuality");
        } catch (JSONException | IOException ignored) {
        }
    }

	@Override
	public void beforeGameSave() {
		Es_ShipLevelFleetData.removeESHullmodsFromAutoFitGoalVariants();
	}


	public static float getMaxQuality() {
		return MAX_QUALITY;
	}

	public static boolean isUpgradeAlwaysSucceed() {
		return UPGRADE_ALWAYS_SUCCEED;
	}

	public static float getUpgradeFailureMinChance() {
		return UPGRADE_FAILURE_MINCHANCE;
	}

	public static boolean getUseShipIdForQuality() {
        return USE_SHIP_ID_FOR_QUALITY_CALCULATION;
    }

    public static float getBaseQuality() {
        return BASE_QUALITY;
    }

    public static float getDividingRatio() {
        return DIVIDING_RATIO;
    }

    public static float getUpgradeCostMaxFactor() {
        return UPGRADE_COST_MAXFACTOR;
    }

    public static float getUpgradeCostMinFactor() {
        return UPGRADE_COST_MINFACTOR;
    }

    public static JSONArray getUpgradeCostMultipliers() {
        return UPGRADE_COST_MULTIPLIERS;
    }

    public static void setDebugUpgradeCosts(boolean set) {
		debugUpgradeCosts = set;
	}

	public static boolean isDebugUpgradeCosts() {
		return debugUpgradeCosts;
	}
}
