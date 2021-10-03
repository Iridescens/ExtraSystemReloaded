package extrasystemreloaded.gui;

import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import extrasystemreloaded.hullmods.ExtraSystemHM;

import java.awt.*;
import java.util.*;
import java.util.List;

public class TabsPanel extends SimpleUI {
    protected final CustomPanelAPI parent;
    protected List<Tab> tabsList = new ArrayList<>();
    protected Map<Tab, SimpleUI> tabs = new HashMap<>();
    protected CustomPanelAPI mainPanel = null;
    protected TabsHeader headersPanel = null;

    public TabsPanel(CustomPanelAPI customPanel) {
        this.parent = customPanel;
        this.mainPanel = customPanel.createCustomPanel(1150, 550, new BasePanel().setColor(new Color(160f / 255f, 100f / 255f, 0f, 0.6f)));
        customPanel.addComponent(this.mainPanel).inTL(0f, 0f);

        this.headersPanel = createTabsHeader();
    }

    protected TabsHeader createTabsHeader() {
        return new TabsHeader(this);
    }

    public void addTab(String name, SimpleUI panel) {
        Tab addedHeader = this.headersPanel.addTab(name);
        this.tabs.put(addedHeader, panel);

        float w = this.mainPanel.getPosition().getWidth();
        float h = this.mainPanel.getPosition().getHeight() - this.headersPanel.getPanel().getPosition().getY() - this.headersPanel.getPanel().getPosition().getHeight();

        panel.getPanel().getPosition().setSize(w, h);
        panel.getPanel().getPosition().inTL(5, 5000);

        if(panel.getMaker() != null) {
            panel.getMaker().getPosition().setSize(w, h);
            panel.getMaker().getPosit
        }
    }

    protected void activatePanelForTab(Tab header) {
        alignActivePanel(this.tabs.get(header));
    }

    protected void alignActivePanel(SimpleUI panel) {
        panel.getPanel().getPosition().inMid();

        if(panel.getMaker() != null) {
            panel.getMaker().getPosition().inMid();
        }

        ExtraSystemHM.log.info("aligned panel");
    }

    protected void deactivatePanelForTab(Tab header) {
        alignInactivePanel(this.tabs.get(header));
    }

    protected void alignInactivePanel(SimpleUI panel) {
        panel.getPanel().getPosition().inTL(5, 5000);
    }

    @Override
    public void checkForActivatedComponents() {
        this.headersPanel.checkForActivatedComponents();

        for (SimpleUI panel : this.tabs.values()) {
            panel.checkForActivatedComponents();
        }
    }

    @Override
    public CustomPanelAPI getPanel() {
        return this.mainPanel;
    }

    @Override
    public TooltipMakerAPI getMaker() {
        return null;
    }
}
