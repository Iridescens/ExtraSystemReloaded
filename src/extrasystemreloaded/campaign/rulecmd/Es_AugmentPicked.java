package extrasystemreloaded.campaign.rulecmd;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.rules.MemKeys;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.impl.campaign.rulecmd.BaseCommandPlugin;
import com.fs.starfarer.api.util.Misc;
import extrasystemreloaded.augments.Augment;

import java.util.List;
import java.util.Map;

import static extrasystemreloaded.augments.AugmentsHandler.AUGMENT_LIST;

public class Es_AugmentPicked extends BaseCommandPlugin {
    public static final String MEM_KEY = "$augmentId";
    private static final org.apache.log4j.Logger log = Global.getLogger(Es_AugmentPicked.class);

    @Override
    public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {

        MemoryAPI memory = memoryMap.get(MemKeys.LOCAL);
        if (!params.isEmpty()) {
            String action = params.get(0).getString(memoryMap);

            switch (action) {
                case "remove":
                    memory.set(MEM_KEY, null);
                    break;
                case "exists":
                    String moduleId = params.get(1).getString(memoryMap);
                    for(Augment augment : AUGMENT_LIST) {
                        if(augment.getKey().equals(moduleId)) {
                            return true;
                        }
                    }
                    return false;
                case "get":
                    return memory.get(MEM_KEY) != null;
                case "set":
                    memory.set(MEM_KEY, params.get(1).getString(memoryMap));
                default:
                    break;
            }
        }
        return true;
    }
}
