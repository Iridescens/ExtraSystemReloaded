package extrasystemreloaded.util;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.CargoStackAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.ids.Submarkets;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Utilities {

    private Utilities() {
    }

    public static final List<String> RESOURCES_LIST = new ArrayList<String>() {{
        add("supplies");
        add("volatiles");
        add("organics");
        add("hand_weapons");
        add("metals");
        add("rare_metals");
        add("heavy_machinery");
    }};

    public static class TextTip {
        public static String Congratulation = "Your chief engineer reports that the upgrade was a success.";
        public static String Failure = "After a few expletives, your chief engineer reports that the upgrade was a failure.";
        public static String resourceHeader = "Resources available:";
        public static String costHeader = "Upgrade cost: ";
        public static String tooExpensivePrefix = "   (";
        public static String tooExpensiveSuffix = " in shortage.)";
        public static String quality1 = "The quality of your ship:";
        public static String chooseUpgrade = "Please choose an upgrade:";
        public static String ability2 = "This upgrade cannot be performed any more on this ship.";
        public static String ability3 = "You have ";
        public static String ability4 = " chance of success.";
    }

    public static class OptionName {
        public static String Leave = Global.getSettings().getString("Options", "leave");
        public static String Back = Global.getSettings().getString("Options", "back");
        public static String Confirm = Global.getSettings().getString("Options", "confirm");
        public static String Cancel = Global.getSettings().getString("Options", "cancel");
        public static String PreviousP = Global.getSettings().getString("Options", "previouspage");
        public static String NextP = Global.getSettings().getString("Options", "nextpage");
        public static String Repurchase = Global.getSettings().getString("Options", "repurchase");
    }

    public static boolean isInside(float arg, float a, float b) {
        return (arg >= a && arg < b);
    }

    public static void removePlayerSpecialItem(String id)
    {
        CampaignFleetAPI playerFleet = Global.getSector().getPlayerFleet();
        if (playerFleet == null)
            return;
        List<CargoStackAPI> playerCargoStacks = playerFleet.getCargo().getStacksCopy();

        for (CargoStackAPI cargoStack : playerCargoStacks) {
            if (cargoStack.isSpecialStack() && cargoStack.getSpecialDataIfSpecial().getId().equals(id)) {
                cargoStack.subtract(1);
                if (cargoStack.getSize() <= 0)
                    playerFleet.getCargo().removeStack(cargoStack);
                return;
            }
        }
    }

    public static boolean playerHasSpecialItem(String id)
    {
        CampaignFleetAPI playerFleet = Global.getSector().getPlayerFleet();
        if (playerFleet == null)
            return false;
        List<CargoStackAPI> playerCargoStacks = playerFleet.getCargo().getStacksCopy();

        for (CargoStackAPI cargoStack : playerCargoStacks) {
            if (cargoStack.isSpecialStack() && cargoStack.getSpecialDataIfSpecial().getId().equals(id) && cargoStack.getSize() > 0) {
                return true;
            }
        }

        return false;
    }

    public static void removePlayerCommodity(String id)
    {
        CampaignFleetAPI playerFleet = Global.getSector().getPlayerFleet();
        if (playerFleet == null)
            return;
        List<CargoStackAPI> playerCargoStacks = playerFleet.getCargo().getStacksCopy();

        for (CargoStackAPI cargoStack : playerCargoStacks) {
            if (cargoStack.isCommodityStack() && cargoStack.getCommodityId().equals(id)) {
                cargoStack.subtract(1);
                if (cargoStack.getSize() <= 0)
                    playerFleet.getCargo().removeStack(cargoStack);
                return;
            }
        }
    }

    public static boolean playerHasCommodity(String id)
    {
        CampaignFleetAPI playerFleet = Global.getSector().getPlayerFleet();
        if (playerFleet == null)
            return false;
        List<CargoStackAPI> playerCargoStacks = playerFleet.getCargo().getStacksCopy();

        for (CargoStackAPI cargoStack : playerCargoStacks) {
            if (cargoStack.isCommodityStack() && cargoStack.getCommodityId().equals(id) && cargoStack.getSize() > 0)
                return true;
        }

        return false;
    }
}
