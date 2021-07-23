package extrasystemreloaded.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import extrasystemreloaded.Es_ModPlugin;
import extrasystemreloaded.campaign.Es_ShipLevelFleetData;
import extrasystemreloaded.util.ExtraSystems;
import extrasystemreloaded.util.FleetMemberUtils;
import extrasystemreloaded.util.Utilities;
import extrasystemreloaded.util.modules.Module;
import extrasystemreloaded.util.modules.Modules;
import extrasystemreloaded.util.upgrades.Upgrade;
import extrasystemreloaded.util.upgrades.Upgrades;
import org.apache.log4j.Logger;

import java.awt.*;

public class ExtraSystemHM extends BaseHullMod {
    public static final Logger log = Logger.getLogger(ExtraSystemHM.class);
    private static Color color = new Color(94, 206, 226);
    private static Color tooltipColor = new Color(220, 220, 220, 255);

    @Override
    public boolean affectsOPCosts() {
        return true;
    }

    @Override
    public Color getNameColor() {
        return color;
    }

    @Override
    public void advanceInCombat(ShipAPI ship, float amount) {
        FleetMemberAPI fm = ship.getFleetMember();
        if (fm == null) return;
        if (fm.getBuffManager().getBuff(Es_ShipLevelFleetData.Es_LEVEL_FUNCTION_ID) == null) return;

        Es_ShipLevelFleetData buff = (Es_ShipLevelFleetData) fm.getBuffManager().getBuff(Es_ShipLevelFleetData.Es_LEVEL_FUNCTION_ID);
        ExtraSystems extraSystems = buff.getExtraSystems();

        ShipAPI.HullSize hullSize = ship.getHullSize();
        float quality = extraSystems.getQuality(fm);
        for(Upgrade upgrade : Upgrades.UPGRADES_LIST) {
            int level = extraSystems.getUpgrade(upgrade);
            if(level <= 0) continue;
            upgrade.advanceInCombat(ship, amount, level, quality, extraSystems.getHullSizeFactor(hullSize));
        }

        for(Module module : Modules.MODULE_LIST) {
            if(!extraSystems.hasModule(module)) continue;
            module.advanceInCombat(ship, amount, quality);
        }
    }

    @Override
    public void applyEffectsBeforeShipCreation(ShipAPI.HullSize hullSize, MutableShipStatsAPI stats, String id) {
        FleetMemberAPI fm = FleetMemberUtils.findMemberForStats(stats);
        if(fm == null) return;

        Es_ShipLevelFleetData buff = (Es_ShipLevelFleetData) fm.getBuffManager().getBuff(Es_ShipLevelFleetData.Es_LEVEL_FUNCTION_ID);

        if (buff == null) {
            if(Es_ModPlugin.hasData(fm.getId())) {
                Es_ModPlugin.applyBuff(fm);
                buff = (Es_ShipLevelFleetData) fm.getBuffManager().getBuff(Es_ShipLevelFleetData.Es_LEVEL_FUNCTION_ID);
            } else {
                fm.getVariant().removePermaMod("es_shiplevelHM");
                return;
            }
        }

        ExtraSystems extraSystems = buff.getExtraSystems();

        float quality = extraSystems.getQuality(fm);

        for(Module module : Modules.MODULE_LIST) {
            if(!extraSystems.hasModule(module)) continue;
            module.applyUpgradeToStats(fm, stats, quality, id);
        }

        for(Upgrade upgrade : Upgrades.UPGRADES_LIST) {
            int level = extraSystems.getUpgrade(upgrade);
            if(level <= 0) continue;
            upgrade.applyUpgradeToStats(fm, stats, extraSystems.getHullSizeFactor(hullSize), extraSystems.getUpgrade(upgrade), quality);
        }
    }

    @Override
    public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
        FleetMemberAPI fm = ship.getFleetMember();
        if(fm == null) return;
        if (fm.getBuffManager().getBuff(Es_ShipLevelFleetData.Es_LEVEL_FUNCTION_ID) == null) return;

        Es_ShipLevelFleetData buff = (Es_ShipLevelFleetData) fm.getBuffManager().getBuff(Es_ShipLevelFleetData.Es_LEVEL_FUNCTION_ID);

        ExtraSystems extraSystems = buff.getExtraSystems();

        float quality = extraSystems.getQuality(fm);

        for(Module module : Modules.MODULE_LIST) {
            if(!extraSystems.hasModule(module)) continue;
            module.applyAfterShipCreation(fm, ship, quality, id);
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
        if (fm == null) { return; }
        Es_ShipLevelFleetData buff = (Es_ShipLevelFleetData) fm.getBuffManager().getBuff(Es_ShipLevelFleetData.Es_LEVEL_FUNCTION_ID);
        if (buff == null) { return; }
        float quality = buff.getExtraSystems().getQuality(fm);
        String qname = Utilities.getQualityName(quality);

        tooltip.addPara("The ship is of %s quality, which affected base upgrade values by %s multiplier (following numbers are final calculations):", 0, Utilities.getQualityColor(quality),qname,""+String.format("%.3f",quality));

        for(Module module : Modules.MODULE_LIST) {
            module.modifyToolTip(tooltip, fm, buff);
            tooltip.setParaFontDefault();
            tooltip.setParaFontColor(tooltipColor);
        }

        for(Upgrade upgrade : Upgrades.UPGRADES_LIST) {
            upgrade.modifyToolTip(tooltip, fm, buff);
            tooltip.setParaFontDefault();
            tooltip.setParaFontColor(tooltipColor);
        }
    }


}