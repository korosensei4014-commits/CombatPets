package su.nightexpress.combatpets.pet.impl;

import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NonNull;
import su.nightexpress.combatpets.api.pet.FoodCategory;
import su.nightexpress.combatpets.api.pet.FoodItem;
import su.nightexpress.nightcore.config.FileConfig;

public class PetFoodItem implements FoodItem {

    private final String          id;
    private final PetFoodCategory category;
    private final ItemStack       item;
    private final double          saturation;

    public PetFoodItem(@NonNull String id, @NonNull PetFoodCategory category, @NonNull ItemStack item,
                       double saturation) {
        this.id = id.toLowerCase();
        this.category = category;
        this.item = new ItemStack(item);
        this.saturation = saturation;
    }

    @NonNull
    public static PetFoodItem read(@NonNull FileConfig config, @NonNull String path, @NonNull String id,
                                   @NonNull PetFoodCategory category) {
        ItemStack itemStack = config.getItem(path + ".Item");
        double saturation = config.getDouble(path + ".Saturation");

        return new PetFoodItem(id, category, itemStack, saturation);
    }

    @Override
    public void write(@NonNull FileConfig config, @NonNull String path) {
        config.setItem(path + ".Item", this.getItem());
        config.set(path + ".Saturation", this.getSaturation());
    }

    @Override
    public boolean isItem(@NonNull ItemStack itemStack) {
        if (this.item.hasItemMeta()) {
            return itemStack.isSimilar(this.item);
        }
        return this.item.getType() == itemStack.getType();
    }

    @Override
    @NonNull
    public String getId() {
        return id;
    }

    @Override
    @NonNull
    public FoodCategory getCategory() {
        return category;
    }

    @Override
    @NonNull
    public ItemStack getItem() {
        return new ItemStack(this.item);
    }

    @Override
    public double getSaturation() {
        return this.saturation;
    }
}
