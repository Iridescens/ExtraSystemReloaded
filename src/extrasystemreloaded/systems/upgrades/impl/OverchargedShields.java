package extrasystemreloaded.systems.upgrades.impl;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShieldAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import extrasystemreloaded.systems.upgrades.Upgrade;
import extrasystemreloaded.util.ExtraSystems;
import extrasystemreloaded.util.StatUtils;
import extrasystemreloaded.util.StringUtils;
import lombok.Getter;

import java.awt.*;

public class OverchargedShields extends Upgrade {
    @Getter protected final float bandwidthUsage = 15f;
    private static float FLUX_PER_DAM_MAX = -25f;
    private static float ARC_MAX = 40f;
    private static float UPKEEP_MAX = 30f;
    private static float UNFOLD_MAX = -25f;
    private static float TURNRATE_MAX = -25f;

    @Override
    public boolean shouldShow(FleetMemberAPI fm, ExtraSystems es) {
        if(!ShieldAPI.ShieldType.FRONT.equals(fm.getHullSpec().getDefenseType())
            && !ShieldAPI.ShieldType.OMNI.equals(fm.getHullSpec().getDefenseType())) {
            return false;
        }

        return super.shouldShow(fm, es);
    }

    @Override
    public void applyUpgradeToStats(FleetMemberAPI fm, MutableShipStatsAPI stats, int level, int maxLevel) {
        StatUtils.setStatMult(stats.getShieldDamageTakenMult(), this.getBuffId(), level, FLUX_PER_DAM_MAX, maxLevel);
        StatUtils.setStatPercent(stats.getShieldArcBonus(), this.getBuffId(), level, ARC_MAX, maxLevel);

        StatUtils.setStatPercent(stats.getShieldUpkeepMult(), this.getBuffId(), level, UPKEEP_MAX, maxLevel);
        StatUtils.setStatMult(stats.getShieldUnfoldRateMult(), this.getBuffId(), level, UNFOLD_MAX, maxLevel);
        StatUtils.setStatMult(stats.getShieldTurnRateMult(), this.getBuffId(), level, TURNRATE_MAX, maxLevel);
    }

    @Override
    public void modifyToolTip(TooltipMakerAPI tooltip, FleetMemberAPI fm, ExtraSystems systems, boolean expand) {
        int level = systems.getUpgrade(this);

        if (level > 0) {
            if(expand) {
                tooltip.addPara(this.getName() + " (%s):", 5, Color.green, String.valueOf(level));

                float shieldEffMod = fm.getStats().getShieldDamageTakenMult().getMultStatMod(this.getBuffId()).getValue();
                float shieldEffMult = -(1f - shieldEffMod);
                float shieldEffFinal = fm.getHullSpec().getBaseShieldFluxPerDamageAbsorbed() * shieldEffMult;
                StringUtils.getTranslation(this.getKey(), "shieldEfficiency")
                        .formatWithOneDecimalAndModifier("percent", shieldEffMult * 100f)
                        .format("finalValue", String.valueOf(Math.round(shieldEffFinal * 100f) / 100f))
                        .addToTooltip(tooltip, 2f);

                this.addIncreaseWithFinalToTooltip(tooltip,
                        "shieldArc",
                        fm.getStats().getShieldArcBonus().getPercentBonus(this.getBuffId()).getValue(),
                        fm.getHullSpec().getShieldSpec().getArc());

                this.addIncreaseWithFinalToTooltip(tooltip,
                        "shieldUpkeep",
                        fm.getStats().getShieldUpkeepMult().getPercentStatMod(this.getBuffId()).getValue(),
                        fm.getHullSpec().getShieldSpec().getUpkeepCost());

                this.addDecreaseToTooltip(tooltip,
                        "shieldUnfoldRate",
                        fm.getStats().getShieldUnfoldRateMult().getMultStatMod(this.getBuffId()).getValue());

                this.addDecreaseToTooltip(tooltip,
                        "shieldTurnRate",
                        fm.getStats().getShieldTurnRateMult().getMultStatMod(this.getBuffId()).getValue());
            } else {
                tooltip.addPara(this.getName() + " (%s)", 5, Color.green, String.valueOf(level));
            }
        }
    }
}
