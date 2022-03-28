package extrasystemreloaded.campaign.rulecmd;

import com.fs.starfarer.api.campaign.OptionPanelAPI;
import com.fs.starfarer.api.campaign.TextPanelAPI;
import com.fs.starfarer.api.campaign.VisualPanelAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import extrasystemreloaded.campaign.ESDialog;
import extrasystemreloaded.campaign.ESDialogContext;
import extrasystemreloaded.campaign.ESRuleUtils;
import extrasystemreloaded.util.ExtraSystems;
import extrasystemreloaded.util.StringUtils;
import org.lwjgl.input.Keyboard;

import java.awt.Color;
import java.util.List;
import java.util.ArrayList;

public class Es_ShipDialog extends ESDialog {
    public static final String RULE_MENUSTATE = "ESShipPicked";
    public static final String RULE_DIALOG_OPTION = "ESShipPicked";
    public static final List<ShipOption> SHIP_OPTIONS = new ArrayList<>();

    public static void addShipOption(ShipOption option) {
        SHIP_OPTIONS.add(option.getOrder(), option);
    }

    public static Object addReturnOption(OptionPanelAPI options) {
        options.addOption(StringUtils.getString("ShipDialog", "BackToShip"), RULE_DIALOG_OPTION);
        return RULE_DIALOG_OPTION;
    }

    public static Object addReturnOptionWithShortcut(OptionPanelAPI options) {
        Object option = addReturnOption(options);
        options.setShortcut(option, Keyboard.KEY_ESCAPE, false, false, false, true);
        return RULE_DIALOG_OPTION;
    }

    @Override
    protected void process(ESDialogContext context, TextPanelAPI textPanel, OptionPanelAPI options, VisualPanelAPI visual) {
        FleetMemberAPI selectedShip = context.getSelectedShip();
        if (selectedShip != null) {
            ExtraSystems buff = context.getBuff();
            MarketAPI market = context.getCurrMarket();

            visual.showFleetMemberInfo(selectedShip);

            for(ShipOption option : SHIP_OPTIONS) {
                if(option.shouldShow(selectedShip, buff, market)) {
                    option.modifyTextPanel(textPanel, selectedShip, buff, market);
                }
            }

            textPanel.addParagraph("-----------------------", Color.gray);
            textPanel.addParagraph(StringUtils.getString("ShipDialog", "OpenedDialog"));

            for(ShipOption option : SHIP_OPTIONS) {
                if(option.shouldShow(selectedShip, buff, market)) {
                    option.addOption(options, selectedShip, buff, market);
                }
            }
        }

        ESRuleUtils.addReturnOptions(options, null, false);
    }

    public static abstract class ShipOption {
        protected final int order;

        public ShipOption(int order) {
            this.order = order;
        }

        /**
         * order of the option in dialog. lower comes first
         * @return order
         */
        protected int getOrder() {
            return order;
        }

        protected boolean shouldShow(FleetMemberAPI fm, ExtraSystems es, MarketAPI market) {
            return true;
        }

        /**
         * adds option to ship dialog option list
         * @param options options panel
         * @param fm fleet member to upgrade
         * @param es the upgrades object
         * @param market the market
         * @return the object used to make the option
         */
        protected abstract Object addOption(OptionPanelAPI options, FleetMemberAPI fm, ExtraSystems es, MarketAPI market);

        /**
         * modifies the text panel to include some text
         * @param textPanel
         * @param fm
         * @param es
         * @param market
         */
        protected void modifyTextPanel(TextPanelAPI textPanel, FleetMemberAPI fm, ExtraSystems es, MarketAPI market) {
            //donothing
        }
    }
}
