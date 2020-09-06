package extrasystemreloaded.campaign;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.econ.SubmarketAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.combat.EngagementResultAPI;
import com.fs.starfarer.api.impl.campaign.ids.Submarkets;
import extrasystemreloaded.util.Es_GameSetPausePlugin;

import java.awt.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Es_ShipTradeFunctionPlugin implements InteractionDialogPlugin {
	private InteractionDialogAPI dialog;
	private TextPanelAPI textPanel;
	private OptionPanelAPI options;
	private VisualPanelAPI visual;
	private CampaignFleetAPI playerFleet;
	private SectorAPI sector;
	private Es_ShipTradeSaveData tradeData;
	private static final String SHIP_TRADE_SAVE_ID = "Es_ShipTradeSaveData";
	public static class OptionId {
		public static final OptionId INIT = new OptionId();
		public static final OptionId ORDER_INIT = new OptionId();//下单
		public static final OptionId ORDER_CHOOSE_SHIPSIZE = new OptionId();//选择舰船种类
		public static final OptionId ORDER_CHOOSE_COP = new OptionId();//选择中介
		public static final OptionId ORDER_CHOOSE_SHIP = new OptionId();//选择船只
		public static final OptionId ORDER_TO_GERERATE = new OptionId();//生成订单
		public static final OptionId GERERATE_QUALITY = new OptionId();//品质
		public static final OptionId GERERATE_FREIGHT = new OptionId();//运费
		public static final OptionId GERERATE_PLACE = new OptionId();//运输地区
		public static final OptionId MAKE_SUREID = new OptionId();//确认
		public static final OptionId ORDER_PRESENT = new OptionId();//目前订单
		public static final OptionId LEAVE = new OptionId();
		public static final OptionId BACK = new OptionId();
	}
	@Override
	public void init(InteractionDialogAPI dialog) {
		this.dialog = dialog;
		tradeData = (Es_ShipTradeSaveData) Global.getSector().getPersistentData().get(SHIP_TRADE_SAVE_ID);
		textPanel = dialog.getTextPanel();
		options = dialog.getOptionPanel();
		visual = dialog.getVisualPanel();
		sector = Global.getSector();
		playerFleet = (CampaignFleetAPI) dialog.getInteractionTarget();
		visual.setVisualFade(0.25f, 0.25f);
		visual.showImagePortion("illustrations", "hound_hangar", 400, 400, 0, 0, 400, 400);
		dialog.setOptionOnEscape("离开", OptionId.LEAVE);
		optionSelected(null, OptionId.INIT);
	}

	@Override
	public void optionSelected(String text, Object optionData) {
		if (optionData == null) return;
		
		OptionId option = (OptionId) optionData;
		
		if (text != null) {
			textPanel.clear();
		}
		if (option == OptionId.INIT) {
			options.addOption("离开", OptionId.LEAVE);
			getPlayerStorages();
		}
		if (option == OptionId.LEAVE) {
			dialog.dismiss();
			Global.getSector().addTransientScript(new Es_GameSetPausePlugin());
			return;
		}
	}
	public List<SubmarketAPI> getPlayerStorages(){
		List<SubmarketAPI>submarketAPIs = new ArrayList<SubmarketAPI>();
		for (StarSystemAPI system : Global.getSector().getStarSystems()) {
			for (PlanetAPI planet : system.getPlanets()) {
				if (planet.getMarket()!=null) {
					MarketAPI marketAPI = planet.getMarket();
					SubmarketAPI submarketAPI = marketAPI.getSubmarket(Submarkets.SUBMARKET_STORAGE);
					if (submarketAPI!=null) {
						SubmarketPlugin plugin = submarketAPI.getPlugin();
						try {
							Field field = plugin.getClass().getDeclaredField("playerPaidToUnlock");//得到
							field.setAccessible(true);  
							boolean isplayerpaid = (boolean) field.get(plugin);
							if (isplayerpaid) {
								submarketAPIs.add(submarketAPI);
							}
						} catch (Exception e) {
							e.printStackTrace();
						} 
					}
				}
			}
		}
		return submarketAPIs;
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
	@Override
	public void optionMousedOver(String optionText, Object optionData) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void advance(float amount) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void backFromEngagement(EngagementResultAPI battleResult) {
		// TODO Auto-generated method stub
		
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