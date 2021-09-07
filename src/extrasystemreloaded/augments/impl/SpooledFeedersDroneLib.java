package extrasystemreloaded.augments.impl;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import data.scripts.util.dl_SubsystemUtils;
import extrasystemreloaded.augments.Augment;
import extrasystemreloaded.augments.impl.subsystems.SpooledFeedersSubSystem;
import extrasystemreloaded.hullmods.ExtraSystemHM;
import extrasystemreloaded.util.ExtraSystems;
import extrasystemreloaded.util.Utilities;

import java.awt.*;

public class SpooledFeedersDroneLib extends Augment {
    public static final String AUGMENT_KEY = "SpooledFeeders";
    private static final String ITEM = "esr_ammospool";
    private static final Color[] tooltipColors = {Color.lightGray, ExtraSystemHM.infoColor, ExtraSystemHM.infoColor, ExtraSystemHM.infoColor, ExtraSystemHM.infoColor, ExtraSystemHM.infoColor};

    private static final int DEBUFF_DURATION = 4;
    private static final int COOLDOWN = DEBUFF_DURATION + 5;

    @Override
    public String getKey() {
        return AUGMENT_KEY;
    }

    @Override
    public String getName() {
        return Global.getSettings().getString("AbilityName", getKey());
    }

    @Override
    public String getDescription() {
        return "Although unable to be used for the same purpose as a full-size Fullerene spool, this much-smaller " +
                "chain can be used instead to replace many of the moving mechanical parts within a ship. The chain " +
                "notably increases the rate of fire of weapons upon being disturbed, generating some kind of intense " +
                "field that crew members can only describe as \"bloodthirsty\". The chain slows down " +
                "considerably after a couple seconds, with an effect on the ship that matches its slower speed.";
    }

    @Override
    public String getTooltip() {
        return "Adds a subsystem that generates hard flux, increases rate of fire and reduces flux costs for all weapons while toggled, and then reducing rate of fire for some time after.";
    }

    @Override
    public boolean canApply(CampaignFleetAPI fleet, FleetMemberAPI fm) {
        return Utilities.playerHasSpecialItem(ITEM);
    }

    public String getUnableToApplyTooltip(CampaignFleetAPI fleet, FleetMemberAPI fm) {
        return "You need a Hyperferrous Chain to install this.";
    }

    @Override
    public boolean removeItemsFromFleet(CampaignFleetAPI fleet, FleetMemberAPI fm) {
        Utilities.removePlayerSpecialItem(ITEM);

        return true;
    }

    @Override
    public void modifyToolTip(TooltipMakerAPI tooltip, FleetMemberAPI fm, ExtraSystems systems, boolean expand) {
        if (systems.hasAugment(this.getKey())) {
            if(expand) {
                tooltip.addPara("%s: Weapon fire rate is increased by %s and flux costs are reduced by %s when the system is active. When disabled, weapon fire rate is reduced by %s for %s. The system takes %s to recharge, and generates an increasing amount of hard flux while active, and disables hard flux dissipation. The buffs do not affect missile weapons.", 5, tooltipColors,
                        this.getName(), "100%", "50%", "25%", DEBUFF_DURATION + " seconds", COOLDOWN + " seconds");
            } else {
                tooltip.addPara(this.getName(), tooltipColors[0], 5);
            }
        }
    }

    @Override
    public void applyAugmentToShip(FleetMemberAPI fm, ShipAPI ship, float quality, String id) {
        if(dl_SubsystemUtils.getSubsystemManager() == null) {
            return;
        }

        dl_SubsystemUtils.queueSubsystemForShip(ship, SpooledFeedersSubSystem.class);
    }
}
