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
import java.util.HashMap;
import java.util.Map;

public class Ordnance extends Upgrade {
    public static final int ORDNANCE_HULLMOD_MAX_LEVEL = 11;
    private final static Map<ShipAPI.HullSize, Integer> ORDNANCE_EFFECT_MULT = new HashMap<>();
    static {
        ORDNANCE_EFFECT_MULT.put(ShipAPI.HullSize.DEFAULT, 0);
        ORDNANCE_EFFECT_MULT.put(ShipAPI.HullSize.FIGHTER, 0);
        ORDNANCE_EFFECT_MULT.put(ShipAPI.HullSize.FRIGATE, 1);
        ORDNANCE_EFFECT_MULT.put(ShipAPI.HullSize.DESTROYER, 2);
        ORDNANCE_EFFECT_MULT.put(ShipAPI.HullSize.CRUISER, 3);
        ORDNANCE_EFFECT_MULT.put(ShipAPI.HullSize.CAPITAL_SHIP, 4);
    }

    @Override
    public ESUpgrades.UpgradeKey getKey() {
        return ESUpgrades.UpgradeKey.ORDNANCE;
    }

    @Override
    public String getName() {
        return Global.getSettings().getString("AbilityName", "Ordnance");
    }

    @Override
    public String getDescription() {
        return "Improve ordnance capacity";
    }

    @Override
    public int getMaxLevel() {
        return ORDNANCE_HULLMOD_MAX_LEVEL;
    }

    @Override
    public void advanceInCombat(ShipAPI ship, float amount, int level, float quality) {
    }

    @Override
    public void applyUpgradeToStats(FleetMemberAPI fm, MutableShipStatsAPI stats, float hullSizeFactor, int level, float quality) {
    }

    @Override
    public void modifyToolTip(TooltipMakerAPI tooltip, FleetMemberAPI fm, Es_ShipLevelFleetData buff) {
        ESUpgrades levels = buff.getUpgrades();

        if (levels.getUpgrade(this.getKey()) > 0) {
            tooltip.addPara(this.getName() + " (%s) [%s]:", 5, Color.green, String.valueOf(this.getLevel(levels)), "Unaffected by quality");
            tooltip.addPara("  Bonus ordnance points: %s", 0, Misc.getHighlightColor(), String.valueOf(this.getLevel(levels) * ORDNANCE_EFFECT_MULT.get(fm.getHullSpec().getHullSize())));
        }
    }
}
