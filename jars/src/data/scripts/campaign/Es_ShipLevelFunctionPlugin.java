package data.scripts.campaign;

import java.awt.Color;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Level;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.lwjgl.util.vector.Vector2f;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.CargoAPI.CargoItemType;
import com.fs.starfarer.api.campaign.CargoStackAPI;
import com.fs.starfarer.api.campaign.FleetMemberPickerListener;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.InteractionDialogPlugin;
import com.fs.starfarer.api.campaign.OptionPanelAPI;
import com.fs.starfarer.api.campaign.SectorAPI;
import com.fs.starfarer.api.campaign.TextPanelAPI;
import com.fs.starfarer.api.campaign.VisualPanelAPI;
import com.fs.starfarer.api.campaign.BuffManagerAPI.Buff;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.combat.EngagementResultAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.fleet.FleetMemberAPI;

import data.scripts.campaign.Es_ShipTradeSaveData.TradeMarketData;
import data.scripts.util.Es_GameSetPausePlugin;


public class Es_ShipLevelFunctionPlugin implements InteractionDialogPlugin {


	public static class OptionId {
		public static OptionId INIT = new OptionId();
		public static OptionId TEST = new OptionId();//жµ‹иЇ•з”Ё
		public static OptionId Ship_LIST = new OptionId();//и€№е€—иЎЁ
		public static OptionId Back_LIST = new OptionId();//и€№е€—иЎЁ
		public static OptionId Next_LIST = new OptionId();//и€№е€—иЎЁ
		public static OptionId Ability_PICKER = new OptionId();//йЂ‰ж‹©иѓЅеЉ›
		public static OptionId Ability_APPLYER = new OptionId();//ж�Їеђ¦зЎ®е®љ
		public static OptionId LEAVE = new OptionId();
		public static OptionId BACK = new OptionId();
	}
	public static class OptionName {
		public static String Leave = Global.getSettings().getString("Options", "leave");
		public static String Back = Global.getSettings().getString("Options", "back");	
		public static String Confirm = Global.getSettings().getString("Options", "confirm");	
		public static String Cancel = Global.getSettings().getString("Options", "cancel");	
		public static String PreviousP = Global.getSettings().getString("Options", "previouspage");	
		public static String NextP = Global.getSettings().getString("Options", "nextpage");	
	}
	public static class TextTip{
		public static String welcomeChooseShip = "Welcome to the ShipLevel System.";
		public static String pleaseChooseShip = "Please choose a ship.";
		public static String improveTheShip = "Upgrade your ships.";
		public static String Congratulation = "Congratulation, the upgrade succeeded.";
		public static String Failure = "Something terrible happened. The Upgrade failed.";
		public static String resoucestip1 = "Resources available.";
		public static String resoucestip2 = "Upgrade cost.";
		public static String resoucestip3 = "   (About ";
		public static String resoucestip4 = " in shortage.)";
		public static String quality1 = "The quality of your ship:";
		public static String ability1 = "Please choose an ability:";
		public static String ability2 = "The upgrade of this ability has been used out on this ship.";
		public static String ability3 = "You have ";
		public static String ability4 = " chance of success.";
	}
	public static final Map<HullSize, Integer> HULLSIZE_TO_MAXLEVEL = new HashMap<HullSize, Integer>();//иѓЅеЉ›жњЂй«�з­‰зє§
	static {
		HULLSIZE_TO_MAXLEVEL.put(HullSize.FRIGATE, 10);//жЉ¤еЌ«и€°
		HULLSIZE_TO_MAXLEVEL.put(HullSize.DESTROYER, 15);//й©±йЂђи€°
		HULLSIZE_TO_MAXLEVEL.put(HullSize.CRUISER, 20);//е·Ўжґ‹и€°
		HULLSIZE_TO_MAXLEVEL.put(HullSize.CAPITAL_SHIP, 25);//ж€�е€—и€°
		HULLSIZE_TO_MAXLEVEL.put(HullSize.FIGHTER, 0);//йЈћжњє
		HULLSIZE_TO_MAXLEVEL.put(HullSize.DEFAULT, 0);//??
	}
	private static final Map<Integer, String>RESOURCE_NAME = new HashMap<>();//ж №жЌ®indexи®°еЅ•и€№еђЌ
	static{
		RESOURCE_NAME.put(0, Global.getSettings().getString("ResourceName", "supplies"));
		RESOURCE_NAME.put(1, Global.getSettings().getString("ResourceName", "volatiles"));
		RESOURCE_NAME.put(2, Global.getSettings().getString("ResourceName", "ore"));
		RESOURCE_NAME.put(3, Global.getSettings().getString("ResourceName", "rare_ore"));
		RESOURCE_NAME.put(4, Global.getSettings().getString("ResourceName", "metals"));
		RESOURCE_NAME.put(5, Global.getSettings().getString("ResourceName", "rare_metals"));
		RESOURCE_NAME.put(6, Global.getSettings().getString("ResourceName", "heavy_machinery"));
		
	}
	
	public static final boolean UPGRADE_ALWAYS_SUCCEED = Global.getSettings().getBoolean("upgradeAlwaysSucceed"); 
	public static final float BASE_FAILURE_MINFACTOR = Global.getSettings().getFloat("baseFailureMinFactor"); //жњЂдЅЋж€ђеЉџж¦‚зЋ‡пјЊз”ЁCosе‡Ѕж•°зљ„ж›Ізєї
	public static final float UPGRADE_COST_MINFACTOR = Global.getSettings().getFloat("upgradeCostMinFactor"); // 0.1f; //жњЂдЅЋеЌ‡зє§ж—¶ж‰ЂйњЂи¦Ѓи€°и€№д»·ж ј
	public static final float UPGRADE_COST_MAXFACTOR = Global.getSettings().getFloat("upgradeCostMaxFactor"); // 0.5f; //жњЂй«�еЌ‡зє§ж—¶ж‰ЂйњЂи¦Ѓи€°и€№д»·ж ј
	public static final float DIVIDING_RATIO = Global.getSettings().getFloat("dividingRatio"); //Csv й™¤д»Ґзљ„зі»ж•°
	
	private static final String Es_SR_FILE_PATH = "data/config/skill_resource_ratio.csv";//жЉЂиѓЅеЇ№еє”еЌ‡зє§иµ„жєђжЇ”дѕ‹
	private static final String SHIP_TRADE_SAVE_ID = "Es_ShipTradeSaveData";
	private static final float[] CARGO_BASEVALUE={100,30,10,75,30,200,150};//иЎҐз»™,ж°”зџї,зџїзџі,зЁЂжњ‰зџїзџі,й‡‘е±ћ,зЁЂжњ‰й‡‘е±ћ,й‡Ќећ‹е™Ёжў°зљ„еџєзЎЂд»·ж ј
	
	private InteractionDialogAPI dialog;
	private TextPanelAPI textPanel;
	private OptionPanelAPI options;
	private VisualPanelAPI visual;
	
	private CampaignFleetAPI playerFleet;
	private SectorAPI sector;
	private List<FleetMemberAPI>ShipList;//и€°й�џж€ђе‘�е€—иЎЁ
	private Map<FleetMemberAPI,String >ShipNameMap = new HashMap<>();//еђЌз§°еЇ№еє”и€°й�џж€ђе‘�
	private Map<FleetMemberAPI, AbilityOption>ShipAbilityMap = new HashMap<>();//и€°й�џж€ђе‘�еЇ№еє”иѓЅеЉ›
	private Map<FleetMemberAPI, OptionId>ShipOptionMap = new HashMap<>();//и€°й�џж€ђе‘�еЇ№еє”йЂ‰йЎ№
	private Map<OptionId, Integer>option_SkillIndex = new HashMap<>();//йЂ‰йЎ№еЇ№еє”жЉЂиѓЅпј€иЂђд№…гЂЃж­¦е™Ёetcпј‰,йњЂи¦Ѓжё…зђ†
	private Map<OptionId, Integer>option_SkillValue = new HashMap<>();//йЂ‰йЎ№еЇ№еє”жЉЂиѓЅз­‰зє§,йњЂи¦Ѓжё…зђ†
	private int ShipIndex=0;//и€°и€№зљ„йЎµж•°
	private int FunctionIndex = 0;//0дёєеЌ‡зє§и€°и€№,1дёєж�ѕз¤єеђ„йЎ№иѓЅеЉ›,2дёєж�Їеђ¦зЎ®и®¤,3дёєж±‡жЉҐж€ђеЉџдёЋеђ¦
	private FleetMemberAPI ShipSelected;//и®°еЅ•йЂ‰ж‹©зљ„и€°и€№
	private OptionId abilitySelected;//йЂ‰ж‹©зљ„иѓЅеЉ›
	private Es_ShipLevelFleetData buff;//еЇ№еє”и€°и€№зљ„data
	private JSONArray Es_sr_csvArray;
	private float[]resources_record = new float[7];//и®°еЅ•йњЂж±‚иµ„жєђ
	
	
	private static class AbilityOption{
		static final String[] DATA ={Global.getSettings().getString("AbilityName", "Durability"),
						Global.getSettings().getString("AbilityName", "WeaponProficiency"),
						Global.getSettings().getString("AbilityName", "Logistics"),
						Global.getSettings().getString("AbilityName", "Flexibility"),
						Global.getSettings().getString("AbilityName", "Technology"),};
		static final String[] DATATOOLTIPS = {
			"Improve hullpoints, armor, EMP resistance, weapon health, engine health.",
			"Improve weapon range, weapon damage, rate of fire.",
			"Improve CR per deployment, weapon ammo, crew, ship repair rate, CR recovery, fuel and supply use.",
			"Improve max speed, burn level, acceleration, deceleration, max turn rate, turn acceleration.",
			"Improve flux capacity and disspation, weapon flux cost, shield/phase efficiency."
		};
		Map<String, OptionId>optionsidMap = new HashMap<>();
		public AbilityOption(){
			for (int i = 0; i < DATA.length; i++) {
				optionsidMap.put(DATA[i], new OptionId());
			}
		}
	}
	
	
	public void init(InteractionDialogAPI dialog) {
		this.dialog = dialog;
		textPanel = dialog.getTextPanel();
		options = dialog.getOptionPanel();
		visual = dialog.getVisualPanel();
		
		sector = Global.getSector();
		playerFleet = (CampaignFleetAPI) dialog.getInteractionTarget();
		
		try {
			Es_sr_csvArray = Global.getSettings().loadCSV(Es_SR_FILE_PATH);
		} catch (IOException | JSONException e) {
			Global.getLogger(Es_ShipLevelFunctionPlugin.class).log(Level.ERROR, "Failed to load settings: " + e.getMessage());
		}
		
		
		ShipList = playerFleet.getFleetData().getMembersListCopy();//иЋ·еѕ—зЋ©е®¶и€°й�џе€—иЎЁ,е№¶дѕќж­¤еЇ№еє”еђЌз§°
		Iterator<FleetMemberAPI>iterator = ShipList.iterator();//еЋ»й™¤йЈћжњє
		while (iterator.hasNext()) {
			FleetMemberAPI fleetMemberAPI = (FleetMemberAPI) iterator.next();
			if (fleetMemberAPI.isFighterWing()) {
				iterator.remove();
			}
		}
		for (FleetMemberAPI fleetMemberAPI : ShipList) {//и®°еЅ•еђЌе­—гЂЃеЌ‡зє§йЂ‰йЎ№гЂЃйЂ‰ж‹©йЂ‰йЎ№
			ShipNameMap.put(fleetMemberAPI, fleetMemberAPI.getShipName()+"(" + fleetMemberAPI.getHullSpec().getHullName() + ")");
			ShipAbilityMap.put(fleetMemberAPI, new AbilityOption());
			ShipOptionMap.put(fleetMemberAPI, new OptionId());
		}
		
		
		visual.setVisualFade(0.25f, 0.25f);
		visual.showImagePortion("illustrations", "cargo_loading", 400, 400, 0, 0, 400, 400);
		dialog.setOptionOnEscape(OptionName.Leave, OptionId.LEAVE);
		
		optionSelected(null, OptionId.INIT);
	}
	
	public void backFromEngagement(EngagementResultAPI result) {
	}
	
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
		}
		
		if (option == OptionId.INIT) {
			addText(TextTip.welcomeChooseShip);
			options.clearOptions();
			options.addOption(TextTip.improveTheShip, OptionId.Ship_LIST);
			options.addOption(OptionName.Leave, OptionId.LEAVE, null);
//			options.addOption("жµ‹иЇ•", OptionId.TEST);//жµ‹иЇ•
//		} 
//		else if (option == OptionId.TEST) {
//			TradeMarketData data = ((Es_ShipTradeSaveData) Global.getSector().getPersistentData().get(SHIP_TRADE_SAVE_ID)).getTradeMarketData();
//			CampaignFleetAPI fleet = data.fleet_D;
//			List<FleetMemberAPI>members = fleet.getFleetData().getMembersListCopy();
//			dialog.showFleetMemberPickerDialog("йЂ‰ж‹©йў„и®ўи€№еЏЄ", OptionName.Confirm, OptionName.Cancel, 
//					5, 6, 70f, false, false, members,
//			new FleetMemberPickerListener() {
//				public void pickedFleetMembers(List<FleetMemberAPI> members) {
//					if (members != null && !members.isEmpty()) {
//						FleetMemberAPI selectedFlagship = members.get(0);
////						playerFleet.getFleetData().setFlagship(selectedFlagship);
//						//addText(getString("selectedFlagship"));
//					}
//				}
//				public void cancelledFleetMemberPicking() {
//					
//				}});
		}else if (option == OptionId.Ship_LIST) {
			addText(TextTip.pleaseChooseShip);
			if (text==TextTip.improveTheShip) {
				FunctionIndex = 0;
				updateOptions();
			}
//			switch (text) {//ж №жЌ®ж–‡е­—жќҐйЂ‰ж‹©е“ЄдёЄе€—иЎЁ
//			case TextTip.pleaseChooseShip:
//				FunctionIndex = 0;
//				updateOptions();
//				break;
//
//			default:
//				break;
//			}
		}else if (option == OptionId.Back_LIST) {
			ShipIndex-=5;
			updateOptions();
		}else if (option == OptionId.Next_LIST) {
			ShipIndex+=5;
			updateOptions();
		}
		else if (option == OptionId.BACK) {
			switch (FunctionIndex) {
			case 0://ењЁеЌ‡зє§и€°и€№ж—¶иї”е›ћдё»з›®еЅ•
				options.clearOptions();
				optionSelected(null, OptionId.INIT);
				break;
			case 1://ењЁйЂ‰ж‹©и€№иѓЅеЉ›ж—¶иї”е›ћ
				options.clearOptions();
				visual.showImagePortion("illustrations", "cargo_loading", 400, 400, 0, 0, 400, 400);
				optionSelected(TextTip.improveTheShip, OptionId.Ship_LIST);
				break;
			case 2://ењЁзЎ®е®љиѓЅеЉ›ж—¶
				options.clearOptions();
				FunctionIndex = 1;
				updateOptions();
				break;
			case 3://ењЁжњЂз»€з»“з®—ж—¶
				options.clearOptions();
				FunctionIndex = 1;
				updateOptions();
				break;	
			default:
				break;
			}
		}
		else if (ShipOptionMap.containsValue(option)) {//йЂ‰ж‹©и€°и€№еђЋи·іе‡єиѓЅеЉ›
			FunctionIndex = 1;
			for (FleetMemberAPI fleetMemberAPI : ShipList) {
				if (ShipOptionMap.get(fleetMemberAPI)==option) {
					ShipSelected = fleetMemberAPI;
					Buff buffmanager = ShipSelected.getBuffManager().getBuff(Es_LEVEL_FUNCTION_ID);//е€¤ж–­ж�Їеђ¦жњ‰buff
					if (buffmanager instanceof Es_ShipLevelFleetData) {
						buff = (Es_ShipLevelFleetData) buffmanager;
					}else {
						ShipSelected.getBuffManager().addBuff(new Es_ShipLevelFleetData(ShipSelected));
						buff = (Es_ShipLevelFleetData)ShipSelected.getBuffManager().getBuff(Es_LEVEL_FUNCTION_ID);
					}
				}
			}
			updateOptions();
		}else if (ShipAbilityMap.get(ShipSelected).optionsidMap.containsValue(option)) {//йЂ‰ж‹©иѓЅеЉ›еђЋи·іе‡єж�Їеђ¦зЎ®е®љ
			FunctionIndex = 2;
			abilitySelected = option;
			updateOptions();
		}else if (option == OptionId.Ability_APPLYER) {//еЌ‡зє§зЎ®е®љ,зЎ®е®љж¦‚зЋ‡
			int max = 0;
			HullSize hullSize = ShipSelected.getHullSpec().getHullSize();//жњЂй«�з­‰зє§
			max=(int)HULLSIZE_TO_MAXLEVEL.get(hullSize);
			int levelnow=option_SkillValue.containsKey(abilitySelected)?option_SkillValue.get(abilitySelected):0;
			
			float possibility = 1f;
			if(!UPGRADE_ALWAYS_SUCCEED) {
				possibility = (float) Math.cos(Math.PI*levelnow*0.5f/max)*(1f-BASE_FAILURE_MINFACTOR)+BASE_FAILURE_MINFACTOR;//зЎ®е®љж¦‚зЋ‡
				if (levelnow==0) {
				possibility=1f;
				}
			}
			int Index = option_SkillIndex.get(abilitySelected);

			if ((float)Math.random()<possibility) {
				// ж №жЌ®Indexж–ЅеЉ 
					buff.getLevelIndex()[Index]+=1;//з­‰зє§еЉ 1
					Global.getSoundPlayer().playUISound("ui_char_increase_skill_new", 1f, 1f);
					addText(TextTip.Congratulation, Color.yellow);
			}else {
				addText(TextTip.Failure, Color.red);
			}
			playerFleet.getCargo().removeSupplies(resources_record[0]);
			playerFleet.getCargo().removeItems(CargoItemType.RESOURCES, "volatiles", resources_record[1]);
			playerFleet.getCargo().removeItems(CargoItemType.RESOURCES, "ore", resources_record[2]);
			playerFleet.getCargo().removeItems(CargoItemType.RESOURCES, "rare_ore", resources_record[3]);
			playerFleet.getCargo().removeItems(CargoItemType.RESOURCES, "metals", resources_record[4]);
			playerFleet.getCargo().removeItems(CargoItemType.RESOURCES, "rare_metals", resources_record[5]);
			playerFleet.getCargo().removeItems(CargoItemType.RESOURCES, "heavy_machinery", resources_record[6]);
			abilitySelected=null;//жё…з©єйЂ‰ж‹©id
			FunctionIndex = 3;
			updateOptions();
		}
		

	}
	
	private void updateOptions() {
		options.clearOptions();
		switch (FunctionIndex) {
		case 0://еЌ‡зє§и€°и€№
			for (int i = ShipIndex; i < ShipIndex+5; i++) {
				if (ShipList.size() > i) {
					FleetMemberAPI fleetMemberAPI = ShipList.get(i);
					String text = ShipNameMap.get(fleetMemberAPI);
					OptionId optionId = ShipOptionMap.get(fleetMemberAPI);
					options.addOption(text, optionId);
				}
			}
			options.addOption(OptionName.PreviousP, OptionId.Back_LIST);
			options.addOption(OptionName.NextP, OptionId.Next_LIST);
			if (ShipIndex==0) {
				options.setEnabled(OptionId.Back_LIST, false);
			}
			if (ShipIndex+5>=ShipList.size()) {
				options.setEnabled(OptionId.Next_LIST, false);
			}
			break;
		case 1://йЂ‰ж‹©и€№иѓЅеЉ›
			option_SkillIndex.clear();
			option_SkillValue.clear();
			visual.showFleetMemberInfo(ShipSelected);
			addText(TextTip.resoucestip1,Color.green);
			addText("-----------------------",Color.gray);
			for (int i = 0; i < 7; i++) {
				String name = RESOURCE_NAME.get(i);
				addText(name + ":"+(int)getFleetCargoMap(playerFleet)[i]);
			}
			addText("-----------------------",Color.gray);
			final String[] data = AbilityOption.DATA;
			final String[] tooltips = AbilityOption.DATATOOLTIPS;
			for (int i = 0; i < data.length; i++) {
				OptionId optionId = ShipAbilityMap.get(ShipSelected).optionsidMap.get(data[i]);
				int level = buff.getLevelIndex()[i];
				option_SkillIndex.put(optionId, i);
				option_SkillValue.put(optionId, level);
				int max = 0;
				HullSize hullSize = ShipSelected.getHullSpec().getHullSize();//жњЂй«�з­‰зє§
				max=(int)HULLSIZE_TO_MAXLEVEL.get(hullSize);
				int levelnow=option_SkillValue.containsKey(optionId)?option_SkillValue.get(optionId):0;
				if (levelnow>=max) {
					options.addOption(data[i]+" ("+level+") (Full)", optionId,tooltips[i]);
				}else {
					options.addOption(data[i]+" ("+level+")", optionId,tooltips[i]);
				}
			}
			addText(TextTip.quality1);
			float quality = Es_ShipLevelFleetData.uppedFleetMemberAPIs.get(ShipSelected.getId());//еѕ—е€°е“ЃиґЁ
			String text = ""+Math.round(quality*100f)/100f + getQualityName(quality);
			appendText(text);
			textPanel.highlightLastInLastPara(text, getQualityColor(quality));
			addText(TextTip.ability1);
			break;
		case 2://зЎ®е®љ
			if (ShipSelected!=null && abilitySelected!=null) {
				int max = 0;
				HullSize hullSize = ShipSelected.getHullSpec().getHullSize();//жњЂй«�з­‰зє§
				max=(int)HULLSIZE_TO_MAXLEVEL.get(hullSize);
				int levelnow=option_SkillValue.containsKey(abilitySelected)?option_SkillValue.get(abilitySelected):0;
				options.addOption(OptionName.Confirm, OptionId.Ability_APPLYER);
				if (levelnow>=max) {
					addText(TextTip.ability2+"("+levelnow+")", Color.yellow);
					options.setEnabled(OptionId.Ability_APPLYER, false);
				}else {
					float shipbasevalue = ShipSelected.getHullSpec().getBaseValue();//еџєзЎЂи€°и€№зљ„д»·ж ј
					float base_ratio = (UPGRADE_COST_MAXFACTOR-UPGRADE_COST_MINFACTOR)/max;//y=ax+b,зі»ж•°a,bдёєvalue_up_min
					float ratio = base_ratio*levelnow+UPGRADE_COST_MINFACTOR;//жЇ”дѕ‹
					float resource_total_value = shipbasevalue*ratio;//иµ„жєђжЂ»и®Ўд»·еЂј
					int Index = option_SkillIndex.get(abilitySelected);
					float[] resources_ratio = new float[7];
					int[] resources_value = new int[7];
					float thisquality = Es_ShipLevelFleetData.uppedFleetMemberAPIs.get(ShipSelected.getId());
					boolean isCanLevelUp = true;//ж�Їеђ¦иѓЅеЌ‡зє§
					String[] showStrings = new String[7];//зјєдёЌзјєиґ§зљ„жЏђз¤є
					try {
					for (int j = 0; j < Es_sr_csvArray.length(); j++) {//жџҐж‰ѕдёЂдё‹
						final JSONObject entry = Es_sr_csvArray.getJSONObject(j);
						if (entry.getInt("skill_index") == Index) {
							resources_ratio[0] = (float)entry.getDouble("supplies");
							resources_ratio[1] = (float)entry.getDouble("volatiles");
							resources_ratio[2] = (float)entry.getDouble("ore");
							resources_ratio[3] = (float)entry.getDouble("rare_ore");
							resources_ratio[4] = (float)entry.getDouble("metals");
							resources_ratio[5] = (float)entry.getDouble("rare_metals");
							resources_ratio[6] = (float)entry.getDouble("heavy_machinery");
							for (int k = 0; k < resources_value.length; k++) {
								resources_value[k]=Math.round(resources_ratio[k]*resource_total_value/CARGO_BASEVALUE[k]*thisquality/DIVIDING_RATIO);
								resources_record[k]=resources_value[k];
								showStrings[k] = "";
								float fleetcargo = getFleetCargoMap(playerFleet)[k];
								if (resources_value[k]>fleetcargo) {
										isCanLevelUp = false;
										showStrings[k]=TextTip.resoucestip3+(int)(resources_value[k]-fleetcargo)+TextTip.resoucestip4;
								}								
							}
						if (!isCanLevelUp) {
							options.setEnabled(OptionId.Ability_APPLYER, false);
						}
						float possibility = 1f;
						if(!UPGRADE_ALWAYS_SUCCEED) {
							possibility = (float) Math.cos(Math.PI*levelnow*0.5f/max)*(1f-BASE_FAILURE_MINFACTOR)+BASE_FAILURE_MINFACTOR;//зЎ®е®љж¦‚зЋ‡
							if (levelnow==0) {
								possibility=1f;
							}
							//ж·»еЉ text
						}	
							addText(TextTip.resoucestip2,Color.green);
							addText("-----------------------",Color.gray);
							for (int i = 0; i < showStrings.length; i++) {
								String name = RESOURCE_NAME.get(i);
								addText(name + ":" +resources_value[i]+showStrings[i]);
							}
							addText("-----------------------",Color.gray);
							addText(TextTip.ability3);
							String text1 = ""+Math.round(possibility*1000f)/10f+"%";
							appendText(text1);
							textPanel.highlightLastInLastPara(text1, Color.green);
							appendText(TextTip.ability4);
							break;
						}
						}
						
					} catch (JSONException e) {
						Global.getLogger(Es_ShipLevelFunctionPlugin.class).log(Level.ERROR, "Failed to load settings: " + e.getMessage());
					}
				}
			}
			break;
		default:
			break;
		}
		options.addOption(OptionName.Back, OptionId.BACK, null);
	}

	
	private static final String Es_LEVEL_FUNCTION_ID = "Es_ShipLevelUp";
	private void addText(String text) {
		textPanel.addParagraph(text);
	}
	private void addText(String text,Color color) {
		textPanel.addParagraph(text,color);
	}
	
	private void appendText(String text) {
		textPanel.appendToLastParagraph(" " + text);
	}
	public void optionMousedOver(String optionText, Object optionData) {
	}
	
	public void advance(float amount) {
		
	}
	
	public Object getContext() {
		return null;
	}
	
	
	public static String getQualityName(float arg){
		String text;
		if (isInside(arg, 0.5f, 0.65f)) {
			text = Global.getSettings().getString("QualityName", "inferior");
		}else if (isInside(arg, 0.65f, 0.8f)) {
			text = Global.getSettings().getString("QualityName", "rough");
		}else if (isInside(arg, 0.8f, 0.95f)) {
			text = Global.getSettings().getString("QualityName", "crude");
		}else if (isInside(arg, 0.95f, 1.1f)) {
			text = Global.getSettings().getString("QualityName", "normal");
		}else if (isInside(arg, 1.1f, 1.25f)) {
			text = Global.getSettings().getString("QualityName", "good");
		}else if (isInside(arg, 1.25f, 1.4f)) {
			text = Global.getSettings().getString("QualityName", "superior");
		}else if (isInside(arg, 1.4f, 1.5f)) {
			text = Global.getSettings().getString("QualityName", "perfect");
		}else {
			if (arg == 1.5f) {
				text = Global.getSettings().getString("QualityName", "s_perfect");				
			}else {
				text = "(WTF?!)";
			}
		}
		return text;
	}
	
	private Color getQualityColor(float arg){
		Color color;
		if (isInside(arg, 0.5f, 0.65f)) {
			color = Color.gray.darker();
		}else if (isInside(arg, 0.65f, 0.8f)) {
			color = Color.gray;
		}else if (isInside(arg, 0.8f, 0.95f)) {
			color = Color.lightGray;
		}else if (isInside(arg, 0.95f, 1.1f)) {
			color = Color.white;
		}else if (isInside(arg, 1.1f, 1.25f)) {
			color = Color.green;
		}else if (isInside(arg, 1.25f, 1.4f)) {
			color = new Color(0,155,255);
		}else if (isInside(arg, 1.4f, 1.5f)) {
			color = Color.orange;
		}else {
			color = Color.CYAN;
		}
		return color;
	}
	
	public static boolean isInside(float arg,float a,float b){
		return (arg>=a && arg<b);
	}
	private float[] getFleetCargoMap(CampaignFleetAPI fleet) {//иї”е›ћдёЂдёЄиґ§з‰©ж•°з»„
		List<CargoStackAPI>cargosList = fleet.getCargo().getStacksCopy();
		float supplies = 0;//иЎҐз»™0
		float volatiles = 0;//ж°”зџї1
		float ore = 0;//зџїзџі2
		float rare_ore = 0;//зЁЂжњ‰зџїзџі3
		float metals = 0;//й‡‘е±ћ4
		float rare_metals = 0;//зЁЂжњ‰й‡‘е±ћ5
		float heavy_machinery = 0;//й‡Ќећ‹жњєжў°6
		for (CargoStackAPI cargoStackAPI : cargosList) {
			String id = cargoStackAPI.getCommodityId();
			if (id==null) {
				continue;
			}
			switch (id) {
			case "supplies":
				supplies+=cargoStackAPI.getSize();
				break;
			case "volatiles":
				volatiles+=cargoStackAPI.getSize();
				break;
			case "ore":
				ore+=cargoStackAPI.getSize();
				break;
			case "rare_ore":
				rare_ore+=cargoStackAPI.getSize();
				break;
			case "metals":
				metals+=cargoStackAPI.getSize();
				break;
			case "rare_metals":
				rare_metals+=cargoStackAPI.getSize();
				break;
			case "heavy_machinery":
				heavy_machinery+=cargoStackAPI.getSize();
				break;				
			default:
				break;
			}
		}
		float[]commodities ={supplies,volatiles,ore,rare_ore,metals,rare_metals,heavy_machinery}; 
		return commodities;
	}
	@Override
	public Map<String, MemoryAPI> getMemoryMap() {
		return null;
	}
}
