package extrasystemreloaded.augments.items;

import extrasystemreloaded.augments.Augment;
import extrasystemreloaded.augments.AugmentSpecialItem;
import extrasystemreloaded.augments.AugmentsHandler;
import extrasystemreloaded.augments.impl.PhasefieldEngine;
import extrasystemreloaded.augments.impl.PlasmaFluxCatalyst;

public class PlasmaCatalystItem extends AugmentSpecialItem {
    @Override
    public Augment getAugment() {
        return AugmentsHandler.AUGMENTS.get(PlasmaFluxCatalyst.AUGMENT_KEY);
    }
}
