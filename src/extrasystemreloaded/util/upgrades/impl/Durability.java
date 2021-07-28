package extrasystemreloaded.util.upgrades.impl;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import extrasystemreloaded.campaign.Es_ShipLevelFleetData;
import extrasystemreloaded.util.StatUtils;
import extrasystemreloaded.util.upgrades.Upgrade;

import java.awt.*;

public class Durability extends Upgrade {
    public static final String UPGRADE_KEY = "Durability";

    private static final float HULL_MULT = 2f;
    private static final float ENGINE_HEALTH_MULT = 2f;
    private static final float EMP_TAKEN_MULT = -3f;

    private static final float ARMOR_SCALAR = 6.5f;
    private static final float ARMOR_QUALITY_MULT = 0.35f;
    @Override
    public String getKey() {
        return UPGRADE_KEY;
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
    public void modifyToolTip(TooltipMakerAPI tooltip, FleetMemberAPI fm, Es_ShipLevelFleetData buff) {
        int level = buff.getExtraSystems().getUpgrade(this);

        if (level > 0) {
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
        }
    }
}
