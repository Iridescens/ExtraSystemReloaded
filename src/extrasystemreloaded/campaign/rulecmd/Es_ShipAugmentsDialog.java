package extrasystemreloaded.campaign.rulecmd;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.OptionPanelAPI;
import com.fs.starfarer.api.campaign.TextPanelAPI;
import com.fs.starfarer.api.campaign.VisualPanelAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import extrasystemreloaded.Es_ModPlugin;
import extrasystemreloaded.augments.Augment;
import extrasystemreloaded.campaign.ESDialog;
import extrasystemreloaded.campaign.ESDialogContext;
import extrasystemreloaded.campaign.Es_ShipLevelFleetData;
import extrasystemreloaded.augments.AugmentsHandler;

import java.awt.*;

import static extrasystemreloaded.util.Utilities.TextTip;

public class Es_ShipAugmentsDialog extends ESDialog {
    public static final String RULE_MENUSTATE = "Augments";
    public static final String RULE_DIALOG_OPTION = "ESShipAugmentsPicked";
    private static final int NumUpgradesPerPage = 5;

    private static final String FUNCTIONTYPE_MODULES = "AugmentsSelected";
    private static final String FUNCTIONTYPE_CHANGEDPAGE = "ChangedPage";
    private static final String OPTION_NEXTPAGE = "ESShipAugmentsNEXT";
    private static final String OPTION_PREVPAGE = "ESShipAugmentsPREV";

    private static final String FUNCTIONTYPE_CONFIRM = "ConfirmAugmentPurchase";

    private static final String OPTION_APPLY = "ESShipAugmentApply";
    private static final String FUNCTIONTYPE_APPLY = "ApplyAugment";

    @Override
    protected void process(ESDialogContext context, TextPanelAPI textPanel, OptionPanelAPI options, VisualPanelAPI visual) {
        options.clearOptions();

        FleetMemberAPI selectedShip = context.getSelectedShip();

        if (selectedShip != null) {
            showOptions(context, textPanel, options, visual);

            options.addOption("Back to ship", Es_ShipDialog.RULE_DIALOG_OPTION);
        }

        options.addOption("Back to ship list", Es_ShipListDialog.RULE_DIALOG_OPTION);
        options.addOption("Back to main menu", ESDialog.RULE_DIALOG_OPTION);
    }

    private void showOptions(ESDialogContext context, TextPanelAPI textPanel, OptionPanelAPI options, VisualPanelAPI visual) {

        String functionType = context.getFunctionType();
        FleetMemberAPI selectedShip = context.getSelectedShip();
        Augment selectedUpgrade = context.getSelectedCore();

        switch (functionType) {
            case FUNCTIONTYPE_MODULES:
            case FUNCTIONTYPE_CHANGEDPAGE:
                if (selectedShip != null) {
                    populateAbilityOptions(context, textPanel, options, visual);
                }
                break;
            case FUNCTIONTYPE_CONFIRM:
                if (selectedShip != null && selectedUpgrade != null) {
                    populateAbilityPurchaseConfirmationOptions(context, textPanel, options, visual);
                    options.addOption("Back to modules", RULE_DIALOG_OPTION);
                }
                break;
            case FUNCTIONTYPE_APPLY:
                if (selectedShip != null && selectedUpgrade != null) {
                    doAbilityPurchase(context, textPanel, options, visual);
                    options.addOption("Back to modules", RULE_DIALOG_OPTION);
                }
                break;
            default:
                break;

        }
    }

    private void populateAbilityOptions(ESDialogContext context, TextPanelAPI textPanel, OptionPanelAPI options, VisualPanelAPI visual) {
        String functionType = context.getFunctionType();
        int upgradePageIndex = (int) context.getLocalMemory().getFloat("$ExtraUpgradePageIndex");

        MarketAPI currMarket = context.getCurrMarket();
        CampaignFleetAPI playerFleet = context.getPlayerFleet();
        FleetMemberAPI shipSelected = context.getSelectedShip();
        Es_ShipLevelFleetData buff = context.getBuff();

        boolean newPage = functionType.equals(FUNCTIONTYPE_CHANGEDPAGE);

        visual.showFleetMemberInfo(shipSelected);

        if(!newPage) {
            textPanel.addParagraph(TextTip.chooseUpgrade);
        }

        for (int i = upgradePageIndex * 5; i < Math.min(upgradePageIndex * 5 + 5, AugmentsHandler.AUGMENT_LIST.size()); i++) {
            Augment augment = AugmentsHandler.AUGMENT_LIST.get(i);
            boolean hasCore = buff.getExtraSystems().hasModule(augment);

            if (hasCore) {
                String tooltip = "You already have this module.";

                options.addOption(augment.getName(), augment.getKey(), tooltip);
                options.setEnabled(augment.getKey(), false);
            } else {
                options.addOption(augment.getName(), augment.getKey(), augment.getDescription());
            }
        }

        options.addOption("Previous page", OPTION_PREVPAGE);
        options.addOption("Next page", OPTION_NEXTPAGE);
        if (upgradePageIndex == 0) {
            options.setEnabled(OPTION_PREVPAGE, false);
        }
        if (upgradePageIndex * NumUpgradesPerPage + 5 >= AugmentsHandler.AUGMENT_LIST.size()) {
            options.setEnabled(OPTION_NEXTPAGE, false);
        }
    }

    private void populateAbilityPurchaseConfirmationOptions(ESDialogContext context, TextPanelAPI textPanel, OptionPanelAPI options, VisualPanelAPI visual) {
        FleetMemberAPI shipSelected = context.getSelectedShip();
        Augment abilitySelected = context.getSelectedCore();

        if (shipSelected != null && abilitySelected != null) {
            boolean canInstall = abilitySelected.canApply(context.getPlayerFleet(), shipSelected);

            textPanel.addPara(abilitySelected.getTextDescription());

            options.addOption(
                    "Install module",
                    OPTION_APPLY,
                    null
            );

            if (!canInstall) {
                textPanel.addPara(abilitySelected.getUnableToApplyTooltip(context.getPlayerFleet(), shipSelected), Color.red);

                options.setTooltip(OPTION_APPLY, abilitySelected.getUnableToApplyTooltip(context.getPlayerFleet(), shipSelected));
                options.setEnabled(OPTION_APPLY, false);
            }
        }
    }

    private void doAbilityPurchase(ESDialogContext context, TextPanelAPI textPanel, OptionPanelAPI options, VisualPanelAPI visual) {
        MarketAPI currMarket = context.getCurrMarket();
        FleetMemberAPI selectedShip = context.getSelectedShip();
        Es_ShipLevelFleetData buff = context.getBuff();
        Augment abilitySelected = context.getSelectedCore();
        CampaignFleetAPI playerFleet = context.getPlayerFleet();

        ShipAPI.HullSize hullSize = selectedShip.getHullSpec().getHullSize();

        if (!Es_ModPlugin.isDebugUpgradeCosts()) {
            abilitySelected.removeItemsFromFleet(playerFleet, selectedShip);
        }

        buff.getExtraSystems().putModule(abilitySelected);
        buff.save();

        Global.getSoundPlayer().playUISound("ui_char_increase_skill_new", 1f, 1f);
        textPanel.addParagraph(TextTip.Congratulation, Color.yellow);

    }
}
