package extrasystemreloaded.gui;

import com.fs.starfarer.api.ui.ButtonAPI;
import com.fs.starfarer.api.ui.UIComponentAPI;
import extrasystemreloaded.hullmods.ExtraSystemHM;

import java.awt.Color;

public class Tab {
    protected String name = "No Name";
    protected boolean clicked = false;
    protected UIComponentAPI uiComponent = null;
    protected ButtonAPI button = null;
    protected ActivatedCallback callback = null;
    protected TabsHeader parent = null;
    protected Color activeTabColor = new Color(100, 100, 200);

    public Tab(TabsHeader parent, String name) {
        this.name = name;
        this.parent = parent;
        this.button = createTabButton();
    }

    public Tab(TabsHeader parent, String name, Color activeTabColor) {
        this.name = name;
        this.parent = parent;
        this.activeTabColor = activeTabColor;
        this.button = createTabButton();
    }

    public Tab(ButtonAPI button) {
        this.button = button;
        button.setEnabled(false);
    }

    public ButtonAPI createTabButton() {
        ButtonAPI button = parent.getMaker().addAreaCheckbox(name, name, Color.BLUE, activeTabColor, Color.WHITE, 160f, 18f, 3f);
        this.uiComponent = parent.getMaker().getPrev();
        return button;
    }

    public String getName() {
        return this.name;
    }

    public UIComponentAPI getUIComponent() {
        return this.uiComponent;
    }

    public ButtonAPI getButton() {
        return this.button;
    }

    public void setClicked(boolean clicked) {
        this.clicked = clicked;
    }

    public void checkShouldBeEnabled() {
        if(this.callback == null && this.parent == null) {
            button.setEnabled(false);
        } else {
            button.setEnabled(true);
        }
    }

    public void setCallback(ActivatedCallback callback) {
        this.callback = callback;
        checkShouldBeEnabled();
    }

    public void callIfClicked() {
        if(!clicked && button.isChecked()) {
            ExtraSystemHM.log.info(String.format("clicked [%s]", this.name));
            boolean override = false;
            if(this.callback != null) {
                override = this.callback.onActivated(true);
            }

            if(!override && this.parent != null) {
                this.parent.activateTab(this);
            }
        } else if (clicked && !button.isChecked()) {
            ExtraSystemHM.log.info(String.format("unclicked [%s]", this.name));
            boolean override = false;
            if(this.callback != null) {
                override = this.callback.onActivated(false);
            }

            if(!override && this.parent != null) {
                this.parent.deactivateTab(this);
            }
        }

        this.clicked = button.isChecked();
    }

    public static interface ActivatedCallback {
        boolean onActivated(boolean checked);
    }
}
