package extrasystemreloaded.util.upgrades;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import extrasystemreloaded.campaign.Es_ShipLevelFleetData;
import extrasystemreloaded.util.ESUpgrades;

import java.awt.*;

import static extrasystemreloaded.campaign.Es_ShipLevelFleetData.Es_LEVEL_FUNCTION_ID;

public class Mobility extends Upgrade {
    @Override
    public ESUpgrades.UpgradeKey getKey() {
        return ESUpgrades.UpgradeKey.MOBILITY;
    }

    @Override
    public String getName() {
        return Global.getSettings().getString("AbilityName", "Mobility");
    }

    @Override
    public String getDescription() {
        return "Improve max speed, burn level, acceleration, deceleration, max turn rate, turn acceleration.";
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
        stats.getMaxSpeed().modifyPercent(Es_LEVEL_FUNCTION_ID, level * quality * 2f);
        stats.getAcceleration().modifyPercent(Es_LEVEL_FUNCTION_ID, level * quality * 3f);
        stats.getDeceleration().modifyPercent(Es_LEVEL_FUNCTION_ID, level * quality * 3f);

        stats.getMaxTurnRate().modifyPercent(Es_LEVEL_FUNCTION_ID, level * quality * 3f);
        stats.getTurnAcceleration().modifyPercent(Es_LEVEL_FUNCTION_ID, level * quality * 3f);

        stats.getMaxBurnLevel().modifyPercent(Es_LEVEL_FUNCTION_ID, level * quality * 2f);
    }

    @Override
    public void modifyToolTip(TooltipMakerAPI tooltip, FleetMemberAPI fm, Es_ShipLevelFleetData buff) {
        ESUpgrades levels = buff.getUpgrades();

        if (levels.getUpgrade(this.getKey()) > 0) {
            tooltip.addPara(this.getName() + " (%s):", 5, Color.green, String.valueOf(this.getLevel(levels)));
            tooltip.addPara("  Maximum speed: +%s (%s)", 0, Misc.getHighlightColor(), String.format("%.3f", fm.getStats().getMaxSpeed().getPercentStatMod(Es_LEVEL_FUNCTION_ID).getValue()) + "%", "+" + String.format("%.0f", fm.getStats().getMaxSpeed().getPercentStatMod(Es_LEVEL_FUNCTION_ID).getValue() * fm.getStats().getMaxSpeed().getBaseValue() * 0.01f));
            tooltip.addPara("  Acceleration and deceleration: +%s", 0, Misc.getHighlightColor(), String.format("%.3f", fm.getStats().getAcceleration().getPercentStatMod(Es_LEVEL_FUNCTION_ID).getValue()) + "%");
            tooltip.addPara("  Maximum turn rate and turn acceleration: +%s", 0, Misc.getHighlightColor(), String.format("%.3f", fm.getStats().getMaxTurnRate().getPercentStatMod(Es_LEVEL_FUNCTION_ID).getValue()) + "%");
            tooltip.addPara("  Maximum burn level: +%s (%s)", 0, Misc.getHighlightColor(), String.format("%.3f", fm.getStats().getMaxBurnLevel().getPercentStatMod(Es_LEVEL_FUNCTION_ID).getValue()) + "%", "+" + String.format("%.0f", fm.getStats().getMaxBurnLevel().getPercentStatMod(Es_LEVEL_FUNCTION_ID).getValue() * fm.getStats().getMaxBurnLevel().getBaseValue() * 0.01f));
        }
    }
}
