package extrasystemreloaded.systems.bandwidth;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.util.Pair;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.lazywizard.lazylib.LazyLib;
import org.lazywizard.lazylib.MathUtils;

import java.awt.Color;
import java.util.*;

@RequiredArgsConstructor
public enum Bandwidth {
    TERRIBLE(0f, "terrible", new Color(200,100,100), 50),
    CRUDE(50f, "crude", new Color(200,150,100), 60),
    POOR(75f, "poor", new Color(210,210,120), 70),
    NORMAL(95f, "normal", new Color(200,200,200), 100),
    GOOD(125f, "good", new Color(100,200,110), 65),
    SUPERIOR(150f, "superior", new Color(75,125,255), 20),
    ULTIMATE(200f, "ultimate", new Color(150,100,200), 10),
    PERFECT(250f, "perfect", new Color(255,100,255), 5),
    DOMAIN(300f, "domain", new Color(200,255,255), 1);
    public static final String BANDWIDTH_RESOURCE = "Bandwidth";
    private static Map<Float, Bandwidth> BANDWIDTH_MAP = null;
    private static List<Bandwidth> BANDWIDTH_LIST = Arrays.asList(values());
    @Getter private final float bandwidth;
    @Getter private final String key;
    @Getter private final Color color;
    @Getter private final int weight;

    public float getRandomInRange() {
        if(BANDWIDTH_LIST.indexOf(this) == BANDWIDTH_LIST.size() - 1) {
            return bandwidth;
        }
        float nextBandwidth = BANDWIDTH_LIST.get(BANDWIDTH_LIST.indexOf(this) + 1).getBandwidth();
        return Math.round(MathUtils.getRandomNumberInRange(bandwidth, nextBandwidth));
    }

    public static Bandwidth generate(int seed) {
        int highNumber = 0;
        for(Bandwidth b : values()) {
            highNumber += b.getWeight();
        }

        MathUtils.getRandom().setSeed(Global.getSector().getSeedString().hashCode() + seed);
        int chosen = MathUtils.getRandomNumberInRange(0, highNumber);
        for(Bandwidth b : values()) {
            chosen -= b.getWeight();

            if(chosen <= 0) {
                return b;
            }
        }
        return NORMAL;
    }

    public static Map<Float, Bandwidth> getBandwidthMap() {
        if(BANDWIDTH_MAP == null) {
            BANDWIDTH_MAP = new LinkedHashMap<>();
            for(Bandwidth b : values()) {
                BANDWIDTH_MAP.put(b.getBandwidth(), b);
            }
        }
        return BANDWIDTH_MAP;
    }

    public static String getBandwidthName(float arg) {
        return Global.getSettings().getString("BandwidthName", getBandwidthDef(arg).getKey());
    }

    public static Color getBandwidthColor(float arg){
        return getBandwidthDef(arg).getColor();
    }

    private static Bandwidth getBandwidthDef(float bandwidth) {
        float winningBandwidth = 0f;
        Bandwidth returnedDef = TERRIBLE;

        for(Bandwidth b : values()) {
            float defBandwidth = b.getBandwidth();

            if(defBandwidth >= winningBandwidth && bandwidth >= defBandwidth) {
                returnedDef = b;
                winningBandwidth = defBandwidth;
            }
        }

        return returnedDef;
    }
}
