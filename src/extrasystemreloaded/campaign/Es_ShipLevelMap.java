package extrasystemreloaded.campaign;

import com.fs.starfarer.api.fleet.FleetMemberAPI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Es_ShipLevelMap {
		Map<String, int[]>ShipLevel_DATA;//全局舰船档案
		List<FleetMemberAPI>uppedFleetMemberAPIs;//记录所有船
		public Es_ShipLevelMap(){
			ShipLevel_DATA = new HashMap<>();
			uppedFleetMemberAPIs = new ArrayList<FleetMemberAPI>();
		}
}