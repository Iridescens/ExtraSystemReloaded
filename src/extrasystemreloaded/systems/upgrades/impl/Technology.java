package extrasystemreloaded.systems.upgrades.impl;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShieldAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import extrasystemreloaded.util.ExtraSystems;
import extrasystemreloaded.util.StatUtils;
import extrasystemreloaded.systems.upgrades.Upgrade;
import org.json.JSONException;
import org.json.JSONObject;

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
    public String getName() {
        return NAME;
    }

    @Override
    protected void loadConfig(JSONObject upgradeSettings) throws JSONException {
        NAME = upgradeSettings.getString("name");
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

                    StatUtils.addMultBonusToTooltipUnrounded(tooltip, "  Shield damage taken: %s (%s)",
                            fm.getStats().getShieldDamageTakenMult().getMultStatMod(this.getBuffId()).getValue(),
                            fm.getStats().getShieldDamageTakenMult().getBaseValue());

                    StatUtils.addMultBonusToTooltip(tooltip, "  Shield upkeep: %s (%s)",
                            fm.getStats().getShieldUpkeepMult().getMultStatMod(this.getBuffId()).getValue(),
                            fm.getStats().getShieldUpkeepMult().getBaseValue() * fm.getStats().getFluxDissipation().getBaseValue());

                    StatUtils.addPercentBonusToTooltip(tooltip, "  Shield unfold rate: +%s",
                            fm.getStats().getShieldUnfoldRateMult().getPercentStatMod(this.getBuffId()).getValue());

                } else if (fm.getHullSpec() != null &&
                        fm.getHullSpec().getShieldType() == ShieldAPI.ShieldType.PHASE) {

                    StatUtils.addMultBonusToTooltip(tooltip, "  Phase cloak activation cost: %s (%s)",
                            fm.getStats().getPhaseCloakActivationCostBonus().getMultBonus(this.getBuffId()).getValue(),
                            fm.getVariant().getHullSpec().getShieldSpec().getPhaseCost());

                    StatUtils.addMultBonusToTooltip(tooltip, "  Phase cloak upkeep cost: %s (%s)",
                            fm.getStats().getPhaseCloakUpkeepCostBonus().getMultBonus(this.getBuffId()).getValue(),
                            fm.getVariant().getHullSpec().getShieldSpec().getPhaseUpkeep());

                    StatUtils.addMultBonusToTooltip(tooltip, "  Phase cloak cooldown: %s",
                            fm.getStats().getPhaseCloakCooldownBonus().getMultBonus(this.getBuffId()).getValue());
                }
            } else {
                tooltip.addPara(this.getName() + " (%s)", 5, Color.green, String.valueOf(level));
            }
        }
    }
}
