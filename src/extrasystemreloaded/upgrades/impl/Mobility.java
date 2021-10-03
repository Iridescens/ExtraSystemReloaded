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

public class Mobility extends Upgrade {
    public static final String UPGRADE_KEY = "Mobility";

    private static String NAME = "Mobility";
    private static float SPEED_MULT;
    private static float ACCELERATION_MULT;
    private static float TURN_RATE_MULT;
    private static float BURN_LEVEL_MULT;

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
        SPEED_MULT = (float) upgradeSettings.getDouble("maxSpeedScalar");
        ACCELERATION_MULT = (float) upgradeSettings.getDouble("accelerationScalar");
        TURN_RATE_MULT = (float) upgradeSettings.getDouble("turnRateScalar");
        BURN_LEVEL_MULT = (float) upgradeSettings.getDouble("burnLevelScalar");
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
    public void modifyToolTip(TooltipMakerAPI tooltip, FleetMemberAPI fm, ExtraSystems systems, boolean expand) {
        int level = systems.getUpgrade(this);

        if (level > 0) {
            if(expand) {
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
            } else {
                tooltip.addPara(this.getName() + " (%s)", 5, Color.green, String.valueOf(level));
            }
        }
    }
}
