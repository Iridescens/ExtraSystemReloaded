package extrasystemreloaded.util;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import extrasystemreloaded.ESModSettings;
import extrasystemreloaded.Es_ModPlugin;
import extrasystemreloaded.systems.augments.Augment;
import extrasystemreloaded.systems.augments.ESAugments;
import extrasystemreloaded.systems.upgrades.ESUpgrades;
import extrasystemreloaded.systems.upgrades.Upgrade;

import java.util.Random;
import java.util.UUID;

public class ExtraSystems {
    public static ExtraSystems getForFleetMember(FleetMemberAPI fm) {
        if(Es_ModPlugin.hasData(fm.getId())) {
            return Es_ModPlugin.getData(fm.getId());
        }
        return new ExtraSystems(fm);
    }

    public ExtraSystems(ESUpgrades upgrades, ESAugments modules, float quality) {
        this.upgrades = upgrades != null ? upgrades : new ESUpgrades();
        this.modules = modules != null ? modules : new ESAugments();
        this.qualityFactor = quality >= 0 ? quality : -1;
    }

    public ExtraSystems(FleetMemberAPI fm) {
        this.upgrades = new ESUpgrades();
        this.modules = new ESAugments();
        this.qualityFactor = generateQuality(fm);
    }

    public boolean shouldApplyHullmod() {
        return this.upgrades.hasUpgrades()
                || this.modules.hasAnyModule();
    }

    public void save(FleetMemberAPI fm) {
        Es_ModPlugin.saveData(fm.getId(), this);
    }

    //quality
    private float qualityFactor = 0f;

    public void putQuality(float qualityFactor) {
        this.qualityFactor = qualityFactor;
    }

    public float getQuality(FleetMemberAPI fm) {
        if(qualityFactor < 0) {
            qualityFactor = generateQuality(fm);
        }
        return qualityFactor;
    }

    public static float generateQuality(FleetMemberAPI fm) {
        if (ESModSettings.getBoolean(ESModSettings.RANDOM_QUALITY)) {
            Random random = new Random();
            random.setSeed(UUID.nameUUIDFromBytes((Global.getSector().getSeedString() + fm.getId()).getBytes()).getMostSignificantBits());

            float sum = 0.5f + 0.05f * random.nextInt(20);
            return sum;
        }
        return ESModSettings.getFloat(ESModSettings.STARTING_QUALITY);
    }

    public boolean canUpgradeQuality(FleetMemberAPI fm) {
        return ESModSettings.getFloat(ESModSettings.MAX_QUALITY) > getQuality(fm);
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
