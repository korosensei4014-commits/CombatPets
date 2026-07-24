package su.nightexpress.combatpets.api.pet;

import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NonNull;
import su.nightexpress.nightcore.config.FileConfig;

public interface FoodItem {

    void write(@NonNull FileConfig config, @NonNull String path);

    boolean isItem(@NonNull ItemStack itemStack);

    @NonNull
    String getId();

    @NonNull
    FoodCategory getCategory();

    @NonNull
    ItemStack getItem();

    double getSaturation();
}
