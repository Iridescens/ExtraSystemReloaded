package extrasystemreloaded.systems.upgrades.impl;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import extrasystemreloaded.util.ExtraSystems;
import extrasystemreloaded.util.StatUtils;
import extrasystemreloaded.systems.upgrades.Upgrade;
import org.json.JSONException;
import org.json.JSONObject;

import java.awt.*;

public class Subsystems extends Upgrade {
    public static final String UPGRADE_KEY = "Subsystems";

    private static String NAME = "Subsystems";
    private static float PEAK_CR_MULT;
    private static float CR_LOSS_MULT; //this value is a post-scaling of the other two factors.
    //if they are reduced, this will be reduced as well.

    private static float FRIGATE_MULT;
    private static float DESTROYER_MULT;
    private static float CRUISER_MULT;
    private static float CAPITAL_MULT;

    @Override
    public String getKey() {
        return UPGRADE_KEY;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    protected void loadConfig(JSONObject upgradeSettings) throws JSONException {
        NAME = upgradeSettings.getString("name");
        PEAK_CR_MULT = (float) upgradeSettings.getDouble("peakCrScalar");
        CR_LOSS_MULT = (float) upgradeSettings.getDouble("crLossScalar");
        FRIGATE_MULT = (float) upgradeSettings.getDouble("frigateMult");
        DESTROYER_MULT = (float) upgradeSettings.getDouble("destroyerMult");
        CRUISER_MULT = (float) upgradeSettings.getDouble("cruiserMult");
        CAPITAL_MULT = (float) upgradeSettings.getDouble("capitalMult");
    }

    @Override
    public String getDescription() {
        return "Improve peak performance time and the rate at which CR decays.";
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
        stats.getCRLossPerSecondPercent().modifyPercent(this.getBuffId(), (float) Math.max(CR_LOSS_MULT * v, -90f));
    }

    @Override
    public void modifyToolTip(TooltipMakerAPI tooltip, FleetMemberAPI fm, ExtraSystems systems, boolean expand) {
        int level = systems.getUpgrade(this);

        if (level > 0) {
            if(expand) {
                tooltip.addPara(this.getName() + " (%s):", 5, Color.green, String.valueOf(level));

                StatUtils.addPercentBonusToTooltip(tooltip, "  Peak performance time: +%s",
                        fm.getStats().getPeakCRDuration().getPercentBonus(this.getBuffId()).getValue(),
                        fm.getVariant().getHullSpec().getNoCRLossTime());

                StatUtils.addMultBonusToTooltip(tooltip, "  CR degradation after peak performance time: %s",
                        fm.getStats().getCRLossPerSecondPercent().getPercentBonus(this.getBuffId()).getValue(),
                        fm.getVariant().getHullSpec().getCRLossPerSecond());
            } else {
                tooltip.addPara(this.getName() + " (%s)", 5, Color.green, String.valueOf(level));
            }
        }
    }
}
