package extrasystemreloaded.util.upgrades;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import extrasystemreloaded.campaign.Es_ShipLevelFleetData;
import extrasystemreloaded.util.ESUpgrades;

import java.awt.*;

import static extrasystemreloaded.campaign.Es_ShipLevelFleetData.Es_LEVEL_FUNCTION_ID;

public class Fighters extends Upgrade {
    @Override
    public ESUpgrades.UpgradeKey getKey() {
        return ESUpgrades.UpgradeKey.FIGHTERS;
    }

    @Override
    public String getName() {
        return Global.getSettings().getString("AbilityName", "Fighters");
    }

    @Override
    public String getDescription() {
        return "Improve fighter refit time, range and replacement speed reduction rate.";
    }

    @Override
    public int getMaxLevel() {
        return -1;
    }

    @Override
    public void advanceInCombat(ShipAPI ship, float amount, int level, float quality) {
    }

    @Override
    public void applyUpgradeToStats(FleetMemberAPI fm, MutableShipStatsAPI stats, float hullSizeFactor, int level, float quality) {
        stats.getFighterRefitTimeMult().modifyPercent(Es_LEVEL_FUNCTION_ID, level * quality * -0.5f);
        stats.getFighterWingRange().modifyPercent(Es_LEVEL_FUNCTION_ID, level * quality);
        stats.getDynamic().getStat(Stats.REPLACEMENT_RATE_DECREASE_MULT).modifyPercent(Es_LEVEL_FUNCTION_ID, level * quality * -1.5f);
        stats.getDynamic().getStat(Stats.REPLACEMENT_RATE_INCREASE_MULT).modifyPercent(Es_LEVEL_FUNCTION_ID, level * quality * 1.5f);
    }

    @Override
    public void modifyToolTip(TooltipMakerAPI tooltip, FleetMemberAPI fm, Es_ShipLevelFleetData buff) {
        ESUpgrades levels = buff.getUpgrades();

        if (levels.getUpgrade(this.getKey()) > 0) {
            tooltip.addPara(this.getName() + " (%s):", 5, Color.green, String.valueOf(this.getLevel(levels)));
            tooltip.addPara("  Fighter refit time multiplier: %s", 0, Misc.getHighlightColor(),
                    String.format("%.3f", fm.getStats().getFighterRefitTimeMult().getPercentStatMod(Es_LEVEL_FUNCTION_ID).getValue()) + "%");
            tooltip.addPara("  Fighter range: +%s", 0, Misc.getHighlightColor(),
                    String.format("%.3f", fm.getStats().getFighterWingRange().getPercentBonus(Es_LEVEL_FUNCTION_ID).getValue()) + "%");
            tooltip.addPara("  Replacement rate decrease multiplier: %s", 0, Misc.getHighlightColor(),
                    String.format("%.3f", fm.getStats().getDynamic().getStat(Stats.REPLACEMENT_RATE_DECREASE_MULT).getPercentStatMod(Es_LEVEL_FUNCTION_ID).getValue()) + "%");
            tooltip.addPara("  Replacement rate increase multiplier: +%s", 0, Misc.getHighlightColor(),
                    String.format("%.3f", fm.getStats().getDynamic().getStat(Stats.REPLACEMENT_RATE_INCREASE_MULT).getPercentStatMod(Es_LEVEL_FUNCTION_ID).getValue()) + "%");
        }
    }
}
