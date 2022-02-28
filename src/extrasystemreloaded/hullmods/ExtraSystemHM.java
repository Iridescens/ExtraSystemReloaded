package extrasystemreloaded.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.loading.VariantSource;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import extrasystemreloaded.util.ExtraSystems;
import extrasystemreloaded.util.FleetMemberUtils;
import extrasystemreloaded.util.Utilities;
import extrasystemreloaded.augments.Augment;
import extrasystemreloaded.augments.AugmentsHandler;
import extrasystemreloaded.upgrades.Upgrade;
import extrasystemreloaded.upgrades.UpgradesHandler;
import org.apache.log4j.Logger;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static extrasystemreloaded.util.StatUtils.formatFloat;

public class ExtraSystemHM extends BaseHullMod {
    public static final Logger log = Logger.getLogger(ExtraSystemHM.class);

    private static Color color = new Color(94, 206, 226);
    private static Color tooltipColor = new Color(220, 220, 220, 255);
    public static Color infoColor = new Color(236, 196, 0);
    private ExtraSystems extraSystems = null;

    public static void addToFleetMember(FleetMemberAPI fm) {
        if (fm.getVariant() == null) {
            return;
        }

        ExtraSystems levels = ExtraSystems.getForFleetMember(fm);
        ShipVariantAPI shipVariant = fm.getVariant();

        if(shipVariant.hasHullMod("es_shiplevelHM")) {
            shipVariant.removePermaMod("es_shiplevelHM");
        }

        if (levels.shouldApplyHullmod()) {

            if(shipVariant.isStockVariant() || shipVariant.getSource() != VariantSource.REFIT) {
                shipVariant = shipVariant.clone();
                shipVariant.setOriginalVariant(null);
                shipVariant.setSource(VariantSource.REFIT);
                fm.setVariant(shipVariant, false, false);
            }

            shipVariant.addPermaMod("es_shiplevelHM");

            List<String> slots = shipVariant.getModuleSlots();

            Iterator<String> moduleIterator = shipVariant.getStationModules().keySet().iterator();
            while(moduleIterator.hasNext()) {
                String moduleVariantId = moduleIterator.next();
                ShipVariantAPI moduleVariant = shipVariant.getModuleVariant(moduleVariantId);

                if (moduleVariant != null) {
                    if(moduleVariant.isStockVariant() || shipVariant.getSource() != VariantSource.REFIT) {
                        moduleVariant = moduleVariant.clone();
                        moduleVariant.setOriginalVariant(null);
                        moduleVariant.setSource(VariantSource.REFIT);
                        shipVariant.setModuleVariant(moduleVariantId, moduleVariant);
                    }

                    moduleVariant.addPermaMod("es_shiplevelHM");
                }
            }

            fm.updateStats();
        }
    }

    public static void removeFromFleetMember(FleetMemberAPI fm) {

    }

    @Override
    public boolean affectsOPCosts() {
        return false;
    }

    @Override
    public Color getNameColor() {
        return color;
    }

    public ExtraSystems getExtraSystems(MutableShipStatsAPI stats) {
        FleetMemberAPI fm = FleetMemberUtils.findMemberForStats(stats);
        if(fm == null) return null;

        return getExtraSystems(fm);
    }

    public ExtraSystems getExtraSystems(FleetMemberAPI fm) {
        return ExtraSystems.getForFleetMember(fm);
    }

    @Override
    public void advanceInCombat(ShipAPI ship, float amount) {
        FleetMemberAPI fm = FleetMemberUtils.findMemberFromShip(ship);
        if(fm == null) return;

        ExtraSystems extraSystems = this.getExtraSystems(fm);
        if(extraSystems == null) return;

        ShipAPI.HullSize hullSize = ship.getHullSize();
        float quality = extraSystems.getQuality(fm);
        for(Upgrade upgrade : UpgradesHandler.UPGRADES_LIST) {
            int level = extraSystems.getUpgrade(upgrade);
            if(level <= 0) continue;
            upgrade.advanceInCombat(ship, amount, level, quality, extraSystems.getHullSizeFactor(hullSize));
        }

        for(Augment augment : AugmentsHandler.AUGMENT_LIST) {
            if(!extraSystems.hasAugment(augment)) continue;
            augment.advanceInCombat(ship, amount, quality);
        }
    }

    @Override
    public void applyEffectsBeforeShipCreation(ShipAPI.HullSize hullSize, MutableShipStatsAPI stats, String id) {
        FleetMemberAPI fm = FleetMemberUtils.findMemberForStats(stats);
        if(fm == null) {
            return;
        }

        try {
            if (!stats.getVariant().getStationModules().isEmpty()) {
                FleetMemberUtils.moduleMap.clear();

                for (Map.Entry<String, String> e : stats.getVariant().getStationModules().entrySet()) {
                    ShipVariantAPI module = stats.getVariant().getModuleVariant(e.getKey());

                    FleetMemberUtils.moduleMap.put(module.getHullVariantId(), fm);
                }
            }
        } catch (Exception e) {
            log.info("Failed to get modules", e);
        }

        ExtraSystems extraSystems = this.getExtraSystems(fm);

        if (extraSystems == null) {
            fm.getVariant().removePermaMod("es_shiplevelHM");
            return;
        }

        float quality = extraSystems.getQuality(fm);

        for(Augment augment : AugmentsHandler.AUGMENT_LIST) {
            if(!extraSystems.hasAugment(augment)) continue;

            augment.applyAugmentToStats(fm, stats, quality, id);
        }

        for(Upgrade upgrade : UpgradesHandler.UPGRADES_LIST) {
            int level = extraSystems.getUpgrade(upgrade);
            if(level <= 0) continue;

            upgrade.applyUpgradeToStats(fm, stats, extraSystems.getHullSizeFactor(hullSize), level, quality);
        }
    }

    @Override
    public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
        FleetMemberAPI fm = FleetMemberUtils.findMemberFromShip(ship);
        if(fm == null) return;

        ExtraSystems extraSystems = this.getExtraSystems(fm);
        if(extraSystems == null) return;

        float quality = extraSystems.getQuality(fm);

        for(Augment augment : AugmentsHandler.AUGMENT_LIST) {
            if(!extraSystems.hasAugment(augment)) continue;
            augment.applyAugmentToShip(fm, ship, quality, id);
        }
    }

    @Override
    public String getDescriptionParam(int index, ShipAPI.HullSize hullSize, ShipAPI ship) {
        FleetMemberAPI fm = FleetMemberUtils.findMemberFromShip(ship);
        if(fm == null) return "SHIP NOT FOUND";
        if(fm.getShipName() == null) {
            return "SHIP MODULE";
        }
        return fm.getShipName();
    }

    @Override
    public void addPostDescriptionSection(TooltipMakerAPI tooltip, ShipAPI.HullSize hullSize, ShipAPI ship, float width, boolean isForModSpec) {
        FleetMemberAPI fm = FleetMemberUtils.findMemberFromShip(ship);
        if(fm == null) return;
        if(fm.getShipName() == null) {
            tooltip.addPara("Ship modules do not support tooltips.", 0);
            return;
        }

        ExtraSystems extraSystems = this.getExtraSystems(fm);
        if (extraSystems == null) return;


        float quality = extraSystems.getQuality(fm);
        String qname = Utilities.getQualityName(quality);

        tooltip.addPara("The ship is of %s %s quality.", 0, Utilities.getQualityColor(quality), formatFloat(quality * 100) + "%", qname);

        boolean expand = Keyboard.isKeyDown(Keyboard.getKeyIndex("F1"));

        boolean addedAugmentSection = false;
        try {
            for (Augment augment : AugmentsHandler.AUGMENT_LIST) {
                if (!extraSystems.hasAugment(augment.getKey())) continue;

                if (!addedAugmentSection) {
                    addedAugmentSection = true;
                    tooltip.addSectionHeading("Augments", Alignment.MID, 6);
                }
                augment.modifyToolTip(tooltip, fm, extraSystems, expand);
                tooltip.setParaFontDefault();
                tooltip.setParaFontColor(tooltipColor);
            }
        } catch (Throwable th) {
            log.info("Caught augment description exception", th);
        }

        boolean addedUpgradeSection = false;
        try {
            for (Upgrade upgrade : UpgradesHandler.UPGRADES_LIST) {
                if (!addedUpgradeSection) {
                    addedUpgradeSection = true;
                    tooltip.addSectionHeading("Upgrades", Alignment.MID, 6);
                }
                upgrade.modifyToolTip(tooltip, fm, extraSystems, expand);
                tooltip.setParaFontDefault();
                tooltip.setParaFontColor(tooltipColor);
            }
        } catch (Throwable th) {
            log.info("Caught upgrade description exception", th);
        }

        if (expand) {
            tooltip.addPara("Press F1 to show less information.", 10, infoColor, "F1");
        } else {
            tooltip.addPara("Hold F1 to show more information.", 10, infoColor, "F1");
        }
    }

    public static void removeESHullModsFromVariant(ShipVariantAPI v) {
        v.removePermaMod("es_shiplevelHM");

        List<String> slots = v.getModuleSlots();
        if (slots == null || slots.isEmpty()) return;

        for(int i = 0; i < slots.size(); ++i) {
            ShipVariantAPI module = v.getModuleVariant(slots.get(i));
            if (module != null) {
                removeESHullModsFromVariant(module);
            }
        }
    }
}