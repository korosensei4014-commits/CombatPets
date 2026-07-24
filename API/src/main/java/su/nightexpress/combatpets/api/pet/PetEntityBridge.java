package su.nightexpress.combatpets.api.pet;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.*;

public class PetEntityBridge {

    public static final Map<PetEntity, ActivePet> BY_PET = new WeakHashMap<>();
    public static final Map<UUID, ActivePet>      BY_ID  = new HashMap<>();

    public static ActivePet getByPet(@NonNull PetEntity entity) {
        return BY_PET.get(entity);
    }

    public static boolean isPet(@NonNull LivingEntity entity) {
        return getByMob(entity) != null;
    }

    @Nullable
    public static ActivePet getByMob(@NonNull LivingEntity entity) {
        if (entity.getType() == EntityType.PLAYER) {
            return null;
        }
        return getByMobId(entity.getUniqueId());
    }

    @Nullable
    public static ActivePet getByPlayer(@NonNull Player player) {
        return BY_ID.get(player.getUniqueId());
    }

    @Nullable
    public static ActivePet getByMobId(@NonNull UUID uuid) {
        return BY_ID.get(uuid);
    }

    @NonNull
    public static Collection<ActivePet> getAll() {
        return new HashSet<>(BY_PET.values());
    }

    public static void addHolder(@NonNull PetEntity entity, @NonNull ActivePet holder) {
        BY_PET.put(entity, holder);
        BY_ID.put(holder.getOwner().getUniqueId(), holder);
        BY_ID.put(holder.getEntity().getUniqueId(), holder);
    }

    /*public static void removeHolder(@NonNull PetEntity entity) {
        IPetHolder holder = BY_ENTITY.remove(entity);
        if (holder == null) return;
    
        BY_ID.remove(holder.getEntity().getUniqueId());
        BY_ID.remove(holder.getOwner().getUniqueId());
    }*/

    public static void removeHolder(@NonNull ActivePet holder) {
        BY_PET.values().remove(holder);
        BY_ID.remove(holder.getEntity().getUniqueId());
        BY_ID.remove(holder.getOwner().getUniqueId());
    }
}
