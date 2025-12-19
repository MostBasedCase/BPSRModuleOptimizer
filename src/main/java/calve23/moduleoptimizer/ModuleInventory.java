package calve23.moduleoptimizer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class ModuleInventory {

    public static ArrayList<Module> MODULES = new ArrayList<>();

    private static final Gson GSON =
            new GsonBuilder().setPrettyPrinting().create();
    private static final File FILE = new File("modules.json");

    private ModuleInventory() {}

    public static void remove(Module m) {
        MODULES.remove(m);
        save();
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

    public static void add(Module currentModule) {
        MODULES.add(currentModule);
        save();
    }
    public static int size() {
        return MODULES.size();
    }
    public static Module get(int index) {
        return MODULES.get(index);
    }
    public static void remove(int index) {
        MODULES.remove(index);
        save();
    }
    public static void save() {
        try (FileWriter fw = new FileWriter(FILE)) {
            GSON.toJson(MODULES, fw);
        } catch (IOException e) {
            System.out.println("Failed to save");
        }
    }
    public static void load() {
        try (FileReader fr = new FileReader(FILE)) {
            Type listType = new TypeToken<ArrayList<Module>>() {}.getType();
            MODULES = GSON.fromJson(fr, listType);
            if (MODULES == null) {
                System.out.println("No modules found to load");
            } else {
                System.out.println("Loaded " + MODULES.size() + " module(s)");
            }
        } catch (IOException e) {
            System.out.println("Failed to load");
        }

    }
}
