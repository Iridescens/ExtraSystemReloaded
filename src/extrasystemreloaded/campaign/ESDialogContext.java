package extrasystemreloaded.campaign;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.rules.MemKeys;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.campaign.BuffManager;
import extrasystemreloaded.util.modules.Module;
import extrasystemreloaded.util.upgrades.Upgrade;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static extrasystemreloaded.util.modules.Modules.MODULE_LIST;
import static extrasystemreloaded.util.upgrades.Upgrades.UPGRADES;

public class ESDialogContext {
    private InteractionDialogAPI dialog;
    private TextPanelAPI textPanel;
    private OptionPanelAPI options;
    private VisualPanelAPI visual;

    private List<Misc.Token> params;
    private Map<String, MemoryAPI> memoryMap;
    private MemoryAPI localMemory;
    private String FunctionType;

    private MarketAPI currMarket;

    private CampaignFleetAPI playerFleet;
    private List<FleetMemberAPI> ShipList;

    private FleetMemberAPI selectedShip;
    private float shipBaseValue;

    private Es_ShipLevelFleetData buff;
    private Module selectedModule;
    private Upgrade selectedUpgrade;
    private float shipQuality;


    public ESDialogContext(InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {
        this.dialog = dialog;
        options = dialog.getOptionPanel();
        visual = dialog.getVisualPanel();
        textPanel = dialog.getTextPanel();

        this.params = params;
        this.memoryMap = memoryMap;

        if(params.size() > 0) {
            //used for ship quality and ship module dialogs
            String newFunctionType = params.get(0).getString(memoryMap);
            if (newFunctionType != null) {
                FunctionType = newFunctionType;
            }
        }

        currMarket = dialog.getInteractionTarget().getMarket();

        playerFleet = Global.getSector().getPlayerFleet();
        ShipList = playerFleet.getFleetData().getMembersListCopy();
        Iterator<FleetMemberAPI> iterator = ShipList.iterator();
        while (iterator.hasNext()) {
            FleetMemberAPI fleetMemberAPI = iterator.next();
            if (fleetMemberAPI.isFighterWing()) {
                iterator.remove();
            }
        }

        localMemory = memoryMap.get(MemKeys.LOCAL);

        String shipSelectedId = localMemory.getString("$ShipSelectedId");
        if (shipSelectedId != null) {
            selectedShip = getSelectedShip(shipSelectedId);
            if(selectedShip != null) {
                BuffManager.Buff thisBuff = selectedShip.getBuffManager().getBuff(Es_ShipLevelFleetData.Es_LEVEL_FUNCTION_ID);
                if (!(thisBuff instanceof Es_ShipLevelFleetData)) {
                    selectedShip.getBuffManager().addBuff(new Es_ShipLevelFleetData(selectedShip));
                    thisBuff = selectedShip.getBuffManager().getBuff(Es_ShipLevelFleetData.Es_LEVEL_FUNCTION_ID);
                }
                buff = (Es_ShipLevelFleetData) thisBuff;

                shipQuality = buff.getExtraSystems().getQuality(selectedShip);
                shipBaseValue = selectedShip.getHullSpec().getBaseValue();
            }
        }

        Object extraUpgradeId = localMemory.get("$UpgradeId");
        if(extraUpgradeId != null) {
            for(String upgradeKey : UPGRADES.keySet()) {
                if(upgradeKey.equals((String) extraUpgradeId)) {
                    selectedUpgrade = UPGRADES.get(upgradeKey);
                }
            }
        }

        Object coreId = localMemory.get("$ModuleId");
        if(coreId != null) {
            for(Module module : MODULE_LIST) {
                if(module.getKey().equals((String) coreId)) {
                    selectedModule = module;
                }
            }
        }
    }

    public FleetMemberAPI getSelectedShip(String shipId) {
        if (ShipList == null) {
            return null;
        }

        for (int i = 0; i < ShipList.size(); i++) {
            FleetMemberAPI fleetMemberAPI = ShipList.get(i);
            if (fleetMemberAPI.getId().equals(shipId)) {
                return fleetMemberAPI;
            }
        }
        return null;
    }

    public List<Misc.Token> getParams() {
        return params;
    }

    public Map<String, MemoryAPI> getMemoryMap() {
        return memoryMap;
    }

    public InteractionDialogAPI getDialog() {
        return dialog;
    }

    public TextPanelAPI getTextPanel() {
        return textPanel;
    }

    public OptionPanelAPI getOptions() {
        return options;
    }

    public VisualPanelAPI getVisual() {
        return visual;
    }

    public String getFunctionType() {
        return FunctionType;
    }

    public MarketAPI getCurrMarket() {
        return currMarket;
    }

    public CampaignFleetAPI getPlayerFleet() {
        return playerFleet;
    }

    public List<FleetMemberAPI> getShipList() {
        return ShipList;
    }

    public float getShipQuality() {
        return shipQuality;
    }

    public float getShipBaseValue() {
        return shipBaseValue;
    }

    public MemoryAPI getLocalMemory() {
        return localMemory;
    }

    public Upgrade getSelectedUpgrade() {
        return selectedUpgrade;
    }

    public Module getSelectedCore() {
        return selectedModule;
    }

    public FleetMemberAPI getSelectedShip() {
        return selectedShip;
    }

    public Es_ShipLevelFleetData getBuff() {
        return buff;
    }
}
