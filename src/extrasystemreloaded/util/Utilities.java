package extrasystemreloaded.util;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.CargoStackAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.ids.Submarkets;

import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Utilities {
    private Utilities() {
    }

    public static final Map<Integer, String> RESOURCE_NAME = new HashMap<>();
    static{
        RESOURCE_NAME.put(0, Global.getSettings().getString("ResourceName", "supplies"));
        RESOURCE_NAME.put(1, Global.getSettings().getString("ResourceName", "volatiles"));
        RESOURCE_NAME.put(2, Global.getSettings().getString("ResourceName", "organics"));
        RESOURCE_NAME.put(3, Global.getSettings().getString("ResourceName", "hand_weapons"));
        RESOURCE_NAME.put(4, Global.getSettings().getString("ResourceName", "metals"));
        RESOURCE_NAME.put(5, Global.getSettings().getString("ResourceName", "rare_metals"));
        RESOURCE_NAME.put(6, Global.getSettings().getString("ResourceName", "heavy_machinery"));
    }

    public static class TextTip {
        public static String Congratulation = "Congratulation, the upgrade succeeded.";
        public static String Failure = "The upgrade failed.";
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

    public static float[] getFleetCargoMap(CampaignFleetAPI fleet, MarketAPI market) {
        List<CargoStackAPI> cargosList = fleet.getCargo().getStacksCopy();

        float[] playerCargo = getCargoFromStacks(cargosList);

        //add cargo from submarket if it exists
        if (market != null && market.getSubmarket(Submarkets.SUBMARKET_STORAGE) != null && market.getSubmarket(Submarkets.SUBMARKET_STORAGE).getCargo() != null) {
            cargosList = market.getSubmarket(Submarkets.SUBMARKET_STORAGE).getCargo().getStacksCopy();

            int i = 0;
            for(float cargo : getCargoFromStacks(cargosList)) {
                playerCargo[i] += cargo;
                i++;
            }
        }

        return playerCargo;
    }

    private static float[] getCargoFromStacks(List<CargoStackAPI> cargoStackAPIList) {
        float supplies = 0;
        float volatiles = 0;
        float organics = 0;
        float hand_weapons = 0;
        float metals = 0;
        float rare_metals = 0;
        float heavy_machinery = 0;


        for (CargoStackAPI cargoStackAPI : cargoStackAPIList) {
            String id = cargoStackAPI.getCommodityId();
            if (id == null) {
                continue;
            }
            switch (id) {
                case "supplies":
                    supplies += cargoStackAPI.getSize();
                    break;
                case "volatiles":
                    volatiles += cargoStackAPI.getSize();
                    break;
                case "organics":
                    organics += cargoStackAPI.getSize();
                    break;
                case "hand_weapons":
                    hand_weapons += cargoStackAPI.getSize();
                    break;
                case "metals":
                    metals += cargoStackAPI.getSize();
                    break;
                case "rare_metals":
                    rare_metals += cargoStackAPI.getSize();
                    break;
                case "heavy_machinery":
                    heavy_machinery += cargoStackAPI.getSize();
                    break;
                default:
                    break;
            }
        }

        return new float[]{supplies, volatiles, organics, hand_weapons, metals, rare_metals, heavy_machinery};
    }

    public static String getQualityName(float arg){
        String text;
        if (isInside(arg, 0.5f, 0.65f)) {
            text = Global.getSettings().getString("QualityName", "inferior");
        }else if (isInside(arg, 0.65f, 0.8f)) {
            text = Global.getSettings().getString("QualityName", "rough");
        }else if (isInside(arg, 0.8f, 0.95f)) {
            text = Global.getSettings().getString("QualityName", "crude");
        }else if (isInside(arg, 0.95f, 1.33f)) {
            text = Global.getSettings().getString("QualityName", "normal");
        }else if (isInside(arg, 1.33f, 1.66f)) {
            text = Global.getSettings().getString("QualityName", "good");
        }else if (isInside(arg, 1.66f, 2.1f)) {
            text = Global.getSettings().getString("QualityName", "superior");
        }else if (isInside(arg, 2.1f, 2.5f)) {
            text = Global.getSettings().getString("QualityName", "perfect");
        }else if (arg < 3f) {
            text = Global.getSettings().getString("QualityName", "s_perfect");
        } else
            text = Global.getSettings().getString("QualityName", "domain");

        return text;
    }

    public static Color getQualityColor(float arg){
        Color color;
        if (isInside(arg, 0.5f, 0.65f)) {
            color = Color.gray.darker();
        }else if (isInside(arg, 0.65f, 0.8f)) {
            color = Color.gray;
        }else if (isInside(arg, 0.8f, 0.95f)) {
            color = Color.lightGray;
        }else if (isInside(arg, 0.95f, 1.1f)) {
            color = Color.white;
        }else if (isInside(arg, 1.1f, 1.25f)) {
            color = Color.green;
        }else if (isInside(arg, 1.25f, 1.4f)) {
            color = new Color(0,155,255);
        }else if (isInside(arg, 1.4f, 1.5f)) {
            color = Color.orange;
        }else {
            color = Color.CYAN;
        }
        return color;
    }

    public static boolean isInside(float arg, float a, float b) {
        return (arg >= a && arg < b);
    }

    public static float diminishingReturns(float level, float scalar, float cap) {
        return cap * (level / (level + scalar));
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

}
