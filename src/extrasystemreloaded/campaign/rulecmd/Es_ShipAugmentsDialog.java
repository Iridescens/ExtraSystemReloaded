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
import extrasystemreloaded.campaign.ESRuleUtils;
import extrasystemreloaded.systems.augments.Augment;
import extrasystemreloaded.campaign.ESDialog;
import extrasystemreloaded.campaign.ESDialogContext;
import extrasystemreloaded.systems.augments.AugmentsHandler;
import extrasystemreloaded.hullmods.ExtraSystemHM;
import extrasystemreloaded.util.ExtraSystems;
import extrasystemreloaded.util.StringUtils;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Es_ShipAugmentsDialog extends ESDialog {
    public static final String RULE_MENUSTATE = "Augments";
    public static final String RULE_DIALOG_OPTION = "ESShipAugmentsPicked";
    private static final int NumUpgradesPerPage = 5;

    private static final String FUNCTIONTYPE_AUGMENTS = "AugmentsSelected";
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
        FleetMemberAPI selectedShip = context.getSelectedShip();

        if (selectedShip != null) {
            showOptions(context, textPanel, options, visual);;
        }

        //use shortcuts only if on paginated list
        ESRuleUtils.addReturnOptions(options, selectedShip,
                !(context.getFunctionType().equals(FUNCTIONTYPE_AUGMENTS) || context.getFunctionType().equals(FUNCTIONTYPE_CHANGEDPAGE)));
    }

    private void showOptions(ESDialogContext context, TextPanelAPI textPanel, OptionPanelAPI options, VisualPanelAPI visual) {

        String functionType = context.getFunctionType();
        FleetMemberAPI selectedShip = context.getSelectedShip();
        Augment selectedUpgrade = context.getSelectedCore();

        switch (functionType) {
            case FUNCTIONTYPE_AUGMENTS:
            case FUNCTIONTYPE_CHANGEDPAGE:
                if (selectedShip != null) {
                    populateAbilityOptions(context, textPanel, options, visual);
                }
                break;
            case FUNCTIONTYPE_CONFIRM:
                if (selectedShip != null && selectedUpgrade != null) {
                    populateAbilityPurchaseConfirmationOptions(context, textPanel, options, visual);

                    options.addOption(StringUtils.getString("AugmentsDialog", "ReturnToAugmentsList"), RULE_DIALOG_OPTION);
                    options.setShortcut(RULE_DIALOG_OPTION, Keyboard.KEY_ESCAPE, false, false, false, true);
                }
                break;
            case FUNCTIONTYPE_APPLY:
                if (selectedShip != null && selectedUpgrade != null) {
                    doAugmentInstall(context, textPanel, options, visual);

                    options.addOption(StringUtils.getString("AugmentsDialog", "ReturnToAugmentsList"), RULE_DIALOG_OPTION);
                    options.setShortcut(RULE_DIALOG_OPTION, Keyboard.KEY_ESCAPE, false, false, false, true);
                }
                break;
            case FUNCTIONTYPE_DESTROY:
                if (selectedShip != null && selectedUpgrade != null) {
                    doAugmentDestroy(context, textPanel, options, visual);

                    options.addOption(StringUtils.getString("AugmentsDialog", "ReturnToAugmentsList"), RULE_DIALOG_OPTION);
                    options.setShortcut(RULE_DIALOG_OPTION, Keyboard.KEY_ESCAPE, false, false, false, true);
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
            textPanel.addParagraph(StringUtils.getString("AugmentsDialog", "AugmentsListOpened"));
        }

        List<Augment> sortedAugmentsList = getSortedAugmentList(shipSelected, buff, currMarket);
        int addedAugments = 0;
        for (int i = upgradePageIndex * 5; i < Math.min(upgradePageIndex * 5 + 5, sortedAugmentsList.size()); i++) {
            Augment augment = sortedAugmentsList.get(i);

            if(buff.hasAugment(augment)) {
                options.addOption(augment.getName(), augment.getKey(), new Color(173, 166, 94), augment.getTooltip());
            } else if (!augment.canApply(playerFleet, shipSelected)) {
                options.addOption(augment.getName(), augment.getKey(), new Color(173, 94, 94), augment.getTooltip());
            } else {
                options.addOption(augment.getName(), augment.getKey(), augment.getTooltip());
            }
        }

        options.addOption(StringUtils.getString("CommonOptions", "PreviousPage"), OPTION_PREVPAGE);
        options.addOption(StringUtils.getString("CommonOptions", "NextPage"), OPTION_NEXTPAGE);
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
                        StringUtils.getString("AugmentsDialog", "DestroyAugmentOption"),
                        OPTION_DESTROY,
                        null
                );
            } else {
                boolean canInstall = abilitySelected.canApply(context.getPlayerFleet(), shipSelected);

                options.addOption(
                        StringUtils.getString("AugmentsDialog", "InstallAugmentOption"),
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

    private void doAugmentInstall(ESDialogContext context, TextPanelAPI textPanel, OptionPanelAPI options, VisualPanelAPI visual) {
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
        textPanel.addParagraph(StringUtils.getString("AugmentsDialog", "AugmentInstalledSuccessfully"));
    }

    private void doAugmentDestroy(ESDialogContext context, TextPanelAPI textPanel, OptionPanelAPI options, VisualPanelAPI visual) {
        MarketAPI currMarket = context.getCurrMarket();
        FleetMemberAPI selectedShip = context.getSelectedShip();
        ExtraSystems buff = context.getBuff();
        Augment abilitySelected = context.getSelectedCore();

        ShipAPI.HullSize hullSize = selectedShip.getHullSpec().getHullSize();

        buff.removeAugment(abilitySelected);
        buff.save(selectedShip);
        ExtraSystemHM.addToFleetMember(selectedShip);

        Global.getSoundPlayer().playUISound("ui_char_increase_skill_new", 1f, 1f);
        textPanel.addParagraph(StringUtils.getString("AugmentsDialog", "AugmentDestroyedSuccessfully"));
    }

    private List<Augment> getSortedAugmentList(FleetMemberAPI fm, ExtraSystems buff, MarketAPI market) {//sort upgrade list so that upgrades that we can't upgrade are put in last.
        List<Augment> sortedUpgradeList = new ArrayList<>();

        //can afford an upgrade, and actually perform it.
        for(Augment upgrade : AugmentsHandler.AUGMENT_LIST) {
            if(!upgrade.shouldShow(fm, buff)) {
                continue;
            }

            boolean canUpgrade = upgrade.canApply(fm.getFleetData().getFleet(), fm);
            if(canUpgrade) {
                sortedUpgradeList.add(upgrade);
            }
        }

        //can not afford an upgrade
        for(Augment upgrade : AugmentsHandler.AUGMENT_LIST) {
            if(!upgrade.shouldShow(fm, buff)) {
                continue;
            }

            if(!sortedUpgradeList.contains(upgrade)) {
                if (!buff.hasAugment(upgrade)) {
                    sortedUpgradeList.add(upgrade);
                }
            }
        }

        //cannot do an upgrade
        for(Augment upgrade : AugmentsHandler.AUGMENT_LIST) {
            if(!upgrade.shouldShow(fm, buff)) {
                continue;
            }

            if(!sortedUpgradeList.contains(upgrade)) {
                sortedUpgradeList.add(upgrade);
            }
        }

        return sortedUpgradeList;
    }

    public static class AugmentOption extends Es_ShipDialog.ShipOption {
        public AugmentOption(int order) {
            super(order);
        }

        @Override
        public Object addOption(OptionPanelAPI options, FleetMemberAPI fm, ExtraSystems es, MarketAPI market) {
            options.addOption(StringUtils.getString("AugmentsDialog", "OpenAugmentOptions"), RULE_DIALOG_OPTION, null);

            return RULE_DIALOG_OPTION;
        }
    }
}
