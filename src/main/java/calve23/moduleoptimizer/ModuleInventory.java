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

    public static Module removeLast() {
        Module m = MODULES.removeLast();
        save();
        return m;
    }
    public static void add(Module currentModule) {
        MODULES.add(currentModule);
        save();
    }
    public static int size() {
        return MODULES.size();
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
