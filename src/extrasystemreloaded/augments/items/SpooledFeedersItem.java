package extrasystemreloaded.augments.items;

import extrasystemreloaded.augments.Augment;
import extrasystemreloaded.augments.AugmentSpecialItem;
import extrasystemreloaded.augments.AugmentsHandler;
import extrasystemreloaded.augments.impl.PhasefieldEngine;
import extrasystemreloaded.augments.impl.SpooledFeeders;

public class SpooledFeedersItem extends AugmentSpecialItem {
    @Override
    public Augment getAugment() {
        return AugmentsHandler.AUGMENTS.get(SpooledFeeders.AUGMENT_KEY);
    }
}
