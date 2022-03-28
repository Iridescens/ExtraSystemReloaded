package extrasystemreloaded.systems.upgrades;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import extrasystemreloaded.ESModSettings;
import extrasystemreloaded.campaign.rulecmd.Es_ShipDialog;
import extrasystemreloaded.campaign.rulecmd.Es_ShipUpgradeDialog;
import extrasystemreloaded.systems.upgrades.methods.CreditsMethod;
import extrasystemreloaded.systems.upgrades.methods.ResourcesMethod;
import extrasystemreloaded.systems.upgrades.methods.UpgradeMethod;
import extrasystemreloaded.util.StringUtils;
import lombok.extern.log4j.Log4j;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.*;

@Log4j
public class UpgradesHandler {
    private static int UPGRADE_OPTION_ORDER = 1;
    public static final Map<String, Upgrade> UPGRADES = new HashMap<>();
    public static final List<Upgrade> UPGRADES_LIST = new ArrayList<>();

    public static List<UpgradeMethod> UPGRADE_METHODS = new ArrayList<UpgradeMethod>();

    public static void addUpgradeMethod(UpgradeMethod method) {
        UPGRADE_METHODS.add(method);
    }

    public static void initialize() {
        UPGRADE_METHODS.clear();
        UPGRADE_METHODS.add(new CreditsMethod());
        UPGRADE_METHODS.add(new ResourcesMethod());

        Es_ShipDialog.addShipOption(new Es_ShipUpgradeDialog.UpgradeOption(UPGRADE_OPTION_ORDER));
        UpgradesHandler.populateUpgrades();
    }

    public static void populateUpgrades() {
        try {
            JSONObject settings = Global.getSettings().getMergedJSONForMod("data/config/upgrades.json", "extra_system_reloaded");

            Iterator upgIterator = settings.keys();
            while(upgIterator.hasNext()) {
                String upgKey = (String) upgIterator.next();

                if(UPGRADES.containsKey(upgKey)) continue;

                JSONObject upgObj = (JSONObject) settings.getJSONObject(upgKey);

                Class<?> clzz = Global.getSettings().getScriptClassLoader().loadClass(upgObj.getString("upgradeClass"));
                Upgrade upgrade = (Upgrade) clzz.newInstance();

                if(upgrade.shouldLoad()) {
                    upgrade.setKey(upgKey);
                    upgrade.setName(StringUtils.getString(upgKey, "name"));
                    upgrade.setDescription(StringUtils.getString(upgKey, "description"));
                    upgrade.setTooltip(StringUtils.getString(upgKey, "tooltip"));
                    upgrade.setConfig(upgObj);

                    UpgradesHandler.addUpgrade(upgrade);
                }
            }
        } catch (JSONException | IOException | ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
            throw new RuntimeException(ex);
        }
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
