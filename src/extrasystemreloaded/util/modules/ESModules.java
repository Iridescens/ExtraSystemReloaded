package extrasystemreloaded.util.modules;

import java.util.ArrayList;
import java.util.List;

public class ESModules {
    private final List<String> modules;

    public ESModules() {
        this.modules = new ArrayList<>();
    }

    public ESModules(List<String> modules) {
        if(modules == null) {
            this.modules = new ArrayList<>();
        } else {
            this.modules = modules;
        }
    }

    public boolean hasModule(Module upgrade) {
        return this.hasModule(upgrade.getKey());
    }
    public boolean hasModule(String key) {
        if(this.modules.contains(key)) {
            return true;
        }
        return false;
    }

    public void putModule(Module upggrade) {
        this.putModule(upggrade.getKey());
    }

    public void putModule(String key) {
        this.modules.add(key);
    }

    public void removeModule(String key) {
        this.modules.remove(key);
    }

    public void removeModule(Module module) {
        this.modules.remove(module.getKey());
    }

    public boolean hasAnyModule() {
        return !this.modules.isEmpty();
    }
}
