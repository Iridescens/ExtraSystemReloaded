package extrasystemreloaded.systems.augments;

import com.fs.starfarer.api.campaign.CargoTransferHandlerAPI;
import com.fs.starfarer.api.campaign.impl.items.BaseSpecialItemPlugin;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

import java.awt.*;

public abstract class AugmentSpecialItem extends BaseSpecialItemPlugin {
    public abstract Augment getAugment();

    public boolean shouldRemoveOnRightClickAction() {
        return false;
    }

    @Override
    public void createTooltip(TooltipMakerAPI tooltip, boolean expanded, CargoTransferHandlerAPI transferHandler, Object stackSource, boolean useGray) {
        float opad = 10.0F;
        tooltip.addTitle(this.getName());
        String design = this.getDesignType();
        Misc.addDesignTypePara(tooltip, design, opad);
        if (!this.spec.getDesc().isEmpty()) {
            Color c = Misc.getTextColor();
            if (useGray) {
                c = Misc.getGrayColor();
            }

            tooltip.addPara(this.spec.getDesc(), c, opad);
        }

        if(this.getAugment() != null) {
            tooltip.addPara(this.getAugment().getTooltip(), Misc.getTextColor(), opad);
        }
    }
}
