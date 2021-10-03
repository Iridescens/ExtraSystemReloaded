package extrasystemreloaded.augments.gui;

import com.fs.starfarer.api.ui.PositionAPI;
import extrasystemreloaded.augments.Augment;
import extrasystemreloaded.gui.*;

public class AugmentsTabHeader extends TabsHeader {
    public AugmentsTabHeader(TabsPanel tabsParent) {
        super(tabsParent);
    }

    public Tab addAugmentTab(Augment augment) {
        Tab addedHeader = new AugmentTab(this, augment);
        this.tabsList.add(addedHeader);

        this.alignTab(addedHeader);

        return addedHeader;
    }

    @Override
    protected PositionAPI alignTab(Tab header) {
        PositionAPI parentPos = this.getPanel().getPosition();
        PositionAPI pos = header.getUIComponent().getPosition();
        if(this.tabsList.size() >= 2) {
            return pos.belowMid(this.tabsList.get(this.tabsList.size() - 2).getUIComponent(), 5f);
        }
        System.out.println(parentPos);
        pos = pos.setLocation(parentPos.getX() + 4, parentPos.getY() - parentPos.getHeight() + 4);
        System.out.println(pos);
        return pos;
    }

    @Override
    public void checkForActivatedComponents() {
        super.checkForActivatedComponents();
    }
}
