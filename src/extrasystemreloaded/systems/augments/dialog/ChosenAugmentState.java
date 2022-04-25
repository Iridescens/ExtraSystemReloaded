package extrasystemreloaded.systems.augments.dialog;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.OptionPanelAPI;
import com.fs.starfarer.api.campaign.TextPanelAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.util.Misc;
import extrasystemreloaded.campaign.rulecmd.ESInteractionDialogPlugin;
import extrasystemreloaded.dialog.DialogOption;
import extrasystemreloaded.dialog.DialogState;
import extrasystemreloaded.systems.augments.Augment;
import extrasystemreloaded.systems.augments.AugmentsHandler;
import extrasystemreloaded.util.ExtraSystems;
import lombok.Getter;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.util.Map;

public class ChosenAugmentState extends DialogState {
    @Getter
    private final Augment augment;
    private final AugmentInstallOption installOption;
    private final AugmentRemoveOption removeOption;

    public ChosenAugmentState(Augment augment) {
        this.augment = augment;
        installOption = new AugmentInstallOption(augment);
        removeOption = new AugmentRemoveOption(augment);
    }

    @Override
    public String getOptionText(ESInteractionDialogPlugin plugin, FleetMemberAPI fm, ExtraSystems es) {
        return augment.getName();
    }

    @Override
    public void execute(InteractionDialogAPI dialog, ESInteractionDialogPlugin plugin) {
        FleetMemberAPI fm = plugin.getShip();
        ExtraSystems es = ExtraSystems.getForFleetMember(fm);
        MarketAPI market = plugin.getMarket();

        TextPanelAPI textPanel = dialog.getTextPanel();
        OptionPanelAPI options = dialog.getOptionPanel();

        dialog.getVisualPanel().showFleetMemberInfo(fm);

        textPanel.addParagraph(augment.getName(), new Color(150, 220, 255));
        textPanel.addParagraph(augment.getDescription());

        addAugmentOptions(options, plugin, fm, es);

        AugmentsHandler.AUGMENTS_PICKER_DIALOG.addToOptions(options, plugin, fm, es, Keyboard.KEY_ESCAPE);
    }

    @Override
    public boolean consumesOptionPickedEvent(Object option) {
        return installOption.equals(option) || removeOption.equals(option);
    }

    @Override
    public void optionPicked(InteractionDialogAPI dialog, ESInteractionDialogPlugin plugin, Object option) {
        ((DialogOption) option).execute(dialog, plugin);

        plugin.redrawResourcesPanel();

        OptionPanelAPI options = dialog.getOptionPanel();
        TextPanelAPI textPanel = dialog.getTextPanel();

        FleetMemberAPI fm = plugin.getShip();
        MarketAPI market = plugin.getMarket();
        ExtraSystems es = ExtraSystems.getForFleetMember(fm);

        addAugmentOptions(options, plugin, fm, es);
    }

    private void addAugmentOptions(OptionPanelAPI options, ESInteractionDialogPlugin plugin, FleetMemberAPI fm, ExtraSystems es) {
        boolean hasAugment = es.hasAugment(augment);

        this.installOption.addToOptions(options, plugin, fm, es);
        this.removeOption.addToOptions(options, plugin, fm, es);

        if (hasAugment) {
            options.setEnabled(installOption, false);
        } else if (!augment.canApply(Global.getSector().getPlayerFleet(), fm)) {
            options.setEnabled(installOption, false);
            options.setEnabled(removeOption, false);
        } else {
            options.setEnabled(removeOption, false);
        }
    }

    @Override
    public void addToOptions(OptionPanelAPI options, ESInteractionDialogPlugin plugin, FleetMemberAPI fm, ExtraSystems es, String tooltip, int hotkey) {
        ShipAPI.HullSize hullSize = fm.getHullSpec().getHullSize();
        boolean hasAugment = es.hasAugment(augment);

        Color color = Misc.getButtonTextColor();

        if (hasAugment) {
            color = new Color(218, 218, 79);
        } else if (!augment.canApply(Global.getSector().getPlayerFleet(), fm)) {
            color = new Color(241, 100, 100);
        }

        options.addOption(getOptionText(plugin, fm, es), this, color, tooltip);

        if(hotkey >= 0) {
            options.setShortcut(this, hotkey, false, false, false, true);
        }
    }

    @Override
    public void modifyResourcesPanel(InteractionDialogAPI dialog, ESInteractionDialogPlugin plugin, Map<String, Float> resourceCosts) {
        FleetMemberAPI fm = plugin.getShip();
        this.getAugment().modifyResourcesPanel(dialog, plugin, resourceCosts, fm);
    }
}
