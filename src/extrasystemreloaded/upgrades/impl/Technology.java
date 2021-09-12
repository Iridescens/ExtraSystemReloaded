package extrasystemreloaded.upgrades.impl;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShieldAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import extrasystemreloaded.util.ExtraSystems;
import extrasystemreloaded.util.StatUtils;
import extrasystemreloaded.upgrades.Upgrade;

import java.awt.*;

public class Technology extends Upgrade {
    public static final String UPGRADE_KEY = "Technology";

    private static final float SENSOR_MULT = 1f;

    private static final float FLUX_SCALAR = 3f;
    private static final float FLUX_QUALITY_MULT = 3.15f;

    private static final float SHIELD_FLUX_SEC_SCALAR = -5f;
    private static final float SHIELD_FLUX_QUALITY_MULT = 2.25f;

    private static final float SHIELD_FLUX_DAM_SCALAR = -1.5f;
    private static final float SHIELD_FLUX_DAM_QUALITY_MULT = 0.45f;

    private static final float SHIELD_UNFOLD_RATE_MULT = 2f;

    private static final float PHASE_FLUX_SEC_SCALAR = -2.1f;
    private static final float PHASE_FLUX_QUALITY_MULT = 3f;

    private static final float PHASE_ACTIVATE_FLUX_SCALAR = -7.75f;
    private static final float PHASE_ACTIVATE_FLUX_QUALITY_MULT = 1.5f;

    private static final float PHASE_COOLDOWN_MULT = -0.75f;

    @Override
    public String getKey() {
        return UPGRADE_KEY;
    }

    @Override
    public String getDescription() {
        return "Improve flux capacity and dissipation, weapon flux cost, shield/phase efficiency.";
    }

    @Override
    public void applyUpgradeToStats(FleetMemberAPI fm, MutableShipStatsAPI stats, float hullSizeFactor, int level, float quality) {
        StatUtils.setStatPercentBonus(stats.getSensorProfile(), this.getBuffId(), level, quality, -SENSOR_MULT, hullSizeFactor);
        StatUtils.setStatPercentBonus(stats.getSensorStrength(), this.getBuffId(), level, quality, SENSOR_MULT, hullSizeFactor);

        StatUtils.setStatPercentBonus(stats.getFluxCapacity(), this.getBuffId(),
                StatUtils.getDiminishingReturnsTotal(level, getMaxLevel(fm.getHullSpec().getHullSize()), quality, FLUX_SCALAR, FLUX_QUALITY_MULT, hullSizeFactor)
        );
        StatUtils.setStatPercentBonus(stats.getFluxDissipation(), this.getBuffId(),
                StatUtils.getDiminishingReturnsTotal(level, getMaxLevel(fm.getHullSpec().getHullSize()), quality, FLUX_SCALAR, FLUX_QUALITY_MULT, hullSizeFactor)
        );

        if (fm.getHullSpec() != null &&
                (fm.getHullSpec().getShieldType() == ShieldAPI.ShieldType.FRONT ||
                        fm.getHullSpec().getShieldType() == ShieldAPI.ShieldType.OMNI)) {

            StatUtils.setStatPercentBonus(stats.getShieldDamageTakenMult(), this.getBuffId(),
                    Math.min(0.9f,
                    StatUtils.getDiminishingReturnsTotal(level, getMaxLevel(fm.getHullSpec().getHullSize()), quality, SHIELD_FLUX_DAM_SCALAR, SHIELD_FLUX_DAM_QUALITY_MULT, hullSizeFactor)
                    )
            );

            StatUtils.setStatPercentBonus(stats.getShieldUpkeepMult(), this.getBuffId(),
                    StatUtils.getDiminishingReturnsTotal(level, getMaxLevel(fm.getHullSpec().getHullSize()), quality, SHIELD_FLUX_SEC_SCALAR, SHIELD_FLUX_QUALITY_MULT, hullSizeFactor)
            );

            StatUtils.setStatPercentBonus(stats.getShieldUnfoldRateMult(), this.getBuffId(), level, quality, SHIELD_UNFOLD_RATE_MULT, hullSizeFactor);

        } else if (fm.getHullSpec() != null &&
                fm.getHullSpec().getShieldType() == ShieldAPI.ShieldType.PHASE) {

            StatUtils.setStatPercentBonus(stats.getPhaseCloakActivationCostBonus(), this.getBuffId(),
                    StatUtils.getDiminishingReturnsTotal(level, getMaxLevel(fm.getHullSpec().getHullSize()), quality, PHASE_ACTIVATE_FLUX_SCALAR, PHASE_ACTIVATE_FLUX_QUALITY_MULT, hullSizeFactor)
            );

            StatUtils.setStatPercentBonus(stats.getPhaseCloakUpkeepCostBonus(), this.getBuffId(),
                    Math.min(0.9f,
                    StatUtils.getDiminishingReturnsTotal(level, getMaxLevel(fm.getHullSpec().getHullSize()), quality, PHASE_FLUX_SEC_SCALAR, PHASE_FLUX_QUALITY_MULT, hullSizeFactor)
                    )
            );

            StatUtils.setStatPercentBonus(stats.getPhaseCloakCooldownBonus(), this.getBuffId(), level, quality, PHASE_COOLDOWN_MULT, hullSizeFactor);
        }
    }

    @Override
    public void modifyToolTip(TooltipMakerAPI tooltip, FleetMemberAPI fm, ExtraSystems systems, boolean expand) {
        int level = systems.getUpgrade(this);

        if (level > 0) {
            if(expand) {
                tooltip.addPara(this.getName() + " (%s):", 5, Color.green, String.valueOf(level));

                StatUtils.addDoublePercentBonusToTooltip(tooltip, "  Flux capacity and dissipation: +%s (%s, %s)",
                        fm.getStats().getFluxCapacity().getPercentStatMod(this.getBuffId()).getValue(),
                        fm.getStats().getFluxCapacity().getBaseValue(), fm.getStats().getFluxDissipation().getBaseValue());

                StatUtils.addDoublePercentBonusToTooltip(tooltip, "  Sensor strength and profile: +/- %s (%s, %s)",
                        fm.getStats().getSensorStrength().getPercentStatMod(this.getBuffId()).getValue(),
                        fm.getStats().getSensorStrength().getBaseValue(), fm.getStats().getSensorProfile().getBaseValue());

                if (fm.getHullSpec() != null &&
                        (fm.getHullSpec().getShieldType() == ShieldAPI.ShieldType.FRONT ||
                                fm.getHullSpec().getShieldType() == ShieldAPI.ShieldType.OMNI)) {

                    StatUtils.addPercentBonusToTooltipUnrounded(tooltip, "  Shield damage taken: %s (%s)",
                            fm.getStats().getShieldDamageTakenMult().getPercentStatMod(this.getBuffId()).getValue(),
                            fm.getStats().getShieldDamageTakenMult().getBaseValue());

                    StatUtils.addPercentBonusToTooltip(tooltip, "  Shield upkeep: %s (%s)",
                            fm.getStats().getShieldUpkeepMult().getPercentStatMod(this.getBuffId()).getValue(),
                            fm.getStats().getShieldUpkeepMult().getBaseValue() * fm.getStats().getFluxDissipation().getBaseValue());

                    StatUtils.addPercentBonusToTooltip(tooltip, "  Shield unfold rate: +%s",
                            fm.getStats().getShieldUnfoldRateMult().getPercentStatMod(this.getBuffId()).getValue());

                } else if (fm.getHullSpec() != null &&
                        fm.getHullSpec().getShieldType() == ShieldAPI.ShieldType.PHASE) {

                    StatUtils.addPercentBonusToTooltip(tooltip, "  Phase cloak activation cost: %s (%s)",
                            fm.getStats().getPhaseCloakActivationCostBonus().getPercentBonus(this.getBuffId()).getValue(),
                            fm.getVariant().getHullSpec().getShieldSpec().getPhaseCost());

                    StatUtils.addPercentBonusToTooltip(tooltip, "  Phase cloak upkeep cost: %s (%s)",
                            fm.getStats().getPhaseCloakUpkeepCostBonus().getPercentBonus(this.getBuffId()).getValue(),
                            fm.getVariant().getHullSpec().getShieldSpec().getPhaseUpkeep());

                    StatUtils.addPercentBonusToTooltip(tooltip, "  Phase cloak cooldown: %s",
                            fm.getStats().getPhaseCloakCooldownBonus().getPercentBonus(this.getBuffId()).getValue());
                }
            } else {
                tooltip.addPara(this.getName() + " (%s)", 5, Color.green, String.valueOf(level));
            }
        }
    }
}
