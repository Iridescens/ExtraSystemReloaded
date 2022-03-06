package extrasystemreloaded.systems.quality;

import com.fs.starfarer.api.Global;
import extrasystemreloaded.util.Utilities;

import java.awt.*;

public class QualityUtil {
    public static String getQualityName(float arg){
        String text;
        if (Utilities.isInside(arg, 0.5f, 0.65f)) {
            text = Global.getSettings().getString("QualityName", "inferior");
        }else if (Utilities.isInside(arg, 0.65f, 0.8f)) {
            text = Global.getSettings().getString("QualityName", "rough");
        }else if (Utilities.isInside(arg, 0.8f, 0.95f)) {
            text = Global.getSettings().getString("QualityName", "crude");
        }else if (Utilities.isInside(arg, 0.95f, 1.33f)) {
            text = Global.getSettings().getString("QualityName", "normal");
        }else if (Utilities.isInside(arg, 1.33f, 1.66f)) {
            text = Global.getSettings().getString("QualityName", "good");
        }else if (Utilities.isInside(arg, 1.66f, 2.1f)) {
            text = Global.getSettings().getString("QualityName", "superior");
        }else if (Utilities.isInside(arg, 2.1f, 2.5f)) {
            text = Global.getSettings().getString("QualityName", "perfect");
        }else if (arg < 3f) {
            text = Global.getSettings().getString("QualityName", "s_perfect");
        } else
            text = Global.getSettings().getString("QualityName", "domain");

        return text;
    }

    public static Color getQualityColor(float arg){
        Color color;
        if (Utilities.isInside(arg, 0.5f, 0.65f)) {
            color = Color.gray.darker();
        }else if (Utilities.isInside(arg, 0.65f, 0.8f)) {
            color = Color.gray;
        }else if (Utilities.isInside(arg, 0.8f, 0.95f)) {
            color = Color.lightGray;
        }else if (Utilities.isInside(arg, 0.95f, 1.1f)) {
            color = Color.white;
        }else if (Utilities.isInside(arg, 1.1f, 1.25f)) {
            color = Color.green;
        }else if (Utilities.isInside(arg, 1.25f, 1.4f)) {
            color = new Color(0,155,255);
        }else if (Utilities.isInside(arg, 1.4f, 1.5f)) {
            color = Color.orange;
        }else {
            color = Color.CYAN;
        }
        return color;
    }
}
