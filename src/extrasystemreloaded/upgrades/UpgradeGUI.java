package extrasystemreloaded.upgrades;

import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import extrasystemreloaded.gui.BasePanel;
import extrasystemreloaded.gui.SimpleUI;

import java.awt.*;

public class UpgradeGUI extends SimpleUI {
    protected CustomPanelAPI panel = null;
    protected TooltipMakerAPI maker = null;
    public UpgradeGUI(CustomPanelAPI customPanel) {
        this.panel = customPanel.createCustomPanel(customPanel.getPosition().getWidth() - 10f, 500f, new BasePanel());

        this.maker = this.panel.createUIElement(this.panel.getPosition().getWidth(), this.panel.getPosition().getHeight(), false);
        this.maker.setTitleOrbitronVeryLarge();
        this.maker.addTitle("Upgrades");
        this.maker.setTitleFontColor(new Color(255,200,150));
        this.maker.addSpacer(20);

        this.panel.addUIElement(this.maker).inTL(0f, 0f);
        customPanel.addComponent(this.panel);
    }

    @Override
    public void checkForActivatedComponents() {

    }

    @Override
    public CustomPanelAPI getPanel() {
        return panel;
    }

    @Override
    public TooltipMakerAPI getMaker() {
        return maker;
    }
}
