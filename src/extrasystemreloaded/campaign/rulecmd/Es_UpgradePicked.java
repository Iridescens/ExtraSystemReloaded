package extrasystemreloaded.campaign.rulecmd;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.rules.MemKeys;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.impl.campaign.rulecmd.BaseCommandPlugin;
import com.fs.starfarer.api.util.Misc;

import java.util.List;
import java.util.Map;

import static extrasystemreloaded.util.upgrades.Upgrades.UPGRADES;

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
                    for(String upgradeKey : UPGRADES.keySet()) {
                        if(upgradeKey.equals(upgradeId)) {
                            return true;
                        }
                    }
                    return false;
                case "get":
                    return memory.get("$UpgradeId") != null;
                case "set":
                    memory.set("$UpgradeId", params.get(1).getString(memoryMap));
                default:
                    break;
            }
        }
        return true;
    }
}
