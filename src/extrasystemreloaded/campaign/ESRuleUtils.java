package extrasystemreloaded.campaign;

import com.fs.starfarer.api.campaign.OptionPanelAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import extrasystemreloaded.campaign.rulecmd.Es_ShipDialog;
import extrasystemreloaded.campaign.rulecmd.Es_ShipListDialog;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access=AccessLevel.PRIVATE)
public class ESRuleUtils {
    /**
     * if FM is null, adds the Ship List option and Main Menu options, with the former having Esc shortcut.
     * if FM is not null, adds the Ship option, Ship List option, and Main Menu options, with the first having shortcut.
     * @param options
     * @param fm
     * @param noShortcuts
     * @return
     */
    public static void addReturnOptions(OptionPanelAPI options, FleetMemberAPI fm, boolean noShortcuts) {
        if(fm != null) {
            if(!noShortcuts) {
                Es_ShipDialog.addReturnOptionWithShortcut(options);
            }

            Es_ShipListDialog.addReturnOption(options);
            ESDialog.addReturnOption(options);
        } else {
            Es_ShipListDialog.addReturnOption(options);

            if(!noShortcuts) {
                ESDialog.addReturnOptionWithShortcut(options);
            }
        }
    }
}
