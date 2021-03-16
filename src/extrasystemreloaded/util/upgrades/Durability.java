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

public class Durability extends Upgrade {
    @Override
    public ESUpgrades.UpgradeKey getKey() {
        return ESUpgrades.UpgradeKey.DURABILITY;
    }

    @Override
    public String getName() {
        return Global.getSettings().getString("AbilityName", "Durability");
    }

    @Override
    public String getDescription() {
        return "Improve hull, armor, EMP resistance, weapon health, engine health.";
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
        stats.getHullBonus().modifyPercent(Es_LEVEL_FUNCTION_ID, level * quality * 3f);
        stats.getArmorBonus().modifyPercent(Es_LEVEL_FUNCTION_ID, level * quality * 3f);
        stats.getWeaponHealthBonus().modifyPercent(Es_LEVEL_FUNCTION_ID, level * quality * 3f);
        stats.getEngineHealthBonus().modifyPercent(Es_LEVEL_FUNCTION_ID, level * quality * 3f);
        stats.getEmpDamageTakenMult().modifyPercent(Es_LEVEL_FUNCTION_ID, level * quality * -4f);
    }

    @Override
    public void modifyToolTip(TooltipMakerAPI tooltip, FleetMemberAPI fm, Es_ShipLevelFleetData buff) {
        ESUpgrades levels = buff.getUpgrades();

        if(levels.getUpgrade(this.getKey()) > 0) {
            tooltip.addPara(this.getName() + " (%s):", 5, Color.green, String.valueOf(this.getLevel(levels)));
            tooltip.addPara("  Hull durability: +%s (%s)", 0, Misc.getHighlightColor(), String.format("%.3f", fm.getStats().getHullBonus().getPercentBonus(Es_LEVEL_FUNCTION_ID).getValue()) + "%", "+" + String.format("%.0f", fm.getStats().getHullBonus().getPercentBonus(Es_LEVEL_FUNCTION_ID).getValue() * fm.getVariant().getHullSpec().getHitpoints() * 0.01f));
            tooltip.addPara("  Armor durability: +%s (%s)", 0, Misc.getHighlightColor(), String.format("%.3f", fm.getStats().getArmorBonus().getPercentBonus(Es_LEVEL_FUNCTION_ID).getValue()) + "%", "+" + String.format("%.0f", fm.getStats().getArmorBonus().getPercentBonus(Es_LEVEL_FUNCTION_ID).getValue() * fm.getVariant().getHullSpec().getArmorRating() * 0.01f));
            tooltip.addPara("  Weapon mounts durability: +%s", 0, Misc.getHighlightColor(), String.format("%.3f", fm.getStats().getWeaponHealthBonus().getPercentBonus(Es_LEVEL_FUNCTION_ID).getValue()) + "%");
            tooltip.addPara("  Engines durability: +%s", 0, Misc.getHighlightColor(), String.format("%.3f", fm.getStats().getEngineHealthBonus().getPercentBonus(Es_LEVEL_FUNCTION_ID).getValue()) + "%");
            tooltip.addPara("  EMP damage: %s", 0, Misc.getHighlightColor(), String.format("%.3f", fm.getStats().getEmpDamageTakenMult().getPercentStatMod(Es_LEVEL_FUNCTION_ID).getValue()) + "%");
        }
    }
}
