package extrasystemreloaded.augments.impl;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import data.scripts.subsystems.dl_BaseSubsystem;
import data.scripts.util.dl_SpecLoadingUtils;
import data.scripts.util.dl_SubsystemUtils;
import extrasystemreloaded.augments.Augment;
import extrasystemreloaded.augments.impl.subsystems.SpooledFeedersSubSystem;
import extrasystemreloaded.hullmods.ExtraSystemHM;
import extrasystemreloaded.util.ExtraSystems;
import extrasystemreloaded.util.Utilities;
import org.json.JSONException;
import org.json.JSONObject;

import java.awt.*;

public class SpooledFeedersDroneLib extends Augment {
    public static final String AUGMENT_KEY = "SpooledFeeders";
    public static final Color MAIN_COLOR = SpooledFeeders.MAIN_COLOR;
    private static final String ITEM = "esr_ammospool";
    private static final Color[] tooltipColors = {MAIN_COLOR, ExtraSystemHM.infoColor, ExtraSystemHM.infoColor, ExtraSystemHM.infoColor, ExtraSystemHM.infoColor, ExtraSystemHM.infoColor};

    private static String NAME = "Spooled Feeders";
    private static float ROF_DEBUFF_PERMANENT = -20f;
    public static float FLUX_BUFF_SUBSYSTEM = -33f;
    public static float ROF_BUFF_SUBSYSTEM = 50f;

    //csv values
    private static final int DEBUFF_DURATION = 4;
    private static final int COOLDOWN = DEBUFF_DURATION + 5;

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
    public void loadConfig(JSONObject augmentSettings) throws JSONException {
        NAME = augmentSettings.getString("name");

        ROF_DEBUFF_PERMANENT = (float) augmentSettings.getDouble("permanentFireRateDebuff");
        ROF_BUFF_SUBSYSTEM = (float) augmentSettings.getDouble("subsystemFireRateBuff");
        FLUX_BUFF_SUBSYSTEM = (float) augmentSettings.getDouble("subsystemWeaponFluxBuff");
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
        if (expand) {
            dl_BaseSubsystem.SubsystemData data = SpooledFeedersSubSystem.getSubsystemSpec();

            tooltip.addPara("%s: Adds a toggleable subsystem. Weapon fire rate is increased by %s and flux costs are reduced by %s when the system is active. When the system is not active, weapon fire rate is reduced by %s. The system takes %s to recharge, and generates an increasing amount of hard flux while active, and disables hard flux dissipation. The buffs do not affect missile weapons.", 5, tooltipColors,
                    this.getName(),
                    ROF_BUFF_SUBSYSTEM + "%",
                    Math.abs(FLUX_BUFF_SUBSYSTEM) + "%",
                    Math.abs(ROF_DEBUFF_PERMANENT / 100f) + "x",
                    data.getOutTime() + " seconds",
                    data.getOutTime() + data.getCooldownTime() + " seconds");
        } else {
            tooltip.addPara(this.getName(), tooltipColors[0], 5);
        }
    }

    @Override
    public void applyAugmentToStats(FleetMemberAPI fm, MutableShipStatsAPI stats, float quality, String id) {
        stats.getBallisticRoFMult().modifyMult(this.getBuffId(), ROF_DEBUFF_PERMANENT / 100f);
        stats.getEnergyRoFMult().modifyMult(this.getBuffId(), ROF_DEBUFF_PERMANENT / 100f);
        stats.getMissileRoFMult().modifyMult(this.getBuffId(), ROF_DEBUFF_PERMANENT / 100f);
    }

    @Override
    public void applyAugmentToShip(FleetMemberAPI fm, ShipAPI ship, float quality, String id) {
        if(dl_SubsystemUtils.getSubsystemManager() == null) {
            return;
        }

        dl_SubsystemUtils.queueSubsystemForShip(ship, SpooledFeedersSubSystem.class);
    }
}
