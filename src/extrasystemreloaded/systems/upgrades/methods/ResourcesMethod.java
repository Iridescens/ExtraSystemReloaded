package extrasystemreloaded.systems.upgrades.methods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.CargoAPI;
import com.fs.starfarer.api.campaign.OptionPanelAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.ids.Submarkets;
import extrasystemreloaded.systems.upgrades.Upgrade;
import extrasystemreloaded.util.ExtraSystems;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class ResourcesMethod implements UpgradeMethod {
    private static final String OPTION = "ESShipExtraUpgradeApplyResources";

    @Override
    public String getOptionId() {
        return OPTION;
    }

    @Override
    public void addOption(OptionPanelAPI options, FleetMemberAPI fm, ExtraSystems es, Upgrade upgrade, MarketAPI market) {
        int level = es.getUpgrade(upgrade);
        String tooltip = getTooltip(fm, market, upgrade.getResourceCosts(fm, es.getUpgrade(upgrade)));

        options.addOption(
                "Craft with resources (see tooltip)",
                getOptionId(),
                tooltip
        );

        if(!canUse(fm, es, upgrade, market)) {
            options.setEnabled(getOptionId(), false);
        }
    }

    @Override
    public boolean canUse(FleetMemberAPI fm, ExtraSystems es, Upgrade upgrade, MarketAPI market) {
        Map<String, Integer> upgradeCosts = upgrade.getResourceCosts(fm, es.getUpgrade(upgrade));
        Map<String, Integer> totalStacks = getTotalResources(fm.getFleetData().getFleet(), market, upgradeCosts.keySet());

        boolean canUpgrade = true;
        for (Map.Entry<String, Integer> upgradeCost : upgradeCosts.entrySet()) {
            int remaining = totalStacks.get(upgradeCost.getKey()) - upgradeCost.getValue();
            if (remaining < 0) {
                canUpgrade = false;
                remaining = 0;
            }
            totalStacks.put(upgradeCost.getKey(), remaining);
        }

        return canUpgrade;
    }

    @Override
    public boolean canShow(FleetMemberAPI fm, ExtraSystems es, Upgrade upgrade, MarketAPI market) {
        return true;
    }

    @Override
    public void apply(FleetMemberAPI fm, ExtraSystems es, Upgrade upgrade, MarketAPI market) {
        Map<String, Integer> upgradeCosts = upgrade.getResourceCosts(fm, es.getUpgrade(upgrade));

        if (market != null
                && market.getSubmarket(Submarkets.SUBMARKET_STORAGE) != null
                && market.getSubmarket(Submarkets.SUBMARKET_STORAGE).getCargo() != null) {

            CargoAPI storageCargo = market.getSubmarket(Submarkets.SUBMARKET_STORAGE).getCargo();

            for (Map.Entry<String, Integer> upgradeCost : upgradeCosts.entrySet()) {
                int remaining = removeCommodityAndReturnRemainingCost(storageCargo, upgradeCost.getKey(), upgradeCost.getValue());
                upgradeCosts.put(upgradeCost.getKey(), remaining);
            }
        }

        CargoAPI fleetCargo = fm.getFleetData().getFleet().getCargo();
        for (Map.Entry<String, Integer> upgradeCost : upgradeCosts.entrySet()) {

            if (upgradeCost.getValue() <= 0) {
                continue;
            }

            int remaining = removeCommodityAndReturnRemainingCost(fleetCargo, upgradeCost.getKey(), upgradeCost.getValue());
            upgradeCosts.put(upgradeCost.getKey(), remaining);
        }

        es.putUpgrade(upgrade);
    }

    private String getTooltip(FleetMemberAPI fm, MarketAPI market, Map<String, Integer> resourceCosts) {
        StringBuilder sb = new StringBuilder();
        sb.append("This upgrade requires:");

        Map<String, Integer> totalResources = getTotalResources(fm.getFleetData().getFleet(), market, resourceCosts.keySet());
        for (Map.Entry<String, Integer> resource : resourceCosts.entrySet()) {
            sb.append("\n");
            sb.append(resource.getValue());
            sb.append(" ");
            sb.append(Global.getSector().getEconomy().getCommoditySpec(resource.getKey()).getName());

            sb.append(" (");
            sb.append(totalResources.get(resource.getKey()));
            sb.append(")");
        }
        sb.append("\nThe amount you have is displayed after the name of each resource. This includes market storage!");

        return sb.toString();
    }

    private Map<String, Integer> getTotalResources(CampaignFleetAPI fleet, MarketAPI market, Set<String> resources) {
        Map<String, Integer> finalStacks = getResourcesFromFleetForUpgrade(fleet, resources);

        for(Map.Entry<String, Integer> marketStack : getResourcesFromStorageForUpgrade(market, resources).entrySet()) {
            finalStacks.put(marketStack.getKey(), finalStacks.get(marketStack.getKey()) + marketStack.getValue());
        }

        return finalStacks;
    }

    private Map<String, Integer> getResourcesFromFleetForUpgrade(CampaignFleetAPI fleet, Set<String> resources) {
        Map<String, Integer> stacks = new HashMap<>();

        for (String commodityId : resources) {
            stacks.put(commodityId, Math.round(fleet.getCargo().getCommodityQuantity(commodityId)));
        }

        return stacks;
    }

    private Map<String, Integer> getResourcesFromStorageForUpgrade(MarketAPI market, Set<String> resources) {
        Map<String, Integer> stacks = new HashMap<>();

        boolean hasStorage = false;
        if (market != null
                && market.getSubmarket(Submarkets.SUBMARKET_STORAGE) != null
                && market.getSubmarket(Submarkets.SUBMARKET_STORAGE).getCargo() != null) {
            hasStorage = true;
        }

        for (String commodityId : resources) {
            int stack = 0;
            if(hasStorage) {
                stack = Math.round(market.getSubmarket(Submarkets.SUBMARKET_STORAGE).getCargo().getCommodityQuantity(commodityId));
            }
            stacks.put(commodityId, stack);
        }

        return stacks;
    }

    private void removeCommodity(CargoAPI cargo, String id, float cost) {
        cargo.removeCommodity(id, cost);
    }

    private int removeCommodityAndReturnRemainingCost(CargoAPI cargo, String id, float cost) {
        float current = cargo.getCommodityQuantity(id);
        float taken = Math.min(current, cost);
        cargo.removeCommodity(id, taken);
        return (int) (cost - taken);
    }
}
