package calve23.moduleoptimizer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.lang.reflect.Type;

public class ScoreModules {
    public static Map<LinkEffectName, Double> PRIORITY = new HashMap<>();

    private static final Gson GSON =
            new GsonBuilder().setPrettyPrinting().create();

    private static final File FILE = new File("priority_list.json");

    public static void score(ArrayList<Module> m) {

        try (FileReader fr = new FileReader(FILE)) {
            Type type = new TypeToken<Map<LinkEffectName, Double>>() {}.getType();
            PRIORITY = GSON.fromJson(fr, type);
            System.out.println("Loading priority list");
        } catch (IOException e) {
            System.out.println("Error loading priority list");
        }

        EnumMap<LinkEffectName, Double> totalEffects = new EnumMap<>(LinkEffectName.class);
        for (LinkEffectName name : LinkEffectName.values()) {
            totalEffects.put(name, 0.0);
        }

        double bestScore = 0;
        Module[] bestCombo = {m.getFirst(), m.get(1), m.get(2), m.get(3)};

        for (int i = 1; i < m.size()-4; i++) {
            for (int j = i + 1; j < m.size()-3; j++) {
                for (int k = j + 1; k < m.size()-2; k++) {
                    for (int l = k + 1; l < m.size()-1; l++) {
                        Module[] currentCombo = {m.get(i), m.get(j), m.get(k), m.get(l)};
                        totalEffects = combine(currentCombo);
                        double currentScore = weightedScore(totalEffects);
                        if (bestScore < currentScore) {
                            bestScore = currentScore;
                            bestCombo = currentCombo;
                        }
                    }
                }
            }
        }
        System.out.println("Best 4 Mods");
        for (Module mod : bestCombo) {
            System.out.println(mod.getEffects().toString());
        }
    }

    private static double weightedScore(EnumMap<LinkEffectName, Double> totalEffects) {
        double score = 0;
        for (var set : PRIORITY.entrySet()) {
            LinkEffectName name = set.getKey();
            Double value = totalEffects.get(name);
            Double weight = PRIORITY.getOrDefault(name, 0.0);

            if (value >= 20) score += 20*weight;
            else if (value >= 16) score += 16*weight;
            else if (value >= 12) score += 12*weight;
            else if (value >= 8) score += 8*weight;
            else if (value >= 4) score += 4*weight;
        }
        return score;
    }
    private static EnumMap<LinkEffectName, Double> combine(Module[] m) {
        EnumMap<LinkEffectName, Double> total = new EnumMap<>(LinkEffectName.class);
        for (LinkEffectName name : LinkEffectName.values()) {
            total.put(name, 0.0);
        }
        for (Module mod : m) {
            for (var x : mod.getEffects().entrySet()) {
                total.merge(x.getKey(), (double)x.getValue(), Double::sum);
            }
        }
        return total;
    }
}
