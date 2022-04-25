package extrasystemreloaded.campaign.listeners;

import com.fs.starfarer.api.campaign.CargoAPI;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.listeners.ShowLootListener;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.impl.campaign.ids.MemFlags;
import com.fs.starfarer.api.impl.campaign.procgen.SalvageEntityGenDataSpec;
import com.fs.starfarer.api.impl.campaign.procgen.themes.SalvageEntityGeneratorOld;
import com.fs.starfarer.api.impl.campaign.rulecmd.salvage.SalvageEntity;
import com.fs.starfarer.api.util.Misc;
import lombok.extern.log4j.Log4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Log4j
public class SalvageListener implements ShowLootListener {
    @Override
    public void reportAboutToShowLootToPlayer(CargoAPI loot, InteractionDialogAPI dialog) {
        SectorEntityToken entity = dialog.getInteractionTarget();

        MemoryAPI memory = entity.getMemoryWithoutUpdate();
        long randomSeed = memory.getLong(MemFlags.SALVAGE_SEED);
        Random random = Misc.getRandom(randomSeed, 100);

        int addItems = 0;

        String specId = entity.getCustomEntityType();
        if (specId == null || entity.getMemoryWithoutUpdate().contains(MemFlags.SALVAGE_SPEC_ID_OVERRIDE)) {
            specId = entity.getMemoryWithoutUpdate().getString(MemFlags.SALVAGE_SPEC_ID_OVERRIDE);
        }

        if(specId == null
            || !SalvageEntityGeneratorOld.hasSalvageSpec(specId)) return;

        SalvageEntityGenDataSpec spec = SalvageEntityGeneratorOld.getSalvageSpec(specId);

        List<SalvageEntityGenDataSpec.DropData> dropData = new ArrayList<>();

        if(spec != null && spec.getDropRandom() != null) {
            dropData.addAll(spec.getDropRandom());
        }

        if(entity.getDropRandom() != null) {
            dropData.addAll(entity.getDropRandom());
        }

        for (SalvageEntityGenDataSpec.DropData data : dropData) {
            if(data.group == null) continue;
            if (data.group.equals("rare_tech")) {
                addItems += random.nextInt((data.chances + 1) * 2);
            } else if (data.group.equals("rare_tech_low")) {
                addItems += random.nextInt((data.chances + 1));
            }
        }

        dropData = new ArrayList<>();
        
        if(spec != null && spec.getDropValue() != null) {
            dropData.addAll(spec.getDropValue());
        }
        
        if(entity.getDropValue() != null) {
            dropData.addAll(entity.getDropValue());
        }

        for (SalvageEntityGenDataSpec.DropData data : dropData) {
            if(data.group == null) continue;
            if (data.group.equals("rare_tech")) {
                addItems += ((data.value + 1) * data.valueMult * 3);
            } else if (data.group.equals("rare_tech_low")) {
                addItems += ((data.value + 1) * data.valueMult * 1.5);
            }
        }

        if(specId.contains("ruins")) {
            addItems = Math.max(addItems - 2, addItems / 2);
        }

        if(addItems > 0) {
            SalvageEntityGenDataSpec.DropData data = new SalvageEntityGenDataSpec.DropData();
            data.group = "esr_augment";
            data.chances = addItems;

            List<SalvageEntityGenDataSpec.DropData> newDropData = new ArrayList<>();
            newDropData.add(data);

            CargoAPI salvage = SalvageEntity.generateSalvage(random,
                    1f, 1f, 1f, 1f, null, newDropData);
            loot.addAll(salvage);
        }
    }
}
