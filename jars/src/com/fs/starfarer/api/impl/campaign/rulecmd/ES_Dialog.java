package com.fs.starfarer.api.impl.campaign.rulecmd;

import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.OptionPanelAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.util.Misc;
import data.scripts.campaign.Es_ShipLevelFunctionPlugin;

import java.util.List;
import java.util.Map;

public class ES_Dialog extends BaseCommandPlugin{

    private Es_ShipLevelFunctionPlugin ShipLevelFunctionPlugin = new Es_ShipLevelFunctionPlugin();

//TODO    private Es_ShipQualityFunctionPlugin ShipQualityFunctionPlugin = new Es_ShipQualityFunctionPlugin();

    @Override
    public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {
        String arg = params.get(0).getString(memoryMap);
        OptionPanelAPI options = dialog.getOptionPanel();
        options.clearOptions();
        switch (arg) {

            case "ShipLevel":
                dialog.setPlugin(ShipLevelFunctionPlugin);
                ShipLevelFunctionPlugin.init(dialog);
                return true;

            case "ShipQuality":
// TODO     Create with rules.csv, not Plugin.

                return true;
        }


    return false;
    }
}

