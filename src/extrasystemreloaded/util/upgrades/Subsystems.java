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

public class Subsystems extends Upgrade {
    @Override
    public ESUpgrades.UpgradeKey getKey() {
        return ESUpgrades.UpgradeKey.SUBSYSTEMS;
    }

    @Override
    public String getName() {
        return Global.getSettings().getString("AbilityName", "Subsystems");
    }

    @Override
    public String getDescription() {
        return "Improve peak performance time.";
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
        stats.getPeakCRDuration().modifyPercent(Es_LEVEL_FUNCTION_ID, level * quality * 2f);
        stats.getCRLossPerSecondPercent().modifyPercent(Es_LEVEL_FUNCTION_ID, level * quality * -2f);
    }

    @Override
    public void modifyToolTip(TooltipMakerAPI tooltip, FleetMemberAPI fm, Es_ShipLevelFleetData buff) {
        ESUpgrades levels = buff.getUpgrades();

        if (levels.getUpgrade(this.getKey()) > 0) {
            tooltip.addPara(this.getName() + " (%s):", 5, Color.green, String.valueOf(this.getLevel(levels)));
            tooltip.addPara("  Peak performance time: %s", 0, Misc.getHighlightColor(), bonusPercentStringWithOriginal(fm.getStats().getPeakCRDuration().getPercentBonus(Es_LEVEL_FUNCTION_ID).getValue(), fm.getVariant().getHullSpec().getNoCRLossTime()));
            tooltip.addPara("  CR degradation after peak performance time: %s", 0, Misc.getHighlightColor(), bonusPercentStringWithOriginal(fm.getStats().getCRLossPerSecondPercent().getPercentBonus(Es_LEVEL_FUNCTION_ID).getValue(), fm.getVariant().getHullSpec().getCRLossPerSecond()));
        }
    }

    public static String bonusPercentString(float bonusPercent) {
        return String.format("%.3f%%", bonusPercent);
    }

    public static String bonusPercentStringWithOriginal(float bonusPercent, float originalValue) {
        return String.format("%s (%s)", bonusPercentString(bonusPercent), String.format("%.2f", bonusPercent * 0.01f * originalValue));
    }
}
