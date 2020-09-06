package extrasystemreloaded.campaign;

import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.LocationAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.loading.VariantSource;
import com.fs.starfarer.api.util.IntervalUtil;
import org.lwjgl.input.Keyboard;

import java.util.List;


public class Es_ExtraSystemController implements EveryFrameScript{
	private static final String Es_LEVEL_FUNCTION_ID = "Es_ShipLevelUp";	
	private static final String SHIP_TRADE_SAVE_ID = "Es_ShipTradeSaveData";
	private static final float AI_LEVEL = 0.3f;//aiејєеє¦
	public static  boolean Enter_Level = false;
	public static boolean Enter_Trade = false;
	public static boolean AIUpgradeOn = Global.getSettings().getBoolean("enabledAIUpgrade");
	private static Es_ShipTradeSaveData tradeData;

	private static final IntervalUtil AI_FLEET_REFRESH_INTER = new IntervalUtil(2f, 4f);//иї‡е‡ е¤©пјЊе€·ж–°дёЂж¬Ў
//	private static final IntervalUtil MARKET_REFRESH_MOUTH_ITER = new IntervalUtil(1f, 1f);//её‚ењєе€·ж–°
//	private static final IntervalUtil DAY_ITER = new IntervalUtil(1f, 1f);//жЇЏе¤©и°ѓз”Ё
	@Override
	public boolean isDone() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean runWhilePaused() {//жљ‚еЃњж—¶д№џдјљзљ„
		return true;
	}

	@Override
	public void advance(float amount) {
		if(!(tradeData instanceof Es_ShipTradeSaveData)||tradeData ==null){
			tradeData = (Es_ShipTradeSaveData) Global.getSector().getPersistentData().get(SHIP_TRADE_SAVE_ID);
		}
		if (AIUpgradeOn){
			if (!Global.getSector().isPaused()) {//еЉїеЉ›и€°й�џе€·ж–°
				float day = Global.getSector().getClock().convertToDays(amount);
				AI_FLEET_REFRESH_INTER.advance(day);
				//			MARKET_REFRESH_MOUTH_ITER.advance(day);
				//			DAY_ITER.advance(day);
				//			if (MARKET_REFRESH_MOUTH_ITER.intervalElapsed()) {
				//				Es_ShipTradeSaveData savedata = (Es_ShipTradeSaveData) Global.getSector().getPersistentData().get(SHIP_TRADE_SAVE_ID);
				//				savedata.marketRefresh();
				//			}
				//			if (DAY_ITER.intervalElapsed()) {//и®ўеЌ•
				//				tradeData.advance();//е‰Ќиї›
				//			}
				if (AI_FLEET_REFRESH_INTER.intervalElapsed()) {		

					LocationAPI location = Global.getSector().getCurrentLocation();
					List<CampaignFleetAPI>fleets = location.getFleets();
					for (CampaignFleetAPI campaignFleetAPI : fleets) {
						if (campaignFleetAPI.isPlayerFleet()) {
							continue;
						}
						int opcost = campaignFleetAPI.getFleetPoints();
						List<FleetMemberAPI>members = campaignFleetAPI.getFleetData().getMembersListCopy();
						for (FleetMemberAPI member : members) {
							if (member.isFighterWing()) {
								continue;
							}
							if (member.getBuffManager().getBuff(Es_LEVEL_FUNCTION_ID)==null) {
								member.getBuffManager().addBuff(new Es_ShipLevelFleetData(member));
								Es_ShipLevelFleetData buff = (Es_ShipLevelFleetData)member.getBuffManager().getBuff(Es_LEVEL_FUNCTION_ID);
								HullSize hullSize = member.getHullSpec().getHullSize();
								int maxlevel = (int)Es_ShipLevelFunctionPlugin.HULLSIZE_TO_MAXLEVEL.get(hullSize);
								float arg1 = opcost/300f;//300дёєжњЂе¤§
								int[] skill = buff.getLevelIndex();
								for (int i = 0; i < skill.length; i++) {
									if (member.isFlagship()) {									
										skill[i] += Math.min(maxlevel, Math.round(maxlevel*arg1*(Math.random()*0.8f+0.2f)*AI_LEVEL));
									}else {									
										skill[i] += Math.min(maxlevel, Math.round(maxlevel*arg1*(Math.random())*AI_LEVEL*0.8f));
									}
								}
							}
						}
					}
				}
			}
		}
////////////////
		CampaignFleetAPI fleet = Global.getSector().getPlayerFleet();
		for (FleetMemberAPI member : fleet.getFleetData().getMembersListCopy()) {
				if (member.getBuffManager().getBuff(Es_LEVEL_FUNCTION_ID)!=null) {
					if (!member.getVariant().hasHullMod("es_shiplevelHM")) {
						Es_ShipLevelFleetData data = (Es_ShipLevelFleetData)member.getBuffManager().getBuff(Es_LEVEL_FUNCTION_ID);
						int[] array = data.getLevelIndex();
						int temp_points = 0;
						for (int i = 0; i < array.length; i++) {
							temp_points += array[i];
						}

						if (temp_points>0){
							ShipVariantAPI v;
							if(member.getVariant().isStockVariant()) {
								v = member.getVariant().clone();
								v.setSource(VariantSource.REFIT);
								member.setVariant(v, false, false);
							} else v = member.getVariant();

							v.setHullVariantId("es_" + member.getId());
							v.addPermaMod("es_shiplevelHM");

							member.updateStats();
						}
					}
				}
		}
////////////////
			  if (Enter_Level) {
				  Global.getSector().getCampaignUI().showInteractionDialog(new Es_ShipLevelFunctionPlugin(), Global.getSector().getPlayerFleet()); 
				  Enter_Level  = false;
				  
			  }
			  if (Enter_Trade) {
				  Global.getSector().getCampaignUI().showInteractionDialog(new Es_ShipTradeFunctionPlugin(), Global.getSector().getPlayerFleet()); 
				  Enter_Trade = false;
			  }
			  if ((Keyboard.isKeyDown(29)) && 
				  (Keyboard.isKeyDown(16))) {
				  Global.getSector().getCampaignUI().showInteractionDialog(new Es_ShipTotalFunctionPlugin(), Global.getSector().getPlayerFleet());
			  }
//			  if ((Keyboard.isKeyDown(29)) && 
//				      (Keyboard.isKeyDown(36))) {
////					Vector2f center = new Vector2f(Display.getWidth() / 2,Display.getHeight() / 8 * 7);
////					SpriteAPI spriteAPI = Global.getSettings().getSprite("graphics/achievements/Es_Achievement_core.png");
////					Es_AchievementSprite sprite = new Es_AchievementSprite(spriteAPI);
////					AchievementUIBaseAnimation core = new AchievementUIBaseAnimation((int) center.x, (int) center.y, spriteAPI.getWidth(),
////					spriteAPI.getHeight(), 1f, 100);
////					core.bindAchievementSprite(sprite);
////					Es_CampaignRenderPlugin.spawnAchievementUI("TEST", core);
////					data.setAchievement("TEST", true);
//				  
//				  	Es_CampaignRenderPlugin.unlockAchievementAndDraw("Es_niceboat");
//			  }
	}

}
