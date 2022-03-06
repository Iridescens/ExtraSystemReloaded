package extrasystemreloaded.systems.augments;

import com.fs.starfarer.api.Global;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.*;

public class AugmentsHandler {
    public static final Logger log = Logger.getLogger(AugmentsHandler.class);
    public static final Map<String, Augment> AUGMENTS = new HashMap<>();
    public static final List<Augment> AUGMENT_LIST = new ArrayList<>();

    public static void populateAugments() {
        try {
            JSONObject settings = Global.getSettings().getMergedJSONForMod("data/config/augments.json", "extra_system_reloaded");

            Iterator augIterator = settings.keys();
            while(augIterator.hasNext()) {
                JSONObject augObj = (JSONObject) settings.getJSONObject((String) augIterator.next());

                Class<?> clzz = Global.getSettings().getScriptClassLoader().loadClass(augObj.getString("augmentClass"));
                Augment augment = (Augment) clzz.newInstance();

                if(augment.shouldLoad()) {
                    augment.loadConfig(settings.getJSONObject(augment.getKey()));
                    AugmentsHandler.addAugment(augment);
                }
            }
        } catch (JSONException | IOException | ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static void loadConfigs() {
        try {
            JSONObject settings = Global.getSettings().getMergedJSONForMod("data/config/augments.json", "extra_system_reloaded");

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
