package extrasystemreloaded.augments.impl;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.combat.listeners.DamageTakenModifier;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.IntervalUtil;
import extrasystemreloaded.augments.Augment;
import extrasystemreloaded.hullmods.ExtraSystemHM;
import extrasystemreloaded.util.ExtraSystems;
import extrasystemreloaded.util.Utilities;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;

public class PhasefieldEngine extends Augment {
    private static final String ITEM = "esr_phaseengine";
    private static final Color[] tooltipColors = {Color.PINK, ExtraSystemHM.infoColor, ExtraSystemHM.infoColor};

    @Override
    public String getKey() {
        return "PhasefieldEngine";
    }

    @Override
    public String getName() {
        return Global.getSettings().getString("AbilityName", getKey());
    }

    @Override
    public String getDescription() {
        return "A Tri-Tachyon joint venture with Ko Combine to reduce catastrophic asteroid impacts, a phasefield " +
                "engine can be used to generate a protective field around a ship. The phase field completely " +
                "dissipates when the hull is destroyed, but the phase field can transport an incoming projectile with " +
                "enough energy into p-space. This protective ability is worth the briefly horrified faces that crew " +
                "members occasionally develop, like they've seen and subsequently forgotten what is behind the giant " +
                "signature that a phase sensor reads from it.";
    }

    @Override
    public String getTooltip() {
        return "Every 60 seconds a killing blow or a projectile that deals more than 1000 damage is completely absorbed.";
    }

    @Override
    public boolean canApply(CampaignFleetAPI fleet, FleetMemberAPI fm) {
        return Utilities.playerHasSpecialItem(ITEM);
    }

    public String getUnableToApplyTooltip(CampaignFleetAPI fleet, FleetMemberAPI fm) {
        return "You need an Phasefield Engine to install this.";
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
                tooltip.addPara("%s: Every %s a killing blow or a projectile that deals more than %s damage is completely absorbed.", 5, tooltipColors,
                                                        this.getName(), "60 seconds", "1000");
            } else {
                tooltip.addPara(this.getName(), tooltipColors[0], 5);
            }
        }
    }

    @Override
    public void applyAugmentToStats(FleetMemberAPI fm, MutableShipStatsAPI stats, float quality, String id) {
    }

    @Override
    public void advanceInCombat(ShipAPI ship, float amount, float quality) {
        if (!ship.hasListenerOfClass(ESR_PhasefieldEngineListener.class)) {
            ship.addListener(new ESR_PhasefieldEngineListener(ship));
        } else {
            ESR_PhasefieldEngineListener listener = ship.getListeners(ESR_PhasefieldEngineListener.class).get(0);
            listener.advanceInterval(amount);
        }
    }

    // damage listener
    private class ESR_PhasefieldEngineListener implements DamageTakenModifier {
        private final ShipAPI ship;
        private IntervalUtil consumeInterval = new IntervalUtil(60, 60);
        private IntervalUtil ghostInterval = new IntervalUtil(2f, 2f);
        private boolean consume = true;

        public ESR_PhasefieldEngineListener(ShipAPI ship) {
            this.ship = ship;
        }

        public boolean canConsume() {
            return consume;
        }

        public void advanceInterval(float time) {
            if (consume) {
                ghostInterval.advance(time);
                if(ghostInterval.intervalElapsed()) {
                    ship.addAfterimage(new Color(255, 0, 255, 50), 0, 0, 0, 0, 0f, 1f, 2f, 1f, true, true, true);
                    ship.addAfterimage(new Color(255, 0, 255), 0, 0, 0, 0, 5f, 1f, 2f, 0.75f, true, false, false);
                    ghostInterval.setInterval(10f, 10f);
                }
                Global.getCombatEngine().maintainStatusForPlayerShip(PhasefieldEngine.this.getBuffId(), "graphics/icons/hullsys/temporal_shell.png", "PHASEFIELD ENGINE", "HUNGRY", false);
                return;
            }

            consumeInterval.advance(time);
            if (consumeInterval.intervalElapsed()) {
                consume = true;
                consumeInterval.setInterval(60, 60); //reset
            } else {
                Global.getCombatEngine().maintainStatusForPlayerShip(PhasefieldEngine.this.getBuffId(), "graphics/icons/hullsys/temporal_shell.png", "PHASEFIELD ENGINE",
                        String.format("FULL FOR %s SECONDS", Math.round(consumeInterval.getIntervalDuration() - consumeInterval.getElapsed())), false);
            }
        }

        @Override
        public String modifyDamageTaken(Object param, CombatEntityAPI target, DamageAPI damageAPI, Vector2f point, boolean shieldHit) {
            if(consume && !shieldHit && target == this.ship) {
                if(damageAPI.getDamage() > Math.min(this.ship.getHitpoints(), 1000f)) {
                    consume = false;
                    damageAPI.getModifier().modifyMult(PhasefieldEngine.this.getBuffId(), 0f);
                    return PhasefieldEngine.this.getBuffId();
                }
            }
            return null;
        }
    }
}