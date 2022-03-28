package extrasystemreloaded.campaign;

import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.OptionPanelAPI;
import com.fs.starfarer.api.campaign.TextPanelAPI;
import com.fs.starfarer.api.campaign.VisualPanelAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.rulecmd.BaseCommandPlugin;
import com.fs.starfarer.api.util.Misc;
import extrasystemreloaded.campaign.rulecmd.Es_ShipDialog;
import extrasystemreloaded.campaign.rulecmd.Es_ShipListDialog;
import extrasystemreloaded.util.StringUtils;
import lombok.extern.log4j.Log4j;
import org.lwjgl.input.Keyboard;

import java.util.List;
import java.util.Map;

@Log4j
public abstract class ESDialog extends BaseCommandPlugin {
    public static final String RULE_DIALOG_OPTION = "ESDialogBack";
    public static final String RULE_MENUSTATE = "ESMainMenu";

    public static Object addReturnOption(OptionPanelAPI options) {
        options.addOption(StringUtils.getString("MainMenu", "BackToMainMenu"), RULE_DIALOG_OPTION);
        return RULE_DIALOG_OPTION;
    }

    public static Object addReturnOptionWithShortcut(OptionPanelAPI options) {
        Object option = addReturnOption(options);
        options.setShortcut(option, Keyboard.KEY_ESCAPE, false, false, false, true);
        return RULE_DIALOG_OPTION;
    }

    @Override
    public boolean doesCommandAddOptions() {
        return false;
    }

    @Override
    public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {
        dialog.getOptionPanel().clearOptions();

        ESDialogContext context = new ESDialogContext(dialog, params, memoryMap);
        process(context, context.getTextPanel(), context.getOptions(), context.getVisual());

        return true;
    }

    protected abstract void process(ESDialogContext context, TextPanelAPI textPanel, OptionPanelAPI options, VisualPanelAPI visual);

    protected void log(String str, Object... args) {
        log.info(str);
    }
}
