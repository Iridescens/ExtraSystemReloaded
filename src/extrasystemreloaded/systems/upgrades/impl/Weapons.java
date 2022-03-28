package extrasystemreloaded.systems.upgrades.impl;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import extrasystemreloaded.util.ExtraSystems;
import extrasystemreloaded.util.StatUtils;
import extrasystemreloaded.systems.upgrades.Upgrade;
import org.json.JSONException;

import java.awt.*;

public class Weapons extends Upgrade {
    private static float WEAPON_HEALTH_MULT;

    private static float RANGE_SCALAR;
    private static float RANGE_QUALITY_MULT;

    private static float DAMAGE_SCALAR;
    private static float DAMAGE_QUALITY_MULT;

    private static float FLUX_COST_SCALAR;
    private static float FLUX_COST_QUALITY_MULT;

    @Override
    protected void loadConfig() throws JSONException {
        WEAPON_HEALTH_MULT = (float) upgradeSettings.getDouble("weaponHealthScalar");
        RANGE_SCALAR = (float) upgradeSettings.getDouble("rangeUpgradeScalar");
        RANGE_QUALITY_MULT = (float) upgradeSettings.getDouble("rangeQualityMult");
        DAMAGE_SCALAR = (float) upgradeSettings.getDouble("damageUpgradeScalar");
        DAMAGE_QUALITY_MULT = (float) upgradeSettings.getDouble("damageQualityMult");
        FLUX_COST_SCALAR = (float) upgradeSettings.getDouble("fluxCostUpgradeScalar");
        FLUX_COST_QUALITY_MULT = (float) upgradeSettings.getDouble("fluxCostQualityMult");
    }

    @Override
    public void applyUpgradeToStats(FleetMemberAPI fm, MutableShipStatsAPI stats, float hullSizeFactor, int level, float quality) {
        StatUtils.setStatPercentBonus(stats.getWeaponHealthBonus(), this.getBuffId(), level, quality, WEAPON_HEALTH_MULT, hullSizeFactor);

        float rangeBonus = StatUtils.getDiminishingReturnsTotal(level, getMaxLevel(fm.getHullSpec().getHullSize()), quality, RANGE_SCALAR, RANGE_QUALITY_MULT, hullSizeFactor);
        StatUtils.setStatPercentBonus(stats.getBallisticWeaponRangeBonus(), this.getBuffId(), rangeBonus);
        StatUtils.setStatPercentBonus(stats.getEnergyWeaponRangeBonus(), this.getBuffId(), rangeBonus);
        StatUtils.setStatPercentBonus(stats.getMissileWeaponRangeBonus(), this.getBuffId(), rangeBonus);

        float damageBonus = StatUtils.getDiminishingReturnsTotal(level, getMaxLevel(fm.getHullSpec().getHullSize()), quality, DAMAGE_SCALAR, DAMAGE_QUALITY_MULT, hullSizeFactor);
        StatUtils.setStatPercentBonus(stats.getBallisticWeaponDamageMult(), this.getBuffId(), damageBonus);
        StatUtils.setStatPercentBonus(stats.getEnergyWeaponDamageMult(), this.getBuffId(), damageBonus);
        StatUtils.setStatPercentBonus(stats.getMissileWeaponDamageMult(), this.getBuffId(), damageBonus);

        float fluxReduction = StatUtils.getDiminishingReturnsTotal(level, getMaxLevel(fm.getHullSpec().getHullSize()), quality, FLUX_COST_SCALAR, FLUX_COST_QUALITY_MULT, hullSizeFactor);
        StatUtils.setStatMultBonus(stats.getBallisticWeaponFluxCostMod(), this.getBuffId(),
                1f + (fluxReduction / 100f));
        StatUtils.setStatMultBonus(stats.getEnergyWeaponFluxCostMod(), this.getBuffId(),
                1f + (fluxReduction / 100f));
        StatUtils.setStatMultBonus(stats.getMissileWeaponFluxCostMod(), this.getBuffId(),
                1f + (fluxReduction / 100f));
    }

    @Override
    public void modifyToolTip(TooltipMakerAPI tooltip, FleetMemberAPI fm, ExtraSystems systems, boolean expand) {
        int level = systems.getUpgrade(this);

        if (level > 0) {
            if(expand) {
                tooltip.addPara(this.getName() + " (%s):", 5, Color.green, String.valueOf(level));

                this.addIncreaseToTooltip(tooltip,
                        "weaponDamageIncrease",
                        fm.getStats().getBallisticWeaponDamageMult().getPercentStatMod(this.getBuffId()).getValue());

                this.addIncreaseToTooltip(tooltip,
                        "weaponRangeIncrease",
                        fm.getStats().getBallisticWeaponRangeBonus().getPercentBonus(this.getBuffId()).getValue());

                this.addDecreaseToTooltip(tooltip,
                        "weaponFluxDecrease",
                        fm.getStats().getBallisticWeaponFluxCostMod().getMultBonus(this.getBuffId()).getValue());

                this.addIncreaseToTooltip(tooltip,
                        "weaponDurabilityIncrease",
                        fm.getStats().getWeaponHealthBonus().getPercentBonus(this.getBuffId()).getValue());
            } else {
                tooltip.addPara(this.getName() + " (%s)", 5, Color.green, String.valueOf(level));
            }
        }
    }
}
