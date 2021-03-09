package extrasystemreloaded.campaign;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.BuffManagerAPI.Buff;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.combat.ShipHullSpecAPI;
import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.loading.VariantSource;
import com.fs.starfarer.api.util.IntervalUtil;
import extrasystemreloaded.Es_ModPlugin;
import extrasystemreloaded.hullmods.ExtraSystemHM;
import extrasystemreloaded.util.ESUpgrades;
import org.lazywizard.console.Console;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import extrasystemreloaded.util.ESUpgrades.UpgradeKey;
import static extrasystemreloaded.Es_ModPlugin.ShipUpgradeData;
import static extrasystemreloaded.Es_ModPlugin.ShipQualityData;
import static extrasystemreloaded.util.ESUpgrades.UPGRADES;

public class Es_ShipLevelFleetData implements Buff{
    public static final String Es_LEVEL_FUNCTION_ID = "Es_ShipLevelUp";
	private static final boolean QUALITYENABLED = Global.getSettings().getBoolean("useShipIdForQualityCalculation");
	private static final float USERQUALITY = Global.getSettings().getFloat("baseQuality");
	private final IntervalUtil interval = new IntervalUtil(2f, 2f);
	private FleetMemberAPI buffedShip = null;
	private boolean expired = false;
	
	private ESUpgrades UpgradesObject;
	private float qualityFactor = 0f;

	private float hullSizeFactor = 1f;
	private static final Map<HullSize, Float> mag = new HashMap<>();//иѓЅеЉ›дёЉеЌ‡еЏ‚ж•°
	static {
		mag.put(HullSize.FRIGATE, 1f);//жЉ¤еЌ«и€°
		mag.put(HullSize.DESTROYER, 0.666f);//й©±йЂђи€°
		mag.put(HullSize.CRUISER, 0.5f);//е·Ўжґ‹и€°
		mag.put(HullSize.CAPITAL_SHIP, 0.4f);//ж€�е€—и€°
	}

	//debug-Logger
	private static void debugMessage(String Text) {
		boolean DEBUG = false; //set to false once done
		if (DEBUG) {
			Global.getLogger(Es_ShipLevelFleetData.class).info(Text);
		}
	}
	
	public Es_ShipLevelFleetData(FleetMemberAPI memberAPI) { //new ship
		buffedShip =memberAPI;
		if (mag.containsKey(memberAPI.getHullSpec().getHullSize())) {			
			hullSizeFactor = mag.get(memberAPI.getHullSpec().getHullSize());
		}
		qualityFactor = getQuality(memberAPI);

		UpgradesObject = new ESUpgrades();

        ShipUpgradeData.put(memberAPI.getId(),this.getUpgrades());
		ShipQualityData.put(memberAPI.getId(),qualityFactor);
	}

	public Es_ShipLevelFleetData(FleetMemberAPI memberAPI, ESUpgrades upgrades) { //random AI stats
		buffedShip =memberAPI;
		if (mag.containsKey(memberAPI.getHullSpec().getHullSize())) {
			hullSizeFactor = mag.get(memberAPI.getHullSpec().getHullSize());
		}
		qualityFactor = getQuality(memberAPI);

		UpgradesObject = upgrades;

		ShipUpgradeData.put(memberAPI.getId(),this.getUpgrades());
		ShipQualityData.put(memberAPI.getId(),qualityFactor);
	}

	public Es_ShipLevelFleetData(FleetMemberAPI memberAPI, ESUpgrades upgrades, float qualityForced) { //quality upgrade
		buffedShip = memberAPI;
		if (mag.containsKey(memberAPI.getHullSpec().getHullSize())) {
			hullSizeFactor = mag.get(memberAPI.getHullSpec().getHullSize());
		}
		qualityFactor = qualityForced;

		UpgradesObject = upgrades;

		ShipUpgradeData.put(memberAPI.getId(),this.getUpgrades());
		ShipQualityData.put(memberAPI.getId(),qualityFactor);
	}

	public float getQualityFactor(){
		return qualityFactor;
	}

	public float getHullSizeFactor() {
		return hullSizeFactor;
	}

	@Override
	public void apply(FleetMemberAPI member) {
		if (ShipQualityData ==null || ShipUpgradeData ==null) {
			return;
		}
		ESUpgrades levels = getUpgrades();

		if (member.getVariant() == null) {
			return;
		}

		if ( levels.hasUpgrades() ) {
			ShipVariantAPI v = member.getVariant();

				v = v.clone();
				v.setGoalVariant(false);
				v.setSource(VariantSource.REFIT);
				member.setVariant(v, false, false);

			v.setHullVariantId(Es_ModPlugin.VARIANT_PREFIX + member.getId());
			removeESHullModsFromVariant(v);

			int ordnanceLevel = Math.min(levels.getUpgrade(UpgradeKey.ORDNANCE), UPGRADES.get(UpgradeKey.ORDNANCE).getMaxLevel(v.getHullSize()));
			v.addPermaMod("es_shiplevelHM" + ordnanceLevel);

			List<String> slots = v.getModuleSlots();
			if (slots == null || slots.isEmpty()) {
				member.updateStats();
				return;
			}
			for (int i = 0; i < slots.size(); ++i) {
				ShipVariantAPI module = v.getModuleVariant(slots.get(i));

				if (module == null) { continue; }
					module = module.clone();
					module.setGoalVariant(false);
					module.setSource(VariantSource.REFIT);
					v.setModuleVariant(slots.get(i), module);

				module.setHullVariantId(
						module.getHullVariantId().contains(v.getHullVariantId()) ?
								module.getHullVariantId() :
								v.getHullVariantId() + "_" + module.getHullVariantId() );
				module.addPermaMod("es_shiplevelHM" + ordnanceLevel);
			}
		}
		member.updateStats();
	}

	public static void removeESHullModsFromVariant(ShipVariantAPI v) {
		for (int i = 0; i <= UPGRADES.get(UpgradeKey.ORDNANCE).getMaxLevel(v.getHullSize()); ++i) {
			v.removePermaMod("es_shiplevelHM"+i);
		}

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
//			removeESHullmodFromAutoFitGoalVariants();
        	if (buffedShip != null) {
                if (buffedShip.getFleetData() == null ||
					buffedShip.getFleetData().getFleet() == null ||
					buffedShip.getFleetData().getFleet().isAIMode() ||
                    !buffedShip.getFleetData().getFleet().isAlive() ) {
						expired = true;
						ShipUpgradeData.remove(buffedShip.getId());
						ShipQualityData.remove(buffedShip.getId());
						ExtraSystemHM.getFleetMemberAPIMap().remove(buffedShip.getStats());
						removeESHullModsFromVariant(buffedShip.getVariant());
				}
            }
        }
	}

	public ESUpgrades getUpgrades(){	//иї”е›ћз­‰зє§ж•°з»„
		return UpgradesObject;
	}

	private float getQuality(FleetMemberAPI member) {
        if (QUALITYENABLED) {

            if (ShipQualityData !=null) {
                if (ShipQualityData.containsKey(member.getId())) {
                    return ShipQualityData.get(member.getId());
                }
            }

			String id = member.getId();
			char[] ids = id.toCharArray();
			float sum = 0f;
			for (int i = 0; i < ids.length; i++) {
				sum += ids[i];
				if ( i%2 == 0 ) {
					sum *= ids[i];
				} else {
					sum /= ids[i];
				}
			}
			while( sum > 1f ) {
				sum /= 10f;
			}
			sum += 0.5f;
			return sum;
		}
		else return ( ((USERQUALITY >= 0f) && (USERQUALITY <= 1.5f)) ? USERQUALITY : 1.0f);
	}

}
