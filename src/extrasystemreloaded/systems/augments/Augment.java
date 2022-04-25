package extrasystemreloaded.systems.augments;

import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import extrasystemreloaded.campaign.rulecmd.ESInteractionDialogPlugin;
import extrasystemreloaded.util.ExtraSystems;
import lombok.Getter;
import lombok.Setter;
import org.json.JSONException;
import org.json.JSONObject;

import java.awt.*;
import java.util.Map;

public abstract class Augment {
    @Getter
    @Setter
    protected String key;
    @Getter
    @Setter
    protected String name;
    @Getter
    @Setter
    protected String description;
    @Getter
    @Setter
    protected String tooltip;
    @Getter
    protected JSONObject augmentSettings;

    public abstract Color getMainColor();

    public String getTextDescription() {
        return getDescription() + "\n\n" + getTooltip();
    }

    public void setConfig(JSONObject augmentSettings) throws JSONException {
        this.augmentSettings = augmentSettings;
        loadConfig();
    }

    protected void loadConfig() throws JSONException {
    }

    ;

    public String getBuffId() {
        return "ESR_" + getKey();
    }

    public abstract boolean canApply(CampaignFleetAPI fleet, FleetMemberAPI fm);

    public abstract String getUnableToApplyTooltip(CampaignFleetAPI fleet, FleetMemberAPI fm);

    public abstract boolean removeItemsFromFleet(CampaignFleetAPI fleet, FleetMemberAPI fm);

    public boolean shouldLoad() {
        return true;
    }

    public boolean shouldShow(FleetMemberAPI fm, ExtraSystems es) {
        return true;
    }

    public void initialize() {
        AugmentsHandler.addAugment(this);
    }

    public void modifyResourcesPanel(InteractionDialogAPI dialog, ESInteractionDialogPlugin plugin, Map<String, Float> resourceCosts, FleetMemberAPI fm) {
    }

    /**
     * extra bandwidth allowed to be installed.
     *
     * @param fm
     * @param es
     * @return
     */
    public float getExtraBandwidthPurchaseable(FleetMemberAPI fm, ExtraSystems es) {
        return 0f;
    }

    /**
     * extra bandwidth added directly to ship.
     *
     * @param fm
     * @param es
     * @return
     */
    public float getExtraBandwidth(FleetMemberAPI fm, ExtraSystems es) {
        return 0f;
    }

    public void applyAugmentToStats(FleetMemberAPI fm, MutableShipStatsAPI stats, float bandwidth, String id) {

    }

    public void applyAugmentToShip(FleetMemberAPI fm, ShipAPI ship, float bandwidth, String id) {

    }

    public void advanceInCombat(ShipAPI ship, float amount, float bandwidth) {

    }

    public abstract void modifyToolTip(TooltipMakerAPI tooltip, FleetMemberAPI fm, ExtraSystems systems, boolean expand);
}
