package data.scripts.campaign;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Level;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.econ.SubmarketAPI;
import com.fs.starfarer.api.combat.WeaponAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.fleets.FleetFactory;
import com.fs.starfarer.api.util.IntervalUtil;

public class Es_ShipTradeSaveData {
	private static final String SHIP_TARDE_SAVE_ID = "Es_ShipTradeSaveData";
	private static final String SHIP_TARDE_CSV_PATH = "data/config/trade_coporation_data.csv";
	private static List<Es_TraderFactionAPI>traderFactionAPIs = null;
	private static final float PREPARE_DELIVER_FACTOR = 0.4f;//准备货物与发货的时间比
	private static final Map<HullSize,Float>TIME_FACTOR_MAP = new HashMap<>();//基本花费时间，对应舰船规格
	static{
		TIME_FACTOR_MAP.put(HullSize.FRIGATE, 15f);
		TIME_FACTOR_MAP.put(HullSize.DESTROYER, 30f);
		TIME_FACTOR_MAP.put(HullSize.CRUISER, 60f);
		TIME_FACTOR_MAP.put(HullSize.CAPITAL_SHIP, 120f);
	}
	private TradeMarketData data;//当前data
	private List<TradeMarketData>datas;//0为当前市场，2为最后市场
	private List<OrderFormData>FORM_DATAS = new ArrayList<>();//订单
	
	public  void init(){
		if (traderFactionAPIs == null) {
			traderFactionAPIs = new ArrayList<Es_TraderFactionAPI>();
		}else {
			traderFactionAPIs.clear();
		}
		try {//加载csv
			JSONArray csvarray = Global.getSettings().loadCSV(SHIP_TARDE_CSV_PATH);
			for (int i = 0; i < csvarray.length(); i++) {
				final JSONObject entry = csvarray.getJSONObject(i);
				Es_TraderFactionAPI factionAPI = new Es_TraderFactionAPI(entry.getString("id"), entry.getString("name"), entry.getString("hints"), entry.getString("signal"),(float) entry.getDouble("quality_f"), (float) entry.getDouble("quality_r"), (float) entry.getDouble("price_f"),  (float) entry.getDouble("price_r"),  (float) entry.getDouble("time_prepare_f"),  (float) entry.getDouble("time_deliver_f"));
				traderFactionAPIs.add(factionAPI);
			}
		} catch (IOException | JSONException e) {
			Global.getLogger(Es_ShipTradeSaveData.class).log(Level.ERROR, "Failed to load settings: " + e.getMessage());
		}
		Es_ShipTradeSaveData STSdata = (Es_ShipTradeSaveData) Global.getSector().getPersistentData().get(SHIP_TARDE_SAVE_ID);
		List<TradeMarketData>datas = STSdata.datas;
		if (datas ==null) {
			datas = new ArrayList<TradeMarketData>();
			for (int i = 0; i <3; i++) {
				TradeMarketData newData = new TradeMarketData();
				newData.init();
				datas.add(newData);
			}
		}
		this.data = datas.get(0);
		this.datas = datas;
	}
	public void advance()//将list所有的form都前进一天,
	{
		for (OrderFormData orderFormData : FORM_DATAS) {
			orderFormData.advance();
		}
	}
	
	public void marketRefresh(){//刷新预订市场
		data = datas.get(1);
		datas.remove(0);
		TradeMarketData newdata = new TradeMarketData();
		newdata.init();
		datas.add(newdata);
		String t1 = "舰船预订市场已刷新！";
		String t2 = ""+data.getMarketOpSize();
		Global.getSector().getCampaignUI().addMessage(t1+ "本次的市场规模为:" + t2+"。", Color.white, t1, t2, Color.yellow, Color.yellow);
	}
	public  TradeMarketData getTradeMarketData(){//得到市场状态
		return data;
	}
	public List<OrderFormData> getOrderDatas(){//获取订单列表
		return FORM_DATAS;
	}
	public OrderFormData generateOrderForm(FleetMemberAPI member,float buyValue, float expectedQuality,
			Es_TraderFactionAPI traderFaction) {// 生成订单文件
		OrderFormData data = new OrderFormData(member, buyValue, expectedQuality, traderFaction);
		FORM_DATAS.add(data);//加入列表
		return data;
	}
	public static class OrderFormData{
		String orderFormId;
		String orderFormUUID;
		String variantID;
		float buyValue;//购买时价格
		float prepareTime;
		float deliverTime;
		float buytime = 0f;//自购买后的时间(天)
		boolean isToDeliver = false;//是否准备好货物
		boolean isFinished = false;//是否是已完成订单
		SubmarketAPI submarket;//仓库
		float finalQuality;
		public OrderFormData(FleetMemberAPI member,float buyValue, float expectedQuality,
				Es_TraderFactionAPI traderFaction){
			this.variantID = member.getVariant().getHullVariantId();
			this.buyValue = buyValue;
			this.finalQuality = traderFaction.getQuality_f()*expectedQuality*(1f-(float)Math.random())*(traderFaction.getQuality_r());
			this.finalQuality = Math.max(0.5f, Math.min(this.finalQuality, 1.5f));
			float basetime = TIME_FACTOR_MAP.get(member.getHullSpec().getHullSize());//基础时间
			float preTimeRatio = traderFaction.getTime_pre_f()*this.finalQuality;
			float delTimeRatio = traderFaction.getTime_del_f()*this.finalQuality;
			this.prepareTime = basetime*PREPARE_DELIVER_FACTOR*preTimeRatio;//准备(发货)时间
			this.deliverTime = basetime*(1f-PREPARE_DELIVER_FACTOR)*delTimeRatio;//到货时间
			String id = "" + traderFaction.getSignal()+member.getId();
			this.orderFormUUID = id;
			String formid = "";
			String[] newid = id.split("");
			for (int i = 0; i < 10; i++) {
				int index = Math.round((float)Math.random()*(newid.length-1));
				formid = formid + newid[index];//id生成
			}
			this.orderFormId = formid;
		}
		public void advance(){//前进
			if (!this.isFinished) {
				this.buytime+=1;
			}
			if (!this.isToDeliver && this.buytime>=this.prepareTime) {
				this.buytime = 0;
				this.isToDeliver = true;
			}else if (!this.isFinished && this.buytime>=this.deliverTime) {
				this.isFinished = true;
			}
		}
		
		
	}
	
	public static class TradeMarketData{
		CampaignFleetAPI fleet_F = Global.getFactory().createEmptyFleet("player", "", true);//护卫舰
		CampaignFleetAPI fleet_D = Global.getFactory().createEmptyFleet("player", "", true);//驱逐
		CampaignFleetAPI fleet_C = Global.getFactory().createEmptyFleet("player", "", true);//巡洋
		CampaignFleetAPI fleet_CS = Global.getFactory().createEmptyFleet("player", "", true);//战舰
		Map<FleetMemberAPI, Float>Ship_BaseValue_MAP = new HashMap<FleetMemberAPI, Float>();//价格波动
		int marketOPsize = 0;
		
		public float getReserveValue(FleetMemberAPI fleetMemberAPI){//返回波动价格（原价+浮动）
			if (Ship_BaseValue_MAP.containsKey(fleetMemberAPI)) {
				return Ship_BaseValue_MAP.get(fleetMemberAPI);
			}else {
				return 0;
			}
		}
		public void init(){//订单确定后，会将舰船取出
			Ship_BaseValue_MAP.clear();
			fleet_F.getFleetData().clear();
			fleet_D.getFleetData().clear();
			fleet_C.getFleetData().clear();
			fleet_CS.getFleetData().clear();
			List<FleetMemberAPI>falseMemberAPIs = new ArrayList<>();
			List<FleetMemberAPI>trueMemberAPIs = new ArrayList<>();
			for (FactionAPI factionAPI :Global.getSector().getAllFactions()) {//各势力船只
				CampaignFleetAPI fleet = FleetFactory.createGenericFleet(factionAPI.getId(), "nothing", 1.5f, 20 ); // ->500 // (int)((float)Math.random()*500f+500f)
				for (FleetMemberAPI fleetMemberAPI : fleet.getFleetData()
						.getMembersListCopy()) {
					falseMemberAPIs.add(fleetMemberAPI);//加入总体fleetmember
				}
			}
			Map<String, Integer>repeatedShips = new HashMap<>();
			for (FleetMemberAPI fleetMemberAPI : falseMemberAPIs) {//总体fleetmember里去除重复
				String shipID = fleetMemberAPI.getHullSpec().getBaseHullId();
				if (repeatedShips.containsKey(shipID)) {
					int times = repeatedShips.get(shipID);
					if ((float)Math.random()>0.42f*times) {
						trueMemberAPIs.add(fleetMemberAPI);
						repeatedShips.put(shipID, times+1);
					}
				}else {
					repeatedShips.put(shipID, 1);
					trueMemberAPIs.add(fleetMemberAPI);
				}
			}
			for (FleetMemberAPI fleetMemberAPI : trueMemberAPIs) {//开始真正处理				
				fleetMemberAPI.getRepairTracker().setCR(1f);
				if (fleetMemberAPI.getHullSpec().getHullSize() == HullSize.FRIGATE) {
					fleet_F.getFleetData().addFleetMember(fleetMemberAPI);
					Ship_BaseValue_MAP.put(fleetMemberAPI, fleetMemberAPI.getHullSpec().getBaseValue()* ((float)Math.random()*0.4f+0.8f));
					marketOPsize+=fleetMemberAPI.getFleetPointCost();
				} else if (fleetMemberAPI.getHullSpec().getHullSize() == HullSize.DESTROYER) {
					fleet_D.getFleetData().addFleetMember(fleetMemberAPI);
					Ship_BaseValue_MAP.put(fleetMemberAPI, fleetMemberAPI.getHullSpec().getBaseValue()* ((float)Math.random()*0.4f+0.8f));
					marketOPsize+=fleetMemberAPI.getFleetPointCost();
				} else if (fleetMemberAPI.getHullSpec().getHullSize() == HullSize.CRUISER) {
					fleet_C.getFleetData().addFleetMember(fleetMemberAPI);
					Ship_BaseValue_MAP.put(fleetMemberAPI, fleetMemberAPI.getHullSpec().getBaseValue()* ((float)Math.random()*0.4f+0.8f));
					marketOPsize+=fleetMemberAPI.getFleetPointCost();
				} else if (fleetMemberAPI.getHullSpec().getHullSize() == HullSize.CAPITAL_SHIP) {
					fleet_CS.getFleetData().addFleetMember(fleetMemberAPI);
					Ship_BaseValue_MAP.put(fleetMemberAPI, fleetMemberAPI.getHullSpec().getBaseValue()* ((float)Math.random()*0.4f+0.8f));
					marketOPsize+=fleetMemberAPI.getFleetPointCost();
				}
			}
			fleet_F.getFleetData().sort();
			fleet_D.getFleetData().sort();
			fleet_C.getFleetData().sort();
			fleet_CS.getFleetData().sort();
		}
		public int getMarketOpSize(){
			return marketOPsize;
		}
	}
}
