package extrasystemreloaded.integration.indevo;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import extrasystemreloaded.systems.upgrades.Upgrade;
import extrasystemreloaded.systems.upgrades.UpgradesHandler;

import java.util.Map;

public class IndEvoUtil {
    public static final String SHIP_COMPONENT_ITEM_ID = "IndEvo_parts";
    public static final String RELIC_COMPONENT_ITEM_ID = "IndEvo_rare_parts";

    public static void loadIntegration() {
        UpgradesHandler.UPGRADE_METHODS.add(new ShipComponentUpgradeMethod());
        UpgradesHandler.UPGRADE_METHODS.add(new RelicComponentUpgradeMethod());
    }

    public static int getUpgradeShipComponentPrice(FleetMemberAPI shipSelected, Upgrade abilitySelected, int level) {
        int creditCost = getCreditCostForResources(abilitySelected.getResourceCosts(shipSelected, level));
        float componentValue = Global.getSector().getEconomy().getCommoditySpec(SHIP_COMPONENT_ITEM_ID).getBasePrice();

        return Math.max(1, Math.round(creditCost / (componentValue * 1.125f)));
    }

    public static int getUpgradeRelicComponentPrice(FleetMemberAPI shipSelected, Upgrade abilitySelected, int level) {
        int creditCost = getCreditCostForResources(abilitySelected.getResourceCosts(shipSelected, level));
        float componentValue = Global.getSector().getEconomy().getCommoditySpec(RELIC_COMPONENT_ITEM_ID).getBasePrice();

        return Math.max(1, Math.round(creditCost / (componentValue * 1.375f)));
    }

    /**
     * Sums up the floats in the map.
     * @param resourceCosts
     * @return The sum.
     */
    private static int getCreditCostForResources(Map<String, Integer> resourceCosts) {
        float creditCost = 0;

        for(Map.Entry<String, Integer> resourceCost : resourceCosts.entrySet()) {
            creditCost += resourceCost.getValue();
        }
        return (int) creditCost;
    }
}
