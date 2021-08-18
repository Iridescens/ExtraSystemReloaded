package extrasystemreloaded.upgrades.impl;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import extrasystemreloaded.util.ExtraSystems;
import extrasystemreloaded.util.StatUtils;
import extrasystemreloaded.upgrades.Upgrade;

import java.awt.*;

public class Subsystems extends Upgrade {
    public static final String UPGRADE_KEY = "Subsystems";

    private static final float PEAK_CR_MULT = 2f;
    private static final float CR_LOSS_MULT = -0.25f; //this value is a post-scaling of the other two factors.
    //if they are reduced, this will be reduced as well.

    @Override
    public String getKey() {
        return UPGRADE_KEY;
    }

    @Override
    public String getDescription() {
        return "Improve peak performance time.";
    }

    @Override
    public void applyUpgradeToStats(FleetMemberAPI fm, MutableShipStatsAPI stats, float hullSizeFactor, int level, float quality) {

        double v = level * quality * PEAK_CR_MULT * hullSizeFactor;
        if (fm.getHullSpec().getHullSize() == ShipAPI.HullSize.CAPITAL_SHIP) {
            v *= 1;
        } else if (fm.getHullSpec().getHullSize() == ShipAPI.HullSize.CRUISER) {
            v *= 1.5;
        } else if (fm.getHullSpec().getHullSize() == ShipAPI.HullSize.DESTROYER) {
            v *= 2.5;
        } else {
            v *= 7;
        }

        stats.getPeakCRDuration().modifyPercent(this.getBuffId(), (float) v);
        stats.getCRLossPerSecondPercent().modifyPercent(this.getBuffId(), (float) Math.max(CR_LOSS_MULT * v, -90f));
    }

    @Override
    public void modifyToolTip(TooltipMakerAPI tooltip, FleetMemberAPI fm, ExtraSystems systems) {
        int level = systems.getUpgrade(this);

        if (level > 0) {
            tooltip.addPara(this.getName() + " (%s):", 5, Color.green, String.valueOf(level));

            StatUtils.addPercentBonusToTooltip(tooltip, "  Peak performance time: %s",
                    fm.getStats().getPeakCRDuration().getPercentBonus(this.getBuffId()).getValue(),
                    fm.getVariant().getHullSpec().getNoCRLossTime());

            StatUtils.addPercentBonusToTooltip(tooltip, "  CR degradation after peak performance time: %s",
                    fm.getStats().getCRLossPerSecondPercent().getPercentBonus(this.getBuffId()).getValue(),
                    fm.getVariant().getHullSpec().getCRLossPerSecond());
        }
    }
}
