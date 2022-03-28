package extrasystemreloaded.integration.indevo;

import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.CargoAPI;
import com.fs.starfarer.api.campaign.OptionPanelAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.ids.Submarkets;
import extrasystemreloaded.systems.upgrades.Upgrade;
import extrasystemreloaded.systems.upgrades.methods.UpgradeMethod;
import extrasystemreloaded.util.ExtraSystems;
import extrasystemreloaded.util.StringUtils;

public class ShipComponentUpgradeMethod implements UpgradeMethod {
    private static final String OPTION = "ESShipExtraUpgradeApplyIndEvoComponents";

    @Override
    public String getOptionId() {
        return OPTION;
    }

    @Override
    public void addOption(OptionPanelAPI options, FleetMemberAPI fm, ExtraSystems es, Upgrade upgrade, MarketAPI market) {
        int level = es.getUpgrade(upgrade);

        String option = StringUtils.getTranslation("UpgradeMethods", "IndEvoComponentsOption")
                .format("components", IndEvoUtil.getUpgradeShipComponentPrice(fm, upgrade, level))
                .toString();

        String tooltip = StringUtils.getTranslation("UpgradeMethods", "IndEvoComponentsTooltip")
                .format("components", getTotalComponents(fm.getFleetData().getFleet(), market))
                .toString();

        options.addOption(
                option,
                getOptionId(),
                tooltip
        );

        if(!canUse(fm, es, upgrade, market)) {
            options.setEnabled(getOptionId(), false);
        }
    }

    @Override
    public boolean canUse(FleetMemberAPI fm, ExtraSystems es, Upgrade upgrade, MarketAPI market) {
        int level = es.getUpgrade(upgrade);
        int upgradeCost = IndEvoUtil.getUpgradeShipComponentPrice(fm, upgrade, level);
        int totalComponents = getTotalComponents(fm.getFleetData().getFleet(), market);

        return (totalComponents - upgradeCost) > 0;
    }

    @Override
    public boolean canShow(FleetMemberAPI fm, ExtraSystems es, Upgrade upgrade, MarketAPI market) {
        return true;
    }

    @Override
    public void apply(FleetMemberAPI fm, ExtraSystems es, Upgrade upgrade, MarketAPI market) {
        int level = es.getUpgrade(upgrade);
        int upgradeCost = IndEvoUtil.getUpgradeShipComponentPrice(fm, upgrade, level);
        int totalComponents = getTotalComponents(fm.getFleetData().getFleet(), market);

        if (market != null
                && market.getSubmarket(Submarkets.SUBMARKET_STORAGE) != null
                && market.getSubmarket(Submarkets.SUBMARKET_STORAGE).getCargo() != null) {

            CargoAPI storageCargo = market.getSubmarket(Submarkets.SUBMARKET_STORAGE).getCargo();

            upgradeCost = removeCommodityAndReturnRemainingCost(storageCargo, IndEvoUtil.SHIP_COMPONENT_ITEM_ID, upgradeCost);
        }

        CargoAPI fleetCargo = fm.getFleetData().getFleet().getCargo();
        if(upgradeCost > 0) {
            removeCommodity(fleetCargo, IndEvoUtil.SHIP_COMPONENT_ITEM_ID, upgradeCost);
        }
        es.putUpgrade(upgrade);
    }

    private Integer getTotalComponents(CampaignFleetAPI fleet, MarketAPI market) {
        return getComponentsFromFleetForUpgrade(fleet) + getComponentsFromStorageForUpgrade(market);
    }

    private int getComponentsFromFleetForUpgrade(CampaignFleetAPI fleet) {
        return Math.round(fleet.getCargo().getCommodityQuantity(IndEvoUtil.SHIP_COMPONENT_ITEM_ID));
    }

    private int getComponentsFromStorageForUpgrade(MarketAPI market) {
        int result = 0;

        boolean hasStorage = false;
        if (market != null
                && market.getSubmarket(Submarkets.SUBMARKET_STORAGE) != null
                && market.getSubmarket(Submarkets.SUBMARKET_STORAGE).getCargo() != null) {
            result = Math.round(market.getSubmarket(Submarkets.SUBMARKET_STORAGE).getCargo().getCommodityQuantity(IndEvoUtil.SHIP_COMPONENT_ITEM_ID));
        }

        return result;
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