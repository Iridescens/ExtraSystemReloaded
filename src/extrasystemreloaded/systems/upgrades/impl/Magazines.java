package extrasystemreloaded.systems.upgrades.impl;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import extrasystemreloaded.util.ExtraSystems;
import extrasystemreloaded.util.StatUtils;
import extrasystemreloaded.systems.upgrades.Upgrade;
import org.json.JSONException;

import java.awt.*;

public class Magazines extends Upgrade {
    private static String NAME;
    private static float MISSILE_MAGAZINE_MULT;

    private static float ROF_SCALAR;
    private static float ROF_QUALITY_MULT;

    @Override
    protected void loadConfig() throws JSONException {
        MISSILE_MAGAZINE_MULT = (float) upgradeSettings.getDouble("missileMagazineScalar");
        ROF_SCALAR = (float) upgradeSettings.getDouble("rateOfFireUpgradeScalar");
        ROF_QUALITY_MULT = (float) upgradeSettings.getDouble("rateOfFireQualityMult");
    }

    @Override
    public void applyUpgradeToStats(FleetMemberAPI fm, MutableShipStatsAPI stats, float hullSizeFactor, int level, float quality) {
        StatUtils.setStatPercentBonus(stats.getMissileAmmoBonus(), this.getBuffId(), level, quality, MISSILE_MAGAZINE_MULT, hullSizeFactor);

        StatUtils.setStatPercentBonus(stats.getBallisticRoFMult(), this.getBuffId(),
                StatUtils.getDiminishingReturnsTotal(level, getMaxLevel(fm.getHullSpec().getHullSize()), quality, ROF_SCALAR, ROF_QUALITY_MULT, hullSizeFactor));

        StatUtils.setStatPercentBonus(stats.getEnergyRoFMult(), this.getBuffId(),
                StatUtils.getDiminishingReturnsTotal(level, getMaxLevel(fm.getHullSpec().getHullSize()), quality, ROF_SCALAR, ROF_QUALITY_MULT, hullSizeFactor));

        StatUtils.setStatPercentBonus(stats.getMissileRoFMult(), this.getBuffId(),
                StatUtils.getDiminishingReturnsTotal(level, getMaxLevel(fm.getHullSpec().getHullSize()), quality, ROF_SCALAR, ROF_QUALITY_MULT, hullSizeFactor));

        StatUtils.setStatPercentBonus(stats.getBallisticAmmoRegenMult(), this.getBuffId(),
                StatUtils.getDiminishingReturnsTotal(level, getMaxLevel(fm.getHullSpec().getHullSize()), quality, ROF_SCALAR, ROF_QUALITY_MULT, hullSizeFactor));

        StatUtils.setStatPercentBonus(stats.getEnergyAmmoRegenMult(), this.getBuffId(),
                StatUtils.getDiminishingReturnsTotal(level, getMaxLevel(fm.getHullSpec().getHullSize()), quality, ROF_SCALAR, ROF_QUALITY_MULT, hullSizeFactor));

        StatUtils.setStatPercentBonus(stats.getMissileAmmoRegenMult(), this.getBuffId(),
                StatUtils.getDiminishingReturnsTotal(level, getMaxLevel(fm.getHullSpec().getHullSize()), quality, ROF_SCALAR, ROF_QUALITY_MULT, hullSizeFactor));
    }

    @Override
    public void modifyToolTip(TooltipMakerAPI tooltip, FleetMemberAPI fm, ExtraSystems systems, boolean expand) {
        int level = systems.getUpgrade(this.getKey());
        float quality = systems.getQuality(fm);

        if (level > 0) {
            if(expand) {
                tooltip.addPara(this.getName() + " (%s):", 5, Color.green, String.valueOf(level));

                this.addIncreaseToTooltip(tooltip,
                        "missileAmmoIncrease",
                        fm.getStats().getMissileAmmoBonus().getPercentBonus(this.getBuffId()).getValue());

                this.addIncreaseToTooltip(tooltip,
                        "firerateIncrease",
                        fm.getStats().getBallisticRoFMult().getPercentStatMod(this.getBuffId()).getValue());
            } else {
                tooltip.addPara(this.getName() + " (%s)", 5, Color.green, String.valueOf(level));
            }
        }
    }
}
