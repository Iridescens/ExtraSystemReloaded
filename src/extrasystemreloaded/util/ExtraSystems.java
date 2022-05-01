package extrasystemreloaded.util;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import data.scripts.util.MagicSettings;
import extrasystemreloaded.ESModSettings;
import extrasystemreloaded.Es_ModPlugin;
import extrasystemreloaded.systems.augments.Augment;
import extrasystemreloaded.systems.augments.AugmentsHandler;
import extrasystemreloaded.systems.augments.ESAugments;
import extrasystemreloaded.systems.bandwidth.Bandwidth;
import extrasystemreloaded.systems.upgrades.ESUpgrades;
import extrasystemreloaded.systems.upgrades.Upgrade;
import extrasystemreloaded.systems.upgrades.UpgradesHandler;
import lombok.extern.log4j.Log4j;
import org.lazywizard.lazylib.MathUtils;

import java.util.Map;
import java.util.Random;

@Log4j
public class ExtraSystems {

    private static Random random = new Random();
    //per fleet member!
    private static float CHANCE_OF_UPGRADES = 0.4f;
    private static float CHANCE_OF_AUGMENTS = 0.1f;

    protected transient FleetMemberAPI fm;

    private static String getFaction(FleetMemberAPI fm) {
        if (fm.getHullId().contains("ziggurat")) {
            return "omega";
        }
        return fm.getFleetData().getFleet().getFaction().getId();
    }

    public static ExtraSystems getForFleetMember(FleetMemberAPI fm) {
        boolean ziggurat = fm.getHullId().contains("ziggurat");
        if(Es_ModPlugin.hasData(fm.getId())) {
            return Es_ModPlugin.getData(fm.getId());
        } else if (ziggurat) {
            ExtraSystems es = generateRandom(fm);
            es.save(fm);
            return es;
        } else {
            ExtraSystems es = new ExtraSystems(fm);
            es.save(fm);
            return es;
        }
    }

    public static ExtraSystems generateRandom(FleetMemberAPI fm) {
        if(Es_ModPlugin.hasData(fm.getId())) {
            return Es_ModPlugin.getData(fm.getId());
        }

        ExtraSystems es = new ExtraSystems(fm);

        String faction = getFaction(fm);

        long seed = fm.getId().hashCode();

        //notes: ziggurat is one-of-a-kind in that it is completely regenerated in a special dialog after its battle.
        //to make sure it still generates the same upgrades, we use its hull ID as seed.
        boolean ziggurat = fm.getHullId().contains("ziggurat");
        if (ziggurat) {
            log.info(String.format("Ship is the ziggurat (%s), so the seed is changed from [%s]", fm.getHullSpec().getBaseHullId(), seed));
            seed = fm.getHullSpec().getBaseHullId().hashCode() + Global.getSector().getSeedString().hashCode();
            log.info(String.format("New seed is %s, calculated using %s and %s", seed, fm.getHullSpec().getBaseHullId().hashCode(), Global.getSector().getSeedString().hashCode()));
        }

        es.generate(seed, faction);
        es.save(fm);

        return es;
    }

    public ExtraSystems(long bandwidthSeed) {
        this.upgrades = new ESUpgrades();
        this.modules = new ESAugments();
        this.bandwidth = Bandwidth.generate(bandwidthSeed).getRandomInRange();
    }

    public ExtraSystems(FleetMemberAPI fm) {
        this.fm = fm;
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

    public void generate(long seed, ShipVariantAPI var) {
        Map<String, Float> factionAugmentChances = MagicSettings.getFloatMap("extrasystemsreloaded", "factionAugmentChances");
        Map<String, Float> factionPerAugmentMult = MagicSettings.getFloatMap("extrasystemsreloaded", "factionPerAugmentMult");
        Map<String, Float> factionUpgradeChances = MagicSettings.getFloatMap("extrasystemsreloaded", "factionUpgradeChances");
        Map<String, Float> factionPerUpgradeMult = MagicSettings.getFloatMap("extrasystemsreloaded", "factionPerUpgradeMult");

        random.setSeed(seed);

        float augmentChance = CHANCE_OF_AUGMENTS;

        if (random.nextFloat() < augmentChance) {
            for (Augment augment : AugmentsHandler.AUGMENT_LIST) {
                if (augment.canApply(var)
                        && random.nextFloat() < augment.getSpawnChance()) {
                    log.info(String.format("Added augment %s to var", augment.getName()));
                    this.putAugment(augment);
                }
            }
        }

        float upgradeChance = CHANCE_OF_UPGRADES;

        if (random.nextFloat() < upgradeChance) {
            for (Upgrade upgrade : UpgradesHandler.UPGRADES_LIST) {
                if (upgrade.canApply(var)
                        && random.nextFloat() < upgrade.getSpawnChance()) {
                    float availableBandwidth = this.getBandwidth() - this.getUsedBandwidth();
                    if(availableBandwidth > upgrade.getBandwidthUsage()) {
                        int level = random.nextInt((int) Math.min(upgrade.getMaxLevel(var.getHullSize()), availableBandwidth / upgrade.getBandwidthUsage())) + 1;

                        log.info(String.format("Added upgrade %s (%s) to var", upgrade.getName(), level));
                        this.putUpgrade(upgrade, level);
                    }
                }
            }
        }
    }

    public void generate(long seed, String faction) {
        Map<String, Float> factionAugmentChances = MagicSettings.getFloatMap("extrasystemsreloaded", "factionAugmentChances");
        Map<String, Float> factionPerAugmentMult = MagicSettings.getFloatMap("extrasystemsreloaded", "factionPerAugmentMult");
        Map<String, Float> factionUpgradeChances = MagicSettings.getFloatMap("extrasystemsreloaded", "factionUpgradeChances");
        Map<String, Float> factionPerUpgradeMult = MagicSettings.getFloatMap("extrasystemsreloaded", "factionPerUpgradeMult");


        log.info(String.format("ExtraSystems seed: [%s]", seed));

        random.setSeed(seed);

        float augmentChance = CHANCE_OF_AUGMENTS;
        if (factionAugmentChances.containsKey(faction)) {
            augmentChance = factionAugmentChances.get(faction);
        }

        if (random.nextFloat() < augmentChance) {
            float mult = 1.0f;
            if (factionPerAugmentMult.containsKey(faction)) {
                mult = factionPerAugmentMult.get(faction);
            }

            for (Augment augment : AugmentsHandler.AUGMENT_LIST) {
                if (augment.canApply(fm)
                        && random.nextFloat() < (augment.getSpawnChance() * mult)) {
                    log.info(String.format("Added augment %s to fm", augment.getName()));
                    this.putAugment(augment);
                }
            }
        }

        float upgradeChance = CHANCE_OF_UPGRADES;
        if (factionUpgradeChances.containsKey(faction)) {
            upgradeChance = factionUpgradeChances.get(faction);
        }

        if (random.nextFloat() < upgradeChance) {
            float mult = 1.0f;
            if (factionPerUpgradeMult.containsKey(faction)) {
                mult = factionPerUpgradeMult.get(faction);
            }

            for (Upgrade upgrade : UpgradesHandler.UPGRADES_LIST) {
                if (upgrade.canApply(fm)
                        && random.nextFloat() < (upgrade.getSpawnChance() * mult)) {
                    float availableBandwidth = this.getBandwidth(fm) - this.getUsedBandwidth();
                    if(availableBandwidth > upgrade.getBandwidthUsage()) {
                        int level = random.nextInt((int) Math.min(upgrade.getMaxLevel(fm), availableBandwidth / upgrade.getBandwidthUsage())) + 1;

                        log.info(String.format("Added upgrade %s (%s) to fm", upgrade.getName(), level));
                        this.putUpgrade(upgrade, level);
                    }
                }
            }
        }
    }

    //bandwidth
    private float bandwidth = -1f;

    public void putBandwidth(float bandwidth) {
        this.bandwidth = bandwidth;
    }

    public float getBandwidth() {
        return Math.max(this.bandwidth, 0f);
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

            long seed = fm.getId().hashCode();
            if (fm.getHullId().contains("ziggurat")) {
                seed = fm.getHullSpec().getBaseHullId().hashCode() + Global.getSector().getSeedString().hashCode();
            }

            if (fm.getFleetData() != null) {
                String faction = getFaction(fm);

                log.info(String.format("Fleet Member has fleet data with faction ID [%s]", faction));

                Map<String, Float> factionBandwidthMult = MagicSettings.getFloatMap("extrasystemsreloaded", "factionBandwidthMult");

                float mult = 1.0f;
                if(factionBandwidthMult.containsKey(faction)) {
                    mult = factionBandwidthMult.get(faction);
                }

                log.info(String.format("Fleet Member has bandwidth mult [%s]", mult));

                return Bandwidth.generate(seed, mult).getRandomInRange();
            }

            return Bandwidth.generate(seed).getRandomInRange();
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

    public boolean hasAugments() {
        return modules.hasAnyModule();
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

    public void putUpgrade(Upgrade upgrade, int level) {
        upgrades.putUpgrade(upgrade, level);
    }

    public int getUpgrade(String key) {
        return upgrades.getUpgrade(key);
    }

    public int getUpgrade(Upgrade upgrade) {
        return getUpgrade(upgrade.getKey());
    }

    public boolean hasUpgrade(Upgrade upgrade){
        return getUpgrade(upgrade) > 0;
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
