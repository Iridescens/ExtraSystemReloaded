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
import extrasystemreloaded.systems.bandwidth.Bandwidth;
import extrasystemreloaded.systems.bandwidth.BandwidthUtil;
import extrasystemreloaded.util.ExtraSystems;
import extrasystemreloaded.util.StringUtils;

public class Es_ShipBandwidthDialog extends ESDialog {
    public static final String RULE_MENUSTATE = "ESBandwidth";
    public static final String RULE_DIALOG_OPTION = "ESShipBandwidthPicked";
    private static final float baseBandwidthStep = 5f;

    @Override
    protected void process(ESDialogContext context, TextPanelAPI textPanel, OptionPanelAPI options, VisualPanelAPI visual) {
        FleetMemberAPI selectedShip = context.getSelectedShip();

        if (selectedShip != null) {
            showBandwidthOptions(context, textPanel, options, visual);
        }

        ESRuleUtils.addReturnOptions(options, selectedShip, false);
    }

    private static void showBandwidthOptions(ESDialogContext context, TextPanelAPI textPanel, OptionPanelAPI options, VisualPanelAPI visual) {
        String functionType = context.getFunctionType();
        CampaignFleetAPI playerFleet = context.getPlayerFleet();
        FleetMemberAPI fm = context.getSelectedShip();
        ExtraSystems es = context.getBuff();
        float shipBandwidth = context.getShipBandwidth();
        float upgradeBandwidthMult = getMarketBandwidthMult(context.getCurrMarket());
        float estimatedOverhaulCost = getBandwidthUpgradePrice(fm, shipBandwidth, upgradeBandwidthMult);

        switch (functionType) {
            case "BandwidthUpgradeSelected":
                if (fm != null) {

                    String shipBandwidthText = BandwidthUtil.getFormattedBandwidthWithName(shipBandwidth);
                    String usedBandwidthText = BandwidthUtil.getFormattedBandwidth(es.getUsedBandwidth());
                    StringUtils.getTranslation("BandwidthDialog", "BandwidthForShip")
                            .format("bandwidth", shipBandwidthText)
                            .format("bandwidthUsedByUpgrades", usedBandwidthText)
                            .addToTextPanel(textPanel, Bandwidth.getBandwidthColor(shipBandwidth), Misc.getHighlightColor());

                    String bonusBandwidth = BandwidthUtil.getRoundedBandwidth(baseBandwidthStep * upgradeBandwidthMult);
                    StringUtils.getTranslation("BandwidthDialog","BandwidthUpgradeForShip")
                            .format("bonusBandwidth", bonusBandwidth)
                            .addToTextPanel(textPanel);

                    String needCredits = Misc.getFormat().format(estimatedOverhaulCost);
                    StringUtils.getTranslation("BandwidthDialog","CostCreditsToUpgrade")
                            .format("credits", needCredits)
                            .addToTextPanel(textPanel);

                    options.addOption(
                            StringUtils.getString("BandwidthDialog","ConfirmPurchaseUpgrade"),
                            "ESShipBandwidthApply", null);

                    if (!isAbleToPayForBandwidthUpgrade(context.getPlayerFleet(), estimatedOverhaulCost)) {
                        options.setTooltip("ESShipBandwidthApply", StringUtils.getString("BandwidthDialog","ConfirmInsufficientCredits"));
                        options.setEnabled("ESShipBandwidthApply", false);
                    } else if (!canUpgrade(es, fm)) {
                        options.setTooltip("ESShipBandwidthApply", StringUtils.getString("BandwidthDialog","ConfirmBandwidthTooHigh"));
                        options.setEnabled("ESShipBandwidthApply", false);
                    }
                }
                break;
            case "ApplyBandwidthUpgrade":
                if (fm != null) {
                    float bonusBandwidth = baseBandwidthStep * upgradeBandwidthMult;
                    float newBandwidth = Math.round((shipBandwidth + bonusBandwidth) * 1000f) / 1000f; // bandwidthFactor + bonus
                    newBandwidth = Math.min(newBandwidth, ESModSettings.getFloat(ESModSettings.MAX_BANDWIDTH));

                    es.putBandwidth(newBandwidth);
                    es.save(fm);
                    ExtraSystemHM.addToFleetMember(fm);

                    if(!Es_ModPlugin.isDebugUpgradeCosts()) {
                        playerFleet.getCargo().getCredits().subtract(estimatedOverhaulCost);
                    }

                    String newBandwidthString = BandwidthUtil.getFormattedBandwidthWithName(newBandwidth);
                    StringUtils.getTranslation("BandwidthDialog","UpgradedBandwidthForShip")
                            .format("bandwidth", newBandwidthString)
                            .addToTextPanel(textPanel, Misc.getPositiveHighlightColor(), Bandwidth.getBandwidthColor(newBandwidth));

                    if(es.canUpgradeBandwidth(fm)) {

                        bonusBandwidth = Math.min(bonusBandwidth, ESModSettings.getFloat(ESModSettings.MAX_BANDWIDTH) - newBandwidth);
                        estimatedOverhaulCost = getBandwidthUpgradePrice(fm, newBandwidth, upgradeBandwidthMult);

                        String needCredits = Misc.getFormat().format(estimatedOverhaulCost);
                        String bonusBandwidthString = BandwidthUtil.getRoundedBandwidth(bonusBandwidth);

                        StringUtils.getTranslation("BandwidthDialog","AnotherBandwidthUpgradeForShip")
                                .format("credits", needCredits)
                                .format("bonusBandwidth", bonusBandwidthString)
                                .addToTextPanel(textPanel);

                        options.addOption(StringUtils.getString("BandwidthDialog","ConfirmPurchaseUpgrade"), "ESShipBandwidthApply");
                        if (!isAbleToPayForBandwidthUpgrade(context.getPlayerFleet(), estimatedOverhaulCost)) {
                            options.setEnabled("ESShipBandwidthApply", false);
                            options.setTooltip("ESShipBandwidthApply", StringUtils.getString("BandwidthDialog","ConfirmInsufficientCredits"));
                        }
                    } else {
                        textPanel.addParagraph(
                                StringUtils.getString("BandwidthDialog","UpgradedToMaxBandwidthShip"));

                        options.addOption(
                                StringUtils.getString("BandwidthDialog","ConfirmPurchaseUpgrade"),
                                "DoNothing",
                                StringUtils.getString("BandwidthDialog","ConfirmBandwidthTooHigh"));
                        options.setEnabled("DoNothing", false);
                    }
                }
                break;
            default:
                break;
        }
    }

    private static boolean canUpgrade(ExtraSystems buff, FleetMemberAPI selectedShip) {
        return buff == null || buff.canUpgradeBandwidth(selectedShip);
    }

    private static boolean isAbleToPayForBandwidthUpgrade(CampaignFleetAPI fleet, float cost) {
        return cost <= fleet.getCargo().getCredits().get() || Es_ModPlugin.isDebugUpgradeCosts();
    }

    private static float getMarketBandwidthMult(MarketAPI currMarket) {
        float bandwidthMult = 1;
        if(currMarket.hasIndustry("heavyindustry")) {
            bandwidthMult+=0.5;
        }
        if(currMarket.hasIndustry("orbitalworks")) {
            bandwidthMult+=1;
        }
        if(currMarket.hasIndustry("IndEvo_Scrapyard")) {
            bandwidthMult+=0.25;
        }
        if(currMarket.hasIndustry("IndEvo_EngHub")) {
            bandwidthMult+=0.25;
        }
        if(currMarket.hasIndustry("ms_modularFac")) {
            bandwidthMult+=0.25;
        }
        if(currMarket.hasIndustry("ms_massIndustry")) {
            bandwidthMult+=0.25;
        }
        if(currMarket.hasIndustry("ms_militaryProduction")) {
            bandwidthMult+=0.5;
        }
        if(currMarket.hasIndustry("ms_orbitalShipyard")) {
            bandwidthMult+=1;
        }

        return bandwidthMult;
    }

    private static float getBandwidthUpgradePrice(FleetMemberAPI selectedShip, float shipBandwidth, float upgradeBandwidthMult) {
        float shipBaseValue = Math.min(Math.min(selectedShip.getBaseDeployCost(), 1) * 40000, selectedShip.getBaseValue());
        float bandwidthMultFactor = 1 - (upgradeBandwidthMult / (upgradeBandwidthMult + 10));

        return Math.round(shipBaseValue * (float) Math.pow(shipBandwidth / 10f, 2) / (2f + 5f * bandwidthMultFactor) * 100f) / 100f;
    }

    public static class BandwidthOption extends Es_ShipDialog.ShipOption {
        public BandwidthOption(int order) {
            super(order);
        }

        @Override
        public Object addOption(OptionPanelAPI options, FleetMemberAPI fm, ExtraSystems es, MarketAPI market) {
            options.addOption(StringUtils.getString("BandwidthDialog", "OpenBandwidthOptions"), RULE_DIALOG_OPTION, null);
            if(!es.canUpgradeBandwidth(fm)) {
                options.setEnabled(RULE_DIALOG_OPTION, false);
                options.setTooltip(RULE_DIALOG_OPTION, StringUtils.getString("BandwidthDialog", "BandwidthFullyUpgraded"));
            }

            return RULE_DIALOG_OPTION;
        }

        @Override
        public void modifyTextPanel(TextPanelAPI textPanel, FleetMemberAPI fm, ExtraSystems es, MarketAPI market) {
            float shipBandwidth = es.getBandwidth(fm);

            String shipBandwidthText = BandwidthUtil.getFormattedBandwidthWithName(shipBandwidth);
            String usedBandwidthText = BandwidthUtil.getFormattedBandwidth(es.getUsedBandwidth());
            StringUtils.getTranslation("BandwidthDialog", "BandwidthForShip")
                    .format("bandwidth", shipBandwidthText)
                    .format("bandwidthUsedByUpgrades", usedBandwidthText)
                    .addToTextPanel(textPanel, Bandwidth.getBandwidthColor(shipBandwidth), Misc.getHighlightColor());

            String bandwidthBonusFromMarket = (int) (getMarketBandwidthMult(market) * 100f) + "%";

            StringUtils.getTranslation("BandwidthDialog", "BandwidthBonusFromMarket")
                    .format("bonus", bandwidthBonusFromMarket)
                    .addToTextPanel(textPanel);
        }
    }
}
