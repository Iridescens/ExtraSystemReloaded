package extrasystemreloaded.systems.upgrades.impl;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShieldAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import extrasystemreloaded.util.ExtraSystems;
import extrasystemreloaded.util.StatUtils;
import extrasystemreloaded.systems.upgrades.Upgrade;
import extrasystemreloaded.util.StringUtils;
import org.json.JSONException;

import java.awt.*;

public class Technology extends Upgrade {
    private static String NAME = "Technology";
    private static float SENSOR_MULT;

    private static float FLUX_SCALAR;
    private static float FLUX_QUALITY_MULT;

    private static float SHIELD_FLUX_SEC_SCALAR;
    private static float SHIELD_FLUX_QUALITY_MULT;

    private static float SHIELD_FLUX_DAM_SCALAR;
    private static float SHIELD_FLUX_DAM_QUALITY_MULT;

    private static float SHIELD_UNFOLD_RATE_MULT;

    private static float PHASE_FLUX_SEC_SCALAR;
    private static float PHASE_FLUX_QUALITY_MULT;

    private static float PHASE_ACTIVATE_FLUX_SCALAR;
    private static float PHASE_ACTIVATE_FLUX_QUALITY_MULT;

    private static float PHASE_COOLDOWN_MULT;

    @Override
    protected void loadConfig() throws JSONException {
        SENSOR_MULT = (float) upgradeSettings.getDouble("sensorScalar");

        FLUX_SCALAR = (float) upgradeSettings.getDouble("fluxUpgradeScalar");
        FLUX_QUALITY_MULT = (float) upgradeSettings.getDouble("fluxQualityMult");

        SHIELD_FLUX_SEC_SCALAR = (float) upgradeSettings.getDouble("shieldFluxPerSecUpgradeScalar");
        SHIELD_FLUX_QUALITY_MULT = (float) upgradeSettings.getDouble("shieldFluxPerSecQualityMult");
        SHIELD_FLUX_DAM_SCALAR = (float) upgradeSettings.getDouble("shieldFluxPerDamUpgradeScalar");
        SHIELD_FLUX_DAM_QUALITY_MULT = (float) upgradeSettings.getDouble("shieldFluxPerDamQualityMult");
        SHIELD_UNFOLD_RATE_MULT = (float) upgradeSettings.getDouble("shieldUnfoldRateScalar");

        PHASE_FLUX_SEC_SCALAR = (float) upgradeSettings.getDouble("phaseFluxPerSecUpgradeScalar");
        PHASE_FLUX_QUALITY_MULT = (float) upgradeSettings.getDouble("phaseFluxPerSecQualityMult");
        PHASE_ACTIVATE_FLUX_SCALAR = (float) upgradeSettings.getDouble("phaseActivateFluxUpgradeScalar");
        PHASE_ACTIVATE_FLUX_QUALITY_MULT = (float) upgradeSettings.getDouble("phaseActivateFluxQualityMult");
        PHASE_COOLDOWN_MULT = (float) upgradeSettings.getDouble("phaseCooldownScalar");
    }

    @Override
    public void applyUpgradeToStats(FleetMemberAPI fm, MutableShipStatsAPI stats, float hullSizeFactor, int level, float quality) {
        StatUtils.setStatMultBonus(stats.getSensorProfile(), this.getBuffId(), level, quality, -SENSOR_MULT, hullSizeFactor);
        StatUtils.setStatPercentBonus(stats.getSensorStrength(), this.getBuffId(), level, quality, SENSOR_MULT, hullSizeFactor);

        StatUtils.setStatMultBonus(stats.getFluxCapacity(), this.getBuffId(),
                1f +
                StatUtils.getDiminishingReturnsTotal(level, getMaxLevel(fm.getHullSpec().getHullSize()), quality, FLUX_SCALAR, FLUX_QUALITY_MULT, hullSizeFactor) / 100f
        );
        StatUtils.setStatMultBonus(stats.getFluxDissipation(), this.getBuffId(),
                1f +
                StatUtils.getDiminishingReturnsTotal(level, getMaxLevel(fm.getHullSpec().getHullSize()), quality, FLUX_SCALAR, FLUX_QUALITY_MULT, hullSizeFactor) / 100f
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

                this.addIncreaseToTooltip(tooltip,
                        "fluxStatsIncrease",
                        (fm.getStats().getFluxCapacity().getMultStatMod(this.getBuffId()).getValue() - 1f) * 100f);

                StringUtils.getTranslation(this.getKey(),"sensorStatsIncrease")
                        .format("percentIncrease", fm.getStats().getSensorStrength().getPercentStatMod(this.getBuffId()).getValue())
                        .format("strengthFinal", fm.getStats().getSensorStrength().getBaseValue() * fm.getStats().getSensorStrength().getPercentStatMod(this.getBuffId()).getValue() / 100f)
                        .format("profileFinal", fm.getStats().getSensorProfile().getBaseValue() * -(1f - fm.getStats().getSensorProfile().getMultStatMod(this.getBuffId()).getValue()))
                        .addToTooltip(tooltip, 2f);

                if (fm.getHullSpec() != null &&
                        (fm.getHullSpec().getShieldType() == ShieldAPI.ShieldType.FRONT ||
                                fm.getHullSpec().getShieldType() == ShieldAPI.ShieldType.OMNI)) {

                    this.addDecreaseWithFinalToTooltip(tooltip,
                            "shieldDamageDecrease",
                            fm.getStats().getShieldDamageTakenMult().getMultStatMod(this.getBuffId()).getValue(),
                            fm.getStats().getShieldDamageTakenMult().getBaseValue());

                    this.addDecreaseWithFinalToTooltip(tooltip,
                            "shieldUpkeepDecrease",
                            fm.getStats().getShieldUpkeepMult().getMultStatMod(this.getBuffId()).getValue(),
                            fm.getStats().getShieldUpkeepMult().getBaseValue());

                    this.addIncreaseToTooltip(tooltip,
                            "shieldOpenSpeedIncrease",
                            fm.getStats().getShieldUnfoldRateMult().getPercentStatMod(this.getBuffId()).getValue());

                } else if (fm.getHullSpec() != null &&
                        fm.getHullSpec().getShieldType() == ShieldAPI.ShieldType.PHASE) {

                    this.addDecreaseWithFinalToTooltip(tooltip,
                            "phaseActivationDecrease",
                            fm.getStats().getPhaseCloakActivationCostBonus().getMultBonus(this.getBuffId()).getValue(),
                            fm.getVariant().getHullSpec().getShieldSpec().getPhaseCost());

                    this.addDecreaseWithFinalToTooltip(tooltip,
                            "phaseUpkeepDecrease",
                            fm.getStats().getPhaseCloakUpkeepCostBonus().getMultBonus(this.getBuffId()).getValue(),
                            fm.getVariant().getHullSpec().getShieldSpec().getPhaseUpkeep());

                    this.addDecreaseToTooltip(tooltip,
                            "phaseCooldownDecrease",
                            fm.getStats().getPhaseCloakCooldownBonus().getMultBonus(this.getBuffId()).getValue());
                }
            } else {
                tooltip.addPara(this.getName() + " (%s)", 5, Color.green, String.valueOf(level));
            }
        }
    }
}
