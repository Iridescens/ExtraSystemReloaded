package extrasystemreloaded.campaign.rulecmd;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.rulecmd.BaseCommandPlugin;
import com.fs.starfarer.api.util.Misc;
import extrasystemreloaded.campaign.Es_ShipLevelFleetData;
import extrasystemreloaded.campaign.Es_ShipLevelFunctionPlugin;

import java.awt.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Es_ShipQualityDialog extends BaseCommandPlugin {

    private int ShipPageIndex;
    private String FunctionType;
    private FleetMemberAPI ShipSelected;
    private String ShipSelectedId;
    private int NumShipsPerPage = 5;
    private int[] AbilityLevelTemp;

    //    private InteractionDialogAPI dialog;
    private TextPanelAPI textPanel;
    private OptionPanelAPI options;
    private VisualPanelAPI visual;

    private BuffManagerAPI.Buff buffmanager;
    private MarketAPI currMarket;
    private CampaignFleetAPI playerFleet;
    private List<FleetMemberAPI> ShipList;
    private Map<FleetMemberAPI, String> ShipNameMap = new HashMap<>();
    private Map<FleetMemberAPI, String> ShipOptionMap = new HashMap<>();

    private float shipQuality;
    private float shipBaseValue;
    private float estimatedOverhaulCost;
    private float baseQualityStep = 0.025f; // TODO Get from settings?


    @Override
    public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {
        ShipPageIndex = (int) params.get(0).getFloat(memoryMap);
        FunctionType = params.get(1).getString(memoryMap);
        currMarket = dialog.getInteractionTarget().getMarket();

        playerFleet = Global.getSector().getPlayerFleet();
        ShipList = playerFleet.getFleetData().getMembersListCopy();
        Iterator<FleetMemberAPI> iterator = ShipList.iterator();
        while (iterator.hasNext()) {
            FleetMemberAPI fleetMemberAPI = (FleetMemberAPI) iterator.next();
            if (fleetMemberAPI.isFighterWing()) {
                iterator.remove();
            }
        }

        if (params.size() > 2 ) {
            if(params.get(2)!= null){
                ShipSelectedId = params.get(2).getString(memoryMap);
                for (int i = 0; i < ShipList.size(); i++) {
                    FleetMemberAPI fleetMemberAPI = ShipList.get(i);
                    if (fleetMemberAPI.getId() == ShipSelectedId) {
                        ShipSelected=fleetMemberAPI;
                        break;
                    }
                }
                buffmanager = ShipSelected.getBuffManager().getBuff(Es_ShipLevelFunctionPlugin.Es_LEVEL_FUNCTION_ID);

                if (buffmanager instanceof Es_ShipLevelFleetData) {
//                    Es_ShipLevelFleetData buffTemp = (Es_ShipLevelFleetData)buffmanager;
//                    ShipSelected.getBuffManager().removeBuff(buffmanager.getId());
//                    ShipSelected.getBuffManager().addBuff(new Es_ShipLevelFleetData(ShipSelected,buffTemp.getQualityFactor(),buffTemp.getLevelIndex()));
                } else {
                    ShipSelected.getBuffManager().addBuff(new Es_ShipLevelFleetData(ShipSelected));
                }

                shipQuality = Es_ShipLevelFleetData.uppedFleetMemberAPIs.get(ShipSelected.getId());
                shipBaseValue = ShipSelected.getHullSpec().getBaseValue();
                estimatedOverhaulCost = Math.round(shipBaseValue*(float)Math.pow(shipQuality,2)/2*100f)/100f; //* (bonusQ()*0.5f/baseQualityStep); // pay more when bonusQ is higher
            }
        }
        options = dialog.getOptionPanel();
        visual = dialog.getVisualPanel();
        textPanel = dialog.getTextPanel();

        updateOptions();
//        memoryMap.get(MemKeys.LOCAL).set("$nex_dirFactionGroup", num);

        return true;
    }

        private void updateOptions() {
            options.clearOptions();
            switch (FunctionType) {
                case "ShipList":
                    for (FleetMemberAPI fleetMemberAPI : ShipList) {
                        ShipNameMap.put(fleetMemberAPI, fleetMemberAPI.getShipName() + "(" + fleetMemberAPI.getHullSpec().getHullName() + ")");
                        ShipOptionMap.put(fleetMemberAPI, fleetMemberAPI.getId());
                    }
                    for (int i = ShipPageIndex*NumShipsPerPage; i < ShipPageIndex*NumShipsPerPage + 5; i++) {
                        if (ShipList.size() > i) {
                            FleetMemberAPI fleetMemberAPI = ShipList.get(i);
                            options.addOption(ShipNameMap.get(fleetMemberAPI), ShipOptionMap.get(fleetMemberAPI));
                        }
                    }
                    visual.showFleetInfo("Your fleet",playerFleet,null,null);
                    options.addOption("Previous page", "ESShipQualityPREV");
                    options.addOption("Next page", "ESShipQualityNEXT");
                    if (ShipPageIndex == 0) {
                        options.setEnabled("ESShipQualityPREV", false);
                    }
                    if (ShipPageIndex*NumShipsPerPage + 5 >= ShipList.size()) {
                        options.setEnabled("ESShipQualityNEXT", false);
                    }
                    break;
                case "ShipSelected":
                    visual.showFleetMemberInfo(ShipSelected);
                    textPanel.addParagraph(Es_ShipLevelFunctionPlugin.TextTip.quality1);
// buffmanager was here
// shipQuality was here
                    String text = ""+Math.round(shipQuality*100f)/100f + Es_ShipLevelFunctionPlugin.getQualityName(shipQuality);
                    textPanel.appendToLastParagraph(" " + text);
                    textPanel.highlightLastInLastPara(text, Es_ShipLevelFunctionPlugin.getQualityColor(shipQuality));
// shipBaseValue & estimatedOverhaulCost were here
                    text = "Local industrial facilities are capable of improving overall quality rating of ships by "+bonusQ();
                    textPanel.addParagraph(text);
                    textPanel.highlightLastInLastPara(""+bonusQ(), Color.green);
                    text = "On-site team estimates ship's overhaul to cost " + estimatedOverhaulCost + " credits";
                    textPanel.addParagraph(text);
                    textPanel.highlightLastInLastPara(""+estimatedOverhaulCost, Color.green);
                    options.addOption("Agree to conditions","ESShipQualityApply",null);
                    isAbleToPayForUpgrade(estimatedOverhaulCost);
                    break;
                case "ApplyUpgrade":
                    float newquality = Math.round((Es_ShipLevelFleetData.uppedFleetMemberAPIs.get(ShipSelectedId)+bonusQ())*100f)/100f; // qualityFactor + bonus
                    Es_ShipLevelFleetData.uppedFleetMemberAPIs.remove(ShipSelectedId);
//                    Es_ShipLevelFleetData.uppedFleetMemberAPIs.put(ShipSelectedId,newquality);
                    Es_ShipLevelFleetData buffTemp = (Es_ShipLevelFleetData)buffmanager;
                    ShipSelected.getBuffManager().removeBuff(buffmanager.getId());
                    ShipSelected.getBuffManager().addBuff(new Es_ShipLevelFleetData(ShipSelected,newquality,buffTemp.getLevelIndex()));
                    playerFleet.getCargo().getCredits().subtract(estimatedOverhaulCost);
                    String text2 = "After some improvements here and there, your ship now has quality rating of "+newquality;
                    textPanel.addParagraph(text2);
                    textPanel.highlightLastInLastPara(""+newquality, Es_ShipLevelFunctionPlugin.getQualityColor(newquality));
                    options.addOption("Back to ShipQuality menu", "ESShipQuality");
                default:
                    break;

            }
            options.addOption("Return to ES main menu", "ESMainMenu", null);
        }
        private void isAbleToPayForUpgrade(float Cost) {
            if (Cost <= playerFleet.getCargo().getCredits().get()) {
                options.setTooltip("ESShipQualityApply", "Proceed with overhaul");
            } else {
                options.setEnabled("ESShipQualityApply", false);
                options.setTooltip("ESShipQualityApply", "Insufficient credits");
            }
        }

        private float bonusQ(){
            return baseQualityStep*(2+(currMarket.hasIndustry("heavyindustry")?1:0) + (currMarket.hasIndustry("orbitalworks")?2:0));
        }

}
