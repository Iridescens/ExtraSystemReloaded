package extrasystemreloaded.systems.upgrades.methods;

import com.fs.starfarer.api.campaign.OptionPanelAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import extrasystemreloaded.systems.upgrades.Upgrade;
import extrasystemreloaded.util.ExtraSystems;

/**
 * An UpgradeMethod represents a way for an upgrade to be attained through the upgrade dialog.
 */
public interface UpgradeMethod {
    /**
     * The Option ID used in the addOption method. This is so the mod can correctly apply the upgrade method.
     * @return the Option ID.
     */
    public String getOptionId();

    /**
     * Add the choice to use this method of upgrading to the Options panel.
     * This method is responsible for disabling the option as well.
     * @param options the Options panel that can be added to
     * @param fm fleet member to be upgraded
     * @param es systems object
     * @param market market
     * @param upgrade
     */
    public void addOption(OptionPanelAPI options, FleetMemberAPI fm, ExtraSystems es, Upgrade upgrade, MarketAPI market);

    /**
     * Whether this upgrade method can be used.
     * @param fm fleet member to be upgraded
     * @param es systems object
     * @param market market
     * @return whether the upgrade method can be used
     */
    public boolean canUse(FleetMemberAPI fm, ExtraSystems es, Upgrade upgrade, MarketAPI market);

    /**
     * Whether to show this upgrade method.
     * @param fm fleet member to be upgraded
     * @param es systems object
     * @param market market
     * @return whether the upgrade method can be shown
     */
    public boolean canShow(FleetMemberAPI fm, ExtraSystems es, Upgrade upgrade, MarketAPI market);

    /**
     * Applies the upgrade to the ship. This should also take the price away from the player.
     * @param fm fleet member to be upgraded
     * @param es systems object
     * @param market market
     */
    public void apply(FleetMemberAPI fm, ExtraSystems es, Upgrade upgrade, MarketAPI market);
}
