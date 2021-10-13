package extrasystemreloaded.campaign.rulecmd;

import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import extrasystemreloaded.campaign.ESDialog;
import extrasystemreloaded.campaign.ESDialogContext;

import java.util.List;

public class Es_ShipListDialog extends ESDialog {
    private static final int NumShipsPerPage = 5;
    public static String RULE_DIALOG_OPTION = "ESShipList";
    private static String RULE_PREV_OPTION = "ESShipPickerPREV";
    private static String RULE_NEXT_OPTION = "ESShipPickerNEXT";

    @Override
    protected void process(final ESDialogContext context, TextPanelAPI textPanel, OptionPanelAPI options, VisualPanelAPI visual) {
        options.clearOptions();

        final InteractionDialogAPI dialog = context.getDialog();

        List<FleetMemberAPI> validSelectionList = context.getShipList();
        int rows = validSelectionList.size() > 5 ? (int) Math.ceil(validSelectionList.size() / 5f) : 1;
        int cols = Math.min(validSelectionList.size(), 5);
        cols = Math.max(cols, 4);

        dialog.showFleetMemberPickerDialog("Select ship", "Confirm", "Cancel", rows,
                cols, 88f, true, false, validSelectionList, new FleetMemberPickerListener() {
                    @Override
                    public void pickedFleetMembers(List<FleetMemberAPI> members) {
                        if (members != null && !members.isEmpty()) {
                            context.getLocalMemory().set(Es_ShipPicked.MEM_KEY, members.get(0).getId());
                            dialog.getPlugin().optionSelected(Es_ShipDialog.RULE_DIALOG_OPTION, Es_ShipDialog.RULE_DIALOG_OPTION);
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
}
