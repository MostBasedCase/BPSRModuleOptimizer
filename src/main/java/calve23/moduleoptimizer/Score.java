package calve23.moduleoptimizer;
import java.util.*;

public class Score {
    private static int weightedScore(EnumMap<LinkEffectName, Integer> totalEffects, Map<LinkEffectName, Integer> priority) {
        int score = 0;
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
    private static LinkEffect randomEffectAndValue(Random rng) {
        return new LinkEffect(LinkEffectName.values()[rng.nextInt(LinkEffectName.values().length)],
                rng.nextInt(1,11));
    }
    private static Module[] makeRandomMods() {
        Random rng = new Random();
        Module[] mods = new Module[100];
        for (int i = 0; i < mods.length; i++) {
            int num = rng.nextInt(100);
            Module temp = new Module(new LinkEffect(randomEffectAndValue(rng)));
            if (num % 10 == 0) {//lvl 3 mod

            }
            else if (num % 7 == 0) {//lvl 1 mod

            }
            else if (num % 4 == 0) {// lvl 2 mod

            }
        }
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

    //We score by getting every combos
    // total value based on weight and levels met 0/4/8/12/16/20
    // (brute force for now) n usually under ~150 and

    public static void main(String[] args) {

        //ask for a priority list of link effects store in this array
        Map<LinkEffectName, Integer> priority = new HashMap<>();
        priority.put(LinkEffectName.RESISTANCE, 5);
        priority.put(LinkEffectName.ARMOR, 3);
        priority.put(LinkEffectName.CRIT_FOCUS, 2);
        priority.put(LinkEffectName.ATTACK_SPD, 1);


        EnumMap<LinkEffectName, Integer> totalEffects = new EnumMap<>(LinkEffectName.class);
        for (LinkEffectName name : LinkEffectName.values()) {
            totalEffects.put(name, 0);
        }
        //this is where I put the modules, IF I HAD SOME.
        //I made random modules to test the scoring
        Module[] modules = makeRandomMods();
        int bestScore = 0;
        Module[] bestCombo = {modules[0], modules[1], modules[2], modules[3]};

        for (int i = 1; i < modules.length-4; i++) {

            for (int j = i + 1; j < modules.length-3; j++) {
                for (int k = j + 1; k < modules.length-2; k++) {
                    for (int l = k + 1; l < modules.length-1; l++) {
                        Module[] currentCombo = {modules[i], modules[j], modules[k], modules[l]};
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
        for (Module m : bestCombo) {
            System.out.println(m.getEffects().toString());
        }
    }


}
