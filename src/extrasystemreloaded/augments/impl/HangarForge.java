package extrasystemreloaded.augments.impl;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import extrasystemreloaded.hullmods.ExtraSystemHM;
import extrasystemreloaded.util.ExtraSystems;
import extrasystemreloaded.util.Utilities;
import extrasystemreloaded.augments.Augment;

import java.awt.*;

public class HangarForge extends Augment {
    public static final String MODULE_KEY = "HangarForge";
    private static final String ITEM = "esr_hangarforge";
    private static final Color[] tooltipColors = {Color.GREEN, ExtraSystemHM.infoColor, ExtraSystemHM.infoColor};

    private static final float RATE_DECREASE_MODIFIER = 50f;
    private static final float FIGHTER_REPLACEMENT_RATE_BONUS = 20f;

    @Override
    public String getKey() {
        return MODULE_KEY;
    }

    @Override
    public String getName() {
        return Global.getSettings().getString("AbilityName", getKey());
    }

    @Override
    public String getDescription() {
        return "A Hangar Forge can be used to produce fightercraft at a rate far faster than comparatively-" +
                "primitive mechanics and other automated equipment ever could. In addition, the forge is much more " +
                "resilient to the effects of extreme fighter losses, although the crew loss in such a scenario is " +
                "always unfortunate.";
    }

    @Override
    public String getTooltip() {
        return "Decreases fighter replacement time. Reduces the rate at which the replacement rate decreases as well.";
    }

    @Override
    public boolean canApply(CampaignFleetAPI fleet, FleetMemberAPI fm) {
        return Utilities.playerHasSpecialItem(ITEM);
    }

    @Override
    public String getUnableToApplyTooltip(CampaignFleetAPI fleet, FleetMemberAPI fm) {
        return "You need a Hangar Forge to install this.";
    }

    @Override
    public boolean removeItemsFromFleet(CampaignFleetAPI fleet, FleetMemberAPI fm) {
        Utilities.removePlayerSpecialItem(ITEM);
        return true;
    }

    @Override
    public void modifyToolTip(TooltipMakerAPI tooltip, FleetMemberAPI fm, ExtraSystems systems, boolean expand) {
        if (systems.hasAugment(this.getKey())) {
            if (expand) {
                tooltip.addPara("%s: Reduces fighter replacement rates by %s. " +
                                "Reduces the rate at which replacement rate degrades by %s.", 5,
                        tooltipColors,
                        this.getName(), FIGHTER_REPLACEMENT_RATE_BONUS + "%", RATE_DECREASE_MODIFIER + "%");
            } else {
                tooltip.addPara(this.getName(), tooltipColors[0], 5);
            }
        }
    }

    @Override
    public void applyUpgradeToStats(FleetMemberAPI fm, MutableShipStatsAPI stats, float quality, String id) {
        stats.getDynamic().getStat(Stats.REPLACEMENT_RATE_DECREASE_MULT).modifyMult(getBuffId(), 1f - RATE_DECREASE_MODIFIER / 100f);
        float timeMult = 1f / ((100f + FIGHTER_REPLACEMENT_RATE_BONUS) / 100f);
        stats.getFighterRefitTimeMult().modifyMult(getBuffId(), timeMult);
    }
}
