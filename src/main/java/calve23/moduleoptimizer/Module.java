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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (LinkEffectName n : effects.keySet()) {
            String name = n.toString();
            sb.append(name).append(" +").append(effects.get(n)).append(" | ");
        }
        sb.delete(sb.length() - 2, sb.length());
        return sb.toString();
    }
    public EnumMap<LinkEffectName, Integer> getEffects() {
        return new EnumMap<>(effects);
    }

}
