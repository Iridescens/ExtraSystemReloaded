package extrasystemreloaded.campaign.rulecmd;

import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.FleetMemberPickerListener;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.TextPanelAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.FleetEncounterContext;
import com.fs.starfarer.api.impl.campaign.FleetInteractionDialogPluginImpl;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Log4j
public class ESScanFleet extends BaseCommandPlugin {
    private static float NOTABLE_BANDWIDTH = 180f;

    @Override
    public boolean doesCommandAddOptions() {
        return false;
    }

    @Override
    public boolean execute(final String ruleId, final InteractionDialogAPI dialog, final List<Misc.Token> params, final Map<String, MemoryAPI> memoryMap) {
        if (dialog == null) return false;

        List<FleetMemberAPI> validSelectionList = new ArrayList<>();

        if (dialog.getInteractionTarget() != null) {

            FleetInteractionDialogPluginImpl interactionPlugin = (FleetInteractionDialogPluginImpl) dialog.getPlugin();
            FleetEncounterContext context = (FleetEncounterContext) interactionPlugin.getContext();
            CampaignFleetAPI otherFleet = context.getBattle().getNonPlayerCombined();

            for (FleetMemberAPI fm : otherFleet.getMembersWithFightersCopy()) {
                if (fm.isFighterWing()) continue;

                if (Es_ModPlugin.hasData(fm.getId())) {
                    ExtraSystems es = Es_ModPlugin.getData(fm.getId());

                    log.info(String.format("ExtraSystems info for ship [%s]: upg [%s] aug [%s] bdw [%s]",
                            fm.getShipName(),
                            es.hasUpgrades(),
                            es.hasAugments(),
                            es.getBandwidth(fm)));

                    if (es.hasUpgrades() || es.hasAugments() || es.getBandwidth(fm) >= NOTABLE_BANDWIDTH) {
                        validSelectionList.add(fm);
                    }
                }
            }

            log.info(String.format("Found [%s] notable modified ships.", validSelectionList.size()));
        }


        int rows = validSelectionList.size() > 8 ? (int) Math.ceil(validSelectionList.size() / 8f) : 1;
        int cols = Math.min(validSelectionList.size(), 10);
        cols = Math.max(cols, 4);

        dialog.showFleetMemberPickerDialog(
                StringUtils.getString("ShipListDialog", "SelectShip"),
                StringUtils.getString("ShipListDialog", "Confirm"),
                StringUtils.getString("ShipListDialog", "Cancel"),
                rows,
                cols, 88f, true, false, validSelectionList, new FleetMemberPickerListener() {
                    @Override
                    public void pickedFleetMembers(List<FleetMemberAPI> members) {
                        if (members != null && !members.isEmpty()) {
                            finishPicking(members.get(0), dialog);
                        } else {
                            finishPicking(null, dialog);
                        }
                    }

                    @Override
                    public void cancelledFleetMemberPicking() {
                        finishPicking(null, dialog);
                    }
                });

        return true;
    }

    private static void finishPicking(FleetMemberAPI fm, InteractionDialogAPI dialog) {
        if (fm != null) {
            ExtraSystems es = ExtraSystems.getForFleetMember(fm);
            float bandwidth = es.getBandwidth(fm);

            TextPanelAPI textPanel = dialog.getTextPanel();
            StringUtils.getTranslation("FleetScanner", "ShipHasUpgrades")
                    .format("name", fm.getShipName(), fm.getFleetData().getFleet().getFaction().getColor())
                    .format("bandwidth", BandwidthUtil.getFormattedBandwidthWithName(bandwidth), Bandwidth.getBandwidthColor(bandwidth))
                    .addToTextPanel(textPanel);

            for (final Upgrade upgrade : UpgradesHandler.UPGRADES_LIST) {
                if(es.getUpgrade(upgrade) > 0) {
                    final TooltipMakerAPI tooltip = textPanel.beginTooltip();

                    StringUtils.getTranslation("FleetScanner", "UpgradeNameWithLevelAndMax")
                            .format("upgradeName", upgrade.getName(), upgrade.getColor())
                            .format("level", es.getUpgrade(upgrade))
                            .format("max", upgrade.getMaxLevel(fm))
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
