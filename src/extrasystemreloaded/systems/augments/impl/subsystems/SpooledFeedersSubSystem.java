package extrasystemreloaded.systems.augments.impl.subsystems;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipwideAIFlags;
import com.fs.starfarer.api.combat.WeaponAPI;
import data.scripts.subsystems.dl_BaseSubsystem;
import data.scripts.util.dl_SpecLoadingUtils;
import extrasystemreloaded.systems.augments.impl.SpooledFeedersDroneLib;
import extrasystemreloaded.hullmods.ExtraSystemHM;
import org.lazywizard.lazylib.MathUtils;

import java.awt.*;
import java.util.EnumSet;

public class SpooledFeedersSubSystem extends dl_BaseSubsystem {
    public static final String SUBSYSTEM_ID = "esr_spooledfeeder";

    public static dl_BaseSubsystem.SubsystemData getSubsystemSpec() {
        return dl_SpecLoadingUtils.getSubsystemData(SUBSYSTEM_ID);
    }

    public float startTime = 0f;

    public SpooledFeedersSubSystem() {
        super(SUBSYSTEM_ID);
    }

    @Override
    public void apply(MutableShipStatsAPI stats, String id, SubsystemState subsystemState, float effectLevel) {
        if (subsystemState.equals(SubsystemState.IN)) {
            startTime = Global.getCombatEngine().getTotalElapsedTime(false);

            float mult = 1f + SpooledFeedersDroneLib.ROF_BUFF_SUBSYSTEM / 100f;

            stats.getBallisticRoFMult().modifyMult(id, mult);
            stats.getBallisticWeaponFluxCostMod().modifyMult(id, 1f - (SpooledFeedersDroneLib.FLUX_BUFF_SUBSYSTEM * 0.01f));

            stats.getEnergyRoFMult().modifyMult(id, mult);
            stats.getEnergyWeaponFluxCostMod().modifyMult(id, 1f - (SpooledFeedersDroneLib.FLUX_BUFF_SUBSYSTEM * 0.01f));

            stats.getHardFluxDissipationFraction().modifyMult(id, 0f);
        } else if (subsystemState.equals(SubsystemState.OUT)) {
            stats.getHardFluxDissipationFraction().unmodify(id);
            stats.getBallisticRoFMult().unmodify(id);
            stats.getBallisticWeaponFluxCostMod().unmodify(id);
            stats.getEnergyRoFMult().unmodify(id);
            stats.getEnergyWeaponFluxCostMod().unmodify(id);
        }

        if (subsystemState.equals(SubsystemState.ACTIVE)) {
            float currTime = Global.getCombatEngine().getTotalElapsedTime(false);
            float addedFlux = getAddedFlux(currTime);

            if(ship.getFluxTracker().isOverloadedOrVenting()
                    || ship.getFluxTracker().getCurrFlux() + addedFlux > ship.getFluxTracker().getMaxFlux()) {

                //deactive system
                activate();
                return;
            }

            ship.getFluxTracker().increaseFlux(addedFlux, true);

        }

        ship.setWeaponGlow(effectLevel, new Color(255, 100, 0, 255), EnumSet.allOf(WeaponAPI.WeaponType.class));
    }

    private float getAddedFlux(float currTime) {
        return ship.getMutableStats().getFluxDissipation().getModifiedValue() * (0.05f * Math.min(4f, (currTime - startTime) / 4f));
    }

    @Override
    public void unapply(MutableShipStatsAPI stats, String id) {
        stats.getHardFluxDissipationFraction().unmodify(id);
        stats.getBallisticRoFMult().unmodify(id);
        stats.getBallisticWeaponFluxCostMod().unmodify(id);
        stats.getEnergyRoFMult().unmodify(id);
        stats.getEnergyWeaponFluxCostMod().unmodify(id);
    }

    @Override
    public String getStatusString() {
        return null;
    }

    @Override
    public String getInfoString() {
        if (isFadingOut()) return "FEEL THE BURN";
        if (isOn()) return "BRING THEM HELL";
        if (isCooldown()) return "BUILDING HEAT";
        return this.getActiveHotkey() != null ? this.getActiveHotkey() : this.getHotkey();
    }

    @Override
    public String getFlavourString() {
        return "FIRE RATE BOOST";
    }

    @Override
    public int getNumGuiBars() {
        return 1;
    }

    @Override
    public void aiInit() {
    }

    private boolean shouldBeActive() {
        if (MathUtils.getDistance(ship, ship.getMouseTarget()) < 1600f) {
            if (ship.getAIFlags().hasFlag(ShipwideAIFlags.AIFlags.MANEUVER_TARGET) && ship.getFluxLevel() < 0.8f) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void aiUpdate(float v) {
        if (ship == null || !ship.isAlive()) return;

        if (!isActive()) {
            if (shouldBeActive()) {
                activate();
            }
        } else if (!shouldBeActive()) {
            //deactivates an active system
            activate();
        }
    }
}
