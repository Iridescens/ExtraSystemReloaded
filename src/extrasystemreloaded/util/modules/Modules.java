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
        Modules.addModule(new SpooledFeeders());
        Modules.addModule(new PlasmaFluxCatalyst());
        Modules.addModule(new FusionMissileIgnition());
        Modules.addModule(new HangarForge());
        Modules.addModule(new EqualizerCore());
    }

    public static void addModule(Module module) {
        CORES.put(module.getName(), module);
        MODULE_LIST.add(module);
    }
}
