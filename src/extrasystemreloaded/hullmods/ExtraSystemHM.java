package extrasystemreloaded.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import extrasystemreloaded.campaign.Es_ShipLevelFleetData;
import extrasystemreloaded.util.ESUpgrades;
import extrasystemreloaded.util.Utilities;
import extrasystemreloaded.util.upgrades.Upgrade;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExtraSystemHM extends BaseHullMod {
    @Override
    public void advanceInCombat(ShipAPI ship, float amount) {
        FleetMemberAPI fm = ship.getFleetMember();
        if ( fm == null || fm.getBuffManager() == null ) { return;}
        if ( fm.getBuffManager().getBuff(Es_ShipLevelFleetData.Es_LEVEL_FUNCTION_ID) == null ) {
            return;
        }

        Es_ShipLevelFleetData buff = (Es_ShipLevelFleetData) fm.getBuffManager().getBuff(Es_ShipLevelFleetData.Es_LEVEL_FUNCTION_ID);
        ESUpgrades upgrades = buff.getUpgrades();
        for(Upgrade upgrade : ESUpgrades.UPGRADES_LIST) {
            int level = upgrades.getUpgrade(upgrade);
            if(level <= 0) continue;
            upgrade.advanceInCombat(ship, amount, level, buff.getQualityFactor());
        }
    }

    @Override
    public void applyEffectsBeforeShipCreation(ShipAPI.HullSize hullSize, MutableShipStatsAPI stats, String id) {
        FleetMemberAPI fm = stats.getFleetMember();
        if ( fm == null || fm.getBuffManager() == null ) { return;}
        if ( fm.getBuffManager().getBuff(Es_ShipLevelFleetData.Es_LEVEL_FUNCTION_ID) == null ) {
            return;
        }
        Es_ShipLevelFleetData buff = (Es_ShipLevelFleetData) fm.getBuffManager().getBuff(Es_ShipLevelFleetData.Es_LEVEL_FUNCTION_ID);
        ESUpgrades upgrades = buff.getUpgrades();

        for(Upgrade upgrade : ESUpgrades.UPGRADES_LIST) {
            int level = upgrades.getUpgrade(upgrade);
            if(level <= 0) continue;
            upgrade.applyUpgradeToStats(fm, stats, buff.getHullSizeFactor(), upgrades.getUpgrade(upgrade), buff.getQualityFactor());
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
        String qname = Utilities.getQualityName(buff.getQualityFactor());

        tooltip.addPara("The ship is of %s quality, which affected base upgrade values by %s multiplier (following numbers are final calculations):", 0, Utilities.getQualityColor(buff.getQualityFactor()),qname,""+String.format("%.3f",buff.getQualityFactor()));
        for(Upgrade upgrade : ESUpgrades.UPGRADES_LIST) {
            upgrade.modifyToolTip(tooltip, fm, buff);
        }
    }
}