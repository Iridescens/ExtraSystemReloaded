package extrasystemreloaded.campaign.rulecmd;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.ids.Submarkets;
import extrasystemreloaded.Es_ModPlugin;
import extrasystemreloaded.campaign.ESDialog;
import extrasystemreloaded.campaign.ESDialogContext;
import extrasystemreloaded.campaign.Es_ShipLevelFleetData;
import extrasystemreloaded.util.upgrades.ESUpgrades;
import extrasystemreloaded.util.upgrades.Upgrade;
import extrasystemreloaded.util.upgrades.Upgrades;

import java.awt.*;

import static extrasystemreloaded.util.Utilities.*;
import static extrasystemreloaded.util.upgrades.Upgrades.getUpgradeCosts;

public class Es_ShipUpgradeDialog extends ESDialog {
    public static final String RULE_MENUSTATE = "ESUpgrades";
    public static final String RULE_DIALOG_OPTION = "ESShipExtraUpgradesPicked";
    private static final int NumUpgradesPerPage = 5;

    private static final String FUNCTIONTYPE_UPGRADES = "ExtraUpgradesSelected";
    private static final String FUNCTIONTYPE_CHANGEDPAGE = "ChangedPage";
    private static final String OPTION_NEXTPAGE = "ESShipExtraUpgradesNEXT";
    private static final String OPTION_PREVPAGE = "ESShipExtraUpgradesPREV";

    private static final String FUNCTIONTYPE_CONFIRM = "ConfirmExtraUpgrade";

    private static final String OPTION_APPLY = "ESShipExtraUpgradeApply";
    private static final String FUNCTIONTYPE_APPLY = "ApplyExtraUpgrade";

    @Override
    protected void process(ESDialogContext context, TextPanelAPI textPanel, OptionPanelAPI options, VisualPanelAPI visual) {
        options.clearOptions();

        FleetMemberAPI selectedShip = context.getSelectedShip();

        if (selectedShip != null) {
            showUpgradeOptions(context, textPanel, options, visual);

            options.addOption("Back to ship", Es_ShipDialog.RULE_DIALOG_OPTION);
        }

        options.addOption("Back to ship list", Es_ShipListDialog.RULE_DIALOG_OPTION);
        options.addOption("Back to main menu", ESDialog.RULE_DIALOG_OPTION);
    }

    private void showUpgradeOptions(ESDialogContext context, TextPanelAPI textPanel, OptionPanelAPI options, VisualPanelAPI visual) {

        String functionType = context.getFunctionType();
        FleetMemberAPI selectedShip = context.getSelectedShip();
        Upgrade selectedUpgrade = context.getSelectedUpgrade();

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
                    options.addOption("Back to upgrades", RULE_DIALOG_OPTION);
                }
                break;
            case FUNCTIONTYPE_APPLY:
                if (selectedShip != null && selectedUpgrade != null) {
                    doAbilityPurchase(context, textPanel, options, visual);
                    populateAbilityPurchasedOptions(context, textPanel, options, visual);
                    options.addOption("Back to upgrades", RULE_DIALOG_OPTION);
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
            textPanel.addParagraph(TextTip.resourceHeader, Color.green);
            textPanel.addParagraph("-----------------------", Color.gray);
            for (int i = 0; i < 7; i++) {
                String name = RESOURCE_NAME.get(i);
                textPanel.addParagraph(name + ":" + (int) getFleetCargoMap(playerFleet, currMarket)[i]);
            }
            textPanel.addParagraph("-----------------------", Color.gray);

            textPanel.addParagraph(TextTip.quality1);

            float quality = buff.getExtraSystems().getQuality(shipSelected);
            String text = Math.round(quality * 100f) / 100f + getQualityName(quality);
            textPanel.appendToLastParagraph(text);
            textPanel.highlightLastInLastPara(text, getQualityColor(quality));
            textPanel.addParagraph(TextTip.chooseUpgrade);
        }

        for (int i = upgradePageIndex * 5; i < Math.min(upgradePageIndex * 5 + 5, Upgrades.UPGRADES_LIST.size()); i++) {
            Upgrade upgrade = Upgrades.UPGRADES_LIST.get(i);
            int level = buff.getExtraSystems().getUpgrade(upgrade.getKey());
            ShipAPI.HullSize hullSize = shipSelected.getHullSpec().getHullSize();
            int max = upgrade.getMaxLevel() != -1 ? upgrade.getMaxLevel() : ESUpgrades.HULLSIZE_TO_MAXLEVEL.get(hullSize);

            String tooltip = buff.getCanUpgradeWithImpossibleTooltip(upgrade, currMarket);
            if(tooltip == null) {
                options.addOption(upgrade.getName() + " (" + level + " / " + max + ")", upgrade.getKey(), upgrade.getDescription());
            } else {
                options.addOption(upgrade.getName() + " (" + level + " / " + max + ")", upgrade.getKey(), tooltip);
                options.setEnabled(upgrade.getKey(), false);
            }
        }

        options.addOption("Previous page", OPTION_PREVPAGE);
        options.addOption("Next page", OPTION_NEXTPAGE);
        if (upgradePageIndex == 0) {
            options.setEnabled(OPTION_PREVPAGE, false);
        }
        if (upgradePageIndex * NumUpgradesPerPage + 5 >= Upgrades.UPGRADES_LIST.size()) {
            options.setEnabled(OPTION_NEXTPAGE, false);
        }
    }

    private void populateAbilityPurchaseConfirmationOptions(ESDialogContext context, TextPanelAPI textPanel, OptionPanelAPI options, VisualPanelAPI visual) {
        MarketAPI currMarket = context.getCurrMarket();
        FleetMemberAPI shipSelected = context.getSelectedShip();
        Es_ShipLevelFleetData buff = context.getBuff();
        Upgrade abilitySelected = context.getSelectedUpgrade();

        if (shipSelected != null && abilitySelected != null) {
            ShipAPI.HullSize hullSize = shipSelected.getHullSpec().getHullSize();
            int max = abilitySelected.getMaxLevel(hullSize);
            int level = buff.getExtraSystems().getUpgrade(abilitySelected);

            options.addOption(
                    String.format(OptionName.Confirm, level, max),
                    OPTION_APPLY,
                    buff.getCanUpgradeWithImpossibleTooltip(abilitySelected, currMarket)
            );
            if (level >= max) {
                textPanel.addParagraph(TextTip.ability2 + "(" + level + ")", Color.yellow);
                options.setEnabled(OPTION_APPLY, false);
            } else {
                boolean isCanLevelUp = appendUpgradeCostText(context, textPanel, options, visual, level, max);

                if (!isCanLevelUp) {
                    options.setEnabled(OPTION_APPLY, false);
                }
            }
        }
    }

    private void populateAbilityPurchasedOptions(ESDialogContext context, TextPanelAPI textPanel, OptionPanelAPI options, VisualPanelAPI visual) {
        FleetMemberAPI shipSelected = context.getSelectedShip();
        Es_ShipLevelFleetData buff = context.getBuff();
        Upgrade abilitySelected = context.getSelectedUpgrade();

        if (shipSelected != null && abilitySelected != null) {
            ShipAPI.HullSize hullSize = shipSelected.getHullSpec().getHullSize();
            int max = abilitySelected.getMaxLevel(hullSize);
            int level = buff.getExtraSystems().getUpgrade(abilitySelected.getKey());
            textPanel.addParagraph(abilitySelected.getName() + "(" + level + " / " + max + ")", Color.yellow);

            options.addOption(OptionName.Repurchase  + " (" + level + " / " + max + ")", OPTION_APPLY);

            if (level >= max) {
                textPanel.addParagraph(TextTip.ability2 + "(" + level + ")", Color.yellow);
                options.setEnabled(OPTION_APPLY, false);
            } else {
                boolean isCanLevelUp = appendUpgradeCostText(context, textPanel, options, visual, level, max);
                if (!isCanLevelUp) {
                    options.setEnabled(OPTION_APPLY, false);
                }
            }
        }
    }

    private void doAbilityPurchase(ESDialogContext context, TextPanelAPI textPanel, OptionPanelAPI options, VisualPanelAPI visual) {
        MarketAPI currMarket = context.getCurrMarket();
        FleetMemberAPI selectedShip = context.getSelectedShip();
        Es_ShipLevelFleetData buff = context.getBuff();
        Upgrade abilitySelected = context.getSelectedUpgrade();
        CampaignFleetAPI playerFleet = context.getPlayerFleet();

        ShipAPI.HullSize hullSize = selectedShip.getHullSpec().getHullSize();
        int max = abilitySelected.getMaxLevel(hullSize);
        int currentLevel = buff.getExtraSystems().getUpgrade(abilitySelected);

        boolean success = true;
        if(!Es_ModPlugin.isUpgradeAlwaysSucceed() && currentLevel!=0 ) {
            float possibility = (float) Math.cos(Math.PI*currentLevel*0.5f/max)*(1f-Es_ModPlugin.getUpgradeFailureMinChance())+Es_ModPlugin.getUpgradeFailureMinChance();
            if ((float)Math.random()>=possibility) {
                success = false;
            }
        }

        if(success) {
            buff.getExtraSystems().putUpgrade(abilitySelected);
            buff.save();

            Global.getSoundPlayer().playUISound("ui_char_increase_skill_new", 1f, 1f);
            textPanel.addParagraph(TextTip.Congratulation, Color.yellow);
        } else {
            textPanel.addParagraph(TextTip.Failure, Color.red);
        }

        if (!Es_ModPlugin.isDebugUpgradeCosts()) {
            float[] resourceCosts = getUpgradeCosts(selectedShip, abilitySelected, currentLevel, buff.getExtraSystems().getQuality(selectedShip));

            if (currMarket != null
                    && currMarket.getSubmarket(Submarkets.SUBMARKET_STORAGE) != null
                    && currMarket.getSubmarket(Submarkets.SUBMARKET_STORAGE).getCargo() != null) {

                CargoAPI storageCargo = currMarket.getSubmarket(Submarkets.SUBMARKET_STORAGE).getCargo();

                resourceCosts[0] = removeCommodityAndReturnRemainingCost(storageCargo, "supplies", resourceCosts[0]);
                resourceCosts[1] = removeCommodityAndReturnRemainingCost(storageCargo, "volatiles", resourceCosts[1]);
                resourceCosts[2] = removeCommodityAndReturnRemainingCost(storageCargo, "organics", resourceCosts[2]);
                resourceCosts[3] = removeCommodityAndReturnRemainingCost(storageCargo, "hand_weapons", resourceCosts[3]);
                resourceCosts[4] = removeCommodityAndReturnRemainingCost(storageCargo, "metals", resourceCosts[4]);
                resourceCosts[5] = removeCommodityAndReturnRemainingCost(storageCargo, "rare_metals", resourceCosts[5]);
                resourceCosts[6] = removeCommodityAndReturnRemainingCost(storageCargo, "heavy_machinery", resourceCosts[6]);
            }

            CargoAPI playerCargo = playerFleet.getCargo();
            removeCommodity(playerCargo, "supplies", resourceCosts[0]);
            removeCommodity(playerCargo, "volatiles", resourceCosts[1]);
            removeCommodity(playerCargo, "organics", resourceCosts[2]);
            removeCommodity(playerCargo, "hand_weapons", resourceCosts[3]);
            removeCommodity(playerCargo, "metals", resourceCosts[4]);
            removeCommodity(playerCargo, "rare_metals", resourceCosts[5]);
            removeCommodity(playerCargo, "heavy_machinery", resourceCosts[6]);
        }
    }

    private boolean appendUpgradeCostText(ESDialogContext context, TextPanelAPI textPanel, OptionPanelAPI options, VisualPanelAPI visual, int level, int max) {
        MarketAPI currMarket = context.getCurrMarket();
        FleetMemberAPI shipSelected = context.getSelectedShip();
        Es_ShipLevelFleetData buff = context.getBuff();
        Upgrade abilitySelected = context.getSelectedUpgrade();
        CampaignFleetAPI playerFleet = context.getPlayerFleet();

        int currentLevel = buff.getExtraSystems().getUpgrade(abilitySelected);

        boolean isCanLevelUp = true;
        if (!Es_ModPlugin.isDebugUpgradeCosts()) {
            float[] resourceCosts = getUpgradeCosts(shipSelected, abilitySelected, currentLevel, buff.getExtraSystems().getQuality(shipSelected));

            float possibility = 1f;
            if (!Es_ModPlugin.isUpgradeAlwaysSucceed() && level != 0) {
                possibility = (float) Math.cos(Math.PI * level * 0.5f / max) * (1f - Es_ModPlugin.getUpgradeFailureMinChance()) + Es_ModPlugin.getUpgradeFailureMinChance();
            }

            textPanel.addParagraph(TextTip.costHeader, Color.green);
            textPanel.addParagraph("-----------------------", Color.gray);

            for (int i = 0; i < resourceCosts.length; ++i) {
                String name = RESOURCE_NAME.get(i);
                float fleetcargo = getFleetCargoMap(playerFleet, currMarket)[i];
                if (resourceCosts[i] > fleetcargo) {
                    isCanLevelUp = false;
                    String suffix = TextTip.tooExpensivePrefix + (int) (resourceCosts[i] - fleetcargo) + TextTip.tooExpensiveSuffix;
                    textPanel.addParagraph(name + ":" + resourceCosts[i] + suffix, Color.red);
                } else {
                    textPanel.addParagraph(name + ":" + resourceCosts[i]);
                }
            }
            textPanel.addParagraph("-----------------------", Color.gray);
            textPanel.addParagraph(TextTip.ability3);
            String text1 = Math.round(possibility * 1000f) / 10f + "%";
            textPanel.appendToLastParagraph(text1);
            textPanel.highlightLastInLastPara(text1, Color.green);
            textPanel.appendToLastParagraph(TextTip.ability4);
        }

        return isCanLevelUp;
    }

    private void removeCommodity(CargoAPI cargo, String id, float cost) {
        cargo.removeCommodity(id, cost);
    }

    private float removeCommodityAndReturnRemainingCost(CargoAPI cargo, String id, float cost) {
        float current = cargo.getCommodityQuantity(id);
        float taken = Math.min(current, cost);
        cargo.removeCommodity(id, taken);
        return cost - taken;
    }
}
