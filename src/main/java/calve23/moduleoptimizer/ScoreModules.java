package calve23.moduleoptimizer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.lang.reflect.Type;
import java.util.stream.Collectors;

public class ScoreModules {
    public static Map<LinkEffectName, Integer> PRIORITY = new HashMap<>();

    private static final Gson GSON =
            new GsonBuilder().setPrettyPrinting().create();

    private static final File FILE = new File("priority_list.json");

    public static void score(ArrayList<Module> m) {

        try (FileReader fr = new FileReader(FILE)) {
            Type type = new TypeToken<Map<LinkEffectName, Integer>>() {}.getType();
            PRIORITY = GSON.fromJson(fr, type);
            System.out.println("Loading priority list");
        } catch (IOException e) {
            System.out.println("Error loading priority list");
        }

        EnumMap<LinkEffectName, Integer> totalEffects = new EnumMap<>(LinkEffectName.class);
        for (LinkEffectName name : LinkEffectName.values()) {
            totalEffects.put(name, 0);
        }
        EnumMap<LinkEffectName, Integer> bestTotals = null;
        int bestScore = 0;
        Module[] bestCombo = {m.getFirst(), m.get(1), m.get(2), m.get(3)};
        System.out.println("Beginning scoring... ");
        for (int i = 1; i < m.size()-4; i++) {
            for (int j = i + 1; j < m.size()-3; j++) {
                for (int k = j + 1; k < m.size()-2; k++) {
                    for (int l = k + 1; l < m.size()-1; l++) {
                        Module[] currentCombo = {m.get(i), m.get(j), m.get(k), m.get(l)};
                        totalEffects = combineNew(currentCombo);
                        int currentScore = weightedScore(totalEffects);
                        if (bestScore < currentScore) {
                            bestScore = currentScore;
                            bestCombo = currentCombo;
                            bestTotals = totalEffects;
                        }
                    }
                }
            }
        }
        System.out.println("Best 4 Mods");
        System.out.println("=============================================");
        for (Module mod : bestCombo) {
            System.out.println(mod.toString());
        }
        System.out.println("=============================================");
        if (bestTotals != null) {
            printTotals(bestTotals);
        } else {
            printTotals(totalEffects);
        }
    }

    private static void printTotals(EnumMap<LinkEffectName, Integer> effects) {

        List<Map.Entry<LinkEffectName, Integer>> sorted =
                effects.entrySet()
                        .stream()
                        .sorted(Map.Entry.<LinkEffectName, Integer>comparingByValue().reversed())
                        .toList();

        for (var entry : sorted) {
            if (entry.getValue() != 0)  {
                int value = entry.getValue();
                String name = entry.getKey().toString();
                if (value >= 20)      System.out.println(name + " LVL6: +" + value + "/+20");
                else if (value >= 16) System.out.println(name + " LVL5: +" + value + "/+20");
                else if (value >= 12) System.out.println(name + " LVL4: +" + value + "/+16");
                else if (value >= 8)  System.out.println(name + " LVL3: +" + value + "/+12");
                else if (value >= 4)  System.out.println(name + " LVL2: +" + value + "/+8");
                else                  System.out.println(name + " LVL1: +" + value + "/+4");
            }
        }
        System.out.println("=============================================");
    }



    private static int weightedScore(EnumMap<LinkEffectName, Integer> totalEffects) {
        int score = 0;
        for (var set : PRIORITY.entrySet()) {
            LinkEffectName name = set.getKey();
            int value = totalEffects.get(name);
            int weight = PRIORITY.getOrDefault(name, 0);

            if (value >= 20) score += 20*weight;
            else if (value >= 16) score += 16*weight;
            else if (value >= 12) score += 12*weight;
            else if (value >= 8) score += 8*weight;
            else if (value >= 4) score += 4*weight;
        }
        return score;
    }
    private static EnumMap<LinkEffectName, Integer> combineNew(Module[] m) {
        EnumMap<LinkEffectName, Integer> total = new EnumMap<>(LinkEffectName.class);
        for (LinkEffectName name : LinkEffectName.values()) {
            total.put(name, 0);
        }
        for (Module mod : m) {
            for (var x : mod.getEffects().entrySet()) {
                total.merge(x.getKey(), x.getValue(), Integer::sum );
            }
        }
        return total;
    }
}
