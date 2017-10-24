package data.scripts.campaign;

import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Condition;

import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;

import data.scripts.util.AchievementData;
import data.scripts.util.AchievementInfo;

public class Es_BaseAchievementsUnlockPlugin implements EveryFrameScript {
	private static final String ACHIEVEMENT_ID = "AchievementData";
	private static final String ACHIEVEMENT_CREDITS_ID = "Achievement_Credits";
	@Override
	public boolean isDone() {
		return false;
	}

	@Override
	public boolean runWhilePaused() {
		return true;
	}

	@Override
	public void advance(float amount) {
		final AchievementData data =  (AchievementData) Global.getSector().getPersistentData().get(ACHIEVEMENT_ID);
		final Map<String, Object> conditionData = data.getCustomData();
		final CampaignFleetAPI fleet = Global.getSector().getPlayerFleet();
		boolean condition_credits[] = (boolean[]) conditionData.get(ACHIEVEMENT_CREDITS_ID);
		
		float credits = fleet.getCargo().getCredits().get();
		if (!condition_credits[0] && credits >= 10000f) {
			Global.getSector().getPersistentData().put("Es_tenthousandcredits", true);
			condition_credits[0]=true;
		}else if (!condition_credits[1] && credits >= 20000f) {
			Global.getSector().getPersistentData().put("Es_twentythousandcredits", true);
			condition_credits[1]=true;
		}else if (!condition_credits[2] && credits >= 50000f) {
			Global.getSector().getPersistentData().put("Es_fiftythousandcredits", true);
			condition_credits[2]=true;
		}else if (!condition_credits[3] && credits >= 100000f) {
			Global.getSector().getPersistentData().put("Es_onehundredthousandcredits", true);
			condition_credits[3]=true;
		}
		
		for (FleetMemberAPI member : fleet.getFleetData().getMembersListCopy()) {
			if (member.getHullId().contains("hermes_d")) {
				Global.getSector().getPersistentData().put("Es_niceboat", true);
			}
		}
		
		final Map<String, AchievementInfo>maps = data.getAchievementMaps();
		for (String id : maps.keySet()) {
			AchievementInfo info = maps.get(id);
			if (info.isDone()) {
				continue;
			}
			if (info.isToggle()) {
				Es_CampaignRenderPlugin.unlockAchievementAndDraw(id);
			}
		}
	}
}
