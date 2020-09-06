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

import java.util.List;

import static extrasystemreloaded.campaign.Es_ShipLevelFunctionPlugin.*;

public class ExtraSystemHM extends BaseHullMod {

//    public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
//        stats.getHitStrengthBonus().modifyPercent(id, DMGBonus);
//    }

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

        if (array[0]>0) {
            tooltip.addPara("The ship is of %s quality, which affected base upgrade values by %s multiplier (following numbers are final calculations):", 0, getQualityColor(buff.getQualityFactor()),qname,""+String.format("%.3f",buff.getQualityFactor()));
            tooltip.addPara("Hull durability: +%s (%s)", 5, Misc.getHighlightColor(), String.format("%.3f",fm.getStats().getHullBonus().getPercentBonus(Es_LEVEL_FUNCTION_ID).getValue()) + "%", "+" + String.format("%.0f",fm.getStats().getHullBonus().getPercentBonus(Es_LEVEL_FUNCTION_ID).getValue()*fm.getVariant().getHullSpec().getHitpoints()*0.01f));
            tooltip.addPara("Armor durability: +%s (%s)", 0, Misc.getHighlightColor(), String.format("%.3f",fm.getStats().getArmorBonus().getPercentBonus(Es_LEVEL_FUNCTION_ID).getValue()) + "%", "+" + String.format("%.0f",fm.getStats().getArmorBonus().getPercentBonus(Es_LEVEL_FUNCTION_ID).getValue()*fm.getVariant().getHullSpec().getArmorRating()*0.01f));
            tooltip.addPara("Weapon mounts durability: +%s", 0, Misc.getHighlightColor(), String.format("%.3f",fm.getStats().getWeaponHealthBonus().getPercentBonus(Es_LEVEL_FUNCTION_ID).getValue()) + "%");
            tooltip.addPara("Engines durability: +%s", 0, Misc.getHighlightColor(), String.format("%.3f",fm.getStats().getEngineHealthBonus().getPercentBonus(Es_LEVEL_FUNCTION_ID).getValue()) + "%");
            tooltip.addPara("EMP damage: %s", 0, Misc.getHighlightColor(), String.format("%.3f",fm.getStats().getEmpDamageTakenMult().getPercentStatMod(Es_LEVEL_FUNCTION_ID).getValue()) + "%");
        }

        if (array[1]>0) {
            tooltip.addPara("Weapons range: +%s", 5, Misc.getHighlightColor(), String.format("%.3f",fm.getStats().getBallisticWeaponRangeBonus().getPercentBonus(Es_LEVEL_FUNCTION_ID).getValue()) + "%");
            tooltip.addPara("Weapons damage: +%s", 0, Misc.getHighlightColor(), String.format("%.3f",fm.getStats().getBallisticWeaponDamageMult().getPercentStatMod(Es_LEVEL_FUNCTION_ID).getValue()) + "%");
            tooltip.addPara("Weapons rate of fire: +%s", 0, Misc.getHighlightColor(), String.format("%.3f",fm.getStats().getBallisticRoFMult().getPercentStatMod(Es_LEVEL_FUNCTION_ID).getValue()) + "%");
        }

        if (array[2]>0) {
            tooltip.addPara("CR per deployment: %s (%s)", 5, Misc.getHighlightColor(), String.format("%.3f",fm.getStats().getCRPerDeploymentPercent().getPercentBonus(Es_LEVEL_FUNCTION_ID).getValue()) + "%", String.format("%.2f",fm.getStats().getCRPerDeploymentPercent().getPercentBonus(Es_LEVEL_FUNCTION_ID).getValue()*fm.getVariant().getHullSpec().getCRToDeploy()*0.01f));
            tooltip.addPara("Bonus ammunition: +%s", 0, Misc.getHighlightColor(), String.format("%.3f",fm.getStats().getBallisticAmmoBonus().getPercentBonus(Es_LEVEL_FUNCTION_ID).getValue()) + "%");
            tooltip.addPara("Less required and more maximum crew: %s (%s)", 0, Misc.getHighlightColor(), String.format("%.3f",fm.getStats().getMaxCrewMod().getPercentBonus(Es_LEVEL_FUNCTION_ID).getValue()) + "%", String.format("%.0f",fm.getStats().getMinCrewMod().getPercentBonus(Es_LEVEL_FUNCTION_ID).getValue()*fm.getVariant().getHullSpec().getMinCrew()*0.01f) + ",+" + String.format("%.0f",fm.getStats().getMaxCrewMod().getPercentBonus(Es_LEVEL_FUNCTION_ID).getValue()*fm.getVariant().getHullSpec().getMaxCrew()*0.01f));
            tooltip.addPara("Repairs and recovery rates: +%s (%s)", 0, Misc.getHighlightColor(), String.format("%.3f",fm.getStats().getBaseCRRecoveryRatePercentPerDay().getPercentStatMod(Es_LEVEL_FUNCTION_ID).getValue()) + "%", "+" + String.format("%.2f",fm.getStats().getBaseCRRecoveryRatePercentPerDay().getPercentStatMod(Es_LEVEL_FUNCTION_ID).getValue()*fm.getStats().getBaseCRRecoveryRatePercentPerDay().getBaseValue()*0.01f));
            tooltip.addPara("Fuel consumption: %s (%s)", 0, Misc.getHighlightColor(), String.format("%.3f",fm.getStats().getFuelUseMod().getPercentBonus(Es_LEVEL_FUNCTION_ID).getValue()) + "%", String.format("%.2f",fm.getStats().getFuelUseMod().getPercentBonus(Es_LEVEL_FUNCTION_ID).getValue()*fm.getVariant().getHullSpec().getFuelPerLY()*0.01f));
            tooltip.addPara("Overall supplies consumption rates: %s (%s)", 0, Misc.getHighlightColor(), String.format("%.3f",fm.getStats().getSuppliesPerMonth().getPercentStatMod(Es_LEVEL_FUNCTION_ID).getValue()) + "%", String.format("%.2f",fm.getStats().getSuppliesPerMonth().getPercentStatMod(Es_LEVEL_FUNCTION_ID).getValue()*fm.getStats().getSuppliesPerMonth().getBaseValue()*0.01f));
//        tooltip.addPara("SuppliesToRecover reduced by: %s", 0, Misc.getHighlightColor(), String.format("%.3f",fm.getStats().getSuppliesToRecover().getPercentStatMod(Es_LEVEL_FUNCTION_ID).getValue()) + "%");
        }

        if (array[3]>0) {
            tooltip.addPara("Maximum speed: +%s (%s)", 5, Misc.getHighlightColor(), String.format("%.3f",fm.getStats().getMaxSpeed().getPercentStatMod(Es_LEVEL_FUNCTION_ID).getValue()) + "%", "+" + String.format("%.0f",fm.getStats().getMaxSpeed().getPercentStatMod(Es_LEVEL_FUNCTION_ID).getValue()*fm.getStats().getMaxSpeed().getBaseValue()*0.01f));
            tooltip.addPara("Acceleration and deceleration: +%s", 0, Misc.getHighlightColor(), String.format("%.3f",fm.getStats().getAcceleration().getPercentStatMod(Es_LEVEL_FUNCTION_ID).getValue()) + "%");
//        tooltip.addPara("Deceleration increased by: +%s", 0, Misc.getHighlightColor(), String.format("%.3f",fm.getStats().getDeceleration().getPercentStatMod(Es_LEVEL_FUNCTION_ID).getValue()) + "%");
            tooltip.addPara("Maximum turn rate and turn acceleration: +%s", 0, Misc.getHighlightColor(), String.format("%.3f",fm.getStats().getMaxTurnRate().getPercentStatMod(Es_LEVEL_FUNCTION_ID).getValue()) + "%");
//        tooltip.addPara("Turn acceleration increased by: +%s", 0, Misc.getHighlightColor(), String.format("%.3f",fm.getStats().getTurnAcceleration().getPercentStatMod(Es_LEVEL_FUNCTION_ID).getValue()) + "%");
            tooltip.addPara("Maximum burn level: +%s (%s)", 0, Misc.getHighlightColor(), String.format("%.3f",fm.getStats().getMaxBurnLevel().getPercentStatMod(Es_LEVEL_FUNCTION_ID).getValue()) + "%", "+" + String.format("%.0f",fm.getStats().getMaxBurnLevel().getPercentStatMod(Es_LEVEL_FUNCTION_ID).getValue()*fm.getStats().getMaxBurnLevel().getBaseValue()*0.01f));
        }

        if (array[4]>0) {
            tooltip.addPara("Flux capacity and dissipation: +%s (%s)", 5, Misc.getHighlightColor(), String.format("%.3f",fm.getStats().getFluxCapacity().getPercentStatMod(Es_LEVEL_FUNCTION_ID).getValue()) + "%", String.format("%.0f",fm.getStats().getFluxCapacity().getPercentStatMod(Es_LEVEL_FUNCTION_ID).getValue()*fm.getStats().getFluxCapacity().getBaseValue()*0.01f));
//        tooltip.addPara("Flux dissipation: +%s", 0, Misc.getHighlightColor(), String.format("%.3f",fm.getStats().getMaxBurnLevel().getPercentStatMod(Es_LEVEL_FUNCTION_ID).getValue()) + "%");
            tooltip.addPara("Weapon flux cost: %s", 0, Misc.getHighlightColor(), String.format("%.3f",fm.getStats().getBallisticWeaponFluxCostMod().getPercentBonus(Es_LEVEL_FUNCTION_ID).getValue()) + "%");
            if(fm.getHullSpec() != null &&
                    (fm.getHullSpec().getShieldType()== ShieldAPI.ShieldType.FRONT ||
                            fm.getHullSpec().getShieldType()== ShieldAPI.ShieldType.OMNI)) {
                tooltip.addPara("Shield damage taken: %s (%s)", 0, Misc.getHighlightColor(), String.format("%.3f",fm.getStats().getShieldDamageTakenMult().getPercentStatMod(Es_LEVEL_FUNCTION_ID).getValue()) + "%", String.format("%.2f",fm.getStats().getShieldDamageTakenMult().getPercentStatMod(Es_LEVEL_FUNCTION_ID).getValue()*fm.getStats().getShieldDamageTakenMult().getBaseValue()*0.01f));
                tooltip.addPara("Shield upkeep: %s (%s)", 0, Misc.getHighlightColor(), String.format("%.3f",fm.getStats().getShieldUpkeepMult().getPercentStatMod(Es_LEVEL_FUNCTION_ID).getValue()) + "%", String.format("%.2f",fm.getStats().getShieldUpkeepMult().getPercentStatMod(Es_LEVEL_FUNCTION_ID).getValue()*fm.getVariant().getHullSpec().getShieldSpec().getUpkeepCost()*0.01f));
                tooltip.addPara("Shield unfold rate: +%s", 0, Misc.getHighlightColor(), String.format("%.3f",fm.getStats().getShieldUnfoldRateMult().getPercentStatMod(Es_LEVEL_FUNCTION_ID).getValue()) + "%");
            }
            else if(fm.getHullSpec() != null &&
                    fm.getHullSpec().getShieldType()== ShieldAPI.ShieldType.PHASE) {
                tooltip.addPara("Phase cloak activation cost: %s (%s)", 0, Misc.getHighlightColor(), String.format("%.3f",fm.getStats().getPhaseCloakActivationCostBonus().getPercentBonus(Es_LEVEL_FUNCTION_ID).getValue()) + "%", String.format("%.2f",fm.getStats().getPhaseCloakActivationCostBonus().getPercentBonus(Es_LEVEL_FUNCTION_ID).getValue()*fm.getVariant().getHullSpec().getShieldSpec().getPhaseCost()*0.01f));
                tooltip.addPara("Phase cloak cooldown: %s", 0, Misc.getHighlightColor(), String.format("%.3f",fm.getStats().getPhaseCloakCooldownBonus().getPercentBonus(Es_LEVEL_FUNCTION_ID).getValue()) + "%");
                tooltip.addPara("Phase cloak upkeep Cost: %s (%s)", 0, Misc.getHighlightColor(), String.format("%.3f",fm.getStats().getPhaseCloakUpkeepCostBonus().getPercentBonus(Es_LEVEL_FUNCTION_ID).getValue()) + "%", String.format("%.2f",fm.getStats().getPhaseCloakUpkeepCostBonus().getPercentBonus(Es_LEVEL_FUNCTION_ID).getValue()*fm.getVariant().getHullSpec().getShieldSpec().getPhaseUpkeep()*0.01f));
            }
        }

        return;
    }
}