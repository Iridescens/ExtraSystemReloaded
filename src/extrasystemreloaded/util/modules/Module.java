package extrasystemreloaded.util.modules;

import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import extrasystemreloaded.campaign.Es_ShipLevelFleetData;

public abstract class Module {
    public abstract String getKey();

    public abstract String getName();

    public abstract String getDescription();

    public abstract String getTooltip();

    public String getTextDescription() {
        return getDescription() + "\n\n" + getTooltip();
    }

    public String getBuffId() {
        return Es_ShipLevelFleetData.Es_LEVEL_FUNCTION_ID + "_" + getKey();
    }

    public abstract boolean canApply(CampaignFleetAPI fleet, FleetMemberAPI fm);

    public abstract String getUnableToApplyTooltip(CampaignFleetAPI fleet, FleetMemberAPI fm);

    public abstract boolean removeItemsFromFleet(CampaignFleetAPI fleet, FleetMemberAPI fm);

    public void applyUpgradeToStats(FleetMemberAPI fm, MutableShipStatsAPI stats, float quality, String id) {

    }

    public void applyAfterShipCreation(FleetMemberAPI fm, ShipAPI ship, float quality, String id) {

    }

    public void advanceInCombat(ShipAPI ship, float amount, float quality) {

    }

    public abstract void modifyToolTip(TooltipMakerAPI tooltip, FleetMemberAPI fm, Es_ShipLevelFleetData buff);
}
