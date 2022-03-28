package extrasystemreloaded.systems.upgrades.impl;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import extrasystemreloaded.util.ExtraSystems;
import extrasystemreloaded.util.StatUtils;
import extrasystemreloaded.systems.upgrades.Upgrade;
import org.json.JSONException;

import java.awt.*;

public class Logistics extends Upgrade {
    private static String NAME;
    private static float MAX_CARGO_MULT;
    private static float MAX_CREW_MULT;
    private static float MAX_FUEL_MULT;


    private static float MIN_CREW_SCALAR;
    private static float MIN_CREW_QUALITY_MULT;
    private static float FUEL_USE_SCALAR;
    private static float FUEL_USE_QUALITY_MULT;
    private static float SUPPLIES_MONTH_SCALAR;
    private static float SUPPLIES_MONTH_QUALITY_MULT;
    private static float SUPPLIES_RECOVERY_SCALAR;
    private static float SUPPLIES_RECOVERY_QUALITY_MULT;
    private static float REPAIR_RATE_SCALAR;

    @Override
    protected void loadConfig() throws JSONException {
        MAX_CARGO_MULT = (float) upgradeSettings.getDouble("cargoScalar");
        MAX_CREW_MULT = (float) upgradeSettings.getDouble("crewScalar");
        MAX_FUEL_MULT = (float) upgradeSettings.getDouble("fuelScalar");
        MIN_CREW_SCALAR = (float) upgradeSettings.getDouble("minCrewUpgradeScalar");
        MIN_CREW_QUALITY_MULT = (float) upgradeSettings.getDouble("minCrewQualityMult");
        FUEL_USE_SCALAR = (float) upgradeSettings.getDouble("fuelUseUpgradeScalar");
        FUEL_USE_QUALITY_MULT = (float) upgradeSettings.getDouble("fuelUseQualityMult");
        SUPPLIES_MONTH_SCALAR = (float) upgradeSettings.getDouble("suppliesPerMonthUpgradeScalar");
        SUPPLIES_MONTH_QUALITY_MULT = (float) upgradeSettings.getDouble("suppliesPerMonthQualityMult");
        SUPPLIES_RECOVERY_SCALAR = (float) upgradeSettings.getDouble("suppliesToDeployUpgradeScalar");
        SUPPLIES_RECOVERY_QUALITY_MULT = (float) upgradeSettings.getDouble("suppliesToDeployQualityMult");
        REPAIR_RATE_SCALAR = (float) upgradeSettings.getDouble("repairRateScalar");
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

        StatUtils.setStatPercentBonus(stats.getSuppliesToRecover(), this.getBuffId(),
                StatUtils.getDiminishingReturnsTotal(level, getMaxLevel(fm.getHullSpec().getHullSize()), quality, SUPPLIES_RECOVERY_SCALAR, SUPPLIES_RECOVERY_QUALITY_MULT, hullSizeFactor));

        StatUtils.setStatPercentBonus(stats.getRepairRatePercentPerDay(), this.getBuffId(), level, quality, REPAIR_RATE_SCALAR, hullSizeFactor);
    }

    @Override
    public void modifyToolTip(TooltipMakerAPI tooltip, FleetMemberAPI fm, ExtraSystems systems, boolean expand) {
        int level = systems.getUpgrade(this);

        if (level > 0) {
            if(expand) {
                tooltip.addPara(this.getName() + " (%s):", 5, Color.green, String.valueOf(level));

                this.addIncreaseWithFinalToTooltip(tooltip,
                        "cargoSpaceIncrease",
                        fm.getStats().getCargoMod().getPercentBonus(this.getBuffId()).getValue(),
                        fm.getHullSpec().getCargo());

                this.addDecreaseWithFinalToTooltip(tooltip,
                        "supplyConsumptionDecrease",
                        fm.getStats().getSuppliesPerMonth().getMultStatMod(this.getBuffId()).getValue(),
                        fm.getHullSpec().getSuppliesPerMonth());

                this.addDecreaseWithFinalToTooltip(tooltip,
                        "suppliesToRecoverDecrease",
                        fm.getStats().getSuppliesToRecover().getMultStatMod(this.getBuffId()).getValue(),
                        fm.getHullSpec().getSuppliesToRecover());

                this.addIncreaseWithFinalToTooltip(tooltip,
                        "fuelSpaceIncrease",
                        fm.getStats().getFuelMod().getPercentBonus(this.getBuffId()).getValue(),
                        fm.getHullSpec().getCargo());

                this.addDecreaseWithFinalToTooltip(tooltip,
                        "fuelConsumptionDecrease",
                        fm.getStats().getFuelUseMod().getMultBonus(this.getBuffId()).getValue(),
                        fm.getHullSpec().getFuelPerLY());

                this.addIncreaseWithFinalToTooltip(tooltip,
                        "crewSpaceIncrease",
                        fm.getStats().getMaxCrewMod().getPercentBonus(this.getBuffId()).getValue(),
                        fm.getHullSpec().getCargo());

                this.addDecreaseWithFinalToTooltip(tooltip,
                        "requiredCrewDecrease",
                        fm.getStats().getMinCrewMod().getMultBonus(this.getBuffId()).getValue(),
                        fm.getHullSpec().getMinCrew());

                this.addIncreaseWithFinalToTooltip(tooltip,
                        "hullRepairIncrease",
                        fm.getStats().getRepairRatePercentPerDay().getPercentStatMod(this.getBuffId()).getValue(),
                        fm.getStats().getRepairRatePercentPerDay().getBaseValue());

            } else {
                tooltip.addPara(this.getName() + " (%s)", 5, Color.green, String.valueOf(level));
            }
        }
    }
}
