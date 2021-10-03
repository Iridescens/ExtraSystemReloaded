package extrasystemreloaded.campaign.rulecmd;

import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.impl.campaign.rulecmd.BaseCommandPlugin;
import com.fs.starfarer.api.util.Misc;
import extrasystemreloaded.campaign.ESDialogContext;
import extrasystemreloaded.gui.ESGUI;

import java.util.List;
import java.util.Map;

public class Es_OpenGUI extends BaseCommandPlugin {
    public static final String RULE_DIALOG_OPTION = "ESOpenGUI";

    @Override
    public boolean execute(String ruleId, final InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {
        final ESDialogContext context = new ESDialogContext(dialog, params, memoryMap);
        ESGUI.showGUI(context);

        return true;
    }

}
