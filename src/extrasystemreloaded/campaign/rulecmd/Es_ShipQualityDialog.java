package extrasystemreloaded.campaign.rulecmd;

import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.OptionPanelAPI;
import com.fs.starfarer.api.campaign.TextPanelAPI;
import com.fs.starfarer.api.campaign.VisualPanelAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.util.Misc;
import extrasystemreloaded.Es_ModPlugin;
import extrasystemreloaded.campaign.ESDialog;
import extrasystemreloaded.campaign.ESDialogContext;
import extrasystemreloaded.hullmods.ExtraSystemHM;
import extrasystemreloaded.util.ExtraSystems;

import java.awt.*;

import static extrasystemreloaded.util.Utilities.*;

public class Es_ShipQualityDialog extends ESDialog {
    public static final String RULE_MENUSTATE = "ESQuality";
    public static final String RULE_DIALOG_OPTION = "ESShipQualityPicked";
    private static final float baseQualityStep = 0.025f;

    @Override
    protected void process(ESDialogContext context, TextPanelAPI textPanel, OptionPanelAPI options, VisualPanelAPI visual) {
        options.clearOptions();

        FleetMemberAPI selectedShip = context.getSelectedShip();

        if (selectedShip != null) {
            showQualityOptions(context, textPanel, options, visual);

            options.addOption("Back to ship", Es_ShipDialog.RULE_DIALOG_OPTION);
        }

        options.addOption("Back to ship list", Es_ShipListDialog.RULE_DIALOG_OPTION);
        options.addOption("Back to main menu", ESDialog.RULE_DIALOG_OPTION);
    }

    private void showQualityOptions(ESDialogContext context, TextPanelAPI textPanel, OptionPanelAPI options, VisualPanelAPI visual) {

        String functionType = context.getFunctionType();
        CampaignFleetAPI playerFleet = context.getPlayerFleet();
        FleetMemberAPI selectedShip = context.getSelectedShip();
        ExtraSystems buff = context.getBuff();
        float shipQuality = context.getShipQuality();
        float shipBaseValue = context.getShipBaseValue();
        float estimatedOverhaulCost = Math.round(shipBaseValue * (float) Math.pow(shipQuality, 2) / 2 * 100f) / 100f;

        switch (functionType) {
            case "QualityUpgradeSelected":
                if (selectedShip != null) {
                    float bonusQuality = bonusQualityAtMarket(context.getCurrMarket());
                    textPanel.addParagraph(TextTip.quality1);
                    String shipQualityText = "" + Math.round(shipQuality * 1000f) / 1000f + getQualityName(shipQuality);
                    textPanel.appendToLastParagraph(" " + shipQualityText);
                    textPanel.highlightLastInLastPara(shipQualityText, getQualityColor(shipQuality));
                    shipQualityText = "Local industrial facilities are capable of improving overall quality rating of ships by " + bonusQuality;
                    textPanel.addParagraph(shipQualityText);
                    textPanel.highlightLastInLastPara("" + bonusQuality, Color.green);

                    String needCredits = Misc.getFormat().format(estimatedOverhaulCost);
                    shipQualityText = "On-site team estimates ship's overhaul to cost " + needCredits + " credits";
                    textPanel.addParagraph(shipQualityText);
                    textPanel.highlightLastInLastPara(needCredits, Color.green);

                    options.addOption("Agree to conditions", "ESShipQualityApply", null);
                    options.setTooltip("ESShipQualityApply", "Proceed with overhaul");

                    if (!isAbleToPayForQualityUpgrade(context.getPlayerFleet(), estimatedOverhaulCost)) {
                        options.setEnabled("ESShipQualityApply", false);
                        options.setTooltip("ESShipQualityApply", "Insufficient credits");
                    } else if (!canUpgrade(buff, selectedShip)) {
                        options.setEnabled("ESShipQualityApply", false);
                        options.setTooltip("ESShipQualityApply", "The quality of this ship has reached its peak.");
                    }
                }
                break;
            case "ApplyQualityUpgrade":
                if (selectedShip != null) {
                    float bonusQuality = bonusQualityAtMarket(context.getCurrMarket());
                    float newQuality = Math.round((shipQuality + bonusQuality) * 1000f) / 1000f; // qualityFactor + bonus
                    newQuality = Math.min(newQuality, Es_ModPlugin.getMaxQuality());

                    buff.putQuality(newQuality);
                    buff.save(selectedShip);
                    ExtraSystemHM.addToFleetMember(selectedShip);

                    if(!Es_ModPlugin.isDebugUpgradeCosts()) {
                        playerFleet.getCargo().getCredits().subtract(estimatedOverhaulCost);
                    }

                    String text2 = "After some improvements here and there, your ship now has quality rating of " + newQuality;
                    textPanel.addParagraph(text2);
                    textPanel.highlightLastInLastPara("" + newQuality, getQualityColor(newQuality));

                    if(buff.canUpgradeQuality(selectedShip)) {
                        estimatedOverhaulCost = Math.round(shipBaseValue * (float) Math.pow(newQuality, 2) / 2 * 100f) / 100f;

                        String shipQualityText = "On-site team estimates that another overhaul would cost %s credits for a quality increase of %s.";
                        String needCredits = Misc.getFormat().format(estimatedOverhaulCost);
                        textPanel.addParagraph(String.format(shipQualityText, estimatedOverhaulCost, bonusQuality));
                        textPanel.highlightLastInLastPara(needCredits, Color.yellow);
                        textPanel.highlightLastInLastPara(String.valueOf(bonusQuality), Color.green);

                        options.addOption(OptionName.Repurchase, "ESShipQualityApply");
                        if (isAbleToPayForQualityUpgrade(context.getPlayerFleet(), estimatedOverhaulCost)) {
                            options.setTooltip("ESShipQualityApply", "Proceed with overhaul");
                        } else {
                            options.setEnabled("ESShipQualityApply", false);
                            options.setTooltip("ESShipQualityApply", "Insufficient credits");
                        }
                    } else {
                        options.addOption("The quality of this ship has reached its peak.", "DoNothing");
                        options.setEnabled("DoNothing", false);
                    }
                }
                break;
            default:
                break;
        }
    }

    private boolean canUpgrade(ExtraSystems buff, FleetMemberAPI selectedShip) {
        return buff == null || buff.canUpgradeQuality(selectedShip);
    }

    private boolean isAbleToPayForQualityUpgrade(CampaignFleetAPI fleet, float cost) {
        return cost <= fleet.getCargo().getCredits().get() || Es_ModPlugin.isDebugUpgradeCosts();
    }

    private float bonusQualityAtMarket(MarketAPI currMarket) {
        float qualityMult = 2;
        if(currMarket.hasIndustry("heavyindustry")) {
            qualityMult+=1;
        }
        if(currMarket.hasIndustry("orbitalworks")) {
            qualityMult+=2;
        }
        if(currMarket.hasIndustry("IndEvo_Scrapyard")) {
            qualityMult+=0.5;
        }
        if(currMarket.hasIndustry("IndEvo_EngHub")) {
            qualityMult+=0.5;
        }
        if(currMarket.hasIndustry("ms_modularFac")) {
            qualityMult+=0.5;
        }
        if(currMarket.hasIndustry("ms_massIndustry")) {
            qualityMult+=0.5;
        }
        if(currMarket.hasIndustry("ms_militaryProduction")) {
            qualityMult+=1;
        }
        if(currMarket.hasIndustry("ms_orbitalShipyard")) {
            qualityMult+=2;
        }

        return baseQualityStep * (2 + (currMarket.hasIndustry("heavyindustry") ? 1 : 0) + (currMarket.hasIndustry("orbitalworks") ? 2 : 0));
    }
}
