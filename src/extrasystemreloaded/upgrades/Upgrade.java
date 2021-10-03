package extrasystemreloaded.upgrades;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import extrasystemreloaded.ESModSettings;
import extrasystemreloaded.util.ExtraSystems;
import org.json.JSONException;
import org.json.JSONObject;

public abstract class Upgrade {
    public abstract String getKey();

    public abstract String getName();

    public abstract String getDescription();

    public abstract void loadConfig(JSONObject upgradeSettings) throws JSONException;

    public String getBuffId() {
        return "ESR_" + getName();
    }

    private int getMaxLevel() {
        return -1;
    }

    public int getMaxLevel(ShipAPI.HullSize hullSize) {
        return getMaxLevel() != -1 ? getMaxLevel() : ESModSettings.getHullSizeToMaxLevel().get(hullSize);
    }

    public int getLevel(ESUpgrades upgrades) {
        return upgrades.getUpgrade(this.getKey());
    }

    public void applyUpgradeToStats(FleetMemberAPI fm, MutableShipStatsAPI stats, float hullSizeFactor, int level, float quality) {

    }

    public void advanceInCombat(ShipAPI ship, float amount, int level, float quality, float hullSizeFactor) {

    }

    public abstract void modifyToolTip(TooltipMakerAPI tooltip, FleetMemberAPI fm, ExtraSystems systems, boolean expand);
}
