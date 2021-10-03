package extrasystemreloaded.gui;

import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.ui.ButtonAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;

import java.awt.Color;

public class Button<T> {
    protected boolean clicked = false;
    protected ButtonAPI button = null;
    protected FleetMemberAPI fm = null;
    protected ClickedCallback callback = null;
    protected T obj = null;

    public Button(ButtonAPI button, T obj) {
        this.button = button;
        this.obj = obj;
        button.setEnabled(false);
    }

    public Button(TooltipMakerAPI panel, T obj) {
        this.button = panel.addButton(obj.toString(), obj, Color.BLUE, Color.WHITE, 160f, 18f, 3f);
        this.obj = obj;
        button.setEnabled(false);
    }

    public void checkShouldBeEnabled() {
        if(this.callback == null || this.fm == null) {
            button.setEnabled(false);
        } else {
            button.setEnabled(true);
        }
    }

    public void setCallback(ClickedCallback callback) {
        this.callback = callback;
        checkShouldBeEnabled();
    }

    public void callIfClicked() {
        if(!clicked && button.isChecked() && this.callback != null) {
            this.callback.onClicked();
        }
        this.clicked = button.isChecked();
    }

    public static interface ClickedCallback {
        void onClicked();
    }
}
