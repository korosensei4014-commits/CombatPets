package su.nightexpress.combatpets.api.pet;

import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NonNull;
import su.nightexpress.combatpets.api.pet.type.ExhaustReason;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.util.bukkit.NightSound;
import su.nightexpress.nightcore.util.placeholder.Placeholder;
import su.nightexpress.nightcore.util.wrapper.UniParticle;

import java.io.File;
import java.util.Map;
import java.util.Set;

public interface Template extends Placeholder {

    @NonNull
    File getFile();

    @NonNull
    FileConfig getConfig();

    boolean load();

    @NonNull
    ItemStack createEgg(@NonNull Tier tier);

    boolean isFood(@NonNull ItemStack itemStack);

    default boolean isFood(@NonNull FoodItem foodItem) {
        return this.getFoodCategories().contains(foodItem.getCategory().getId());
    }

    @NonNull
    String getId();

    @NonNull
    String getDefaultName();

    void setDefaultName(@NonNull String defaultName);

    @NonNull
    EntityType getEntityType();

    void setEntityType(@NonNull EntityType type);

    @NonNull
    String getEggTexture();

    void setEggTexture(@NonNull String eggTexture);

    boolean canHaveInventory();

    void setInventory(boolean inventory);

    boolean canHaveEquipment();

    void setEquipment(boolean equipment);


    boolean isCapturable();

    void setCapturable(boolean capturable);

    /**
     * @return Max. health (in % of max. health) a mob of this type may have to be captured, or a negative value to use
     * the global capture setting.
     */
    double getCaptureHealthPercent();

    void setCaptureHealthPercent(double percent);

    double getCaptureEscapeChance();

    void setCaptureEscapeChance(double catchingEscapeChance);

    double getCaptureChance();

    void setCaptureChance(double catchingChance);

    @NonNull
    String getCapturePermission();


    @NonNull
    NightSound getEatSound();

    void setEatSound(@NonNull NightSound eatSound);

    @NonNull
    UniParticle getSpawnParticle();

    void setSpawnParticle(@NonNull UniParticle spawnParticle);

    @NonNull
    UniParticle getDespawnParticle();

    void setDespawnParticle(@NonNull UniParticle despawnParticle);

    @NonNull
    Set<String> getFoodCategories();

    void setFoodCategories(@NonNull Set<String> foodCategories);


    double getExhaustModifier(@NonNull ExhaustReason reason);

    void setExhaustModifier(@NonNull ExhaustReason reason, double mod);

    @NonNull
    Map<String, Double> getAttributesStart();

    @NonNull
    Map<String, Double> getAttributesPerAspect();

    default boolean hasAttribute(@NonNull Stat attribute) {
        return this.getAttributesStart().containsKey(attribute.getId());
    }

    default double getAttributeStart(@NonNull Stat attribute) {
        return this.getAttributeStart(attribute.getId());
    }

    default double getAttributePerAspect(@NonNull Stat attribute) {
        return this.getAttributePerAspect(attribute.getId());
    }

    default double getAttributeStart(@NonNull String attribute) {
        return this.getAttributesStart().getOrDefault(attribute.toLowerCase(), 0D);
    }

    default double getAttributePerAspect(@NonNull String attribute) {
        return this.getAttributesPerAspect().getOrDefault(attribute.toLowerCase(), 0D);
    }
}
