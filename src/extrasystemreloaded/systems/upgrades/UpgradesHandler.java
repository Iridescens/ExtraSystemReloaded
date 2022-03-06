package extrasystemreloaded.systems.upgrades;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import extrasystemreloaded.ESModSettings;
import extrasystemreloaded.systems.upgrades.impl.*;
import extrasystemreloaded.systems.upgrades.methods.CreditsMethod;
import extrasystemreloaded.systems.upgrades.methods.ResourcesMethod;
import extrasystemreloaded.systems.upgrades.methods.UpgradeMethod;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.*;

public class UpgradesHandler {
    private static final org.apache.log4j.Logger log = Global.getLogger(UpgradesHandler.class);

    private static final String UPGRADE_SETTINGS_KEY = "upgradeSettings";
    public static final Map<String, Upgrade> UPGRADES = new HashMap<>();
    public static final List<Upgrade> UPGRADES_LIST = new ArrayList<>();

    public static List<UpgradeMethod> UPGRADE_METHODS = new ArrayList<UpgradeMethod>() {{
        add(new CreditsMethod());
        add(new ResourcesMethod());
    }};

    public static void addUpgradeMethod(UpgradeMethod method) {
        UPGRADE_METHODS.add(method);
    }

    public static void populateUpgrades() {
        UpgradesHandler.addUpgrade(new Weapons());
        UpgradesHandler.addUpgrade(new Logistics());
        UpgradesHandler.addUpgrade(new Mobility());
        UpgradesHandler.addUpgrade(new Technology());
        UpgradesHandler.addUpgrade(new Durability());
        UpgradesHandler.addUpgrade(new Fighters());
        UpgradesHandler.addUpgrade(new Subsystems());
        UpgradesHandler.addUpgrade(new Magazines());

        loadConfigs();
    }

    public static void loadConfigs() {
        try {
            JSONObject settings = Global.getSettings().loadJSON("data/config/upgrades.json", "extra_system_reloaded");

            for (Upgrade upgrade : UPGRADES_LIST) {
                if (!settings.has(upgrade.getKey())) {
                    continue;
                }

                upgrade.setConfig(settings.getJSONObject(upgrade.getKey()));
            }
        } catch (JSONException | IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static void addUpgrade(Upgrade upgrade) {
        if(UPGRADES.containsKey(upgrade.getKey())) return;

        UPGRADES.put(upgrade.getKey(),upgrade);
        UPGRADES_LIST.add(upgrade);
        log.info(String.format("initialized upgrade [%s]", upgrade.getName()));
    }

    public static ESUpgrades generateRandomStats(FleetMemberAPI fleetMember, int fp) {
        ShipAPI.HullSize hullSize = fleetMember.getHullSpec().getHullSize();
        int maxlevel = ESModSettings.getHullSizeToMaxLevel().get(hullSize);
        float arg1 = fp/300f;
        ESUpgrades upgrades = new ESUpgrades();
        for(Upgrade name : UPGRADES_LIST) {
            upgrades.putUpgrade(name, (int) Math.min(maxlevel, Math.round(maxlevel*arg1*(Math.random()*0.8f+0.2f))));
        }
        return upgrades;
    }

}
