package extrasystemreloaded.upgrades.impl;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import extrasystemreloaded.util.ExtraSystems;
import extrasystemreloaded.util.StatUtils;
import extrasystemreloaded.upgrades.Upgrade;

import java.awt.*;

public class Weapons extends Upgrade {
    public static final String UPGRADE_KEY = "Weapons";

    private static final float WEAPON_HEALTH_MULT = 2f;

    private static final float RANGE_SCALAR = 1.15f;
    private static final float RANGE_QUALITY_MULT = 2f;

    private static final float DAMAGE_SCALAR = 5f;
    private static final float DAMAGE_QUALITY_MULT = 1.5f;

    private static final float ROF_SCALAR = 5f;
    private static final float ROF_QUALITY_MULT = 1.45f;



    @Override
    public String getKey() {
        return UPGRADE_KEY;
    }

    @Override
    public String getDescription() {
        return "Improve weapon range, weapon damage, rate of fire.";
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

        StatUtils.setStatPercentBonus(stats.getBallisticRoFMult(), this.getBuffId(),
                StatUtils.getDiminishingReturnsTotal(level, getMaxLevel(fm.getHullSpec().getHullSize()), quality, ROF_SCALAR, ROF_QUALITY_MULT, hullSizeFactor));

        StatUtils.setStatPercentBonus(stats.getEnergyRoFMult(), this.getBuffId(),
                StatUtils.getDiminishingReturnsTotal(level, getMaxLevel(fm.getHullSpec().getHullSize()), quality, ROF_SCALAR, ROF_QUALITY_MULT, hullSizeFactor));

        StatUtils.setStatPercentBonus(stats.getMissileRoFMult(), this.getBuffId(),
                StatUtils.getDiminishingReturnsTotal(level, getMaxLevel(fm.getHullSpec().getHullSize()), quality, ROF_SCALAR, ROF_QUALITY_MULT, hullSizeFactor));
    }

    @Override
    public void modifyToolTip(TooltipMakerAPI tooltip, FleetMemberAPI fm, ExtraSystems systems) {
        int level = systems.getUpgrade(this);

        if (level > 0) {
            tooltip.addPara(this.getName() + " (%s):", 5, Color.green, String.valueOf(level));

            StatUtils.addPercentBonusToTooltip(tooltip, "  Weapons range: +%s",
                    fm.getStats().getBallisticWeaponRangeBonus().getPercentBonus(this.getBuffId()).getValue());

            StatUtils.addPercentBonusToTooltip(tooltip, "  Weapons damage: +%s",
                    fm.getStats().getBallisticWeaponDamageMult().getPercentStatMod(this.getBuffId()).getValue());

            StatUtils.addPercentBonusToTooltip(tooltip, "  Weapons rate of fire: +%s",
                    fm.getStats().getBallisticRoFMult().getPercentStatMod(this.getBuffId()).getValue());

            StatUtils.addPercentBonusToTooltip(tooltip, "  Weapon mounts durability: +%s",
                    fm.getStats().getWeaponHealthBonus().getPercentBonus(this.getBuffId()).getValue());
        }
    }
}
