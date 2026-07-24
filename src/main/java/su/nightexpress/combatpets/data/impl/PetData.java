package su.nightexpress.combatpets.data.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.bukkit.Material;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import su.nightexpress.combatpets.PetAPI;
import su.nightexpress.combatpets.Placeholders;
import su.nightexpress.combatpets.api.pet.Aspect;
import su.nightexpress.combatpets.api.pet.Stat;
import su.nightexpress.combatpets.api.pet.Template;
import su.nightexpress.combatpets.api.pet.Tier;
import su.nightexpress.combatpets.api.pet.Wardrobe;
import su.nightexpress.combatpets.api.pet.type.CombatMode;
import su.nightexpress.combatpets.config.Config;
import su.nightexpress.combatpets.pet.AttributeRegistry;
import su.nightexpress.combatpets.util.PetUtils;
import su.nightexpress.combatpets.wardrobe.PetWardrobe;
import su.nightexpress.nightcore.util.placeholder.Placeholder;
import su.nightexpress.nightcore.util.placeholder.PlaceholderMap;

public class PetData implements Placeholder {

    private final Template template;
    private final Tier     tier;

    private String                        name;
    private boolean                       silent;
    private long                          reviveDate;
    private double                        health;
    private double                        foodLevel;
    private int                           level;
    private int                           xp;
    private int                           aspectPoints;
    private Map<String, Integer>          aspects;
    private List<ItemStack>               inventory;
    private Map<EquipmentSlot, ItemStack> equipment;
    private CombatMode                    combatMode;
    private PetWardrobe                   wardrobe;

    private final PlaceholderMap placeholderMap;

    public static PetData create(@NonNull Template config, @NonNull Tier tier) {
        String defaultName = config.getDefaultName();
        boolean silent = false;
        long reviveDate = 0L;
        double health = config.getAttributeStart(AttributeRegistry.MAX_HEALTH);
        double foodLevel = config.getAttributeStart(AttributeRegistry.MAX_SATURATION);
        int level = 1;
        int xp = 0;
        int aspectPoints = tier.getStartAspectPoints();

        Map<String, Integer> aspects = new HashMap<>();
        List<ItemStack> inventory = new ArrayList<>();
        Map<EquipmentSlot, ItemStack> equipment = new HashMap<>();
        CombatMode combatMode = CombatMode.PROTECTIVE_AND_SUPPORTIVE;
        PetWardrobe customizer = new PetWardrobe();

        PetData data = new PetData(
            config, tier, defaultName, silent, reviveDate, health, foodLevel, level, xp, aspectPoints, aspects, inventory, equipment, combatMode, customizer
        );

        if (Config.PET_POP_DEFAULT_EQUIPMENT.get()) {
            PetUtils.populateDefaultEquipment(data);
        }

        return data;
    }

    public PetData(
                   @NonNull Template template,
                   @NonNull Tier tier,
                   @NonNull String name,
                   boolean silent,
                   long reviveDate,
                   double health,
                   double foodLevel,
                   int level,
                   int xp,
                   int points,
                   @NonNull Map<String, Integer> aspects,
                   @NonNull List<ItemStack> inventory,
                   @NonNull Map<EquipmentSlot, ItemStack> equipment,
                   @NonNull CombatMode combatMode,
                   @NonNull PetWardrobe wardrobe
    ) {
        this.template = template;
        this.tier = tier;
        this.setName(name);
        this.setSilent(silent);
        this.setReviveDate(reviveDate);
        this.setHealth(health);
        this.setFoodLevel(foodLevel);
        this.setAspectPoints(points);
        this.setAspects(aspects);
        this.setInventory(inventory);
        this.setEquipment(equipment);
        this.setCombatMode(combatMode);
        this.setWardrobe(wardrobe);

        this.setLevel(level);
        this.setXP(xp);

        this.placeholderMap = Placeholders.forData(this);
    }

    @Override
    @NonNull
    public PlaceholderMap getPlaceholders() {
        return this.placeholderMap;
    }

    public void refresh() {
        this.updateXP();
        this.tryRevive();
    }

    public void resetXP() {
        this.setLevel(1);
        this.setXP(0);
        this.setAspectPoints(0);
        this.getAspects().clear();

        this.updateXP();
    }

    public void updateXP() {
        boolean isMaxLevel = this.getLevel() >= this.getMaxLevel();
        if (this.getXP() >= this.getRequiredXP() && (!isMaxLevel)) {
            this.upLevel(this.getXP() - this.getRequiredXP());
        }

        if (this.getXP() <= -(this.getRequiredXP()) && (this.getLevel() > 1)) {
            this.downLevel(this.getXP() + (this.getRequiredXP()));
        }
    }

    public boolean tryRevive() {
        if (this.isReviveTime()) {
            this.revive();
            return true;
        }
        return false;
    }

    public void revive() {
        this.setHealth(this.getAttributeValue(AttributeRegistry.MAX_HEALTH));
        this.setReviveDate(0L);
    }

    public void removeXP(int remove) {
        remove = Math.abs(remove);

        int toDown = this.getXPToLevelDown();
        if (remove >= toDown) {
            boolean isFirstLevel = this.getLevel() == 1;

            if (isFirstLevel) {
                this.setXP(-this.getRequiredXP());
            }
            else {
                this.downLevel(remove - toDown);
            }
            return;
        }

        this.setXP(this.getXP() - remove);
    }

    public void addXP(int gain) {
        gain = Math.abs(gain);

        int toUp = this.getXPToLevelUp();
        if (gain >= toUp) {
            boolean isMaxLevel = this.getLevel() >= this.getMaxLevel();

            if (isMaxLevel) {
                this.setXP(this.getRequiredXP());
            }
            else {
                this.upLevel(gain - toUp);
            }
            return;
        }

        this.setXP(this.getXP() + gain);
    }

    public void upLevel(int expLeft) {
        this.setLevel(this.getLevel() + 1);

        int expReq = this.getRequiredXP();
        if (expReq <= 0) expReq = this.getTier().getInitialXP();

        this.setXP(expLeft);
        this.addAspectPoints(this.getTier().getAspectPointsPerLevel());

        if (this.getXP() >= expReq) {
            if (this.getLevel() >= this.getMaxLevel()) {
                this.addXP(1);
            }
            else {
                this.upLevel(expLeft - expReq);
            }
        }
    }

    public void downLevel(int expLeft) {
        if (this.getLevel() == 1) return;
        this.setLevel(this.getLevel() - 1);

        int expMax = this.getRequiredXP();
        if (expMax <= 0) expMax = this.getTier().getInitialXP();

        this.setXP(-Math.abs(expLeft));

        int expDown = -(expMax);
        if (this.getXP() <= expDown) {
            if (this.getLevel() == 1) {
                this.setXP(-this.getXPToLevelDown());
            }
            else {
                this.downLevel((this.getXP() - expDown));
            }
        }
    }

    @NonNull
    @Deprecated
    public Template getConfig() {
        return this.getTemplate();
    }

    @NonNull
    public Template getTemplate() {
        return this.template;
    }

    @NonNull
    public Tier getTier() {
        return this.tier;
    }

    //    public void setTier(@NonNull Tier tier) {
    //        this.tier = tier;
    //    }

    public boolean isDead() {
        return this.getHealth() <= 0D;
    }

    public boolean isAlive() {
        return !this.isDead();
    }

    @NonNull
    public String getName() {
        return this.name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    public boolean isSilent() {
        return silent;
    }

    public void setSilent(boolean silent) {
        this.silent = silent;
    }

    public long getReviveDate() {
        return reviveDate;
    }

    public void setReviveDate(long reviveDate) {
        this.reviveDate = reviveDate;
    }

    public boolean isReviveTime() {
        return this.isDead() && this.isAutoRevivable() && System.currentTimeMillis() >= this.getReviveDate();
    }

    public boolean isAutoRevivable() {
        return this.getReviveDate() >= 0L;
    }

    public boolean isManualRevive() {
        return this.getReviveDate() < 0L;
    }

    public double getHealth() {
        return this.health;
    }

    public void setHealth(double health) {
        this.health = Math.max(0D, health);
    }

    public double getFoodLevel() {
        return this.foodLevel;
    }

    public void setFoodLevel(double foodLevel) {
        this.foodLevel = Math.max(0D, foodLevel);
    }

    public int getLevel() {
        return this.level;
    }

    public int getMaxLevel() {
        return this.getTier().getMaxLevel();
    }

    public void setLevel(int level) {
        this.level = Math.max(1, level);
    }

    public int getXP() {
        return this.xp;
    }

    public void setXP(int xp) {
        this.xp = xp;
    }

    public int getRequiredXP() {
        return this.getTier().getRequiredXP(this.getLevel());
    }

    public int getXPToLevelUp() {
        return this.getRequiredXP() - this.getXP();
    }

    public int getXPToLevelDown() {
        return this.getXP() + this.getRequiredXP();
    }

    public int getAspectPoints() {
        return this.aspectPoints;
    }

    public void setAspectPoints(int amount) {
        this.aspectPoints = Math.max(0, amount);
    }

    public void addAspectPoints(int points) {
        this.setAspectPoints(this.getAspectPoints() + Math.abs(points));
    }

    public void removeAspectPoints(int points) {
        this.setAspectPoints(this.getAspectPoints() - Math.abs(points));
    }

    @NonNull
    public Map<String, Integer> getAspects() {
        return this.aspects;
    }

    public void setAspects(@NonNull Map<String, Integer> aspects) {
        this.aspects = aspects;
    }

    public int getAspect(@NonNull Aspect aspect) {
        return this.getAspect(aspect.getId());
    }

    public int getAspect(@NonNull String aspectId) {
        return this.getAspects().getOrDefault(aspectId.toLowerCase(), 0);
    }

    public void addAspect(@NonNull Aspect aspect, int amount) {
        int set = this.getAspect(aspect) + amount;
        int max = this.getTier().getAspectMax(aspect);
        this.setAspect(aspect, Math.min(set, max));
    }

    public void setAspect(@NonNull Aspect aspect, int value) {
        this.setAspect(aspect.getId(), value);
    }

    public void setAspect(@NonNull String aspectId, int value) {
        this.getAspects().put(aspectId.toLowerCase(), Math.max(0, value));
    }

    public double getAttributeValue(@NonNull Stat attribute) {
        return this.getAttributeValue(attribute.getId());
    }

    public double getAttributeValue(@NonNull String attributeName) {
        double value = this.getConfig().getAttributeStart(attributeName);

        for (Aspect aspect : PetAPI.getPetManager().getAspects()) {
            if (aspect.getAttributes().contains(attributeName)) {
                int aspectValue = this.getAspect(aspect);
                double aspectModifier = this.getConfig().getAttributePerAspect(attributeName);
                double total = aspectModifier * aspectValue;

                value += total;
            }
        }

        return value;
    }

    @NonNull
    public List<ItemStack> getInventory() {
        return this.inventory;
    }

    public void setInventory(@NonNull ItemStack[] inventory) {
        this.setInventory(new ArrayList<>(Arrays.asList(inventory)));
    }

    public void setInventory(@NonNull List<ItemStack> inventory) {
        inventory.removeIf(Objects::isNull);
        this.inventory = inventory;
    }

    @NonNull
    public Map<EquipmentSlot, ItemStack> getEquipment() {
        return this.equipment;
    }

    public void setEquipment(@NonNull Map<EquipmentSlot, ItemStack> equipment) {
        this.equipment = new HashMap<>();
        equipment.forEach(this::setEquipment);
    }

    @NonNull
    public ItemStack getEquipment(@NonNull EquipmentSlot type) {
        return this.equipment.computeIfAbsent(type, item -> new ItemStack(Material.AIR));
    }

    public void setEquipment(@NonNull EquipmentSlot type, @Nullable ItemStack item) {
        this.equipment.put(type, item == null ? new ItemStack(Material.AIR) : item);
    }

    @NonNull
    public CombatMode getCombatMode() {
        return this.combatMode;
    }

    public void setCombatMode(@NonNull CombatMode combatMode) {
        this.combatMode = combatMode;
    }

    @NonNull
    public Wardrobe getWardrobe() {
        return this.wardrobe;
    }

    public void setWardrobe(@NonNull PetWardrobe wardrobe) {
        this.wardrobe = wardrobe;
    }
}
