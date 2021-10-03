package extrasystemreloaded;

import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipHullSpecAPI;
import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import extrasystemreloaded.augments.AugmentsHandler;
import extrasystemreloaded.campaign.battle.EngagementListener;
import extrasystemreloaded.campaign.salvage.SalvageListener;
import extrasystemreloaded.hullmods.ExtraSystemHM;
import extrasystemreloaded.upgrades.UpgradesHandler;
import extrasystemreloaded.util.ExtraSystems;
import extrasystemreloaded.util.StatUtils;
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

	public static final String ES_PERSISTENTUPGRADEMAP = "ES_UPGRADEMAP";
	private static Map<String, ExtraSystems> ShipUpgradeData;
	private static boolean debugUpgradeCosts = false;

	@Override
    public void onGameLoad(boolean newGame){
		ESModSettings.loadModSettings();
		StatUtils.loadStatCurves();
		UpgradesHandler.populateUpgrades();
		AugmentsHandler.populateAugments();

		Global.getSector().getListenerManager().addListener(new SalvageListener(), true);
		Global.getSector().addTransientListener(new EngagementListener(false));

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

		ShipUpgradeData.remove(shipId);
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

    public static void setDebugUpgradeCosts(boolean set) {
		debugUpgradeCosts = set;
	}

	public static boolean isDebugUpgradeCosts() {
		return debugUpgradeCosts;
	}
}
