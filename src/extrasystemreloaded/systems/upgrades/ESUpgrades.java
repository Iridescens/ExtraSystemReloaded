package extrasystemreloaded.systems.upgrades;

import com.fs.starfarer.api.combat.ShipAPI;
import extrasystemreloaded.ESModSettings;

import java.util.HashMap;
import java.util.Map;

public class ESUpgrades {
    private final Map<String, Integer> upgrades;

    public ESUpgrades() {
        this.upgrades = new HashMap<>();
    }

    public ESUpgrades(Map<String, Integer> upgrades) {
        this.upgrades = upgrades;
    }

    public float getHullSizeFactor(ShipAPI.HullSize hullSize) {
        return ESModSettings.getHullSizeFactors().get(hullSize);
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
