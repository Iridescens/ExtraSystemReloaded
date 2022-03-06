package extrasystemreloaded.systems.augments.items;

import extrasystemreloaded.systems.augments.Augment;
import extrasystemreloaded.systems.augments.AugmentSpecialItem;
import extrasystemreloaded.systems.augments.AugmentsHandler;
import extrasystemreloaded.systems.augments.impl.PhasefieldEngine;

public class PhasefieldEngineItem extends AugmentSpecialItem {
    @Override
    public Augment getAugment() {
        return AugmentsHandler.AUGMENTS.get(PhasefieldEngine.AUGMENT_KEY);
    }
}
