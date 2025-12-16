package calve23.moduleoptimizer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.lang.reflect.Type;


public class ScoreModules {
    public static Map<LinkEffectName, Integer> PRIORITY = new HashMap<>();
    private static final Gson GSON =
            new GsonBuilder().setPrettyPrinting().create();
    private static final File FILE = new File("priority_list.json");

    //We score by getting every combos
    // total value based on weight and levels met 0/4/8/12/16/20
    // (brute force for now) n usually under ~150 and
    //ask for a priority list of link effects store in this array
    public static void score(ArrayList<Module> m) {
        //we will just read a file for now
        try (FileReader fr = new FileReader(FILE)) {
            Type type = new TypeToken<Map<LinkEffectName, Integer>>() {}.getType();
            PRIORITY = GSON.fromJson(fr, type);
            System.out.println("Loading priority list");
        } catch (IOException e) {
            System.out.println("Error loading priority list");
            System.out.println("Score will now be calculated by highest total value");
        }

        EnumMap<LinkEffectName, Integer> totalEffects = new EnumMap<>(LinkEffectName.class);
        for (LinkEffectName name : LinkEffectName.values()) {
            totalEffects.put(name, 0);
        }

        int bestScore = 0;
        Module[] bestCombo = {m.getFirst(), m.get(1), m.get(2), m.get(3)};

        for (int i = 1; i < m.size()-4; i++) {

            for (int j = i + 1; j < m.size()-3; j++) {
                for (int k = j + 1; k < m.size()-2; k++) {
                    for (int l = k + 1; l < m.size()-1; l++) {
                        Module[] currentCombo = {m.get(i), m.get(j), m.get(k), m.get(l)};
                        totalEffects = combine(currentCombo);
                        int currentScore = weightedScore(totalEffects);
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

    private static int weightedScore(EnumMap<LinkEffectName, Integer> totalEffects) {
        int score = 0;
        //now adding the value will give a equal combos but smaller higher values more priority
        // we reach 16 but there a same combo that reaches 17 so we pick 17 for extra attribute points
        // small gain but a gain is a gain.
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

    private static EnumMap<LinkEffectName, Integer> combine(Module[] m) {
        EnumMap<LinkEffectName, Integer> total = new EnumMap<>(LinkEffectName.class);
        for (LinkEffectName name : LinkEffectName.values()) {//make map have all 0 values for all stats
            total.put(name, 0);
        }
        //merge handles 0 values so we can remove up here^
        //use getOrDefault to get values default being 0 if not there
        for (Module mod : m) {
            for (var x : mod.getEffects().entrySet()) {
                //combine all modules values and get stat total
                total.merge(x.getKey(), x.getValue(), Integer::sum);
            }
        }
        return total;
    }
}
