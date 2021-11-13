package extrasystemreloaded.util;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.MutableStat;
import com.fs.starfarer.api.combat.StatBonus;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import extrasystemreloaded.ESModSettings;
import extrasystemreloaded.hullmods.ExtraSystemHM;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class StatUtils {
    private static final String STAT_CURVE_SCALAR = "scalar";
    private static final String STAT_CURVE_PERCENT = "percentOfLevels";

    private static final DecimalFormat FLOATING_FORMAT = new DecimalFormat("#");
    private static final DecimalFormat FLOATING_FORMAT_UNROUNDED = new DecimalFormat("#.##");
    private static final List<StatCurve> STAT_CURVES = new ArrayList<>();

    private StatUtils() {

    }

    public static void loadStatCurves() {
        STAT_CURVES.clear();
        try {
            JSONArray curveSettings = ESModSettings.getArray(ESModSettings.SCALING_CURVES);

            for(int i = 0; i < curveSettings.length(); i++) {
                JSONObject curve = curveSettings.getJSONObject(i);

                if(curve.isNull(STAT_CURVE_SCALAR) || curve.isNull(STAT_CURVE_PERCENT)) {
                    throw new RuntimeException("A curve is incorrect in ExtraSystemReloaded settings.json!");
                }

                STAT_CURVES.add(new StatCurve((float) curve.getDouble(STAT_CURVE_SCALAR), (float) curve.getDouble(STAT_CURVE_PERCENT)));
            }

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
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
        float qualityFactor = (float) Math.pow(1f + quality * 0.33f, 1f + qualityMult / 5f);

        for(StatCurve curve : STAT_CURVES) {
            curve.doFloatCurve(finalVal, levelAsFloat, maxLevel, levelFactor, qualityFactor, hullSizeFactor);
        }

        return finalVal.getValue();
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

    private static class StatCurve {
        private final float statScalar;
        private final float percentOfLevels;

        protected StatCurve(float statScalar, float percentOfLevels) {
            this.statScalar = statScalar;
            this.percentOfLevels = percentOfLevels;
        }

        public void doFloatCurve(FloatHolder stat, FloatHolder level, float maxLevel, float levelFactor, float qualityFactor, float hullSizeFactor) {
            if(level.getValue() <= 0f) {
                return;
            }

            //todo - determine if this is good. stat curves in settings have been adjusted for this.
            qualityFactor = 1;

            if(percentOfLevels == -1) {
                stat.add(levelFactor * qualityFactor * level.getValue() * hullSizeFactor * statScalar);
            } else {
                stat.add(levelFactor * qualityFactor * Math.min(level.getValue(), percentOfLevels * maxLevel) * hullSizeFactor * statScalar);
                level.add(-percentOfLevels * maxLevel);
            }
        }
    }
}
