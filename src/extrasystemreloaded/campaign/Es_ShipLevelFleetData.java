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
import org.lazywizard.console.Console;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static extrasystemreloaded.campaign.Es_ShipLevelFunctionPlugin.ORDNANCE_HULLMOD_MAX_LEVEL;

public class Es_ShipLevelFleetData implements Buff{
	private static final String Es_LEVEL_FUNCTION_ID = "Es_ShipLevelUp";	
	private static final String Es_LEVEL_SHIPLIST_ID = "Es_LEVEL_SHIPLIST";	
	private static final String Es_LEVEL_SHIPLMAP_ID = "Es_LEVEL_SHIPMAP";
	private static final boolean QUALITYENABLED = Global.getSettings().getBoolean("useShipIdForQualityCalculation");
	private static final float USERQUALITY = Global.getSettings().getFloat("baseQuality");
	public static Map<String, int[]>ShipLevel_DATA;//е…Ёе±Ђи€°и€№жЎЈжЎ€
	public static Map<String,Float>uppedFleetMemberAPIs ;//и®°еЅ•ж‰Ђжњ‰и€№зљ„е“ЃиґЁ
	private final IntervalUtil interval = new IntervalUtil(2f, 2f);
	private FleetMemberAPI lastMember = null;
	private float hullSizeFactor = 1f;
	private float qualityFactor = 0f;
	private boolean expired = false;
	
	private int[] AbilityLevel = {0,0,0,0,0,0};//е€†е€«еЇ№еє”0иЂђд№…гЂЃ1ж­¦е™ЁгЂЃ2еђЋе‹¤гЂЃ3жњєеЉЁгЂЃ4з§‘жЉЂ
	private static final Map<HullSize, Float> mag = new HashMap<>();//иѓЅеЉ›дёЉеЌ‡еЏ‚ж•°
	static {
		mag.put(HullSize.FRIGATE, 1f);//жЉ¤еЌ«и€°
		mag.put(HullSize.DESTROYER, 0.666f);//й©±йЂђи€°
		mag.put(HullSize.CRUISER, 0.5f);//е·Ўжґ‹и€°
		mag.put(HullSize.CAPITAL_SHIP, 0.4f);//ж€�е€—и€°
	}

	@SuppressWarnings("unchecked")
	public static void loadagain(){
		ShipLevel_DATA =  (Map<String, int[]>) Global.getSector().getPersistentData().get(Es_LEVEL_SHIPLMAP_ID);
		uppedFleetMemberAPIs = (Map<String, Float>) Global.getSector().getPersistentData().get(Es_LEVEL_SHIPLIST_ID);
	}
	
	public Es_ShipLevelFleetData(FleetMemberAPI memberAPI) {//жћ„йЂ е‡Ѕж•°
		lastMember=memberAPI;
		if (mag.containsKey(memberAPI.getHullSpec().getHullSize())) {			
			hullSizeFactor = mag.get(memberAPI.getHullSpec().getHullSize());
		}
		qualityFactor = getQuality(memberAPI);
        ShipLevel_DATA.put(memberAPI.getId(),this.getLevelIndex());
		uppedFleetMemberAPIs.put(memberAPI.getId(),qualityFactor);
	}

	public Es_ShipLevelFleetData(FleetMemberAPI memberAPI, int[] AbilityLevelsForced) {//жћ„йЂ е‡Ѕж•°
		lastMember=memberAPI;
		if (mag.containsKey(memberAPI.getHullSpec().getHullSize())) {
			hullSizeFactor = mag.get(memberAPI.getHullSpec().getHullSize());
		}
		qualityFactor = getQuality(memberAPI);
		AbilityLevel = AbilityLevelsForced;
		ShipLevel_DATA.put(memberAPI.getId(),this.getLevelIndex());
		uppedFleetMemberAPIs.put(memberAPI.getId(),qualityFactor);
	}

	public Es_ShipLevelFleetData(FleetMemberAPI memberAPI, int[] AbilityLevelsForced, float qualityForced) {
		lastMember=memberAPI;
		if (mag.containsKey(memberAPI.getHullSpec().getHullSize())) {
			hullSizeFactor = mag.get(memberAPI.getHullSpec().getHullSize());
		}
		qualityFactor = qualityForced;
		AbilityLevel = AbilityLevelsForced;
		ShipLevel_DATA.put(memberAPI.getId(),this.getLevelIndex());
		uppedFleetMemberAPIs.put(memberAPI.getId(),qualityFactor);

	}

	public float getQualityFactor(){
		return qualityFactor;
	}

	public float getHullSizeFactor() {
		return hullSizeFactor;
	}

	@Override
	public void apply(FleetMemberAPI member) {
		if (uppedFleetMemberAPIs==null || ShipLevel_DATA ==null) {
			return;
		}
		int[] levels = getLevelIndex();

		int temp_points = 0;
		for (int i = 0; i < levels.length; ++i) {
			temp_points += levels[i];
		}

		if (member.getVariant() == null) {
//				member.updateStats();
			return;
		}

		if ( temp_points > 0 ) {
			ShipVariantAPI v = member.getVariant();

//				if (v.isStockVariant()) {
				v = v.clone();
				v.setGoalVariant(false);
				v.setSource(VariantSource.REFIT);
				member.setVariant(v, false, false);
//				}

			v.setHullVariantId(Es_ModPlugin.VARIANT_PREFIX + member.getId());
			removeESHullModsFromVariant(v);
			v.addPermaMod("es_shiplevelHM"+(Math.min(levels[5], ORDNANCE_HULLMOD_MAX_LEVEL)));

			List<String> slots = v.getModuleSlots();
			if (slots == null || slots.isEmpty()) {
				member.updateStats();
				return;
			}
			for (int i = 0; i < slots.size(); ++i) {
				ShipVariantAPI module = v.getModuleVariant(slots.get(i));

				if (module == null) { continue; }
//					if (module.isStockVariant()) {
					module = module.clone();
					module.setGoalVariant(false);
					module.setSource(VariantSource.REFIT);
					v.setModuleVariant(slots.get(i), module);
//					}
				module.setHullVariantId(
						module.getHullVariantId().contains(v.getHullVariantId()) ?
								module.getHullVariantId() :
								v.getHullVariantId() + "_" + module.getHullVariantId() );
				module.addPermaMod("es_shiplevelHM"+(Math.min(levels[5], ORDNANCE_HULLMOD_MAX_LEVEL)));
			}
		}
			member.updateStats();
	}

	public static void removeESHullModsFromVariant(ShipVariantAPI v) {
		for (int i = 0; i <= ORDNANCE_HULLMOD_MAX_LEVEL; ++i) {
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
        	if (lastMember != null) {
                if (lastMember.getFleetData() == null ||
					lastMember.getFleetData().getFleet() == null ||
					lastMember.getFleetData().getFleet().isAIMode() ||
                    !lastMember.getFleetData().getFleet().isAlive() ) {
						expired = true;
						ShipLevel_DATA.remove(lastMember.getId());
						uppedFleetMemberAPIs.remove(lastMember.getId());
						removeESHullModsFromVariant(lastMember.getVariant());
				}
            }
        }
	}

	public int[] getLevelIndex(){	//иї”е›ћз­‰зє§ж•°з»„
		return AbilityLevel;
	}

	private float getQuality(FleetMemberAPI member) {
        if (QUALITYENABLED) {

            if (uppedFleetMemberAPIs !=null) {
                if (uppedFleetMemberAPIs.containsKey(member.getId())) {
                    return uppedFleetMemberAPIs.get(member.getId());
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
