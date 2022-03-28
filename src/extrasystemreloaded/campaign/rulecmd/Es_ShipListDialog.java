package extrasystemreloaded.campaign.rulecmd;

import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import extrasystemreloaded.campaign.ESDialog;
import extrasystemreloaded.campaign.ESDialogContext;
import extrasystemreloaded.util.StringUtils;
import org.lwjgl.input.Keyboard;

import java.util.List;

public class Es_ShipListDialog extends ESDialog {
    private static final int NumShipsPerPage = 5;
    public static String RULE_DIALOG_OPTION = "ESShipList";
    private static String RULE_PREV_OPTION = "ESShipPickerPREV";
    private static String RULE_NEXT_OPTION = "ESShipPickerNEXT";

    public static Object addReturnOption(OptionPanelAPI options) {
        options.addOption(StringUtils.getString("ShipListDialog", "BackToShipList"), RULE_DIALOG_OPTION);
        return RULE_DIALOG_OPTION;
    }

    public static Object addReturnOptionWithShortcut(OptionPanelAPI options) {
        Object option = addReturnOption(options);
        options.setShortcut(option, Keyboard.KEY_ESCAPE, false, false, false, true);
        return RULE_DIALOG_OPTION;
    }

    @Override
    protected void process(final ESDialogContext context, TextPanelAPI textPanel, OptionPanelAPI options, VisualPanelAPI visual) {
        options.clearOptions();

        final InteractionDialogAPI dialog = context.getDialog();

        List<FleetMemberAPI> validSelectionList = context.getShipList();
        int rows = validSelectionList.size() > 8 ? (int) Math.ceil(validSelectionList.size() / 8f) : 1;
        int cols = Math.min(validSelectionList.size(), 10);
        cols = Math.max(cols, 4);

        dialog.showFleetMemberPickerDialog(
                StringUtils.getString("ShipListDialog", "SelectShip"),
                StringUtils.getString("ShipListDialog", "Confirm"),
                StringUtils.getString("ShipListDialog", "Cancel"),
                rows,
                cols, 88f, true, false, validSelectionList, new FleetMemberPickerListener() {
                    @Override
                    public void pickedFleetMembers(List<FleetMemberAPI> members) {
                        if (members != null && !members.isEmpty()) {
                            context.getLocalMemory().set(Es_ShipPicked.MEM_KEY, members.get(0).getId());
                            dialog.getPlugin().optionSelected(Es_ShipDialog.RULE_DIALOG_OPTION, Es_ShipDialog.RULE_DIALOG_OPTION);
                        } else {
                            //sue me
                            dialog.getPlugin().optionSelected("ESMainMenu", ESDialog.RULE_DIALOG_OPTION);
                        }
                    }

                    @Override
                    public void cancelledFleetMemberPicking() {
                        dialog.getPlugin().optionSelected("ESMainMenu", ESDialog.RULE_DIALOG_OPTION);
                    }
                });
    }
}
