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

public class PlasmaFluxCatalyst extends Module {
    public static final String MODULE_KEY = "PlasmaCatalyst";
    private static final String ITEM = "esr_plasmacatalyst";

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
    public void modifyToolTip(TooltipMakerAPI tooltip, FleetMemberAPI fm, Es_ShipLevelFleetData buff) {
        ExtraSystems systems = buff.getExtraSystems();
        if (systems.hasModule(this.getKey())) {
            tooltip.addPara(this.getName() + ": Reduces energy weapon OP costs by 1/2/4. Increases ballistic weapon flux costs by 25%.", Color.orange, 5);
        }
    }

    @Override
    public void applyUpgradeToStats(FleetMemberAPI fm, MutableShipStatsAPI stats, float quality, String id) {
        //modules conflicting with each other
        stats.getDynamic().getMod(Stats.LARGE_ENERGY_MOD).modifyFlat(getBuffId(), -4);
        stats.getDynamic().getMod(Stats.MEDIUM_ENERGY_MOD).modifyFlat(getBuffId(), -2);
        stats.getDynamic().getMod(Stats.SMALL_ENERGY_MOD).modifyFlat(getBuffId(), -1);

        stats.getBallisticWeaponFluxCostMod().modifyPercent(getBuffId(), 25f);
    }
}
