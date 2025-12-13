package calve23.moduleoptimizer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ModuleInventory {

    private static final List<Module> MODULES = new ArrayList<Module>();
    private ModuleInventory() {}

    public static void add(Module m) {
        MODULES.add(m);
    }
    public static void remove(Module m) {
        MODULES.remove(m);
    }
    public static List<Module> getModules() {
        return Collections.unmodifiableList(MODULES);
    }
    public static void clear() {
        MODULES.clear();
    }
    public static void removeLast() {
        MODULES.removeLast();
    }
    public static int count() {
        return MODULES.size();
    }
}
