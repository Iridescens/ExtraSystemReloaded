package extrasystemreloaded.systems.upgrades.impl;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import extrasystemreloaded.util.ExtraSystems;
import extrasystemreloaded.util.StatUtils;
import extrasystemreloaded.systems.upgrades.Upgrade;
import org.json.JSONException;
import org.json.JSONObject;

import java.awt.*;

public class Magazines extends Upgrade {
    public static final String UPGRADE_KEY = "Magazines";

    private static String NAME;
    private static float MISSILE_MAGAZINE_MULT;

    private static float ROF_SCALAR;
    private static float ROF_QUALITY_MULT;

    @Override
    public String getKey() {
        return UPGRADE_KEY;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    protected void loadConfig(JSONObject upgradeSettings) throws JSONException {
        NAME = upgradeSettings.getString("name");
        MISSILE_MAGAZINE_MULT = (float) upgradeSettings.getDouble("missileMagazineScalar");
        ROF_SCALAR = (float) upgradeSettings.getDouble("rateOfFireUpgradeScalar");
        ROF_QUALITY_MULT = (float) upgradeSettings.getDouble("rateOfFireQualityMult");
    }

    @Override
    public String getDescription() {
        return "Increases missile ammo capacity and the rate at which all weapons fire and reload magazines.";
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

                StatUtils.addPercentBonusToTooltip(tooltip, "  Bonus missile ammunition: +%s",
                        fm.getStats().getMissileAmmoBonus().getPercentBonus(this.getBuffId()).getValue());

                StatUtils.addPercentBonusToTooltip(tooltip, "  Weapons rate of fire and reload speed: +%s",
                        fm.getStats().getBallisticRoFMult().getPercentStatMod(this.getBuffId()).getValue());
            } else {
                tooltip.addPara(this.getName() + " (%s)", 5, Color.green, String.valueOf(level));
            }
        }
    }
}
