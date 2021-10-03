package extrasystemreloaded.gui;

import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.PositionAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import extrasystemreloaded.hullmods.ExtraSystemHM;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TabsHeader extends SimpleUI {
    protected final CustomPanelAPI parent;
    protected List<Tab> tabsList = new ArrayList<>();
    protected Map<Tab, SimpleUI> tabs = new HashMap<>();

    protected TabsPanel tabsParent = null;
    protected CustomPanelAPI headersPanel = null;
    protected TooltipMakerAPI tabsContainer = null;

    public TabsHeader(TabsPanel tabsParent) {
        this.tabsParent = tabsParent;

        this.parent = tabsParent.getPanel();

        this.headersPanel = this.parent.createCustomPanel(parent.getPosition().getWidth() - 10, 20, new BasePanel());
        this.parent.addComponent(headersPanel).inTL(0f, 0f);

        this.tabsContainer = headersPanel.createUIElement(headersPanel.getPosition().getWidth(), headersPanel.getPosition().getHeight(), true);
        headersPanel.addUIElement(tabsContainer).inMid();
    }

    protected PositionAPI alignTab(Tab header) {
        PositionAPI pos = header.getUIComponent().getPosition();
        if(this.tabsList.size() >= 2) {
            return pos.rightOfMid(this.tabsList.get(this.tabsList.size() - 2).getUIComponent(), 5f);
        }
        return pos.inTL(0f, 1f);
    }

    public Tab addTab(String name) {
        Tab addedHeader = new Tab(this, name);
        this.tabsList.add(addedHeader);

        this.alignTab(addedHeader);
        return addedHeader;
    }

    public Tab addTab(Tab tab) {
        this.tabsList.add(tab);
        this.alignTab(tab);
        return tab;
    }

    public final void activateTab(Tab clickedHeader) {
        uncheckOtherHeaders(clickedHeader);

        if(clickedHeader != null) {
            ExtraSystemHM.log.info(String.format("activating tab [%s]", clickedHeader.getName()));
            this.tabsParent.activatePanelForTab(clickedHeader);
        }
    }

    public final void deactivateTab(Tab clickedHeader) {
        if(clickedHeader != null) {
            clickedHeader.setClicked(false);
            clickedHeader.getButton().setChecked(false);
            this.tabsParent.deactivatePanelForTab(clickedHeader);
        }
    }

    protected final void uncheckOtherHeaders(Tab clickedHeader) {
        for(Tab header : this.tabsList) {
            if(clickedHeader != null && header == clickedHeader) continue;
            if(header.clicked) {
                ExtraSystemHM.log.info(String.format("deactivating tab [%s]", header.getName()));
                deactivateTab(header);
            }
        }
    }

    @Override
    public void checkForActivatedComponents() {
        for(Tab header : this.tabsList) {
            header.callIfClicked();
        }
    }

    @Override
    public CustomPanelAPI getPanel() {
        return this.headersPanel;
    }

    @Override
    public TooltipMakerAPI getMaker() {
        return this.tabsContainer;
    }
}
