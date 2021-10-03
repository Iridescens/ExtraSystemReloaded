package extrasystemreloaded.augments;

import com.fs.starfarer.api.Global;
import extrasystemreloaded.augments.impl.*;
import extrasystemreloaded.upgrades.Upgrade;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AugmentsHandler {
    public static final Logger log = Logger.getLogger(AugmentsHandler.class);
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
        AugmentsHandler.addAugment(new HangarForgeMissiles());

        loadConfigs();
    }

    public static void loadConfigs() {
        try {
            JSONObject settings = Global.getSettings().loadJSON("data/config/augments.json", "extra_system_reloaded");

            for (Augment augment : AUGMENT_LIST) {
                if (!settings.has(augment.getKey())) {
                    continue;
                }

                augment.loadConfig(settings.getJSONObject(augment.getKey()));
            }
        } catch (JSONException | IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static void addAugment(Augment augment) {
        if(AUGMENTS.containsKey(augment.getKey())) return;

        AUGMENTS.put(augment.getKey(), augment);
        AUGMENT_LIST.add(augment);

        log.info(String.format("initialized augment [%s]", augment.getName()));
    }
}
