package extrasystemreloaded.augments;

import extrasystemreloaded.augments.impl.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AugmentsHandler {
    public static final Map<String, Augment> AUGMENTS = new HashMap<>();
    public static final List<Augment> AUGMENT_LIST = new ArrayList<>();

    static {
        AugmentsHandler.addModule(new SpooledFeeders());
        AugmentsHandler.addModule(new PlasmaFluxCatalyst());
        AugmentsHandler.addModule(new FusionMissileIgnition());
        AugmentsHandler.addModule(new HangarForge());
        AugmentsHandler.addModule(new EqualizerCore());
    }

    public static void addModule(Augment augment) {
        AUGMENTS.put(augment.getName(), augment);
        AUGMENT_LIST.add(augment);
    }
}
