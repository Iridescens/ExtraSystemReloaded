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
import extrasystemreloaded.util.StatUtils;
import extrasystemreloaded.util.Utilities;
import org.json.JSONException;
import org.json.JSONObject;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;

public class PhasefieldEngine extends Augment {
    public static final String AUGMENT_KEY = "PhasefieldEngine";
    public static final Color MAIN_COLOR = Color.PINK;
    private static final String ITEM = "esr_phaseengine";
    private static final Color[] tooltipColors = {MAIN_COLOR, ExtraSystemHM.infoColor, ExtraSystemHM.infoColor};

    private static String NAME = "Phasefield Engine";

    private static int PHASE_COOLDOWN_INTERVAL = 8;
    private static int INVULNERABLE_INTERVAL = 2;

    private static float PHASE_COST_PERCENT_REDUCTION = 75f;
    private static float PHASE_COST_PERCENT_IF_NEGATIVE = -100f;
    private static float PHASE_COST_IF_ZERO = 10f;

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
        return "A Tri-Tachyon joint venture with Ko Combine to reduce catastrophic asteroid impacts, a phasefield " +
                "engine can be used to generate a protective field around a ship. The phase field completely " +
                "dissipates when the hull is destroyed, but the phase field can transport incoming projectiles " +
                "into p-space. This protective ability fades quickly after exiting phase space, and " +
                "rapid jumps to p-space take an increasingly large toll on the flux systems of a given ship.";
    }

    @Override
    public String getTooltip() {
        return "Phase activation cost is reduced, but doubles every time you use it within a short time. " +
                "The ship becomes invincible for a short time when exiting phase. Ships with negative or zero flux cost " +
                "will cost flux to phase.";
    }

    @Override
    public void loadConfig(JSONObject augmentSettings) throws JSONException {
        NAME = augmentSettings.getString("name");

        PHASE_COOLDOWN_INTERVAL = (int) augmentSettings.getInt("phaseCooldownInterval");
        INVULNERABLE_INTERVAL = (int) augmentSettings.getInt("invulnerableInterval");

        PHASE_COST_PERCENT_REDUCTION = (float) augmentSettings.getDouble("phaseCostBaseReduction");
        PHASE_COST_PERCENT_IF_NEGATIVE = (float) augmentSettings.getDouble("phaseCostModIfNegative");
        PHASE_COST_IF_ZERO = (float) augmentSettings.getDouble("phaseCostIfZero");
    }

    @Override
    public boolean canApply(CampaignFleetAPI fleet, FleetMemberAPI fm) {
        if(fm.getHullSpec().getShieldType() != ShieldAPI.ShieldType.PHASE) {
            return false;
        }

        return Utilities.playerHasSpecialItem(ITEM);
    }

    public String getUnableToApplyTooltip(CampaignFleetAPI fleet, FleetMemberAPI fm) {
        if(fm.getHullSpec().getShieldType() != ShieldAPI.ShieldType.PHASE) {
            return "Only phase ships can install this.";
        }

        return "You need an Phasefield Engine to install this.";
    }

    @Override
    public boolean removeItemsFromFleet(CampaignFleetAPI fleet, FleetMemberAPI fm) {
        Utilities.removePlayerSpecialItem(ITEM);

        return true;
    }

    @Override
    public void modifyToolTip(TooltipMakerAPI tooltip, FleetMemberAPI fm, ExtraSystems systems, boolean expand) {
        if (expand) {
            tooltip.addPara("%s: Phase activation cost is reduced by %s, but doubles every time you use it in a period of %s." +
                            "The ship is invincible for %s when exiting phase. Ships with a base negative phase flux cost " +
                            "will instead cost flux, and ships with a base zero phase flux cost will cost %s of base flux capacity " +
                            "before this augment is applied. ", 5, tooltipColors,

                    this.getName(),
                    PHASE_COST_PERCENT_REDUCTION + "%",
                    PHASE_COOLDOWN_INTERVAL + " seconds",
                    INVULNERABLE_INTERVAL + " seconds",
                    PHASE_COST_IF_ZERO * 100 + "%");
        } else {
            tooltip.addPara(this.getName(), tooltipColors[0], 5);
        }
    }

    @Override
    public void applyAugmentToStats(FleetMemberAPI fm, MutableShipStatsAPI stats, float quality, String id) {
        if(fm.getHullSpec().getShieldSpec().getPhaseCost() == 0) {
            stats.getPhaseCloakActivationCostBonus().modifyFlat(getBuffId() + "base", PHASE_COST_IF_ZERO / 100f);
        } else if (fm.getHullSpec().getShieldSpec().getPhaseCost() < 0) {
            stats.getPhaseCloakActivationCostBonus().modifyPercent(getBuffId() + "base", -100f);
        }

        stats.getPhaseCloakActivationCostBonus().modifyPercent(getBuffId(), -75f);
    }

    private String getPhaseStateId(ShipAPI ship) {
        return String.format("%s_%s_phasestate", this.getBuffId(), ship.getId());
    }

    private ShipSystemAPI.SystemState getLastPhaseState(ShipAPI ship) {
        Object val = Global.getCombatEngine().getCustomData().get(getPhaseStateId(ship));
        if(val != null) {
            return (ShipSystemAPI.SystemState) val;
        }
        return null;
    }

    private void setPhaseState(ShipAPI ship) {
        Global.getCombatEngine().getCustomData().put(getPhaseStateId(ship), ship.getPhaseCloak().getState());
    }

    private String getPhaseCostId(ShipAPI ship) {
        return String.format("%s_%s_phasecooldown", this.getBuffId(), ship.getId());
    }

    private IntervalUtil getPhaseCostInterval(ShipAPI ship) {
        Object val = Global.getCombatEngine().getCustomData().get(getPhaseCostId(ship));
        if(val != null) {
            return (IntervalUtil) val;
        }
        return null;
    }

    private IntervalUtil createPhaseCostInterval(ShipAPI ship) {
        IntervalUtil phaseCostInterval = new IntervalUtil(PHASE_COOLDOWN_INTERVAL, PHASE_COOLDOWN_INTERVAL);
        Global.getCombatEngine().getCustomData().put(getPhaseCostId(ship), phaseCostInterval);
        return phaseCostInterval;
    }

    private void removePhaseCostInterval(ShipAPI ship) {
        Global.getCombatEngine().getCustomData().remove(getPhaseCostId(ship));
    }

    private String getInvulverableId(ShipAPI ship) {
        return String.format("%s_%s_invulnerable", this.getBuffId(), ship.getId());
    }

    private IntervalUtil getInvulnerableInterval(ShipAPI ship) {
        Object val = Global.getCombatEngine().getCustomData().get(getInvulverableId(ship));
        if(val != null) {
            return (IntervalUtil) val;
        }
        return null;
    }

    private IntervalUtil createInvulverableInterval(ShipAPI ship) {
        IntervalUtil invulnInterval = new IntervalUtil(INVULNERABLE_INTERVAL, INVULNERABLE_INTERVAL);
        Global.getCombatEngine().getCustomData().put(getInvulverableId(ship), invulnInterval);
        return invulnInterval;
    }

    private void removeInvulnerableInterval(ShipAPI ship) {
        Global.getCombatEngine().getCustomData().remove(getInvulverableId(ship));
    }

    private String getTimesPhasedId(ShipAPI ship) {
        return String.format("%s_%s_timesphased", this.getBuffId(), ship.getId());
    }

    private int getTimesPhasedInInterval(ShipAPI ship) {
        Object val = Global.getCombatEngine().getCustomData().get(getTimesPhasedId(ship));
        if(val != null) {
            return (int) val;
        }
        return -1;
    }

    private void addToTimesPhased(ShipAPI ship) {
        Global.getCombatEngine().getCustomData().put(getTimesPhasedId(ship), getTimesPhasedInInterval(ship) + 1);
    }

    private void removeTimesPhased(ShipAPI ship) {
        Global.getCombatEngine().getCustomData().remove(getTimesPhasedId(ship));
    }

    private boolean isPhasing(ShipAPI ship) {
        ShipSystemAPI.SystemState currState = ship.getPhaseCloak().getState();
        return currState == ShipSystemAPI.SystemState.IN || currState == ShipSystemAPI.SystemState.ACTIVE;
    }

    private boolean justEnteredPhase(ShipAPI ship) {
        ShipSystemAPI.SystemState lastState = getLastPhaseState(ship);
        return ship.getPhaseCloak().getState() == ShipSystemAPI.SystemState.IN && lastState == ShipSystemAPI.SystemState.IDLE;
    }

    private boolean justExitedPhase(ShipAPI ship) {
        ShipSystemAPI.SystemState lastState = getLastPhaseState(ship);
        return ship.getPhaseCloak().getState() == ShipSystemAPI.SystemState.OUT && (lastState == ShipSystemAPI.SystemState.ACTIVE || lastState == ShipSystemAPI.SystemState.IN);
    }

    @Override
    public void advanceInCombat(ShipAPI ship, float amount, float quality) {
        IntervalUtil phaseInterval = getPhaseCostInterval(ship);
        IntervalUtil invulnInterval = getInvulnerableInterval(ship);

        if(justExitedPhase(ship)) {
            if (!ship.hasListenerOfClass(ESR_PhasefieldEngineListener.class)) {
                ESR_PhasefieldEngineListener listener = new ESR_PhasefieldEngineListener(ship);
                ship.addListener(listener);
            }

            if (invulnInterval == null) {
                createInvulverableInterval(ship);
            } else {
                invulnInterval.setInterval(INVULNERABLE_INTERVAL, INVULNERABLE_INTERVAL);
            }
        } else if (justEnteredPhase(ship)) {
            if (phaseInterval == null) {
                createPhaseCostInterval(ship);
            } else {
                phaseInterval.setInterval(PHASE_COOLDOWN_INTERVAL, PHASE_COOLDOWN_INTERVAL);
            }
            addToTimesPhased(ship);
            ship.getMutableStats().getPhaseCloakActivationCostBonus().modifyMult(this.getBuffId(), (float) Math.pow(2, getTimesPhasedInInterval(ship)));
        }

        if(invulnInterval != null) {
            invulnInterval.advance(amount);
            if(invulnInterval.intervalElapsed()) {
                if (ship.hasListenerOfClass(ESR_PhasefieldEngineListener.class)) {
                    ship.removeListenerOfClass(ESR_PhasefieldEngineListener.class);
                }

                removeInvulnerableInterval(ship);
            } else {
                String invulnText = String.format("INVULNERABLE (%s)", StatUtils.formatFloatUnrounded(INVULNERABLE_INTERVAL - invulnInterval.getElapsed()));
                ship.addAfterimage(new Color(255, 0, 255, 5), 0, 0, 0, 0, 0f, 0f, 0.1f, 0.75f, true, true, true);
                ship.addAfterimage(new Color(255, 0, 255), 0, 0, 0, 0, 5f, 0f, 0.1f, 0.75f, true, false, false);
                Global.getCombatEngine().maintainStatusForPlayerShip(getInvulverableId(ship), "graphics/icons/hullsys/temporal_shell.png", "PHASEFIELD ENGINE", invulnText, false);
            }
        }

        if (phaseInterval != null) {
            String phasedTimesText = String.format("PHASED %s TIMES, REFRESH IN %s", getTimesPhasedInInterval(ship), StatUtils.formatFloatUnrounded(PHASE_COOLDOWN_INTERVAL - phaseInterval.getElapsed()));
            Global.getCombatEngine().maintainStatusForPlayerShip(getTimesPhasedId(ship), "graphics/icons/hullsys/temporal_shell.png", "PHASEFIELD ENGINE", phasedTimesText, false);
            if(!isPhasing(ship)) {
                phaseInterval.advance(amount);
                if (phaseInterval.intervalElapsed()) {
                    removeTimesPhased(ship);
                    removePhaseCostInterval(ship);
                    ship.getMutableStats().getPhaseCloakActivationCostBonus().unmodifyMult(this.getBuffId());
                }
            }
        }

        setPhaseState(ship);
    }

    // damage listener
    private class ESR_PhasefieldEngineListener implements DamageTakenModifier {
        private final ShipAPI ship;

        public ESR_PhasefieldEngineListener(ShipAPI ship) {
            this.ship = ship;
        }

        @Override
        public String modifyDamageTaken(Object param, CombatEntityAPI target, DamageAPI damageAPI, Vector2f point, boolean shieldHit) {
            if(target == this.ship) {
                damageAPI.getModifier().modifyMult(PhasefieldEngine.this.getBuffId(), 0f);
                return PhasefieldEngine.this.getBuffId();
            }
            return null;
        }
    }
}