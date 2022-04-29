package extrasystemreloaded.util;

import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import extrasystemreloaded.ESModSettings;
import extrasystemreloaded.Es_ModPlugin;
import extrasystemreloaded.systems.augments.Augment;
import extrasystemreloaded.systems.augments.AugmentsHandler;
import extrasystemreloaded.systems.augments.ESAugments;
import extrasystemreloaded.systems.bandwidth.Bandwidth;
import extrasystemreloaded.systems.upgrades.ESUpgrades;
import extrasystemreloaded.systems.upgrades.Upgrade;
import extrasystemreloaded.systems.upgrades.UpgradesHandler;

public class ExtraSystems {
    public static ExtraSystems getForFleetMember(FleetMemberAPI fm) {
        if(Es_ModPlugin.hasData(fm.getId())) {
            return Es_ModPlugin.getData(fm.getId());
        }
        return new ExtraSystems(fm);
    }

    public ExtraSystems(ESUpgrades upgrades, ESAugments modules, float bandwidth) {
        this.upgrades = upgrades != null ? upgrades : new ESUpgrades();
        this.modules = modules != null ? modules : new ESAugments();
        this.bandwidth = bandwidth >= 0 ? bandwidth : -1;
    }

    public ExtraSystems(FleetMemberAPI fm) {
        this.upgrades = new ESUpgrades();
        this.modules = new ESAugments();
        this.bandwidth = generateBandwidth(fm);
    }

    public boolean shouldApplyHullmod() {
        return this.upgrades.hasUpgrades()
                || this.modules.hasAnyModule();
    }

    public void save(FleetMemberAPI fm) {
        Es_ModPlugin.saveData(fm.getId(), this);
    }

    //bandwidth
    private float bandwidth = -1f;

    public void putBandwidth(float bandwidthFactor) {
        this.bandwidth = bandwidthFactor;
    }

    public float getBandwidth(FleetMemberAPI fm) {
        if(bandwidth < 0f) {
            bandwidth = generateBandwidth(fm);
        }

        float returnedBandwidth = bandwidth;

        for(Augment augment : AugmentsHandler.AUGMENT_LIST) {
            if(this.hasAugment(augment)) {
                returnedBandwidth += augment.getExtraBandwidth(fm, this);
            }
        }

        return returnedBandwidth;
    }

    public static float generateBandwidth(FleetMemberAPI fm) {
        if (ESModSettings.getBoolean(ESModSettings.RANDOM_BANDWIDTH)) {
            return Bandwidth.generate(fm.getId().hashCode()).getRandomInRange();
        }
        return ESModSettings.getFloat(ESModSettings.STARTING_BANDWIDTH);
    }

    public boolean canUpgradeBandwidth(FleetMemberAPI fm) {
        float maxBandwidth = ESModSettings.getFloat(ESModSettings.MAX_BANDWIDTH);
        for(Augment augment : AugmentsHandler.AUGMENT_LIST) {
            if(this.hasAugment(augment)) {
                maxBandwidth += augment.getExtraBandwidthPurchaseable(fm, this);
            }
        }
        return maxBandwidth > getBandwidth(fm);
    }

    public float getUsedBandwidth() {
        float usedBandwidth = 0f;
        for(Upgrade upgrade : UpgradesHandler.UPGRADES_LIST) {
            usedBandwidth += upgrade.getBandwidthUsage() * this.getUpgrade(upgrade);
        }

        return usedBandwidth;
    }

    //augments
    protected ESAugments modules;

    protected ESAugments getESModules() {
        return modules;
    }

    public boolean hasAugment(String key) {
        return modules.hasModule(key);
    }

    public boolean hasAugment(Augment augment) {
        return hasAugment(augment.getKey());
    }

    public void putAugment(Augment augment) {
        modules.putModule(augment);
    }

    public void removeAugment(Augment augment) {
        modules.removeModule(augment);
    }

    //upgrades
    private ESUpgrades upgrades;
    protected ESUpgrades getESUpgrades() {
        return upgrades;
    }

    public void putUpgrade(Upgrade upgrade) {
        upgrades.putUpgrade(upgrade);
    }

    public int getUpgrade(String key) {
        return upgrades.getUpgrade(key);
    }

    public int getUpgrade(Upgrade upgrade) {
        return getUpgrade(upgrade.getKey());
    }

    public boolean hasUpgrades() {
        return this.upgrades.hasUpgrades();
    }

    public float getHullSizeFactor(ShipAPI.HullSize hullSize) {
        return this.upgrades.getHullSizeFactor(hullSize);
    }

    public boolean isMaxLevel(FleetMemberAPI shipSelected, Upgrade upgrade) {
        return this.getUpgrade(upgrade) >= upgrade.getMaxLevel(shipSelected.getHullSpec().getHullSize());
    }
}
