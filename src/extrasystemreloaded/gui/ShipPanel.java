package extrasystemreloaded.gui;

import com.fs.starfarer.api.campaign.CustomUIPanelPlugin;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.PositionAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import extrasystemreloaded.augments.gui.AugmentsTabPanel;
import extrasystemreloaded.campaign.ESDialogContext;
import extrasystemreloaded.upgrades.UpgradeGUI;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

public class ShipPanel implements CustomUIPanelPlugin {
    PositionAPI pos = null;
    ESDialogContext context = null;
    List<SimpleUI> components = new ArrayList<>();

    public ShipPanel(ESDialogContext context) {
        this.context = context;
    }

    @Override
    public void positionChanged(PositionAPI positionAPI) {
        this.pos = positionAPI;
    }

    @Override
    public void renderBelow(float alpha) {
    }

    @Override
    public void render(float alpha) {
        GL11.glPushMatrix();
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glColor4f(0f, 76f / 255f, 140f / 255f, 0.6f * alpha);
        GL11.glBegin(GL11.GL_LINES);
        GL11.glLineWidth(10f);

        GL11.glVertex2f(pos.getX(), pos.getY() + pos.getHeight());
        GL11.glVertex2f(pos.getX() + pos.getWidth(), pos.getY() + pos.getHeight());
        GL11.glVertex2f(pos.getX() + pos.getWidth(), pos.getY() + pos.getHeight());
        GL11.glVertex2f(pos.getX() + pos.getWidth(), pos.getY());
        GL11.glVertex2f(pos.getX() + pos.getWidth(), pos.getY());
        GL11.glVertex2f(pos.getX(), pos.getY());
        GL11.glVertex2f(pos.getX(), pos.getY());
        GL11.glVertex2f(pos.getX(), pos.getY() + pos.getHeight());

        GL11.glEnd();
        GL11.glPopMatrix();
    }

    @Override
    public void advance(float amount) {
        checkForActivatedComponents();
    }

    @Override
    public void processInput(List<InputEventAPI> list) {
    }

    public void populateGUI(CustomPanelAPI customPanel) {
        TooltipMakerAPI panelHeader = customPanel.createUIElement(customPanel.getPosition().getWidth() - 10, 20, false);
        panelHeader.addTitle("Systems GUI");

        customPanel.addUIElement(panelHeader).inTL(1f, 1f);

        TabsPanel panel = new TabsPanel(customPanel);
        panel.getPanel().getPosition().inTL(5f, 20f);

        AugmentsTabPanel augmentsTab = new AugmentsTabPanel(panel.getPanel());
        panel.addTab("Augments", augmentsTab);

        UpgradeGUI upgradeGUI = new UpgradeGUI(panel.getPanel());
        panel.addTab("Upgrades", upgradeGUI);
        upgradeGUI.postInitialize();

        this.components.add(panel);
    }

    public void checkForActivatedComponents() {
        for(SimpleUI panel : this.components) {
            panel.checkForActivatedComponents();
        }
    }
}
