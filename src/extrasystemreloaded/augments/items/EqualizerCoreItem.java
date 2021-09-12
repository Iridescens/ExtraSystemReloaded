package extrasystemreloaded.augments.items;

import extrasystemreloaded.augments.Augment;
import extrasystemreloaded.augments.AugmentSpecialItem;
import extrasystemreloaded.augments.AugmentsHandler;
import extrasystemreloaded.augments.impl.EqualizerCore;
import extrasystemreloaded.augments.impl.PhasefieldEngine;

public class EqualizerCoreItem extends AugmentSpecialItem {
    @Override
    public Augment getAugment() {
        return AugmentsHandler.AUGMENTS.get(EqualizerCore.AUGMENT_KEY);
    }
}
