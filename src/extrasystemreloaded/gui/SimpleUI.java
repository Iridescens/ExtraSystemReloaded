package extrasystemreloaded.gui;

import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;

public abstract class SimpleUI {
    public abstract void checkForActivatedComponents();
    public abstract CustomPanelAPI getPanel();
    public abstract TooltipMakerAPI getMaker();
    public void postInitialize() {};
}
