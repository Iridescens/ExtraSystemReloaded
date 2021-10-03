package extrasystemreloaded.upgrades.impl;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import extrasystemreloaded.util.ExtraSystems;
import extrasystemreloaded.util.StatUtils;
import extrasystemreloaded.upgrades.Upgrade;
import org.json.JSONException;
import org.json.JSONObject;

import java.awt.*;

public class Weapons extends Upgrade {
    public static final String UPGRADE_KEY = "Weapons";

    private static String NAME = "Weapons";
    private static float WEAPON_HEALTH_MULT;

    private static float RANGE_SCALAR;
    private static float RANGE_QUALITY_MULT;

    private static float DAMAGE_SCALAR;
    private static float DAMAGE_QUALITY_MULT;

    private static float FLUX_COST_SCALAR;
    private static float FLUX_COST_QUALITY_MULT;


    @Override
    public String getKey() {
        return UPGRADE_KEY;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public void loadConfig(JSONObject upgradeSettings) throws JSONException {
        NAME = upgradeSettings.getString("name");
        WEAPON_HEALTH_MULT = (float) upgradeSettings.getDouble("weaponHealthScalar");
        RANGE_SCALAR = (float) upgradeSettings.getDouble("rangeUpgradeScalar");
        RANGE_QUALITY_MULT = (float) upgradeSettings.getDouble("rangeQualityMult");
        DAMAGE_SCALAR = (float) upgradeSettings.getDouble("damageUpgradeScalar");
        DAMAGE_QUALITY_MULT = (float) upgradeSettings.getDouble("damageQualityMult");
        FLUX_COST_SCALAR = (float) upgradeSettings.getDouble("fluxCostUpgradeScalar");
        FLUX_COST_QUALITY_MULT = (float) upgradeSettings.getDouble("fluxCostQualityMult");
    }

    @Override
    public String getDescription() {
        return "Improve weapon range, damage, flux costs, and health.";
    }

    @Override
    public void applyUpgradeToStats(FleetMemberAPI fm, MutableShipStatsAPI stats, float hullSizeFactor, int level, float quality) {
        StatUtils.setStatPercentBonus(stats.getWeaponHealthBonus(), this.getBuffId(), level, quality, WEAPON_HEALTH_MULT, hullSizeFactor);

        StatUtils.setStatPercentBonus(stats.getBallisticWeaponRangeBonus(), this.getBuffId(),
                StatUtils.getDiminishingReturnsTotal(level, getMaxLevel(fm.getHullSpec().getHullSize()), quality, RANGE_SCALAR, RANGE_QUALITY_MULT, hullSizeFactor));
        StatUtils.setStatPercentBonus(stats.getEnergyWeaponRangeBonus(), this.getBuffId(),
                StatUtils.getDiminishingReturnsTotal(level, getMaxLevel(fm.getHullSpec().getHullSize()), quality, RANGE_SCALAR, RANGE_QUALITY_MULT, hullSizeFactor));
        StatUtils.setStatPercentBonus(stats.getMissileWeaponRangeBonus(), this.getBuffId(),
                StatUtils.getDiminishingReturnsTotal(level, getMaxLevel(fm.getHullSpec().getHullSize()), quality, RANGE_SCALAR, RANGE_QUALITY_MULT, hullSizeFactor));

        StatUtils.setStatPercentBonus(stats.getBallisticWeaponDamageMult(), this.getBuffId(),
                StatUtils.getDiminishingReturnsTotal(level, getMaxLevel(fm.getHullSpec().getHullSize()), quality, DAMAGE_SCALAR, DAMAGE_QUALITY_MULT, hullSizeFactor));

        StatUtils.setStatPercentBonus(stats.getEnergyWeaponDamageMult(), this.getBuffId(),
                StatUtils.getDiminishingReturnsTotal(level, getMaxLevel(fm.getHullSpec().getHullSize()), quality, DAMAGE_SCALAR, DAMAGE_QUALITY_MULT, hullSizeFactor));

        StatUtils.setStatPercentBonus(stats.getMissileWeaponDamageMult(), this.getBuffId(),
                StatUtils.getDiminishingReturnsTotal(level, getMaxLevel(fm.getHullSpec().getHullSize()), quality, DAMAGE_SCALAR, DAMAGE_QUALITY_MULT, hullSizeFactor));

        StatUtils.setStatPercentBonus(stats.getBallisticWeaponFluxCostMod(), this.getBuffId(),
                StatUtils.getDiminishingReturnsTotal(level, getMaxLevel(fm.getHullSpec().getHullSize()), quality, FLUX_COST_SCALAR, FLUX_COST_QUALITY_MULT, hullSizeFactor));
        StatUtils.setStatPercentBonus(stats.getEnergyWeaponFluxCostMod(), this.getBuffId(),
                StatUtils.getDiminishingReturnsTotal(level, getMaxLevel(fm.getHullSpec().getHullSize()), quality, FLUX_COST_SCALAR, FLUX_COST_QUALITY_MULT, hullSizeFactor));
        StatUtils.setStatPercentBonus(stats.getMissileWeaponFluxCostMod(), this.getBuffId(),
                StatUtils.getDiminishingReturnsTotal(level, getMaxLevel(fm.getHullSpec().getHullSize()), quality, FLUX_COST_SCALAR, FLUX_COST_QUALITY_MULT, hullSizeFactor));
    }

    @Override
    public void modifyToolTip(TooltipMakerAPI tooltip, FleetMemberAPI fm, ExtraSystems systems, boolean expand) {
        int level = systems.getUpgrade(this);

        if (level > 0) {
            if(expand) {
                tooltip.addPara(this.getName() + " (%s):", 5, Color.green, String.valueOf(level));

                StatUtils.addPercentBonusToTooltip(tooltip, "  Weapons range: +%s",
                        fm.getStats().getBallisticWeaponRangeBonus().getPercentBonus(this.getBuffId()).getValue());

                StatUtils.addPercentBonusToTooltip(tooltip, "  Weapons damage: +%s",
                        fm.getStats().getBallisticWeaponDamageMult().getPercentStatMod(this.getBuffId()).getValue());

                StatUtils.addPercentBonusToTooltip(tooltip, "  Weapons flux cost: %s",
                        fm.getStats().getBallisticWeaponFluxCostMod().getPercentBonus(this.getBuffId()).getValue());

                StatUtils.addPercentBonusToTooltip(tooltip, "  Weapon mounts durability: +%s",
                        fm.getStats().getWeaponHealthBonus().getPercentBonus(this.getBuffId()).getValue());
            } else {
                tooltip.addPara(this.getName() + " (%s)", 5, Color.green, String.valueOf(level));
            }
        }
    }
}
