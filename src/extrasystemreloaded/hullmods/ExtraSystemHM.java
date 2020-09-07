package extrasystemreloaded.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShieldAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import extrasystemreloaded.campaign.Es_ShipLevelFleetData;

import java.awt.*;
import java.util.List;

import static extrasystemreloaded.campaign.Es_ShipLevelFunctionPlugin.*;

public class ExtraSystemHM extends BaseHullMod {

    private static final String[] ABILITY_NAME = {
            Global.getSettings().getString("AbilityName", "Durability"),
            Global.getSettings().getString("AbilityName", "WeaponProficiency"),
            Global.getSettings().getString("AbilityName", "Logistics"),
            Global.getSettings().getString("AbilityName", "Flexibility"),
            Global.getSettings().getString("AbilityName", "Technology")
    };

    @Override
    public void applyEffectsBeforeShipCreation(ShipAPI.HullSize hullSize, MutableShipStatsAPI stats, String id) {
        FleetMemberAPI fm = findShip(hullSize, stats);
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

        for (int i = 0; i < levels.length; i++) {
            float level = (float)levels[i]*buff.getQualityFactor();
            switch (i) {
                case 0://иЂђд№…
                    fm.getStats().getHullBonus().modifyPercent(Es_LEVEL_FUNCTION_ID, buff.getHullSizeFactor()*level*3f);
                    fm.getStats().getArmorBonus().modifyPercent(Es_LEVEL_FUNCTION_ID, buff.getHullSizeFactor()*level*3f);
                    fm.getStats().getWeaponHealthBonus().modifyPercent(Es_LEVEL_FUNCTION_ID, buff.getHullSizeFactor()*level*3f);
                    fm.getStats().getEngineHealthBonus().modifyPercent(Es_LEVEL_FUNCTION_ID, buff.getHullSizeFactor()*level*3f);
                    fm.getStats().getEmpDamageTakenMult().modifyPercent(Es_LEVEL_FUNCTION_ID, -buff.getHullSizeFactor()*level*4f);
                    break;
                case 1://ж­¦е™Ё
                    fm.getStats().getBallisticWeaponRangeBonus().modifyPercent(Es_LEVEL_FUNCTION_ID, buff.getHullSizeFactor()*level*1.5f);
                    fm.getStats().getEnergyWeaponRangeBonus().modifyPercent(Es_LEVEL_FUNCTION_ID, buff.getHullSizeFactor()*level*1.5f);
                    fm.getStats().getMissileWeaponRangeBonus().modifyPercent(Es_LEVEL_FUNCTION_ID, buff.getHullSizeFactor()*level*1.5f);

                    fm.getStats().getBallisticWeaponDamageMult().modifyPercent(Es_LEVEL_FUNCTION_ID, buff.getHullSizeFactor()*level*1.5f);
                    fm.getStats().getEnergyWeaponDamageMult().modifyPercent(Es_LEVEL_FUNCTION_ID, buff.getHullSizeFactor()*level*1.5f);
                    fm.getStats().getMissileWeaponDamageMult().modifyPercent(Es_LEVEL_FUNCTION_ID, buff.getHullSizeFactor()*level*1.5f);

                    fm.getStats().getBallisticRoFMult().modifyPercent(Es_LEVEL_FUNCTION_ID, buff.getHullSizeFactor()*level*1.5f);
                    fm.getStats().getEnergyRoFMult().modifyPercent(Es_LEVEL_FUNCTION_ID, buff.getHullSizeFactor()*level*1.5f);
                    fm.getStats().getMissileRoFMult().modifyPercent(Es_LEVEL_FUNCTION_ID, buff.getHullSizeFactor()*level*1.5f);
                    break;
                case 2://еђЋе‹¤
                    fm.getStats().getCRPerDeploymentPercent().modifyPercent(Es_LEVEL_FUNCTION_ID, -buff.getHullSizeFactor()*level*1.5f);

                    fm.getStats().getBallisticAmmoBonus().modifyPercent(Es_LEVEL_FUNCTION_ID, buff.getHullSizeFactor()*level*2f);
                    fm.getStats().getEnergyAmmoBonus().modifyPercent(Es_LEVEL_FUNCTION_ID, buff.getHullSizeFactor()*level*2f);
                    fm.getStats().getMissileAmmoBonus().modifyPercent(Es_LEVEL_FUNCTION_ID, buff.getHullSizeFactor()*level*2f);

                    fm.getStats().getMinCrewMod().modifyPercent(Es_LEVEL_FUNCTION_ID, -buff.getHullSizeFactor()*level*2f);
                    fm.getStats().getMaxCrewMod().modifyPercent(Es_LEVEL_FUNCTION_ID, buff.getHullSizeFactor()*level*2f);

                    fm.getStats().getRepairRatePercentPerDay().modifyPercent(Es_LEVEL_FUNCTION_ID, buff.getHullSizeFactor()*level*2.5f);
                    fm.getStats().getBaseCRRecoveryRatePercentPerDay().modifyPercent(Es_LEVEL_FUNCTION_ID, buff.getHullSizeFactor()*level*2.5f);

                    fm.getStats().getFuelUseMod().modifyPercent(Es_LEVEL_FUNCTION_ID, -buff.getHullSizeFactor()*level*2.5f);

                    fm.getStats().getSuppliesPerMonth().modifyPercent(Es_LEVEL_FUNCTION_ID, -buff.getHullSizeFactor()*level*2f);
                    fm.getStats().getSuppliesToRecover().modifyPercent(Es_LEVEL_FUNCTION_ID, -buff.getHullSizeFactor()*level*2f);
                    break;
                case 3://жњєеЉЁ
                    fm.getStats().getMaxSpeed().modifyPercent(Es_LEVEL_FUNCTION_ID, buff.getHullSizeFactor()*level*2f);
                    fm.getStats().getAcceleration().modifyPercent(Es_LEVEL_FUNCTION_ID, buff.getHullSizeFactor()*level*3f);
                    fm.getStats().getDeceleration().modifyPercent(Es_LEVEL_FUNCTION_ID, buff.getHullSizeFactor()*level*3f);

                    fm.getStats().getMaxTurnRate().modifyPercent(Es_LEVEL_FUNCTION_ID, buff.getHullSizeFactor()*level*3f);
                    fm.getStats().getTurnAcceleration().modifyPercent(Es_LEVEL_FUNCTION_ID, buff.getHullSizeFactor()*level*3f);

                    fm.getStats().getMaxBurnLevel().modifyPercent(Es_LEVEL_FUNCTION_ID, buff.getHullSizeFactor()*level*2f);
                    break;
                case 4://з§‘жЉЂ
                    fm.getStats().getFluxCapacity().modifyPercent(Es_LEVEL_FUNCTION_ID, buff.getHullSizeFactor()*level*2f);
                    fm.getStats().getFluxDissipation().modifyPercent(Es_LEVEL_FUNCTION_ID, buff.getHullSizeFactor()*level*2f);

                    fm.getStats().getBallisticWeaponFluxCostMod().modifyPercent(Es_LEVEL_FUNCTION_ID, -buff.getHullSizeFactor()*level*1.5f);
                    fm.getStats().getMissileWeaponFluxCostMod().modifyPercent(Es_LEVEL_FUNCTION_ID, -buff.getHullSizeFactor()*level*1.5f);
                    fm.getStats().getEnergyWeaponFluxCostMod().modifyPercent(Es_LEVEL_FUNCTION_ID, -buff.getHullSizeFactor()*level*1.5f);
//
//					if(member!=null && fm.getHullSpec() != null)
//						  Global.getLogger(Es_ShipLevelFleetData.class).log(Level.INFO,fm.getHullSpec().getShieldType());
                    if(fm.getHullSpec() != null &&
                            (fm.getHullSpec().getShieldType()== ShieldAPI.ShieldType.FRONT ||
                                    fm.getHullSpec().getShieldType()== ShieldAPI.ShieldType.OMNI)) {
                        fm.getStats().getShieldDamageTakenMult().modifyPercent(Es_LEVEL_FUNCTION_ID, -buff.getHullSizeFactor()*level*1.5f);
                        fm.getStats().getShieldUpkeepMult().modifyPercent(Es_LEVEL_FUNCTION_ID, -buff.getHullSizeFactor()*level*2f);
                        fm.getStats().getShieldUnfoldRateMult().modifyPercent(Es_LEVEL_FUNCTION_ID, buff.getHullSizeFactor()*level*5f);
                    }
                    else if(fm.getHullSpec() != null &&
                            fm.getHullSpec().getShieldType()== ShieldAPI.ShieldType.PHASE) {
                        fm.getStats().getPhaseCloakActivationCostBonus().modifyPercent(Es_LEVEL_FUNCTION_ID, -buff.getHullSizeFactor()*level*2.5f);
                        fm.getStats().getPhaseCloakCooldownBonus().modifyPercent(Es_LEVEL_FUNCTION_ID, -buff.getHullSizeFactor()*level*2.5f);
                        fm.getStats().getPhaseCloakUpkeepCostBonus().modifyPercent(Es_LEVEL_FUNCTION_ID, -buff.getHullSizeFactor()*level*2f);
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

        try {
            members = Global.getSector().getPlayerFleet().getFleetData().getMembersListCopy();
        } catch (NullPointerException e) { return null; }

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
            if (key.equals(s.getVariant().getHullVariantId())) {
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
        Es_ShipLevelFleetData buff = (Es_ShipLevelFleetData) fm.getBuffManager().getBuff(Es_LEVEL_FUNCTION_ID);
        int[] array = buff.getLevelIndex();
        String qname = getQualityName(buff.getQualityFactor());

        tooltip.addPara("The ship is of %s quality, which affected base upgrade values by %s multiplier (following numbers are final calculations):", 0, getQualityColor(buff.getQualityFactor()),qname,""+String.format("%.3f",buff.getQualityFactor()));

        if (array[0]>0) {
            tooltip.addPara(ABILITY_NAME[0] + " (%s):", 5, Color.green, ""+array[0]);
            tooltip.addPara("  Hull durability: +%s (%s)", 0, Misc.getHighlightColor(), String.format("%.3f",fm.getStats().getHullBonus().getPercentBonus(Es_LEVEL_FUNCTION_ID).getValue()) + "%", "+" + String.format("%.0f",fm.getStats().getHullBonus().getPercentBonus(Es_LEVEL_FUNCTION_ID).getValue()*fm.getVariant().getHullSpec().getHitpoints()*0.01f));
            tooltip.addPara("  Armor durability: +%s (%s)", 0, Misc.getHighlightColor(), String.format("%.3f",fm.getStats().getArmorBonus().getPercentBonus(Es_LEVEL_FUNCTION_ID).getValue()) + "%", "+" + String.format("%.0f",fm.getStats().getArmorBonus().getPercentBonus(Es_LEVEL_FUNCTION_ID).getValue()*fm.getVariant().getHullSpec().getArmorRating()*0.01f));
            tooltip.addPara("  Weapon mounts durability: +%s", 0, Misc.getHighlightColor(), String.format("%.3f",fm.getStats().getWeaponHealthBonus().getPercentBonus(Es_LEVEL_FUNCTION_ID).getValue()) + "%");
            tooltip.addPara("  Engines durability: +%s", 0, Misc.getHighlightColor(), String.format("%.3f",fm.getStats().getEngineHealthBonus().getPercentBonus(Es_LEVEL_FUNCTION_ID).getValue()) + "%");
            tooltip.addPara("  EMP damage: %s", 0, Misc.getHighlightColor(), String.format("%.3f",fm.getStats().getEmpDamageTakenMult().getPercentStatMod(Es_LEVEL_FUNCTION_ID).getValue()) + "%");
        }

        if (array[1]>0) {
            tooltip.addPara(ABILITY_NAME[1] + " (%s):", 5, Color.green, ""+array[1]);
            tooltip.addPara("  Weapons range: +%s", 0, Misc.getHighlightColor(), String.format("%.3f",fm.getStats().getBallisticWeaponRangeBonus().getPercentBonus(Es_LEVEL_FUNCTION_ID).getValue()) + "%");
            tooltip.addPara("  Weapons damage: +%s", 0, Misc.getHighlightColor(), String.format("%.3f",fm.getStats().getBallisticWeaponDamageMult().getPercentStatMod(Es_LEVEL_FUNCTION_ID).getValue()) + "%");
            tooltip.addPara("  Weapons rate of fire: +%s", 0, Misc.getHighlightColor(), String.format("%.3f",fm.getStats().getBallisticRoFMult().getPercentStatMod(Es_LEVEL_FUNCTION_ID).getValue()) + "%");
        }

        if (array[2]>0) {
            tooltip.addPara(ABILITY_NAME[2] + " (%s):", 5, Color.green, ""+array[2]);
            tooltip.addPara("  CR per deployment: %s (%s)", 0, Misc.getHighlightColor(), String.format("%.3f",fm.getStats().getCRPerDeploymentPercent().getPercentBonus(Es_LEVEL_FUNCTION_ID).getValue()) + "%", String.format("%.2f",fm.getStats().getCRPerDeploymentPercent().getPercentBonus(Es_LEVEL_FUNCTION_ID).getValue()*fm.getVariant().getHullSpec().getCRToDeploy()*0.01f));
            tooltip.addPara("  Bonus ammunition: +%s", 0, Misc.getHighlightColor(), String.format("%.3f",fm.getStats().getBallisticAmmoBonus().getPercentBonus(Es_LEVEL_FUNCTION_ID).getValue()) + "%");
            tooltip.addPara("  Less required and more maximum crew: %s (%s,%s)", 0, Misc.getHighlightColor(), String.format("%.3f",fm.getStats().getMaxCrewMod().getPercentBonus(Es_LEVEL_FUNCTION_ID).getValue()) + "%", String.format("%.0f",fm.getStats().getMinCrewMod().getPercentBonus(Es_LEVEL_FUNCTION_ID).getValue()*fm.getVariant().getHullSpec().getMinCrew()*0.01f), "+" + String.format("%.0f",fm.getStats().getMaxCrewMod().getPercentBonus(Es_LEVEL_FUNCTION_ID).getValue()*fm.getVariant().getHullSpec().getMaxCrew()*0.01f));
            tooltip.addPara("  Repairs and recovery rates: +%s (%s)", 0, Misc.getHighlightColor(), String.format("%.3f",fm.getStats().getBaseCRRecoveryRatePercentPerDay().getPercentStatMod(Es_LEVEL_FUNCTION_ID).getValue()) + "%", "+" + String.format("%.2f",fm.getStats().getBaseCRRecoveryRatePercentPerDay().getPercentStatMod(Es_LEVEL_FUNCTION_ID).getValue()*fm.getStats().getBaseCRRecoveryRatePercentPerDay().getBaseValue()*0.01f));
            tooltip.addPara("  Fuel consumption: %s (%s)", 0, Misc.getHighlightColor(), String.format("%.3f",fm.getStats().getFuelUseMod().getPercentBonus(Es_LEVEL_FUNCTION_ID).getValue()) + "%", String.format("%.2f",fm.getStats().getFuelUseMod().getPercentBonus(Es_LEVEL_FUNCTION_ID).getValue()*fm.getVariant().getHullSpec().getFuelPerLY()*0.01f));
            tooltip.addPara("  Overall supplies consumption rates: %s (%s)", 0, Misc.getHighlightColor(), String.format("%.3f",fm.getStats().getSuppliesPerMonth().getPercentStatMod(Es_LEVEL_FUNCTION_ID).getValue()) + "%", String.format("%.2f",fm.getStats().getSuppliesPerMonth().getPercentStatMod(Es_LEVEL_FUNCTION_ID).getValue()*fm.getStats().getSuppliesPerMonth().getBaseValue()*0.01f));
//        tooltip.addPara("SuppliesToRecover reduced by: %s", 0, Misc.getHighlightColor(), String.format("%.3f",fm.getStats().getSuppliesToRecover().getPercentStatMod(Es_LEVEL_FUNCTION_ID).getValue()) + "%");
        }

        if (array[3]>0) {
            tooltip.addPara(ABILITY_NAME[3] + " (%s):", 5, Color.green, ""+array[3]);
            tooltip.addPara("  Maximum speed: +%s (%s)", 0, Misc.getHighlightColor(), String.format("%.3f",fm.getStats().getMaxSpeed().getPercentStatMod(Es_LEVEL_FUNCTION_ID).getValue()) + "%", "+" + String.format("%.0f",fm.getStats().getMaxSpeed().getPercentStatMod(Es_LEVEL_FUNCTION_ID).getValue()*fm.getStats().getMaxSpeed().getBaseValue()*0.01f));
            tooltip.addPara("  Acceleration and deceleration: +%s", 0, Misc.getHighlightColor(), String.format("%.3f",fm.getStats().getAcceleration().getPercentStatMod(Es_LEVEL_FUNCTION_ID).getValue()) + "%");
//        tooltip.addPara("Deceleration increased by: +%s", 0, Misc.getHighlightColor(), String.format("%.3f",fm.getStats().getDeceleration().getPercentStatMod(Es_LEVEL_FUNCTION_ID).getValue()) + "%");
            tooltip.addPara("  Maximum turn rate and turn acceleration: +%s", 0, Misc.getHighlightColor(), String.format("%.3f",fm.getStats().getMaxTurnRate().getPercentStatMod(Es_LEVEL_FUNCTION_ID).getValue()) + "%");
//        tooltip.addPara("Turn acceleration increased by: +%s", 0, Misc.getHighlightColor(), String.format("%.3f",fm.getStats().getTurnAcceleration().getPercentStatMod(Es_LEVEL_FUNCTION_ID).getValue()) + "%");
            tooltip.addPara("  Maximum burn level: +%s (%s)", 0, Misc.getHighlightColor(), String.format("%.3f",fm.getStats().getMaxBurnLevel().getPercentStatMod(Es_LEVEL_FUNCTION_ID).getValue()) + "%", "+" + String.format("%.0f",fm.getStats().getMaxBurnLevel().getPercentStatMod(Es_LEVEL_FUNCTION_ID).getValue()*fm.getStats().getMaxBurnLevel().getBaseValue()*0.01f));
        }

        if (array[4]>0) {
            tooltip.addPara(ABILITY_NAME[4] + " (%s):", 5, Color.green, ""+array[4]);
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

        return;
    }
}