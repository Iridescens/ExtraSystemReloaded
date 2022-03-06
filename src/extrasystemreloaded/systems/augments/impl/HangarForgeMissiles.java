package extrasystemreloaded.systems.augments.impl;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.WeaponAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.IntervalUtil;
import extrasystemreloaded.systems.augments.Augment;
import extrasystemreloaded.hullmods.ExtraSystemHM;
import extrasystemreloaded.util.ExtraSystems;
import extrasystemreloaded.util.Utilities;
import org.json.JSONException;
import org.json.JSONObject;

import java.awt.*;

public class HangarForgeMissiles extends Augment {
    public static final String AUGMENT_KEY = "HangarForgeMissiles";
    public static final Color MAIN_COLOR = Color.GREEN;
    private static final String ITEM = "esr_hangarforge";
    private static final Color[] tooltipColors = {MAIN_COLOR, ExtraSystemHM.infoColor, ExtraSystemHM.infoColor};

    private static String NAME = "Hacked Missile Forge";
    private static int SECONDS_PER_RELOAD = 90;
    private static float PERCENT_RELOADED = 50f;

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
        return "Corrupting a hangar microforge to fabricate the most delicate components of missiles is " +
                "possible, given a few engineers skilled with Domain technology. Their services don't come cheap, " +
                "however.";
    }

    @Override
    public String getTooltip() {
        return "Reloads some of all limited missile's ammunition capacity periodically.";
    }

    @Override
    public void loadConfig(JSONObject augmentSettings) throws JSONException {
        NAME = augmentSettings.getString("name");
        SECONDS_PER_RELOAD = (int) augmentSettings.getInt("secondsBetweenReloads");
        PERCENT_RELOADED = (float) augmentSettings.getDouble("percentReloaded");
    }

    @Override
    public boolean canApply(CampaignFleetAPI fleet, FleetMemberAPI fm) {
        return Utilities.playerHasSpecialItem(ITEM);
    }

    @Override
    public String getUnableToApplyTooltip(CampaignFleetAPI fleet, FleetMemberAPI fm) {
        if (fleet.getCargo().getCredits().get() < 150000) {
            return "You need 150,000 credits to install this.";
        }
        return "You need a Hangar Forge to install this.";
    }

    @Override
    public boolean removeItemsFromFleet(CampaignFleetAPI fleet, FleetMemberAPI fm) {
        fleet.getCargo().getCredits().subtract(150000);
        Utilities.removePlayerSpecialItem(ITEM);
        return true;
    }

    @Override
    public void modifyToolTip(TooltipMakerAPI tooltip, FleetMemberAPI fm, ExtraSystems systems, boolean expand) {
        if (systems.hasAugment(this.getKey())) {
            if (expand) {
                tooltip.addPara("%s: Reloads %s of limited missile's ammunition capacity every %s.", 5,
                        tooltipColors,
                        this.getName(), PERCENT_RELOADED + "%", SECONDS_PER_RELOAD + " seconds");
            } else {
                tooltip.addPara(this.getName(), tooltipColors[0], 5);
            }
        }
    }

    private String getReloadId(ShipAPI ship) {
        return String.format("%s%s_reload", this.getBuffId(), ship.getId());
    }

    private IntervalUtil getReloadInterval(ShipAPI ship) {
        Object val = Global.getCombatEngine().getCustomData().get(getReloadId(ship));
        if (val != null) {
            return (IntervalUtil) val;
        }
        return null;
    }

    private IntervalUtil createReloadInterval(ShipAPI ship) {
        IntervalUtil interval = new IntervalUtil(SECONDS_PER_RELOAD, SECONDS_PER_RELOAD);
        Global.getCombatEngine().getCustomData().put(getReloadId(ship), interval);
        return interval;
    }

    @Override
    public void advanceInCombat(ShipAPI ship, float amount, float quality) {
        IntervalUtil reloadInterval = getReloadInterval(ship);
        if (reloadInterval == null) {
            reloadInterval = createReloadInterval(ship);
        }

        reloadInterval.advance(amount);

        if(reloadInterval.intervalElapsed()) {

            boolean addedAmmo = false;
            for(WeaponAPI weapon : ship.getAllWeapons()) {
                if(weapon.getAmmoTracker() != null && weapon.getAmmoTracker().usesAmmo() && weapon.getAmmoTracker().getAmmoPerSecond() == 0) {

                    int ammo = weapon.getAmmoTracker().getAmmo();
                    int maxAmmo = weapon.getAmmoTracker().getMaxAmmo();

                    if(ammo < maxAmmo) {
                        weapon.getAmmoTracker().setAmmo((int) Math.min(maxAmmo, ammo + maxAmmo * PERCENT_RELOADED / 100f));
                        addedAmmo = true;
                    }
                }
            }

            if (addedAmmo) {
                reloadInterval.setInterval(SECONDS_PER_RELOAD, SECONDS_PER_RELOAD);

                Global.getCombatEngine().addFloatingText(
                        ship.getLocation(),
                        "Reloaded missiles!",
                        8,
                        Color.WHITE,
                        ship,
                        2,
                        2
                );
            } else {
                reloadInterval.setInterval(10f, 10f);
            }
        }
    }
}
