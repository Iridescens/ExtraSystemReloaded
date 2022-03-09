package extrasystemreloaded.systems.upgrades.impl;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.skills.SystemsExpertise;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import extrasystemreloaded.systems.upgrades.Upgrade;
import extrasystemreloaded.util.ExtraSystems;
import extrasystemreloaded.util.StatUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.awt.*;

public class Systems extends Upgrade {
    private static String NAME;

    private static float SYSTEM_USES_MULT;
    private static float SYSTEM_RECHARGE_MULT;
    private static float SYSTEM_RANGE_MULT;
    private static float SYSTEM_COOLDOWN_MULT;

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    protected void loadConfig(JSONObject upgradeSettings) throws JSONException {
        NAME = upgradeSettings.getString("name");
        SYSTEM_USES_MULT = (float) upgradeSettings.getDouble("systemUsesMult");
        SYSTEM_RECHARGE_MULT = (float) upgradeSettings.getDouble("systemRechargeRateMult");
        SYSTEM_RANGE_MULT = (float) upgradeSettings.getDouble("systemRangeMult");
        SYSTEM_COOLDOWN_MULT = (float) upgradeSettings.getDouble("systemCooldownMult");
    }

    @Override
    public String getDescription() {
        return "Increases ship system charge capacity, recharge rate, range, and cooldown.";
    }

    @Override
    public void applyUpgradeToStats(FleetMemberAPI fm, MutableShipStatsAPI stats, float hullSizeFactor, int level, float quality) {
        StatUtils.setStatPercentBonus(stats.getSystemUsesBonus(), this.getBuffId(), level, quality, SYSTEM_USES_MULT, hullSizeFactor);
        StatUtils.setStatPercentBonus(stats.getSystemRegenBonus(), this.getBuffId(), level, quality, SYSTEM_RECHARGE_MULT, hullSizeFactor);
        StatUtils.setStatPercentBonus(stats.getSystemRangeBonus(), this.getBuffId(), level, quality, SYSTEM_RANGE_MULT, hullSizeFactor);
        StatUtils.setStatPercentBonus(stats.getSystemCooldownBonus(), this.getBuffId(), level, quality, -SYSTEM_COOLDOWN_MULT, hullSizeFactor);
    }

    @Override
    public void modifyToolTip(TooltipMakerAPI tooltip, FleetMemberAPI fm, ExtraSystems systems, boolean expand) {
        int level = systems.getUpgrade(this.getKey());
        float quality = systems.getQuality(fm);

        if (level > 0) {
            if(expand) {
                tooltip.addPara(this.getName() + " (%s):", 5, Color.green, String.valueOf(level));

                StatUtils.addPercentBonusToTooltip(tooltip, "  Bonus system charges: +%s",
                        fm.getStats().getSystemUsesBonus().getPercentBonus(this.getBuffId()).getValue());

                StatUtils.addPercentBonusToTooltip(tooltip, "  System reload speed: +%s",
                        fm.getStats().getSystemRegenBonus().getPercentBonus(this.getBuffId()).getValue());

                StatUtils.addPercentBonusToTooltip(tooltip, "  System range: +%s",
                        fm.getStats().getSystemRangeBonus().getPercentBonus(this.getBuffId()).getValue());

                StatUtils.addPercentBonusToTooltip(tooltip, "  System cooldown speed: +%s",
                        fm.getStats().getSystemCooldownBonus().getPercentBonus(this.getBuffId()).getValue());
            } else {
                tooltip.addPara(this.getName() + " (%s)", 5, Color.green, String.valueOf(level));
            }
        }
    }
}
