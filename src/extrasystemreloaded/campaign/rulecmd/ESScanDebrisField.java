package extrasystemreloaded.campaign.rulecmd;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.FleetMemberPickerListener;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.TextPanelAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.fleet.FleetMemberType;
import com.fs.starfarer.api.impl.campaign.ids.Entities;
import com.fs.starfarer.api.impl.campaign.ids.MemFlags;
import com.fs.starfarer.api.impl.campaign.rulecmd.BaseCommandPlugin;
import com.fs.starfarer.api.impl.campaign.rulecmd.salvage.special.ShipRecoverySpecial;
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
public class ESScanDebrisField extends BaseCommandPlugin {
    private static float NOTABLE_BANDWIDTH = 180f;

    @Override
    public boolean doesCommandAddOptions() {
        return false;
    }

    @Override
    public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {
        if (dialog == null) return false;

        SectorEntityToken interactionTarget = dialog.getInteractionTarget();
        if (interactionTarget.getCustomEntityType().equals(Entities.DEBRIS_FIELD_SHARED)
                && interactionTarget.getMemoryWithoutUpdate().contains(MemFlags.SALVAGE_SPECIAL_DATA)
                && interactionTarget.getMemoryWithoutUpdate().get(MemFlags.SALVAGE_SPECIAL_DATA) instanceof ShipRecoverySpecial.ShipRecoverySpecialData) {
            ShipRecoverySpecial.ShipRecoverySpecialData data = (ShipRecoverySpecial.ShipRecoverySpecialData) interactionTarget.getMemoryWithoutUpdate().get(MemFlags.SALVAGE_SPECIAL_DATA);

            if(data.ships.size() > 1) {
                scanMultipleShips(dialog, data.ships);
            } else {
                scanPerShipData(0, data.ships.get(0), dialog);
            }
        }
        return true;
    }

    private static void scanMultipleShips(final InteractionDialogAPI dialog, final List<ShipRecoverySpecial.PerShipData> shipsData) {
        //oh no.
        final List<FleetMemberAPI> validSelectionList = new ArrayList<>();
        for(ShipRecoverySpecial.PerShipData shipData : shipsData) {
            FleetMemberAPI fm = Global.getFactory().createFleetMember(FleetMemberType.SHIP, shipData.getVariant());
            validSelectionList.add(fm);
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
                            int index = validSelectionList.indexOf(members.get(0));
                            scanPerShipData(index, shipsData.get(index), dialog);
                        }
                    }

                    @Override
                    public void cancelledFleetMemberPicking() {
                    }
                });
    }

    private static void scanPerShipData(int index, ShipRecoverySpecial.PerShipData shipData, InteractionDialogAPI dialog) {
        String esId = dialog.getInteractionTarget().getId() + String.valueOf(index + 1);
        scanDerelict(esId, shipData.shipName != null ? shipData.shipName : "???", shipData.getVariant(), dialog);
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
            if (es.getUpgrade(upgrade) > 0) {
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
            if (es.hasAugment(augment)) {
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
