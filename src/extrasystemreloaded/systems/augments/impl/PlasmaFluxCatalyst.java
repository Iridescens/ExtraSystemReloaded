package extrasystemreloaded.systems.augments.impl;

import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import extrasystemreloaded.systems.augments.Augment;
import extrasystemreloaded.hullmods.ExtraSystemHM;
import extrasystemreloaded.util.ExtraSystems;
import extrasystemreloaded.util.Utilities;
import org.json.JSONException;
import org.json.JSONObject;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class PlasmaFluxCatalyst extends Augment {
    public static final String AUGMENT_KEY = "PlasmaCatalyst";
    public static final Color MAIN_COLOR = Color.blue;
    private static final String ITEM = "esr_plasmacatalyst";
    private static final Color[] tooltipColors = {MAIN_COLOR, ExtraSystemHM.infoColor};

    private static Map<ShipAPI.HullSize, Integer> MAX_FLUX_EQUIPMENT = new HashMap() {{
        put(ShipAPI.HullSize.FIGHTER, 10);
        put(ShipAPI.HullSize.FRIGATE, 10);
        put(ShipAPI.HullSize.DESTROYER, 20);
        put(ShipAPI.HullSize.CRUISER, 30);
        put(ShipAPI.HullSize.CAPITAL_SHIP, 50);
    }};

    private static String NAME = "Plasma Flux Catalyst";

    @Override
    public String getKey() {
        return AUGMENT_KEY;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public Color getMainColor() {
        return MAIN_COLOR;
    }

    @Override
    public String getDescription() {
        return "A Plasma Flux Catalyst can be used to vastly decrease the amount of equipment needed to provide " +
                "power to a ship thanks to its ability to extract that energy from any number of capacitors in " +
                "parallel. The space saved allows for more complicated weaponry to be installed, although the " +
                "resulting heat from such a system is dreadful if too many capacitors or flux vents are installed.";
    }

    @Override
    public String getTooltip() {
        return "Improve flux capacitors and vents. Reduce CR if too many are installed.";
    }

    @Override
    public void loadConfig(JSONObject augmentSettings) throws JSONException {
        NAME = augmentSettings.getString("name");
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
    public void modifyToolTip(TooltipMakerAPI tooltip, FleetMemberAPI fm, ExtraSystems systems, boolean expand) {
        if (systems.hasAugment(this.getKey())) {
            if(expand) {
                int maxCaps = (int) fm.getFleetCommander().getStats().getMaxCapacitorsBonus().computeEffective(MAX_FLUX_EQUIPMENT.get(fm.getHullSpec().getHullSize()));
                int maxVents = (int) fm.getFleetCommander().getStats().getMaxVentsBonus().computeEffective(MAX_FLUX_EQUIPMENT.get(fm.getHullSpec().getHullSize()));

                tooltip.addPara("%s: %s the effectiveness of flux capacitors and vents. Installing more than %s capacitors or %s vents will reduce combat readiness by %s for every one installed over that amount. " +
                                            "Note that this decrease doesn't appear immediately inside the refit dialog.", 5, tooltipColors,
                                                        this.getName(), "Doubles", String.valueOf(maxCaps / 2), String.valueOf(maxVents / 2), "1");
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
