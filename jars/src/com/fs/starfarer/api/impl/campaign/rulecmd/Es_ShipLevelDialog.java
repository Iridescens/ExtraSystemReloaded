package com.fs.starfarer.api.impl.campaign.rulecmd;

import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.OptionPanelAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.util.Misc;
import data.scripts.campaign.Es_ShipLevelFunctionPlugin;

import java.util.List;
import java.util.Map;

public class Es_ShipLevelDialog extends BaseCommandPlugin{

    private Es_ShipLevelFunctionPlugin ShipLevelFunctionPlugin = new Es_ShipLevelFunctionPlugin();

    @Override
    public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {
        OptionPanelAPI options = dialog.getOptionPanel();
        options.clearOptions();

        dialog.setPlugin(ShipLevelFunctionPlugin);
        ShipLevelFunctionPlugin.init(dialog);
        return true;
    }
}

