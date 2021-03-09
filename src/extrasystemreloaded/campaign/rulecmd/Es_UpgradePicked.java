package extrasystemreloaded.campaign.rulecmd;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.rules.MemKeys;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.rulecmd.BaseCommandPlugin;
import com.fs.starfarer.api.util.Misc;
import extrasystemreloaded.util.ESUpgrades;
import org.apache.log4j.Level;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static extrasystemreloaded.util.ESUpgrades.UPGRADES;

public class Es_UpgradePicked extends BaseCommandPlugin {
    private static final org.apache.log4j.Logger log = Global.getLogger(Es_UpgradePicked.class);
    @Override
    public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {

        MemoryAPI memory = memoryMap.get(MemKeys.LOCAL);
        if (!params.isEmpty()) {
            String action = params.get(0).getString(memoryMap);

            switch (action) {
                case "remove":
                    memory.set("$UpgradeId", null);
                    break;
                case "exists":
                    String upgradeId = params.get(1).getString(memoryMap);
                    log.log(Level.INFO, String.format("upgradeID: [%s]", upgradeId));
                    for(ESUpgrades.UpgradeKey upgradeKey : UPGRADES.keySet()) {
                        if(upgradeKey.compareKey(upgradeId)) {
                            return true;
                        }
                    }
                    return false;
                case "get":
                    return memory.get("$UpgradeId") != null;
                case "set":
                    log.log(Level.INFO, String.format("upgradeID: [%s]", params.get(1).getString(memoryMap)));
                    memory.set("$UpgradeId", params.get(1).getString(memoryMap));
                default:
                    break;
            }
        }
        return true;
    }
}
