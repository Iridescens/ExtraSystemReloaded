package extrasystemreloaded;

import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipHullSpecAPI;
import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import extrasystemreloaded.campaign.battle.EngagementListener;
import extrasystemreloaded.campaign.salvage.SalvageListener;
import extrasystemreloaded.hullmods.ExtraSystemHM;
import extrasystemreloaded.util.ExtraSystems;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.lazywizard.console.Console;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class Es_ModPlugin extends BaseModPlugin {
	private static final Logger log = Logger.getLogger(Es_ModPlugin.class);

	private static final String ES_UPGRADECOST_MULTS_FILE = "data/config/skill_resource_ratio.csv";
	private static JSONArray UPGRADE_COST_MULTIPLIERS = null;
    private static float UPGRADE_COST_MINFACTOR = Global.getSettings().getFloat("upgradeCostMinFactor");
    private static float UPGRADE_COST_MAXFACTOR = Global.getSettings().getFloat("upgradeCostMaxFactor");
    private static float DIVIDING_RATIO = Global.getSettings().getFloat("dividingRatio");
    private static boolean USE_RANDOM_QUALITY = Global.getSettings().getBoolean("useRandomQuality");
    private static float BASE_QUALITY = Global.getSettings().getFloat("baseQuality");
	private static float MAX_QUALITY = Global.getSettings().getFloat("maxQuality");
	private static boolean UPGRADE_ALWAYS_SUCCEED = Global.getSettings().getBoolean("upgradeAlwaysSucceed");
	private static float UPGRADE_FAILURE_MINCHANCE = Global.getSettings().getFloat("baseFailureMinFactor");

	private static boolean KEEP_UPGRADES_ON_DEATH = Global.getSettings().getBoolean("shipsKeepUpgradesOnDeath");

	public static final Map<ShipAPI.HullSize, Integer> HULLSIZE_TO_MAXLEVEL = new HashMap<>();
	public static final Map<ShipAPI.HullSize, Float> HULLSIZE_FACTOR = new HashMap<>();
	static {
		int frigateMaxUpgrades = Global.getSettings().getInt("frigateMaxUpgrades");
		int destroyerMaxUpgrades = Global.getSettings().getInt("destroyerMaxUpgrades");
		int cruiserMaxUpgrades = Global.getSettings().getInt("cruiserMaxUpgrades");
		int capitalMaxUpgrades = Global.getSettings().getInt("capitalMaxUpgrades");

		HULLSIZE_TO_MAXLEVEL.put(ShipAPI.HullSize.FIGHTER, frigateMaxUpgrades);
		HULLSIZE_TO_MAXLEVEL.put(ShipAPI.HullSize.DEFAULT, frigateMaxUpgrades);
		HULLSIZE_TO_MAXLEVEL.put(ShipAPI.HullSize.FRIGATE, frigateMaxUpgrades);
		HULLSIZE_TO_MAXLEVEL.put(ShipAPI.HullSize.DESTROYER, destroyerMaxUpgrades);
		HULLSIZE_TO_MAXLEVEL.put(ShipAPI.HullSize.CRUISER, cruiserMaxUpgrades);
		HULLSIZE_TO_MAXLEVEL.put(ShipAPI.HullSize.CAPITAL_SHIP, capitalMaxUpgrades);

		calculateHullSizeFactors();
	}

	public static final String ES_PERSISTENTUPGRADEMAP = "ES_UPGRADEMAP";

	private static Map<String, ExtraSystems> ShipUpgradeData;

	private static boolean debugUpgradeCosts = false;

	@Override
    public void onGameLoad(boolean newGame){
		Global.getSector().getListenerManager().addListener(new SalvageListener(), true);
		Global.getSector().addTransientListener(new EngagementListener(false));

		loadConfig();
		loadUpgradeData();

		for(FleetMemberAPI fm : Global.getSector().getPlayerFleet().getFleetData().getMembersListCopy()) {
			if(!ShipUpgradeData.containsKey(fm.getId())) continue;

			ExtraSystemHM.addToFleetMember(fm);
		}
	}

	public static void loadUpgradeData() {
		if(Global.getSector().getPersistentData().get(ES_PERSISTENTUPGRADEMAP)==null) {
			Global.getSector().getPersistentData().put(ES_PERSISTENTUPGRADEMAP, new HashMap<String, ExtraSystems>());
		}
		ShipUpgradeData = (Map<String, ExtraSystems>) Global.getSector().getPersistentData().get(ES_PERSISTENTUPGRADEMAP);
	}

	public static ExtraSystems getData(String shipId) {
		if(ShipUpgradeData == null) {
			loadUpgradeData();
		}

		return ShipUpgradeData.get(shipId);
	}

	public static boolean hasData(String shipId) {
		if(ShipUpgradeData == null) {
			loadUpgradeData();
		}

		return ShipUpgradeData.containsKey(shipId);
	}

	public static void saveData(String shipId, ExtraSystems systems) {
		if(ShipUpgradeData == null) {
			loadUpgradeData();
		}

		ShipUpgradeData.put(shipId, systems);
	}

	public static void removeData(String shipId) {
		if(ShipUpgradeData == null) {
			loadUpgradeData();
		}

		log.info(String.format("removed shipid %s", shipId));
		ShipUpgradeData.remove(shipId);
	}

	public static void loadConfig() {
	    try {
	    	JSONObject settings = Global.getSettings().loadJSON("data/config/settings.json", "extra_system_reloaded");
            UPGRADE_COST_MULTIPLIERS = Global.getSettings().loadCSV(ES_UPGRADECOST_MULTS_FILE);

			DIVIDING_RATIO = (float) settings.getDouble("dividingRatio");
            UPGRADE_COST_MINFACTOR = (float) settings.getDouble("upgradeCostMinFactor");
            UPGRADE_COST_MAXFACTOR = (float) settings.getDouble("upgradeCostMaxFactor");

			UPGRADE_ALWAYS_SUCCEED = settings.getBoolean("upgradeAlwaysSucceed");
			UPGRADE_FAILURE_MINCHANCE = (float) settings.getDouble("baseFailureMinFactor");

            USE_RANDOM_QUALITY = Global.getSettings().getBoolean("useRandomQuality");
            BASE_QUALITY = (float) settings.getDouble("baseQuality");
			MAX_QUALITY = (float) settings.getDouble("maxQuality");

			KEEP_UPGRADES_ON_DEATH = settings.getBoolean("shipsKeepUpgradesOnDeath");


			int frigateMaxUpgrades = settings.getInt("frigateMaxUpgrades");
			int destroyerMaxUpgrades = settings.getInt("destroyerMaxUpgrades");
			int cruiserMaxUpgrades = settings.getInt("cruiserMaxUpgrades");
			int capitalMaxUpgrades = settings.getInt("capitalMaxUpgrades");

			HULLSIZE_TO_MAXLEVEL.put(ShipAPI.HullSize.FIGHTER, frigateMaxUpgrades);
			HULLSIZE_TO_MAXLEVEL.put(ShipAPI.HullSize.DEFAULT, frigateMaxUpgrades);
			HULLSIZE_TO_MAXLEVEL.put(ShipAPI.HullSize.FRIGATE, frigateMaxUpgrades);
			HULLSIZE_TO_MAXLEVEL.put(ShipAPI.HullSize.DESTROYER, destroyerMaxUpgrades);
			HULLSIZE_TO_MAXLEVEL.put(ShipAPI.HullSize.CRUISER, cruiserMaxUpgrades);
			HULLSIZE_TO_MAXLEVEL.put(ShipAPI.HullSize.CAPITAL_SHIP, capitalMaxUpgrades);

			calculateHullSizeFactors();
        } catch (JSONException | IOException ignored) {
        }
    }

    public static void calculateHullSizeFactors() {
		float lowestMax = Integer.MAX_VALUE;
		for(ShipAPI.HullSize hullSize : HULLSIZE_TO_MAXLEVEL.keySet()) {
			if(HULLSIZE_TO_MAXLEVEL.get(hullSize) < lowestMax) {
				lowestMax = HULLSIZE_TO_MAXLEVEL.get(hullSize);
			}
		}

		for(ShipAPI.HullSize hullSize : HULLSIZE_TO_MAXLEVEL.keySet()) {
			HULLSIZE_FACTOR.put(hullSize, lowestMax / HULLSIZE_TO_MAXLEVEL.get(hullSize));
		}
	}

	@Override
	public void beforeGameSave() {
		Es_ModPlugin.removeESHullmodsFromAutoFitGoalVariants();
	}

	public static void removeESHullmodsFromAutoFitGoalVariants() {
		try {
			for (ShipHullSpecAPI spec : Global.getSettings().getAllShipHullSpecs()) {
				for (ShipVariantAPI v : Global.getSector().getAutofitVariants().getTargetVariants(spec.getHullId())) {
					if(v != null) ExtraSystemHM.removeESHullModsFromVariant(v);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void removeESHullmodsFromEveryVariant() {
		try {
			for (CampaignFleetAPI campaignFleetAPI : Global.getSector().getCurrentLocation().getFleets()) {
				for (FleetMemberAPI member : campaignFleetAPI.getFleetData().getMembersListCopy()) {
					ExtraSystemHM.removeESHullModsFromVariant(member.getVariant());
					Console.showMessage("Cleared ESR data from: "+member.getShipName());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		removeESHullmodsFromAutoFitGoalVariants();
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

	public static boolean useRandomQuality() {
        return USE_RANDOM_QUALITY;
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

	public static boolean isKeepUpgradesOnDeath() {
		return KEEP_UPGRADES_ON_DEATH;
	}
}
