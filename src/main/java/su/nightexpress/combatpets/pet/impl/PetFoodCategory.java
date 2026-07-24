package su.nightexpress.combatpets.pet.impl;

import org.jspecify.annotations.NonNull;
import su.nightexpress.combatpets.api.pet.FoodCategory;
import su.nightexpress.combatpets.api.pet.FoodItem;
import su.nightexpress.nightcore.config.ConfigValue;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.util.StringUtil;

import java.util.HashMap;
import java.util.Map;

public class PetFoodCategory implements FoodCategory {

    private final String                id;
    private final String                name;
    private final Map<String, FoodItem> itemMap;

    public PetFoodCategory(@NonNull String id, @NonNull String name, @NonNull Map<String, FoodItem> itemMap) {
        this.id = id;
        this.name = name;
        this.itemMap = itemMap;
    }

    @NonNull
    public static PetFoodCategory read(@NonNull FileConfig config, @NonNull String path, @NonNull String id) {
        String name = ConfigValue.create(path + ".Name", StringUtil.capitalizeUnderscored(id)).read(config);
        Map<String, FoodItem> itemMap = new HashMap<>();

        PetFoodCategory category = new PetFoodCategory(id, name, itemMap);

        config.getSection(path + ".Items").forEach(itemId -> {
            FoodItem foodItem = PetFoodItem.read(config, path + ".Items." + itemId, itemId, category);
            itemMap.put(foodItem.getId(), foodItem);
        });

        return category;
    }

    @Override
    public void write(@NonNull FileConfig config, @NonNull String path) {
        config.set(path + ".Name", this.getName());
        this.itemMap.values().forEach(foodItem -> {
            foodItem.write(config, path + ".Items." + foodItem.getId());
        });
    }

    @Override
    @NonNull
    public String getId() {
        return id;
    }

    @Override
    @NonNull
    public String getName() {
        return name;
    }

    @Override
    @NonNull
    public Map<String, FoodItem> getItemMap() {
        return itemMap;
    }
}
