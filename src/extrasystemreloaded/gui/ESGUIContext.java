package extrasystemreloaded.gui;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.rules.MemKeys;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.util.Misc;
import extrasystemreloaded.augments.Augment;
import extrasystemreloaded.upgrades.Upgrade;
import extrasystemreloaded.util.ExtraSystems;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static extrasystemreloaded.augments.AugmentsHandler.AUGMENT_LIST;
import static extrasystemreloaded.upgrades.UpgradesHandler.UPGRADES;

public class ESGUIContext {
    private InteractionDialogAPI dialog;
    private TextPanelAPI textPanel;
    private OptionPanelAPI options;
    private VisualPanelAPI visual;

    private MarketAPI currMarket;

    private CampaignFleetAPI playerFleet;
    private List<FleetMemberAPI> ShipList;

    private FleetMemberAPI selectedShip;
    private float shipBaseValue;

    private ExtraSystems buff;
    private Augment selectedAugment;
    private Upgrade selectedUpgrade;
    private float shipQuality;


    public ESGUIContext(InteractionDialogAPI dialog) {
        this.dialog = dialog;
        options = dialog.getOptionPanel();
        visual = dialog.getVisualPanel();
        textPanel = dialog.getTextPanel();

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
    }

    public void setMarket(MarketAPI market) {
        this.currMarket = market;
    }

    public void setSelectedShip(FleetMemberAPI fm) {
        this.selectedShip = fm;
        if(selectedShip != null) {
            buff = ExtraSystems.getForFleetMember(selectedShip);
            shipQuality = buff.getQuality(selectedShip);
            shipBaseValue = selectedShip.getHullSpec().getBaseValue();
        }
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

    public Upgrade getSelectedUpgrade() {
        return selectedUpgrade;
    }

    public Augment getSelectedCore() {
        return selectedAugment;
    }

    public FleetMemberAPI getSelectedShip() {
        return selectedShip;
    }

    public ExtraSystems getBuff() {
        return buff;
    }
}
