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

public class Weapons extends Upgrade {
    @Override
    public ESUpgrades.UpgradeKey getKey() {
        return ESUpgrades.UpgradeKey.WEAPONS;
    }

    @Override
    public String getName() {
        return Global.getSettings().getString("AbilityName", "WeaponProficiency");
    }

    @Override
    public String getDescription() {
        return "Improve weapon range, weapon damage, rate of fire.";
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
        stats.getBallisticWeaponRangeBonus().modifyPercent(Es_LEVEL_FUNCTION_ID, level * quality * 1.5f);
        stats.getEnergyWeaponRangeBonus().modifyPercent(Es_LEVEL_FUNCTION_ID, level * quality * 1.5f);
        stats.getMissileWeaponRangeBonus().modifyPercent(Es_LEVEL_FUNCTION_ID, level * quality * 1.5f);

        stats.getBallisticWeaponDamageMult().modifyPercent(Es_LEVEL_FUNCTION_ID, level * quality * 1.5f);
        stats.getEnergyWeaponDamageMult().modifyPercent(Es_LEVEL_FUNCTION_ID, level * quality * 1.5f);
        stats.getMissileWeaponDamageMult().modifyPercent(Es_LEVEL_FUNCTION_ID, level * quality * 1.5f);

        stats.getBallisticRoFMult().modifyPercent(Es_LEVEL_FUNCTION_ID, level * quality * 1.5f);
        stats.getEnergyRoFMult().modifyPercent(Es_LEVEL_FUNCTION_ID, level * quality * 1.5f);
        stats.getMissileRoFMult().modifyPercent(Es_LEVEL_FUNCTION_ID, level * quality * 1.5f);
    }

    @Override
    public void modifyToolTip(TooltipMakerAPI tooltip, FleetMemberAPI fm, Es_ShipLevelFleetData buff) {
        ESUpgrades levels = buff.getUpgrades();

        if(levels.getUpgrade(this.getKey()) > 0) {
            tooltip.addPara(this.getName() + " (%s):", 5, Color.green, String.valueOf(levels.getUpgrade(this.getKey())));
            tooltip.addPara("  Weapons range: +%s", 0, Misc.getHighlightColor(), String.format("%.3f", fm.getStats().getBallisticWeaponRangeBonus().getPercentBonus(Es_LEVEL_FUNCTION_ID).getValue()) + "%");
            tooltip.addPara("  Weapons damage: +%s", 0, Misc.getHighlightColor(), String.format("%.3f", fm.getStats().getBallisticWeaponDamageMult().getPercentStatMod(Es_LEVEL_FUNCTION_ID).getValue()) + "%");
            tooltip.addPara("  Weapons rate of fire: +%s", 0, Misc.getHighlightColor(), String.format("%.3f", fm.getStats().getBallisticRoFMult().getPercentStatMod(Es_LEVEL_FUNCTION_ID).getValue()) + "%");
        }
    }
}
