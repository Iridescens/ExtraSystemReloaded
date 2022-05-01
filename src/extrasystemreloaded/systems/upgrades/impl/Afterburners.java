package extrasystemreloaded.systems.upgrades.impl;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import extrasystemreloaded.util.ExtraSystems;
import extrasystemreloaded.util.StatUtils;
import extrasystemreloaded.systems.upgrades.Upgrade;
import lombok.Getter;

import java.awt.*;

public class Afterburners extends Upgrade {
    @Getter protected final float bandwidthUsage = 10f;
    private static float SPEED_MULT = 15f;
    private static float ACCELERATION_MAX = 30f;
    private static float TURN_RATE_MAX = 30f;
    private static float BURN_LEVEL_MAX = 20f;

    private static float DECELERATION_MAX = -30f;
    private static float FUEL_USE_MAX = 16f;

    @Override
    public void applyUpgradeToStats(FleetMemberAPI fm, MutableShipStatsAPI stats, int level, int maxLevel) {
        StatUtils.setStatPercent(stats.getMaxSpeed(), this.getBuffId(), level, SPEED_MULT, maxLevel);

        StatUtils.setStatPercent(stats.getAcceleration(), this.getBuffId(), level, ACCELERATION_MAX, maxLevel);

        StatUtils.setStatPercent(stats.getMaxTurnRate(), this.getBuffId(), level, TURN_RATE_MAX, maxLevel);
        StatUtils.setStatPercent(stats.getTurnAcceleration(), this.getBuffId(), level, TURN_RATE_MAX, maxLevel);

        StatUtils.setStatPercent(stats.getMaxBurnLevel(), this.getBuffId(), level, BURN_LEVEL_MAX, maxLevel);

        StatUtils.setStatMult(stats.getDeceleration(), this.getBuffId(), level, DECELERATION_MAX, maxLevel);
        StatUtils.setStatPercent(stats.getFuelUseMod(), this.getBuffId(), level, FUEL_USE_MAX, maxLevel);
    }

    @Override
    public void modifyToolTip(TooltipMakerAPI tooltip, FleetMemberAPI fm, ExtraSystems systems, boolean expand) {
        int level = systems.getUpgrade(this);

        if (level > 0) {
            if(expand) {
                tooltip.addPara(this.getName() + " (%s):", 5, this.getColor(), String.valueOf(level));

                this.addIncreaseWithFinalToTooltip(tooltip,
                        "speed",
                        fm.getStats().getMaxSpeed().getPercentStatMod(this.getBuffId()).getValue(),
                        fm.getStats().getMaxSpeed().getBaseValue());

                this.addIncreaseToTooltip(tooltip,
                        "acceleration",
                        fm.getStats().getAcceleration().getPercentStatMod(this.getBuffId()).getValue());

                this.addIncreaseToTooltip(tooltip,
                        "turnrate",
                        fm.getStats().getMaxTurnRate().getPercentStatMod(this.getBuffId()).getValue());

                this.addIncreaseWithFinalToTooltip(tooltip,
                        "burnLevel",
                        fm.getStats().getMaxBurnLevel().getPercentStatMod(this.getBuffId()).getValue(),
                        fm.getStats().getMaxBurnLevel().getBaseValue());

                this.addDecreaseToTooltip(tooltip,
                        "deceleration",
                        fm.getStats().getDeceleration().getMultStatMod(this.getBuffId()).getValue());

                this.addIncreaseWithFinalToTooltip(tooltip,
                        "fuelUse",
                        fm.getStats().getFuelUseMod().getPercentBonus(this.getBuffId()).getValue(),
                        fm.getHullSpec().getFuelPerLY());
            } else {
                tooltip.addPara(this.getName() + " (%s)", 5, this.getColor(), String.valueOf(level));
            }
        }
    }
}
