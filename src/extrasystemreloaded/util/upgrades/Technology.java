package extrasystemreloaded.util.upgrades;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShieldAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import extrasystemreloaded.campaign.Es_ShipLevelFleetData;
import extrasystemreloaded.util.ESUpgrades;
import org.apache.log4j.Level;

import java.awt.*;

import static extrasystemreloaded.campaign.Es_ShipLevelFleetData.Es_LEVEL_FUNCTION_ID;

public class Technology extends Upgrade {
    private static final org.apache.log4j.Logger log = Global.getLogger(Technology.class);
    @Override
    public ESUpgrades.UpgradeKey getKey() {
        return ESUpgrades.UpgradeKey.TECHNOLOGY;
    }

    @Override
    public String getName() {
        return Global.getSettings().getString("AbilityName", "Technology");
    }

    @Override
    public String getDescription() {
        return "Improve flux capacity and dissipation, weapon flux cost, shield/phase efficiency.";
    }

    @Override
    public int getMaxLevel() {
        return -1;
    }

    @Override
    public void advanceInCombat(ShipAPI ship, float amount, int level, float quality) {
    }

    @Override
    public void applyUpgradeToStats(FleetMemberAPI fm, MutableShipStatsAPI stats, float hullSizeFactor, int level, float quality) {
        stats.getSensorProfile().modifyPercent(Es_LEVEL_FUNCTION_ID, level * quality * -1f);
        stats.getSensorStrength().modifyPercent(Es_LEVEL_FUNCTION_ID, level * quality * 1f);

        stats.getFluxCapacity().modifyPercent(Es_LEVEL_FUNCTION_ID, level * quality * 2f);
        stats.getFluxDissipation().modifyPercent(Es_LEVEL_FUNCTION_ID, level * quality * 2f);

        stats.getBallisticWeaponFluxCostMod().modifyPercent(Es_LEVEL_FUNCTION_ID, level * quality * -1.5f);
        stats.getMissileWeaponFluxCostMod().modifyPercent(Es_LEVEL_FUNCTION_ID, level * quality * -1.5f);
        stats.getEnergyWeaponFluxCostMod().modifyPercent(Es_LEVEL_FUNCTION_ID, level * quality * -1.5f);

        if (fm.getHullSpec() != null &&
                (fm.getHullSpec().getShieldType() == ShieldAPI.ShieldType.FRONT ||
                        fm.getHullSpec().getShieldType() == ShieldAPI.ShieldType.OMNI)) {
            stats.getShieldDamageTakenMult().modifyPercent(Es_LEVEL_FUNCTION_ID, getFluxUpkeepScale(fm, level, quality));
            stats.getShieldUpkeepMult().modifyPercent(Es_LEVEL_FUNCTION_ID, getFluxUpkeepScale(fm, level, quality));
            stats.getShieldUnfoldRateMult().modifyPercent(Es_LEVEL_FUNCTION_ID, level * quality * 5f);
        } else if (fm.getHullSpec() != null &&
                fm.getHullSpec().getShieldType() == ShieldAPI.ShieldType.PHASE) {
            stats.getPhaseCloakActivationCostBonus().modifyPercent(Es_LEVEL_FUNCTION_ID, getActivationFluxScale(fm, level, quality));
            stats.getPhaseCloakCooldownBonus().modifyPercent(Es_LEVEL_FUNCTION_ID, level * quality * -2.5f);
            stats.getPhaseCloakUpkeepCostBonus().modifyPercent(Es_LEVEL_FUNCTION_ID, getFluxUpkeepScale(fm, level, quality));
        }
    }

    public float getFluxUpkeepScale(FleetMemberAPI fm, int level, float quality) {
        log.log(Level.INFO, String.format("Quality %s Level %s", quality, level));
        return -66f*(1f - (float) Math.exp(-0.5f * quality)) * level / this.getMaxLevel(fm.getHullSpec().getHullSize());
    }

    public float getActivationFluxScale(FleetMemberAPI fm, int level, float quality) {
        log.log(Level.INFO, String.format("Quality %s Level %s", quality, level));
        return -90f*(1f - (float) Math.exp(-0.7f * quality)) * level / this.getMaxLevel(fm.getHullSpec().getHullSize());
    }

    public float getDamageFluxScale(FleetMemberAPI fm, int level, float quality) {
        log.log(Level.INFO, String.format("Quality %s Level %s", quality, level));
        return -66f*(1f - (float) Math.exp(-0.3f * quality)) * level / this.getMaxLevel(fm.getHullSpec().getHullSize());
    }

    @Override
    public void modifyToolTip(TooltipMakerAPI tooltip, FleetMemberAPI fm, Es_ShipLevelFleetData buff) {
        ESUpgrades levels = buff.getUpgrades();

        if(levels.getUpgrade(this.getKey()) > 0) {
            tooltip.addPara(this.getName() + " (%s):", 5, Color.green, String.valueOf(this.getLevel(levels)));
            tooltip.addPara("  Flux capacity and dissipation: +%s (%s,%s)", 0, Misc.getHighlightColor(),
                    String.format("%.3f", fm.getStats().getFluxCapacity().getPercentStatMod(Es_LEVEL_FUNCTION_ID).getValue()) + "%",
                    "+" + String.format("%.0f", fm.getStats().getFluxCapacity().getPercentStatMod(Es_LEVEL_FUNCTION_ID).getValue() * fm.getStats().getFluxCapacity().getBaseValue() * 0.01f),
                    "+" + String.format("%.0f", fm.getStats().getFluxDissipation().getPercentStatMod(Es_LEVEL_FUNCTION_ID).getValue() * fm.getStats().getFluxDissipation().getBaseValue() * 0.01f));
            tooltip.addPara("  Weapon flux cost: %s", 0, Misc.getHighlightColor(), String.format("%.3f", fm.getStats().getBallisticWeaponFluxCostMod().getPercentBonus(Es_LEVEL_FUNCTION_ID).getValue()) + "%");
            tooltip.addPara("  Sensor profile and range: %s (%s,%s)", 0, Misc.getHighlightColor(),
                    String.format("%.3f", fm.getStats().getSensorProfile().getPercentStatMod(Es_LEVEL_FUNCTION_ID).getValue()) + "%",
                    String.format("+%.0f", fm.getStats().getSensorProfile().getPercentStatMod(Es_LEVEL_FUNCTION_ID).getValue() * fm.getStats().getSensorProfile().getBaseValue() * 0.01),
                    String.format("+%.0f", fm.getStats().getSensorStrength().getPercentStatMod(Es_LEVEL_FUNCTION_ID).getValue() * fm.getStats().getSensorStrength().getBaseValue() * 0.01));

            if (fm.getHullSpec() != null &&
                    (fm.getHullSpec().getShieldType() == ShieldAPI.ShieldType.FRONT ||
                            fm.getHullSpec().getShieldType() == ShieldAPI.ShieldType.OMNI)) {
                tooltip.addPara("  Shield damage taken: %s (%s)", 0, Misc.getHighlightColor(), String.format("%.3f", fm.getStats().getShieldDamageTakenMult().getPercentStatMod(Es_LEVEL_FUNCTION_ID).getValue()) + "%", String.format("%.2f", fm.getStats().getShieldDamageTakenMult().getPercentStatMod(Es_LEVEL_FUNCTION_ID).getValue() * fm.getStats().getShieldDamageTakenMult().getBaseValue() * 0.01f));
                tooltip.addPara("  Shield upkeep: %s (%s)", 0, Misc.getHighlightColor(), String.format("%.3f", fm.getStats().getShieldUpkeepMult().getPercentStatMod(Es_LEVEL_FUNCTION_ID).getValue()) + "%", String.format("%.2f", fm.getStats().getShieldUpkeepMult().getPercentStatMod(Es_LEVEL_FUNCTION_ID).getValue() * fm.getVariant().getHullSpec().getShieldSpec().getUpkeepCost() * 0.01f));
                tooltip.addPara("  Shield unfold rate: +%s", 0, Misc.getHighlightColor(), String.format("%.3f", fm.getStats().getShieldUnfoldRateMult().getPercentStatMod(Es_LEVEL_FUNCTION_ID).getValue()) + "%");
            } else if (fm.getHullSpec() != null &&
                    fm.getHullSpec().getShieldType() == ShieldAPI.ShieldType.PHASE) {
                tooltip.addPara("  Phase cloak activation cost: %s (%s)", 0, Misc.getHighlightColor(), String.format("%.3f", fm.getStats().getPhaseCloakActivationCostBonus().getPercentBonus(Es_LEVEL_FUNCTION_ID).getValue()) + "%", String.format("%.2f", fm.getStats().getPhaseCloakActivationCostBonus().getPercentBonus(Es_LEVEL_FUNCTION_ID).getValue() * fm.getVariant().getHullSpec().getShieldSpec().getPhaseCost() * 0.01f));
                tooltip.addPara("  Phase cloak cooldown: %s", 0, Misc.getHighlightColor(), String.format("%.3f", fm.getStats().getPhaseCloakCooldownBonus().getPercentBonus(Es_LEVEL_FUNCTION_ID).getValue()) + "%");
                tooltip.addPara("  Phase cloak upkeep Cost: %s (%s)", 0, Misc.getHighlightColor(), String.format("%.3f", fm.getStats().getPhaseCloakUpkeepCostBonus().getPercentBonus(Es_LEVEL_FUNCTION_ID).getValue()) + "%", String.format("%.2f", fm.getStats().getPhaseCloakUpkeepCostBonus().getPercentBonus(Es_LEVEL_FUNCTION_ID).getValue() * fm.getVariant().getHullSpec().getShieldSpec().getPhaseUpkeep() * 0.01f));
            }
        }
    }
}
