package extrasystemreloaded.util.upgrades;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import extrasystemreloaded.Es_ModPlugin;
import extrasystemreloaded.util.upgrades.impl.*;
import org.apache.log4j.Level;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Upgrades {
    private static final org.apache.log4j.Logger log = Global.getLogger(ESUpgrades.class);

    public static final Map<String, Upgrade> UPGRADES = new HashMap<>();
    public static final List<Upgrade> UPGRADES_LIST = new ArrayList<>();

    private static final float[] CARGO_BASE_VALUE ={100,250,30,500,30,200,150};//supplies,volatiles,organics,hand_weapons,metals,rare_metals,heavy_machinery

    static {
        Upgrades.addUpgrade(new Weapons());
        Upgrades.addUpgrade(new Logistics());
        Upgrades.addUpgrade(new Mobility());
        Upgrades.addUpgrade(new Technology());
        Upgrades.addUpgrade(new Durability());
        Upgrades.addUpgrade(new Fighters());
        Upgrades.addUpgrade(new Subsystems());
        Upgrades.addUpgrade(new Magazines());
    }

    public static void addUpgrade(Upgrade upgrade) {
        UPGRADES.put(upgrade.getKey(),upgrade);
        UPGRADES_LIST.add(upgrade);
    }

    public static ESUpgrades generateRandomStats(FleetMemberAPI fleetMember, int fp) {
        ShipAPI.HullSize hullSize = fleetMember.getHullSpec().getHullSize();
        int maxlevel = ESUpgrades.HULLSIZE_TO_MAXLEVEL.get(hullSize);
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

        float upgradeCostRatioByLevel = (Es_ModPlugin.getUpgradeCostMaxFactor() - Es_ModPlugin.getUpgradeCostMinFactor()) * level / max + Es_ModPlugin.getUpgradeCostMinFactor();
        float upgradeCostByHull = hullBaseValue * upgradeCostRatioByLevel;

        try {
            for (int j = 0; j < Es_ModPlugin.getUpgradeCostMultipliers().length(); j++) {
                final JSONObject entry = Es_ModPlugin.getUpgradeCostMultipliers().getJSONObject(j);
                if (abilitySelected.getKey().equals(entry.getString("key"))) {
                    float supplyRatio = (float) entry.getDouble("supplies");
                    resourceCosts[0] = getUpgradeCost(supplyRatio, CARGO_BASE_VALUE[0], qualityFactor, upgradeCostByHull);

                    float volatileRatio = (float) entry.getDouble("volatiles");
                    resourceCosts[1] = getUpgradeCost(volatileRatio, CARGO_BASE_VALUE[1], qualityFactor, upgradeCostByHull);

                    float organicsRatio = (float) entry.getDouble("organics");
                    resourceCosts[2] = getUpgradeCost(organicsRatio, CARGO_BASE_VALUE[2], qualityFactor, upgradeCostByHull);

                    float heavyWeaponRatio = (float) entry.getDouble("hand_weapons");
                    resourceCosts[3] = getUpgradeCost(heavyWeaponRatio, CARGO_BASE_VALUE[3], qualityFactor, upgradeCostByHull);

                    float metalsRatio = (float) entry.getDouble("metals");
                    resourceCosts[4] = getUpgradeCost(metalsRatio, CARGO_BASE_VALUE[4], qualityFactor, upgradeCostByHull);

                    float rareMetalRatio = (float) entry.getDouble("rare_metals");
                    resourceCosts[5] = getUpgradeCost(rareMetalRatio, CARGO_BASE_VALUE[5], qualityFactor, upgradeCostByHull);

                    float machineryRatio = (float) entry.getDouble("heavy_machinery");
                    resourceCosts[6] = getUpgradeCost(machineryRatio, CARGO_BASE_VALUE[6], qualityFactor, upgradeCostByHull);
                    break;
                }
            }
        } catch (JSONException e) {
            log.log(Level.ERROR, "Failed to load settings: " + e.getMessage());
        }
        return resourceCosts;
    }

    public static float getUpgradeCost(float multiplier, float baseValue, float shipQuality, float upgradeValueByHull) {
        return Math.round(multiplier * upgradeValueByHull / baseValue * shipQuality / Es_ModPlugin.getDividingRatio());
    }

    public enum UpgradeKey {
        ORDNANCE("Ordnance"),
        WEAPONS("WeaponProficiency"),
        LOGISTICS("Logistics"),
        MOBILITY("Mobility"),
        DURABILITY("Durability"),
        TECHNOLOGY("Technology"),
        FIGHTERS("Fighters"),
        SUBSYSTEMS("Subsystems"),
        MAGAZINES("Magazines");

        private final String key;

        UpgradeKey(String key) {
            this.key = key;
        }

        public String getKey() {
            return this.key;
        }

        public boolean compareKey(String otherKey) {
            return key.equals(otherKey);
        }
    }
}
