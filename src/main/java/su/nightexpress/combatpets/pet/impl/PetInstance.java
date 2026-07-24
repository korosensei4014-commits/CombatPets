package su.nightexpress.combatpets.pet.impl;

import org.bukkit.Particle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.Breedable;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Phantom;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Tameable;
import org.bukkit.entity.Zombie;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

import io.github.arcaneplugins.levelledmobs.libs.jetbrains.annotations.Nullable;
import su.nightexpress.combatpets.PetsPlugin;
import su.nightexpress.combatpets.Placeholders;
import su.nightexpress.combatpets.api.pet.ActivePet;
import su.nightexpress.combatpets.api.pet.Aspect;
import su.nightexpress.combatpets.api.pet.FoodItem;
import su.nightexpress.combatpets.api.pet.PetEntityBridge;
import su.nightexpress.combatpets.api.pet.PetSharedData;
import su.nightexpress.combatpets.api.pet.Stat;
import su.nightexpress.combatpets.api.pet.Template;
import su.nightexpress.combatpets.api.pet.Tier;
import su.nightexpress.combatpets.api.pet.event.PetLevelDownEvent;
import su.nightexpress.combatpets.api.pet.event.PetLevelUpEvent;
import su.nightexpress.combatpets.api.pet.type.CombatMode;
import su.nightexpress.combatpets.api.pet.type.ExhaustReason;
import su.nightexpress.combatpets.config.Config;
import su.nightexpress.combatpets.config.Lang;
import su.nightexpress.combatpets.data.impl.PetData;
import su.nightexpress.combatpets.level.LevelingConfig;
import su.nightexpress.combatpets.pet.AttributeRegistry;
import su.nightexpress.combatpets.util.PetUtils;
import su.nightexpress.combatpets.wardrobe.PetWardrobe;
import su.nightexpress.nightcore.menu.api.Menu;
import su.nightexpress.nightcore.menu.impl.AbstractMenu;
import su.nightexpress.nightcore.util.Lists;
import su.nightexpress.nightcore.util.NumberUtil;
import su.nightexpress.nightcore.util.TimeUtil;
import su.nightexpress.nightcore.util.Version;
import su.nightexpress.nightcore.util.placeholder.PlaceholderMap;
import su.nightexpress.nightcore.util.text.NightMessage;
import su.nightexpress.nightcore.util.wrapper.UniParticle;

@NullMarked
public final class PetInstance implements ActivePet {

    private final PetsPlugin     plugin;
    private final PetSharedData  sharedData;
    private final LivingEntity   entity;
    private final PetData        data;
    private final Inventory      inventory;
    private final PlaceholderMap placeholderMap;

    private boolean equipmentUnlocked;
    private long    nextRegenTime;
    private long    regenCooldownDate;
    private BossBar healthBar;

    //private DynamicEntity dynamicEntity;

    public PetInstance(PetsPlugin plugin, PetSharedData sharedData, LivingEntity entity,
                       PetData data) {
        this.plugin = plugin;
        this.sharedData = sharedData;
        this.entity = entity;
        this.data = data;

        int inventorySize = this.getTier().hasInventory() ? this.getTier().getInventorySize() : 9;
        this.inventory = this.plugin.getServer().createInventory(this, inventorySize, NightMessage.asLegacy(this
            .getName()));
        this.placeholderMap = Placeholders.forHolder(this);

        //this.dynamicEntity = DynamicEntity.create("robot_01", this.entity);
        //this.dynamicEntity.setName(this.petData.getName());
        //this.dynamicEntity.setNameVisible(true);
    }

    @Override

    public PlaceholderMap getPlaceholders() {
        return this.placeholderMap;
    }

    @Override
    public @Nullable Player getOwner() {
        return this.sharedData.getOwner();
    }

    @Override
    public PetSharedData getSharedData() {
        return this.sharedData;
    }

    @Override
    public boolean isOwner(Player player) {
        return this.getOwner().getUniqueId().equals(player.getUniqueId());
    }

    @Override

    public LivingEntity getEntity() {
        return this.entity;
    }

    @Override
    public void openMenu() {
        this.plugin.getPetManager().openOverviewMenu(this.getOwner());
    }

    @Override

    public CombatMode getCombatMode() {
        return this.data.getCombatMode();
    }

    @Override

    public Inventory getInventory() {
        return this.inventory;
    }

    @Override
    public void setName(String name) {
        this.data.setName(name);
        this.updateName();
    }

    @Override

    public String getName() {
        return this.data.getName();
    }

    @Override
    public void setSilent(boolean silent) {
        this.data.setSilent(silent);
        this.entity.setSilent(silent);
    }

    @Override
    public boolean isSilent() {
        return this.data.isSilent();
    }

    @Override
    public void tickPet() {
        if (!this.entity.isValid()) {
            this.remove();
            return;
        }

        if (!PetUtils.isAllowedPetZone(this.entity.getLocation())) {
            Lang.PET_ERROR_BAD_WORLD.message().send(this.getOwner());
            this.plugin.getPetManager().removePetAndSave(this);
            return;
        }

        if (this.entity.getNoDamageTicks() <= 0) {
            if (this.plugin.getPetNMS().hasNavigationPath(this.entity)) {
                this.doExhaust(ExhaustReason.WALK);
            }
            else {
                this.doExhaust(ExhaustReason.IDLE);
            }
        }
        this.autoFood();
        this.updateHealthBar();
    }

    private void autoFood() {
        if (!Config.PET_AUTO_FOOD_ENABLED.get()) return;
        if (!this.getTier().hasInventory()) return;

        double maxSaturation = this.getMaxSaturation();
        if (maxSaturation <= 0D) return;

        double foodLevel = this.data.getFoodLevel();
        double percent = (foodLevel / maxSaturation) * 100D;
        if (percent > Config.PET_AUTO_FOOD_PERCENT.get()) return;

        for (ItemStack item : this.getInventory().getContents()) {
            if (item == null || item.getType().isAir()) continue;

            FoodItem foodItem = this.plugin.getPetManager().getFoodItem(item);
            if (foodItem == null || !this.getTemplate().isFood(foodItem)) continue;

            if (this.doFeed(foodItem)) {
                item.setAmount(item.getAmount() - 1);
                break;
            }
        }
    }

    @Override
    public void remove() {
        this.removeHealthBar();

        // Close owner's inventory if it's custom engine GUI.
        Menu menu = AbstractMenu.getMenu(this.getOwner());
        if (menu != null) {
            this.getOwner().closeInventory();
        }

        this.saveData(); // Save pet's settings to the data holder.
        this.getInventory().clear(); // Clear pet's inventory content.

        // Keep death animation
        if (!this.entity.isDead()) {
            this.getTemplate().getDespawnParticle().play(this.entity.getEyeLocation(), 0.5, 0.1, 25);
            this.entity.remove();
        }

        PetEntityBridge.removeHolder(this);
    }

    @Override
    public void handleDeath() {
        if (Config.isLevelingEnabled()) {
            double lossAmount = LevelingConfig.DEATH_XP_LOSS.get();
            if (this.canLossXP() && lossAmount > 0) {
                double percent = lossAmount / 100D;
                double xpRequired = this.getRequiredXP();
                int toLoss = (int) (xpRequired * percent);

                if (toLoss > 0) {
                    this.removeXP(toLoss);
                    Lang.LEVELING_XP_LOSE_DEATH.message().send(this.getOwner(), replacer -> replacer.replace(
                        Placeholders.GENERIC_AMOUNT, NumberUtil.format(toLoss)));
                }
            }
        }

        this.remove();
        this.data.setHealth(0D);
        this.data.setReviveDate(PetUtils.createTimestamp(this.getTier().getAutoRespawnTime()));
        Lang.PET_DESPAWN_DEATH.message().send(this.getOwner());
    }

    @Override
    public void handleSpawn() {
        if (this.getTemplate().canHaveInventory() && this.getTier().hasInventory()) {
            this.getInventory().setContents(this.data.getInventory().toArray(new ItemStack[0]));
        }
        if (this.getTemplate().canHaveEquipment() && this.getTier().hasEquipment()) {
            this.equipPet();
        }
        this.data.getWardrobe().dressUp(this.entity);
        this.updateAttributes();

        this.entity.setHealth(Math.min(this.data.getHealth(), this.getMaxHealth()));
        this.entity.setSilent(this.data.isSilent());
        this.entity.setPersistent(true);
        this.entity.setRemoveWhenFarAway(false);
        this.entity.setCanPickupItems(false);
        if (this.entity instanceof Breedable breedable) {
            breedable.setAgeLock(true);
        }
        if (this.entity instanceof Tameable tameable) {
            tameable.setTamed(true);
            tameable.setOwner(this.getOwner());
        }
        if (this.entity instanceof AbstractHorse) {
            this.plugin.getPetNMS().setSaddle(this.entity);
        }
        if (Version.isPaper()) {
            switch (this.entity) {
                case Skeleton skeleton -> skeleton.setShouldBurnInDay(false);
                case Zombie zombie -> zombie.setShouldBurnInDay(false);
                case Phantom phantom -> phantom.setShouldBurnInDay(false);
                default -> {
                }
            }
        }

        this.updateHealthBar();
        this.updateName();
        this.getTemplate().getSpawnParticle().play(entity.getEyeLocation(), 0.5, 0.1, 25);
    }

    @Override
    public void onIncomingDamage() {
        this.regenCooldownDate = TimeUtil.createFutureTimestamp(Config.PET_DAMAGE_REGEN_COOLDOWN.get());
    }

    @Override
    public void onLevelUp() {
        PetLevelUpEvent event = new PetLevelUpEvent(this);
        this.plugin.getPluginManager().callEvent(event);

        this.update();

        Lang.LEVELING_LEVEL_UP.message().send(this.getOwner(), replacer -> replacer.replace(this.data
            .replacePlaceholders()));
    }

    @Override
    public void onLevelDowngrade() {
        PetLevelDownEvent event = new PetLevelDownEvent(this);
        this.plugin.getPluginManager().callEvent(event);

        this.update();

        Lang.LEVELING_LEVEL_DOWN.message().send(this.getOwner(), replacer -> replacer.replace(this.data
            .replacePlaceholders()));
    }

    @Override
    public void saveData() {
        // Update customizer from entity's settings.
        this.data.setWardrobe(PetWardrobe.of(this.entity));

        // Update inventory content to pet data.
        if (this.getTemplate().canHaveInventory() && this.getTier().hasInventory()) {
            this.data.setInventory(this.getInventory().getContents());
        }

        // Update equipment to pet data.
        if (this.getTemplate().canHaveEquipment() && this.getTier().hasEquipment()) {
            EntityEquipment equipment = this.entity.getEquipment();
            if (equipment != null) {
                for (EquipmentSlot slot : EquipmentSlot.values()) {
                    this.data.setEquipment(slot, equipment.getItem(slot));
                }
            }
        }

        // Update pet's health.
        this.data.setHealth(this.entity.getHealth());
    }

    @Override
    public void update() {
        this.updateAttributes();
        this.updateHealthBar();
        this.updateName();
    }

    @Override
    public void updateAttributes() {
        Template config = this.getTemplate();

        AttributeRegistry.stream().forEach(attribute -> {
            if (!config.hasAttribute(attribute)) return;

            double value = this.data.getAttributeValue(attribute);
            PetUtils.setAttribute(this.entity, attribute, value);
        });
    }

    @Override
    public void updateName() {
        Tier tier = this.getTier();
        String format = this.replacePlaceholders().apply(tier.getNameFormat());

        this.entity.setCustomName(NightMessage.asLegacy(format));
        this.entity.setCustomNameVisible(true);
    }

    @Override
    public void updateHealthBar() {
        if (!Config.PET_HEALTHBAR_ENABLED.get()) return;
        if (this.entity.isDead() || !this.entity.isValid()) return;

        if (this.healthBar == null) {
            this.healthBar = this.plugin.getServer().createBossBar("", Config.PET_HEALTHBAR_COLOR.get(),
                Config.PET_HEALTHBAR_STYLE.get());
            this.healthBar.setVisible(true);
            this.healthBar.addPlayer(this.getOwner());
        }

        double maxHealth = this.getMaxHealth();
        double result = Math.max(0, this.entity.getHealth() / maxHealth);
        String title = this.replacePlaceholders().apply(Config.PET_HEALTHBAR_TITLE.get());

        this.healthBar.setProgress(result);
        this.healthBar.setTitle(NightMessage.asLegacy(title));
    }

    @Override
    public void removeHealthBar() {
        if (this.healthBar == null) return;

        this.healthBar.removeAll();
        this.healthBar = null;
    }


    public Tier getTier() {
        return this.data.getTier();
    }

    @Override
    public Template getTemplate() {
        return this.data.getTemplate();
    }


    public PetData getData() {
        return this.data;
    }

    @Override
    public boolean isEquipmentUnlocked() {
        return equipmentUnlocked;
    }

    @Override
    public void setEquipmentUnlocked(boolean equipmentUnlocked) {
        this.equipmentUnlocked = equipmentUnlocked;
    }

    @Override
    public double getAttribute(String name) {
        Stat attribute = AttributeRegistry.getById(name);
        return attribute == null ? 0D : this.getAttribute(attribute);
    }

    @Override
    public double getAttribute(Stat attribute) {
        return PetUtils.getAttribute(this.entity, attribute);
    }

    @Override
    public double getAttackDamage() {
        return this.getAttribute(AttributeRegistry.ATTACK_DAMAGE);
    }

    @Override
    public double getMaxSaturation() {
        return this.getAttribute(AttributeRegistry.MAX_SATURATION);
    }

    @Override
    public double getMaxHealth() {
        return this.getAttribute(AttributeRegistry.MAX_HEALTH);
    }

    @Override
    public double getRegenerationForce() {
        return this.getAttribute(AttributeRegistry.HEALTH_REGENEATION_FORCE);
    }

    @Override
    public double getRegenerationSpeed() {
        return this.getAttribute(AttributeRegistry.HEALTH_REGENEATION_SPEED);
    }

    @Override
    public boolean doRegenerate(EntityRegainHealthEvent.RegainReason reason) {
        if (reason == EntityRegainHealthEvent.RegainReason.REGEN) {
            if (!TimeUtil.isPassed(this.regenCooldownDate)) return false;
            if (!TimeUtil.isPassed(this.nextRegenTime)) return false;
        }

        double healthMax = this.getMaxHealth();
        double healthHas = this.entity.getHealth();
        if (healthHas >= healthMax) return false;

        if (this.getMaxSaturation() > 0D) {
            double saturation = this.data.getFoodLevel();
            double saturationMod = saturation / this.getMaxSaturation() * 100D;
            if (saturationMod < Config.PET_SATURATION_PERCENT_TO_REGEN.get()) return false;
        }

        double regenAmount = this.getRegenerationForce();

        EntityRegainHealthEvent event = new EntityRegainHealthEvent(this.entity, regenAmount, reason);
        this.plugin.getPluginManager().callEvent(event);
        if (event.isCancelled() || event.getAmount() <= 0D) return false;

        this.doRegenerate(event.getAmount());

        if (reason == EntityRegainHealthEvent.RegainReason.REGEN) {
            this.nextRegenTime = TimeUtil.createFutureTimestamp(this.getRegenerationSpeed());
        }
        return true;
    }

    @Override
    public void doRegenerate(double amount) {
        double maxHealth = this.getMaxHealth();
        double currentHealth = this.entity.getHealth();

        double healthFinal = Math.max(0, Math.min(maxHealth, currentHealth + amount));
        this.entity.setHealth(healthFinal);
        this.data.setHealth(this.entity.getHealth());

        this.updateHealthBar();
        this.updateName();
    }

    @Override
    public void doExhaust(ExhaustReason reason) {
        this.doExhaust(this.getTemplate().getExhaustModifier(reason));
    }

    @Override
    public void doExhaust(double amount) {
        this.data.setFoodLevel(Math.max(0, this.data.getFoodLevel() - amount));
    }

    @Override
    public boolean doFeed(FoodItem foodItem) {
        double maxSaturation = this.getMaxSaturation();
        if (maxSaturation <= 0D) return false;

        double saturation = this.data.getFoodLevel();
        if (saturation >= maxSaturation) return false;

        this.doFeed(foodItem.getSaturation());

        if (Version.isAtLeast(Version.MC_1_21)) {
            UniParticle.of(Particle.ITEM, foodItem.getItem()).play(this.entity.getEyeLocation(), 0.25, 0.1, 15);
        }
        return true;
    }

    @Override
    public void doFeed(double amount) {
        double saturation = this.data.getFoodLevel();
        this.data.setFoodLevel(Math.min(this.getMaxSaturation(), saturation + amount));
    }

    @Override
    public void moveToOwner() {
        this.entity.teleport(this.getOwner().getLocation());
    }

    public void resetLeveling() {
        this.data.resetXP();
        this.update();
    }

    @Override
    public int getAspectPoints() {
        return this.data.getAspectPoints();
    }

    @Override
    public void addAspectPoints(int amount) {
        this.data.addAspectPoints(amount);
    }

    @Override
    public void setAspectPoints(int amount) {
        this.data.setAspectPoints(amount);
    }

    @Override
    public void removeAspectPoints(int amount) {
        this.data.removeAspectPoints(amount);
    }

    @Override
    public int getAspectValue(Aspect aspect) {
        return this.data.getAspect(aspect);
    }

    @Override
    public void setAspectValue(Aspect aspect, int value) {
        this.data.setAspect(aspect, value);
    }

    @Override
    public void reallocateAspects() {
        this.plugin.getPetManager().getAspects().forEach(aspect -> {
            int has = this.getAspectValue(aspect);
            this.setAspectPoints(this.getAspectPoints() + has);
            this.setAspectValue(aspect, 0);
        });
    }

    @Override
    public int getXP() {
        return this.data.getXP();
    }

    @Override
    public int getRequiredXP() {
        return this.data.getRequiredXP();
    }

    @Override
    public boolean canGainXP() {
        if (!Config.isLevelingEnabled()) return false;
        if (!this.isMaxLevel()) return true;

        return this.getXP() < this.getRequiredXP();
    }

    @Override
    public boolean canLossXP() {
        if (!Config.isLevelingEnabled()) return false;
        if (this.getLevel() == 1) {
            if (!LevelingConfig.ALLOW_DOWNGRADE.get()) {
                return this.getXP() <= 0;
            }
        }
        return this.getXP() > 0 || (this.getXP() < 0 && Math.abs(this.getXP()) < this.getRequiredXP());
    }

    @Override
    public void addXP(int amount) {
        if (amount <= 0 || !Config.isLevelingEnabled()) return;

        int oldLevel = this.data.getLevel();
        this.data.addXP(amount);
        int newLevel = this.data.getLevel();

        if (newLevel > oldLevel) {
            this.onLevelUp();
        }
        else {
            this.updateHealthBar();
            this.updateName();
        }
    }

    @Override
    public void removeXP(int amount) {
        if (amount < 0 || !Config.isLevelingEnabled()) return;
        if (!LevelingConfig.ALLOW_DOWNGRADE.get() && amount > this.getXP()) {
            amount = Math.min(this.getXP(), amount);
        }
        if (amount == 0) return;

        int oldLevel = this.data.getLevel();
        this.data.removeXP(amount);
        int newLevel = this.data.getLevel();

        if (newLevel < oldLevel) {
            this.onLevelDowngrade();
        }
        else {
            this.updateHealthBar();
            this.updateName();
        }
    }

    @Override
    public void setXP(int amount) {
        if (!Config.isLevelingEnabled()) return;

        this.data.setXP(0);

        if (amount > 0) {
            this.addXP(amount);
        }
        else {
            this.removeXP(amount);
        }
    }

    @Override
    public int getLevel() {
        return this.data.getLevel();
    }

    @Override
    public boolean isMaxLevel() {
        return this.getLevel() >= this.getTier().getMaxLevel();
    }

    @Deprecated
    public void addLevel(int amount) {
        if (!Config.isLevelingEnabled()) return;

        for (int count = 0; count < amount; count++) {
            this.data.upLevel(0);
            this.onLevelUp();
        }
    }

    public void equipPet() {
        EntityEquipment equipment = this.entity.getEquipment();
        if (equipment == null) return;

        equipment.clear();

        this.data.getEquipment().forEach(equipment::setItem);
    }

    public void toggleCombatMode() {
        this.data.setCombatMode(Lists.next(this.data.getCombatMode()));
    }
}
