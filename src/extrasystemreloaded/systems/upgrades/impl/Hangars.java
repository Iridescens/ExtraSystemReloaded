package extrasystemreloaded.systems.upgrades.impl;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import extrasystemreloaded.util.ExtraSystems;
import extrasystemreloaded.util.StatUtils;
import extrasystemreloaded.systems.upgrades.Upgrade;
import org.json.JSONException;

import java.awt.*;

public class Hangars extends Upgrade {
    private static String NAME;
    private static float REFIT_TIME_MULT;
    private static float RANGE_MULT;
    private static float REPLACEMENT_DECREASE_MULT;
    private static float REPLACEMENT_REGENERATE_MULT;

    @Override
    protected void loadConfig() throws JSONException {
        REFIT_TIME_MULT = (float) upgradeSettings.getDouble("refitTimeScalar");
        RANGE_MULT = (float) upgradeSettings.getDouble("rangeScalar");
        REPLACEMENT_DECREASE_MULT = (float) upgradeSettings.getDouble("replacementDecreaseScalar");
        REPLACEMENT_REGENERATE_MULT = (float) upgradeSettings.getDouble("replacementIncreaseScalar");
    }

    @Override
    public void applyUpgradeToStats(FleetMemberAPI fm, MutableShipStatsAPI stats, float hullSizeFactor, int level, float quality) {

        StatUtils.setStatMultBonus(stats.getFighterRefitTimeMult(), this.getBuffId(), level, quality, REFIT_TIME_MULT, hullSizeFactor);
        StatUtils.setStatPercentBonus(stats.getFighterWingRange(), this.getBuffId(), level, quality, RANGE_MULT, hullSizeFactor);
        StatUtils.setStatMultBonus(stats.getDynamic().getStat(Stats.REPLACEMENT_RATE_DECREASE_MULT), this.getBuffId(), level, quality, REPLACEMENT_DECREASE_MULT, hullSizeFactor);
        StatUtils.setStatPercentBonus(stats.getDynamic().getStat(Stats.REPLACEMENT_RATE_INCREASE_MULT), this.getBuffId(), level, quality, REPLACEMENT_REGENERATE_MULT, hullSizeFactor);
    }

    @Override
    public void modifyToolTip(TooltipMakerAPI tooltip, FleetMemberAPI fm, ExtraSystems systems, boolean expand) {
        int level = systems.getUpgrade(this);

        if (level > 0) {
            if(expand) {
                tooltip.addPara(this.getName() + " (%s):", 5, Color.green, String.valueOf(level));

                this.addDecreaseToTooltip(tooltip,
                        "refitTimeDecrease",
                        fm.getStats().getFighterRefitTimeMult().getMultStatMod(this.getBuffId()).getValue());

                this.addDecreaseToTooltip(tooltip,
                        "replacementRateDecrease",
                        fm.getStats().getDynamic().getStat(Stats.REPLACEMENT_RATE_DECREASE_MULT).getMultStatMod(this.getBuffId()).getValue());

                this.addIncreaseToTooltip(tooltip,
                        "replacementRateIncrease",
                        fm.getStats().getDynamic().getStat(Stats.REPLACEMENT_RATE_INCREASE_MULT).getPercentStatMod(this.getBuffId()).getValue());

                this.addIncreaseToTooltip(tooltip,
                        "fighterRangeIncrease",
                        fm.getStats().getFighterWingRange().getPercentBonus(this.getBuffId()).getValue());
            } else {
                tooltip.addPara(this.getName() + " (%s)", 5, Color.green, String.valueOf(level));
            }
        }
    }
}
