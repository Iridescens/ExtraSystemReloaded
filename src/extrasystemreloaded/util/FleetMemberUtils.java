package extrasystemreloaded.util;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import org.apache.log4j.Logger;

public class FleetMemberUtils {
    private static final Logger log = Logger.getLogger(FleetMemberUtils.class);

    private FleetMemberUtils() {
    }

    public static FleetMemberAPI findMemberForStats(MutableShipStatsAPI stats) {
        if (stats.getFleetMember() != null) {
            return stats.getFleetMember();
        }

        if (stats.getEntity() instanceof ShipAPI) {
            ShipAPI ship = (ShipAPI) stats.getEntity();
            if (ship.getFleetMember() != null) {
                return ship.getFleetMember();
            }
        }

        return searchFleetForStats(Global.getSector().getPlayerFleet(), stats);
    }

    private static FleetMemberAPI searchFleetForStats(CampaignFleetAPI fleet, MutableShipStatsAPI stats) {
        FleetMemberAPI fm = null;
        for(FleetMemberAPI member : Global.getSector().getPlayerFleet().getFleetData().getMembersListCopy()) {
            if(member.isFighterWing()) continue;
            if(member.getStats() == stats) {
                fm = member;
            } else if (member.getVariant().getStatsForOpCosts() != null) {
                if (member.getVariant().getStatsForOpCosts() == stats) {
                    fm = member;
                } else if (member.getVariant().getStatsForOpCosts().getEntity() != null && member.getVariant().getStatsForOpCosts().getEntity() == stats.getEntity()) {
                    fm = member;
                }
            }

            if(fm != null) break;

            if (member.getVariant().getModuleSlots() != null && !member.getVariant().getModuleSlots().isEmpty()) {
                for(String variantId : member.getVariant().getModuleSlots()) {
                    ShipVariantAPI variant = member.getVariant().getModuleVariant(variantId);

                    if(variant.getStatsForOpCosts() != null) {
                        if (variant.getStatsForOpCosts() == stats) {
                            fm = member;
                        } else if (variant.getStatsForOpCosts().getEntity() != null && stats.getEntity() == variant.getStatsForOpCosts().getEntity()) {
                            fm = member;
                        }

                        if(fm != null) break;
                    }
                }
            }

            if(fm != null) break;
        }
        return fm;
    }
}
