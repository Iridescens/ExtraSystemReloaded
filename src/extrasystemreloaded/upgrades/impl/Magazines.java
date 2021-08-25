package extrasystemreloaded.upgrades.impl;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.WeaponAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import extrasystemreloaded.util.ExtraSystems;
import extrasystemreloaded.util.StatUtils;
import extrasystemreloaded.upgrades.Upgrade;

import java.awt.*;

public class Magazines extends Upgrade {
    public static final String UPGRADE_KEY = "Magazines";


    private static final float RELOAD_PER_SECOND_MULT = 0.025f;
    private static final float MISSILE_MAGAZINE_MULT = 2f;

    @Override
    public String getKey() {
        return UPGRADE_KEY;
    }


    @Override
    public String getDescription() {
        return "Increases missile ammo capacity and the rate at which all weapons reload limited ammunition.";
    }

    @Override
    public void advanceInCombat(ShipAPI ship, float amount, int level, float quality, float hullSizeFactor) {
        CombatEngineAPI engine = Global.getCombatEngine();
        if (engine.isPaused() || !ship.isAlive()) {
            return;
        }
        for (WeaponAPI w : ship.getAllWeapons()) {
            //only bother with ammo regenerators

            float reloadRate = w.getAmmoPerSecond();
            if (w.usesAmmo() && reloadRate > 0) {
                float nuCharge = reloadRate * (1f + (level * quality * RELOAD_PER_SECOND_MULT * hullSizeFactor));
                w.getAmmoTracker().setAmmoPerSecond(nuCharge);
            }
        }
    }

    @Override
    public void applyUpgradeToStats(FleetMemberAPI fm, MutableShipStatsAPI stats, float hullSizeFactor, int level, float quality) {
        StatUtils.setStatPercentBonus(stats.getMissileAmmoBonus(), this.getBuffId(), level, quality, MISSILE_MAGAZINE_MULT, hullSizeFactor);
    }

    @Override
    public void modifyToolTip(TooltipMakerAPI tooltip, FleetMemberAPI fm, ExtraSystems systems, boolean expand) {
        int level = systems.getUpgrade(this.getKey());
        float quality = systems.getQuality(fm);

        if (level > 0) {
            if(expand) {
                tooltip.addPara(this.getName() + " (%s):", 5, Color.green, String.valueOf(level));

                StatUtils.addPercentBonusToTooltip(tooltip, "  Bonus missile ammunition: +%s",
                        fm.getStats().getMissileAmmoBonus().getPercentBonus(this.getBuffId()).getValue());

                StatUtils.addPercentBonusToTooltip(tooltip, "  Magazine reload speed: +%s per second",
                        level * quality * RELOAD_PER_SECOND_MULT);
            } else {
                tooltip.addPara(this.getName() + " (%s)", 5, Color.green, String.valueOf(level));
            }
        }
    }
}
