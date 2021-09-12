package extrasystemreloaded.upgrades;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import extrasystemreloaded.Es_ModPlugin;
import extrasystemreloaded.upgrades.impl.*;
import org.apache.log4j.Level;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static extrasystemreloaded.Es_ModPlugin.HULLSIZE_TO_MAXLEVEL;

public class UpgradesHandler {
    private static final org.apache.log4j.Logger log = Global.getLogger(UpgradesHandler.class);

    public static final Map<String, Upgrade> UPGRADES = new HashMap<>();
    public static final List<Upgrade> UPGRADES_LIST = new ArrayList<>();
    private static final List<String> RESOURCES_LIST = new ArrayList() {{
        add("supplies");
        add("volatiles");
        add("organics");
        add("hand_weapons");
        add("metals");
        add("rare_metals");
        add("heavy_machinery");
    }};

    private static final float[] CARGO_BASE_VALUE = {100,250,30,500,30,200,150};//supplies,volatiles,organics,hand_weapons,metals,rare_metals,heavy_machinery

    public static void populateUpgrades() {
        UpgradesHandler.addUpgrade(new Weapons());
        UpgradesHandler.addUpgrade(new Logistics());
        UpgradesHandler.addUpgrade(new Mobility());
        UpgradesHandler.addUpgrade(new Technology());
        UpgradesHandler.addUpgrade(new Durability());
        UpgradesHandler.addUpgrade(new Fighters());
        UpgradesHandler.addUpgrade(new Subsystems());
        UpgradesHandler.addUpgrade(new Magazines());
    }

    public static void addUpgrade(Upgrade upgrade) {
        if(UPGRADES.containsKey(upgrade.getKey())) return;

        UPGRADES.put(upgrade.getKey(),upgrade);
        UPGRADES_LIST.add(upgrade);
        log.info(String.format("initialized upgrade [%s]", upgrade.getName()));
    }

    public static ESUpgrades generateRandomStats(FleetMemberAPI fleetMember, int fp) {
        ShipAPI.HullSize hullSize = fleetMember.getHullSpec().getHullSize();
        int maxlevel = HULLSIZE_TO_MAXLEVEL.get(hullSize);
        float arg1 = fp/300f;
        ESUpgrades upgrades = new ESUpgrades();
        for(Upgrade name : UPGRADES_LIST) {
            upgrades.putUpgrade(name, (int) Math.min(maxlevel, Math.round(maxlevel*arg1*(Math.random()*0.8f+0.2f))));
        }
        return upgrades;
    }

    public static float[] getUpgradeCosts(FleetMemberAPI shipSelected, Upgrade abilitySelected, int level, float qualityFactor) {
        int max = abilitySelected.getMaxLevel(shipSelected.getHullSpec().getHullSize());

        float[] resourceCosts = new float[7];

        float hullBaseValue = shipSelected.getHullSpec().getBaseValue();
        float adjustedHullValue =
                hullBaseValue * Es_ModPlugin.getHullBaseFactor()
                + ((1 - Es_ModPlugin.getHullBaseFactor()) * hullBaseValue * (Es_ModPlugin.getHullValueMaximum() / (hullBaseValue + Es_ModPlugin.getHullValueMaximum())));

        float upgradeCostRatioByLevel = Es_ModPlugin.getUpgradeCostMinFactor() + Es_ModPlugin.getUpgradeCostMaxFactor() * ((float) level) / ((float) max);
        float upgradeCostByHull = adjustedHullValue * upgradeCostRatioByLevel / Es_ModPlugin.getDividingRatio();

        try {
            for (int j = 0; j < Es_ModPlugin.getUpgradeCostMultipliers().length(); j++) {
                final JSONObject entry = Es_ModPlugin.getUpgradeCostMultipliers().getJSONObject(j);
                if (abilitySelected.getKey().equals(entry.getString("key"))) {
                    for(int i = 0; i < 7; i++) {
                        float ratio = (float) entry.getDouble(RESOURCES_LIST.get(i));
                        resourceCosts[i] = Math.round(ratio * upgradeCostByHull / CARGO_BASE_VALUE[i]);
                    }
                    break;
                }
            }
        } catch (JSONException e) {
            log.log(Level.ERROR, "Failed to load settings: " + e.getMessage());
        }
        return resourceCosts;
    }

    public static float getUpgradeCost(float multiplier, float baseValue, float upgradeValueByHull) {
        return 1f;
    }

    public static float getCreditCost(MarketAPI market, float amount, int resource) {
        return CARGO_BASE_VALUE[resource] * amount;
    }

    public static int getCreditCost(MarketAPI market, float[] resourceCosts, int level, int max) {
        float finalCost = 0;
        for(int i = 0; i < 7; i++) {
            finalCost += getCreditCost(market, resourceCosts[i], i);
        }

        float rel = market.getFaction().getRelToPlayer().getRel();
        float exp = (float) (1 + 3.25 * level / max);
        float base = 2f - 0.5f * rel;
        float additive = (float) (finalCost * (0.5f * Math.pow(base, exp)));

        return (int) (finalCost + additive);
    }
}
