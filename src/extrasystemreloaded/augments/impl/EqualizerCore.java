package extrasystemreloaded.augments.impl;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.WeaponAPI;
import com.fs.starfarer.api.combat.listeners.WeaponRangeModifier;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import extrasystemreloaded.hullmods.ExtraSystemHM;
import extrasystemreloaded.util.ExtraSystems;
import extrasystemreloaded.util.Utilities;
import extrasystemreloaded.augments.Augment;

import java.awt.*;

public class EqualizerCore extends Augment {
    private static final String ITEM = "esr_equalizercore";
    private static final Color[] tooltipColors = {Color.red, ExtraSystemHM.infoColor, ExtraSystemHM.infoColor, ExtraSystemHM.infoColor, ExtraSystemHM.infoColor, ExtraSystemHM.infoColor, ExtraSystemHM.infoColor, ExtraSystemHM.infoColor};

    @Override
    public String getKey() {
        return "EqualizerCore";
    }

    @Override
    public String getName() {
        return Global.getSettings().getString("AbilityName", getKey());
    }

    @Override
    public String getDescription() {
        return "This core is dedicated to managing weaponry to an degree that rivals even Alpha Cores given control " +
                "of weapon arrays, with one unique quirk: effective ordnance ranges of non-missile weapons are " +
                "equalized to a certain degree. The core, in its Terms of Use, swears by its Stronger than an Alpha " +
                "trademark, and it can't be used for anything but weapons.. supposedly.";
    }

    @Override
    public String getTooltip() {
        return "Improve recoil control and weapon turn rate. Equalizes weapon ranges to a middle-ground range.";
    }

    @Override
    public boolean canApply(CampaignFleetAPI fleet, FleetMemberAPI fm) {
        return Utilities.playerHasSpecialItem(ITEM);
    }

    public String getUnableToApplyTooltip(CampaignFleetAPI fleet, FleetMemberAPI fm) {
        return "You need an Equalizer Core to install this.";
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
                tooltip.addPara("%s: Reduces recoil by %s. Increases weapon turn rate by %s. Autofire leading is %s. " +
                        "Weapons with at most %s have range increased by %s. Weapons with at least %s have range reduced by %s.", 5,
                        tooltipColors,
                        this.getName(), "25%", "50%", "nearly perfected", "550 range", "200", "800 range", "-150");
            } else {
                tooltip.addPara(this.getName(), tooltipColors[0], 5);
            }
        }
    }

    @Override
    public void applyAugmentToStats(FleetMemberAPI fm, MutableShipStatsAPI stats, float quality, String id) {
        stats.getAutofireAimAccuracy().modifyPercent(this.getBuffId(), 1000f);
        stats.getMaxRecoilMult().modifyPercent(this.getBuffId(), -25f);
        stats.getRecoilDecayMult().modifyPercent(this.getBuffId(), -25f);
        stats.getRecoilPerShotMult().modifyPercent(this.getBuffId(), -25f);

        stats.getWeaponTurnRateBonus().modifyPercent(this.getBuffId(), 50f);
        stats.getBeamWeaponTurnRateBonus().modifyPercent(this.getBuffId(), 50f);
    }

    @Override
    public void advanceInCombat(ShipAPI ship, float amount, float quality) {
        if (!ship.hasListenerOfClass(ESR_EqualizerCoreListener.class)) {
            ship.addListener(new ESR_EqualizerCoreListener());
        }
    }

    // Our range listener
    private class ESR_EqualizerCoreListener implements WeaponRangeModifier {

        @Override
        public float getWeaponRangePercentMod(ShipAPI ship, WeaponAPI weapon) {
            return 0f;
        }

        @Override
        public float getWeaponRangeMultMod(ShipAPI ship, WeaponAPI weapon) {
            return 1f;
        }

        @Override
        public float getWeaponRangeFlatMod(ShipAPI ship, WeaponAPI weapon) {
            if (weapon.getType() == WeaponAPI.WeaponType.MISSILE) {
                return 0f;
            }

            //Stolen from Nicke. Thx buddy
            float percentRangeIncreases = 0f;
            if (weapon.getType() == WeaponAPI.WeaponType.ENERGY) {
                percentRangeIncreases = ship.getMutableStats().getEnergyWeaponRangeBonus().getPercentMod();
            } else if (weapon.getType() == WeaponAPI.WeaponType.BALLISTIC) {
                percentRangeIncreases = ship.getMutableStats().getBallisticWeaponRangeBonus().getPercentMod();
            }
            if (ship.hasListenerOfClass(WeaponRangeModifier.class)) {
                for (WeaponRangeModifier listener : ship.getListeners(WeaponRangeModifier.class)) {
                    //Should not be needed, but good practice: no infinite loops allowed here, no
                    if (listener == this) {
                        continue;
                    }
                    percentRangeIncreases += listener.getWeaponRangePercentMod(ship, weapon);
                }
            }

            float baseRangeMod = 0;
            if(weapon.getSpec().getMaxRange() >= 800) {
                baseRangeMod = -150;
            } else if (weapon.getSpec().getMaxRange() <= 550) {
                baseRangeMod = 200;
            }

            return baseRangeMod * (1f + (percentRangeIncreases/100f));
        }
    }
}