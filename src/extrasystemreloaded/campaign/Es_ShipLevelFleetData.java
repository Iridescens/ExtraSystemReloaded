package extrasystemreloaded.campaign;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.BuffManagerAPI.Buff;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.loading.VariantSource;
import com.fs.starfarer.api.util.IntervalUtil;
import extrasystemreloaded.Es_ModPlugin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

//import org.lazywizard.lazylib.VectorUtils;

public class Es_ShipLevelFleetData implements Buff{
	private static final String Es_LEVEL_FUNCTION_ID = "Es_ShipLevelUp";	
	private static final String Es_LEVEL_SHIPLIST_ID = "Es_LEVEL_SHIPLIST";	
	private static final String Es_LEVEL_SHIPLMAP_ID = "Es_LEVEL_SHIPMAP";
	private static final boolean QUALITYENABLED = Global.getSettings().getBoolean("useShipIdForQualityCalculation");
	private static final float USERQUALITY = Global.getSettings().getFloat("baseQuality");
	public static Map<String, int[]>ShipLevel_DATA;//е…Ёе±Ђи€°и€№жЎЈжЎ€
	public static Map<String,Float>uppedFleetMemberAPIs ;//и®°еЅ•ж‰Ђжњ‰и€№зљ„е“ЃиґЁ
	private final IntervalUtil interval = new IntervalUtil(1f, 1f);
	private FleetMemberAPI lastMember = null;
	private float hullSizeFactor = 1f;
	private float qualityFactor = 0f;
	private boolean expired = false;
	
	private int[] AbilityLevel = {0,0,0,0,0};//е€†е€«еЇ№еє”0иЂђд№…гЂЃ1ж­¦е™ЁгЂЃ2еђЋе‹¤гЂЃ3жњєеЉЁгЂЃ4з§‘жЉЂ
	private static final Map<HullSize, Float> mag = new HashMap<HullSize, Float>();//иѓЅеЉ›дёЉеЌ‡еЏ‚ж•°
	static {
		mag.put(HullSize.FRIGATE, 1f);//жЉ¤еЌ«и€°
		mag.put(HullSize.DESTROYER, 0.666f);//й©±йЂђи€°
		mag.put(HullSize.CRUISER, 0.533f);//е·Ўжґ‹и€°
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

	public Es_ShipLevelFleetData(FleetMemberAPI memberAPI, float qualityForced, int[] AbilityLevelsForced) {
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
			for (int i = 0; i < levels.length; i++) {
				temp_points += levels[i];
			}

			ShipVariantAPI mGV = member.getVariant();
			if (mGV == null) { return; }
			boolean hasHM = mGV.hasHullMod("es_shiplevelHM");

			if ( temp_points > 0 ) {
				if ( !hasHM ) {
					ShipVariantAPI v;
					if (member.getVariant().isStockVariant()) {
						v = member.getVariant().clone();
						v.setSource(VariantSource.REFIT);
						member.setVariant(v, false, false);
					} else v = member.getVariant();

					v.setHullVariantId(Es_ModPlugin.VARIANT_PREFIX + member.getId());
					v.addPermaMod("es_shiplevelHM");

					List<String> slots = v.getModuleSlots();
					for (int i = 0; i < slots.size(); ++i) {
						ShipVariantAPI module = v.getModuleVariant(slots.get(i));
						if (module.isStockVariant()) {
							module = module.clone();
							module.setSource(VariantSource.REFIT);
							v.setModuleVariant(slots.get(i), module);
						}
						module.setHullVariantId(v.getHullVariantId());
						module.addPermaMod("es_shiplevelHM");
					}

				}
			}

			if (  temp_points <= 0 ) {
				if ( hasHM ) {
					ShipVariantAPI v = member.getVariant();
					v.removePermaMod("es_shiplevelHM");

					List<String> slots = v.getModuleSlots();
					for(int i = 0; i < slots.size(); ++i) {
						ShipVariantAPI module = v.getModuleVariant(slots.get(i));
						module.removePermaMod("es_shiplevelHM");
					}

				}
			}
			member.updateStats();
	}

//            for (int i = 0; i < levels.length; i++) {
//				float level = (float)levels[i]*qualityFactor;
//				switch (i) {
//				case 0://иЂђд№…
//					member.getStats().getHullBonus().modifyPercent(Es_LEVEL_FUNCTION_ID, hullSizeFactor*level*3f);
//					member.getStats().getArmorBonus().modifyPercent(Es_LEVEL_FUNCTION_ID, hullSizeFactor*level*3f);
//					member.getStats().getWeaponHealthBonus().modifyPercent(Es_LEVEL_FUNCTION_ID, hullSizeFactor*level*3f);
//					member.getStats().getEngineHealthBonus().modifyPercent(Es_LEVEL_FUNCTION_ID, hullSizeFactor*level*3f);
//					member.getStats().getEmpDamageTakenMult().modifyPercent(Es_LEVEL_FUNCTION_ID, -hullSizeFactor*level*4f);
//					break;
//				case 1://ж­¦е™Ё
//					member.getStats().getBallisticWeaponRangeBonus().modifyPercent(Es_LEVEL_FUNCTION_ID, hullSizeFactor*level*1.5f);
//					member.getStats().getEnergyWeaponRangeBonus().modifyPercent(Es_LEVEL_FUNCTION_ID, hullSizeFactor*level*1.5f);
//					member.getStats().getMissileWeaponRangeBonus().modifyPercent(Es_LEVEL_FUNCTION_ID, hullSizeFactor*level*1.5f);
//
//					member.getStats().getBallisticWeaponDamageMult().modifyPercent(Es_LEVEL_FUNCTION_ID, hullSizeFactor*level*1.5f);
//					member.getStats().getEnergyWeaponDamageMult().modifyPercent(Es_LEVEL_FUNCTION_ID, hullSizeFactor*level*1.5f);
//					member.getStats().getMissileWeaponDamageMult().modifyPercent(Es_LEVEL_FUNCTION_ID, hullSizeFactor*level*1.5f);
//
//					member.getStats().getBallisticRoFMult().modifyPercent(Es_LEVEL_FUNCTION_ID, hullSizeFactor*level*1.5f);
//					member.getStats().getEnergyRoFMult().modifyPercent(Es_LEVEL_FUNCTION_ID, hullSizeFactor*level*1.5f);
//					member.getStats().getMissileRoFMult().modifyPercent(Es_LEVEL_FUNCTION_ID, hullSizeFactor*level*1.5f);
//					break;
//				case 2://еђЋе‹¤
//					member.getStats().getCRPerDeploymentPercent().modifyPercent(Es_LEVEL_FUNCTION_ID, -hullSizeFactor*level*1.5f);
//
//					member.getStats().getBallisticAmmoBonus().modifyPercent(Es_LEVEL_FUNCTION_ID, hullSizeFactor*level*2f);
//					member.getStats().getEnergyAmmoBonus().modifyPercent(Es_LEVEL_FUNCTION_ID, hullSizeFactor*level*2f);
//					member.getStats().getMissileAmmoBonus().modifyPercent(Es_LEVEL_FUNCTION_ID, hullSizeFactor*level*2f);
//
//					member.getStats().getMinCrewMod().modifyPercent(Es_LEVEL_FUNCTION_ID, -hullSizeFactor*level*2f);
//					member.getStats().getMaxCrewMod().modifyPercent(Es_LEVEL_FUNCTION_ID, hullSizeFactor*level*2f);
//
//					member.getStats().getRepairRatePercentPerDay().modifyPercent(Es_LEVEL_FUNCTION_ID, hullSizeFactor*level*2.5f);
//					member.getStats().getBaseCRRecoveryRatePercentPerDay().modifyPercent(Es_LEVEL_FUNCTION_ID, hullSizeFactor*level*2.5f);
//
//					member.getStats().getFuelUseMod().modifyPercent(Es_LEVEL_FUNCTION_ID, -hullSizeFactor*level*2.5f);
//
//					member.getStats().getSuppliesPerMonth().modifyPercent(Es_LEVEL_FUNCTION_ID, -hullSizeFactor*level*2f);
//					member.getStats().getSuppliesToRecover().modifyPercent(Es_LEVEL_FUNCTION_ID, -hullSizeFactor*level*2f);
//					break;
//				case 3://жњєеЉЁ
//					member.getStats().getMaxSpeed().modifyPercent(Es_LEVEL_FUNCTION_ID, hullSizeFactor*level*2f);
//					member.getStats().getAcceleration().modifyPercent(Es_LEVEL_FUNCTION_ID, hullSizeFactor*level*3f);
//					member.getStats().getDeceleration().modifyPercent(Es_LEVEL_FUNCTION_ID, hullSizeFactor*level*3f);
//
//					member.getStats().getMaxTurnRate().modifyPercent(Es_LEVEL_FUNCTION_ID, hullSizeFactor*level*3f);
//					member.getStats().getTurnAcceleration().modifyPercent(Es_LEVEL_FUNCTION_ID, hullSizeFactor*level*3f);
//
//					member.getStats().getMaxBurnLevel().modifyPercent(Es_LEVEL_FUNCTION_ID, hullSizeFactor*level*2f);
//					break;
//				case 4://з§‘жЉЂ
//					member.getStats().getFluxCapacity().modifyPercent(Es_LEVEL_FUNCTION_ID, hullSizeFactor*level*2f);
//					member.getStats().getFluxDissipation().modifyPercent(Es_LEVEL_FUNCTION_ID, hullSizeFactor*level*2f);
//
//					member.getStats().getBallisticWeaponFluxCostMod().modifyPercent(Es_LEVEL_FUNCTION_ID, -hullSizeFactor*level*1.5f);
//					member.getStats().getMissileWeaponFluxCostMod().modifyPercent(Es_LEVEL_FUNCTION_ID, -hullSizeFactor*level*1.5f);
//					member.getStats().getEnergyWeaponFluxCostMod().modifyPercent(Es_LEVEL_FUNCTION_ID, -hullSizeFactor*level*1.5f);
////
////					if(member!=null && member.getHullSpec() != null)
////						  Global.getLogger(Es_ShipLevelFleetData.class).log(Level.INFO,member.getHullSpec().getShieldType());
//					if(member.getHullSpec() != null &&
//							(member.getHullSpec().getShieldType()==ShieldType.FRONT ||
//							 member.getHullSpec().getShieldType()==ShieldType.OMNI)) {
//						member.getStats().getShieldDamageTakenMult().modifyPercent(Es_LEVEL_FUNCTION_ID, -hullSizeFactor*level*1.5f);
//						member.getStats().getShieldUpkeepMult().modifyPercent(Es_LEVEL_FUNCTION_ID, -hullSizeFactor*level*2f);
//						member.getStats().getShieldUnfoldRateMult().modifyPercent(Es_LEVEL_FUNCTION_ID, hullSizeFactor*level*5f);
//					}
//					else if(member.getHullSpec() != null &&
//							member.getHullSpec().getShieldType()==ShieldType.PHASE) {
//						member.getStats().getPhaseCloakActivationCostBonus().modifyPercent(Es_LEVEL_FUNCTION_ID, -hullSizeFactor*level*2.5f);
//						member.getStats().getPhaseCloakCooldownBonus().modifyPercent(Es_LEVEL_FUNCTION_ID, -hullSizeFactor*level*2.5f);
//						member.getStats().getPhaseCloakUpkeepCostBonus().modifyPercent(Es_LEVEL_FUNCTION_ID, -hullSizeFactor*level*2f);
//					}
//					break;
//
//// TODO -> getOrdnancePoints(MutableCharacterStatsAPI) - getShipOrdnancePointBonus().modifyPercent(Es_LEVEL_FUNCTION_ID, -hullSizeFactor*level*2.5f);
//				default:
//					break;
//				}
//			}


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
            if (lastMember != null) {
                if ( lastMember.getFleetData() == null ||
						lastMember.getFleetData().getFleet() == null ||
						lastMember.getFleetData().getFleet().isAIMode() ||
                    !lastMember.getFleetData().getFleet().isAlive()) {
                    expired = true;
                    ShipLevel_DATA.remove(lastMember.getId());
                    uppedFleetMemberAPIs.remove(lastMember.getId());
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
				sum += (float)ids[i];
				if (i%2 ==0) {
					sum*=(float)ids[i];
				}else {
					sum/=(float)ids[i];
				}
			}
			while(sum>1f) {
				sum/=10f;
			}
			sum += 0.5f;
			return sum;
		}
		else return ( ((USERQUALITY >= 0f) && (USERQUALITY <= 1.5f)) ? USERQUALITY : 1.0f);
	}

}
