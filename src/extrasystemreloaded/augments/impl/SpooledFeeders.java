package extrasystemreloaded.augments.impl;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.WeaponAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.IntervalUtil;
import extrasystemreloaded.hullmods.ExtraSystemHM;
import extrasystemreloaded.util.ExtraSystems;
import extrasystemreloaded.util.Utilities;
import extrasystemreloaded.augments.Augment;
import org.json.JSONException;
import org.json.JSONObject;
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.util.Map;

public class SpooledFeeders extends Augment {
    public static final String AUGMENT_KEY = "SpooledFeeders";
    public static final Color MAIN_COLOR = Color.lightGray;
    private static final String ITEM = "esr_ammospool";
    private static final Color[] tooltipColors = {MAIN_COLOR, ExtraSystemHM.infoColor, ExtraSystemHM.infoColor, ExtraSystemHM.infoColor, ExtraSystemHM.infoColor, ExtraSystemHM.infoColor};

    private static String NAME = "Spooled Feeders";
    private static float RATE_OF_FIRE_BUFF = 100f;
    private static float RATE_OF_FIRE_DEBUFF = -33f;

    private static int COOLDOWN = 16;
    private static int BUFF_DURATION = 5;
    private static int DEBUFF_DURATION = 4;

    @Override
    public String getKey() {
        return AUGMENT_KEY;
    }

    @Override
    public String getName() {
        return Global.getSettings().getString("AbilityName", getKey());
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
        return "Increases weapon firerate substantially when a weapon fires. Rate of fire slows for a time after.";
    }

    @Override
    public void loadConfig(JSONObject augmentSettings) throws JSONException {
        NAME = augmentSettings.getString("name");

        RATE_OF_FIRE_BUFF = (float) augmentSettings.getDouble("weaponFireRateBuff");
        RATE_OF_FIRE_DEBUFF = (float) augmentSettings.getDouble("weaponFireRateDebuff");

        COOLDOWN = (int) augmentSettings.getInt("buffCooldown");
        BUFF_DURATION = (int) augmentSettings.getInt("buffActiveTime");
        DEBUFF_DURATION = (int) augmentSettings.getInt("debuffActiveTime");
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
            tooltip.addPara("%s: Weapon fire rate is increased by %s for %s when a weapon is fired. After this time, weapon fire rate is reduced by %s for %s. This can occur once every %s." +
                            "If this ship is controlled by a player, the buff applies if a weapon is manually fired. If controlled by an AI, any non-PD weapon triggers it.", 5, tooltipColors,
                    this.getName(), RATE_OF_FIRE_BUFF + "%", BUFF_DURATION + " seconds", Math.abs(RATE_OF_FIRE_DEBUFF) + "%", DEBUFF_DURATION + " seconds", COOLDOWN + " seconds");
        } else {
            tooltip.addPara(this.getName(), tooltipColors[0], 5);
        }
    }

    @Override
    public void applyAugmentToStats(FleetMemberAPI fm, MutableShipStatsAPI stats, float quality, String id) {
    }

    private String getIntervalId(ShipAPI ship) {
        return ship.getId() + this.getKey() + "interval";
    }


    private String getSpooledId(ShipAPI ship) {
        return ship.getId() + this.getKey() + "spooled";
    }

    private boolean isPD(WeaponAPI weapon) {
        return weapon.hasAIHint(WeaponAPI.AIHints.PD) || weapon.hasAIHint(WeaponAPI.AIHints.PD_ONLY);
    }

    private boolean canSpool(ShipAPI ship) {
        return ship.getShipAI() != null || Mouse.isButtonDown(0);
    }

    @Override
    public void advanceInCombat(ShipAPI ship, float amount, float quality) {
        Map<String, Object> customData = Global.getCombatEngine().getCustomData();
        if(!customData.containsKey(getSpooledId(ship))) {
            customData.put(getIntervalId(ship), new IntervalUtil(COOLDOWN, COOLDOWN));
            customData.put(getSpooledId(ship), SpoolState.SPOOLED);
        }

        SpoolState spooled = (SpoolState) customData.get(getSpooledId(ship));
        IntervalUtil interval = (IntervalUtil) customData.get(getIntervalId(ship));

        if(spooled == SpoolState.SPOOLED) {
            Global.getCombatEngine().maintainStatusForPlayerShip(this.getBuffId(), "graphics/icons/hullsys/ammo_feeder.png", "SPOOLED FEEDERS", "READY", false);

            if(canSpool(ship)) {
                for (WeaponAPI weapon : ship.getAllWeapons()) {
                    if (weapon.isFiring() && !(ship.getShipAI() != null && isPD(weapon))) {
                        interval.setInterval(BUFF_DURATION, BUFF_DURATION);
                        customData.put(getSpooledId(ship), SpoolState.BUFFED);

                        ship.addAfterimage(new Color(255, 0, 0, 150), 0, 0, 0, 0, 0f, 0.1f, 4.6f, 0.25f, true, true, true);

                        for (WeaponAPI buffWeapon : ship.getAllWeapons()) {
                            if (buffWeapon.getCooldownRemaining() > (buffWeapon.getCooldown() / 2f)) {
                                buffWeapon.setRemainingCooldownTo(buffWeapon.getCooldown() / 2f);
                            }
                        }

                        break;
                    }
                }
            }
        } else {
            interval.advance(amount);

            if(interval.intervalElapsed()) {
                if (spooled == SpoolState.BUFFED) {
                    interval.setInterval(DEBUFF_DURATION, DEBUFF_DURATION);
                    customData.put(getSpooledId(ship), SpoolState.DEBUFFED);

                    ship.addAfterimage(new Color(0, 100, 255, 150), 0, 0, 0, 0, 0f, 0.1f, 3.3f, 0.25f, true, true, true);
                } else if (spooled == SpoolState.DEBUFFED) {
                    interval.setInterval(COOLDOWN, COOLDOWN);
                    customData.put(getSpooledId(ship), SpoolState.RECHARGE);

                    ship.getMutableStats().getBallisticRoFMult().unmodifyMult(this.getBuffId());
                    ship.getMutableStats().getEnergyRoFMult().unmodifyMult(this.getBuffId());
                } else if (spooled == SpoolState.RECHARGE) {
                    customData.put(getSpooledId(ship), SpoolState.SPOOLED);
                }
            } else if (spooled == SpoolState.BUFFED) {
                Global.getCombatEngine().maintainStatusForPlayerShip(this.getBuffId(), "graphics/icons/hullsys/ammo_feeder.png", "SPOOLED FEEDERS", "GIVE THEM HELL", false);

                ship.getMutableStats().getBallisticRoFMult().modifyMult(this.getBuffId(), 1 + RATE_OF_FIRE_BUFF / 100f);
                ship.getMutableStats().getEnergyRoFMult().modifyMult(this.getBuffId(), 1 + RATE_OF_FIRE_BUFF / 100f);
            } else if (spooled == SpoolState.DEBUFFED) {
                Global.getCombatEngine().maintainStatusForPlayerShip(this.getBuffId(), "graphics/icons/hullsys/ammo_feeder.png", "SPOOLED FEEDERS", "FEEL THE BURN", true);

                ship.getMutableStats().getBallisticRoFMult().modifyMult(this.getBuffId(), 1 + RATE_OF_FIRE_DEBUFF / 100f);
                ship.getMutableStats().getEnergyRoFMult().modifyMult(this.getBuffId(), 1 + RATE_OF_FIRE_DEBUFF / 100f);
            } else if (spooled == SpoolState.RECHARGE) {
                Global.getCombatEngine().maintainStatusForPlayerShip(this.getBuffId(), "graphics/icons/hullsys/ammo_feeder.png", "SPOOLED FEEDERS",
                        String.format("RECHARGED IN %s SECONDS", Math.round(interval.getIntervalDuration() - interval.getElapsed())), false);
            }
        }
    }

    private enum SpoolState {
        SPOOLED,
        BUFFED,
        DEBUFFED,
        RECHARGE
    }
}
