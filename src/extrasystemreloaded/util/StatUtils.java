package extrasystemreloaded.util;

import com.fs.starfarer.api.combat.MutableStat;
import com.fs.starfarer.api.combat.StatBonus;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

import java.text.DecimalFormat;

public class StatUtils {
    private static final DecimalFormat FLOATING_FORMAT = new DecimalFormat("#.##");

    private StatUtils() {

    }

    public static void setStatPercentBonus(StatBonus stat, String buffId, float mult) {
        stat.modifyPercent(buffId, mult);
    }

    public static void setStatPercentBonus(MutableStat stat, String buffId, float mult) {
        stat.modifyPercent(buffId, mult);
    }

    public static void setStatPercentBonus(StatBonus stat, String buffId, int level, float quality, float mult, float hullSizeFactor) {
        stat.modifyPercent(buffId, mult * level * quality * hullSizeFactor);
    }

    public static void setStatPercentBonus(MutableStat stat, String buffId, int level, float quality, float mult, float hullSizeFactor) {
        stat.modifyPercent(buffId, mult * level * quality * hullSizeFactor);
    }

    public static float getDiminishingReturnsTotal(int level, int maxLevel, float quality, float scalar, float qualityMult, float hullSizeFactor) {
        return scalar*( (quality * qualityMult) / (quality + 4) ) * level * hullSizeFactor;
    }

    public static void addDoublePercentBonusToTooltip(TooltipMakerAPI tooltip, String format, float bonusPercent, float originalValue1, float originalValue2) {
        tooltip.addPara(format, 0, Misc.getHighlightColor(), formatFloat(bonusPercent) + "%", formatFloat(originalValue1 * 0.01f * bonusPercent), formatFloat(originalValue2 * bonusPercent * 0.01f));
    }

    public static void addPercentBonusToTooltip(TooltipMakerAPI tooltip, String format, float bonusPercent, float originalValue) {
        tooltip.addPara(format, 0, Misc.getHighlightColor(), formatFloat(bonusPercent) + "%", formatFloat(originalValue * 0.01f * bonusPercent));
    }

    public static void addPercentBonusToTooltip(TooltipMakerAPI tooltip, String format, float bonusPercent) {
        tooltip.addPara(format, 0, Misc.getHighlightColor(), formatFloat(bonusPercent) + "%");
    }

    public static String getStatPercentBonusString(String format, float bonusPercent, float originalValue) {
        return String.format(format, formatFloat(bonusPercent) + "%", formatFloat(bonusPercent * 0.01f * originalValue));
    }

    private static String formatFloat(float theFloat) {
        return FLOATING_FORMAT.format(theFloat);
    }

    private static String roundFloat(float theFloat) {
        return Integer.toString(Math.round(theFloat));
    }
}
