package extrasystemreloaded.campaign.rulecmd;

import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.impl.campaign.rulecmd.BaseCommandPlugin;
import com.fs.starfarer.api.util.Misc;

import java.util.List;
import java.util.Map;

public class Es_MainMenu extends BaseCommandPlugin {
    public static final String RULE_DIALOG_OPTION = "ESMainMenu";
    public static final String RULE_MENUSTATE = "ESMainMenu";

    /**
     * 15:ESShipList:Consider an overhaul to a ship
     * 16:ESOpenGUI:Open the Systems GUI
     * 90:ESDialogBack:Back
     */

    public boolean doesCommandAddOptions() {
        return false;
    }

    @Override
    public boolean execute(String s, InteractionDialogAPI interactionDialogAPI, List<Misc.Token> list, Map<String, MemoryAPI> map) {
        interactionDialogAPI.getOptionPanel().clearOptions();
        interactionDialogAPI.setXOffset(0f);
        interactionDialogAPI.setYOffset(0f);

        interactionDialogAPI.getOptionPanel().addOption("Consider an overhaul to a ship", "ESShipList");
        interactionDialogAPI.getOptionPanel().addOption("Open the Systems GUI", "ESOpenGUI");
        interactionDialogAPI.getOptionPanel().addOption("Back", "ESDialogBack");

        return true;
    }
}
