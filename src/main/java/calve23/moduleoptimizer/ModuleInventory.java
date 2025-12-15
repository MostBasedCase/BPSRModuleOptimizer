package calve23.moduleoptimizer;

import java.util.ArrayList;

public class ModuleInventory {

    private static final ArrayList<Module> MODULES = new ArrayList<>();
    private ModuleInventory() {}

    public static void add(Module m) {
        MODULES.add(m);
    }
    public static void remove(Module m) {
        MODULES.remove(m);
    }
    public static ArrayList<Module> getModules() {
        return MODULES;
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
