package extrasystemreloaded.systems.augments.dialog;

import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.OptionPanelAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.rules.MemKeys;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.util.Misc;
import extrasystemreloaded.dialog.DialogOption;
import extrasystemreloaded.dialog.PaginationOption;
import extrasystemreloaded.dialog.modifications.SystemState;
import extrasystemreloaded.campaign.rulecmd.ESInteractionDialogPlugin;
import extrasystemreloaded.systems.augments.Augment;
import extrasystemreloaded.systems.augments.AugmentsHandler;
import extrasystemreloaded.util.ExtraSystems;
import extrasystemreloaded.util.StringUtils;
import org.lazywizard.lazylib.MathUtils;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static extrasystemreloaded.campaign.rulecmd.ESInteractionDialogPlugin.PAGINATION_KEY;

public class AugmentsPickerState extends SystemState {
    private static final int MIN_CHOICES_FOR_PAGINATION = 8;
    private static final int CHOICES_PER_PAGE = 6;
    private static final DialogOption OPTION_NEXTPAGE = new PaginationOption(true);
    private static final DialogOption OPTION_PREVPAGE = new PaginationOption(false);

    @Override
    public String getOptionText(ESInteractionDialogPlugin plugin, FleetMemberAPI fm, ExtraSystems es) {
        return StringUtils.getString("AugmentsDialog", "OpenAugmentOptions");
    }

    @Override
    public void execute(InteractionDialogAPI dialog, ESInteractionDialogPlugin plugin) {
        //populate upgrade options
        OptionPanelAPI options = dialog.getOptionPanel();
        FleetMemberAPI fm = plugin.getShip();
        ExtraSystems es = ExtraSystems.getForFleetMember(fm);
        MarketAPI market = plugin.getMarket();

        options.clearOptions();

        List<Augment> sortedAugmentsList = getSortedAugmentList(fm, es, market);

        int startIndex = 0;
        int endIndex = sortedAugmentsList.size() - 1;
        int pageIndex = 0;

        if(plugin.getMemoryMap().get(MemKeys.LOCAL).contains(PAGINATION_KEY)) {
            pageIndex = plugin.getMemoryMap().get(MemKeys.LOCAL).getInt(PAGINATION_KEY);
        }

        if(endIndex >= MIN_CHOICES_FOR_PAGINATION) {
            pageIndex = MathUtils.clamp(pageIndex, 0, sortedAugmentsList.size() / CHOICES_PER_PAGE);
            startIndex = CHOICES_PER_PAGE * pageIndex;
            endIndex = Math.min(startIndex + CHOICES_PER_PAGE, sortedAugmentsList.size());
        }

        addAugmentsToOptions(options, plugin, fm, es, market, sortedAugmentsList, startIndex, endIndex);

        if(endIndex >= MIN_CHOICES_FOR_PAGINATION) {
            OPTION_PREVPAGE.addToOptions(options, plugin, fm, es);
            OPTION_NEXTPAGE.addToOptions(options, plugin, fm, es);
            if (startIndex == 0) {
                options.setEnabled(OPTION_PREVPAGE, false);
            }
            if (endIndex + CHOICES_PER_PAGE >= sortedAugmentsList.size()) {
                options.setEnabled(OPTION_NEXTPAGE, false);
            }
        }

        ESInteractionDialogPlugin.SYSTEM_PICKER.addToOptions(options, plugin, fm, es, Keyboard.KEY_ESCAPE);
    }

    private void addAugmentsToOptions(OptionPanelAPI options, ESInteractionDialogPlugin plugin, FleetMemberAPI fm, ExtraSystems es, MarketAPI market, List<Augment> augments, int startIndex, int endIndex) {
        ShipAPI.HullSize hullSize = fm.getHullSpec().getHullSize();

        for (int i = startIndex; i < endIndex; i++) {
            Augment augment = augments.get(i);
            boolean hasAugment = es.hasAugment(augment);

            Color color = Misc.getButtonTextColor();

            if (hasAugment) {
                color = new Color(196, 189, 56);
            } else if (!augment.canApply(fm)) {
                color = new Color(173, 94, 94);
            }

            ChosenAugmentState upgradeState = new ChosenAugmentState(augment);
            options.addOption(
                    upgradeState.getOptionText(plugin, fm, es),
                    upgradeState,
                    color,
                    augment.getDescription());
        }
    }

    private List<Augment> getSortedAugmentList(FleetMemberAPI fm, ExtraSystems buff, MarketAPI market) {
        //sort augment list so that augmnets that we can't install are put in last.
        List<Augment> sortedAugmentList = new ArrayList<>();

        //can afford an upgrade, and actually perform it.
        for(Augment augment : AugmentsHandler.AUGMENT_LIST) {
            if(!augment.shouldShow(fm, buff, market)) {
                continue;
            }

            if (buff.hasAugment(augment)) {
                continue;
            }

            if(augment.canApply(fm)) {
                sortedAugmentList.add(augment);
            }
        }

        //can not afford an upgrade
        for(Augment augment : AugmentsHandler.AUGMENT_LIST) {
            if(!augment.shouldShow(fm, buff, market)) {
                continue;
            }

            if (buff.hasAugment(augment)) {
                continue;
            }

            if(!sortedAugmentList.contains(augment)) {
                sortedAugmentList.add(augment);
            }
        }

        //cannot do an upgrade
        for(Augment augment : AugmentsHandler.AUGMENT_LIST) {
            if(!augment.shouldShow(fm, buff, market)) {
                continue;
            }

            if(!sortedAugmentList.contains(augment)) {
                sortedAugmentList.add(augment);
            }
        }

        return sortedAugmentList;
    }
}
