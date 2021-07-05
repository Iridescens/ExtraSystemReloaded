package extrasystemreloaded.campaign.rulecmd;

import com.fs.starfarer.api.campaign.OptionPanelAPI;
import com.fs.starfarer.api.campaign.TextPanelAPI;
import com.fs.starfarer.api.campaign.VisualPanelAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import extrasystemreloaded.campaign.ESDialog;
import extrasystemreloaded.campaign.ESDialogContext;
import extrasystemreloaded.campaign.Es_ShipLevelFleetData;
import extrasystemreloaded.util.Utilities;
import extrasystemreloaded.util.upgrades.ESUpgrades;
import extrasystemreloaded.util.upgrades.Upgrade;
import extrasystemreloaded.util.upgrades.Upgrades;

import java.awt.*;

import static extrasystemreloaded.util.Utilities.getQualityColor;
import static extrasystemreloaded.util.Utilities.getQualityName;

public class Es_ShipDialog extends ESDialog {
    public static final String RULE_MENUSTATE = "ESShipPicked";
    public static final String RULE_DIALOG_OPTION = "ESShipPicked";

    @Override
    protected void process(ESDialogContext context, TextPanelAPI textPanel, OptionPanelAPI options, VisualPanelAPI visual) {
        options.clearOptions();

        FleetMemberAPI selectedShip = context.getSelectedShip();
        if (selectedShip != null) {
            Es_ShipLevelFleetData buff = context.getBuff();
            float shipQuality = context.getShipQuality();

            visual.showFleetMemberInfo(selectedShip);

            textPanel.addParagraph(Utilities.TextTip.quality1);
            String shipQualityText = "" + Math.round(shipQuality * 1000f) / 1000f + getQualityName(shipQuality);
            textPanel.appendToLastParagraph(" " + shipQualityText);
            textPanel.highlightLastInLastPara(shipQualityText, getQualityColor(shipQuality));

            for (Upgrade upgrade : Upgrades.UPGRADES_LIST) {
                int level = buff.getExtraSystems().getUpgrade(upgrade.getKey());
                ShipAPI.HullSize hullSize = selectedShip.getHullSpec().getHullSize();
                int max = upgrade.getMaxLevel() != -1 ? upgrade.getMaxLevel() : ESUpgrades.HULLSIZE_TO_MAXLEVEL.get(hullSize);

                textPanel.addParagraph(upgrade.getName() + " (" + level + " / " + max + ")");
            }

            textPanel.addParagraph("-----------------------", Color.gray);
            textPanel.addParagraph("Pick an operation");

            options.addOption("Quality", Es_ShipQualityDialog.RULE_DIALOG_OPTION, null);
            if(!buff.getExtraSystems().canUpgradeQuality(selectedShip)) {
                options.setEnabled(Es_ShipQualityDialog.RULE_DIALOG_OPTION, false);
                options.setTooltip(Es_ShipQualityDialog.RULE_DIALOG_OPTION, "The quality of this ship has reached its peak.");
            }

            String cantUpgrade = buff.getCanUpgradeWithImpossibleTooltip();
            options.addOption("Upgrades", Es_ShipUpgradeDialog.RULE_DIALOG_OPTION, cantUpgrade);
            if(cantUpgrade != null) {
                options.setEnabled(Es_ShipUpgradeDialog.RULE_DIALOG_OPTION, false);
            }

            options.addOption("Modules", Es_ShipModulesDialog.RULE_DIALOG_OPTION, null);
        }

        options.addOption("Back to ship list", Es_ShipListDialog.RULE_DIALOG_OPTION);
        options.addOption("Back to main menu", ESDialog.RULE_DIALOG_OPTION);
    }
}
