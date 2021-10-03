package extrasystemreloaded.commands;

import extrasystemreloaded.ESModSettings;
import extrasystemreloaded.Es_ModPlugin;
import extrasystemreloaded.upgrades.UpgradesHandler;
import extrasystemreloaded.util.StatUtils;
import org.lazywizard.console.BaseCommand;
import org.lazywizard.console.CommonStrings;
import org.lazywizard.console.Console;

public class esr_reloadconfig implements BaseCommand {
    @Override
    public CommandResult runCommand(String args, CommandContext context) {
        if (context.isInCampaign()) {

            ESModSettings.loadModSettings();
            StatUtils.loadStatCurves();
            UpgradesHandler.loadConfigs();

            return CommandResult.SUCCESS;
        } else {
            Console.showMessage(CommonStrings.ERROR_CAMPAIGN_ONLY);
            return CommandResult.WRONG_CONTEXT;
        }
    }
}
