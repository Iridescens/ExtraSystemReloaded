package extrasystemreloaded.systems.augments.items;

import extrasystemreloaded.systems.augments.Augment;
import extrasystemreloaded.systems.augments.AugmentSpecialItem;
import extrasystemreloaded.systems.augments.AugmentsHandler;
import extrasystemreloaded.systems.augments.impl.PlasmaFluxCatalyst;

public class PlasmaCatalystItem extends AugmentSpecialItem {
    @Override
    public Augment getAugment() {
        return AugmentsHandler.AUGMENTS.get("PlasmaCatalyst");
    }
}
