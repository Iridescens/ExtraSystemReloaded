package extrasystemreloaded.systems.augments;

import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import extrasystemreloaded.util.ExtraSystems;
import org.json.JSONException;
import org.json.JSONObject;

import java.awt.Color;

public abstract class Augment {
    public abstract String getKey();

    public abstract String getName();

    public abstract Color getMainColor();

    public abstract String getDescription();

    public abstract String getTooltip();

    public String getTextDescription() {
        return getDescription() + "\n\n" + getTooltip();
    }

    public abstract void loadConfig(JSONObject augmentSettings) throws JSONException;

    public String getBuffId() {
        return "ESR_" + getKey();
    }

    public abstract boolean canApply(CampaignFleetAPI fleet, FleetMemberAPI fm);

    public abstract String getUnableToApplyTooltip(CampaignFleetAPI fleet, FleetMemberAPI fm);

    public abstract boolean removeItemsFromFleet(CampaignFleetAPI fleet, FleetMemberAPI fm);

    public boolean shouldLoad() {
        return true;
    }

    public boolean shouldHide() {
        return false;
    }

    public void initialize() {
        AugmentsHandler.addAugment(this);
    }

    public void applyAugmentToStats(FleetMemberAPI fm, MutableShipStatsAPI stats, float quality, String id) {

    }

    public void applyAugmentToShip(FleetMemberAPI fm, ShipAPI ship, float quality, String id) {

    }

    public void advanceInCombat(ShipAPI ship, float amount, float quality) {

    }

    public abstract void modifyToolTip(TooltipMakerAPI tooltip, FleetMemberAPI fm, ExtraSystems systems, boolean expand);
}
