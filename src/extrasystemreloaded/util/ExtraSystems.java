package extrasystemreloaded.util;

import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import extrasystemreloaded.Es_ModPlugin;
import extrasystemreloaded.campaign.Es_ShipLevelFleetData;
import extrasystemreloaded.util.modules.ESModules;
import extrasystemreloaded.util.modules.Module;
import extrasystemreloaded.util.upgrades.ESUpgrades;
import extrasystemreloaded.util.upgrades.Upgrade;

import static extrasystemreloaded.Es_ModPlugin.getUseShipIdForQuality;

public class ExtraSystems {
    public ExtraSystems(ESUpgrades upgrades, ESModules modules, float quality) {
        this.upgrades = upgrades != null ? upgrades : new ESUpgrades();
        this.modules = modules != null ? modules : new ESModules();
        this.qualityFactor = quality >= 0 ? quality : -1;
    }

    public ExtraSystems(FleetMemberAPI fleetMemberAPI) {
        this.upgrades = new ESUpgrades();
        this.modules = new ESModules();
        this.qualityFactor = generateQuality(fleetMemberAPI);
    }

    public boolean shouldApplyHullmod() {
        return this.upgrades.hasUpgrades()
                || this.modules.hasAnyModule();
    }

    //quality
    private float qualityFactor = 0f;

    public void putQuality(float qualityFactor) {
        this.qualityFactor = qualityFactor;
    }

    public float getQuality() {
        if(qualityFactor < 0) {
            qualityFactor = Es_ModPlugin.getBaseQuality();
        }
        return qualityFactor;
    }

    public float getQuality(FleetMemberAPI member) {
        if(qualityFactor < 0) {
            qualityFactor = generateQuality(member);
        }
        return qualityFactor;
    }

    private float generateQuality(FleetMemberAPI member) {
        if (getUseShipIdForQuality()) {
            String id = member.getId();
            char[] ids = id.toCharArray();
            float sum = 0f;
            for (int i = 0; i < ids.length; i++) {
                sum += ids[i];
                if ( i%2 == 0 ) {
                    sum *= ids[i];
                } else {
                    sum /= ids[i];
                }
            }
            while( sum > 1f ) {
                sum /= 10f;
            }
            sum += 0.5f;
            return sum;
        }
        return Es_ModPlugin.getBaseQuality();
    }

    public boolean canUpgradeQuality(FleetMemberAPI member) {
        //return true if buff doesn't exist
        if(member.getBuffManager().getBuff(Es_ShipLevelFleetData.Es_LEVEL_FUNCTION_ID) == null)
            return true;

        Es_ShipLevelFleetData buff = (Es_ShipLevelFleetData) member.getBuffManager().getBuff(Es_ShipLevelFleetData.Es_LEVEL_FUNCTION_ID);
        if(buff.getExtraSystems() == null)
            return true;

        return Es_ModPlugin.getMaxQuality() > buff.getExtraSystems().getQuality(member);
    }

    //modules
    protected ESModules modules;

    protected ESModules getESModules() {
        return modules;
    }

    public boolean hasModule(String key) {
        return modules.hasModule(key);
    }

    public boolean hasModule(Module module) {
        return hasModule(module.getKey());
    }

    public void putModule(Module module) {
        modules.putModule(module);
    }

    public void removeModule(String key) {
        modules.removeModule(key);
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


}
