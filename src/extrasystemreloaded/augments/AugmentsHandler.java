package extrasystemreloaded.augments;

import com.fs.starfarer.api.Global;
import extrasystemreloaded.augments.impl.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AugmentsHandler {
    public static final Map<String, Augment> AUGMENTS = new HashMap<>();
    public static final List<Augment> AUGMENT_LIST = new ArrayList<>();

    public static void populateAugments() {

        if(Global.getSettings().getModManager().isModEnabled("dronelib")) {
            AugmentsHandler.addAugment(new SpooledFeedersDroneLib());
        } else {
            AugmentsHandler.addAugment(new SpooledFeeders());
        }

        AugmentsHandler.addAugment(new PlasmaFluxCatalyst());
        AugmentsHandler.addAugment(new DriveFluxVent());
        AugmentsHandler.addAugment(new HangarForge());
        AugmentsHandler.addAugment(new EqualizerCore());
        AugmentsHandler.addAugment(new PhasefieldEngine());
    }

    public static void addAugment(Augment augment) {
        AUGMENTS.put(augment.getName(), augment);
        AUGMENT_LIST.add(augment);
    }
}
