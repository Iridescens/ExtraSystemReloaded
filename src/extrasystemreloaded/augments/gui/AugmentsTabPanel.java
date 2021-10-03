package extrasystemreloaded.augments.gui;

import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.PositionAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import extrasystemreloaded.augments.Augment;
import extrasystemreloaded.augments.AugmentsHandler;
import extrasystemreloaded.gui.*;
import extrasystemreloaded.hullmods.ExtraSystemHM;

import java.awt.Color;

public class AugmentsTabPanel extends TabsPanel {
    public AugmentsTabPanel(CustomPanelAPI customPanel) {
        super(customPanel);

        TooltipMakerAPI title = mainPanel.createUIElement(400, 30, false);
        title.setTitleOrbitronVeryLarge();
        title.addTitle("Augments");
        title.setTitleFontColor(new Color(200,175,255));
        title.addSpacer(20);
        mainPanel.addUIElement(title).inTMid(4f);

        for (Augment augment : AugmentsHandler.AUGMENT_LIST) {
            addAugment(augment);
        }
    }

    @Override
    protected TabsHeader createTabsHeader() {
        AugmentsTabHeader tabsHeader = new AugmentsTabHeader(this);

        float w = 160f;
        float h = this.parent.getPosition().getHeight() - 30f;
        tabsHeader.getPanel().getPosition().setSize(w, h).inLMid(5f).setYAlignOffset(-1f);
        tabsHeader.getMaker().getPosition().setSize(w, h).inMid();
        return tabsHeader;
    }

    public void addAugment(Augment augment) {
        //must generate new AugmentPanel.
        AugmentPanel augmentPanel = new AugmentPanel(this.mainPanel, augment);

        Tab addedHeader = new AugmentTab(this.headersPanel, augment);
        this.headersPanel.addTab(addedHeader);

        this.tabs.put(addedHeader, augmentPanel);

        float w = this.mainPanel.getPosition().getWidth();
        float h = this.mainPanel.getPosition().getHeight() - this.headersPanel.getPanel().getPosition().getY() - this.headersPanel.getPanel().getPosition().getHeight();

        augmentPanel.getPanel().getPosition().setSize(w, h);
        augmentPanel.getPanel().getPosition().inTL(5, 5000);

        PositionAPI pos = augmentPanel.getPanel().getPosition();
        ExtraSystemHM.log.info(String.format("X %s Y %s W %s H %s", pos.getX(), pos.getY(), pos.getWidth(), pos.getHeight()));
    }

    @Override
    public void postInitialize() {
    }

    @Override
    protected void alignActivePanel(SimpleUI panel) {
        PositionAPI parentPos = this.mainPanel.getPosition();
        this.mainPanel.getPosition().setSize(1000f, 500f);
        ExtraSystemHM.log.info(String.format("parent X %s Y %s W %s H %s", parentPos.getX(), parentPos.getY(), parentPos.getWidth(), parentPos.getHeight()));
        PositionAPI pos = panel.getPanel().getPosition().setSize(1000f,500f).inTL(0f, 0f);
        panel.getMaker().getPosition().inMid();
        ExtraSystemHM.log.info(String.format("X %s Y %s W %s H %s", pos.getX(), pos.getY(), pos.getWidth(), pos.getHeight()));
    }

    @Override
    public void checkForActivatedComponents() {
        super.checkForActivatedComponents();
    }
}
