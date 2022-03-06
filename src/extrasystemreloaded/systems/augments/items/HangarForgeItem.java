package extrasystemreloaded.systems.augments.items;

import extrasystemreloaded.systems.augments.Augment;
import extrasystemreloaded.systems.augments.AugmentSpecialItem;
import extrasystemreloaded.systems.augments.AugmentsHandler;
import extrasystemreloaded.systems.augments.impl.HangarForge;

public class HangarForgeItem extends AugmentSpecialItem {
    @Override
    public Augment getAugment() {
        return AugmentsHandler.AUGMENTS.get(HangarForge.AUGMENT_KEY);
    }
}
