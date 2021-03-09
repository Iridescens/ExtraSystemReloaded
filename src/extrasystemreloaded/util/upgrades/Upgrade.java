package extrasystemreloaded.util.upgrades;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import extrasystemreloaded.campaign.Es_ShipLevelFleetData;
import extrasystemreloaded.util.ESUpgrades;

public abstract class Upgrade {
    public abstract ESUpgrades.UpgradeKey getKey();

    public abstract String getName();

    public abstract String getDescription();

    public abstract int getMaxLevel();

    public int getMaxLevel(ShipAPI.HullSize hullSize) {
        return getMaxLevel() != -1 ? getMaxLevel() : ESUpgrades.HULLSIZE_TO_MAXLEVEL.get(hullSize);
    }

    public int getLevel(ESUpgrades upgrades) {
        return upgrades.getUpgrade(this.getKey());
    }

    public abstract void applyUpgradeToStats(FleetMemberAPI fm, MutableShipStatsAPI stats, float hullSizeFactor, int level, float quality);
    public abstract void modifyToolTip(TooltipMakerAPI tooltip, FleetMemberAPI fm, Es_ShipLevelFleetData buff);
    public abstract void advanceInCombat(ShipAPI ship, float amount, int level, float quality);
}
