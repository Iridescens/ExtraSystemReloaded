package extrasystemreloaded.util.upgrades;

import com.fs.starfarer.api.combat.ShipAPI;

import java.util.HashMap;
import java.util.Map;

public class ESUpgrades {
    public static final Map<ShipAPI.HullSize, Integer> HULLSIZE_TO_MAXLEVEL = new HashMap<>();
    static {
        ESUpgrades.HULLSIZE_TO_MAXLEVEL.put(ShipAPI.HullSize.FRIGATE, 10);
        ESUpgrades.HULLSIZE_TO_MAXLEVEL.put(ShipAPI.HullSize.DESTROYER, 15);
        ESUpgrades.HULLSIZE_TO_MAXLEVEL.put(ShipAPI.HullSize.CRUISER, 20);
        ESUpgrades.HULLSIZE_TO_MAXLEVEL.put(ShipAPI.HullSize.CAPITAL_SHIP, 25);
        ESUpgrades.HULLSIZE_TO_MAXLEVEL.put(ShipAPI.HullSize.FIGHTER, 0);
        ESUpgrades.HULLSIZE_TO_MAXLEVEL.put(ShipAPI.HullSize.DEFAULT, 0);
    }

    private static final Map<ShipAPI.HullSize, Float> HULLSIZE_MAGNITUDE = new HashMap<>();
    static {
        HULLSIZE_MAGNITUDE.put(ShipAPI.HullSize.FRIGATE, 1f);
        HULLSIZE_MAGNITUDE.put(ShipAPI.HullSize.DESTROYER, 0.666f);
        HULLSIZE_MAGNITUDE.put(ShipAPI.HullSize.CRUISER, 0.5f);
        HULLSIZE_MAGNITUDE.put(ShipAPI.HullSize.CAPITAL_SHIP, 0.4f);
    }

    private final Map<String, Integer> upgrades;

    public ESUpgrades() {
        this.upgrades = new HashMap<>();
    }

    public ESUpgrades(Map<String, Integer> upgrades) {
        this.upgrades = upgrades;
    }

    public float getHullSizeFactor(ShipAPI.HullSize hullSize) {
        return HULLSIZE_MAGNITUDE.get(hullSize);
    }

    public int getUpgrade(Upgrade upgrade) {
        return this.getUpgrade(upgrade.getKey());
    }
    public int getUpgrade(String key) {
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

    public void putUpgrade(String key) {
        this.putUpgrade(key, getUpgrade(key) + 1);
    }

    public void putUpgrade(String key, int level) {
        this.upgrades.put(key, level);
    }

    public boolean hasUpgrades() {
        return !this.upgrades.isEmpty();
    }
}
