package extrasystemreloaded.systems.augments.impl;

import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import extrasystemreloaded.hullmods.ExtraSystemHM;
import extrasystemreloaded.util.ExtraSystems;
import extrasystemreloaded.util.StringUtils;
import extrasystemreloaded.util.Utilities;
import extrasystemreloaded.systems.augments.Augment;
import lombok.Getter;
import org.json.JSONException;

import java.awt.*;

public class HangarForge extends Augment {
    private static final String ITEM = "esr_hangarforge";
    private static final Color[] tooltipColors = {Color.GREEN, ExtraSystemHM.infoColor, ExtraSystemHM.infoColor};

    private static float RATE_DECREASE_MODIFIER = -35f;
    private static float FIGHTER_REPLACEMENT_TIME_BONUS = -15f;

    @Getter private final Color mainColor = Color.GREEN;

    @Override
    public void loadConfig() throws JSONException {
        RATE_DECREASE_MODIFIER = (float) augmentSettings.getDouble("replacementRateDecreaseSpeed");
        FIGHTER_REPLACEMENT_TIME_BONUS = (float) augmentSettings.getDouble("fighterReplacementBuff");
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
                StringUtils.getTranslation(this.getKey(), "longDescription")
                        .format("augmentName", this.getName())
                        .format("replacementRateIncrease", FIGHTER_REPLACEMENT_TIME_BONUS)
                        .format("rateDecreaseBuff", RATE_DECREASE_MODIFIER)
                        .addToTooltip(tooltip, tooltipColors);
            } else {
                tooltip.addPara(this.getName(), tooltipColors[0], 5);
            }
        }
    }

    @Override
    public void applyAugmentToStats(FleetMemberAPI fm, MutableShipStatsAPI stats, float quality, String id) {
        stats.getDynamic().getStat(Stats.REPLACEMENT_RATE_DECREASE_MULT).modifyMult(getBuffId(), 1f + RATE_DECREASE_MODIFIER / 100f);

        float timeMult = 1f / ((100f + FIGHTER_REPLACEMENT_TIME_BONUS) / 100f);
        stats.getFighterRefitTimeMult().modifyMult(getBuffId(), timeMult);
    }
}
