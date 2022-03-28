package extrasystemreloaded.util;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.CargoStackAPI;
import java.util.*;
import java.util.List;

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
