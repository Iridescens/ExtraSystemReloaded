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

public class Logistics extends Upgrade {
    @Override
    public ESUpgrades.UpgradeKey getKey() {
        return ESUpgrades.UpgradeKey.LOGISTICS;
    }

    @Override
    public String getName() {
        return Global.getSettings().getString("AbilityName", "Logistics");
    }

    @Override
    public String getDescription() {
        return "Improve CR per deployment, weapon ammo, crew, ship repair rate, CR recovery, fuel and supply use.";
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

        stats.getCRPerDeploymentPercent().modifyPercent(Es_LEVEL_FUNCTION_ID, level * quality * -1.5f);

        stats.getBallisticAmmoBonus().modifyPercent(Es_LEVEL_FUNCTION_ID, level * quality * 2f);
        stats.getEnergyAmmoBonus().modifyPercent(Es_LEVEL_FUNCTION_ID, level * quality * 2f);
        stats.getMissileAmmoBonus().modifyPercent(Es_LEVEL_FUNCTION_ID, level * quality * 2f);

        stats.getMinCrewMod().modifyPercent(Es_LEVEL_FUNCTION_ID, level * quality * -2f);
        stats.getMaxCrewMod().modifyPercent(Es_LEVEL_FUNCTION_ID, level * quality * 2f);

        stats.getRepairRatePercentPerDay().modifyPercent(Es_LEVEL_FUNCTION_ID, level * quality * 2.5f);
        stats.getBaseCRRecoveryRatePercentPerDay().modifyPercent(Es_LEVEL_FUNCTION_ID, level * quality * 2.5f);

        stats.getFuelUseMod().modifyPercent(Es_LEVEL_FUNCTION_ID, level * quality * -2.5f);

        stats.getSuppliesPerMonth().modifyPercent(Es_LEVEL_FUNCTION_ID, level * quality * -2f);
        stats.getSuppliesToRecover().modifyPercent(Es_LEVEL_FUNCTION_ID, level * quality * -2f);
    }

    @Override
    public void modifyToolTip(TooltipMakerAPI tooltip, FleetMemberAPI fm, Es_ShipLevelFleetData buff) {
        ESUpgrades levels = buff.getUpgrades();

        if (levels.getUpgrade(this.getKey()) > 0) {
            tooltip.addPara(this.getName() + " (%s):", 5, Color.green, String.valueOf(this.getLevel(levels)));
            tooltip.addPara("  CR per deployment: %s (%s)", 0, Misc.getHighlightColor(), String.format("%.3f", fm.getStats().getCRPerDeploymentPercent().getPercentBonus(Es_LEVEL_FUNCTION_ID).getValue()) + "%", String.format("%.2f", fm.getStats().getCRPerDeploymentPercent().getPercentBonus(Es_LEVEL_FUNCTION_ID).getValue() * fm.getVariant().getHullSpec().getCRToDeploy() * 0.01f));
            tooltip.addPara("  Bonus ammunition: +%s", 0, Misc.getHighlightColor(), String.format("%.3f", fm.getStats().getBallisticAmmoBonus().getPercentBonus(Es_LEVEL_FUNCTION_ID).getValue()) + "%");
            tooltip.addPara("  Less required and more maximum crew: %s (%s,%s)", 0, Misc.getHighlightColor(), String.format("%.3f", fm.getStats().getMaxCrewMod().getPercentBonus(Es_LEVEL_FUNCTION_ID).getValue()) + "%", String.format("%.0f", fm.getStats().getMinCrewMod().getPercentBonus(Es_LEVEL_FUNCTION_ID).getValue() * fm.getVariant().getHullSpec().getMinCrew() * 0.01f), "+" + String.format("%.0f", fm.getStats().getMaxCrewMod().getPercentBonus(Es_LEVEL_FUNCTION_ID).getValue() * fm.getVariant().getHullSpec().getMaxCrew() * 0.01f));
            tooltip.addPara("  Repairs and recovery rates: +%s (%s)", 0, Misc.getHighlightColor(), String.format("%.3f", fm.getStats().getBaseCRRecoveryRatePercentPerDay().getPercentStatMod(Es_LEVEL_FUNCTION_ID).getValue()) + "%", "+" + String.format("%.2f", fm.getStats().getBaseCRRecoveryRatePercentPerDay().getPercentStatMod(Es_LEVEL_FUNCTION_ID).getValue() * fm.getStats().getBaseCRRecoveryRatePercentPerDay().getBaseValue() * 0.01f));
            tooltip.addPara("  Fuel consumption: %s (%s)", 0, Misc.getHighlightColor(), String.format("%.3f", fm.getStats().getFuelUseMod().getPercentBonus(Es_LEVEL_FUNCTION_ID).getValue()) + "%", String.format("%.2f", fm.getStats().getFuelUseMod().getPercentBonus(Es_LEVEL_FUNCTION_ID).getValue() * fm.getVariant().getHullSpec().getFuelPerLY() * 0.01f));
            tooltip.addPara("  Overall supplies consumption rates: %s (%s)", 0, Misc.getHighlightColor(), String.format("%.3f", fm.getStats().getSuppliesPerMonth().getPercentStatMod(Es_LEVEL_FUNCTION_ID).getValue()) + "%", String.format("%.2f", fm.getStats().getSuppliesPerMonth().getPercentStatMod(Es_LEVEL_FUNCTION_ID).getValue() * fm.getStats().getSuppliesPerMonth().getBaseValue() * 0.01f));
        }
    }
}
