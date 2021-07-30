package extrasystemreloaded.augments;

import java.util.ArrayList;
import java.util.List;

public class ESAugments {
    private final List<String> augments;

    public ESAugments() {
        this.augments = new ArrayList<>();
    }

    public ESAugments(List<String> augments) {
        if(augments == null) {
            this.augments = new ArrayList<>();
        } else {
            this.augments = augments;
        }
    }

    public boolean hasModule(Augment upgrade) {
        return this.hasModule(upgrade.getKey());
    }
    public boolean hasModule(String key) {
        if(this.augments.contains(key)) {
            return true;
        }
        return false;
    }

    public void putModule(Augment upggrade) {
        this.putModule(upggrade.getKey());
    }

    public void putModule(String key) {
        this.augments.add(key);
    }

    public void removeModule(String key) {
        this.augments.remove(key);
    }

    public void removeModule(Augment augment) {
        this.augments.remove(augment.getKey());
    }

    public boolean hasAnyModule() {
        return !this.augments.isEmpty();
    }
}
