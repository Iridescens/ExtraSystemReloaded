package extrasystemreloaded.campaign;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.OptionPanelAPI;
import com.fs.starfarer.api.campaign.TextPanelAPI;
import com.fs.starfarer.api.campaign.VisualPanelAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.impl.campaign.rulecmd.BaseCommandPlugin;
import com.fs.starfarer.api.util.Misc;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.Map;

public abstract class ESDialog extends BaseCommandPlugin {

    protected static Logger log;

    public boolean doesCommandAddOptions() {
        return true;
    }

    @Override
    public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {
        ESDialogContext context = new ESDialogContext(dialog, params, memoryMap);
        process(context, context.getTextPanel(), context.getOptions(), context.getVisual());

        return true;
    }

    protected abstract void process(ESDialogContext context, TextPanelAPI textPanel, OptionPanelAPI options, VisualPanelAPI visual);

    protected void log(String str, Object... args) {
        if(log == null) {
            log = Global.getLogger(this.getClass());
        }

        if(args != null && args.length > 0) {
            str = String.format(str, args);
        }

        log.info(str);
    }
}
