package extrasystemreloaded.systems.upgrades.impl;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipSystemSpecAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import extrasystemreloaded.systems.upgrades.Upgrade;
import extrasystemreloaded.util.ExtraSystems;
import extrasystemreloaded.util.StatUtils;
import org.json.JSONException;

import java.awt.*;

public class Systems extends Upgrade {
    private static String NAME;

    private static float SYSTEM_USES_MULT;
    private static float SYSTEM_RECHARGE_MULT;
    private static float SYSTEM_RANGE_MULT;
    private static float SYSTEM_COOLDOWN_MULT;

    @Override
    protected void loadConfig() throws JSONException {
        SYSTEM_USES_MULT = (float) upgradeSettings.getDouble("systemUsesMult");
        SYSTEM_RECHARGE_MULT = (float) upgradeSettings.getDouble("systemRechargeRateMult");
        SYSTEM_RANGE_MULT = (float) upgradeSettings.getDouble("systemRangeMult");
        SYSTEM_COOLDOWN_MULT = (float) upgradeSettings.getDouble("systemCooldownMult");
    }

    private static boolean doesShipHaveSystemUses(FleetMemberAPI fm, MutableShipStatsAPI stats) {
        String shipSystemId = fm.getHullSpec().getShipSystemId();
        if(shipSystemId == null) {
            return false;
        }

        ShipSystemSpecAPI systemSpec = Global.getSettings().getShipSystemSpec(shipSystemId);
        if(systemSpec == null) {
            return false;
        }

        int usesBase = systemSpec.getMaxUses(null);
        return usesBase < Integer.MAX_VALUE;
    }

    @Override
    public void applyUpgradeToStats(FleetMemberAPI fm, MutableShipStatsAPI stats, float hullSizeFactor, int level, float quality) {
        StatUtils.setStatPercentBonus(stats.getSystemRangeBonus(), this.getBuffId(), level, quality, SYSTEM_RANGE_MULT, hullSizeFactor);

        if(doesShipHaveSystemUses(fm, stats)) {
            StatUtils.setStatPercentBonus(stats.getSystemUsesBonus(), this.getBuffId(), level, quality, SYSTEM_USES_MULT, hullSizeFactor);
            StatUtils.setStatPercentBonus(stats.getSystemRegenBonus(), this.getBuffId(), level, quality, SYSTEM_RECHARGE_MULT, hullSizeFactor);
            StatUtils.setStatPercentBonus(stats.getSystemCooldownBonus(), this.getBuffId(), level, quality, -SYSTEM_COOLDOWN_MULT, hullSizeFactor);
        } else {
            StatUtils.setStatPercentBonus(stats.getSystemCooldownBonus(), this.getBuffId(), level, quality, -SYSTEM_COOLDOWN_MULT * 1.5f, hullSizeFactor);
        }
    }

    @Override
    public void modifyToolTip(TooltipMakerAPI tooltip, FleetMemberAPI fm, ExtraSystems systems, boolean expand) {
        int level = systems.getUpgrade(this.getKey());
        float quality = systems.getQuality(fm);

        if (level > 0) {
            if(expand) {
                tooltip.addPara(this.getName() + " (%s):", 5, Color.green, String.valueOf(level));

                if(doesShipHaveSystemUses(fm, fm.getStats())) {
                    this.addIncreaseToTooltip(tooltip,
                            "systemChargesIncrease",
                            fm.getStats().getSystemUsesBonus().getPercentBonus(this.getBuffId()).getValue());

                    this.addIncreaseToTooltip(tooltip,
                            "systemRechargeIncrease",
                            fm.getStats().getSystemRegenBonus().getPercentBonus(this.getBuffId()).getValue());
                }

                this.addIncreaseToTooltip(tooltip,
                        "systemCooldownRateIncrease",
                        fm.getStats().getSystemCooldownBonus().getMultBonus(this.getBuffId()).getValue());

                this.addIncreaseToTooltip(tooltip,
                        "systemRangeIncrease",
                        fm.getStats().getSystemRangeBonus().getPercentBonus(this.getBuffId()).getValue());
            } else {
                tooltip.addPara(this.getName() + " (%s)", 5, Color.green, String.valueOf(level));
            }
        }
    }
}
