package scripts.MassFighter.Data;

import com.runemate.game.api.hybrid.local.Skill;

public enum Potion {

    // MULTIPLE
    OVERLOAD("Overload", getIncrease(Skill.ATTACK, 3, 15), Skill.ATTACK, Skill.DEFENCE, Skill.STRENGTH, Skill.RANGED, Skill.MAGIC),
    COMBAT_POTION("Combat potion", getIncrease(Skill.ATTACK, 3, 10), Skill.ATTACK, Skill.STRENGTH),
    // ATTACK POTIONS
    ATTACK_POTION("Attack potion", getIncrease(Skill.ATTACK, 1, 8), Skill.ATTACK),
    ATTACK_MIX("Attack mix", getIncrease(Skill.ATTACK, 1, 8), Skill.ATTACK),
    SUPER_ATTACK("Super attack", getIncrease(Skill.ATTACK, 2, 12), Skill.ATTACK),
    SUPER_ATTACK_MIX("Super attack mix", getIncrease(Skill.ATTACK, 2, 12), Skill.ATTACK),
    EXTREME_ATTACK("Extreme attack", getIncrease(Skill.ATTACK, 3, 15), Skill.ATTACK),
    // STRENGTH POTIONS
    STRENGTH_POTION("Strength potion", getIncrease(Skill.STRENGTH, 1, 8),  Skill.STRENGTH),
    STRENGTH_MIX("Strength mix", getIncrease(Skill.STRENGTH, 1, 8),  Skill.STRENGTH),
    SUPER_STRENGTH("Super strength", getIncrease(Skill.STRENGTH, 2, 12), Skill.STRENGTH),
    SUPER_STRENGTH_MIX("Super strength mix", getIncrease(Skill.STRENGTH, 2, 12), Skill.STRENGTH),
    EXTREME_STRENGTH("Extreme strength", getIncrease(Skill.STRENGTH, 3, 15), Skill.STRENGTH),
    // DEFENCE POTIONS
    DEFENCE_POTION("Defence potion", getIncrease(Skill.DEFENCE, 1, 8), Skill.DEFENCE),
    DEFENCE_MIX("Defence mix", getIncrease(Skill.DEFENCE, 1, 8), Skill.DEFENCE),
    SUPER_DEFENCE("Super defence", getIncrease(Skill.DEFENCE, 2, 12), Skill.DEFENCE),
    SUPER_DEFENCE_MIX("Super defence mix", getIncrease(Skill.DEFENCE, 2, 12), Skill.DEFENCE),
    EXTREME_DEFENCE("Extreme defence", getIncrease(Skill.DEFENCE, 3, 15), Skill.DEFENCE),
    // RANGED POTIONS
    RANGING_POTION("Ranging potion", getIncrease(Skill.RANGED, 1, 8), Skill.RANGED),
    RANGING_MIX("Ranging mix", getIncrease(Skill.RANGED, 1, 8), Skill.RANGED),
    SUPER_RANGING_POTION("Super ranging potion", getIncrease(Skill.RANGED, 2, 12), Skill.RANGED),
    SUPER_RANGING_MIX("Super ranging mix", getIncrease(Skill.RANGED, 2, 12), Skill.RANGED),
    EXTREME_RANGING("Extreme ranging", getIncrease(Skill.RANGED, 3, 15), Skill.RANGED),
    // MAGIC POTIONS
    MAGIC_POTION("Magic potion", getIncrease(Skill.MAGIC, 1, 8), Skill.MAGIC),
    MAGIC_MIX("Magic mix", getIncrease(Skill.MAGIC, 1, 8), Skill.MAGIC),
    SUPER_MAGIC_POTION("Super magic potion", getIncrease(Skill.MAGIC, 2, 12), Skill.MAGIC),
    SUPER_MAGIC_MIX("Super magic mix", getIncrease(Skill.MAGIC, 2, 12), Skill.MAGIC),
    EXTREME_MAGIC("Extreme magic", getIncrease(Skill.MAGIC, 3, 15), Skill.MAGIC);

    private String potionName;
    private Skill[] potionSkills;
    private int potionBoost;

    public int getBoost() {
        return potionBoost;
    }

    public Skill[] getPotionSkills() {
        return potionSkills;
    }

    public String getPotionName() {
        return potionName;
    }

    private Potion(String name, int boost,  Skill... skills) {
        potionName = name;
        potionBoost = boost;
        potionSkills = skills;
    }

    private static int getIncrease(Skill skill, int increase, int percent) {
        return increase + (skill.getBaseLevel()/100*percent);
    }





}
