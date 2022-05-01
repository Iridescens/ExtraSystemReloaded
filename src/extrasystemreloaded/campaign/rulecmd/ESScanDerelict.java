package extrasystemreloaded.campaign.rulecmd;

import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.impl.campaign.DerelictShipEntityPlugin;
import com.fs.starfarer.api.impl.campaign.rulecmd.BaseCommandPlugin;
import com.fs.starfarer.api.ui.BaseTooltipCreator;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import extrasystemreloaded.Es_ModPlugin;
import extrasystemreloaded.systems.augments.Augment;
import extrasystemreloaded.systems.augments.AugmentsHandler;
import extrasystemreloaded.systems.bandwidth.Bandwidth;
import extrasystemreloaded.systems.bandwidth.BandwidthUtil;
import extrasystemreloaded.systems.upgrades.Upgrade;
import extrasystemreloaded.systems.upgrades.UpgradesHandler;
import extrasystemreloaded.util.ExtraSystems;
import extrasystemreloaded.util.StringUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;

import java.util.List;
import java.util.Map;

@Log4j
public class ESScanDerelict extends BaseCommandPlugin {
    private static float NOTABLE_BANDWIDTH = 180f;

    @Override
    public boolean doesCommandAddOptions() {
        return false;
    }

    @Override
    public boolean execute(final String ruleId, final InteractionDialogAPI dialog, final List<Misc.Token> params, final Map<String, MemoryAPI> memoryMap) {
        if (dialog == null) return false;

        SectorEntityToken interactionTarget = dialog.getInteractionTarget();
        DerelictShipEntityPlugin plugin = (DerelictShipEntityPlugin) interactionTarget.getCustomPlugin();

        scanDerelict(interactionTarget.getId(), plugin.getData().ship.shipName, plugin.getData().ship.getVariant(), dialog);

        return true;
    }

    private static void scanDerelict(String id, String shipName, ShipVariantAPI var, InteractionDialogAPI dialog) {

            ExtraSystems es = Es_ModPlugin.getData(id);
            float bandwidth = es.getBandwidth();

            TextPanelAPI textPanel = dialog.getTextPanel();
            StringUtils.getTranslation("FleetScanner", "ShipHasUpgrades")
                    .format("name", shipName, Misc.getHighlightColor())
                    .format("bandwidth", BandwidthUtil.getFormattedBandwidthWithName(bandwidth), Bandwidth.getBandwidthColor(bandwidth))
                    .addToTextPanel(textPanel);

            for (final Upgrade upgrade : UpgradesHandler.UPGRADES_LIST) {
                if(es.getUpgrade(upgrade) > 0) {
                    final TooltipMakerAPI tooltip = textPanel.beginTooltip();

                    StringUtils.getTranslation("FleetScanner", "UpgradeNameWithLevelAndMax")
                            .format("upgradeName", upgrade.getName(), upgrade.getColor())
                            .format("level", es.getUpgrade(upgrade))
                            .format("max", upgrade.getMaxLevel(var.getHullSize()))
                            .addToTooltip(tooltip);

                    tooltip.addTooltipToPrevious(new UpgradeTooltip(upgrade, tooltip), TooltipMakerAPI.TooltipLocation.BELOW);

                    textPanel.addTooltip();
                }
            }

            for (final Augment augment : AugmentsHandler.AUGMENT_LIST) {
                if(es.hasAugment(augment)) {
                    final TooltipMakerAPI tooltip = textPanel.beginTooltip();

                    tooltip.addPara(augment.getName(), augment.getMainColor(), 0f);
                    tooltip.addTooltipToPrevious(new AugmentTooltip(augment, tooltip), TooltipMakerAPI.TooltipLocation.BELOW);

                    textPanel.addTooltip();
                }
            }
    }

    @RequiredArgsConstructor
    protected static class AugmentTooltip extends BaseTooltipCreator {
        @Getter
        private final Augment augment;
        private final TooltipMakerAPI tooltip;

        @Override
        public float getTooltipWidth(Object tooltipParam) {
            return Math.min(tooltip.computeStringWidth(augment.getDescription()), 300f);
        }

        @Override
        public void createTooltip(TooltipMakerAPI tooltip, boolean expanded, Object tooltipParam) {
            tooltip.addPara(augment.getDescription(), 0f);
        }
    }

    @RequiredArgsConstructor
    protected static class UpgradeTooltip extends BaseTooltipCreator {
        @Getter
        private final Upgrade upgrade;
        private final TooltipMakerAPI tooltip;

        @Override
        public float getTooltipWidth(Object tooltipParam) {
            return Math.min(tooltip.computeStringWidth(upgrade.getDescription()), 300f);
        }

        @Override
        public void createTooltip(TooltipMakerAPI tooltip, boolean expanded, Object tooltipParam) {
            tooltip.addPara(upgrade.getDescription(), 0f);
        }
    }
}
