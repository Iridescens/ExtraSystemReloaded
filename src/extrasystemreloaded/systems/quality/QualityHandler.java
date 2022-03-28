package extrasystemreloaded.systems.quality;

import extrasystemreloaded.campaign.rulecmd.Es_ShipDialog;
import extrasystemreloaded.campaign.rulecmd.Es_ShipQualityDialog;

public class QualityHandler {
    private static int UPGRADE_OPTION_ORDER = 0;
    public static void initialize() {
        Es_ShipDialog.addShipOption(new Es_ShipQualityDialog.QualityOption(UPGRADE_OPTION_ORDER));
    }
}
