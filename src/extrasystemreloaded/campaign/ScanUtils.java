package extrasystemreloaded.campaign;

import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import extrasystemreloaded.Es_ModPlugin;
import extrasystemreloaded.util.ExtraSystems;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j;

import java.util.ArrayList;
import java.util.List;

@Log4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ScanUtils {
    private static float NOTABLE_BANDWIDTH = 180f;

    public static List<FleetMemberAPI> getNotableFleetMembers(CampaignFleetAPI fleet) {
        List<FleetMemberAPI> notableMembers = new ArrayList<>();

        for (FleetMemberAPI fm : fleet.getMembersWithFightersCopy()) {
            if (fm.isFighterWing()) continue;
            if (isFleetMemberNotable(fm)) {
                notableMembers.add(fm);
            }
        }

        log.info(String.format("Fleet has %s notable members", notableMembers.size()));

        return notableMembers;
    }

    public static boolean isFleetMemberNotable(FleetMemberAPI fm) {
        if (Es_ModPlugin.hasData(fm.getId())) {
            ExtraSystems es = Es_ModPlugin.getData(fm.getId());

            log.info(String.format("ExtraSystems info for ship [%s]: upg [%s] aug [%s] bdw [%s]",
                    fm.getShipName(),
                    es.hasUpgrades(),
                    es.hasAugments(),
                    es.getBandwidth(fm)));

            return isESNotable(es);
        }
        return false;
    }

    public static boolean isESNotable(ExtraSystems es) {
        if (es.hasUpgrades() || es.hasAugments() || es.getBandwidth() >= NOTABLE_BANDWIDTH) {
            return true;
        }
        return false;
    }
}
