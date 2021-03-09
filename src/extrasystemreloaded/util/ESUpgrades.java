package extrasystemreloaded.util;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import extrasystemreloaded.Es_ModPlugin;
import extrasystemreloaded.util.upgrades.*;
import org.apache.log4j.Level;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ESUpgrades {
    private static final org.apache.log4j.Logger log = Global.getLogger(ESUpgrades.class);

    private static final String Es_SR_FILE_PATH = "data/config/skill_resource_ratio.csv";
    public static final float UPGRADE_COST_MINFACTOR = Global.getSettings().getFloat("upgradeCostMinFactor");
    public static final float UPGRADE_COST_MAXFACTOR = Global.getSettings().getFloat("upgradeCostMaxFactor");
    public static final float DIVIDING_RATIO = Global.getSettings().getFloat("dividingRatio");
    public static JSONArray UPGRADE_COST_MULTIPLIERS = null;
    private static final float[] CARGO_BASE_VALUE ={100,250,30,500,30,200,150};//supplies,volatiles,organics,hand_weapons,metals,rare_metals,heavy_machinery

    public enum UpgradeKey {
        ORDNANCE("Ordnance"),
        WEAPONS("WeaponProficiency"),
        LOGISTICS("Logistics"),
        MOBILITY("Mobility"),
        DURABILITY("Durability"),
        TECHNOLOGY("Technology"),
        FIGHTERS("Fighters"),
        SUBSYSTEMS("Subsystems"),
        AGGRESSION_PROTOCOL("Aggression");

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

    public static final Map<UpgradeKey, Upgrade> UPGRADES = new HashMap<>();
    public static final List<Upgrade> UPGRADES_LIST = new ArrayList<>();
    public static void addUpgrade(Upgrade upgrade) {
        UPGRADES.put(upgrade.getKey(),upgrade);
        UPGRADES_LIST.add(upgrade);
    }

    static {
        addUpgrade(new Ordnance());
        addUpgrade(new Weapons());
        addUpgrade(new Logistics());
        addUpgrade(new Mobility());
        addUpgrade(new Technology());
        addUpgrade(new Durability());
        addUpgrade(new Fighters());
        addUpgrade(new Subsystems());
        addUpgrade(new Aggression());
    }

    public static final Map<ShipAPI.HullSize, Integer> HULLSIZE_TO_MAXLEVEL = new HashMap<>();
    static {
        ESUpgrades.HULLSIZE_TO_MAXLEVEL.put(ShipAPI.HullSize.FRIGATE, 10);
        ESUpgrades.HULLSIZE_TO_MAXLEVEL.put(ShipAPI.HullSize.DESTROYER, 15);
        ESUpgrades.HULLSIZE_TO_MAXLEVEL.put(ShipAPI.HullSize.CRUISER, 20);
        ESUpgrades.HULLSIZE_TO_MAXLEVEL.put(ShipAPI.HullSize.CAPITAL_SHIP, 25);
        ESUpgrades.HULLSIZE_TO_MAXLEVEL.put(ShipAPI.HullSize.FIGHTER, 0);
        ESUpgrades.HULLSIZE_TO_MAXLEVEL.put(ShipAPI.HullSize.DEFAULT, 0);
    }

    private final Map<UpgradeKey, Integer> upgrades;

    public ESUpgrades() {
        this.upgrades = new HashMap<>();
    }

    public ESUpgrades(Map<UpgradeKey, Integer> upgrades) {
        this.upgrades = upgrades;
    }

    public int getUpgrade(Upgrade upgrade) {
        return this.getUpgrade(upgrade.getKey());
    }
    public int getUpgrade(UpgradeKey key) {
        if(this.upgrades.containsKey(key)) {
            return this.upgrades.get(key);
        }
        return 0;
    }

    public void putUpgrade(Upgrade upggrade) {
        this.putUpgrade(upggrade.getKey());
    }

    public void putUpgrade(Upgrade upggrade, int level) {
        this.putUpgrade(upggrade.getKey(), level);
    }

    public void putUpgrade(UpgradeKey key) {
        this.putUpgrade(key, getUpgrade(key) + 1);
    }

    public void putUpgrade(UpgradeKey key, int level) {
        this.upgrades.put(key, level);
    }

    public boolean hasUpgrades() {
        return !this.upgrades.isEmpty();
    }

    public static ESUpgrades generateRandomStats(FleetMemberAPI fleetMember, int fp) {
        ShipAPI.HullSize hullSize = fleetMember.getHullSpec().getHullSize();
        int maxlevel = HULLSIZE_TO_MAXLEVEL.get(hullSize);
        float arg1 = fp/300f;//300дёєжњЂе¤§
        ESUpgrades upgrades = new ESUpgrades();
        for(UpgradeKey name : UpgradeKey.values()) {
            upgrades.putUpgrade(name, (int) Math.min(maxlevel, Math.round(maxlevel*arg1*(Math.random()*0.8f+0.2f))));
        }
        return upgrades;
    }

    public static int[] getUpgradeCosts(FleetMemberAPI shipSelected, Upgrade abilitySelected, int level, int max) {
        try {
            UPGRADE_COST_MULTIPLIERS = Global.getSettings().loadCSV(Es_SR_FILE_PATH);
        } catch (IOException | JSONException e) {
            log.log(Level.ERROR, "Failed to load settings: " + e.getMessage());
        }
        int[] resourceCosts = new int[7];

        float shipQuality = Es_ModPlugin.ShipQualityData.get(shipSelected.getId());
        float hullBaseValue = shipSelected.getHullSpec().getBaseValue();

        float upgradeCostRatioByLevel = (UPGRADE_COST_MAXFACTOR - UPGRADE_COST_MINFACTOR) * level / max + UPGRADE_COST_MINFACTOR;
        float upgradeCostByHull = hullBaseValue * upgradeCostRatioByLevel;

        try {
            for (int j = 0; j < UPGRADE_COST_MULTIPLIERS.length(); j++) {
                final JSONObject entry = UPGRADE_COST_MULTIPLIERS.getJSONObject(j);
                if (abilitySelected.getKey().compareKey(entry.getString("key"))) {
                    float supplyRatio = (float) entry.getDouble("supplies");
                    resourceCosts[0] = getUpgradeCost(supplyRatio, CARGO_BASE_VALUE[0], shipQuality, upgradeCostByHull);

                    float volatileRatio = (float) entry.getDouble("volatiles");
                    resourceCosts[1] = getUpgradeCost(volatileRatio, CARGO_BASE_VALUE[1], shipQuality, upgradeCostByHull);

                    float organicsRatio = (float) entry.getDouble("organics");
                    resourceCosts[2] = getUpgradeCost(organicsRatio, CARGO_BASE_VALUE[2], shipQuality, upgradeCostByHull);

                    float heavyWeaponRatio = (float) entry.getDouble("hand_weapons");
                    resourceCosts[3] = getUpgradeCost(heavyWeaponRatio, CARGO_BASE_VALUE[3], shipQuality, upgradeCostByHull);

                    float metalsRatio = (float) entry.getDouble("metals");
                    resourceCosts[4] = getUpgradeCost(metalsRatio, CARGO_BASE_VALUE[4], shipQuality, upgradeCostByHull);

                    float rareMetalRatio = (float) entry.getDouble("rare_metals");
                    resourceCosts[5] = getUpgradeCost(rareMetalRatio, CARGO_BASE_VALUE[5], shipQuality, upgradeCostByHull);

                    float machineryRatio = (float) entry.getDouble("heavy_machinery");
                    resourceCosts[6] = getUpgradeCost(machineryRatio, CARGO_BASE_VALUE[6], shipQuality, upgradeCostByHull);
                    break;
                }
            }
        } catch (JSONException e) {
            log.log(Level.ERROR, "Failed to load settings: " + e.getMessage());
        }
        return resourceCosts;
    }

    public static int getUpgradeCost(float multiplier, float baseValue, float shipQuality, float upgradeValueByHull) {
        return Math.round(multiplier * upgradeValueByHull / baseValue * shipQuality / DIVIDING_RATIO);
    }
}
