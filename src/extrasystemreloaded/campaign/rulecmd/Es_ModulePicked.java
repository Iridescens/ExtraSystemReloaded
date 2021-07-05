package extrasystemreloaded.campaign.rulecmd;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.rules.MemKeys;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.impl.campaign.rulecmd.BaseCommandPlugin;
import com.fs.starfarer.api.util.Misc;
import extrasystemreloaded.util.modules.Module;

import java.util.List;
import java.util.Map;

import static extrasystemreloaded.util.modules.Modules.MODULE_LIST;

public class Es_ModulePicked extends BaseCommandPlugin {
    private static final org.apache.log4j.Logger log = Global.getLogger(Es_ModulePicked.class);
    @Override
    public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {

        MemoryAPI memory = memoryMap.get(MemKeys.LOCAL);
        if (!params.isEmpty()) {
            String action = params.get(0).getString(memoryMap);

            switch (action) {
                case "remove":
                    memory.set("$ModuleId", null);
                    break;
                case "exists":
                    String moduleId = params.get(1).getString(memoryMap);
                    for(Module module : MODULE_LIST) {
                        if(module.getKey().equals(moduleId)) {
                            return true;
                        }
                    }
                    return false;
                case "get":
                    return memory.get("$ModuleId") != null;
                case "set":
                    memory.set("$ModuleId", params.get(1).getString(memoryMap));
                default:
                    break;
            }
        }
        return true;
    }
}
