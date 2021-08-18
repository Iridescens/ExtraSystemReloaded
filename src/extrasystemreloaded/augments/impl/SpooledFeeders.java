package extrasystemreloaded.augments.impl;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.WeaponAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import extrasystemreloaded.util.ExtraSystems;
import extrasystemreloaded.util.OPCostListener;
import extrasystemreloaded.util.Utilities;
import extrasystemreloaded.augments.Augment;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class SpooledFeeders extends Augment {
    public static final String MODULE_KEY = "SpooledFeeders";
    private static final String ITEM = "esr_ammospool";

    private static Map<WeaponAPI.WeaponSize, Integer> OP_MODIFIER_BALLISTIC = new HashMap() {{
        put(WeaponAPI.WeaponSize.SMALL, -1);
        put(WeaponAPI.WeaponSize.MEDIUM, -2);
        put(WeaponAPI.WeaponSize.LARGE, -4);
    }};

    private static Map<WeaponAPI.WeaponSize, Integer> OP_MODIFIER_ENERGY = new HashMap() {{
        put(WeaponAPI.WeaponSize.SMALL, 0);
        put(WeaponAPI.WeaponSize.MEDIUM, 1);
        put(WeaponAPI.WeaponSize.LARGE, 2);
    }};

    private static Map<WeaponAPI.WeaponType, Map<WeaponAPI.WeaponSize, Integer>> OP_MODIFIERS = new HashMap() {{
        put(WeaponAPI.WeaponType.BALLISTIC, OP_MODIFIER_BALLISTIC);
        put(WeaponAPI.WeaponType.ENERGY, OP_MODIFIER_ENERGY);
    }};

    @Override
    public String getKey() {
        return MODULE_KEY;
    }

    @Override
    public String getName() {
        return Global.getSettings().getString("AbilityName", getKey());
    }

    @Override
    public String getDescription() {
        return "Using a Hyperferrous Chain to route ammunition to ballistic weapons is a much simpler and " +
                "space-efficient solution than the more-primitive ammo feeders typically used on ships. " +
                "The movement of the massive chain releases highly conductive flakes, dangerous to even " +
                "heavily protected electrical components, requiring advanced protection for energy weapons.";
    }

    @Override
    public String getTooltip() {
        return "Improve ballistic ordnance capacity. Reduce energy ordnance capacity.";
    }

    @Override
    public boolean canApply(CampaignFleetAPI fleet, FleetMemberAPI fm) {
        return Utilities.playerHasSpecialItem(ITEM);
    }

    public String getUnableToApplyTooltip(CampaignFleetAPI fleet, FleetMemberAPI fm) {
        return "You need a Hyperferrous Chain to install this.";
    }

    @Override
    public boolean removeItemsFromFleet(CampaignFleetAPI fleet, FleetMemberAPI fm) {
        Utilities.removePlayerSpecialItem(ITEM);

        return true;
    }

    @Override
    public void modifyToolTip(TooltipMakerAPI tooltip, FleetMemberAPI fm, ExtraSystems systems) {
        if (systems.hasAugment(this.getKey())) {
            tooltip.addPara(this.getName() + ": Reduces ballistic weapon OP costs by 1/2/4. Increases energy weapon OP costs by 0/1/2.", Color.orange, 5);
        }
    }

    @Override
    public void applyUpgradeToStats(FleetMemberAPI fm, MutableShipStatsAPI stats, float quality, String id) {
        if(!stats.hasListenerOfClass(ESR_SpooledFeederListener.class)) {
            stats.addListener(new ESR_SpooledFeederListener());
        }
    }

    private static class ESR_SpooledFeederListener extends OPCostListener {
        @Override
        protected Map<WeaponAPI.WeaponType, Map<WeaponAPI.WeaponSize, Integer>> getModifierMap() {
            return OP_MODIFIERS;
        }
    }
}
