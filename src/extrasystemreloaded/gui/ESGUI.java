package extrasystemreloaded.gui;

import com.fs.starfarer.api.campaign.FleetMemberPickerListener;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import extrasystemreloaded.campaign.ESDialogContext;

import java.util.List;

public class ESGUI {
    public static void showGUI(final ESDialogContext context) {
        final InteractionDialogAPI dialog = context.getDialog();
        context.getOptions().clearOptions();

        List<FleetMemberAPI> validSelectionList = context.getShipList();
        int rows = validSelectionList.size() > 5 ? (int) Math.ceil(validSelectionList.size() / 5f) : 1;
        int cols = Math.min(validSelectionList.size(), 5);
        cols = Math.max(cols, 4);

        dialog.showFleetMemberPickerDialog("Select ship", "Confirm", "Cancel", rows,
                cols, 88f, true, false, validSelectionList, new FleetMemberPickerListener() {
                    @Override
                    public void pickedFleetMembers(List<FleetMemberAPI> members) {
                        if (members != null && !members.isEmpty()) {
                            context.getLocalMemory().set("$ShipSelectedId", members.get(0).getId());
                            populateOptions(context);
                        } else {
                            //sue me
                            dialog.getPlugin().optionSelected("ESMainMenu", "ESMainMenu");
                        }
                    }

                    @Override
                    public void cancelledFleetMemberPicking() {
                        dialog.getPlugin().optionSelected("ESMainMenu", "ESMainMenu");
                    }
                });
    }

    private static void populateOptions(ESDialogContext context) {
        context.getLocalMemory().set("$GUIShown", true);
        context.getOptions().clearOptions();

        int xOffset = 0;
        int yOffset = 600;

        context.getDialog().setXOffset(xOffset);
        context.getDialog().setYOffset(yOffset);

        ShipPanel panel = new ShipPanel(context);
        CustomPanelAPI customPanel = context.getVisual().showCustomPanel(1200f, 600f, panel);
        customPanel.getPosition().setYAlignOffset(-xOffset).setXAlignOffset(-yOffset).inMid();
        panel.populateGUI(customPanel);

        context.getOptions().addOption("Exit", "ESMainMenu");
    }
}
