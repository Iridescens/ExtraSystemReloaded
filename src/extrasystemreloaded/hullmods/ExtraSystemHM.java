package extrasystemreloaded.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShieldAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import extrasystemreloaded.campaign.Es_ShipLevelFleetData;

import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static extrasystemreloaded.campaign.Es_ShipLevelFunctionPlugin.*;

public class ExtraSystemHM extends BaseHullMod {

    private static final String[] ABILITY_NAME = {
            Global.getSettings().getString("AbilityName", "Durability"),
            Global.getSettings().getString("AbilityName", "WeaponProficiency"),
            Global.getSettings().getString("AbilityName", "Logistics"),
            Global.getSettings().getString("AbilityName", "Flexibility"),
            Global.getSettings().getString("AbilityName", "Technology"),
            Global.getSettings().getString("AbilityName", "Ordnance")
    };
    private final static Map<ShipAPI.HullSize, Integer> ORDNANCE_EFFECT_MULT = new HashMap<>();
    static {
        ORDNANCE_EFFECT_MULT.put(ShipAPI.HullSize.DEFAULT, 0);
        ORDNANCE_EFFECT_MULT.put(ShipAPI.HullSize.FIGHTER, 0);
        ORDNANCE_EFFECT_MULT.put(ShipAPI.HullSize.FRIGATE, 1);
        ORDNANCE_EFFECT_MULT.put(ShipAPI.HullSize.DESTROYER, 2);
        ORDNANCE_EFFECT_MULT.put(ShipAPI.HullSize.CRUISER, 3);
        ORDNANCE_EFFECT_MULT.put(ShipAPI.HullSize.CAPITAL_SHIP, 4);
    }


    @Override
    public void applyEffectsBeforeShipCreation(ShipAPI.HullSize hullSize, MutableShipStatsAPI stats, String id) {
        FleetMemberAPI fm = findShip(hullSize, stats);
        if ( fm == null || fm.getBuffManager() == null ) { return;}
        if ( fm.getBuffManager().getBuff(Es_LEVEL_FUNCTION_ID) == null ) {
//            Es_ShipLevelFleetData.removeESHullMods(fm.getVariant());
            return;
        }
        Es_ShipLevelFleetData buff = (Es_ShipLevelFleetData) fm.getBuffManager().getBuff(Es_LEVEL_FUNCTION_ID);
        int[] levels = buff.getLevelIndex();

//        if (uppedFleetMemberAPIs.containsKey(fm.getId())) {
//            String tips = "----Extra System upgrades----\n" +
//                    "Quality:"+ (float)Math.round(uppedFleetMemberAPIs.get(fm.getId())*100)/100 + "\n" +
//                    ABILITY_NAME[0] + ": " + levels[0] + "\n" +
//                    ABILITY_NAME[1] + ": " + levels[1] + "\n" +
//                    ABILITY_NAME[2] + ": " + levels[2] + "\n" +
//                    ABILITY_NAME[3] + ": " + levels[3] + "\n" +
//                    ABILITY_NAME[4] + ": " + levels[4] + "\n" +
//                    "-----------------------------";
//            fm.getStats().getMaxCombatReadiness().modifyFlat(Es_LEVEL_FUNCTION_ID, 0.00001f, tips);
//        }

        for (int i = 0; i < levels.length; ++i) {
            float level = (float)levels[i] * buff.getQualityFactor();
            switch (i) {
                case 0://иЂђд№…
                    stats.getHullBonus().modifyPercent(Es_LEVEL_FUNCTION_ID, buff.getHullSizeFactor()*level*3f);
                    stats.getArmorBonus().modifyPercent(Es_LEVEL_FUNCTION_ID, buff.getHullSizeFactor()*level*3f);
                    stats.getWeaponHealthBonus().modifyPercent(Es_LEVEL_FUNCTION_ID, buff.getHullSizeFactor()*level*3f);
                    stats.getEngineHealthBonus().modifyPercent(Es_LEVEL_FUNCTION_ID, buff.getHullSizeFactor()*level*3f);
                    stats.getEmpDamageTakenMult().modifyPercent(Es_LEVEL_FUNCTION_ID, -buff.getHullSizeFactor()*level*4f);
                    break;
                case 1://ж­¦е™Ё
                    stats.getBallisticWeaponRangeBonus().modifyPercent(Es_LEVEL_FUNCTION_ID, buff.getHullSizeFactor()*level*1.5f);
                    stats.getEnergyWeaponRangeBonus().modifyPercent(Es_LEVEL_FUNCTION_ID, buff.getHullSizeFactor()*level*1.5f);
                    stats.getMissileWeaponRangeBonus().modifyPercent(Es_LEVEL_FUNCTION_ID, buff.getHullSizeFactor()*level*1.5f);

                    stats.getBallisticWeaponDamageMult().modifyPercent(Es_LEVEL_FUNCTION_ID, buff.getHullSizeFactor()*level*1.5f);
                    stats.getEnergyWeaponDamageMult().modifyPercent(Es_LEVEL_FUNCTION_ID, buff.getHullSizeFactor()*level*1.5f);
                    stats.getMissileWeaponDamageMult().modifyPercent(Es_LEVEL_FUNCTION_ID, buff.getHullSizeFactor()*level*1.5f);

                    stats.getBallisticRoFMult().modifyPercent(Es_LEVEL_FUNCTION_ID, buff.getHullSizeFactor()*level*1.5f);
                    stats.getEnergyRoFMult().modifyPercent(Es_LEVEL_FUNCTION_ID, buff.getHullSizeFactor()*level*1.5f);
                    stats.getMissileRoFMult().modifyPercent(Es_LEVEL_FUNCTION_ID, buff.getHullSizeFactor()*level*1.5f);
                    break;
                case 2://еђЋе‹¤
                    stats.getCRPerDeploymentPercent().modifyPercent(Es_LEVEL_FUNCTION_ID, -buff.getHullSizeFactor()*level*1.5f);

                    stats.getBallisticAmmoBonus().modifyPercent(Es_LEVEL_FUNCTION_ID, buff.getHullSizeFactor()*level*2f);
                    stats.getEnergyAmmoBonus().modifyPercent(Es_LEVEL_FUNCTION_ID, buff.getHullSizeFactor()*level*2f);
                    stats.getMissileAmmoBonus().modifyPercent(Es_LEVEL_FUNCTION_ID, buff.getHullSizeFactor()*level*2f);

                    stats.getMinCrewMod().modifyPercent(Es_LEVEL_FUNCTION_ID, -buff.getHullSizeFactor()*level*2f);
                    stats.getMaxCrewMod().modifyPercent(Es_LEVEL_FUNCTION_ID, buff.getHullSizeFactor()*level*2f);

                    stats.getRepairRatePercentPerDay().modifyPercent(Es_LEVEL_FUNCTION_ID, buff.getHullSizeFactor()*level*2.5f);
                    stats.getBaseCRRecoveryRatePercentPerDay().modifyPercent(Es_LEVEL_FUNCTION_ID, buff.getHullSizeFactor()*level*2.5f);

                    stats.getFuelUseMod().modifyPercent(Es_LEVEL_FUNCTION_ID, -buff.getHullSizeFactor()*level*2.5f);

                    stats.getSuppliesPerMonth().modifyPercent(Es_LEVEL_FUNCTION_ID, -buff.getHullSizeFactor()*level*2f);
                    stats.getSuppliesToRecover().modifyPercent(Es_LEVEL_FUNCTION_ID, -buff.getHullSizeFactor()*level*2f);
                    break;
                case 3://жњєеЉЁ
                    stats.getMaxSpeed().modifyPercent(Es_LEVEL_FUNCTION_ID, buff.getHullSizeFactor()*level*2f);
                    stats.getAcceleration().modifyPercent(Es_LEVEL_FUNCTION_ID, buff.getHullSizeFactor()*level*3f);
                    stats.getDeceleration().modifyPercent(Es_LEVEL_FUNCTION_ID, buff.getHullSizeFactor()*level*3f);

                    stats.getMaxTurnRate().modifyPercent(Es_LEVEL_FUNCTION_ID, buff.getHullSizeFactor()*level*3f);
                    stats.getTurnAcceleration().modifyPercent(Es_LEVEL_FUNCTION_ID, buff.getHullSizeFactor()*level*3f);

                    stats.getMaxBurnLevel().modifyPercent(Es_LEVEL_FUNCTION_ID, buff.getHullSizeFactor()*level*2f);
                    break;
                case 4://з§‘жЉЂ
                    stats.getFluxCapacity().modifyPercent(Es_LEVEL_FUNCTION_ID, buff.getHullSizeFactor()*level*2f);
                    stats.getFluxDissipation().modifyPercent(Es_LEVEL_FUNCTION_ID, buff.getHullSizeFactor()*level*2f);

                    stats.getBallisticWeaponFluxCostMod().modifyPercent(Es_LEVEL_FUNCTION_ID, -buff.getHullSizeFactor()*level*1.5f);
                    stats.getMissileWeaponFluxCostMod().modifyPercent(Es_LEVEL_FUNCTION_ID, -buff.getHullSizeFactor()*level*1.5f);
                    stats.getEnergyWeaponFluxCostMod().modifyPercent(Es_LEVEL_FUNCTION_ID, -buff.getHullSizeFactor()*level*1.5f);
//
//					if(member!=null && fm.getHullSpec() != null)
//						  Global.getLogger(Es_ShipLevelFleetData.class).log(Level.INFO,fm.getHullSpec().getShieldType());
                    if(fm.getHullSpec() != null &&
                            (fm.getHullSpec().getShieldType()== ShieldAPI.ShieldType.FRONT ||
                                    fm.getHullSpec().getShieldType()== ShieldAPI.ShieldType.OMNI)) {
                        stats.getShieldDamageTakenMult().modifyPercent(Es_LEVEL_FUNCTION_ID, -buff.getHullSizeFactor()*level*1.5f);
                        stats.getShieldUpkeepMult().modifyPercent(Es_LEVEL_FUNCTION_ID, -buff.getHullSizeFactor()*level*2f);
                        stats.getShieldUnfoldRateMult().modifyPercent(Es_LEVEL_FUNCTION_ID, buff.getHullSizeFactor()*level*5f);
                    }
                    else if(fm.getHullSpec() != null &&
                            fm.getHullSpec().getShieldType()== ShieldAPI.ShieldType.PHASE) {
                        stats.getPhaseCloakActivationCostBonus().modifyPercent(Es_LEVEL_FUNCTION_ID, -buff.getHullSizeFactor()*level*2.5f);
                        stats.getPhaseCloakCooldownBonus().modifyPercent(Es_LEVEL_FUNCTION_ID, -buff.getHullSizeFactor()*level*2.5f);
                        stats.getPhaseCloakUpkeepCostBonus().modifyPercent(Es_LEVEL_FUNCTION_ID, -buff.getHullSizeFactor()*level*2f);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    FleetMemberAPI findShip(ShipAPI.HullSize hullSize, MutableShipStatsAPI stats) {
        String key = stats.getVariant().getHullVariantId();
        List<FleetMemberAPI> members;
//        List<FleetMemberAPI> members = Global.getSector().getPlayerFleet().getFleetData().getMembersListCopy();
        try {
            members = Global.getSector().getPlayerFleet().getFleetData().getMembersListCopy();
        } catch (NullPointerException e) { return null; }

        try {
            for (CampaignFleetAPI campaignFleetAPI : Global.getSector().getCurrentLocation().getFleets()) {
                if ( ! campaignFleetAPI.equals( Global.getSector().getPlayerFleet() ) ) {
                    members.addAll(campaignFleetAPI.getFleetData().getMembersListCopy());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        if(stats.getEntity() != null) {
            if (stats.getEntity() instanceof FleetMemberAPI) {
                return (FleetMemberAPI)stats.getEntity();
            } else if (stats.getEntity() instanceof ShipAPI) {
                for (FleetMemberAPI s : members) {
                    if (s == stats.getEntity()) {
                        return s;
                    }
                }
            }
        }

        for (FleetMemberAPI s : members) {
//            if (key.equals(s.getVariant().getHullVariantId())) {
            if (key.contains(s.getVariant().getHullVariantId())) {
                //if(stats == s.getStats()) {
                return s;
            }
        }

        return null;
    }

    @Override
    public String getDescriptionParam(int index, ShipAPI.HullSize hullSize, ShipAPI ship) {
        FleetMemberAPI fm = findShip(hullSize, ship.getMutableStats());
        if(fm == null) return "SHIP NOT FOUND";
        return fm.getShipName();
    }
    @Override
    public void addPostDescriptionSection(TooltipMakerAPI tooltip, ShipAPI.HullSize hullSize, ShipAPI ship, float width, boolean isForModSpec) {
        FleetMemberAPI fm = findShip(hullSize, ship.getMutableStats());
        if (fm == null) { return; }
        Es_ShipLevelFleetData buff = (Es_ShipLevelFleetData) fm.getBuffManager().getBuff(Es_LEVEL_FUNCTION_ID);
        if (buff == null) { return; }
        int[] levels = buff.getLevelIndex();
        String qname = getQualityName(buff.getQualityFactor());

        tooltip.addPara("The ship is of %s quality, which affected base upgrade values by %s multiplier (following numbers are final calculations):", 0, getQualityColor(buff.getQualityFactor()),qname,""+String.format("%.3f",buff.getQualityFactor()));


        if (levels[0]>0) {
            tooltip.addPara(ABILITY_NAME[0] + " (%s):", 5, Color.green, ""+levels[0]);
            tooltip.addPara("  Hull durability: +%s (%s)", 0, Misc.getHighlightColor(), String.format("%.3f",fm.getStats().getHullBonus().getPercentBonus(Es_LEVEL_FUNCTION_ID).getValue()) + "%", "+" + String.format("%.0f",fm.getStats().getHullBonus().getPercentBonus(Es_LEVEL_FUNCTION_ID).getValue()*fm.getVariant().getHullSpec().getHitpoints()*0.01f));
            tooltip.addPara("  Armor durability: +%s (%s)", 0, Misc.getHighlightColor(), String.format("%.3f",fm.getStats().getArmorBonus().getPercentBonus(Es_LEVEL_FUNCTION_ID).getValue()) + "%", "+" + String.format("%.0f",fm.getStats().getArmorBonus().getPercentBonus(Es_LEVEL_FUNCTION_ID).getValue()*fm.getVariant().getHullSpec().getArmorRating()*0.01f));
            tooltip.addPara("  Weapon mounts durability: +%s", 0, Misc.getHighlightColor(), String.format("%.3f",fm.getStats().getWeaponHealthBonus().getPercentBonus(Es_LEVEL_FUNCTION_ID).getValue()) + "%");
            tooltip.addPara("  Engines durability: +%s", 0, Misc.getHighlightColor(), String.format("%.3f",fm.getStats().getEngineHealthBonus().getPercentBonus(Es_LEVEL_FUNCTION_ID).getValue()) + "%");
            tooltip.addPara("  EMP damage: %s", 0, Misc.getHighlightColor(), String.format("%.3f",fm.getStats().getEmpDamageTakenMult().getPercentStatMod(Es_LEVEL_FUNCTION_ID).getValue()) + "%");
        }

        if (levels[1]>0) {
            tooltip.addPara(ABILITY_NAME[1] + " (%s):", 5, Color.green, ""+levels[1]);
            tooltip.addPara("  Weapons range: +%s", 0, Misc.getHighlightColor(), String.format("%.3f",fm.getStats().getBallisticWeaponRangeBonus().getPercentBonus(Es_LEVEL_FUNCTION_ID).getValue()) + "%");
            tooltip.addPara("  Weapons damage: +%s", 0, Misc.getHighlightColor(), String.format("%.3f",fm.getStats().getBallisticWeaponDamageMult().getPercentStatMod(Es_LEVEL_FUNCTION_ID).getValue()) + "%");
            tooltip.addPara("  Weapons rate of fire: +%s", 0, Misc.getHighlightColor(), String.format("%.3f",fm.getStats().getBallisticRoFMult().getPercentStatMod(Es_LEVEL_FUNCTION_ID).getValue()) + "%");
        }

        if (levels[2]>0) {
            tooltip.addPara(ABILITY_NAME[2] + " (%s):", 5, Color.green, ""+levels[2]);
            tooltip.addPara("  CR per deployment: %s (%s)", 0, Misc.getHighlightColor(), String.format("%.3f",fm.getStats().getCRPerDeploymentPercent().getPercentBonus(Es_LEVEL_FUNCTION_ID).getValue()) + "%", String.format("%.2f",fm.getStats().getCRPerDeploymentPercent().getPercentBonus(Es_LEVEL_FUNCTION_ID).getValue()*fm.getVariant().getHullSpec().getCRToDeploy()*0.01f));
            tooltip.addPara("  Bonus ammunition: +%s", 0, Misc.getHighlightColor(), String.format("%.3f",fm.getStats().getBallisticAmmoBonus().getPercentBonus(Es_LEVEL_FUNCTION_ID).getValue()) + "%");
            tooltip.addPara("  Less required and more maximum crew: %s (%s,%s)", 0, Misc.getHighlightColor(), String.format("%.3f",fm.getStats().getMaxCrewMod().getPercentBonus(Es_LEVEL_FUNCTION_ID).getValue()) + "%", String.format("%.0f",fm.getStats().getMinCrewMod().getPercentBonus(Es_LEVEL_FUNCTION_ID).getValue()*fm.getVariant().getHullSpec().getMinCrew()*0.01f), "+" + String.format("%.0f",fm.getStats().getMaxCrewMod().getPercentBonus(Es_LEVEL_FUNCTION_ID).getValue()*fm.getVariant().getHullSpec().getMaxCrew()*0.01f));
            tooltip.addPara("  Repairs and recovery rates: +%s (%s)", 0, Misc.getHighlightColor(), String.format("%.3f",fm.getStats().getBaseCRRecoveryRatePercentPerDay().getPercentStatMod(Es_LEVEL_FUNCTION_ID).getValue()) + "%", "+" + String.format("%.2f",fm.getStats().getBaseCRRecoveryRatePercentPerDay().getPercentStatMod(Es_LEVEL_FUNCTION_ID).getValue()*fm.getStats().getBaseCRRecoveryRatePercentPerDay().getBaseValue()*0.01f));
            tooltip.addPara("  Fuel consumption: %s (%s)", 0, Misc.getHighlightColor(), String.format("%.3f",fm.getStats().getFuelUseMod().getPercentBonus(Es_LEVEL_FUNCTION_ID).getValue()) + "%", String.format("%.2f",fm.getStats().getFuelUseMod().getPercentBonus(Es_LEVEL_FUNCTION_ID).getValue()*fm.getVariant().getHullSpec().getFuelPerLY()*0.01f));
            tooltip.addPara("  Overall supplies consumption rates: %s (%s)", 0, Misc.getHighlightColor(), String.format("%.3f",fm.getStats().getSuppliesPerMonth().getPercentStatMod(Es_LEVEL_FUNCTION_ID).getValue()) + "%", String.format("%.2f",fm.getStats().getSuppliesPerMonth().getPercentStatMod(Es_LEVEL_FUNCTION_ID).getValue()*fm.getStats().getSuppliesPerMonth().getBaseValue()*0.01f));
//        tooltip.addPara("SuppliesToRecover reduced by: %s", 0, Misc.getHighlightColor(), String.format("%.3f",fm.getStats().getSuppliesToRecover().getPercentStatMod(Es_LEVEL_FUNCTION_ID).getValue()) + "%");
        }

        if (levels[3]>0) {
            tooltip.addPara(ABILITY_NAME[3] + " (%s):", 5, Color.green, ""+levels[3]);
            tooltip.addPara("  Maximum speed: +%s (%s)", 0, Misc.getHighlightColor(), String.format("%.3f",fm.getStats().getMaxSpeed().getPercentStatMod(Es_LEVEL_FUNCTION_ID).getValue()) + "%", "+" + String.format("%.0f",fm.getStats().getMaxSpeed().getPercentStatMod(Es_LEVEL_FUNCTION_ID).getValue()*fm.getStats().getMaxSpeed().getBaseValue()*0.01f));
            tooltip.addPara("  Acceleration and deceleration: +%s", 0, Misc.getHighlightColor(), String.format("%.3f",fm.getStats().getAcceleration().getPercentStatMod(Es_LEVEL_FUNCTION_ID).getValue()) + "%");
//        tooltip.addPara("Deceleration increased by: +%s", 0, Misc.getHighlightColor(), String.format("%.3f",fm.getStats().getDeceleration().getPercentStatMod(Es_LEVEL_FUNCTION_ID).getValue()) + "%");
            tooltip.addPara("  Maximum turn rate and turn acceleration: +%s", 0, Misc.getHighlightColor(), String.format("%.3f",fm.getStats().getMaxTurnRate().getPercentStatMod(Es_LEVEL_FUNCTION_ID).getValue()) + "%");
//        tooltip.addPara("Turn acceleration increased by: +%s", 0, Misc.getHighlightColor(), String.format("%.3f",fm.getStats().getTurnAcceleration().getPercentStatMod(Es_LEVEL_FUNCTION_ID).getValue()) + "%");
            tooltip.addPara("  Maximum burn level: +%s (%s)", 0, Misc.getHighlightColor(), String.format("%.3f",fm.getStats().getMaxBurnLevel().getPercentStatMod(Es_LEVEL_FUNCTION_ID).getValue()) + "%", "+" + String.format("%.0f",fm.getStats().getMaxBurnLevel().getPercentStatMod(Es_LEVEL_FUNCTION_ID).getValue()*fm.getStats().getMaxBurnLevel().getBaseValue()*0.01f));
        }

        if (levels[4]>0) {
            tooltip.addPara(ABILITY_NAME[4] + " (%s):", 5, Color.green, ""+levels[4]);
            tooltip.addPara("  Flux capacity and dissipation: +%s (%s,%s)", 0, Misc.getHighlightColor(), String.format("%.3f",fm.getStats().getFluxCapacity().getPercentStatMod(Es_LEVEL_FUNCTION_ID).getValue()) + "%", "+" + String.format("%.0f",fm.getStats().getFluxCapacity().getPercentStatMod(Es_LEVEL_FUNCTION_ID).getValue()*fm.getStats().getFluxCapacity().getBaseValue()*0.01f), "+" + String.format("%.0f",fm.getStats().getFluxDissipation().getPercentStatMod(Es_LEVEL_FUNCTION_ID).getValue()*fm.getStats().getFluxDissipation().getBaseValue()*0.01f));
//        tooltip.addPara("Flux dissipation: +%s", 0, Misc.getHighlightColor(), String.format("%.3f",fm.getStats().getMaxBurnLevel().getPercentStatMod(Es_LEVEL_FUNCTION_ID).getValue()) + "%");
            tooltip.addPara("  Weapon flux cost: %s", 0, Misc.getHighlightColor(), String.format("%.3f",fm.getStats().getBallisticWeaponFluxCostMod().getPercentBonus(Es_LEVEL_FUNCTION_ID).getValue()) + "%");
            if(fm.getHullSpec() != null &&
                    (fm.getHullSpec().getShieldType()== ShieldAPI.ShieldType.FRONT ||
                            fm.getHullSpec().getShieldType()== ShieldAPI.ShieldType.OMNI)) {
                tooltip.addPara("  Shield damage taken: %s (%s)", 0, Misc.getHighlightColor(), String.format("%.3f",fm.getStats().getShieldDamageTakenMult().getPercentStatMod(Es_LEVEL_FUNCTION_ID).getValue()) + "%", String.format("%.2f",fm.getStats().getShieldDamageTakenMult().getPercentStatMod(Es_LEVEL_FUNCTION_ID).getValue()*fm.getStats().getShieldDamageTakenMult().getBaseValue()*0.01f));
                tooltip.addPara("  Shield upkeep: %s (%s)", 0, Misc.getHighlightColor(), String.format("%.3f",fm.getStats().getShieldUpkeepMult().getPercentStatMod(Es_LEVEL_FUNCTION_ID).getValue()) + "%", String.format("%.2f",fm.getStats().getShieldUpkeepMult().getPercentStatMod(Es_LEVEL_FUNCTION_ID).getValue()*fm.getVariant().getHullSpec().getShieldSpec().getUpkeepCost()*0.01f));
                tooltip.addPara("  Shield unfold rate: +%s", 0, Misc.getHighlightColor(), String.format("%.3f",fm.getStats().getShieldUnfoldRateMult().getPercentStatMod(Es_LEVEL_FUNCTION_ID).getValue()) + "%");
            }
            else if(fm.getHullSpec() != null &&
                    fm.getHullSpec().getShieldType()== ShieldAPI.ShieldType.PHASE) {
                tooltip.addPara("  Phase cloak activation cost: %s (%s)", 0, Misc.getHighlightColor(), String.format("%.3f",fm.getStats().getPhaseCloakActivationCostBonus().getPercentBonus(Es_LEVEL_FUNCTION_ID).getValue()) + "%", String.format("%.2f",fm.getStats().getPhaseCloakActivationCostBonus().getPercentBonus(Es_LEVEL_FUNCTION_ID).getValue()*fm.getVariant().getHullSpec().getShieldSpec().getPhaseCost()*0.01f));
                tooltip.addPara("  Phase cloak cooldown: %s", 0, Misc.getHighlightColor(), String.format("%.3f",fm.getStats().getPhaseCloakCooldownBonus().getPercentBonus(Es_LEVEL_FUNCTION_ID).getValue()) + "%");
                tooltip.addPara("  Phase cloak upkeep Cost: %s (%s)", 0, Misc.getHighlightColor(), String.format("%.3f",fm.getStats().getPhaseCloakUpkeepCostBonus().getPercentBonus(Es_LEVEL_FUNCTION_ID).getValue()) + "%", String.format("%.2f",fm.getStats().getPhaseCloakUpkeepCostBonus().getPercentBonus(Es_LEVEL_FUNCTION_ID).getValue()*fm.getVariant().getHullSpec().getShieldSpec().getPhaseUpkeep()*0.01f));
            }
        }
        if (levels[5]>0) {
            tooltip.addPara(ABILITY_NAME[5] + " (%s) [%s]:", 5, Color.green, "" + levels[5], "Unaffected by quality");
            tooltip.addPara("  Bonus ordnance points: %s", 0, Misc.getHighlightColor(), "" + levels[5] * ORDNANCE_EFFECT_MULT.get(hullSize));
        }


    }
}