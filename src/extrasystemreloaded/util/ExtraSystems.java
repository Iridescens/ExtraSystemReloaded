package extrasystemreloaded.util;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import extrasystemreloaded.Es_ModPlugin;
import extrasystemreloaded.augments.Augment;
import extrasystemreloaded.augments.ESAugments;
import extrasystemreloaded.upgrades.ESUpgrades;
import extrasystemreloaded.upgrades.Upgrade;
import extrasystemreloaded.upgrades.UpgradesHandler;

import java.util.Random;
import java.util.UUID;

import static extrasystemreloaded.Es_ModPlugin.useRandomQuality;
import static extrasystemreloaded.util.Utilities.RESOURCE_NAME;
import static extrasystemreloaded.util.Utilities.getFleetCargoMap;

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
        if (useRandomQuality()) {
            Random random = new Random();
            random.setSeed(UUID.nameUUIDFromBytes((Global.getSector().getSeedString() + fm.getId()).getBytes()).getMostSignificantBits());

            float sum = 0.5f + 0.05f * random.nextInt(20);
            return sum;
        }
        return Es_ModPlugin.getBaseQuality();
    }

    public boolean canUpgradeQuality(FleetMemberAPI fm) {
        return Es_ModPlugin.getMaxQuality() > getQuality(fm);
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

    public String getCanUpgradeWithImpossibleTooltip(FleetMemberAPI shipSelected) {
        return getCanUpgradeWithImpossibleTooltip(shipSelected, null, null);
    }

    public String getCanUpgradeWithImpossibleTooltip(FleetMemberAPI shipSelected, Upgrade upgrade, MarketAPI market) {
        String returnValue = null;
        if(upgrade != null) {
            if(this.getUpgrade(upgrade) >= upgrade.getMaxLevel(shipSelected.getHullSpec().getHullSize())) {
                returnValue = "This ship cannot support any more upgrades of this type.";
            }
        }
        return returnValue;
    }

    public String canUpgradeByResourceCosts(FleetMemberAPI shipSelected, MarketAPI market, Upgrade upgrade, float qualityFactor) {
        if(Es_ModPlugin.isDebugUpgradeCosts()) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        float[] resourceCosts = UpgradesHandler.getUpgradeCosts(shipSelected, upgrade, this.getUpgrade(upgrade), qualityFactor);
        for (int i = 0; i < resourceCosts.length; ++i) {
            String name = RESOURCE_NAME.get(i);

            float fleetcargo = getFleetCargoMap(shipSelected.getFleetData().getFleet(), market)[i];
            if (resourceCosts[i] > fleetcargo) {
                sb.append("\n");
                sb.append((int) (resourceCosts[i] - fleetcargo));
                sb.append(" ");
                sb.append(name);
            }
        }

        if(sb.length() == 0) {
            return null;
        }

        return sb.toString();
    }
}
