package extrasystemreloaded.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.loading.VariantSource;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import extrasystemreloaded.util.ExtraSystems;
import extrasystemreloaded.util.FleetMemberUtils;
import extrasystemreloaded.util.Utilities;
import extrasystemreloaded.augments.Augment;
import extrasystemreloaded.augments.AugmentsHandler;
import extrasystemreloaded.upgrades.Upgrade;
import extrasystemreloaded.upgrades.UpgradesHandler;
import org.apache.log4j.Logger;

import java.awt.*;
import java.util.Iterator;
import java.util.List;

import static extrasystemreloaded.util.StatUtils.formatFloat;

public class ExtraSystemHM extends BaseHullMod {
    public static final Logger log = Logger.getLogger(ExtraSystemHM.class);
    private static Color color = new Color(94, 206, 226);
    private static Color tooltipColor = new Color(220, 220, 220, 255);
    private ExtraSystems extraSystems = null;

    public static void addToFleetMember(FleetMemberAPI fm) {
        if (fm.getVariant() == null) {
            return;
        }

        ExtraSystems levels = ExtraSystems.getForFleetMember(fm);
        if (levels.shouldApplyHullmod()) {
            ShipVariantAPI shipVariant = fm.getVariant();

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
                ShipVariantAPI moduleVariant = shipVariant.getModuleVariant(moduleIterator.next());

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
        return true;
    }

    @Override
    public Color getNameColor() {
        return color;
    }

    public ExtraSystems getExtraSystems(ShipAPI ship) {
        FleetMemberAPI fm = ship.getFleetMember();
        if (fm == null) return null;

        return getExtraSystems(fm);
    }

    public ExtraSystems getExtraSystems(MutableShipStatsAPI stats) {
        FleetMemberAPI fm = FleetMemberUtils.findMemberForStats(stats);
        if(fm == null) return null;

        return getExtraSystems(fm);
    }

    public ExtraSystems getExtraSystems(FleetMemberAPI fm) {
        return ExtraSystems.getForFleetMember(fm);

        /*
        if (fm.getBuffManager().getBuff(Es_ShipLevelFleetData.Es_LEVEL_FUNCTION_ID) == null) {
            if (Es_ModPlugin.hasData(fm.getId())) {
                Es_ModPlugin.applyBuff(fm);
            }
        }

        Es_ShipLevelFleetData buff = (Es_ShipLevelFleetData) fm.getBuffManager().getBuff(Es_ShipLevelFleetData.Es_LEVEL_FUNCTION_ID);
        if(buff == null) return null;

        return buff.getExtraSystems();*/
    }

    @Override
    public void advanceInCombat(ShipAPI ship, float amount) {
        ExtraSystems extraSystems = this.getExtraSystems(ship);

        ShipAPI.HullSize hullSize = ship.getHullSize();
        float quality = extraSystems.getQuality();
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
        ExtraSystemHM.log.info("[ExtraSystemsHM] applyEffectsBeforeShipCreation");
        ExtraSystemHM.log.info(String.format("[ExtraSystemsHM] stats getEntity %s getFleetMember %s", stats.getEntity(), stats.getFleetMember()));

        FleetMemberAPI fm = FleetMemberUtils.findMemberForStats(stats);
        if(fm == null) {
            ExtraSystemHM.log.info("[ExtraSystemsHM] could not find a fleet member for this stats object!");
            return;
        } else {
            ExtraSystemHM.log.info(String.format("[ExtraSystemsHM] found fleet member %s", fm));
        }
        ExtraSystems extraSystems = this.getExtraSystems(fm);

        if (extraSystems == null) {
            fm.getVariant().removePermaMod("es_shiplevelHM");
            return;
        }

        float quality = extraSystems.getQuality();

        for(Augment augment : AugmentsHandler.AUGMENT_LIST) {
            if(!extraSystems.hasAugment(augment)) continue;

            ExtraSystemHM.log.info(String.format("[ExtraSystemsHM] FleetMember has augment %s installed", augment.getName()));
            augment.applyUpgradeToStats(fm, stats, quality, id);
        }

        for(Upgrade upgrade : UpgradesHandler.UPGRADES_LIST) {
            int level = extraSystems.getUpgrade(upgrade);
            if(level <= 0) continue;

            ExtraSystemHM.log.info(String.format("[ExtraSystemsHM] FleetMember has upgrade %s at level %s installed", upgrade.getName(), level));
            upgrade.applyUpgradeToStats(fm, stats, extraSystems.getHullSizeFactor(hullSize), extraSystems.getUpgrade(upgrade), quality);
        }
    }

    @Override
    public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
        FleetMemberAPI fm = ship.getFleetMember();
        if(fm == null) return;
        ExtraSystems extraSystems = this.getExtraSystems(fm);
        if(extraSystems == null) return;

        float quality = extraSystems.getQuality();

        for(Augment augment : AugmentsHandler.AUGMENT_LIST) {
            if(!extraSystems.hasAugment(augment)) continue;
            augment.applyAfterShipCreation(fm, ship, quality, id);
        }
    }

    @Override
    public String getDescriptionParam(int index, ShipAPI.HullSize hullSize, ShipAPI ship) {
        FleetMemberAPI fm = ship.getFleetMember();
        if(fm == null) return "SHIP NOT FOUND";
        return fm.getShipName();
    }

    @Override
    public void addPostDescriptionSection(TooltipMakerAPI tooltip, ShipAPI.HullSize hullSize, ShipAPI ship, float width, boolean isForModSpec) {
        FleetMemberAPI fm = ship.getFleetMember();
        if(fm == null) return;
        ExtraSystems extraSystems = this.getExtraSystems(fm);
        if(extraSystems == null) return;

        float quality = extraSystems.getQuality();
        String qname = Utilities.getQualityName(quality);

        tooltip.addPara("The ship is of %s %s quality:", 0, Utilities.getQualityColor(quality), formatFloat(quality * 100) + "%", qname);

        for(Augment augment : AugmentsHandler.AUGMENT_LIST) {
            augment.modifyToolTip(tooltip, fm, extraSystems);
            tooltip.setParaFontDefault();
            tooltip.setParaFontColor(tooltipColor);
        }

        for(Upgrade upgrade : UpgradesHandler.UPGRADES_LIST) {
            upgrade.modifyToolTip(tooltip, fm, extraSystems);
            tooltip.setParaFontDefault();
            tooltip.setParaFontColor(tooltipColor);
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