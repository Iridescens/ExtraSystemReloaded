package extrasystemreloaded.util;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.WeaponAPI;
import com.fs.starfarer.api.combat.listeners.WeaponOPCostModifier;
import com.fs.starfarer.api.loading.WeaponSpecAPI;
import extrasystemreloaded.hullmods.ExtraSystemHM;

import java.util.Map;

public abstract class OPCostListener implements WeaponOPCostModifier {
    protected abstract Map<WeaponAPI.WeaponType, Map<WeaponAPI.WeaponSize, Integer>> getModifierMap();

    private int getOPModifier(WeaponAPI.WeaponType type, WeaponAPI.WeaponSize size) {
        if (!getModifierMap().containsKey(type) || !getModifierMap().get(type).containsKey(size)) {
            return 0;
        }

        return getModifierMap().get(type).get(size);
    }

    @Override
    public int getWeaponOPCost(MutableShipStatsAPI mutableShipStatsAPI, WeaponSpecAPI weaponSpecAPI, int i) {
        return i + getOPModifier(weaponSpecAPI.getType(), weaponSpecAPI.getSize());
    }
}
