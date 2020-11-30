package extrasystemreloaded.commands;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.LocationAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import org.lazywizard.console.BaseCommand;
import org.lazywizard.console.CommonStrings;
import org.lazywizard.console.Console;
import org.lwjgl.util.vector.Vector2f;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


public class esr_getNearbyFleets implements BaseCommand {
    @Override
    public CommandResult runCommand(String args, CommandContext context) {
        if ( context.isInCampaign() )
        {
            Vector2f playerFleetLocation = Global.getSector().getPlayerFleet().getLocation();
            Console.showMessage("Player fleet at "+playerFleetLocation.x+","+playerFleetLocation.y);
            LocationAPI location = Global.getSector().getCurrentLocation();
            List<CampaignFleetAPI> fleets = location.getFleets();
            Map<Double,CampaignFleetAPI> fleetsAndDist = new TreeMap<Double,CampaignFleetAPI>(Collections.reverseOrder());

            double seek_range=30000;
            if (args != "") {
                try
                {
                    seek_range = Math.abs(Double.parseDouble(args));
                }
                catch (NumberFormatException ex)
                {
                    return CommandResult.BAD_SYNTAX;
                }
            }

            for (CampaignFleetAPI campaignFleetAPI : fleets) {
                if (campaignFleetAPI.isPlayerFleet()) {
                    continue;
                }
                Vector2f fleetLocation = campaignFleetAPI.getLocation();
                double distance = Math.sqrt(Math.pow((playerFleetLocation.x - fleetLocation.x), 2) + Math.pow((playerFleetLocation.y - fleetLocation.y), 2));
                if (distance <= seek_range) {
                    fleetsAndDist.put(distance, campaignFleetAPI);
                }
            }


            for (Map.Entry<Double,CampaignFleetAPI> entry : fleetsAndDist.entrySet() ) {
                Vector2f fleetLocation = entry.getValue().getLocation();
                Console.showMessage("Fleet: " + entry.getValue().getNameWithFaction() + " is at " + fleetLocation.x + "," + fleetLocation.y + " and " + entry.getKey() + " away");
                Console.showMessage("Consists of:");
                List<FleetMemberAPI> members = entry.getValue().getFleetData().getMembersListCopy();
                for (FleetMemberAPI member : members) {
                    if (member.getVariant().getModuleSlots() == null || member.getVariant().getModuleSlots().isEmpty()) {
                        Console.showMessage("  " + member.getShipName() + " " + member.getHullId());
                    } else {
                        Console.showMessage("  " + member.getShipName() + " " + member.getHullId() + "    <--- THIS SHIP IS MODULAR");
                    }
                }
            }
            return CommandResult.SUCCESS;
        } else {
            Console.showMessage(CommonStrings.ERROR_CAMPAIGN_ONLY);
            return CommandResult.WRONG_CONTEXT;
        }
    }
}
