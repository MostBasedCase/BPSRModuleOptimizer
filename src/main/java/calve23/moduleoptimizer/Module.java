package calve23.moduleoptimizer;

import java.util.EnumMap;
import java.util.List;

public class Module {

    public final EnumMap<LinkEffectName, Integer> effects = new EnumMap<>(LinkEffectName.class);

    public Module(List<LinkEffect> effects) {
        for (LinkEffect effect : effects) {
            this.effects.put(effect.name(), effect.value());
        }
    }
    public Module(LinkEffect effect) {
        this.effects.put(effect.name(), effect.value());
    }
    public String toString() {
        return getEffects().toString();
    }
    public EnumMap<LinkEffectName, Integer> getEffects() {
        return new EnumMap<>(effects);
    }

}
