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

public class FusionMissileIgnition extends Module {
    private static final String ITEM = "esr_fusionmissileignitor";

    @Override
    public String getKey() {
        return "FusionIgnition";
    }

    @Override
    public String getName() {
        return Global.getSettings().getString("AbilityName", getKey());
    }

    @Override
    public String getDescription() {
        return "The ignition torches from an orbital fusion lamp can be used to greatly simplify " +
                "missile launch tubes and equipment. The upgrade requires a small effort of engineering, and the " +
                "result is a greatly simplified missile system, leaving much more room for more complicated missile " +
                "weaponry. The most difficult part is finding the Fusion Missile Ignitor to install.";
    }

    @Override
    public String getTooltip() {
        return "Reduces the ordnance point cost of missiles.";
    }

    @Override
    public boolean canApply(CampaignFleetAPI fleet, FleetMemberAPI fm) {
        return Utilities.playerHasSpecialItem(ITEM);
    }

    public String getUnableToApplyTooltip(CampaignFleetAPI fleet, FleetMemberAPI fm) {
        return "You need a Fusion Missile Ignitor to install this.";
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
            tooltip.addPara(this.getName() + ": Reduces missile weapon OP costs by 0/1/2.", Color.orange, 5);
        }
    }

    @Override
    public void applyUpgradeToStats(FleetMemberAPI fm, MutableShipStatsAPI stats, float quality, String id) {
        stats.getDynamic().getMod(Stats.LARGE_MISSILE_MOD).modifyFlat(getBuffId(), -2);
        stats.getDynamic().getMod(Stats.MEDIUM_MISSILE_MOD).modifyFlat(getBuffId(), -1);
        //stats.getDynamic().getMod(Stats.SMALL_MISSILE_MOD).modifyFlat(getBuffId(), -0);

    }
}
