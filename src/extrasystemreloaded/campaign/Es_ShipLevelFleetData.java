package extrasystemreloaded.campaign;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.BuffManagerAPI.Buff;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.combat.ShipHullSpecAPI;
import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.loading.VariantSource;
import com.fs.starfarer.api.util.IntervalUtil;
import extrasystemreloaded.Es_ModPlugin;
import extrasystemreloaded.util.ExtraSystems;
import extrasystemreloaded.util.upgrades.Upgrade;
import extrasystemreloaded.util.upgrades.Upgrades;
import org.apache.log4j.Logger;
import org.lazywizard.console.Console;

import java.util.List;

import static extrasystemreloaded.Es_ModPlugin.ShipUpgradeData;
import static extrasystemreloaded.util.Utilities.RESOURCE_NAME;
import static extrasystemreloaded.util.Utilities.getFleetCargoMap;

public class Es_ShipLevelFleetData implements Buff {
	private static final Logger log = Logger.getLogger(Es_ShipLevelFleetData.class);
    public static final String Es_LEVEL_FUNCTION_ID = "Es_ShipLevelUp";

	private final IntervalUtil interval = new IntervalUtil(2f, 2f);
	private FleetMemberAPI buffedShip = null;
	private boolean expired = false;

	private ExtraSystems UpgradesObject;

	public Es_ShipLevelFleetData(FleetMemberAPI memberAPI) { //new ship
		buffedShip = memberAPI;
		if(ShipUpgradeData.containsKey(memberAPI.getId())) {
			UpgradesObject = ShipUpgradeData.get(memberAPI.getId());
		} else {
			UpgradesObject = new ExtraSystems(memberAPI);
		}
	}

	public Es_ShipLevelFleetData(FleetMemberAPI memberAPI, ExtraSystems upgrades) { //random AI stats
		buffedShip = memberAPI;
		UpgradesObject = upgrades;
	}

	public void save() {
		Es_ModPlugin.saveData(buffedShip.getId(), this.getExtraSystems());
	}

	public String getCanUpgradeWithImpossibleTooltip() {
		return getCanUpgradeWithImpossibleTooltip(null, null);
	}

	public String getCanUpgradeWithImpossibleTooltip(Upgrade upgrade, MarketAPI market) {
		String returnValue = null;
		FleetMemberAPI shipSelected = this.buffedShip;
		if(upgrade != null) {
			if(this.getExtraSystems().getUpgrade(upgrade) >= upgrade.getMaxLevel(shipSelected.getHullSpec().getHullSize())) {
				returnValue = "This ship cannot support any more upgrades of this type.";
			} else {
				returnValue = canUpgradeByResourceCosts(shipSelected, market, upgrade, this.getExtraSystems().getQuality(this.buffedShip));
			}
		}
		return returnValue;
	}

	private String canUpgradeByResourceCosts(FleetMemberAPI shipSelected, MarketAPI market, Upgrade upgrade, float qualityFactor) {
		if(Es_ModPlugin.isDebugUpgradeCosts()) {
			return null;
		}

		StringBuilder sb = new StringBuilder();
		float[] resourceCosts = Upgrades.getUpgradeCosts(shipSelected, upgrade, this.getExtraSystems().getUpgrade(upgrade), qualityFactor);
		for (int i = 0; i < resourceCosts.length; ++i) {
			String name = RESOURCE_NAME.get(i);

			float fleetcargo = getFleetCargoMap(shipSelected.getFleetData().getFleet(), market)[i];
			if (resourceCosts[i] > fleetcargo) {
				sb.append("\n");
				sb.append((int) (resourceCosts[i] - fleetcargo));
				sb.append(" ");
				sb.append(name);
			}
		}

		if(sb.length() == 0) {
			return null;
		}

		return "This upgrade requires more resources: " + sb.toString();
	}

	@Override
	public void apply(FleetMemberAPI member) {
		if (member.getVariant() == null) {
			return;
		} else if(member.getVariant().isStockVariant()) {
			ShipVariantAPI v = member.getVariant().clone();
			v.setSource(VariantSource.REFIT);
			member.setVariant(v, false, false);
		}

		ExtraSystems levels = getExtraSystems();
		if (levels.shouldApplyHullmod()) {
			ShipVariantAPI shipVariant = member.getVariant();

			if(shipVariant.hasHullMod("es_shiplevelHM")) {
				shipVariant.removeMod("es_shiplevelHM");
			}

			shipVariant = shipVariant.clone();
			shipVariant.setGoalVariant(false);
			shipVariant.setSource(VariantSource.REFIT);
			shipVariant.setHullVariantId(Es_ModPlugin.VARIANT_PREFIX + member.getId());


			member.setVariant(shipVariant, false, false);

			removeESHullModsFromVariant(shipVariant);

			shipVariant.addPermaMod("es_shiplevelHM");

			List<String> slots = shipVariant.getModuleSlots();

			if(slots != null && !slots.isEmpty()) {
				for (int i = 0; i < slots.size(); ++i) {
					ShipVariantAPI module = shipVariant.getModuleVariant(slots.get(i));

					if (module != null) {
						module = module.clone();
						module.setGoalVariant(false);
						module.setSource(VariantSource.REFIT);
						shipVariant.setModuleVariant(slots.get(i), module);

						module.setHullVariantId(
								module.getHullVariantId().contains(shipVariant.getHullVariantId()) ?
										module.getHullVariantId() :
										shipVariant.getHullVariantId() + "_" + module.getHullVariantId());
						module.addPermaMod("es_shiplevelHM");
					}
				}
			}

			member.updateStats();
		}
	}

	public static void removeESHullModsFromVariant(ShipVariantAPI v) {
		v.removePermaMod("es_shiplevelHM");

		List<String> slots = v.getModuleSlots();
		if ((slots == null) || slots.isEmpty()) { return; }

		for(int i = 0; i < slots.size(); ++i) {
			ShipVariantAPI module = v.getModuleVariant(slots.get(i));
			if (module != null) {
				removeESHullModsFromVariant(module);
			}
		}
	}

	public static void removeESHullmodsFromAutoFitGoalVariants() {
		try {
			for (ShipHullSpecAPI spec : Global.getSettings().getAllShipHullSpecs()) {
				for (ShipVariantAPI v : Global.getSector().getAutofitVariants().getTargetVariants(spec.getHullId())) {
					if(v != null) removeESHullModsFromVariant(v);
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
					if (member.getBuffManager().getBuff(Es_LEVEL_FUNCTION_ID) != null) {
						member.getBuffManager().removeBuff(Es_LEVEL_FUNCTION_ID);
						removeESHullModsFromVariant(member.getVariant());
						Console.showMessage("Cleared ESR data from: "+member.getShipName());
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		removeESHullmodsFromAutoFitGoalVariants();
	}

	@Override
	public String getId() {
		return Es_LEVEL_FUNCTION_ID;
	}

	@Override
	public boolean isExpired() {
		return expired;
	}

	@Override
	public void advance(float days) {
		interval.advance(days);
		if (interval.intervalElapsed()) {
			if (buffedShip != null && (
					buffedShip.getFleetData() == null ||
					buffedShip.getFleetData().getFleet() == null ||
					buffedShip.getFleetData().getFleet().isAIMode() ||
					!buffedShip.getFleetData().getFleet().isAlive())) {
				expired = true;
				Es_ModPlugin.removeData(buffedShip.getId());
				removeESHullModsFromVariant(buffedShip.getVariant());
			} else if (buffedShip == null) {
				expired = true;
			}
		}
	}

	public ExtraSystems getExtraSystems() {
		return UpgradesObject;
	}
}
