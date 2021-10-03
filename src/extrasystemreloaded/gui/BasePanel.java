package extrasystemreloaded.gui;

import com.fs.starfarer.api.campaign.CustomUIPanelPlugin;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.PositionAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.List;

public class BasePanel extends SimpleUI implements CustomUIPanelPlugin {
    PositionAPI pos = null;
    Color color = new Color(0f, 76f / 255f, 140f / 255f, 0.6f);

    public BasePanel setColor(Color color) {
        this.color = color;
        return this;
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
        GL11.glColor4f(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f * alpha);
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

    @Override
    public void checkForActivatedComponents() {
    }

    @Override
    public CustomPanelAPI getPanel() {
        return null;
    }

    @Override
    public TooltipMakerAPI getMaker() {
        return null;
    }
}
