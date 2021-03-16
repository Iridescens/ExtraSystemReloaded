package extrasystemreloaded.campaign;

import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.LocationAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.util.IntervalUtil;
import extrasystemreloaded.util.ESUpgrades;

import java.util.List;

import static extrasystemreloaded.campaign.Es_ShipLevelFleetData.Es_LEVEL_FUNCTION_ID;


public class Es_ExtraSystemController implements EveryFrameScript {
    public static boolean AIUpgradeOn = Global.getSettings().getBoolean("enabledAIUpgrade");

    private static final IntervalUtil EXTRA_SYSTEMS_INTERVAL = new IntervalUtil(2f, 4f);

    @Override
    public boolean isDone() {
        return false;
    }

    @Override
    public boolean runWhilePaused() {//жљ‚еЃњж—¶д№џдјљзљ„
        return true;
    }

    @Override
    public void advance(float amount) {
        float day = Global.getSector().getClock().convertToDays(amount);
        EXTRA_SYSTEMS_INTERVAL.advance(day);
        if (EXTRA_SYSTEMS_INTERVAL.intervalElapsed()) {
            if (AIUpgradeOn) {
                if (!Global.getSector().isPaused()) {

                    LocationAPI location = Global.getSector().getCurrentLocation();
                    List<CampaignFleetAPI> fleets = location.getFleets();
                    for (CampaignFleetAPI campaignFleetAPI : fleets) {
                        if (campaignFleetAPI.isPlayerFleet()) {
                            continue;
                        }
                        int fp = campaignFleetAPI.getFleetPoints();
                        List<FleetMemberAPI> members = campaignFleetAPI.getFleetData().getMembersListCopy();
                        for (FleetMemberAPI member : members) {
                            if (member.isFighterWing() || member.isStation() || member.isMothballed()) {
                                continue;
                            }
                            if (member.getBuffManager().getBuff(Es_LEVEL_FUNCTION_ID) == null) {
                                member.getBuffManager().addBuff(new Es_ShipLevelFleetData(member, ESUpgrades.generateRandomStats(member, fp)));
                            }
                        }
                    }
                }

            }
        }
    }
}
