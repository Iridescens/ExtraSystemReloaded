package extrasystemreloaded.systems.upgrades.impl;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import extrasystemreloaded.util.ExtraSystems;
import extrasystemreloaded.util.StatUtils;
import extrasystemreloaded.systems.upgrades.Upgrade;
import org.json.JSONException;
import org.json.JSONObject;

import java.awt.*;

public class Logistics extends Upgrade {
    private static String NAME;
    private static float MAX_CARGO_MULT;
    private static float MAX_CREW_MULT;
    private static float MAX_FUEL_MULT;


    private static float CR_TO_DEPLOY_SCALAR;
    private static float CR_TO_DEPLOY_QUALITY_MULT;
    private static float MIN_CREW_SCALAR;
    private static float MIN_CREW_QUALITY_MULT;
    private static float FUEL_USE_SCALAR;
    private static float FUEL_USE_QUALITY_MULT;
    private static float SUPPLIES_MONTH_SCALAR;
    private static float SUPPLIES_MONTH_QUALITY_MULT;
    private static float SUPPLIES_RECOVERY_SCALAR;
    private static float SUPPLIES_RECOVERY_QUALITY_MULT;
    private static float CR_RECOVERY_RATE_SCALAR;
    private static float CR_RECOVERY_RATE_QUALITY_MULT;
    private static float REPAIR_RATE_SCALAR;
    private static float REPAIR_RATE_QUALITY_MULT;

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    protected void loadConfig(JSONObject upgradeSettings) throws JSONException {
        NAME = upgradeSettings.getString("name");
        MAX_CARGO_MULT = (float) upgradeSettings.getDouble("cargoScalar");
        MAX_CREW_MULT = (float) upgradeSettings.getDouble("crewScalar");
        MAX_FUEL_MULT = (float) upgradeSettings.getDouble("fuelScalar");
        CR_TO_DEPLOY_SCALAR = (float) upgradeSettings.getDouble("crDeployedUpgradeScalar");
        CR_TO_DEPLOY_QUALITY_MULT = (float) upgradeSettings.getDouble("crDeployedQualityMult");
        MIN_CREW_SCALAR = (float) upgradeSettings.getDouble("minCrewUpgradeScalar");
        MIN_CREW_QUALITY_MULT = (float) upgradeSettings.getDouble("minCrewQualityMult");
        FUEL_USE_SCALAR = (float) upgradeSettings.getDouble("fuelUseUpgradeScalar");
        FUEL_USE_QUALITY_MULT = (float) upgradeSettings.getDouble("fuelUseQualityMult");
        SUPPLIES_MONTH_SCALAR = (float) upgradeSettings.getDouble("suppliesPerMonthUpgradeScalar");
        SUPPLIES_MONTH_QUALITY_MULT = (float) upgradeSettings.getDouble("suppliesPerMonthQualityMult");
        SUPPLIES_RECOVERY_SCALAR = (float) upgradeSettings.getDouble("suppliesToDeployUpgradeScalar");
        SUPPLIES_RECOVERY_QUALITY_MULT = (float) upgradeSettings.getDouble("suppliesToDeployQualityMult");
        CR_RECOVERY_RATE_SCALAR = (float) upgradeSettings.getDouble("crRecoveryUpgradeScalar");
        CR_RECOVERY_RATE_QUALITY_MULT = (float) upgradeSettings.getDouble("crRecoveryQualityMult");
        REPAIR_RATE_SCALAR = (float) upgradeSettings.getDouble("repairRateUpgradeScalar");
        REPAIR_RATE_QUALITY_MULT = (float) upgradeSettings.getDouble("repairRateQualityMult");
    }

    @Override
    public String getDescription() {
        return "Improve CR per deployment, crew, ship repair rate, CR recovery, fuel and supply use.";
    }

    @Override
    public void applyUpgradeToStats(FleetMemberAPI fm, MutableShipStatsAPI stats, float hullSizeFactor, int level, float quality) {

        StatUtils.setStatPercentBonus(stats.getCargoMod(), this.getBuffId(), level, quality, MAX_CARGO_MULT, hullSizeFactor);
        StatUtils.setStatPercentBonus(stats.getMaxCrewMod(), this.getBuffId(), level, quality, MAX_CREW_MULT, hullSizeFactor);
        StatUtils.setStatPercentBonus(stats.getFuelMod(), this.getBuffId(), level, quality, MAX_FUEL_MULT, hullSizeFactor);

        StatUtils.setStatPercentBonus(stats.getMinCrewMod(), this.getBuffId(),
                StatUtils.getDiminishingReturnsTotal(level, getMaxLevel(fm.getHullSpec().getHullSize()), quality, MIN_CREW_SCALAR, MIN_CREW_QUALITY_MULT, hullSizeFactor));

        StatUtils.setStatPercentBonus(stats.getFuelUseMod(), this.getBuffId(),
                StatUtils.getDiminishingReturnsTotal(level, getMaxLevel(fm.getHullSpec().getHullSize()), quality, FUEL_USE_SCALAR, FUEL_USE_QUALITY_MULT, hullSizeFactor));

        StatUtils.setStatPercentBonus(stats.getSuppliesPerMonth(), this.getBuffId(),
                StatUtils.getDiminishingReturnsTotal(level, getMaxLevel(fm.getHullSpec().getHullSize()), quality, SUPPLIES_MONTH_SCALAR, SUPPLIES_MONTH_QUALITY_MULT, hullSizeFactor));


        StatUtils.setStatPercentBonus(stats.getCRPerDeploymentPercent(), this.getBuffId(),
                StatUtils.getDiminishingReturnsTotal(level, getMaxLevel(fm.getHullSpec().getHullSize()), quality, CR_TO_DEPLOY_SCALAR, CR_TO_DEPLOY_QUALITY_MULT, hullSizeFactor));

        StatUtils.setStatPercentBonus(stats.getSuppliesToRecover(), this.getBuffId(),
                StatUtils.getDiminishingReturnsTotal(level, getMaxLevel(fm.getHullSpec().getHullSize()), quality, SUPPLIES_RECOVERY_SCALAR, SUPPLIES_RECOVERY_QUALITY_MULT, hullSizeFactor));

        StatUtils.setStatPercentBonus(stats.getBaseCRRecoveryRatePercentPerDay(), this.getBuffId(),
                StatUtils.getDiminishingReturnsTotal(level, getMaxLevel(fm.getHullSpec().getHullSize()), quality, CR_RECOVERY_RATE_SCALAR, CR_RECOVERY_RATE_QUALITY_MULT, hullSizeFactor));

        StatUtils.setStatPercentBonus(stats.getRepairRatePercentPerDay(), this.getBuffId(),
                StatUtils.getDiminishingReturnsTotal(level, getMaxLevel(fm.getHullSpec().getHullSize()), quality, REPAIR_RATE_SCALAR, REPAIR_RATE_QUALITY_MULT, hullSizeFactor));

    }

    @Override
    public void modifyToolTip(TooltipMakerAPI tooltip, FleetMemberAPI fm, ExtraSystems systems, boolean expand) {
        int level = systems.getUpgrade(this);

        if (level > 0) {
            if(expand) {
                tooltip.addPara(this.getName() + " (%s):", 5, Color.green, String.valueOf(level));


                StatUtils.addMultBonusToTooltip(tooltip, "  CR per deployment: %s (%s)",
                        fm.getStats().getCRPerDeploymentPercent().getMultBonus(this.getBuffId()).getValue(),
                        fm.getVariant().getHullSpec().getCRToDeploy());

                StatUtils.addMultBonusToTooltip(tooltip, "  Less required crew: %s (%s)",
                        fm.getStats().getMinCrewMod().getMultBonus(this.getBuffId()).getValue(),
                        fm.getVariant().getHullSpec().getMinCrew());

                StatUtils.addPercentBonusToTooltip(tooltip, "  More crew space: +%s (%s)",
                        fm.getStats().getMaxCrewMod().getPercentBonus(this.getBuffId()).getValue(),
                        fm.getVariant().getHullSpec().getMaxCrew());
                StatUtils.addPercentBonusToTooltip(tooltip, "  More cargo space: +%s (%s)",
                        fm.getStats().getCargoMod().getPercentBonus(this.getBuffId()).getValue(),
                        fm.getVariant().getHullSpec().getCargo());
                StatUtils.addPercentBonusToTooltip(tooltip, "  More fuel space: +%s (%s)",
                        fm.getStats().getFuelMod().getPercentBonus(this.getBuffId()).getValue(),
                        fm.getVariant().getHullSpec().getFuel());


                StatUtils.addPercentBonusToTooltipUnrounded(tooltip, "  Repairs and recovery rates: +%s (%s)",
                        fm.getStats().getBaseCRRecoveryRatePercentPerDay().getPercentStatMod(this.getBuffId()).getValue(),
                        fm.getStats().getBaseCRRecoveryRatePercentPerDay().getBaseValue());

                StatUtils.addMultBonusToTooltip(tooltip, "  Fuel consumption: %s (%s)",
                        fm.getStats().getFuelUseMod().getMultBonus(this.getBuffId()).getValue(),
                        fm.getVariant().getHullSpec().getFuelPerLY());

                StatUtils.addMultBonusToTooltip(tooltip, "  Overall supplies consumption rates: %s (%s)",
                        fm.getStats().getSuppliesPerMonth().getMultStatMod(this.getBuffId()).getValue(),
                        fm.getStats().getSuppliesPerMonth().getBaseValue());
            } else {
                tooltip.addPara(this.getName() + " (%s)", 5, Color.green, String.valueOf(level));
            }
        }
    }
}
