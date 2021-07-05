package extrasystemreloaded.util.modules;

import extrasystemreloaded.util.modules.impl.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Modules {
    public static final Map<String, Module> CORES = new HashMap<>();
    public static final List<Module> MODULE_LIST = new ArrayList<>();

    static {
        Modules.addUpgrade(new SpooledFeeders());
        Modules.addUpgrade(new PlasmaFluxCatalyst());
        Modules.addUpgrade(new FusionMissileIgnition());
        Modules.addUpgrade(new HangarForge());
        Modules.addUpgrade(new EqualizerCore());
    }

    public static void addUpgrade(Module upgrade) {
        CORES.put(upgrade.getName(), upgrade);
        MODULE_LIST.add(upgrade);
    }
}
