package extrasystemreloaded.util.upgrades.impl;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import extrasystemreloaded.campaign.Es_ShipLevelFleetData;
import extrasystemreloaded.util.StatUtils;
import extrasystemreloaded.util.upgrades.Upgrade;

import java.awt.*;

public class Fighters extends Upgrade {
    public static final String UPGRADE_KEY = "Fighters";

    private static final float REFIT_TIME_MULT = -0.5f;
    private static final float RANGE_MULT = 2f;
    private static final float REPLACEMENT_DECREASE_MULT = -1.5f;
    private static final float REPLACEMENT_REGENERATE_MULT = 1.5f;

    @Override
    public String getKey() {
        return UPGRADE_KEY;
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
    public void modifyToolTip(TooltipMakerAPI tooltip, FleetMemberAPI fm, Es_ShipLevelFleetData buff) {
        int level = buff.getExtraSystems().getUpgrade(this);

        if (level > 0) {
            tooltip.addPara(this.getName() + " (%s):", 5, Color.green, String.valueOf(level));

            StatUtils.addPercentBonusToTooltip(tooltip, "  Fighter refit time: %s",
                    fm.getStats().getFighterRefitTimeMult().getPercentStatMod(this.getBuffId()).getValue());
            StatUtils.addPercentBonusToTooltip(tooltip, "  Fighter range: +%s",
                    fm.getStats().getFighterWingRange().getPercentBonus(this.getBuffId()).getValue());
            StatUtils.addPercentBonusToTooltip(tooltip, "  Replacement rate decrease multiplier: %s",
                    fm.getStats().getDynamic().getStat(Stats.REPLACEMENT_RATE_DECREASE_MULT).getPercentStatMod(this.getBuffId()).getValue());
            StatUtils.addPercentBonusToTooltip(tooltip, "  Replacement rate increase multiplier: +%s",
                    fm.getStats().getDynamic().getStat(Stats.REPLACEMENT_RATE_INCREASE_MULT).getPercentStatMod(this.getBuffId()).getValue());
        }
    }
}
