package extrasystemreloaded.util;

import com.fs.starfarer.api.combat.MutableStat;
import com.fs.starfarer.api.combat.StatBonus;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import extrasystemreloaded.Es_ModPlugin;
import extrasystemreloaded.hullmods.ExtraSystemHM;

import java.text.DecimalFormat;

public class StatUtils {
    private static final DecimalFormat FLOATING_FORMAT = new DecimalFormat("#");
    private static final DecimalFormat FLOATING_FORMAT_UNROUNDED = new DecimalFormat("#.##");

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

    public static float getDiminishingReturnsTotal(int level, int maxLevel, float quality, float levelFactor, float qualityMult, float hullSizeFactor) {
        FloatHolder finalVal = new FloatHolder(0f);
        FloatHolder levelAsFloat = new FloatHolder(level);
        float qualityFactor = (float) Math.pow(1f + quality * 0.33f, 1 + qualityMult / 5f);

        doFloatCurve(finalVal, 0.65f, levelAsFloat, maxLevel, 0.35f, levelFactor, qualityFactor, hullSizeFactor);
        doFloatCurve(finalVal, 0.3f, levelAsFloat, maxLevel, 0.25f, levelFactor, qualityFactor, hullSizeFactor);
        doFloatCurve(finalVal, 0.1f, levelAsFloat, maxLevel, 0.1f, levelFactor, qualityFactor, hullSizeFactor);
        doFloatCurve(finalVal, 0.05f, levelAsFloat, maxLevel, -1, levelFactor, qualityFactor, hullSizeFactor);

        return finalVal.getValue();
    }

    private static void doFloatCurve(FloatHolder stat, float statScalar, FloatHolder level, float maxLevel, float percentOfLevels, float levelFactor, float qualityFactor, float hullSizeFactor) {
        if(level.getValue() <= 0f) {
            return;
        }

        if(percentOfLevels == -1) {
            stat.add(levelFactor * qualityFactor * level.getValue() * hullSizeFactor * statScalar);
        } else {
            stat.add(levelFactor * qualityFactor * Math.min(level.getValue(), percentOfLevels * maxLevel) * hullSizeFactor * statScalar);
            level.add(-percentOfLevels * maxLevel);
        }
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

    public static void addPercentBonusToTooltipUnrounded(TooltipMakerAPI tooltip, String format, float bonusPercent) {
        tooltip.addPara(format, 0, Misc.getHighlightColor(), formatFloatUnrounded(bonusPercent) + "%");
    }

    public static void addPercentBonusToTooltipUnrounded(TooltipMakerAPI tooltip, String format, float bonusPercent, float originalValue) {
        tooltip.addPara(format, 0, Misc.getHighlightColor(), formatFloatUnrounded(bonusPercent) + "%", formatFloatUnrounded(originalValue * 0.01f * bonusPercent));
    }

    public static String formatFloatUnrounded(float theFloat) {
        return FLOATING_FORMAT_UNROUNDED.format(theFloat);
    }

    public static String formatFloat(float theFloat) {
        return FLOATING_FORMAT.format(theFloat);
    }

    private static class FloatHolder {
        private Float myFloat;
        public FloatHolder(float initialValue) {
            myFloat = initialValue;
        }

        public float getValue() {
            return myFloat;
        }

        public void add(float add) {
            myFloat += add;
        }
    }
}
