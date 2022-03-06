package extrasystemreloaded.systems.upgrades;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import extrasystemreloaded.ESModSettings;
import extrasystemreloaded.util.ExtraSystems;
import extrasystemreloaded.util.Utilities;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public abstract class Upgrade {
    protected JSONObject upgradeSettings;
    protected Map<String, Float> resourceRatios = new HashMap<>();

    public abstract String getKey();

    public abstract String getName();

    public abstract String getDescription();

    public void setConfig(JSONObject upgradeSettings) throws JSONException {
        this.upgradeSettings = upgradeSettings;
        loadConfig(upgradeSettings);

        resourceRatios.clear();
        JSONObject settingRatios = upgradeSettings.getJSONObject("resourceRatios");
        for (String resource : Utilities.RESOURCES_LIST) {
            float ratio = 0f;
            if(settingRatios.has(resource)) {
                //class cast exception indicates an improperly configured config
                ratio = ((Number) settingRatios.get(resource)).floatValue();
            }
            resourceRatios.put(resource, ratio);
        }
    }

    protected abstract void loadConfig(JSONObject upgradeSettings) throws JSONException;

    protected Map<String, Float> getResourceRatios() {
        return resourceRatios;
    }

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

    public Map<String, Integer> getResourceCosts(FleetMemberAPI shipSelected, int level) {
        int max = getMaxLevel(shipSelected.getHullSpec().getHullSize());

        float hullBaseValue = shipSelected.getHullSpec().getBaseValue();
        float baseValueFactor = ESModSettings.getFloat(ESModSettings.HULL_COST_BASE_FACTOR);
        float maxShipValue = ESModSettings.getFloat(ESModSettings.HULL_COST_DIMINISHING_MAXIMUM);
        float adjustedHullValue =
                hullBaseValue * baseValueFactor
                        + ((1 - baseValueFactor) * hullBaseValue * (maxShipValue / (hullBaseValue + maxShipValue)));

        float upgradeCostMinFactor = ESModSettings.getFloat(ESModSettings.UPGRADE_COST_MIN_FACTOR);
        float upgradeCostMaxFactor = ESModSettings.getFloat(ESModSettings.UPGRADE_COST_MAX_FACTOR);
        float upgradeCostDivisor = ESModSettings.getFloat(ESModSettings.UPGRADE_COST_DIVIDING_RATIO);
        float upgradeCostRatioByLevel = upgradeCostMinFactor + upgradeCostMaxFactor * ((float) level) / ((float) max);
        float upgradeCostByHull = adjustedHullValue * upgradeCostRatioByLevel / upgradeCostDivisor;

        Map<String, Integer> resourceCosts = new HashMap<>();
        Map<String, Float> resourceRatios = getResourceRatios();
        for (Map.Entry<String, Float> ratio : resourceRatios.entrySet()) {
            int commodityCost = Math.round(Global.getSector().getEconomy().getCommoditySpec(ratio.getKey()).getBasePrice());
            int finalCost = Math.round(ratio.getValue() * upgradeCostByHull / commodityCost);
            resourceCosts.put(ratio.getKey(), finalCost);
        }

        return resourceCosts;
    }
}
