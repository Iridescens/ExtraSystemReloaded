package extrasystemreloaded.systems.augments;

import com.fs.starfarer.api.Global;
import extrasystemreloaded.campaign.rulecmd.Es_ShipAugmentsDialog;
import extrasystemreloaded.campaign.rulecmd.Es_ShipDialog;
import extrasystemreloaded.util.StringUtils;
import lombok.extern.log4j.Log4j;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.*;

@Log4j
public class AugmentsHandler {
    private static int AUGMENT_OPTION_ORDER = 2;
    public static final Map<String, Augment> AUGMENTS = new HashMap<>();
    public static final List<Augment> AUGMENT_LIST = new ArrayList<>();

    public static void initialize() {
        Es_ShipDialog.addShipOption(new Es_ShipAugmentsDialog.AugmentOption(AUGMENT_OPTION_ORDER));
        AugmentsHandler.populateAugments();
    }

    public static void populateAugments() {
        try {
            JSONObject settings = Global.getSettings().getMergedJSONForMod("data/config/augments.json", "extra_system_reloaded");

            Iterator augIterator = settings.keys();
            while(augIterator.hasNext()) {
                String augKey = (String) augIterator.next();

                if(AUGMENTS.containsKey(augKey)) return;

                JSONObject augObj = (JSONObject) settings.getJSONObject(augKey);

                Class<?> clzz = Global.getSettings().getScriptClassLoader().loadClass(augObj.getString("augmentClass"));
                Augment augment = (Augment) clzz.newInstance();

                if(augment.shouldLoad()) {
                    augment.setKey(augKey);
                    augment.setName(StringUtils.getString(augKey, "name"));
                    augment.setDescription(StringUtils.getString(augKey, "description"));
                    augment.setTooltip(StringUtils.getString(augKey, "tooltip"));
                    augment.setConfig(augObj);

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

                augment.loadConfig();
            }
        } catch (JSONException | IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static void addAugment(Augment augment) {
        AUGMENTS.put(augment.getKey(), augment);
        AUGMENT_LIST.add(augment);

        log.info(String.format("initialized augment [%s]", augment.getName()));
    }
}
