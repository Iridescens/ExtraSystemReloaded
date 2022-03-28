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
import extrasystemreloaded.util.StringUtils;
import extrasystemreloaded.util.Utilities;
import lombok.Getter;
import org.json.JSONException;

import java.awt.*;

public class HangarForgeMissiles extends Augment {
    public static final String AUGMENT_KEY = "HangarForgeMissiles";
    private static final String ITEM = "esr_hangarforge";
    private static final Color[] tooltipColors = {Color.GREEN, ExtraSystemHM.infoColor, ExtraSystemHM.infoColor};

    private static float COST_CREDITS = 150000;
    private static int SECONDS_PER_RELOAD = 90;
    private static float PERCENT_RELOADED = 50f;

    @Getter private final Color mainColor = Color.GREEN;

    @Override
    public void loadConfig() throws JSONException {
        SECONDS_PER_RELOAD = (int) augmentSettings.getInt("secondsBetweenReloads");
        PERCENT_RELOADED = (float) augmentSettings.getDouble("percentReloaded");
    }

    @Override
    public boolean canApply(CampaignFleetAPI fleet, FleetMemberAPI fm) {
        return Utilities.playerHasSpecialItem(ITEM)
                && fleet.getCargo().getCredits().get() < COST_CREDITS;
    }

    @Override
    public String getUnableToApplyTooltip(CampaignFleetAPI fleet, FleetMemberAPI fm) {
        if (fleet.getCargo().getCredits().get() < COST_CREDITS) {
            return StringUtils.getTranslation(this.getKey(), "needCredits")
                    .format("needCredits", COST_CREDITS)
                    .toString();
        }

        return StringUtils.getTranslation(this.getKey(), "needItem")
                .format("itemName", Global.getSettings().getSpecialItemSpec(ITEM).getName())
                .toString();
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
                StringUtils.getTranslation(this.getKey(), "longDescription")
                        .format("augmentName", this.getName())
                        .format("reloadSize", PERCENT_RELOADED)
                        .format("reloadTime", SECONDS_PER_RELOAD)
                        .addToTooltip(tooltip, tooltipColors);
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
                if(weapon.getAmmoTracker() != null && weapon.getAmmoTracker().usesAmmo() && weapon.getAmmoTracker().getAmmoPerSecond() == 0f) {

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
                        StringUtils.getString(this.getKey(), "statusReloaded"),
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