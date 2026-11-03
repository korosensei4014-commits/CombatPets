package su.nightexpress.combatpets.nms.paper26p12.ai;

import java.util.Collection;
import java.util.List;

import org.jspecify.annotations.NullMarked;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.Brain.ActivitySupplier;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import su.nightexpress.nightcore.util.Lists;

@NullMarked
public final class BrainUtils {

    private static final List<SensorType<? extends Sensor<? super Mob>>> DEFAULT_SENSORS = Lists.newList(
        SensorType.NEAREST_LIVING_ENTITIES,
        SensorType.NEAREST_PLAYERS,
        SensorType.HURT_BY
    );

    private BrainUtils() {
    }

    public static <E extends Mob> List<SensorType<? extends Sensor<? super E>>> defaultSensors() {
        return List.copyOf(DEFAULT_SENSORS);
    }

    public static <E extends Mob> Brain.Provider<E> brainProvider(ActivitySupplier<E> supplier) {
        return brainProvider(defaultSensors(), supplier);
    }

    /**
     * Like {@link #brainProvider(ActivitySupplier)}, plus extra memory types the vanilla mob reads directly
     * (otherwise it throws "Unregistered memory fetched").
     */
    public static <E extends Mob> Brain.Provider<E> brainProviderWithMemories(Collection<? extends net.minecraft.world.entity.ai.memory.MemoryModuleType<?>> memories,
                                                                  ActivitySupplier<E> supplier) {
        return Brain.provider(memories, defaultSensors(), supplier);
    }

    public static <E extends Mob> Brain.Provider<E> brainProvider(Collection<SensorType<? extends Sensor<? super E>>> sensors,
                                                                  ActivitySupplier<E> supplier) {
        return Brain.provider(sensors, supplier);
    }
}
