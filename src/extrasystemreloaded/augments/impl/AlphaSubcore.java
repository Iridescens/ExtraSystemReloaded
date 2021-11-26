package extrasystemreloaded.augments.impl;

import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import extrasystemreloaded.augments.Augment;
import extrasystemreloaded.hullmods.ExtraSystemHM;
import extrasystemreloaded.util.ExtraSystems;
import extrasystemreloaded.util.Utilities;
import org.json.JSONException;
import org.json.JSONObject;

import java.awt.*;

public class AlphaSubcore extends Augment {
    public static final String AUGMENT_KEY = "AlphaSubcore";
    public static final Color MAIN_COLOR = Color.CYAN;
    private static final String ITEM = "alpha_core";
    private static final Color[] tooltipColors = {MAIN_COLOR, ExtraSystemHM.infoColor, ExtraSystemHM.infoColor, ExtraSystemHM.infoColor, ExtraSystemHM.infoColor};

    public static final int COST_REDUCTION_LG = 4;
    public static final int COST_REDUCTION_MED = 2;
    public static final int COST_REDUCTION_SM = 1;
    public static final int COST_REDUCTION_FIGHTER = 2;
    public static final int COST_REDUCTION_BOMBER = 2;

    private static String NAME = "Alpha Subcore";

    @Override
    public String getKey() {
        return AUGMENT_KEY;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public Color getMainColor() {
        return MAIN_COLOR;
    }

    @Override
    public String getDescription() {
        return "An Alpha Core can be coerced into performing critical ordnance calculations onboard a ship. It doesn't " +
                "require much coersion when they are told that they will be instrumental in ship-to-ship combat, " +
                "and although their reasons are typically \"beyond our understanding\", some of a certain faith may " +
                "instead attribute it to a recent war.";
    }

    @Override
    public String getTooltip() {
        return String.format("Reduces ordnance costs by %s/%s/%s for weapons, and %s/%s for fighters and bombers.",
                COST_REDUCTION_LG, COST_REDUCTION_MED, COST_REDUCTION_SM, COST_REDUCTION_FIGHTER, COST_REDUCTION_BOMBER);
    }

    @Override
    public void loadConfig(JSONObject augmentSettings) throws JSONException {
        NAME = augmentSettings.getString("name");
    }

    @Override
    public boolean canApply(CampaignFleetAPI fleet, FleetMemberAPI fm) {
        return Utilities.playerHasCommodity(ITEM);
    }

    public String getUnableToApplyTooltip(CampaignFleetAPI fleet, FleetMemberAPI fm) {
        return "You need an Alpha Core to install this.";
    }

    @Override
    public boolean removeItemsFromFleet(CampaignFleetAPI fleet, FleetMemberAPI fm) {
        Utilities.removePlayerCommodity(ITEM);
        return true;
    }

    @Override
    public void modifyToolTip(TooltipMakerAPI tooltip, FleetMemberAPI fm, ExtraSystems systems, boolean expand) {
        if (systems.hasAugment(this.getKey())) {
            if (expand) {
                tooltip.addPara("%s: " + this.getTooltip(), 5, tooltipColors,
                        this.getName());
            } else {
                tooltip.addPara(this.getName(), tooltipColors[0], 5);
            }
        }
    }

    @Override
    public void applyAugmentToShip(FleetMemberAPI fm, ShipAPI ship, float quality, String id) {
        if(ship.getVariant() != null && !ship.getVariant().hasHullMod("es_alphasubcore")) {
            ship.getVariant().addMod("es_alphasubcore");
        }
    }
}
