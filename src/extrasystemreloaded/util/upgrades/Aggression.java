package extrasystemreloaded.util.upgrades;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import extrasystemreloaded.campaign.Es_ShipLevelFleetData;
import extrasystemreloaded.util.ESUpgrades;
import org.apache.log4j.Level;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

import static extrasystemreloaded.campaign.Es_ShipLevelFleetData.Es_LEVEL_FUNCTION_ID;

public class Aggression extends Upgrade {
    private static Map<String, Float> systemActivationTimes = new HashMap<>();

    public static String AGGRESSION_DATA_KEY = "esr_aggression_data_key";

    @Override
    public ESUpgrades.UpgradeKey getKey() {
        return ESUpgrades.UpgradeKey.AGGRESSION_PROTOCOL;
    }

    @Override
    public String getName() {
        return Global.getSettings().getString("AbilityName", "Aggression");
    }

    @Override
    public String getDescription() {
        return "Grants a massive boost to acceleration and speed when using the ship system.";
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

    @Override
    public void advanceInCombat(ShipAPI ship, float amount, int level, float quality) {
        if(!ship.isAlive()) {
            return;
        }

        CombatEngineAPI engine = Global.getCombatEngine();
        String customDataId = AGGRESSION_DATA_KEY + ship.getId();
        MutableShipStatsAPI stats = ship.getMutableStats();

        if(ship.getSystem().isActive() && !engine.getCustomData().containsKey(customDataId)) {
            engine.getCustomData().put(customDataId, engine.getTotalElapsedTime(false) + 3.5f);
        } else if(!ship.getSystem().isActive() && engine.getCustomData().containsKey(customDataId)) {
            engine.getCustomData().remove(customDataId);
        }

        if(engine.getCustomData().containsKey(customDataId)) {
            float timeLeft = (float) engine.getCustomData().get(customDataId) - engine.getTotalElapsedTime(false);
            if(timeLeft > 0) {
                float timeRatio = Math.max(1, 1 + Math.min(2, timeLeft) / 2f);
                stats.getMaxSpeed().modifyFlat(AGGRESSION_DATA_KEY,
                        (stats.getZeroFluxSpeedBoost().getModifiedValue() * level / this.getMaxLevel() + getQualityBonus(quality))
                                * timeRatio);
                stats.getAcceleration().modifyPercent(AGGRESSION_DATA_KEY, level * quality * timeRatio * 25f);
                stats.getTurnAcceleration().modifyPercent(AGGRESSION_DATA_KEY, level * quality * timeRatio * 25f);
                stats.getMaxTurnRate().modifyPercent(AGGRESSION_DATA_KEY, level * quality * timeRatio * 25f);

                if (ship == Global.getCombatEngine().getPlayerShip()) {
                    engine.maintainStatusForPlayerShip(AGGRESSION_DATA_KEY, "graphics/icons/hullsys/maneuvering_jets.png", "Aggression Protocol", "THEY WILL KNOW FEAR (" + timeLeft + "s)", false);
                }
            } else {
                stats.getMaxSpeed().unmodifyFlat(AGGRESSION_DATA_KEY);
                stats.getAcceleration().unmodifyPercent(AGGRESSION_DATA_KEY);
                stats.getTurnAcceleration().unmodifyPercent(AGGRESSION_DATA_KEY);
                stats.getMaxTurnRate().unmodifyPercent(AGGRESSION_DATA_KEY);
            }
        }
    }

    public float getQualityBonus(float quality) {
        return 25f*(1f - (float) Math.exp(-0.5f * quality));
    }

    @Override
    public void applyUpgradeToStats(FleetMemberAPI fm, MutableShipStatsAPI stats, float hullSizeFactor, int level, float quality) {
    }

    @Override
    public void modifyToolTip(TooltipMakerAPI tooltip, FleetMemberAPI fm, Es_ShipLevelFleetData buff) {
        ESUpgrades levels = buff.getUpgrades();

        if (levels.getUpgrade(this.getKey()) > 0) {
            tooltip.addPara(this.getName() + " (%s)", 5, Color.green, String.valueOf(this.getLevel(levels)));
        }
    }
}
