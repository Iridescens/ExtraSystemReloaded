package extrasystemreloaded.campaign.rulecmd;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.impl.campaign.rulecmd.BaseCommandPlugin;
import com.fs.starfarer.api.util.Misc;

import java.util.List;
import java.util.Map;

public class LocalOr extends BaseCommandPlugin {
    private static final org.apache.log4j.Logger log = Global.getLogger(LocalOr.class);
    @Override
    public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {
        if (!params.isEmpty()) {
            String memoryParam = params.get(0).getString(memoryMap);
            if (memoryParam != null) {
                for (int i = 1; i < params.size(); i++) {
                    if (memoryParam.equals(params.get(i).getString(memoryMap))) {
                        return true;
                    }
                }
                return false;
            }
        }
        return true;
    }
}
