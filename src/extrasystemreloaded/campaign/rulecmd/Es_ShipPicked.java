package extrasystemreloaded.campaign.rulecmd;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.rules.MemKeys;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.rulecmd.BaseCommandPlugin;
import com.fs.starfarer.api.util.Misc;

import java.util.List;
import java.util.Map;

public class Es_ShipPicked extends BaseCommandPlugin {
    public static final String MEM_KEY = "$shipId";
    private static final org.apache.log4j.Logger log = Global.getLogger(Es_ShipPicked.class);

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
                    String shipSelectedId = params.get(1).getString(memoryMap);
                    List<FleetMemberAPI> ShipList = Global.getSector().getPlayerFleet().getFleetData().getMembersListCopy();

                    for (int i = 0; i < ShipList.size(); i++) {
                        FleetMemberAPI fleetMemberAPI = ShipList.get(i);
                        if (fleetMemberAPI.getId().equals(shipSelectedId)) {
                            return true;
                        }
                    }
                    return false;
                case "get":
                    return memory.getString(MEM_KEY) != null;
                case "set":
                    memory.set(MEM_KEY, params.get(1).getString(memoryMap));
                default:
                    break;
            }
        }
        return true;
    }
}
