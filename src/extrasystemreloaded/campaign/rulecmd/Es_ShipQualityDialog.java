package extrasystemreloaded.campaign.rulecmd;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.rules.MemKeys;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.rulecmd.BaseCommandPlugin;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.campaign.BuffManager;
import extrasystemreloaded.Es_ModPlugin;
import extrasystemreloaded.campaign.Es_ShipLevelFleetData;
import extrasystemreloaded.util.ESUpgrades;
import extrasystemreloaded.util.upgrades.Upgrade;
import org.apache.log4j.Level;

import java.awt.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static extrasystemreloaded.util.ESUpgrades.UPGRADES;
import static extrasystemreloaded.util.ESUpgrades.getUpgradeCosts;
import static extrasystemreloaded.util.Utilities.*;

public class Es_ShipQualityDialog extends BaseCommandPlugin {
    private static final org.apache.log4j.Logger log = Global.getLogger(Es_ShipQualityDialog.class);
    public static final boolean UPGRADE_ALWAYS_SUCCEED = Global.getSettings().getBoolean("upgradeAlwaysSucceed");
    public static final float BASE_FAILURE_MINFACTOR = Global.getSettings().getFloat("baseFailureMinFactor");

    private static final boolean DEBUG_UPGRADES_REMOVE_COST = false;
    private static final int NumShipsPerPage = 5;
    private static final float baseQualityStep = 0.025f;

    private String FunctionType;

    private TextPanelAPI textPanel;
    private OptionPanelAPI options;
    private VisualPanelAPI visual;

    private MarketAPI currMarket;
    private CampaignFleetAPI playerFleet;
    private List<FleetMemberAPI> ShipList;
    private Map<FleetMemberAPI, String> ShipNameMap = new HashMap<>();
    private Map<FleetMemberAPI, String> ShipOptionMap = new HashMap<>();

    private float shipQuality;
    private float shipBaseValue;
    private float estimatedOverhaulCost;


    @Override
    public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {
        String newFunctionType = params.get(0).getString(memoryMap);
        if(newFunctionType != null) {
            FunctionType = newFunctionType;
        }
        currMarket = dialog.getInteractionTarget().getMarket();

        playerFleet = Global.getSector().getPlayerFleet();
        ShipList = playerFleet.getFleetData().getMembersListCopy();
        Iterator<FleetMemberAPI> iterator = ShipList.iterator();
        while (iterator.hasNext()) {
            FleetMemberAPI fleetMemberAPI = iterator.next();
            if (fleetMemberAPI.isFighterWing()) {
                iterator.remove();
            }
        }

        options = dialog.getOptionPanel();
        visual = dialog.getVisualPanel();
        textPanel = dialog.getTextPanel();

        updateOptions(params, memoryMap);

        return true;
    }

    private void updateOptions(List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {
        MemoryAPI memory = memoryMap.get(MemKeys.LOCAL);
        String shipSelectedId = memory.getString("$ShipSelectedId");
        FleetMemberAPI selectedShip = null;
        Es_ShipLevelFleetData buff = null;
        if (shipSelectedId != null) {
            selectedShip = getSelectedShip(shipSelectedId);

            BuffManager.Buff thisBuff = selectedShip.getBuffManager().getBuff(Es_ShipLevelFleetData.Es_LEVEL_FUNCTION_ID);
            if (!(thisBuff instanceof Es_ShipLevelFleetData)) {
                selectedShip.getBuffManager().addBuff(new Es_ShipLevelFleetData(selectedShip));
                thisBuff = selectedShip.getBuffManager().getBuff(Es_ShipLevelFleetData.Es_LEVEL_FUNCTION_ID);
            }
            buff = (Es_ShipLevelFleetData) thisBuff;

            shipQuality = buff.getQualityFactor();
            shipBaseValue = selectedShip.getHullSpec().getBaseValue();
            estimatedOverhaulCost = Math.round(shipBaseValue * (float) Math.pow(shipQuality, 2) / 2 * 100f) / 100f; //* (bonusQ()*0.5f/baseQualityStep); // pay more when bonusQ is higher
        }

        Object extraUpgradeId = memory.get("$UpgradeId");
        Upgrade selectedUpgrade = null;
        if(extraUpgradeId != null) {
            for(ESUpgrades.UpgradeKey upgradeKey : UPGRADES.keySet()) {
                if(upgradeKey.compareKey((String) extraUpgradeId)) {
                    selectedUpgrade = UPGRADES.get(upgradeKey);
                }
            }
        }

        options.clearOptions();

        switch (FunctionType) {
            case "ShipList":
                int shipPageIndex = (int) memory.getFloat("$ShipPageIndex");
                for (FleetMemberAPI fleetMemberAPI : ShipList) {
                    ShipNameMap.put(fleetMemberAPI, fleetMemberAPI.getShipName() + "(" + fleetMemberAPI.getHullSpec().getHullName() + ")");
                    ShipOptionMap.put(fleetMemberAPI, fleetMemberAPI.getId());
                }
                for (int i = shipPageIndex * NumShipsPerPage; i < shipPageIndex * NumShipsPerPage + 5; i++) {
                    if (ShipList.size() > i) {
                        FleetMemberAPI fleetMemberAPI = ShipList.get(i);
                        options.addOption(ShipNameMap.get(fleetMemberAPI), ShipOptionMap.get(fleetMemberAPI));
                    }
                }
                visual.showFleetInfo("Your fleet", playerFleet, null, null);
                options.addOption("Previous page", "ESShipPickerPREV");
                options.addOption("Next page", "ESShipPickerNEXT");
                if (shipPageIndex == 0) {
                    options.setEnabled("ESShipPickerPREV", false);
                }
                if (shipPageIndex * NumShipsPerPage + 5 >= ShipList.size()) {
                    options.setEnabled("ESShipPickerNEXT", false);
                }
                options.addOption("Return to ES main menu", "ESMainMenu", null);
                break;
            case "ShipSelected":
                if (selectedShip != null) {
                    visual.showFleetMemberInfo(selectedShip);

                    textPanel.addParagraph(TextTip.quality1);
                    String shipQualityText = "" + Math.round(shipQuality * 1000f) / 1000f + getQualityName(shipQuality);
                    textPanel.appendToLastParagraph(" " + shipQualityText);
                    textPanel.highlightLastInLastPara(shipQualityText, getQualityColor(shipQuality));

                    for (Upgrade upgrade : ESUpgrades.UPGRADES_LIST) {
                        int level = buff.getUpgrades().getUpgrade(upgrade.getKey());
                        ShipAPI.HullSize hullSize = selectedShip.getHullSpec().getHullSize();
                        int max = upgrade.getMaxLevel() != -1 ? upgrade.getMaxLevel() : ESUpgrades.HULLSIZE_TO_MAXLEVEL.get(hullSize);

                        textPanel.addParagraph(upgrade.getName() + " (" + level + " / " + max + ")");
                    }

                    textPanel.addParagraph("-----------------------", Color.gray);

                    textPanel.addParagraph("Pick an operation");
                    options.addOption("Quality", "ESShipQualityPicked", null);
                    options.addOption("Upgrades", "ESShipExtraUpgradesPicked", null);
                } else {
                    log.log(Level.INFO, String.format("did not find fm"));
                }

                options.addOption("Back to ship list", "ESShipQuality");
                break;
            case "QualityUpgradeSelected":
                if (selectedShip != null) {
                    textPanel.addParagraph(TextTip.quality1);
                    String shipQualityText = "" + Math.round(shipQuality * 1000f) / 1000f + getQualityName(shipQuality);
                    textPanel.appendToLastParagraph(" " + shipQualityText);
                    textPanel.highlightLastInLastPara(shipQualityText, getQualityColor(shipQuality));
                    shipQualityText = "Local industrial facilities are capable of improving overall quality rating of ships by " + bonusQualityAtMarket();
                    textPanel.addParagraph(shipQualityText);
                    textPanel.highlightLastInLastPara("" + bonusQualityAtMarket(), Color.green);

                    shipQualityText = "On-site team estimates ship's overhaul to cost " + estimatedOverhaulCost + " credits";
                    textPanel.addParagraph(shipQualityText);
                    textPanel.highlightLastInLastPara("" + estimatedOverhaulCost, Color.green);

                    options.addOption("Agree to conditions", "ESShipQualityApply", null);
                    isAbleToPayForQualityUpgrade(estimatedOverhaulCost);

                    options.addOption("Back to ship", shipSelectedId);
                }
                options.addOption("Back to ship list", "ESShipQuality");
                break;
            case "ApplyQualityUpgrade":
                if (selectedShip != null) {
                    if (!Es_ModPlugin.ShipQualityData.containsKey(shipSelectedId)) {
                        Es_ModPlugin.ShipQualityData.put(shipSelectedId, shipQuality);
                    }
                    float newQuality = Math.round((shipQuality + bonusQualityAtMarket()) * 1000f) / 1000f; // qualityFactor + bonus
                    Es_ModPlugin.ShipQualityData.remove(shipSelectedId);
                    selectedShip.getBuffManager().removeBuff(Es_ShipLevelFleetData.Es_LEVEL_FUNCTION_ID);
                    selectedShip.getBuffManager().addBuff(new Es_ShipLevelFleetData(selectedShip, buff.getUpgrades(), newQuality));
                    playerFleet.getCargo().getCredits().subtract(estimatedOverhaulCost);
                    String text2 = "After some improvements here and there, your ship now has quality rating of " + newQuality;
                    textPanel.addParagraph(text2);
                    textPanel.highlightLastInLastPara("" + newQuality, getQualityColor(newQuality));
                    options.addOption("Back to ship", shipSelectedId);
                }
                options.addOption("Back to ship list", "ESShipQuality");
                break;
            case "ExtraUpgradesSelected":
                int upgradePageIndex = (int) memory.getFloat("$ExtraUpgradePageIndex");
                if (selectedShip != null) {
                    populateAbilityOptions(selectedShip, buff, upgradePageIndex, false);
                    options.addOption("Back to ship", shipSelectedId);
                }
                options.addOption("Back to ship list", "ESShipQuality");
                break;
            case "ExtraUpgradesChangedPage":
                int newPageIndex = (int) memory.getFloat("$ExtraUpgradePageIndex");
                if (selectedShip != null) {
                    populateAbilityOptions(selectedShip, buff, newPageIndex, true);
                    options.addOption("Back to ship", shipSelectedId);
                }
                options.addOption("Back to ship list", "ESShipQuality");
                break;
            case "ConfirmExtraUpgrade":
                if (selectedShip != null && selectedUpgrade != null) {
                    populateAbilityPurchaseConfirmationOptions(selectedShip, buff, selectedUpgrade);
                    options.addOption("Back to upgrades", "ESShipExtraUpgradesPicked");
                    options.addOption("Back to ship", shipSelectedId);
                }
                options.addOption("Back to ship list", "ESShipQuality");
                break;
            case "ApplyExtraUpgrade":
                if (selectedShip != null && selectedUpgrade != null) {
                    doAbilityPurchase(selectedShip, buff, selectedUpgrade);
                    populateAbilityPurchasedOptions(selectedShip, buff, selectedUpgrade);
                    options.addOption("Back to upgrades", "ESShipExtraUpgradesPicked");
                    options.addOption("Back to ship", shipSelectedId);
                }
                options.addOption("Back to ship list", "ESShipQuality");
                break;
            default:
                break;

        }
    }

    private void isAbleToPayForQualityUpgrade(float Cost) {
        if (Cost <= playerFleet.getCargo().getCredits().get() || Es_ModPlugin.isDebugUpgradeCosts()) {
            options.setTooltip("ESShipQualityApply", "Proceed with overhaul");
        } else {
            options.setEnabled("ESShipQualityApply", false);
            options.setTooltip("ESShipQualityApply", "Insufficient credits");
        }
    }

    private float bonusQualityAtMarket() {
        return baseQualityStep * (2 + (currMarket.hasIndustry("heavyindustry") ? 1 : 0) + (currMarket.hasIndustry("orbitalworks") ? 2 : 0));
    }

    private void populateAbilityOptions(FleetMemberAPI shipSelected, Es_ShipLevelFleetData buff, int upgradePageIndex, boolean newPage) {
        options.clearOptions();
        visual.showFleetMemberInfo(shipSelected);

        if(!newPage) {
            textPanel.addParagraph(TextTip.resourceHeader, Color.green);
            textPanel.addParagraph("-----------------------", Color.gray);
            for (int i = 0; i < 7; i++) {
                String name = RESOURCE_NAME.get(i);
                textPanel.addParagraph(name + ":" + (int) getFleetCargoMap(playerFleet)[i]);
            }
            textPanel.addParagraph("-----------------------", Color.gray);

            textPanel.addParagraph(TextTip.quality1);
            float quality = Es_ModPlugin.ShipQualityData.get(shipSelected.getId());//еѕ—е€°е“ЃиґЁ
            String text = Math.round(quality * 100f) / 100f + getQualityName(quality);
            textPanel.appendToLastParagraph(text);
            textPanel.highlightLastInLastPara(text, getQualityColor(quality));
            textPanel.addParagraph(TextTip.chooseUpgrade);
        }

        for (int i = upgradePageIndex * 5; i < Math.min(upgradePageIndex * 5 + 5, ESUpgrades.UPGRADES_LIST.size()); i++) {
            Upgrade upgrade = ESUpgrades.UPGRADES_LIST.get(i);
            int level = buff.getUpgrades().getUpgrade(upgrade.getKey());
            ShipAPI.HullSize hullSize = shipSelected.getHullSpec().getHullSize();
            int max = upgrade.getMaxLevel() != -1 ? upgrade.getMaxLevel() : ESUpgrades.HULLSIZE_TO_MAXLEVEL.get(hullSize);

            options.addOption(upgrade.getName() + " (" + level + " / " + max + ")", upgrade.getKey().getKey(), upgrade.getDescription());
            if (level >= max) {
                options.setEnabled(upgrade.getKey().getKey(), false);
            }
        }

        options.addOption("Previous page", "ESShipExtraUpgradesPREV");
        options.addOption("Next page", "ESShipExtraUpgradesNEXT");
        if (upgradePageIndex == 0) {
            options.setEnabled("ESShipExtraUpgradesPREV", false);
        }
        if (upgradePageIndex * NumShipsPerPage + 5 >= ESUpgrades.UPGRADES_LIST.size()) {
            options.setEnabled("ESShipExtraUpgradesNEXT", false);
        }
    }

    private void populateAbilityPurchaseConfirmationOptions(FleetMemberAPI shipSelected, Es_ShipLevelFleetData buff, Upgrade abilitySelected) {
        options.clearOptions();
        if (shipSelected != null && abilitySelected != null) {
            ShipAPI.HullSize hullSize = shipSelected.getHullSpec().getHullSize();//жњЂй«�з­‰зє§
            int max = abilitySelected.getMaxLevel(hullSize);
            int level = buff.getUpgrades().getUpgrade(abilitySelected);

            options.addOption(String.format(OptionName.Confirm, level, max), "ESShipExtraUpgradeApply");
            if (level >= max) {
                textPanel.addParagraph(TextTip.ability2 + "(" + level + ")", Color.yellow);
                options.setEnabled("ESShipExtraUpgradeApply", false);
            } else {
                boolean isCanLevelUp = true;
                if (!DEBUG_UPGRADES_REMOVE_COST) {
                    int[] resourceCosts = getUpgradeCosts(shipSelected, abilitySelected, level, max);

                    float possibility = 1f;
                    if (!UPGRADE_ALWAYS_SUCCEED && level != 0) {
                        possibility = (float) Math.cos(Math.PI * level * 0.5f / max) * (1f - BASE_FAILURE_MINFACTOR) + BASE_FAILURE_MINFACTOR;
                    }

                    textPanel.addParagraph(TextTip.costHeader, Color.green);
                    textPanel.addParagraph("-----------------------", Color.gray);

                    for (int i = 0; i < resourceCosts.length; ++i) {
                        String name = RESOURCE_NAME.get(i);
                        float fleetcargo = getFleetCargoMap(playerFleet)[i];
                        if (resourceCosts[i] > fleetcargo) {
                            isCanLevelUp = false;
                            String suffix = TextTip.tooExpensivePrefix + (int) (resourceCosts[i] - fleetcargo) + TextTip.tooExpensiveSuffix;
                            textPanel.addParagraph(name + ":" + resourceCosts[i] + suffix, Color.red);
                        } else {
                            textPanel.addParagraph(name + ":" + resourceCosts[i]);
                        }
                    }
                    textPanel.addParagraph("-----------------------", Color.gray);
                    textPanel.addParagraph(TextTip.ability3);
                    String text1 = Math.round(possibility * 1000f) / 10f + "%";
                    textPanel.appendToLastParagraph(text1);
                    textPanel.highlightLastInLastPara(text1, Color.green);
                    textPanel.appendToLastParagraph(TextTip.ability4);
                }

                if (!isCanLevelUp) {
                    options.setEnabled("ESShipExtraUpgradeApply", false);
                }
            }
        }
    }

    private void populateAbilityPurchasedOptions(FleetMemberAPI shipSelected, Es_ShipLevelFleetData buff, Upgrade abilitySelected) {
        options.clearOptions();
        if (shipSelected != null && abilitySelected != null) {
            ShipAPI.HullSize hullSize = shipSelected.getHullSpec().getHullSize();
            int max = abilitySelected.getMaxLevel(hullSize);
            int level = buff.getUpgrades().getUpgrade(abilitySelected.getKey());
            textPanel.addParagraph(abilitySelected.getName() + "(" + level + " / " + max + ")", Color.yellow);

            options.addOption(OptionName.Repurchase, abilitySelected.getKey().getKey());
        }
    }

    private void doAbilityPurchase(FleetMemberAPI shipSelected, Es_ShipLevelFleetData buff, Upgrade abilitySelected) {
        ShipAPI.HullSize hullSize = shipSelected.getHullSpec().getHullSize();
        int max = abilitySelected.getMaxLevel(hullSize);
        int currentLevel = buff.getUpgrades().getUpgrade(abilitySelected);

        float possibility = 1f;
        if(!UPGRADE_ALWAYS_SUCCEED && currentLevel!=0 ) {
            possibility = (float) Math.cos(Math.PI*currentLevel*0.5f/max)*(1f-BASE_FAILURE_MINFACTOR)+BASE_FAILURE_MINFACTOR;//зЎ®е®љж¦‚зЋ‡
        }

        if ((float)Math.random()<possibility) {
            buff.getUpgrades().putUpgrade(abilitySelected);
            Global.getSoundPlayer().playUISound("ui_char_increase_skill_new", 1f, 1f);
            textPanel.addParagraph(TextTip.Congratulation, Color.yellow);
        }else {
            textPanel.addParagraph(TextTip.Failure, Color.red);
        }
        if (!DEBUG_UPGRADES_REMOVE_COST) {
            int[] resourceCosts = getUpgradeCosts(shipSelected, abilitySelected, currentLevel, max);
            playerFleet.getCargo().removeSupplies(resourceCosts[0]);
            playerFleet.getCargo().removeItems(CargoAPI.CargoItemType.RESOURCES, "volatiles", resourceCosts[1]);
            playerFleet.getCargo().removeItems(CargoAPI.CargoItemType.RESOURCES, "organics", resourceCosts[2]);
            playerFleet.getCargo().removeItems(CargoAPI.CargoItemType.RESOURCES, "hand_weapons", resourceCosts[3]);
            playerFleet.getCargo().removeItems(CargoAPI.CargoItemType.RESOURCES, "metals", resourceCosts[4]);
            playerFleet.getCargo().removeItems(CargoAPI.CargoItemType.RESOURCES, "rare_metals", resourceCosts[5]);
            playerFleet.getCargo().removeItems(CargoAPI.CargoItemType.RESOURCES, "heavy_machinery", resourceCosts[6]);
        }
    }

    public FleetMemberAPI getSelectedShip(String shipId) {
        if (ShipList == null) {
            return null;
        }

        for (int i = 0; i < ShipList.size(); i++) {
            FleetMemberAPI fleetMemberAPI = ShipList.get(i);
            if (fleetMemberAPI.getId().equals(shipId)) {
                return fleetMemberAPI;
            }
        }
        return null;
    }
}
