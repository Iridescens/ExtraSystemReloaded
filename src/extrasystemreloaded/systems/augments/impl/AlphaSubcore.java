package extrasystemreloaded.systems.augments.impl;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import extrasystemreloaded.systems.augments.Augment;
import extrasystemreloaded.hullmods.ExtraSystemHM;
import extrasystemreloaded.util.ExtraSystems;
import extrasystemreloaded.util.StringUtils;
import extrasystemreloaded.util.Utilities;
import lombok.Getter;
import org.json.JSONException;
import org.json.JSONObject;

import java.awt.*;

public class AlphaSubcore extends Augment {
    private static final String ITEM = "alpha_core";
    private static final Color[] tooltipColors = {Color.CYAN, ExtraSystemHM.infoColor};

    public static final int COST_REDUCTION_LG = 4;
    public static final int COST_REDUCTION_MED = 2;
    public static final int COST_REDUCTION_SM = 1;
    public static final int COST_REDUCTION_FIGHTER = 2;
    public static final int COST_REDUCTION_BOMBER = 2;

    @Getter private final Color mainColor = Color.cyan;

    //this mod already has an "alpha core" installation.
    //yunru's does as well, but i'm less worried about that one.
    @Override
    public boolean shouldLoad() {
        if (Global.getSettings().getModManager().isModEnabled("mayu_specialupgrades")) {
            return false;
        }

        return super.shouldLoad();
    }

    @Override
    public boolean canApply(CampaignFleetAPI fleet, FleetMemberAPI fm) {
        return Utilities.playerHasCommodity(ITEM);
    }

    public String getUnableToApplyTooltip(CampaignFleetAPI fleet, FleetMemberAPI fm) {
        return StringUtils.getTranslation(this.getKey(), "needItem")
                .format("itemName", Global.getSettings().getSpecialItemSpec(ITEM).getName())
                .toString();
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
                StringUtils.getTranslation(this.getKey(), "longDescription")
                        .format("augmentName", this.getName())
                        .format("large", COST_REDUCTION_LG)
                        .format("medium", COST_REDUCTION_MED)
                        .format("small", COST_REDUCTION_SM)
                        .format("fighters", COST_REDUCTION_FIGHTER)
                        .format("bombers", COST_REDUCTION_BOMBER)
                        .addToTooltip(tooltip, tooltipColors);
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
