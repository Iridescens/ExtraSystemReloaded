package extrasystemreloaded.augments.items;

import extrasystemreloaded.augments.Augment;
import extrasystemreloaded.augments.AugmentSpecialItem;
import extrasystemreloaded.augments.AugmentsHandler;
import extrasystemreloaded.augments.impl.DriveFluxVent;
import extrasystemreloaded.augments.impl.EqualizerCore;

public class DriveVentItem extends AugmentSpecialItem {
    @Override
    public Augment getAugment() {
        return AugmentsHandler.AUGMENTS.get(DriveFluxVent.AUGMENT_KEY);
    }
}
