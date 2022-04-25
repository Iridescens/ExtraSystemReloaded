package extrasystemreloaded.systems.augments.impl;

import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.WeaponAPI;
import com.fs.starfarer.api.combat.listeners.WeaponRangeModifier;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import extrasystemreloaded.campaign.rulecmd.ESInteractionDialogPlugin;
import extrasystemreloaded.hullmods.ExtraSystemHM;
import extrasystemreloaded.util.ExtraSystems;
import extrasystemreloaded.util.StringUtils;
import extrasystemreloaded.util.Utilities;
import extrasystemreloaded.systems.augments.Augment;
import lombok.Getter;
import org.json.JSONException;

import java.awt.*;
import java.util.Map;

public class EqualizerCore extends Augment {
    private static final String ITEM = "esr_equalizercore";
    private static final Color[] tooltipColors = {Color.orange.darker(), ExtraSystemHM.infoColor};

    private static float RECOIL_REDUCTION = -25f;
    private static float TURN_RATE_BUFF = 50f;

    private static int RANGE_LIMIT_BOTTOM = 550;
    private static int RANGE_BOTTOM_BUFF = 200;
    private static int RANGE_LIMIT_TOP = 800;
    private static int RANGE_TOP_BUFF = -150;

    @Getter private final Color mainColor = Color.orange.darker();

    @Override
    public void loadConfig() throws JSONException {
        RECOIL_REDUCTION = (float) augmentSettings.getDouble("recoilReduction");
        TURN_RATE_BUFF = (float) augmentSettings.getDouble("weaponTurnRateIncrease");

        RANGE_LIMIT_BOTTOM = (int) augmentSettings.getInt("rangeBottomBounds");
        RANGE_BOTTOM_BUFF = (int) augmentSettings.getInt("rangeBottomBuff");
        RANGE_LIMIT_TOP = (int) augmentSettings.getInt("rangeTopBounds");
        RANGE_TOP_BUFF = (int) augmentSettings.getInt("rangeTopBuff");
    }

    @Override
    public boolean canApply(CampaignFleetAPI fleet, FleetMemberAPI fm) {
        return Utilities.hasItem(fleet.getCargo(), ITEM);
    }

    public String getUnableToApplyTooltip(CampaignFleetAPI fleet, FleetMemberAPI fm) {
        return "You need an Equalizer Core to install this.";
    }

    @Override
    public boolean removeItemsFromFleet(CampaignFleetAPI fleet, FleetMemberAPI fm) {
        Utilities.takeItemQuantity(fleet.getCargo(), ITEM, 1);

        return true;
    }

    @Override
    public void modifyResourcesPanel(InteractionDialogAPI dialog, ESInteractionDialogPlugin plugin, Map<String, Float> resourceCosts, FleetMemberAPI fm) {
        resourceCosts.put(ITEM, 1f);
    }

    @Override
    public void modifyToolTip(TooltipMakerAPI tooltip, FleetMemberAPI fm, ExtraSystems systems, boolean expand) {
        if (systems.hasAugment(this.getKey())) {
            if(expand) {
                StringUtils.getTranslation(this.getKey(), "longDescription")
                        .format("augmentName", this.getName())
                        .format("recoilReduction", RECOIL_REDUCTION)
                        .format("weaponTurnBonus", TURN_RATE_BUFF)
                        .format("lowRangeThreshold", RANGE_LIMIT_BOTTOM)
                        .format("rangeBonus", RANGE_BOTTOM_BUFF)
                        .format("highRangeThreshold", RANGE_LIMIT_TOP)
                        .format("rangeMalus", RANGE_TOP_BUFF)
                        .addToTooltip(tooltip, tooltipColors);
            } else {
                tooltip.addPara(this.getName(), tooltipColors[0], 5);
            }
        }
    }

    @Override
    public void applyAugmentToStats(FleetMemberAPI fm, MutableShipStatsAPI stats, float bandwidth, String id) {
        stats.getAutofireAimAccuracy().modifyPercent(this.getBuffId(), 1000f);
        stats.getMaxRecoilMult().modifyMult(this.getBuffId(), Math.abs(RECOIL_REDUCTION) / 100f);
        stats.getRecoilDecayMult().modifyMult(this.getBuffId(), Math.abs(RECOIL_REDUCTION) / 100f);
        stats.getRecoilPerShotMult().modifyMult(this.getBuffId(), Math.abs(RECOIL_REDUCTION) / 100f);

        stats.getWeaponTurnRateBonus().modifyPercent(this.getBuffId(), TURN_RATE_BUFF);
        stats.getBeamWeaponTurnRateBonus().modifyPercent(this.getBuffId(), TURN_RATE_BUFF);
    }

    @Override
    public void advanceInCombat(ShipAPI ship, float amount, float bandwidth) {
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
            if(weapon.getSpec().getMaxRange() >= RANGE_LIMIT_TOP) {
                baseRangeMod = RANGE_TOP_BUFF;
            } else if (weapon.getSpec().getMaxRange() <= RANGE_LIMIT_BOTTOM) {
                baseRangeMod = RANGE_BOTTOM_BUFF;
            }

            return baseRangeMod * (1f + (percentRangeIncreases / 100f));
        }
    }
}