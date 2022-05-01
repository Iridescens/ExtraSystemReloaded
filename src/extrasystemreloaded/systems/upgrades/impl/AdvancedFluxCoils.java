package extrasystemreloaded.systems.upgrades.impl;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import extrasystemreloaded.systems.upgrades.Upgrade;
import extrasystemreloaded.util.ExtraSystems;
import extrasystemreloaded.util.StatUtils;
import lombok.Getter;

public class AdvancedFluxCoils extends Upgrade {
    @Getter protected final float bandwidthUsage = 30f;
    private static float CAPACITY_MAX = 25f;
    private static float VENT_SPEED_MAX = 25f;
    private static float WEAPON_FLUX_MAX = -10f;

    @Override
    public boolean canApply(FleetMemberAPI fm) {
        if (fm.getHullId().contains("ziggurat")) {
            return super.canApply(fm);
        }

        if (fm.getFleetData() != null
                && (fm.getFleetData().getFleet().getFaction().equals(Factions.OMEGA)
                || fm.getFleetData().getFleet().getFaction().equals(Factions.REMNANTS)
                || fm.getFleetData().getFleet().getFaction().equals(Factions.DERELICT))) {
            return super.canApply(fm);
        }

        return false;
    }

    @Override
    public void applyUpgradeToStats(FleetMemberAPI fm, MutableShipStatsAPI stats, int level, int maxLevel) {
        StatUtils.setStatMult(stats.getFluxCapacity(), this.getBuffId(), level, CAPACITY_MAX, maxLevel);
        StatUtils.setStatPercent(stats.getVentRateMult(), this.getBuffId(), level, VENT_SPEED_MAX, maxLevel);
        StatUtils.setStatMult(stats.getBallisticWeaponFluxCostMod(), this.getBuffId(), level, WEAPON_FLUX_MAX, maxLevel);
        StatUtils.setStatMult(stats.getEnergyWeaponFluxCostMod(), this.getBuffId(), level, WEAPON_FLUX_MAX, maxLevel);
        StatUtils.setStatMult(stats.getMissileWeaponFluxCostMod(), this.getBuffId(), level, WEAPON_FLUX_MAX, maxLevel);
    }

    @Override
    public void modifyToolTip(TooltipMakerAPI tooltip, FleetMemberAPI fm, ExtraSystems systems, boolean expand) {
        int level = systems.getUpgrade(this);

        if (level > 0) {
            if(expand) {
                tooltip.addPara(this.getName() + " (%s):", 5, this.getColor(), String.valueOf(level));

                this.addIncreaseToTooltip(tooltip,
                        "fluxCapacity",
                        (fm.getStats().getFluxCapacity().getMultStatMod(this.getBuffId()).getValue() - 1f) * 100f);

                this.addIncreaseToTooltip(tooltip,
                        "ventSpeed",
                        fm.getStats().getVentRateMult().getPercentStatMod(this.getBuffId()).getValue());

                this.addDecreaseToTooltip(tooltip,
                        "weaponFluxCosts",
                        fm.getStats().getBallisticWeaponFluxCostMod().getPercentBonus(this.getBuffId()).getValue());
            } else {
                tooltip.addPara(this.getName() + " (%s)", 5, this.getColor(), String.valueOf(level));
            }
        }
    }
}
