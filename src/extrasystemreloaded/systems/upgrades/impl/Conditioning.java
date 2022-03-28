package extrasystemreloaded.systems.upgrades.impl;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import extrasystemreloaded.util.ExtraSystems;
import extrasystemreloaded.util.StatUtils;
import extrasystemreloaded.systems.upgrades.Upgrade;
import extrasystemreloaded.util.StringUtils;
import org.json.JSONException;

import java.awt.*;

public class Conditioning extends Upgrade {
    private static float CR_TO_DEPLOY_SCALAR;
    private static float CR_RECOVERY_RATE_SCALAR;

    private static float PEAK_CR_MULT;
    private static float CR_LOSS_MULT; //this value is a post-scaling of the other two factors.
    //if they are reduced, this will be reduced as well.

    private static float FRIGATE_MULT;
    private static float DESTROYER_MULT;
    private static float CRUISER_MULT;
    private static float CAPITAL_MULT;

    @Override
    protected void loadConfig() throws JSONException {
        PEAK_CR_MULT = (float) upgradeSettings.getDouble("peakCrScalar");
        CR_LOSS_MULT = (float) upgradeSettings.getDouble("crLossScalar");
        FRIGATE_MULT = (float) upgradeSettings.getDouble("frigateMult");
        DESTROYER_MULT = (float) upgradeSettings.getDouble("destroyerMult");
        CRUISER_MULT = (float) upgradeSettings.getDouble("cruiserMult");
        CAPITAL_MULT = (float) upgradeSettings.getDouble("capitalMult");

        CR_RECOVERY_RATE_SCALAR = (float) upgradeSettings.getDouble("crRecoveryScalar");
        CR_TO_DEPLOY_SCALAR = (float) upgradeSettings.getDouble("crDeployedScalar");
    }

    @Override
    public void applyUpgradeToStats(FleetMemberAPI fm, MutableShipStatsAPI stats, float hullSizeFactor, int level, float quality) {

        double v = level * quality * PEAK_CR_MULT * hullSizeFactor;
        if (fm.getHullSpec().getHullSize() == ShipAPI.HullSize.CAPITAL_SHIP) {
            v *= 1;
        } else if (fm.getHullSpec().getHullSize() == ShipAPI.HullSize.CRUISER) {
            v *= 1.5;
        } else if (fm.getHullSpec().getHullSize() == ShipAPI.HullSize.DESTROYER) {
            v *= 3;
        } else {
            v *= 8;
        }

        stats.getPeakCRDuration().modifyPercent(this.getBuffId(), (float) v);
        stats.getCRLossPerSecondPercent().modifyMult(this.getBuffId(), 1f + ((float) Math.max(CR_LOSS_MULT * v, -90f)) / 100f);

        StatUtils.setStatPercentBonus(stats.getBaseCRRecoveryRatePercentPerDay(), this.getBuffId(), level, quality, CR_RECOVERY_RATE_SCALAR, hullSizeFactor);
        StatUtils.setStatMultBonus(stats.getCRPerDeploymentPercent(), this.getBuffId(), level, quality, CR_TO_DEPLOY_SCALAR, hullSizeFactor);
    }

    @Override
    public void modifyToolTip(TooltipMakerAPI tooltip, FleetMemberAPI fm, ExtraSystems systems, boolean expand) {
        int level = systems.getUpgrade(this);

        if (level > 0) {
            if(expand) {
                tooltip.addPara(this.getName() + " (%s):", 5, Color.green, String.valueOf(level));

                this.addIncreaseWithFinalToTooltip(tooltip,
                        "peakPerformanceTimeIncrease",
                        fm.getStats().getPeakCRDuration().getPercentBonus(this.getBuffId()).getValue(),
                        fm.getVariant().getHullSpec().getNoCRLossTime());

                this.addDecreaseToTooltip(tooltip,
                        "crDegradationDecrease",
                        fm.getStats().getCRLossPerSecondPercent().getMultBonus(this.getBuffId()).getValue());

                this.addDecreaseWithFinalToTooltip(tooltip,
                        "crPerDeploymentDecrease",
                        fm.getStats().getCRPerDeploymentPercent().getMultBonus(this.getBuffId()).getValue(),
                        fm.getHullSpec().getCRToDeploy());

                this.addIncreaseWithFinalToTooltip(tooltip,
                        "crRecoveryIncrease",
                        fm.getStats().getBaseCRRecoveryRatePercentPerDay().getPercentStatMod(this.getBuffId()).getValue(),
                        fm.getStats().getBaseCRRecoveryRatePercentPerDay().getBaseValue());
            } else {
                tooltip.addPara(this.getName() + " (%s)", 5, Color.green, String.valueOf(level));
            }
        }
    }
}
