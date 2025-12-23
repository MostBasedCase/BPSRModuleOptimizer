package calve23.moduleoptimizer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;


import java.awt.Rectangle;
import java.io.*;
import java.lang.reflect.Type;
import java.util.EnumMap;
import java.util.List;

public class RegionPersistence {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File FILE = new File("regions.json");
    public static EnumMap<ModuleType, List<Rectangle>> REGION_MAP = new EnumMap<>(ModuleType.class);

    private RegionPersistence () {}

    public static void save (EnumMap<ModuleType, List<Rectangle>> map) {
        try (FileWriter write = new FileWriter(FILE)) {
            GSON.toJson(map, write);
            REGION_MAP = map;
        } catch (IOException e) {
            System.err.println("Error saving region: " + e.getMessage());
        }
    }

    public static boolean load () {
        if (!FILE.exists()) return false;

        try (FileReader reader = new FileReader(FILE)) {
            Type listType = new TypeToken<EnumMap<ModuleType, List<Rectangle>>>() {}.getType();
            REGION_MAP = GSON.fromJson(reader, listType);
            if (REGION_MAP == null || REGION_MAP.get(ModuleType.A).isEmpty()) {
                return false;
            } else {
                System.out.println("Regions loaded, use 1, 2, 3, or 4 to specify region type for capturing.");
                System.out.println("1 Excellent Module - Premium.");
                System.out.println("2 Excellent Module.");
                System.out.println("3 Advanced Module.");
                System.out.println("4 Basic Module.");
            }
        } catch (IOException e) {
            System.out.println("Could not load regions.json");
        }
        return true;
    }


}
