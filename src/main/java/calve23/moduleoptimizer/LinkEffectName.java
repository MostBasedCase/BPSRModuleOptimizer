package calve23.moduleoptimizer;

public enum LinkEffectName {
    STRENGTH_BOOST("Strength Boost"),
    AGILITY_BOOST("Agility Boost"),
    INTELLECT_BOOST("Intellect Boost"),
    SPECIAL_ATTACK("Special Attack"),
    ELITE_STRIKE("Elite Strike"),
    HEALING_BOOST("Healing Boost"),
    HEALING_ENHANCE("Healing Enhance"),
    RESISTANCE("Resistance"),
    ARMOR("Armor"),
    CAST_FOCUS("Cast Focus"),
    ATTACK_SPD("Attack Speed"),
    CRIT_FOCUS("Crit Focus"),
    TEAM_LUCK_AND_CRIT("Team Luck & Crit"),
    FINAL_PROTECTION("Final Protection"),
    LIFE_CONDENSE("Life Condense"),
    FIRST_AID("First Aid"),
    LIFE_STEAL("Life Steal"),
    LIFE_WAVE("Life Wave"),
    AGILE("Agile"),
    DMG_STACK("DMG Stack"),
    LUCK_FOCUS("Luck Focus"),;

    private final String name;

    LinkEffectName(String name) {
        this.name = name;
    }
    @Override
    public String toString() {
        return name;
    }
}
