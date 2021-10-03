package extrasystemreloaded.augments.gui;

import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import extrasystemreloaded.augments.Augment;
import extrasystemreloaded.gui.BasePanel;
import extrasystemreloaded.gui.SimpleUI;

public class AugmentPanel extends SimpleUI {
    protected final CustomPanelAPI panel;
    protected final TooltipMakerAPI maker;
    public AugmentPanel(CustomPanelAPI customPanel, Augment augment) {
        this.panel = customPanel.createCustomPanel(500, 300, new BasePanel());
        this.maker = panel.createUIElement(this.panel.getPosition().getWidth(), this.panel.getPosition().getHeight(), false);
        maker.setTitleOrbitronLarge();
        maker.addTitle(augment.getName());
        maker.addPara(augment.getDescription(), 0);
        maker.setParaFontColor(augment.getMainColor());
        this.panel.addUIElement(maker).inTL(0f, 0f);

        customPanel.addComponent(this.panel).inTL(0f, 0f);
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
