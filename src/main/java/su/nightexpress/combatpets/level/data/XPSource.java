package su.nightexpress.combatpets.level.data;

import org.bukkit.entity.*;
import org.jspecify.annotations.NonNull;
import su.nightexpress.combatpets.Placeholders;
import su.nightexpress.nightcore.config.ConfigValue;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.util.BukkitThing;
import su.nightexpress.nightcore.util.Lists;
import su.nightexpress.nightcore.util.random.Rnd;
import su.nightexpress.nightcore.util.wrapper.UniInt;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class XPSource {

    private final UniInt      amount;
    private final double      chance;
    private final Set<String> mobs;

    public XPSource(@NonNull UniInt amount, double chance, @NonNull Set<String> mobs) {
        this.amount = amount;
        this.chance = chance;
        this.mobs = mobs;
    }

    @NonNull
    public static XPSource read(@NonNull FileConfig config, @NonNull String path) {
        UniInt amount = UniInt.read(config, path + ".Amount");
        double chance = ConfigValue.create(path + ".Chance", 50).read(config);
        Set<String> mobs = ConfigValue.create(path + ".Mobs", Lists.newSet()).onRead(set -> Lists.modify(set,
            String::toLowerCase)).read(config);

        return new XPSource(amount, chance, mobs);
    }

    public void write(@NonNull FileConfig config, @NonNull String path) {
        this.amount.write(config, path + ".Amount");
        config.set(path + ".Chance", this.chance);
        config.set(path + ".Mobs", this.mobs);
    }

    public boolean checkChance() {
        return Rnd.chance(this.chance);
    }

    public boolean isMob(@NonNull String name) {
        return this.mobs.contains(name.toLowerCase()) || this.mobs.contains(Placeholders.WILDCARD);
    }

    @NonNull
    public UniInt getAmount() {
        return amount;
    }

    public double getChance() {
        return chance;
    }

    @NonNull
    public Set<String> getMobs() {
        return mobs;
    }

    @NonNull
    public static Map<String, XPSource> getDefaults() {
        Map<String, XPSource> map = new HashMap<>();

        Set<String> animalNames = new HashSet<>();
        Set<String> illagerNames = new HashSet<>();
        Set<String> monsterNames = new HashSet<>();
        Set<String> otherNames = new HashSet<>();

        for (EntityType entityType : EntityType.values()) {
            Class<? extends Entity> clazz = entityType.getEntityClass();
            if (clazz == null || !LivingEntity.class.isAssignableFrom(clazz)) continue;

            String name = BukkitThing.toString(entityType);

            if (Animals.class.isAssignableFrom(clazz)) {
                animalNames.add(name);
            }
            else if (Illager.class.isAssignableFrom(clazz)) {
                illagerNames.add(name);
            }
            else if (Monster.class.isAssignableFrom(clazz)) {
                monsterNames.add(name);
            }
            else if (Enemy.class.isAssignableFrom(clazz)) {
                otherNames.add(name);
            }
        }

        map.put("animals", new XPSource(UniInt.of(5, 25), 35, animalNames));
        map.put("monsters", new XPSource(UniInt.of(20, 60), 50, monsterNames));
        map.put("illagers", new XPSource(UniInt.of(30, 70), 70, illagerNames));
        map.put("others", new XPSource(UniInt.of(25, 50), 65, otherNames));
        return map;
    }
}
