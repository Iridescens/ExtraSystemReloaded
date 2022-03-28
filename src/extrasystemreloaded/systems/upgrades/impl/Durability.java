package extrasystemreloaded.systems.upgrades.impl;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import extrasystemreloaded.util.ExtraSystems;
import extrasystemreloaded.util.StatUtils;
import extrasystemreloaded.systems.upgrades.Upgrade;
import extrasystemreloaded.util.StringUtils;
import org.json.JSONException;

import java.awt.*;

public class Durability extends Upgrade {
    private static String NAME;
    private static float HULL_MULT;
    private static float ENGINE_HEALTH_MULT;
    private static float EMP_TAKEN_MULT;

    private static float ARMOR_SCALAR;
    private static float ARMOR_QUALITY_MULT;

    @Override
    protected void loadConfig() throws JSONException {
        HULL_MULT = (float) upgradeSettings.getDouble("hullUpgradeScalar");
        ENGINE_HEALTH_MULT = (float) upgradeSettings.getDouble("engineHealthScalar");
        EMP_TAKEN_MULT = (float) upgradeSettings.getDouble("empTakenScalar");
        ARMOR_SCALAR = (float) upgradeSettings.getDouble("armorUpgradeScalar");
        ARMOR_QUALITY_MULT = (float) upgradeSettings.getDouble("armorQualityMult");
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

                this.addIncreaseWithFinalToTooltip(tooltip,
                        "hullIncrease",
                        fm.getStats().getHullBonus().getPercentBonus(this.getBuffId()).getValue(),
                        fm.getVariant().getHullSpec().getHitpoints());

                this.addIncreaseWithFinalToTooltip(tooltip,
                        "armorIncrease",
                        fm.getStats().getArmorBonus().getPercentBonus(this.getBuffId()).getValue(),
                        fm.getVariant().getHullSpec().getArmorRating());

                this.addIncreaseToTooltip(tooltip,
                        "engineHealthIncrease",
                        fm.getStats().getEngineHealthBonus().getPercentBonus(this.getBuffId()).getValue());

                this.addDecreaseToTooltip(tooltip,
                        "empDamageDecrease",
                        fm.getStats().getEmpDamageTakenMult().getMultStatMod(this.getBuffId()).getValue());
            } else {
                tooltip.addPara(this.getName() + " (%s)", 5, Color.green, String.valueOf(level));
            }
        }
    }
}
