package extrasystemreloaded.campaign.battle;

import com.fs.starfarer.api.campaign.BaseCampaignEventListener;
import com.fs.starfarer.api.campaign.EngagementResultForFleetAPI;
import com.fs.starfarer.api.combat.EngagementResultAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import extrasystemreloaded.Es_ModPlugin;
import extrasystemreloaded.hullmods.ExtraSystemHM;

import java.util.ArrayList;
import java.util.List;

public class EngagementListener extends BaseCampaignEventListener {
    public EngagementListener(boolean permaRegister) {
        super(permaRegister);
    }

    @Override
    public void reportPlayerEngagement(EngagementResultAPI result) {
        if(Es_ModPlugin.isKeepUpgradesOnDeath()) return;

        EngagementResultForFleetAPI playerResult = result.didPlayerWin()
                ? result.getWinnerResult()
                : result.getLoserResult();

        List<FleetMemberAPI> fms = new ArrayList<>();
            fms.addAll(playerResult.getDisabled());
            fms.addAll(playerResult.getDestroyed());

        for(FleetMemberAPI fm : fms) {
            ExtraSystemHM.removeFromFleetMember(fm);
            Es_ModPlugin.removeData(fm.getId());
        }
    }
}
