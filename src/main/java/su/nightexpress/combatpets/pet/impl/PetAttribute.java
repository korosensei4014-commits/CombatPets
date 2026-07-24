package su.nightexpress.combatpets.pet.impl;

import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import su.nightexpress.combatpets.api.pet.Stat;
import su.nightexpress.nightcore.config.ConfigValue;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.util.StringUtil;

public class PetAttribute implements Stat {

    //    public static final IPetAttribute MAX_HEALTH           = register("max_health", Attribute.GENERIC_MAX_HEALTH);
    //    public static final IPetAttribute KNOCKBACK_RESISTANCE = register("knockback_resistance", Attribute.GENERIC_KNOCKBACK_RESISTANCE);
    //    public static final IPetAttribute MOVEMENT_SPEED       = register("movement_speed", Attribute.GENERIC_MOVEMENT_SPEED);
    //    public static final IPetAttribute FLYING_SPEED         = register("flying_speed", Attribute.GENERIC_FLYING_SPEED);
    //    public static final IPetAttribute ATTACK_DAMAGE        = register("attack_damage", Attribute.GENERIC_ATTACK_DAMAGE);
    //    public static final IPetAttribute ATTACK_KNOCKBACK     = register("attack_knockback", Attribute.GENERIC_ATTACK_KNOCKBACK);
    //    public static final IPetAttribute ATTACK_SPEED         = register("attack_speed", Attribute.GENERIC_ATTACK_SPEED);
    //    public static final IPetAttribute ARMOR                = register("armor", Attribute.GENERIC_ARMOR);
    //    public static final IPetAttribute HORSE_JUMP_STRENGTH  = register("horse_jump_strength", Attribute.HORSE_JUMP_STRENGTH);
    //
    //    public static final IPetAttribute MAX_SATURATION           = register("max_saturation");
    //    public static final IPetAttribute HEALTH_REGENEATION_FORCE = register("health_regeneration_force");
    //    public static final IPetAttribute HEALTH_REGENEATION_SPEED = register("health_regeneration_speed");

    private final String        id;
    private final NamespacedKey key;
    private final Attribute     vanillaMirror;

    private String displayName;

    public PetAttribute(@NonNull String id, @NonNull Attribute vanillaMirror) {
        this(id, vanillaMirror.getKey(), vanillaMirror);
    }

    public PetAttribute(@NonNull String id, @NonNull NamespacedKey key) {
        this(id, key, null);
    }

    private PetAttribute(@NonNull String id, @NonNull NamespacedKey key, @Nullable Attribute vanillaMirror) {
        this.id = id.toLowerCase();
        this.key = key;
        this.vanillaMirror = vanillaMirror;

        this.setDisplayName(StringUtil.capitalizeUnderscored(id));
    }

    public void read(@NonNull FileConfig config, @NonNull String path) {
        this.setDisplayName(ConfigValue.create(path + ".Name", this.getDisplayName()).read(config));
    }

    @NonNull
    public String getId() {
        return id;
    }

    @Override
    @NonNull
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public void setDisplayName(@NonNull String displayName) {
        this.displayName = displayName;
    }

    @NonNull
    @Override
    public NamespacedKey getKey() {
        return key;
    }

    @Override
    @Nullable
    public Attribute getVanillaMirror() {
        return vanillaMirror;
    }
}
