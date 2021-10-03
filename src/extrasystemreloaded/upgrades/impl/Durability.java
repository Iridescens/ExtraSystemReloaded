package extrasystemreloaded.upgrades.impl;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import extrasystemreloaded.util.ExtraSystems;
import extrasystemreloaded.util.StatUtils;
import extrasystemreloaded.upgrades.Upgrade;
import org.json.JSONException;
import org.json.JSONObject;

import java.awt.*;

public class Durability extends Upgrade {
    public static final String UPGRADE_KEY = "Durability";

    private static String NAME = "Durability";
    private static float HULL_MULT;
    private static float ENGINE_HEALTH_MULT;
    private static float EMP_TAKEN_MULT;

    private static float ARMOR_SCALAR;
    private static float ARMOR_QUALITY_MULT;

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getKey() {
        return UPGRADE_KEY;
    }

    @Override
    public void loadConfig(JSONObject upgradeSettings) throws JSONException {
        NAME = upgradeSettings.getString("name");

        HULL_MULT = (float) upgradeSettings.getDouble("hullUpgradeScalar");
        ENGINE_HEALTH_MULT = (float) upgradeSettings.getDouble("engineHealthScalar");
        EMP_TAKEN_MULT = (float) upgradeSettings.getDouble("empTakenScalar");
        ARMOR_SCALAR = (float) upgradeSettings.getDouble("armorUpgradeScalar");
        ARMOR_QUALITY_MULT = (float) upgradeSettings.getDouble("armorQualityMult");
    }

    @Override
    public String getDescription() {
        return "Improve hull, armor, EMP resistance, weapon health, engine health.";
    }

    @Override
    public void applyUpgradeToStats(FleetMemberAPI fm, MutableShipStatsAPI stats, float hullSizeFactor, int level, float quality) {

        StatUtils.setStatPercentBonus(stats.getHullBonus(), this.getBuffId(), level, quality, HULL_MULT, hullSizeFactor);
        StatUtils.setStatPercentBonus(stats.getEngineHealthBonus(), this.getBuffId(), level, quality, ENGINE_HEALTH_MULT, hullSizeFactor);
        StatUtils.setStatPercentBonus(stats.getEmpDamageTakenMult(), this.getBuffId(), level, quality, EMP_TAKEN_MULT, hullSizeFactor);

        StatUtils.setStatPercentBonus(stats.getArmorBonus(), this.getBuffId(),
                StatUtils.getDiminishingReturnsTotal(level, getMaxLevel(fm.getHullSpec().getHullSize()), quality, ARMOR_SCALAR, ARMOR_QUALITY_MULT, hullSizeFactor)
        );
    }

    @Override
    public void modifyToolTip(TooltipMakerAPI tooltip, FleetMemberAPI fm, ExtraSystems systems, boolean expand) {
        int level = systems.getUpgrade(this);

        if (level > 0) {
            if(expand) {
                tooltip.addPara(this.getName() + " (%s):", 5, Color.green, String.valueOf(level));

                StatUtils.addPercentBonusToTooltip(tooltip, "  Hull durability: +%s (%s)",
                        fm.getStats().getHullBonus().getPercentBonus(this.getBuffId()).getValue(),
                        fm.getVariant().getHullSpec().getHitpoints());

                StatUtils.addPercentBonusToTooltip(tooltip, "  Armor durability: +%s (%s)",
                        fm.getStats().getArmorBonus().getPercentBonus(this.getBuffId()).getValue(),
                        fm.getVariant().getHullSpec().getArmorRating());


                StatUtils.addPercentBonusToTooltip(tooltip, "  Engines durability: +%s",
                        fm.getStats().getEngineHealthBonus().getPercentBonus(this.getBuffId()).getValue());

                StatUtils.addPercentBonusToTooltip(tooltip, "  EMP damage taken: %s",
                        fm.getStats().getEmpDamageTakenMult().getPercentStatMod(this.getBuffId()).getValue());
            } else {
                tooltip.addPara(this.getName() + " (%s)", 5, Color.green, String.valueOf(level));
            }
        }
    }
}
