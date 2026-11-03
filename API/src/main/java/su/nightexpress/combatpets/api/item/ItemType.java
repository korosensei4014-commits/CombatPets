package su.nightexpress.combatpets.api.item;

public enum ItemType {
    EGG,
    MYSTERY_EGG,
    STORED_EGG, // A pet stored with "Store in Egg" (level, name, aspects, stats kept).
    @Deprecated XP_ORB,
    @Deprecated REBIRTH
}
