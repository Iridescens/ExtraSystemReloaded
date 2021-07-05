package extrasystemreloaded.util.modules.impl;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import extrasystemreloaded.campaign.Es_ShipLevelFleetData;
import extrasystemreloaded.util.ExtraSystems;
import extrasystemreloaded.util.Utilities;
import extrasystemreloaded.util.modules.Module;

import java.awt.*;

public class SpooledFeeders extends Module {
    public static final String MODULE_KEY = "SpooledFeeders";
    private static final String ITEM = "esr_ammospool";

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
    public void modifyToolTip(TooltipMakerAPI tooltip, FleetMemberAPI fm, Es_ShipLevelFleetData buff) {
        ExtraSystems systems = buff.getExtraSystems();
        if (systems.hasModule(this.getKey())) {
            tooltip.addPara(this.getName() + ": Reduces ballistic weapon OP costs by 1/2/4. Increases energy weapon OP costs by 0/1/2.", Color.orange, 5);
        }
    }

    @Override
    public void applyUpgradeToStats(FleetMemberAPI fm, MutableShipStatsAPI stats, float quality, String id) {
        stats.getDynamic().getMod(Stats.LARGE_BALLISTIC_MOD).modifyFlat(getBuffId(), -4);
        stats.getDynamic().getMod(Stats.MEDIUM_BALLISTIC_MOD).modifyFlat(getBuffId(), -2);
        stats.getDynamic().getMod(Stats.SMALL_BALLISTIC_MOD).modifyFlat(getBuffId(), -1);

        stats.getDynamic().getMod(Stats.LARGE_ENERGY_MOD).modifyFlat(getBuffId(), 2);
        stats.getDynamic().getMod(Stats.MEDIUM_ENERGY_MOD).modifyFlat(getBuffId(), 1);
        //stats.getDynamic().getMod(Stats.SMALL_ENERGY_MOD).modifyFlat(getBuffId(), 0);
    }
}
