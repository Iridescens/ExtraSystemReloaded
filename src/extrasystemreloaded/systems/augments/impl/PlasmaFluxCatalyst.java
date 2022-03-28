package extrasystemreloaded.systems.augments.impl;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import extrasystemreloaded.systems.augments.Augment;
import extrasystemreloaded.hullmods.ExtraSystemHM;
import extrasystemreloaded.util.ExtraSystems;
import extrasystemreloaded.util.StringUtils;
import extrasystemreloaded.util.Utilities;
import lombok.Getter;
import org.json.JSONException;
import org.json.JSONObject;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class PlasmaFluxCatalyst extends Augment {
    private static final String ITEM = "esr_plasmacatalyst";
    private static final Color[] tooltipColors = {Color.blue, ExtraSystemHM.infoColor};

    private static Map<ShipAPI.HullSize, Integer> MAX_FLUX_EQUIPMENT = new HashMap() {{
        put(ShipAPI.HullSize.FIGHTER, 10);
        put(ShipAPI.HullSize.FRIGATE, 10);
        put(ShipAPI.HullSize.DESTROYER, 20);
        put(ShipAPI.HullSize.CRUISER, 30);
        put(ShipAPI.HullSize.CAPITAL_SHIP, 50);
    }};

    @Getter private final Color mainColor = Color.blue;

    @Override
    public boolean canApply(CampaignFleetAPI fleet, FleetMemberAPI fm) {
        return Utilities.playerHasSpecialItem(ITEM);
    }

    public String getUnableToApplyTooltip(CampaignFleetAPI fleet, FleetMemberAPI fm) {
        return StringUtils.getTranslation(this.getKey(), "needItem")
                .format("itemName", Global.getSettings().getSpecialItemSpec(ITEM).getName())
                .toString();
    }

    @Override
    public boolean removeItemsFromFleet(CampaignFleetAPI fleet, FleetMemberAPI fm) {
        Utilities.removePlayerSpecialItem(ITEM);

        return true;
    }

    @Override
    public void modifyToolTip(TooltipMakerAPI tooltip, FleetMemberAPI fm, ExtraSystems systems, boolean expand) {
        if (systems.hasAugment(this.getKey())) {
            if(expand) {
                int maxCaps = (int) fm.getFleetCommander().getStats().getMaxCapacitorsBonus().computeEffective(MAX_FLUX_EQUIPMENT.get(fm.getHullSpec().getHullSize()));
                int maxVents = (int) fm.getFleetCommander().getStats().getMaxVentsBonus().computeEffective(MAX_FLUX_EQUIPMENT.get(fm.getHullSpec().getHullSize()));

                StringUtils.getTranslation(this.getKey(), "longDescription")
                        .format("augmentName", this.getName())
                        .format("capacitorLimit", maxCaps / 2)
                        .format("ventLimit", maxVents / 2)
                        .format("crDecrease", 1)
                        .addToTooltip(tooltip, tooltipColors);
            } else {
                tooltip.addPara(this.getName(), tooltipColors[0], 5);
            }
        }
    }

    @Override
    public void applyAugmentToStats(FleetMemberAPI fm, MutableShipStatsAPI stats, float quality, String id) {
        if (fm.getFleetCommander() == null) {
            return;
        }

        int numCapsStats = stats.getVariant().getNumFluxCapacitors();
        int numVentsStats = stats.getVariant().getNumFluxVents();

        int maxCaps = (int) fm.getFleetCommander().getStats().getMaxCapacitorsBonus().computeEffective(MAX_FLUX_EQUIPMENT.get(fm.getHullSpec().getHullSize()));
        int maxVents = (int) fm.getFleetCommander().getStats().getMaxVentsBonus().computeEffective(MAX_FLUX_EQUIPMENT.get(fm.getHullSpec().getHullSize()));

        int crReduction = 0;
        if(numCapsStats > maxCaps / 2) {
            crReduction += numCapsStats - (maxCaps / 2);
        }

        if(numVentsStats > maxVents / 2) {
            crReduction += numVentsStats - (maxVents / 2);
        }

        if(crReduction > 0) {
            stats.getMaxCombatReadiness().modifyFlat(this.getName(), -crReduction / 100f, this.getName());
        }
    }

    @Override
    public void applyAugmentToShip(FleetMemberAPI fm, ShipAPI ship, float quality, String id) {
        int numCaps = ship.getVariant().getNumFluxCapacitors();
        int numVents = ship.getVariant().getNumFluxVents();

        ship.getMutableStats().getFluxCapacity().modifyFlat(this.getBuffId(), numCaps * 200);
        ship.getMutableStats().getFluxDissipation().modifyFlat(this.getBuffId(), numVents * 10);
    }
}
