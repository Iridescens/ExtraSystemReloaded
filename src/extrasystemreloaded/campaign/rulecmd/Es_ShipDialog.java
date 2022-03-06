package extrasystemreloaded.campaign.rulecmd;

import com.fs.starfarer.api.campaign.OptionPanelAPI;
import com.fs.starfarer.api.campaign.TextPanelAPI;
import com.fs.starfarer.api.campaign.VisualPanelAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import extrasystemreloaded.campaign.ESDialog;
import extrasystemreloaded.campaign.ESDialogContext;
import extrasystemreloaded.util.ExtraSystems;
import extrasystemreloaded.util.Utilities;
import extrasystemreloaded.systems.upgrades.Upgrade;
import extrasystemreloaded.systems.upgrades.UpgradesHandler;

import java.awt.*;

import static extrasystemreloaded.systems.quality.QualityUtil.getQualityColor;
import static extrasystemreloaded.systems.quality.QualityUtil.getQualityName;

public class Es_ShipDialog extends ESDialog {
    public static final String RULE_MENUSTATE = "ESShipPicked";
    public static final String RULE_DIALOG_OPTION = "ESShipPicked";

    @Override
    protected void process(ESDialogContext context, TextPanelAPI textPanel, OptionPanelAPI options, VisualPanelAPI visual) {
        options.clearOptions();

        FleetMemberAPI selectedShip = context.getSelectedShip();
        if (selectedShip != null) {
            ExtraSystems buff = context.getBuff();
            float shipQuality = context.getShipQuality();

            visual.showFleetMemberInfo(selectedShip);

            textPanel.addParagraph(Utilities.TextTip.quality1);
            String shipQualityText = "" + Math.round(shipQuality * 1000f) / 1000f + getQualityName(shipQuality);
            textPanel.appendToLastParagraph(" " + shipQualityText);
            textPanel.highlightLastInLastPara(shipQualityText, getQualityColor(shipQuality));
            textPanel.addParagraph("-----------------------", Color.gray);
            textPanel.addParagraph("Pick an operation");

            options.addOption("Quality", Es_ShipQualityDialog.RULE_DIALOG_OPTION, null);
            if(!buff.canUpgradeQuality(selectedShip)) {
                options.setEnabled(Es_ShipQualityDialog.RULE_DIALOG_OPTION, false);
                options.setTooltip(Es_ShipQualityDialog.RULE_DIALOG_OPTION, "The quality of this ship has reached its peak.");
            }

            options.addOption("Upgrades", Es_ShipUpgradeDialog.RULE_DIALOG_OPTION, null);
            options.addOption("Augments", Es_ShipAugmentsDialog.RULE_DIALOG_OPTION, null);
        }

        options.addOption("Back to ship list", Es_ShipListDialog.RULE_DIALOG_OPTION);
        options.addOption("Back to main menu", ESDialog.RULE_DIALOG_OPTION);
    }
}
