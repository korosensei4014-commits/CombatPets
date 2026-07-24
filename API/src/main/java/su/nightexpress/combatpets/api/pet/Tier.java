package su.nightexpress.combatpets.api.pet;

import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NonNull;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.util.placeholder.Placeholder;

import java.util.Map;

public interface Tier extends Placeholder {

    @NonNull
    FileConfig getConfig();

    boolean load();

    @NonNull
    String getId();

    @NonNull
    String getName();

    void setName(@NonNull String name);

    @NonNull
    ItemStack getIcon();

    void setIcon(@NonNull ItemStack icon);

    @NonNull
    String getNameFormat();

    void setNameFormat(@NonNull String nameFormat);

    double getWeight();

    void setWeight(double weight);

    //@NonNull IPetCustomizer getDefaultCustomizer();

    boolean isCapturable();

    void setCapturable(boolean capturable);

    boolean hasInventory();

    void setHasInventory(boolean hasInventory);

    int getInventorySize();

    void setInventorySize(int inventorySize);

    boolean hasEquipment();

    void setHasEquipment(boolean hasEquipment);

    int getAutoRespawnTime();

    void setAutoRespawnTime(int autoRespawnTime);

    @NonNull
    String getReviveCurrency();

    void setReviveCurrency(@NonNull String currency);

    double getReviveCost();

    void setReviveCost(double reviveCost);

    double getInventoryDropChance();

    void setInventoryDropChance(double inventoryDropChance);

    double getEquipmentDropChance();

    void setEquipmentDropChance(double equipmentDropChance);


    int getInitialXP();

    void setInitialXP(int initialXP);

    double getXPModifier();

    void setXPModifier(double xpModifier);

    int getMaxLevel();

    void setMaxLevel(int maxLevel);

    int getRequiredXP(int level);

    int getAspectPointsPerLevel();

    void setAspectPointsPerLevel(int aspectPointsPerLevel);


    int getStartAspectPoints();

    void setStartAspectPoints(int aspectPoints);

    @NonNull
    Map<String, Integer> getAspectsMax();

    default int getAspectMax(@NonNull Aspect aspect) {
        return this.getAspectMax(aspect.getId());
    }

    default int getAspectMax(@NonNull String id) {
        return this.getAspectsMax().getOrDefault(id.toLowerCase(), 0);
    }
}
