package extrasystemreloaded.util.upgrades;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import extrasystemreloaded.campaign.Es_ShipLevelFleetData;

import static extrasystemreloaded.Es_ModPlugin.HULLSIZE_TO_MAXLEVEL;
import static extrasystemreloaded.campaign.Es_ShipLevelFleetData.Es_LEVEL_FUNCTION_ID;

public abstract class Upgrade {
    public abstract String getKey();

    public String getName() {
        return Global.getSettings().getString("AbilityName", getKey());
    }

    public abstract String getDescription();

    public String getBuffId() {
        return Es_LEVEL_FUNCTION_ID + getName();
    }

    public int getMaxLevel() {
        return -1;
    }

    public int getMaxLevel(ShipAPI.HullSize hullSize) {
        return getMaxLevel() != -1 ? getMaxLevel() : HULLSIZE_TO_MAXLEVEL.get(hullSize);
    }

    public int getLevel(ESUpgrades upgrades) {
        return upgrades.getUpgrade(this.getKey());
    }

    public void applyUpgradeToStats(FleetMemberAPI fm, MutableShipStatsAPI stats, float hullSizeFactor, int level, float quality) {

    }

    public void advanceInCombat(ShipAPI ship, float amount, int level, float quality, float hullSizeFactor) {

    }

    public abstract void modifyToolTip(TooltipMakerAPI tooltip, FleetMemberAPI fm, Es_ShipLevelFleetData buff);
}
