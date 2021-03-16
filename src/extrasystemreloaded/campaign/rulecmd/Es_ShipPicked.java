package extrasystemreloaded.campaign.rulecmd;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.rules.MemKeys;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.rulecmd.BaseCommandPlugin;
import com.fs.starfarer.api.util.Misc;
import org.apache.log4j.Level;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Es_ShipPicked extends BaseCommandPlugin {
    private static final org.apache.log4j.Logger log = Global.getLogger(Es_ShipPicked.class);
    @Override
    public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {
        MemoryAPI memory = memoryMap.get(MemKeys.LOCAL);
        log.log(Level.INFO, String.format("menuState %s", memory.getString("$menuState")));
        if (!params.isEmpty()) {
            String action = params.get(0).getString(memoryMap);

            log.log(Level.INFO, String.format("action %s", action));
            switch (action) {
                case "remove":
                    memory.set("$ShipSelectedId", null);
                    break;
                case "exists":
                    String shipSelectedId = params.get(1).getString(memoryMap);
                    List<FleetMemberAPI> ShipList = Global.getSector().getPlayerFleet().getFleetData().getMembersListCopy();
                    Iterator<FleetMemberAPI> iterator = ShipList.iterator();
                    while (iterator.hasNext()) {
                        FleetMemberAPI fleetMemberAPI = (FleetMemberAPI) iterator.next();
                        if (fleetMemberAPI.isFighterWing()) {
                            iterator.remove();
                        }
                    }

                    for (int i = 0; i < ShipList.size(); i++) {
                        FleetMemberAPI fleetMemberAPI = ShipList.get(i);
                        log.log(Level.INFO, String.format("fm Id [%s]", fleetMemberAPI.getId()));
                        if (fleetMemberAPI.getId().equals(shipSelectedId)) {
                            log.log(Level.INFO, String.format("exists %s", shipSelectedId));
                            return true;
                        }
                    }
                    return false;
                case "get":
                    return memory.getString("$ShipSelectedId") != null;
                case "set":
                    memory.set("$ShipSelectedId", params.get(1).getString(memoryMap));
                default:
                    break;
            }
        }
        return true;
    }
}
