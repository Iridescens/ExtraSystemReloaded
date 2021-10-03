package extrasystemreloaded.augments.gui;

import extrasystemreloaded.augments.Augment;
import extrasystemreloaded.gui.Tab;
import extrasystemreloaded.gui.TabsHeader;

public class AugmentTab extends Tab {
    protected Augment augment;
    public AugmentTab(TabsHeader parent, Augment augment) {
        super(parent, augment.getName(), augment.getMainColor());
    }
}
