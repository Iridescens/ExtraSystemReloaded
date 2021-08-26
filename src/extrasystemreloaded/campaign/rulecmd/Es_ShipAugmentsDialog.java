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
import extrasystemreloaded.augments.AugmentsHandler;
import extrasystemreloaded.hullmods.ExtraSystemHM;
import extrasystemreloaded.util.ExtraSystems;

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

    private static final String OPTION_DESTROY = "ESShipAugmentDestroy";
    private static final String FUNCTIONTYPE_DESTROY = "DestroyAugment";

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
                    options.addOption("Back to augments", RULE_DIALOG_OPTION);
                }
                break;
            case FUNCTIONTYPE_APPLY:
                if (selectedShip != null && selectedUpgrade != null) {
                    doAbilityPurchase(context, textPanel, options, visual);
                    options.addOption("Back to augments", RULE_DIALOG_OPTION);
                }
                break;
            case FUNCTIONTYPE_DESTROY:
                if (selectedShip != null && selectedUpgrade != null) {
                    doAbilityDestroy(context, textPanel, options, visual);
                    options.addOption("Back to augments", RULE_DIALOG_OPTION);
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
        ExtraSystems buff = context.getBuff();

        boolean newPage = functionType.equals(FUNCTIONTYPE_CHANGEDPAGE);

        visual.showFleetMemberInfo(shipSelected);

        if(!newPage) {
            textPanel.addParagraph(TextTip.chooseUpgrade);
        }

        int addedAugments = 0;
        for (int i = upgradePageIndex * 5; i < Math.min(upgradePageIndex * 5 + 5, AugmentsHandler.AUGMENT_LIST.size()); i++) {
            Augment augment = AugmentsHandler.AUGMENT_LIST.get(i);
            options.addOption(augment.getName(), augment.getKey(), augment.getTooltip());
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
        ExtraSystems buff = context.getBuff();

        if (shipSelected != null && abilitySelected != null) {
            textPanel.addPara(abilitySelected.getTextDescription());

            if(buff.hasAugment(abilitySelected)) {
                options.addOption(
                        "Destroy augment",
                        OPTION_DESTROY,
                        null
                );
            } else {
                boolean canInstall = abilitySelected.canApply(context.getPlayerFleet(), shipSelected);

                options.addOption(
                        "Install augment",
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
    }

    private void doAbilityPurchase(ESDialogContext context, TextPanelAPI textPanel, OptionPanelAPI options, VisualPanelAPI visual) {
        MarketAPI currMarket = context.getCurrMarket();
        FleetMemberAPI selectedShip = context.getSelectedShip();
        ExtraSystems buff = context.getBuff();
        Augment abilitySelected = context.getSelectedCore();
        CampaignFleetAPI playerFleet = context.getPlayerFleet();

        ShipAPI.HullSize hullSize = selectedShip.getHullSpec().getHullSize();

        if (!Es_ModPlugin.isDebugUpgradeCosts()) {
            abilitySelected.removeItemsFromFleet(playerFleet, selectedShip);
        }

        buff.putAugment(abilitySelected);
        buff.save(selectedShip);
        ExtraSystemHM.addToFleetMember(selectedShip);

        Global.getSoundPlayer().playUISound("ui_char_increase_skill_new", 1f, 1f);
        textPanel.addParagraph(TextTip.Congratulation, Color.yellow);

    }

    private void doAbilityDestroy(ESDialogContext context, TextPanelAPI textPanel, OptionPanelAPI options, VisualPanelAPI visual) {
        MarketAPI currMarket = context.getCurrMarket();
        FleetMemberAPI selectedShip = context.getSelectedShip();
        ExtraSystems buff = context.getBuff();
        Augment abilitySelected = context.getSelectedCore();

        ShipAPI.HullSize hullSize = selectedShip.getHullSpec().getHullSize();

        buff.removeAugment(abilitySelected);
        buff.save(selectedShip);
        ExtraSystemHM.addToFleetMember(selectedShip);

        Global.getSoundPlayer().playUISound("ui_char_increase_skill_new", 1f, 1f);
        textPanel.addParagraph(TextTip.Congratulation, Color.yellow);

    }
}
