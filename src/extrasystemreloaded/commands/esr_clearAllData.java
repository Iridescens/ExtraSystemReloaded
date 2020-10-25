package extrasystemreloaded.commands;

import com.fs.starfarer.api.Global;
import extrasystemreloaded.campaign.Es_ShipLevelFleetData;
import org.apache.log4j.Level;
import org.lazywizard.console.BaseCommand;
import org.lazywizard.console.CommonStrings;
import org.lazywizard.console.Console;

public class esr_clearAllData implements BaseCommand
{
    @Override
    public CommandResult runCommand(String args, CommandContext context)
    {
        if ( context.isInCampaign() )
        {
            Es_ShipLevelFleetData.removeESHullmodsFromEveryVariant();
            Global.getSector().getPersistentData().remove("Es_LEVEL_SHIPLIST");
            Global.getSector().getPersistentData().remove("Es_LEVEL_SHIPMAP");
            Global.getSector().getPersistentData().remove("AchievementData");
            try {
                Global.getSector().removeTransientScriptsOfClass(Class.forName("extrasystemreloaded.campaign.Es_ExtraSystemController"));
                Global.getSector().removeTransientScriptsOfClass(Class.forName("extrasystemreloaded.campaign.Es_BaseAchievementsUnlockPlugin"));
                Global.getSector().removeTransientScriptsOfClass(Class.forName("extrasystemreloaded.campaign.Es_CampaignRenderPlugin"));
                Global.getSector().removeTransientScriptsOfClass(Class.forName("extrasystemreloaded.campaign.Es_GameSetPausePlugin"));
            } catch (Exception e) {
                e.printStackTrace();
                Console.showMessage("Exception while removing transient scripts: "+e.getMessage(), Level.ERROR);
            }
            return CommandResult.SUCCESS;
        } else {
            Console.showMessage(CommonStrings.ERROR_CAMPAIGN_ONLY);
            return CommandResult.WRONG_CONTEXT;
        }

    }
}