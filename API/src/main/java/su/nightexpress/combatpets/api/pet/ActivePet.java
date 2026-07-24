package su.nightexpress.combatpets.api.pet;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.inventory.InventoryHolder;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import su.nightexpress.combatpets.api.pet.type.CombatMode;
import su.nightexpress.combatpets.api.pet.type.ExhaustReason;
import su.nightexpress.nightcore.util.placeholder.Placeholder;

@NullMarked
public interface ActivePet extends Placeholder, InventoryHolder {

    PetSharedData getSharedData();

    @Nullable
    Player getOwner();

    LivingEntity getEntity();

    boolean isOwner(Player player);

    void openMenu();

    void tickPet();

    void handleSpawn();

    void handleDeath();

    void remove();

    void onLevelUp();

    void onLevelDowngrade();

    void onIncomingDamage();

    void saveData();

    void update();

    void updateAttributes();

    void updateName();

    void removeHealthBar();

    void updateHealthBar();

    Tier getTier();

    Template getTemplate();

    double getAttribute(String name);

    double getAttribute(Stat attribute);

    double getAttackDamage();

    double getMaxSaturation();

    double getMaxHealth();

    double getRegenerationForce();

    double getRegenerationSpeed();

    boolean isEquipmentUnlocked();

    void setEquipmentUnlocked(boolean unlocked);

    boolean doRegenerate(EntityRegainHealthEvent.RegainReason reason);

    void doRegenerate(double amount);

    void doExhaust(ExhaustReason reason);

    void doExhaust(double amount);

    boolean doFeed(FoodItem foodItem);

    void doFeed(double amount);

    void moveToOwner();

    void resetLeveling();

    int getXP();

    int getRequiredXP();

    boolean canGainXP();

    boolean canLossXP();

    void addXP(int amount);

    void removeXP(int amount);

    void setXP(int amount);

    int getLevel();

    boolean isMaxLevel();

    void addLevel(int amount);

    void addAspectPoints(int amount);

    void setAspectPoints(int amount);

    void removeAspectPoints(int amount);

    int getAspectPoints();

    int getAspectValue(Aspect aspect);

    void setAspectValue(Aspect aspect, int value);

    void reallocateAspects();

    void equipPet();

    void toggleCombatMode();

    void setName(String name);


    String getName();

    void setSilent(boolean silent);

    boolean isSilent();


    CombatMode getCombatMode();
}
