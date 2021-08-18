package extrasystemreloaded.upgrades.impl;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import extrasystemreloaded.util.ExtraSystems;
import extrasystemreloaded.util.StatUtils;
import extrasystemreloaded.upgrades.Upgrade;

import java.awt.*;

public class Mobility extends Upgrade {
    public static final String UPGRADE_KEY = "Mobility";

    private static final float SPEED_MULT = 2f;
    private static final float ACCELERATION_MULT = 3f;
    private static final float TURN_RATE_MULT = 3f;
    private static final float BURN_LEVEL_MULT = 2f;

    @Override
    public String getKey() {
        return UPGRADE_KEY;
    }

    @Override
    public String getDescription() {
        return "Improve max speed, burn level, acceleration, deceleration, max turn rate, turn acceleration.";
    }

    @Override
    public void applyUpgradeToStats(FleetMemberAPI fm, MutableShipStatsAPI stats, float hullSizeFactor, int level, float quality) {

        StatUtils.setStatPercentBonus(stats.getMaxSpeed(), this.getBuffId(), level, quality, SPEED_MULT, hullSizeFactor);

        StatUtils.setStatPercentBonus(stats.getAcceleration(), this.getBuffId(), level, quality, ACCELERATION_MULT, hullSizeFactor);
        StatUtils.setStatPercentBonus(stats.getDeceleration(), this.getBuffId(), level, quality, ACCELERATION_MULT, hullSizeFactor);

        StatUtils.setStatPercentBonus(stats.getMaxTurnRate(), this.getBuffId(), level, quality, TURN_RATE_MULT, hullSizeFactor);
        StatUtils.setStatPercentBonus(stats.getTurnAcceleration(), this.getBuffId(), level, quality, TURN_RATE_MULT, hullSizeFactor);

        StatUtils.setStatPercentBonus(stats.getMaxBurnLevel(), this.getBuffId(), level, quality, BURN_LEVEL_MULT, hullSizeFactor);
    }

    @Override
    public void modifyToolTip(TooltipMakerAPI tooltip, FleetMemberAPI fm, ExtraSystems systems) {
        int level = systems.getUpgrade(this);

        if (level > 0) {
            tooltip.addPara(this.getName() + " (%s):", 5, Color.green, String.valueOf(level));

            StatUtils.addPercentBonusToTooltip(tooltip, "  Maximum speed: +%s (%s)",
                    fm.getStats().getMaxSpeed().getPercentStatMod(this.getBuffId()).getValue(),
                    fm.getStats().getMaxSpeed().getBaseValue());

            StatUtils.addPercentBonusToTooltip(tooltip, "  Acceleration and deceleration: +%s",
                    fm.getStats().getAcceleration().getPercentStatMod(this.getBuffId()).getValue());

            StatUtils.addPercentBonusToTooltip(tooltip, "  Maximum turn rate and turn acceleration: +%s",
                    fm.getStats().getMaxTurnRate().getPercentStatMod(this.getBuffId()).getValue());

            StatUtils.addPercentBonusToTooltip(tooltip, "  Maximum burn level: +%s (%s)",
                    fm.getStats().getMaxBurnLevel().getPercentStatMod(this.getBuffId()).getValue(),
                    fm.getStats().getMaxBurnLevel().getBaseValue());
        }
    }
}
