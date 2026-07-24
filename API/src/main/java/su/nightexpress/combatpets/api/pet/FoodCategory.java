package su.nightexpress.combatpets.api.pet;

import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import su.nightexpress.nightcore.config.FileConfig;

import java.util.*;

public interface FoodCategory {

    void write(@NonNull FileConfig config, @NonNull String path);

    @Nullable
    default FoodItem getItem(@NonNull ItemStack itemStack) {
        return this.getItems().stream().filter(foodItem -> foodItem.isItem(itemStack)).findFirst().orElse(null);
    }

    default boolean isItem(@NonNull ItemStack itemStack) {
        return this.getItem(itemStack) != null;
    }

    @NonNull
    default Set<FoodItem> getItems() {
        return new HashSet<>(this.getItemMap().values());
    }

    @NonNull
    default List<String> getItemNames() {
        return new ArrayList<>(this.getItemMap().keySet());
    }

    @Nullable
    default FoodItem getItem(@NonNull String id) {
        return this.getItemMap().get(id.toLowerCase());
    }

    @NonNull
    String getId();

    @NonNull
    String getName();

    @NonNull
    Map<String, FoodItem> getItemMap();
}
