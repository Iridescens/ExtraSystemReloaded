package extrasystemreloaded.commands;

import extrasystemreloaded.campaign.Es_ShipLevelFunctionPlugin;
import org.lazywizard.console.BaseCommand;
import org.lazywizard.console.CommonStrings;
import org.lazywizard.console.Console;

public class esr_defaultUpgradeCosts implements BaseCommand
{
    @Override
    public CommandResult runCommand(String args, CommandContext context)
    {
        if ( context.isInCampaign() || context.isInMarket() )  // TODO: Remove isInCampaign when Ctrl+q deprecates
        {
            Es_ShipLevelFunctionPlugin.unsetDebugUpgradesRemoveCost();
            Console.showMessage("DEBUG_UPGRADES_REMOVE_COST flag set to "+Es_ShipLevelFunctionPlugin.isDebugUpgradesRemoveCost());
            return CommandResult.SUCCESS;
        } else {
            Console.showMessage(CommonStrings.ERROR_CAMPAIGN_ONLY);
            return CommandResult.WRONG_CONTEXT;
        }

    }
}
