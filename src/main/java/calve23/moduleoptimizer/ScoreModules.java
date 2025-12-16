package calve23.moduleoptimizer;
import java.util.*;


public class ScoreModules {

    //We score by getting every combos
    // total value based on weight and levels met 0/4/8/12/16/20
    // (brute force for now) n usually under ~150 and
    //ask for a priority list of link effects store in this array
    public static void score(ArrayList<Module> m) {

        Map<LinkEffectName, Integer> priority = new HashMap<>();

        //saving this for testing my frost mage
//        priority.put(LinkEffectName.SPECIAL_ATTACK, 6);
//        priority.put(LinkEffectName.ELITE_STRIKE, 5);
//        priority.put(LinkEffectName.INTELLECT_BOOST, 4);
//        priority.put(LinkEffectName.CRIT_FOCUS, 3);
//        priority.put(LinkEffectName.CAST_FOCUS, 1);
//        priority.put(LinkEffectName.LUCK_FOCUS, 1);

        //saving this for testing my tank
        priority.put(LinkEffectName.RESISTANCE, 4);
        priority.put(LinkEffectName.ARMOR, 3);
        priority.put(LinkEffectName.CRIT_FOCUS, 2);
        priority.put(LinkEffectName.ATTACK_SPD, 1);


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
                        int currentScore = weightedScore(totalEffects, priority);
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










    private static int weightedScore(EnumMap<LinkEffectName, Integer> totalEffects, Map<LinkEffectName, Integer> priority) {
        int score = 0;
        //now adding the value will give a equal combos but smaller higher values more priority
        // we reach 16 but there a same combo that reaches 17 so we pick 17 for extra attribute points
        // small gain but a gain is a gain.
        for (var set : priority.entrySet()) {
            LinkEffectName name = set.getKey();
            int value = totalEffects.get(name);
            int weight = priority.getOrDefault(name, 0);
            if (value >= 20) score += 20*weight;
            else if (value >= 16) score += 16*weight;
            else if (value >= 12) score += 12*weight;
            else if (value >= 8) score += 8*weight;
            else if (value >= 4) score += 4*weight;
        }
        return score;
    }
    private static LinkEffect randomEffect(Random rng) {
        return new LinkEffect(LinkEffectName.values()[rng.nextInt(LinkEffectName.values().length)],
                rng.nextInt(1,11));
    }
    private static Module[] makeRandomMods() {
        Random rng = new Random(1);
        Module[] mods = new Module[100];
        for (int i = 0; i < mods.length; i++) {
            int num = rng.nextInt(100);
            Module temp = new Module(randomEffect(rng));
            if (num % 8 == 0) {//lvl 3 mod (3 effects)
                temp.addEffect(randomEffect(rng));
                temp.addEffect(randomEffect(rng));
            }
            else if (num % 2 == 0) { //lvl 2 mod (2 effects)
                temp.addEffect(randomEffect(rng));
            }
            mods[i] = temp;
        }
        return mods;
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
