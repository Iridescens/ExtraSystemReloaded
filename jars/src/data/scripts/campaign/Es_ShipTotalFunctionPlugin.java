//Use to control Ctrl+q
package data.scripts.campaign;

import java.awt.Color;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.FleetMemberPickerListener;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.InteractionDialogPlugin;
import com.fs.starfarer.api.campaign.OptionPanelAPI;
import com.fs.starfarer.api.campaign.SectorAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.TextPanelAPI;
import com.fs.starfarer.api.campaign.VisualPanelAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.combat.BattleCreationContext;
import com.fs.starfarer.api.combat.EngagementResultAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;

import data.scripts.campaign.Es_ShipLevelFunctionPlugin.OptionId;
import data.scripts.util.AchievementData;
import data.scripts.util.AchievementInfo;
import data.scripts.util.Es_GameSetPausePlugin;

public class Es_ShipTotalFunctionPlugin implements InteractionDialogPlugin {
	private static final String ACHIEVEMENT_ID = "AchievementData";
	private static final boolean ACHIEVEMENTSENABLED = Global.getSettings().getBoolean("enableAchievements");
	private InteractionDialogAPI dialog;
	private TextPanelAPI textPanel;
	private OptionPanelAPI options;
	private VisualPanelAPI visual;
	private CampaignFleetAPI playerFleet;
	private SectorAPI sector;
	
	public static class OptionId {
		public static OptionId INIT = new OptionId();
		public static OptionId LEAVE = new OptionId();
		public static OptionId LEVEL = new OptionId();
		public static OptionId TRADE = new OptionId();
		public static OptionId ACHIEVELIST = new OptionId();
	}
	@Override
	public void init(InteractionDialogAPI dialog) {
		this.dialog = dialog;
		textPanel = dialog.getTextPanel();
		options = dialog.getOptionPanel();
		visual = dialog.getVisualPanel();
		sector = Global.getSector();
		playerFleet = (CampaignFleetAPI) dialog.getInteractionTarget();
		visual.setVisualFade(0.25f, 0.25f);
		visual.showImagePortion("illustrations", "jump_point_normal", 640, 400, 0, 0, 640, 400);
		dialog.setOptionOnEscape("Leave", OptionId.LEAVE);
		optionSelected(null, OptionId.INIT);
	}

	@Override
	public void optionSelected(String text, Object optionData) {
		if (optionData == null) return;
		
		OptionId option = (OptionId) optionData;
		
		if (text != null) {
			textPanel.clear();
		}

		if (option == OptionId.LEAVE) {
			dialog.dismiss();
			Global.getSector().addTransientScript(new Es_GameSetPausePlugin());
			return;
		}else if (option == OptionId.INIT) {
			addText("Welcome to ExtraSystem Reloaded!");
			addText("Choose the function you want.");
			options.addOption("ShipLevel System", OptionId.LEVEL);
			options.addOption("OrderShip System", OptionId.TRADE);
			options.addOption("Show Achievements", OptionId.ACHIEVELIST);
			if(!ACHIEVEMENTSENABLED){
				options.setEnabled(OptionId.ACHIEVELIST, false);
			}
			options.setEnabled(OptionId.TRADE, false);
			options.addOption("Leave", OptionId.LEAVE, null);
		}else if (option == OptionId.LEVEL) {
			Es_ExtraSystemController.Enter_Level=true;
			dialog.dismiss();
		}else if (option == OptionId.TRADE) {
			Es_ExtraSystemController.Enter_Trade=true;
			dialog.dismiss();
		}else if (option == OptionId.ACHIEVELIST) {
			final AchievementData data =  (AchievementData) Global.getSector().getPersistentData().get(ACHIEVEMENT_ID);
			for(AchievementInfo info : data.getAchievementMaps().values()){
				if (info.isDone()) {
					addText("**"+info.getName()+"**");
					addText("Conditions:");
					addText(info.getTooltip2(),Color.green);
					addText(info.getTooltip1(),Color.yellow);
					addText("");
				}
			}
		}
	}
	private void addText(String text) {
		textPanel.addParagraph(text);
	}
	private void addText(String text,Color color) {
		textPanel.addParagraph(text,color);
	}
	
	private void appendText(String text) {
		textPanel.appendToLastParagraph(" " + text);
	}
	private void appendText(String text,Color color) {
		textPanel.appendToLastParagraph(" " + text);
		textPanel.highlightLastInLastPara(text, color);
	}
	@Override
	public void optionMousedOver(String optionText, Object optionData) {
		
	}

	@Override
	public void advance(float amount) {
		
	}

	@Override
	public void backFromEngagement(EngagementResultAPI battleResult) {
		
	}

	@Override
	public Object getContext() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, MemoryAPI> getMemoryMap() {
		// TODO Auto-generated method stub
		return null;
	}
}