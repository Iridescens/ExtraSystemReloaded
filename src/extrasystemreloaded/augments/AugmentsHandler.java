package extrasystemreloaded.augments;

import com.fs.starfarer.api.Global;
import extrasystemreloaded.augments.impl.*;
import extrasystemreloaded.hullmods.ExtraSystemHM;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AugmentsHandler {
    public static final Logger log = Logger.getLogger(AugmentsHandler.class);
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
        if(AUGMENT_LIST.contains(augment)) return;

        AUGMENT_LIST.add(augment);
        log.info(String.format("initialized augment [%s]", augment.getName()));
    }
}
