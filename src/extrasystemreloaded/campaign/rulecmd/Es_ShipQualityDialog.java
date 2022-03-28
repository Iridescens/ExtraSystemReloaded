package extrasystemreloaded.campaign.rulecmd;

import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.OptionPanelAPI;
import com.fs.starfarer.api.campaign.TextPanelAPI;
import com.fs.starfarer.api.campaign.VisualPanelAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.util.Misc;
import extrasystemreloaded.ESModSettings;
import extrasystemreloaded.Es_ModPlugin;
import extrasystemreloaded.campaign.ESDialog;
import extrasystemreloaded.campaign.ESDialogContext;
import extrasystemreloaded.campaign.ESRuleUtils;
import extrasystemreloaded.hullmods.ExtraSystemHM;
import extrasystemreloaded.systems.quality.QualityUtil;
import extrasystemreloaded.util.ExtraSystems;
import extrasystemreloaded.util.StringUtils;

import java.awt.*;

import static extrasystemreloaded.systems.quality.QualityUtil.getQualityColor;

public class Es_ShipQualityDialog extends ESDialog {
    public static final String RULE_MENUSTATE = "ESQuality";
    public static final String RULE_DIALOG_OPTION = "ESShipQualityPicked";
    private static final float baseQualityStep = 0.05f;

    @Override
    protected void process(ESDialogContext context, TextPanelAPI textPanel, OptionPanelAPI options, VisualPanelAPI visual) {
        FleetMemberAPI selectedShip = context.getSelectedShip();

        if (selectedShip != null) {
            showQualityOptions(context, textPanel, options, visual);
        }

        ESRuleUtils.addReturnOptions(options, selectedShip, false);
    }

    private static void showQualityOptions(ESDialogContext context, TextPanelAPI textPanel, OptionPanelAPI options, VisualPanelAPI visual) {
        String functionType = context.getFunctionType();
        CampaignFleetAPI playerFleet = context.getPlayerFleet();
        FleetMemberAPI selectedShip = context.getSelectedShip();
        ExtraSystems buff = context.getBuff();
        float shipQuality = context.getShipQuality();
        float upgradeQualityMult = getMarketQualityMult(context.getCurrMarket());
        float estimatedOverhaulCost = getQualityUpgradePrice(selectedShip, shipQuality, upgradeQualityMult);

        switch (functionType) {
            case "QualityUpgradeSelected":
                if (selectedShip != null) {

                    String shipQualityText = QualityUtil.getQualityString(shipQuality);
                    textPanel.addParagraph(
                            StringUtils.getTranslation("QualityDialog","QualityForShip")
                                    .format("quality", shipQualityText)
                                    .toString()
                    );
                    textPanel.highlightLastInLastPara(shipQualityText, QualityUtil.getQualityColor(shipQuality));

                    String bonusQuality = QualityUtil.getRoundedQuality(baseQualityStep * upgradeQualityMult);
                    textPanel.addParagraph(
                            StringUtils.getTranslation("QualityDialog","QualityUpgradeForShip")
                                    .format("bonusQuality", bonusQuality)
                                    .toString()
                    );
                    textPanel.highlightLastInLastPara(bonusQuality, Color.green);

                    String needCredits = Misc.getFormat().format(estimatedOverhaulCost);
                    textPanel.addParagraph(
                            StringUtils.getTranslation("QualityDialog","CostCreditsToUpgrade")
                                    .format("credits", needCredits)
                                    .toString()
                    );
                    textPanel.highlightLastInLastPara(needCredits, Color.yellow);

                    options.addOption(
                            StringUtils.getString("QualityDialog","ConfirmPurchaseUpgrade"),
                            "ESShipQualityApply", null);

                    if (!isAbleToPayForQualityUpgrade(context.getPlayerFleet(), estimatedOverhaulCost)) {
                        options.setTooltip("ESShipQualityApply", StringUtils.getString("QualityDialog","ConfirmInsufficientCredits"));
                        options.setEnabled("ESShipQualityApply", false);
                    } else if (!canUpgrade(buff, selectedShip)) {
                        options.setTooltip("ESShipQualityApply", StringUtils.getString("QualityDialog","ConfirmQualityTooHigh"));
                        options.setEnabled("ESShipQualityApply", false);
                    }
                }
                break;
            case "ApplyQualityUpgrade":
                if (selectedShip != null) {
                    float bonusQuality = baseQualityStep * upgradeQualityMult;
                    float newQuality = Math.round((shipQuality + bonusQuality) * 1000f) / 1000f; // qualityFactor + bonus
                    newQuality = Math.min(newQuality, ESModSettings.getFloat(ESModSettings.MAX_QUALITY));

                    buff.putQuality(newQuality);
                    buff.save(selectedShip);
                    ExtraSystemHM.addToFleetMember(selectedShip);

                    if(!Es_ModPlugin.isDebugUpgradeCosts()) {
                        playerFleet.getCargo().getCredits().subtract(estimatedOverhaulCost);
                    }

                    String newQualityString = QualityUtil.getQualityString(newQuality);
                    textPanel.addParagraph(
                            StringUtils.getTranslation("QualityDialog","UpgradedQualityForShip")
                                    .format("quality", newQualityString)
                                    .toString()
                    );
                    textPanel.highlightLastInLastPara(newQualityString, QualityUtil.getQualityColor(newQuality));

                    if(buff.canUpgradeQuality(selectedShip)) {

                        bonusQuality = Math.min(bonusQuality, ESModSettings.getFloat(ESModSettings.MAX_QUALITY) - newQuality);
                        estimatedOverhaulCost = getQualityUpgradePrice(selectedShip, newQuality, upgradeQualityMult);

                        String needCredits = Misc.getFormat().format(estimatedOverhaulCost);
                        String bonusQualityString = QualityUtil.getRoundedQuality(bonusQuality);

                        textPanel.addParagraph(
                                StringUtils.getTranslation("QualityDialog","AnotherQualityUpgradeForShip")
                                        .format("credits", needCredits)
                                        .format("bonusQuality", bonusQualityString)
                                        .toString()
                        );
                        textPanel.highlightInLastPara(Color.yellow, bonusQualityString, needCredits);

                        options.addOption(StringUtils.getString("QualityDialog","ConfirmPurchaseUpgrade"), "ESShipQualityApply");
                        if (!isAbleToPayForQualityUpgrade(context.getPlayerFleet(), estimatedOverhaulCost)) {
                            options.setEnabled("ESShipQualityApply", false);
                            options.setTooltip("ESShipQualityApply", StringUtils.getString("QualityDialog","ConfirmInsufficientCredits"));
                        }
                    } else {
                        textPanel.addParagraph(
                                StringUtils.getString("QualityDialog","UpgradedToMaxQualityShip"));

                        options.addOption(
                                StringUtils.getString("QualityDialog","ConfirmPurchaseUpgrade"),
                                "DoNothing",
                                StringUtils.getString("QualityDialog","ConfirmQualityTooHigh"));
                        options.setEnabled("DoNothing", false);
                    }
                }
                break;
            default:
                break;
        }
    }

    private static boolean canUpgrade(ExtraSystems buff, FleetMemberAPI selectedShip) {
        return buff == null || buff.canUpgradeQuality(selectedShip);
    }

    private static boolean isAbleToPayForQualityUpgrade(CampaignFleetAPI fleet, float cost) {
        return cost <= fleet.getCargo().getCredits().get() || Es_ModPlugin.isDebugUpgradeCosts();
    }

    private static float getMarketQualityMult(MarketAPI currMarket) {
        float qualityMult = 1;
        if(currMarket.hasIndustry("heavyindustry")) {
            qualityMult+=0.5;
        }
        if(currMarket.hasIndustry("orbitalworks")) {
            qualityMult+=1;
        }
        if(currMarket.hasIndustry("IndEvo_Scrapyard")) {
            qualityMult+=0.25;
        }
        if(currMarket.hasIndustry("IndEvo_EngHub")) {
            qualityMult+=0.25;
        }
        if(currMarket.hasIndustry("ms_modularFac")) {
            qualityMult+=0.25;
        }
        if(currMarket.hasIndustry("ms_massIndustry")) {
            qualityMult+=0.25;
        }
        if(currMarket.hasIndustry("ms_militaryProduction")) {
            qualityMult+=0.5;
        }
        if(currMarket.hasIndustry("ms_orbitalShipyard")) {
            qualityMult+=1;
        }

        return qualityMult;
    }

    private static float getQualityUpgradePrice(FleetMemberAPI selectedShip, float shipQuality, float upgradeQualityMult) {
        float shipBaseValue = Math.min(Math.min(selectedShip.getBaseDeployCost(), 1) * 40000, selectedShip.getBaseValue());
        return Math.round(shipBaseValue * (float) Math.pow(shipQuality, 2) / (2f + 0.25f * (upgradeQualityMult - 1)) * 100f) / 100f;
    }

    public static class QualityOption extends Es_ShipDialog.ShipOption {
        public QualityOption(int order) {
            super(order);
        }

        @Override
        public Object addOption(OptionPanelAPI options, FleetMemberAPI fm, ExtraSystems es, MarketAPI market) {
            options.addOption(StringUtils.getString("QualityDialog", "OpenQualityOptions"), RULE_DIALOG_OPTION, null);
            if(!es.canUpgradeQuality(fm)) {
                options.setEnabled(RULE_DIALOG_OPTION, false);
                options.setTooltip(RULE_DIALOG_OPTION, StringUtils.getString("QualityDialog", "QualityFullyUpgraded"));
            }

            return RULE_DIALOG_OPTION;
        }

        @Override
        public void modifyTextPanel(TextPanelAPI textPanel, FleetMemberAPI fm, ExtraSystems es, MarketAPI market) {
            float shipQuality = es.getQuality(fm);

            String shipQualityText = QualityUtil.getQualityString(shipQuality);
            textPanel.addParagraph(
                    StringUtils.getTranslation("QualityDialog", "QualityForShip")
                        .format("quality", shipQualityText)
                        .toString());
            textPanel.highlightLastInLastPara(shipQualityText, getQualityColor(shipQuality));

            String qualityBonusFromMarket = QualityUtil.getRoundedQuality(getMarketQualityMult(market));
            textPanel.addParagraph(StringUtils.getTranslation("QualityDialog", "QualityBonusFromMarket")
                    .format("bonus", qualityBonusFromMarket)
                    .toString());
            textPanel.highlightInLastPara(qualityBonusFromMarket);
        }
    }
}
