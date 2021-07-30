package extrasystemreloaded.upgrades.impl;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import extrasystemreloaded.campaign.Es_ShipLevelFleetData;
import extrasystemreloaded.util.StatUtils;
import extrasystemreloaded.upgrades.Upgrade;

import java.awt.*;

public class Logistics extends Upgrade {
    public static final String UPGRADE_KEY = "Logistics";

    private static final float MAX_CARGO_MULT = 2f;
    private static final float MAX_CREW_MULT = 2f;
    private static final float MAX_FUEL_MULT = 2f;


    private static final float CR_TO_DEPLOY_SCALAR = -10.2f;
    private static final float CR_TO_DEPLOY_QUALITY_MULT = 0.9f;
    private static final float MIN_CREW_SCALAR = -8.6f;
    private static final float MIN_CREW_QUALITY_MULT = 0.5f;
    private static final float FUEL_USE_SCALAR = -5.1f;
    private static final float FUEL_USE_QUALITY_MULT = 3f;
    private static final float SUPPLIES_MONTH_SCALAR = -4.65f;
    private static final float SUPPLIES_MONTH_QUALITY_MULT = 2f;
    private static final float SUPPLIES_RECOVERY_SCALAR = -8.5f;
    private static final float SUPPLIES_RECOVERY_QUALITY_MULT = 2f;
    private static final float CR_RECOVERY_RATE_SCALAR = 7.5f;
    private static final float CR_RECOVERY_RATE_QUALITY_MULT = 1.5f;
    private static final float REPAIR_RATE_SCALAR = 7.5f;
    private static final float REPAIR_RATE_QUALITY_MULT = 1.5f;

    @Override
    public String getKey() {
        return UPGRADE_KEY;
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
    public void modifyToolTip(TooltipMakerAPI tooltip, FleetMemberAPI fm, Es_ShipLevelFleetData buff) {
        int level = buff.getExtraSystems().getUpgrade(this);

        if (level > 0) {
            tooltip.addPara(this.getName() + " (%s):", 5, Color.green, String.valueOf(level));


            StatUtils.addPercentBonusToTooltip(tooltip, "  CR per deployment: %s (%s)",
                    fm.getStats().getCRPerDeploymentPercent().getPercentBonus(this.getBuffId()).getValue(),
                    fm.getVariant().getHullSpec().getCRToDeploy());

            StatUtils.addPercentBonusToTooltip(tooltip, "  Less required crew: %s (%s)",
                    fm.getStats().getMinCrewMod().getPercentBonus(this.getBuffId()).getValue(),
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


            StatUtils.addPercentBonusToTooltip(tooltip, "  Repairs and recovery rates: +%s (%s)",
                    fm.getStats().getBaseCRRecoveryRatePercentPerDay().getPercentStatMod(this.getBuffId()).getValue(),
                    fm.getStats().getBaseCRRecoveryRatePercentPerDay().getBaseValue());

            StatUtils.addPercentBonusToTooltip(tooltip, "  Fuel consumption: %s (%s)",
                    fm.getStats().getFuelUseMod().getPercentBonus(this.getBuffId()).getValue(),
                    fm.getVariant().getHullSpec().getFuelPerLY());

            StatUtils.addPercentBonusToTooltip(tooltip, "  Overall supplies consumption rates: %s (%s)",
                    fm.getStats().getSuppliesPerMonth().getPercentStatMod(this.getBuffId()).getValue(),
                    fm.getStats().getSuppliesPerMonth().getBaseValue());
        }
    }
}
