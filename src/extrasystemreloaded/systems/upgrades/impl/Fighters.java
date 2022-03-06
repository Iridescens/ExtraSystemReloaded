package extrasystemreloaded.systems.upgrades.impl;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import extrasystemreloaded.util.ExtraSystems;
import extrasystemreloaded.util.StatUtils;
import extrasystemreloaded.systems.upgrades.Upgrade;
import org.json.JSONException;
import org.json.JSONObject;

import java.awt.*;

public class Fighters extends Upgrade {
    public static final String UPGRADE_KEY = "Fighters";

    private static String NAME = "Fighters";
    private static float REFIT_TIME_MULT;
    private static float RANGE_MULT;
    private static float REPLACEMENT_DECREASE_MULT;
    private static float REPLACEMENT_REGENERATE_MULT;

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
        REFIT_TIME_MULT = (float) upgradeSettings.getDouble("refitTimeScalar");
        RANGE_MULT = (float) upgradeSettings.getDouble("rangeScalar");
        REPLACEMENT_DECREASE_MULT = (float) upgradeSettings.getDouble("replacementDecreaseScalar");
        REPLACEMENT_REGENERATE_MULT = (float) upgradeSettings.getDouble("replacementIncreaseScalar");
    }

    @Override
    public String getDescription() {
        return "Improve fighter refit time, range and replacement speed reduction rate.";
    }

    @Override
    public void applyUpgradeToStats(FleetMemberAPI fm, MutableShipStatsAPI stats, float hullSizeFactor, int level, float quality) {

        StatUtils.setStatPercentBonus(stats.getFighterRefitTimeMult(), this.getBuffId(), level, quality, REFIT_TIME_MULT, hullSizeFactor);
        StatUtils.setStatPercentBonus(stats.getFighterWingRange(), this.getBuffId(), level, quality, RANGE_MULT, hullSizeFactor);
        StatUtils.setStatPercentBonus(stats.getDynamic().getStat(Stats.REPLACEMENT_RATE_DECREASE_MULT), this.getBuffId(), level, quality, REPLACEMENT_DECREASE_MULT, hullSizeFactor);
        StatUtils.setStatPercentBonus(stats.getDynamic().getStat(Stats.REPLACEMENT_RATE_INCREASE_MULT), this.getBuffId(), level, quality, REPLACEMENT_REGENERATE_MULT, hullSizeFactor);
    }

    @Override
    public void modifyToolTip(TooltipMakerAPI tooltip, FleetMemberAPI fm, ExtraSystems systems, boolean expand) {
        int level = systems.getUpgrade(this);

        if (level > 0) {
            if(expand) {
                tooltip.addPara(this.getName() + " (%s):", 5, Color.green, String.valueOf(level));

                StatUtils.addMultBonusToTooltip(tooltip, "  Fighter refit time: %s",
                        fm.getStats().getFighterRefitTimeMult().getMultStatMod(this.getBuffId()).getValue());
                StatUtils.addPercentBonusToTooltip(tooltip, "  Fighter range: +%s",
                        fm.getStats().getFighterWingRange().getPercentBonus(this.getBuffId()).getValue());
                StatUtils.addMultBonusToTooltip(tooltip, "  Replacement rate decrease multiplier: %s",
                        fm.getStats().getDynamic().getStat(Stats.REPLACEMENT_RATE_DECREASE_MULT).getMultStatMod(this.getBuffId()).getValue());
                StatUtils.addPercentBonusToTooltip(tooltip, "  Replacement rate increase multiplier: +%s",
                        fm.getStats().getDynamic().getStat(Stats.REPLACEMENT_RATE_INCREASE_MULT).getPercentStatMod(this.getBuffId()).getValue());
            } else {
                tooltip.addPara(this.getName() + " (%s)", 5, Color.green, String.valueOf(level));
            }
        }
    }
}
