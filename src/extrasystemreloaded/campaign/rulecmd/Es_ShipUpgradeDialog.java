package extrasystemreloaded.campaign.rulecmd;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import extrasystemreloaded.ESModSettings;
import extrasystemreloaded.campaign.ESDialog;
import extrasystemreloaded.campaign.ESDialogContext;
import extrasystemreloaded.campaign.ESRuleUtils;
import extrasystemreloaded.hullmods.ExtraSystemHM;
import extrasystemreloaded.systems.upgrades.Upgrade;
import extrasystemreloaded.systems.upgrades.UpgradesHandler;
import extrasystemreloaded.systems.upgrades.methods.UpgradeMethod;
import extrasystemreloaded.util.ExtraSystems;
import extrasystemreloaded.util.StringUtils;
import org.lwjgl.input.Keyboard;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class Es_ShipUpgradeDialog extends ESDialog {
    public static final String RULE_MENUSTATE = "ESUpgrades";
    public static final String RULE_DIALOG_OPTION = "ESShipExtraUpgradesPicked";
    private static final int NumUpgradesPerPage = 5;

    private static final String FUNCTIONTYPE_UPGRADES = "ExtraUpgradesSelected";
    private static final String FUNCTIONTYPE_CHANGEDPAGE = "ChangedPage";
    private static final String OPTION_NEXTPAGE = "ESShipExtraUpgradesNEXT";
    private static final String OPTION_PREVPAGE = "ESShipExtraUpgradesPREV";

    private static final String FUNCTIONTYPE_CONFIRM = "ConfirmExtraUpgrade";

    @Override
    protected void process(ESDialogContext context, TextPanelAPI textPanel, OptionPanelAPI options, VisualPanelAPI visual) {
        options.clearOptions();

        FleetMemberAPI selectedShip = context.getSelectedShip();

        if (selectedShip != null) {
            showUpgradeOptions(context, textPanel, options, visual);
        }

        //use shortcuts only if on paginated list
        ESRuleUtils.addReturnOptions(options, selectedShip,
                !(context.getFunctionType().equals(FUNCTIONTYPE_UPGRADES) || context.getFunctionType().equals(FUNCTIONTYPE_CHANGEDPAGE)));
    }

    private void showUpgradeOptions(ESDialogContext context, TextPanelAPI textPanel, OptionPanelAPI options, VisualPanelAPI visual) {

        String functionType = context.getFunctionType();
        FleetMemberAPI selectedShip = context.getSelectedShip(); //memkey set by rules.csv
        Upgrade selectedUpgrade = context.getSelectedUpgrade(); //memkey set by rules.csv

        switch (functionType) {
            case FUNCTIONTYPE_UPGRADES:
            case FUNCTIONTYPE_CHANGEDPAGE:
                if (selectedShip != null) {
                    populateAbilityOptions(context, textPanel, options, visual);
                }
                break;
            case FUNCTIONTYPE_CONFIRM:
                if (selectedShip != null && selectedUpgrade != null) {
                    populateAbilityPurchaseConfirmationOptions(context, textPanel, options, visual);
                    options.addOption(StringUtils.getString("UpgradesDialog", "ReturnToUpgradesList"), RULE_DIALOG_OPTION);
                    options.setShortcut(RULE_DIALOG_OPTION, Keyboard.KEY_ESCAPE, false, false, false, true);
                }
                break;
            default:
                //must be upgrade method
                if (selectedShip != null && selectedUpgrade != null) {
                    for(UpgradeMethod method : UpgradesHandler.UPGRADE_METHODS) {
                        String methodOption = method.getOptionId();
                        if (functionType.equals(methodOption)) {
                            doAbilityUpgrade(context, method, textPanel, options, visual);
                            populateAbilityPurchaseConfirmationOptions(context, textPanel, options, visual);


                            options.addOption(StringUtils.getString("UpgradesDialog", "ReturnToUpgradesList"), RULE_DIALOG_OPTION);
                            options.setShortcut(RULE_DIALOG_OPTION, Keyboard.KEY_ESCAPE, false, false, false, true);
                            return;
                        }
                    }
                }

                log("Invalid upgrade dialog option ID [%s]", functionType);
                textPanel.addParagraph(String.format("Invalid upgrade dialog option ID [%s]", functionType));

                options.addOption(StringUtils.getString("UpgradesDialog", "ReturnToUpgradesList"), RULE_DIALOG_OPTION);
                options.setShortcut(RULE_DIALOG_OPTION, Keyboard.KEY_ESCAPE, false, false, false, true);
                break;
        }
    }

    private void populateAbilityOptions(ESDialogContext context, TextPanelAPI textPanel, OptionPanelAPI options, VisualPanelAPI visual) {
        String functionType = context.getFunctionType();
        int upgradePageIndex = (int) context.getLocalMemory().getFloat("$ExtraUpgradePageIndex");

        MarketAPI currMarket = context.getCurrMarket();
        CampaignFleetAPI playerFleet = context.getPlayerFleet();
        FleetMemberAPI selectedShip = context.getSelectedShip();
        ShipAPI.HullSize hullSize = selectedShip.getHullSpec().getHullSize();
        ExtraSystems buff = context.getBuff();

        boolean newPage = functionType.equals(FUNCTIONTYPE_CHANGEDPAGE);

        visual.showFleetMemberInfo(selectedShip);

        if(!newPage) {
            textPanel.addParagraph(StringUtils.getString("UpgradesDialog", "UpgradesListOpened"));
        }

        List<Upgrade> sortedUpgradeList = getSortedUpgradeList(selectedShip, buff, currMarket);

        for (int i = upgradePageIndex * 5; i < Math.min(upgradePageIndex * 5 + 5, sortedUpgradeList.size()); i++) {
            Upgrade upgrade = sortedUpgradeList.get(i);
            int level = buff.getUpgrade(upgrade.getKey());
            int max = upgrade.getMaxLevel(hullSize);

            if (buff.isMaxLevel(selectedShip, upgrade)) {
                options.addOption(
                        StringUtils.getTranslation("UpgradesDialog", "UpgradeNameMaxed")
                                .format("upgradeName", upgrade.getName())
                                .toString(),
                        upgrade.getKey(), new Color(173, 166, 94), upgrade.getDescription());
            } else if (!canUseUpgradeMethods(selectedShip, buff, hullSize, upgrade, playerFleet, currMarket)) {
                options.addOption(
                        StringUtils.getTranslation("UpgradesDialog", "UpgradeNameWithLevelAndMax")
                                .format("upgradeName", upgrade.getName())
                                .format("level", level)
                                .format("max", max)
                                .toString(),
                        upgrade.getKey(), new Color(173, 94, 94), upgrade.getDescription());
            } else {
                options.addOption(
                        StringUtils.getTranslation("UpgradesDialog", "UpgradeNameWithLevelAndMax")
                                .format("upgradeName", upgrade.getName())
                                .format("level", level)
                                .format("max", max)
                                .toString(),
                        upgrade.getKey(), upgrade.getDescription());
            }
        }

        options.addOption(StringUtils.getString("CommonOptions", "PreviousPage"), OPTION_PREVPAGE);
        options.addOption(StringUtils.getString("CommonOptions", "NextPage"), OPTION_NEXTPAGE);
        if (upgradePageIndex == 0) {
            options.setEnabled(OPTION_PREVPAGE, false);
        }
        if (upgradePageIndex * NumUpgradesPerPage + 5 >= UpgradesHandler.UPGRADES_LIST.size()) {
            options.setEnabled(OPTION_NEXTPAGE, false);
        }
    }

    private boolean canUseUpgradeMethods(FleetMemberAPI selectedShip, ExtraSystems buff, ShipAPI.HullSize hullSize, Upgrade upgrade, CampaignFleetAPI fleet, MarketAPI currMarket) {
        for (UpgradeMethod method : UpgradesHandler.UPGRADE_METHODS) {
            if (method.canShow(selectedShip, buff, upgrade, currMarket)
                && method.canUse(selectedShip, buff, upgrade, currMarket)) {
                return true;
            }
        }

        return false;
    }

    private void populateAbilityPurchaseConfirmationOptions(ESDialogContext context, TextPanelAPI textPanel, OptionPanelAPI options, VisualPanelAPI visual) {
        MarketAPI currMarket = context.getCurrMarket();
        FleetMemberAPI selectedShip = context.getSelectedShip();
        ExtraSystems buff = context.getBuff();
        Upgrade abilitySelected = context.getSelectedUpgrade();
        float quality = buff.getQuality(selectedShip);

        if (selectedShip != null && abilitySelected != null) {
            ShipAPI.HullSize hullSize = selectedShip.getHullSpec().getHullSize();
            int max = abilitySelected.getMaxLevel(hullSize);
            int level = buff.getUpgrade(abilitySelected);

            if(level >= max) {
                textPanel.addParagraph(StringUtils.getString("UpgradesDialog", "CannotPerformUpgradeMaxLevel"));
                return;
            }

            appendUpgradeSuccessChance(context, textPanel, options, visual, level, max);

            for(UpgradeMethod method : UpgradesHandler.UPGRADE_METHODS) {
                if(method.canShow(selectedShip, buff, abilitySelected, currMarket)) {
                    method.addOption(options, selectedShip, buff, abilitySelected, currMarket);
                }
            }
        }
    }

    private void populateAbilityPurchasedOptions(ESDialogContext context, TextPanelAPI textPanel, OptionPanelAPI options, VisualPanelAPI visual) {
        FleetMemberAPI fm = context.getSelectedShip();
        MarketAPI market = context.getCurrMarket();
        ExtraSystems es = context.getBuff();
        Upgrade upgrade = context.getSelectedUpgrade();
        float quality = es.getQuality(fm);

        if (fm != null && upgrade != null) {
            ShipAPI.HullSize hullSize = fm.getHullSpec().getHullSize();
            int max = upgrade.getMaxLevel(hullSize);
            int level = es.getUpgrade(upgrade.getKey());

            textPanel.addParagraph(StringUtils.getTranslation("UpgradesDialog", "UpgradeNameWithLevelAndMax")
                    .format("upgradeName", upgrade.getName())
                    .format("level", level)
                    .format("max", max)
                    .toString());

            if (level >= max) {
                textPanel.addParagraph(StringUtils.getString("UpgradesDialog", "CannotPerformUpgradeMaxLevel"));
            } else {
                for(UpgradeMethod method : UpgradesHandler.UPGRADE_METHODS) {
                    if(method.canShow(fm, es, upgrade, market)) {
                        method.addOption(options, fm, es, upgrade, market);
                    }
                }
            }
        }
    }

    private void doAbilityUpgrade(ESDialogContext context, UpgradeMethod method, TextPanelAPI textPanel, OptionPanelAPI options, VisualPanelAPI visual) {
        FleetMemberAPI selectedShip = context.getSelectedShip();
        ExtraSystems buff = context.getBuff();
        Upgrade abilitySelected = context.getSelectedUpgrade();
        MarketAPI currMarket = context.getCurrMarket();

        ShipAPI.HullSize hullSize = selectedShip.getHullSpec().getHullSize();
        int max = abilitySelected.getMaxLevel(hullSize);
        int currentLevel = buff.getUpgrade(abilitySelected);

        boolean success = ESModSettings.getBoolean(ESModSettings.UPGRADE_ALWAYS_SUCCEED);
        if(!success && currentLevel != 0) {
            float minChanceOfFailure = ESModSettings.getFloat(ESModSettings.UPGRADE_FAILURE_CHANCE);
            float possibility = (float) Math.cos(Math.PI * currentLevel * 0.5f / max)
                    * (1f - minChanceOfFailure) + minChanceOfFailure;

            success = ((float) Math.random()) < possibility;
        }

        if(success) {
            method.apply(selectedShip, buff, abilitySelected, currMarket);

            buff.save(selectedShip);
            ExtraSystemHM.addToFleetMember(selectedShip);

            Global.getSoundPlayer().playUISound("ui_char_increase_skill_new", 1f, 1f);
            textPanel.addParagraph(StringUtils.getString("UpgradesDialog", "UpgradePerformedSuccessfully"));
        } else {
            textPanel.addParagraph(StringUtils.getString("UpgradesDialog", "UpgradePerformedFailure"));
        }
    }

    private void appendUpgradeSuccessChance(ESDialogContext context, TextPanelAPI textPanel, OptionPanelAPI options, VisualPanelAPI visual, int level, int max) {
        MarketAPI currMarket = context.getCurrMarket();
        FleetMemberAPI selectedShip = context.getSelectedShip();
        ExtraSystems buff = context.getBuff();
        Upgrade abilitySelected = context.getSelectedUpgrade();
        CampaignFleetAPI playerFleet = context.getPlayerFleet();


        if(!ESModSettings.getBoolean(ESModSettings.UPGRADE_ALWAYS_SUCCEED)) {
            float possibility = 1f;
            if (level != 0) {
                float minChanceOfFailure = ESModSettings.getFloat(ESModSettings.UPGRADE_FAILURE_CHANCE);
                possibility = (float) Math.cos(Math.PI * level * 0.5f / max)
                        * (1f - minChanceOfFailure) + minChanceOfFailure;
            }

            String successChance = Math.round(possibility * 1000f) / 10f + "%";
            textPanel.addParagraph(
                    StringUtils.getTranslation("UpgradesDialog", "UpgradeSuccessChance")
                            .format("successChance", successChance)
                            .toString());
            textPanel.highlightInLastPara(successChance);
        }
    }

    private List<Upgrade> getSortedUpgradeList(FleetMemberAPI fm, ExtraSystems buff, MarketAPI market) {//sort upgrade list so that upgrades that we can't upgrade are put in last.
        List<Upgrade> sortedUpgradeList = new ArrayList<>();

        //can afford an upgrade, and actually perform it.
        for(Upgrade upgrade : UpgradesHandler.UPGRADES_LIST) {
            if(!upgrade.shouldShow(buff)) {
                continue;
            }

            boolean canUpgrade = !buff.isMaxLevel(fm, upgrade);
            if (canUpgrade) {
                canUpgrade = canUseUpgradeMethods(fm, buff, fm.getHullSpec().getHullSize(), upgrade, fm.getFleetData().getFleet(), market);
            }

            if(canUpgrade) {
                sortedUpgradeList.add(upgrade);
            }
        }

        //can not afford an upgrade
        for(Upgrade upgrade : UpgradesHandler.UPGRADES_LIST) {
            if(!upgrade.shouldShow(buff)) {
                continue;
            }

            if(!sortedUpgradeList.contains(upgrade)) {
                if (!buff.isMaxLevel(fm, upgrade)) {
                    sortedUpgradeList.add(upgrade);
                }
            }
        }

        //cannot do an upgrade
        for(Upgrade upgrade : UpgradesHandler.UPGRADES_LIST) {
            if(!upgrade.shouldShow(buff)) {
                continue;
            }

            if(!sortedUpgradeList.contains(upgrade)) {
                sortedUpgradeList.add(upgrade);
            }
        }

        return sortedUpgradeList;
    }

    public static class UpgradeOption extends Es_ShipDialog.ShipOption {
        public UpgradeOption(int order) {
            super(order);
        }

        @Override
        public Object addOption(OptionPanelAPI options, FleetMemberAPI fm, ExtraSystems es, MarketAPI market) {
            options.addOption(StringUtils.getString("UpgradesDialog", "OpenUpgradeOptions"), RULE_DIALOG_OPTION, null);

            return RULE_DIALOG_OPTION;
        }
    }
}
