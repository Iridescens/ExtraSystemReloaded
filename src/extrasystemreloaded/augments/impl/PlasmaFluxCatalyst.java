package extrasystemreloaded.augments.impl;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.WeaponAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import extrasystemreloaded.augments.Augment;
import extrasystemreloaded.util.ExtraSystems;
import extrasystemreloaded.util.OPCostListener;
import extrasystemreloaded.util.Utilities;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class PlasmaFluxCatalyst extends Augment {
    public static final String AUGMENT_KEY = "PlasmaCatalyst";
    private static final String ITEM = "esr_plasmacatalyst";

    private static Map<WeaponAPI.WeaponSize, Integer> OP_MODIFIER_ENERGY = new HashMap() {{
        put(WeaponAPI.WeaponSize.SMALL, -1);
        put(WeaponAPI.WeaponSize.MEDIUM, -2);
        put(WeaponAPI.WeaponSize.LARGE, -4);
    }};

    private static Map<WeaponAPI.WeaponSize, Integer> OP_MODIFIER_BALLISTIC = new HashMap() {{
        put(WeaponAPI.WeaponSize.SMALL, 0);
        put(WeaponAPI.WeaponSize.MEDIUM, 1);
        put(WeaponAPI.WeaponSize.LARGE, 2);
    }};

    private static Map<WeaponAPI.WeaponType, Map<WeaponAPI.WeaponSize, Integer>> OP_MODIFIERS = new HashMap() {{
        put(WeaponAPI.WeaponType.ENERGY, OP_MODIFIER_ENERGY);
        put(WeaponAPI.WeaponType.BALLISTIC, OP_MODIFIER_BALLISTIC);
    }};

    @Override
    public String getKey() {
        return AUGMENT_KEY;
    }

    @Override
    public String getName() {
        return Global.getSettings().getString("AbilityName", getKey());
    }

    @Override
    public String getDescription() {
        return "A Plasma Flux Catalyst can be used to vastly decrease the amount of equipment needed to charge energy-based " +
                "weaponry thanks to its ability to extract that energy from any number of capacitors in parallel." +
                "The space saved from the capacitors allows for more complicated weaponry to be installed, " +
                "although the resulting heat from such a system creates a requirement for advanced heat sinks " +
                "to ensure that moving parts, like those used in ammo feeders for ballistic weaponry, will not overheat.";
    }

    @Override
    public String getTooltip() {
        return "Improve energy ordnance capacity. Reduce ballistic ordnance capacity.";
    }

    @Override
    public boolean canApply(CampaignFleetAPI fleet, FleetMemberAPI fm) {
        return Utilities.playerHasSpecialItem(ITEM);
    }

    public String getUnableToApplyTooltip(CampaignFleetAPI fleet, FleetMemberAPI fm) {
        return "You need a Plasma Flux Catalyst to install this.";
    }

    @Override
    public boolean removeItemsFromFleet(CampaignFleetAPI fleet, FleetMemberAPI fm) {
        Utilities.removePlayerSpecialItem(ITEM);

        return true;
    }

    @Override
    public void modifyToolTip(TooltipMakerAPI tooltip, FleetMemberAPI fm, ExtraSystems systems) {
        if (systems.hasAugment(this.getKey())) {
            tooltip.addPara(this.getName() + ": Reduces energy weapon OP costs by 1/2/4. Increases ballistic weapon OP costs by 0/1/2.", Color.orange, 5);
        }
    }

    @Override
    public void applyUpgradeToStats(FleetMemberAPI fm, MutableShipStatsAPI stats, float quality, String id) {
        if(!stats.hasListenerOfClass(ESR_PlasmaCatalystListener.class)) {
            stats.addListener(new ESR_PlasmaCatalystListener());
        }
    }

    private static class ESR_PlasmaCatalystListener extends OPCostListener {
        @Override
        protected Map<WeaponAPI.WeaponType, Map<WeaponAPI.WeaponSize, Integer>> getModifierMap() {
            return OP_MODIFIERS;
        }
    }
}
