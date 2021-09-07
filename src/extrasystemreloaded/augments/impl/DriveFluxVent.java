package extrasystemreloaded.augments.impl;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.IntervalUtil;
import extrasystemreloaded.hullmods.ExtraSystemHM;
import extrasystemreloaded.util.ExtraSystems;
import extrasystemreloaded.util.Utilities;
import extrasystemreloaded.augments.Augment;
import org.lazywizard.lazylib.VectorUtils;

import java.awt.*;
import java.util.Map;

public class DriveFluxVent extends Augment {
    private static final String AUGMENT_KEY = "DriveFluxVent";
    private static final String ITEM = "esr_drivevent";
    private static final Color[] tooltipColors = {Color.magenta, ExtraSystemHM.infoColor, ExtraSystemHM.infoColor, ExtraSystemHM.infoColor, ExtraSystemHM.infoColor};

    private static final float VENT_SPEED_INCREASE = 30f;
    private static final int FORWARD_SPEED_INCREASE = 30;

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
        return "An experimental flux vent that can only function under the intense heat that ship thrusters " +
                "generate. It excels at venting flux, so well in fact that the thrusters around it receive a " +
                "temporary increase in power that only shows in the forward direction. This effect resides for a " +
                "couple seconds after venting as well. Crew members remark that the purple glow of the engines is " +
                "one of the prettiest sights to ever see, but the reduced ability to retreat never lets them be at " +
                "ease.";
    }

    @Override
    public String getTooltip() {
        return "Increases vent speed, and forward speed is increased while and shortly after venting.";
    }

    @Override
    public boolean canApply(CampaignFleetAPI fleet, FleetMemberAPI fm) {
        return Utilities.playerHasSpecialItem(ITEM);
    }

    public String getUnableToApplyTooltip(CampaignFleetAPI fleet, FleetMemberAPI fm) {
        return "You need a Drive Flux Vent to install this.";
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
                tooltip.addPara("%s: Increases vent speed by %s. If venting is started while flux is above %s, " +
                                        "forward speed is increased by %s while venting and for %s afterwards.", 5, tooltipColors,
                        this.getName(), VENT_SPEED_INCREASE + "%", "50%", String.valueOf(FORWARD_SPEED_INCREASE), "4 seconds");
            } else {
                tooltip.addPara(this.getName(), tooltipColors[0], 5);
            }
        }
    }

    @Override
    public void applyAugmentToStats(FleetMemberAPI fm, MutableShipStatsAPI stats, float quality, String id) {
        stats.getVentRateMult().modifyPercent(this.getBuffId(), VENT_SPEED_INCREASE);
    }

    private String getDriveStateId(ShipAPI ship) {
        return ship.getId() + this.getKey() + "state";
    }

    private String getIntervalId(ShipAPI ship) {
        return ship.getId() + this.getKey() + "interval";
    }

    @Override
    public void advanceInCombat(ShipAPI ship, float amount, float quality) {
        Map<String, Object> customData = Global.getCombatEngine().getCustomData();
        if(!customData.containsKey(getDriveStateId(ship))) {
            customData.put(getDriveStateId(ship), DriveState.NONE);
        }

        DriveState state = (DriveState) customData.get(getDriveStateId(ship));

        if(state == DriveState.VENTING || state == DriveState.OUT) {
            float velocityDir = VectorUtils.getFacing(ship.getVelocity()) - ship.getFacing();
            if(Math.abs(velocityDir) < 25f) {
                ship.getMutableStats().getAcceleration().modifyPercent(this.getBuffId(), 50f);
                ship.getMutableStats().getMaxSpeed().modifyPercent(this.getBuffId(), FORWARD_SPEED_INCREASE);

                Global.getCombatEngine().maintainStatusForPlayerShip(this.getBuffId(), "graphics/icons/hullsys/infernium_injector.png", "DRIVE FLUX VENT", "INCREASED FORWARD VELOCITY", false);
            } else {
                ship.getMutableStats().getAcceleration().unmodify(this.getBuffId());
                ship.getMutableStats().getMaxSpeed().unmodify(this.getBuffId());
            }
        }

        if(ship.getFluxTracker().isVenting()) {
            if(ship.getCurrFlux() > ship.getMaxFlux() / 2f) {
                ship.getEngineController().fadeToOtherColor(this.getBuffId(), new Color(255, 75, 255), null, 1f, 0.75f);

                if (state != DriveState.VENTING) {
                    customData.put(getDriveStateId(ship), DriveState.VENTING);
                }
            }
        } else {
            if(state == DriveState.VENTING) {
                customData.put(getDriveStateId(ship), DriveState.OUT);
                customData.put(getIntervalId(ship), new IntervalUtil(4f, 4f));
                state = DriveState.OUT;
            }

            if(state == DriveState.OUT) {

                IntervalUtil intervalUtil = (IntervalUtil) customData.get(getIntervalId(ship));
                intervalUtil.advance(amount);

                float ratio = 1f - (intervalUtil.getElapsed() / intervalUtil.getIntervalDuration());
                ship.getEngineController().fadeToOtherColor(this.getBuffId(), new Color(255, 75, 255), null, 0.25f + 0.5f * ratio, 0.75f);

                if(intervalUtil.intervalElapsed()) {
                    customData.put(getDriveStateId(ship), DriveState.NONE);

                    ship.getMutableStats().getAcceleration().unmodify(this.getBuffId());
                    ship.getMutableStats().getMaxSpeed().unmodify(this.getBuffId());
                }
            }
        }
    }

    private enum DriveState {
        VENTING,
        OUT,
        NONE
    }
}
