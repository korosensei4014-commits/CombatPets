package su.nightexpress.combatpets.pet;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import su.nightexpress.combatpets.PetsPlugin;
import su.nightexpress.combatpets.Placeholders;
import su.nightexpress.combatpets.api.pet.ActivePet;
import su.nightexpress.combatpets.api.pet.Aspect;
import su.nightexpress.combatpets.api.pet.FoodCategory;
import su.nightexpress.combatpets.api.pet.FoodItem;
import su.nightexpress.combatpets.api.pet.PetEntityBridge;
import su.nightexpress.combatpets.api.pet.PetFollowSettings;
import su.nightexpress.combatpets.api.pet.Template;
import su.nightexpress.combatpets.api.pet.Tier;
import su.nightexpress.combatpets.api.pet.event.generic.PetReleaseEvent;
import su.nightexpress.combatpets.api.pet.type.CombatMode;
import su.nightexpress.combatpets.bedrock.form.PetForms;
import su.nightexpress.combatpets.pet.dialog.PetRenameDialog;
import su.nightexpress.combatpets.config.Config;
import su.nightexpress.combatpets.config.Lang;
import su.nightexpress.combatpets.config.Perms;
import su.nightexpress.combatpets.data.impl.PetData;
import su.nightexpress.combatpets.data.impl.PetUser;
import su.nightexpress.combatpets.hook.HookId;
import su.nightexpress.combatpets.pet.impl.PetAspect;
import su.nightexpress.combatpets.pet.impl.PetAttribute;
import su.nightexpress.combatpets.pet.impl.PetFoodCategory;
import su.nightexpress.combatpets.pet.impl.PetInstance;
import su.nightexpress.combatpets.pet.impl.PetSharedImpl;
import su.nightexpress.combatpets.pet.impl.PetTemplate;
import su.nightexpress.combatpets.pet.impl.PetTier;
import su.nightexpress.combatpets.pet.listener.CombatListener;
import su.nightexpress.combatpets.pet.listener.LevelledMobsListener;
import su.nightexpress.combatpets.pet.listener.PetGenericListener;
import su.nightexpress.combatpets.pet.listener.PlayerGenericListener;
import su.nightexpress.combatpets.pet.menu.AspectsMenu;
import su.nightexpress.combatpets.pet.menu.CollectionMenu;
import su.nightexpress.combatpets.pet.menu.PetMenu;
import su.nightexpress.combatpets.pet.menu.ReleaseMenu;
import su.nightexpress.combatpets.pet.menu.ReviveMenu;
import su.nightexpress.combatpets.pet.menu.TiersMenu;
import su.nightexpress.combatpets.util.PetCreator;
import su.nightexpress.combatpets.util.PetUtils;
import su.nightexpress.nightcore.bridge.currency.Currency;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.core.config.CoreLang;
import su.nightexpress.nightcore.dialog.Dialog;
import su.nightexpress.nightcore.integration.currency.EconomyBridge;
import su.nightexpress.nightcore.manager.AbstractManager;
import su.nightexpress.nightcore.util.FileUtil;
import su.nightexpress.nightcore.util.ItemUtil;
import su.nightexpress.nightcore.util.Players;
import su.nightexpress.nightcore.util.Plugins;
import su.nightexpress.nightcore.util.bukkit.NightSound;
import su.nightexpress.nightcore.util.random.Rnd;
import su.nightexpress.nightcore.util.text.NightMessage;

public class PetManager extends AbstractManager<PetsPlugin> {

    private final Map<String, Tier>         tierMap;
    private final Map<String, Template>     templateMap;
    private final Map<String, Aspect>       aspectMap;
    private final Map<String, FoodCategory> foodMap;

    private TiersMenu      tiersMenu;
    private CollectionMenu collectionMenu;
    private PetMenu        petMenu;
    private AspectsMenu    aspectsMenu;
    private ReleaseMenu    releaseMenu;
    private ReviveMenu     reviveMenu;

    public PetManager(@NonNull PetsPlugin plugin) {
        super(plugin);
        this.tierMap = new HashMap<>();
        this.templateMap = new HashMap<>();
        this.aspectMap = new HashMap<>();
        this.foodMap = new HashMap<>();
    }

    @Override
    protected void onLoad() {
        PetFollowSettings.set(Config.PET_FOLLOW_START_DISTANCE.get(), Config.PET_FOLLOW_STOP_DISTANCE.get());

        this.loadAttributes();
        this.loadAspects();
        this.loadFood();
        this.loadTiers();
        this.loadPets();
        this.loadUI();

        this.addListener(new PetGenericListener(this.plugin, this));
        this.addListener(new CombatListener(this.plugin, this));
        this.addListener(new PlayerGenericListener(this.plugin, this));
        if (Plugins.isInstalled(HookId.LEVELLED_MOBS)) {
            this.addListener(new LevelledMobsListener(this.plugin));
        }

        this.addTask(this.plugin.createTask(this::tickPets).setSecondsInterval(1));
        this.addTask(this.plugin.createTask(this::regeneratePets).setTicksInterval(5L));
    }

    @Override
    protected void onShutdown() {
        PetManager.getActivePets().forEach(holder -> {
            this.removePet(holder);
            this.plugin.getDataHandler().saveUser(plugin.getUserManager().getOrFetch(holder.getOwner()));
        });

        if (this.collectionMenu != null) this.collectionMenu.clear();
        if (this.petMenu != null) this.petMenu.clear();
        if (this.aspectsMenu != null) this.aspectsMenu.clear();
        if (this.releaseMenu != null) this.releaseMenu.clear();
        if (this.reviveMenu != null) this.reviveMenu.clear();
        if (this.tiersMenu != null) this.tiersMenu.clear();

        this.tierMap.clear();
        this.templateMap.clear();
        this.aspectMap.clear();
        this.foodMap.clear();

        AttributeRegistry.clear();
    }

    private void loadTiers() {
        File dir = new File(plugin.getDataFolder() + Config.DIR_TIERS);
        if (!dir.exists() && dir.mkdirs()) {
            PetCreator.createTiers(this.plugin);
        }

        for (File file : FileUtil.getConfigFiles(plugin.getDataFolder() + Config.DIR_TIERS)) {
            Tier tier = new PetTier(this.plugin, file);
            if (tier.load()) {
                this.tierMap.put(tier.getId(), tier);
            }
            else this.plugin.warn("Tier not loaded: '" + file.getName() + "' !");
        }

        this.plugin.info("Loaded " + this.tierMap.size() + " pet tiers!");
    }

    private void loadPets() {
        File dir = new File(plugin.getDataFolder() + Config.DIR_PETS);
        if (!dir.exists() && dir.mkdirs()) {
            PetCreator.createConfigs(this.plugin);
        }

        for (File config : FileUtil.getConfigFiles(plugin.getDataFolder() + Config.DIR_PETS)) {
            Template petConfig = new PetTemplate(this.plugin, config);
            if (petConfig.load()) {
                this.templateMap.put(petConfig.getId(), petConfig);
            }
            else this.plugin.warn("Pet not loaded: '" + config.getName() + "' !");
        }

        this.plugin.info("Loaded " + this.templateMap.size() + " pet configs!");
    }

    private void loadFood() {
        FileConfig config = this.plugin.getConfig();
        if (!config.contains("Food")) {
            PetCreator.getDefaultFoods().forEach(category -> category.write(config, "Food." + category.getId()));
        }

        config.getSection("Food").forEach(sId -> {
            PetFoodCategory category = PetFoodCategory.read(config, "Food." + sId, sId);
            this.foodMap.put(category.getId(), category);
        });

        this.plugin.info("Loaded " + this.foodMap.size() + " food categories.");
    }

    private void loadAspects() {
        FileConfig config = this.plugin.getConfig();
        if (!config.contains("Aspects")) {
            PetCreator.getDefaultAspects().forEach(aspect -> aspect.write(config, "Aspects." + aspect.getId()));
        }

        config.getSection("Aspects").forEach(sId -> {
            Aspect aspect = PetAspect.read(config, "Aspects." + sId, sId);
            this.aspectMap.put(aspect.getId(), aspect);
        });

        this.plugin.info("Loaded " + this.aspectMap.size() + " aspects.");
    }

    private void loadAttributes() {
        this.loadAttribute(AttributeRegistry.ARMOR, Attribute.ARMOR);
        this.loadAttribute(AttributeRegistry.ATTACK_DAMAGE, Attribute.ATTACK_DAMAGE);
        this.loadAttribute(AttributeRegistry.ATTACK_KNOCKBACK, Attribute.ATTACK_KNOCKBACK);
        this.loadAttribute(AttributeRegistry.ATTACK_SPEED, Attribute.ATTACK_SPEED);
        this.loadAttribute(AttributeRegistry.FLYING_SPEED, Attribute.FLYING_SPEED);
        this.loadAttribute(AttributeRegistry.HEALTH_REGENEATION_FORCE);
        this.loadAttribute(AttributeRegistry.HEALTH_REGENEATION_SPEED);
        this.loadAttribute(AttributeRegistry.HORSE_JUMP_STRENGTH, Attribute.JUMP_STRENGTH);
        this.loadAttribute(AttributeRegistry.KNOCKBACK_RESISTANCE, Attribute.KNOCKBACK_RESISTANCE);
        this.loadAttribute(AttributeRegistry.MAX_HEALTH, Attribute.MAX_HEALTH);
        this.loadAttribute(AttributeRegistry.MAX_SATURATION);
        this.loadAttribute(AttributeRegistry.MOVEMENT_SPEED, Attribute.MOVEMENT_SPEED);
        //this.loadAttribute(AttributeRegistry.ATTACK_REACH, Attribute.valueOf("GENERIC_ATTACK_REACH"));
        this.loadAttribute(AttributeRegistry.BURNING_TIME, Attribute.BURNING_TIME);
        this.loadAttribute(AttributeRegistry.EXPLOSION_KNOCKBACK_RESISTANCE, Attribute.EXPLOSION_KNOCKBACK_RESISTANCE);
        this.loadAttribute(AttributeRegistry.FALL_DAMAGE_MULTIPLIER, Attribute.FALL_DAMAGE_MULTIPLIER);
        this.loadAttribute(AttributeRegistry.GRAVITY, Attribute.GRAVITY);
        this.loadAttribute(AttributeRegistry.MOVEMENT_EFFICIENCY, Attribute.MOVEMENT_EFFICIENCY);
        this.loadAttribute(AttributeRegistry.SAFE_FALL_DISTANCE, Attribute.SAFE_FALL_DISTANCE);
        this.loadAttribute(AttributeRegistry.SCALE, Attribute.SCALE);
        this.loadAttribute(AttributeRegistry.STEP_HEIGHT, Attribute.STEP_HEIGHT);
        this.loadAttribute(AttributeRegistry.WATER_MOVEMENT_EFFICIENCY, Attribute.WATER_MOVEMENT_EFFICIENCY);
    }

    private void loadAttribute(@NonNull String name) {
        NamespacedKey key = new NamespacedKey(this.plugin, "attribute." + name.toLowerCase());
        this.loadAttribute(new PetAttribute(name, key));
    }

    private void loadAttribute(@NonNull String name, @NonNull Attribute vanillaMirror) {
        this.loadAttribute(new PetAttribute(name, vanillaMirror));
    }

    private void loadAttribute(@NonNull PetAttribute attribute) {
        FileConfig config = this.plugin.getConfig();

        attribute.read(config, "Attributes." + attribute.getId());

        AttributeRegistry.register(attribute);
        this.plugin.info("Registered '" + attribute.getId() + "' pet attribute.");
    }

    private void loadUI() {
        this.collectionMenu = new CollectionMenu(this.plugin);
        this.tiersMenu = new TiersMenu(this.plugin);
        this.petMenu = new PetMenu(this.plugin);
        this.aspectsMenu = new AspectsMenu(this.plugin);
        this.releaseMenu = new ReleaseMenu(this.plugin);
        this.reviveMenu = new ReviveMenu(this.plugin);
    }

    public void regeneratePets() {
        getActivePets().forEach(holder -> {
            holder.doRegenerate(EntityRegainHealthEvent.RegainReason.REGEN);
        });
    }

    public void tickPets() {
        getActivePets().forEach(ActivePet::tickPet);
    }

    @NonNull
    public Map<String, Tier> getTierMap() {
        return this.tierMap;
    }

    @NonNull
    public Set<Tier> getTiers() {
        return new HashSet<>(this.tierMap.values());
    }

    @NonNull
    public List<String> getTierIds() {
        return new ArrayList<>(this.tierMap.keySet());
    }

    @Nullable
    public Tier getTier(@NonNull String id) {
        return this.tierMap.get(id.toLowerCase());
    }

    @NonNull
    public Tier getTierByWeight() {
        Map<Tier, Double> map = new HashMap<>();
        this.getTiers().forEach(tier -> {
            map.put(tier, tier.getWeight());
        });
        return Rnd.getByWeight(map);
    }

    @NonNull
    public Map<String, Template> getTemplateMap() {
        return this.templateMap;
    }

    @NonNull
    public Set<Template> getTemplates() {
        return new HashSet<>(this.templateMap.values());
    }

    @NonNull
    public List<String> getTemplateIds() {
        return new ArrayList<>(this.templateMap.keySet());
    }

    @Nullable
    public Template getTemplate(@NonNull String id) {
        return this.templateMap.get(id.toLowerCase());
    }

    @Nullable
    public Template getTemplate(@NonNull LivingEntity entity) {
        return this.getTemplates().stream()
            .filter(config -> config.getEntityType() == entity.getType()).findAny().orElse(null);
    }

    @NonNull
    public Set<Aspect> getAspects() {
        return new HashSet<>(this.aspectMap.values());
    }

    @Nullable
    public Aspect getAspect(@NonNull String id) {
        return this.aspectMap.get(id.toLowerCase());
    }

    @Nullable
    public FoodItem getFoodItem(@NonNull String categoryName, @NonNull String itemName) {
        FoodCategory category = this.getFoodCategory(categoryName);
        if (category == null) return null;

        return category.getItem(itemName.toLowerCase());
    }

    @Nullable
    public FoodItem getFoodItem(@NonNull ItemStack itemStack) {
        return this.getFoodCategories().stream().map(category -> category.getItem(itemStack)).filter(Objects::nonNull)
            .findFirst().orElse(null);
    }

    @Nullable
    public FoodCategory getFoodCategory(@NonNull String id) {
        return this.foodMap.get(id.toLowerCase());
    }

    @NonNull
    public Set<FoodCategory> getFoodCategories() {
        return new HashSet<>(this.foodMap.values());
    }

    @NonNull
    public Set<FoodItem> getFoodItems() {
        return this.getFoodCategories().stream().flatMap(category -> category.getItems().stream()).collect(Collectors
            .toSet());
    }

    @NonNull
    public List<String> getFoodCategoryNames() {
        return new ArrayList<>(this.foodMap.keySet());
    }

    @NonNull
    public static Collection<ActivePet> getActivePets() {
        return PetEntityBridge.getAll();
    }

    @Nullable
    public ActivePet getPlayerPet(@NonNull Player player) {
        return PetEntityBridge.getByPlayer(player);
    }

    @Nullable
    public ActivePet getPetByMob(@NonNull LivingEntity entity) {
        return PetEntityBridge.getByMob(entity);
    }

    public boolean isPetEntity(@NonNull LivingEntity entity) {
        return PetEntityBridge.isPet(entity);
    }

    public boolean hasActivePet(@NonNull Player player) {
        return this.getPlayerPet(player) != null;
    }


    /**
     * Only call after {@link PetsPlugin#useBedrockForms(Player)} returned true.
     */
    @NonNull
    private PetForms bedrockForms() {
        return Objects.requireNonNull(this.plugin.getBedrockManager()).getPetForms();
    }

    public void openTierCollection(@NonNull Player player) {
        if (!plugin.getUserManager().getOrFetch(player).isLoaded()) return;
        if (this.plugin.useBedrockForms(player) && this.bedrockForms().openTiers(player)) return;

        this.tiersMenu.open(player);
    }

    public void openPetsCollection(@NonNull Player player, @NonNull Tier tier) {
        if (!plugin.getUserManager().getOrFetch(player).isLoaded()) return;
        if (this.plugin.useBedrockForms(player) && this.bedrockForms().openCollection(player, tier, 0)) return;

        this.collectionMenu.open(player, tier);
    }

    public void openOverviewMenu(@NonNull Player player) {
        if (this.plugin.useBedrockForms(player) && this.bedrockForms().openPetMenu(player)) return;

        this.petMenu.open(player);
    }

    public void openAspectsMenu(@NonNull Player player) {
        if (this.plugin.useBedrockForms(player) && this.bedrockForms().openAspects(player)) return;

        this.aspectsMenu.open(player);
    }

    public void openReleaseMenu(@NonNull Player player, @NonNull PetData data) {
        if (this.plugin.useBedrockForms(player)) {
            Runnable toCollection = () -> this.openPetsCollection(player, data.getTier());
            if (this.bedrockForms().openReleaseConfirm(player, data, toCollection, toCollection)) return;
        }

        this.releaseMenu.open(player, data);
    }

    public void openReviveMenu(@NonNull Player player, @NonNull PetData data) {
        if (this.plugin.useBedrockForms(player)) {
            Runnable toCollection = () -> this.openPetsCollection(player, data.getTier());
            if (this.bedrockForms().openReviveConfirm(player, data, toCollection, toCollection)) return;
        }

        this.reviveMenu.open(player, data);
    }

    public boolean openPetMenu(@NonNull Player player) {
        ActivePet petHolder = getPlayerPet(player);
        if (petHolder == null) {
            Lang.PET_ERROR_NO_ACTIVE_PET.message().send(player);
            return false;
        }

        petHolder.openMenu();
        return true;
    }

    public static int getPetsLimit(@NonNull Player player) {
        return Config.PET_AMOUNT_PER_RANK.get().getGreatestOrNegative(player);
    }

    public boolean spawnPet(@NonNull Player player, @NonNull Tier tier, @NonNull Template config) {
        PetUser user = plugin.getUserManager().getOrFetch(player);
        PetData petData = user.getPet(config, tier);
        if (petData == null) return false;

        return this.spawnPet(player, petData);
    }

    public boolean spawnPet(@NonNull Player player, @NonNull PetData petData) {
        if (this.hasActivePet(player)) {
            Lang.PET_SPAWN_ERROR_ALREADY.message().send(player);
            return false;
        }
        if (petData.isDead()) {
            Lang.PET_SPAWN_ERROR_DEAD.message().send(player);
            return false;
        }
        if (!PetUtils.isAllowedPetZone(player.getLocation())) {
            Lang.PET_ERROR_BAD_WORLD.message().send(player);
            return false;
        }

        Location location = player.getLocation().clone();
        Vector direction = location.getDirection();
        location.add(0, 1, 0).add(direction.multiply(1.5));
        Block block = location.getBlock();
        if (!block.isEmpty() && block.getType().isSolid()) {
            Lang.PET_SPAWN_ERROR_BAD_PLACE.message().send(player);
            return false;
        }

        location.getChunk(); // Force load chunk before pet spawn.

        Template template = petData.getTemplate();

        PetSharedImpl sharedData = new PetSharedImpl(player.getUniqueId(), petData);

        ActivePet pet = plugin.getPetNMS().spawnPet(template, sharedData, location,
            entity -> new PetInstance(this.plugin, sharedData, entity, petData));
        pet.handleSpawn();

        return true;
    }

    public void handleDeath(@NonNull ActivePet holder) {
        Player player = holder.getOwner();
        PetUser user = this.plugin.getUserManager().getOrFetch(player);

        holder.handleDeath();

        this.plugin.getUserManager().save(user);
    }

    public void despawnPet(@NonNull Player player) {
        ActivePet holder = getPlayerPet(player);
        if (holder == null) return;

        this.despawnPet(holder);
    }

    public void despawnPet(@NonNull ActivePet holder) {
        this.removePetAndSave(holder);
        Lang.PET_DESPAWN_DEFAULT.message().send(holder.getOwner());
    }

    public void removePet(@NonNull ActivePet holder) {
        holder.remove();
    }

    public void removePetAndSave(@NonNull ActivePet holder) {
        this.removePet(holder);
        PetUser user = this.plugin.getUserManager().getOrFetch(holder.getOwner());
        this.plugin.getUserManager().save(user);
    }

    @NonNull
    public PetData addToCollection(@NonNull PetUser user, @NonNull Tier tier, @NonNull Template template) {
        PetData petData = PetData.create(template, tier);
        user.addPet(petData);
        this.plugin.getUserManager().save(user);

        return petData;
    }

    public void removeFromCollection(@NonNull PetUser user, @NonNull Tier tier, @NonNull Template template) {
        Player player = user.getPlayer();
        ActivePet activePet = player == null ? null : this.getPlayerPet(player);
        if (activePet != null && activePet.getTier() == tier && activePet.getTemplate() == template) {
            this.removePet(activePet);
        }
        user.removePet(template, tier);
        this.plugin.getUserManager().save(user);
    }

    @Nullable
    public PetData tryClaimPet(@NonNull Player player, @NonNull Tier tier, @NonNull Template config) {
        PetUser user = this.plugin.getUserManager().getOrFetch(player);
        if (!user.isLoaded()) return null;

        if (user.hasPet(config, tier)) {
            Lang.PET_CLAIM_ERROR_ALREADY_HAVE.message().send(player);
            return null;
        }

        int petLimit = PetManager.getPetsLimit(player);
        int petCollected = user.getPets(tier).size();
        if (petLimit > 0 && petCollected >= petLimit) {
            Lang.PET_CLAIM_ERROR_REACHED_LIMIT.message().send(player, replacer -> replacer
                .replace(tier.replacePlaceholders())
                .replace(Placeholders.GENERIC_AMOUNT, petLimit)
            );
            return null;
        }

        Lang.PET_CLAIM_SUCCESS.message().send(player);
        return this.addToCollection(user, tier, config);
    }

    public boolean revivePet(@NonNull Player player, @NonNull PetData petData) {
        Tier tier = petData.getTier();

        Currency currency = EconomyBridge.api().getCurrency(tier.getReviveCurrency());
        if (currency == null) return false;

        double cost = tier.getReviveCost();

        if (cost > 0) {
            if (currency.queryBalance(player) < cost) {
                Lang.PET_REVIVE_ERROR_NOT_ENOUGH_FUNDS.message().send(player, replacer -> replacer
                    .replace(Placeholders.PET_NAME, petData.getName())
                    .replace(Placeholders.GENERIC_AMOUNT, currency.format(cost))
                );
                return false;
            }
            currency.withdrawAsync(player.getUniqueId(), cost);
        }

        petData.revive();

        Lang.PET_REVIVE_SUCCESS.message().send(player, replacer -> replacer
            .replace(Placeholders.PET_NAME, petData.getName())
            .replace(Placeholders.GENERIC_AMOUNT, currency.format(cost))
        );
        return true;
    }

    public boolean releasePet(@NonNull Player player, @NonNull PetData petData) {
        if (!Config.PET_RELEASE_ALLOWED.get() && !player.hasPermission(Perms.BYPASS_RELEASE_DISABLED)) {
            CoreLang.ERROR_NO_PERMISSION.withPrefix(this.plugin).send(player);
            return false;
        }

        if (Config.PET_RELEASE_DISABLED_WORLDS.get().contains(player.getWorld().getName())) {
            Lang.PET_RELEASE_ERROR_BAD_WORLD.message().send(player);
            return false;
        }

        Template template = petData.getTemplate();
        Tier tier = petData.getTier();

        PetReleaseEvent event = new PetReleaseEvent(player, tier, template);
        this.plugin.getPluginManager().callEvent(event);
        if (event.isCancelled()) return false;

        if (Config.PET_RELEASE_NATURAL.get()) {
            Location location = player.getLocation();
            Block against = location.getBlock();
            Block placed = against.getRelative(BlockFace.UP);
            ItemStack item = new ItemStack(Material.STONE);
            BlockPlaceEvent placeEvent = new BlockPlaceEvent(placed, placed
                .getState(), against, item, player, true, EquipmentSlot.HAND);
            plugin.getPluginManager().callEvent(placeEvent);
            if (placeEvent.isCancelled()) {
                Lang.PET_RELEASE_ERROR_PROTECTED_AREA.message().send(player);
                return false;
            }

            if (PetUtils.isAllowedPetZone(location) && petData.isAlive()) {
                Entity entity = player.getWorld().spawnEntity(location, template.getEntityType());
                if (entity instanceof LivingEntity livingEntity) {
                    petData.getWardrobe().dressUp(livingEntity);
                }
                entity.setVelocity(location.getDirection().multiply(3));

            }
        }

        this.removeFromCollection(plugin.getUserManager().getOrFetch(player), tier, template);
        Lang.PET_RELEASE_SUCCESS.message().send(player, replacer -> replacer.replace(petData.replacePlaceholders()));
        return true;
    }

    public boolean tryInteract(@NonNull Player player, @NonNull LivingEntity entity) {
        ActivePet petHolder = this.getPetByMob(entity);
        if (petHolder == null) return false;

        if (!petHolder.isOwner(player)) {
            Lang.PET_ERROR_NOT_YOUR.message().send(player);
            return true;
        }

        ItemStack handItem = player.getInventory().getItemInMainHand();
        FoodItem foodItem = this.getFoodItem(handItem);
        if (foodItem != null && petHolder.getTemplate().isFood(foodItem) && petHolder.doFeed(foodItem)) {
            handItem.setAmount(handItem.getAmount() - 1);
            return true;
        }

        if (handItem.getType() == Material.NAME_TAG && Config.PET_NAME_RENAME_ALLOW_NAMETAGS.get()) {
            ItemMeta meta = handItem.getItemMeta();
            if (meta != null && meta.hasDisplayName()) {
                if (this.tryRename(player, meta.getDisplayName())) {
                    handItem.setAmount(handItem.getAmount() - 1);
                }
            }
            return true;
        }

        if (petHolder.isEquipmentUnlocked() && !handItem.getType().isAir()) {
            EntityEquipment equipment = entity.getEquipment();
            if (equipment != null) {
                EquipmentSlot slot = EquipmentSlot.HAND;
                if (ItemUtil.isHelmet(handItem)) slot = EquipmentSlot.HEAD;
                else if (ItemUtil.isChestplate(handItem)) slot = EquipmentSlot.CHEST;
                else if (ItemUtil.isLeggings(handItem)) slot = EquipmentSlot.LEGS;
                else if (ItemUtil.isBoots(handItem)) slot = EquipmentSlot.FEET;
                else if (handItem.getType() == Material.SHIELD) slot = EquipmentSlot.OFF_HAND;

                if (equipment.getItem(slot).getType().isAir()) {
                    equipment.setItem(slot, new ItemStack(handItem));
                    handItem.setAmount(0);

                    petHolder.saveData();
                    PetUser user = this.plugin.getUserManager().getOrFetch(player);
                    this.plugin.getUserManager().save(user);
                    NightSound.of(Sound.ITEM_ARMOR_EQUIP_LEATHER).play(entity.getLocation());
                }
                return true;
            }
        }

        boolean isRideable = entity instanceof AbstractHorse;
        boolean needSneak = Config.PET_SNEAK_TO_OPEN_MENU.get();
        if (isRideable) needSneak = !needSneak;

        boolean isMenu = needSneak == player.isSneaking();

        if (isMenu) {
            petHolder.openMenu();
        }
        else if (entity instanceof AbstractHorse horse) {
            horse.addPassenger(player);
        }
        return true;
    }

    public boolean startRename(@NonNull Player player) {
        if (!this.hasActivePet(player)) {
            Lang.PET_ERROR_NO_ACTIVE_PET.message().send(player);
            return false;
        }
        if (this.plugin.useBedrockForms(player) && this.bedrockForms().openRename(player)) {
            return true;
        }

        // Only check for the Name Tag here. It is taken when the name is entered, so nothing is lost
        // if the player leaves the prompt or it times out.
        if (isRenameNametagRequired() && !this.hasRenameNametag(player)) {
            return false;
        }

        if (Config.PET_NAME_RENAME_USE_DIALOG.get()) {
            // Delay so the Pet Menu finishes closing first: its scheduled closeInventory() would
            // otherwise close the dialog screen right after it opens.
            this.plugin.runTaskLater(() -> {
                if (player.isOnline()) PetRenameDialog.show(this.plugin, player);
            }, 2L);
            return true;
        }

        Lang.PET_RENAME_PROMPT.message().send(player);
        Dialog.create(player, (dialog, input) -> {
            this.plugin.runTask(task -> this.submitRename(player, input.getText()));
            return true;
        });

        return true;
    }

    /**
     * @return Whether renaming a pet from a menu (Java GUI or Bedrock form) costs a Name Tag.
     */
    public static boolean isRenameNametagRequired() {
        return Config.PET_NAME_RENAME_MENU_REQUIRES_NAMETAG.get();
    }

    /**
     * Checks whether the player has a Name Tag for a menu rename, without taking it.
     * Sends the "no name tag" message if they don't.
     */
    public boolean hasRenameNametag(@NonNull Player player) {
        if (Players.countItem(player, Material.NAME_TAG) < 1) {
            Lang.PET_RENAME_ERROR_NO_NAMETAG.message().send(player);
            return false;
        }
        return true;
    }

    /**
     * Takes one Name Tag from the player as the cost of a menu rename.
     * @return false (and a message is sent) if the player has no Name Tag.
     */
    public boolean takeRenameNametag(@NonNull Player player) {
        if (!this.hasRenameNametag(player)) return false;

        Players.takeItem(player, Material.NAME_TAG, 1);
        return true;
    }

    /**
     * Handles a name entered in a menu rename (Java chat prompt or Bedrock form): takes the Name Tag if
     * required, then renames. The Name Tag is refunded if the rename fails.
     * Must be called on the main thread.
     */
    public boolean submitRename(@NonNull Player player, @NonNull String name) {
        if (!this.hasActivePet(player)) {
            Lang.PET_ERROR_NO_ACTIVE_PET.message().send(player);
            return false;
        }

        boolean required = isRenameNametagRequired();
        if (required && !this.takeRenameNametag(player)) {
            return false;
        }

        return this.completeRename(player, name, required);
    }

    /**
     * Applies a rename requested from a menu. Refunds the Name Tag if the rename fails and one was taken.
     */
    public boolean completeRename(@NonNull Player player, @NonNull String name, boolean refundNametag) {
        if (this.tryRename(player, name)) return true;

        if (refundNametag) {
            Players.addItem(player, new ItemStack(Material.NAME_TAG));
        }
        return false;
    }

    public boolean tryRename(@NonNull Player player, @NonNull String name) {
        ActivePet holder = this.getPlayerPet(player);
        if (holder == null) {
            Lang.PET_ERROR_NO_ACTIVE_PET.message().send(player);
            return false;
        }

        String rawName = NightMessage.stripAll(name);
        if (rawName.isEmpty()) return false;

        if (!player.hasPermission(Perms.BYPASS_NAME_WORDS)) {
            if (Config.PET_NAME_BLOCKED_WORDS.get().stream().anyMatch(rawName::contains)) {
                Lang.PET_RENAME_ERROR_FORBIDDEN.message().send(player);
                return false;
            }
        }

        if (!player.hasPermission(Perms.BYPASS_NAME_LENGTH)) {
            int max = Config.PET_NAME_LENGTH_MAX.get();
            if (rawName.length() > max) {
                Lang.PET_RENAME_ERROR_TOO_LONG.message().send(player, replacer -> replacer.replace(
                    Placeholders.GENERIC_AMOUNT, max));
                return false;
            }

            int min = Config.PET_NAME_LENGTH_MIN.get();
            if (rawName.length() < min) {
                Lang.PET_RENAME_ERROR_TOO_SHORT.message().send(player, replacer -> replacer.replace(
                    Placeholders.GENERIC_AMOUNT, min));
                return false;
            }
        }

        holder.setName(name);
        Lang.PET_RENAME_SUCCESS.message().send(player, replacer -> replacer.replace(Placeholders.PET_NAME, holder
            .getName()));
        return true;
    }

    // ------------------------------------------------------------------------------------------
    // Shared menu actions.
    // These are used by both the Java chest GUIs and the Bedrock forms so both editions execute
    // exactly the same logic.
    // ------------------------------------------------------------------------------------------

    /**
     * Result of the primary action on a pet in the collection.
     */
    public enum CollectionSelectResult {
        /** The pet is dead, the revive confirmation should be shown. */
        OPEN_REVIVE,
        /** The selected pet was already summoned and has been returned to the collection. */
        DESPAWNED,
        /** Spawn was attempted (see {@link #spawnPet(Player, PetData)} for messages). */
        SPAWN_ATTEMPTED
    }

    /**
     * Primary collection action (Java: left-click on a pet in the Collection GUI).
     * Dead pet -> revive; summoned pet -> despawn; otherwise despawn the current pet (if any) and summon the selected one.
     */
    @NonNull
    public CollectionSelectResult selectCollectionPet(@NonNull Player player, @NonNull PetData petData) {
        if (petData.isDead()) {
            return CollectionSelectResult.OPEN_REVIVE;
        }

        ActivePet holder = this.getPlayerPet(player);
        if (holder != null) {
            // Compare template AND tier: the same mob type can be owned in several tiers.
            boolean sameEntry = holder.getTemplate() == petData.getTemplate() && holder.getTier() == petData.getTier();
            this.despawnPet(player);
            if (sameEntry) {
                return CollectionSelectResult.DESPAWNED;
            }
        }

        this.spawnPet(player, petData);
        return CollectionSelectResult.SPAWN_ATTEMPTED;
    }

    /**
     * @return Whether the given collection entry is the player's currently summoned pet.
     */
    public boolean isSummoned(@NonNull Player player, @NonNull PetData petData) {
        ActivePet holder = this.getPlayerPet(player);
        return holder != null && holder.getTemplate() == petData.getTemplate() && holder.getTier() == petData.getTier();
    }

    /**
     * Whether the release confirmation can be opened for the pet (Java: Q/Drop key in the Collection GUI).
     * Final checks (permissions, worlds, events) are done in {@link #releasePet(Player, PetData)}.
     */
    public boolean canOpenRelease(@NonNull Player player, @NonNull PetData petData) {
        if (!this.isReleaseAllowed(player)) return false;

        return !this.isSummoned(player, petData);
    }

    /**
     * @return Whether the player may release pets at all: enabled in config, or bypassed by permission.
     * Same rule as {@link #releasePet(Player, PetData)}.
     */
    public boolean isReleaseAllowed(@NonNull Player player) {
        return Config.PET_RELEASE_ALLOWED.get() || player.hasPermission(Perms.BYPASS_RELEASE_DISABLED);
    }

    public boolean hasInventoryAccess(@NonNull ActivePet pet) {
        return pet.getTier().hasInventory() && pet.getTemplate().canHaveInventory();
    }

    public boolean hasEquipmentAccess(@NonNull ActivePet pet) {
        return pet.getTier().hasEquipment();
    }

    /**
     * Toggles the equipment lock (Java: left-click on the Equipment button).
     */
    public boolean toggleEquipmentLock(@NonNull ActivePet pet) {
        if (!this.hasEquipmentAccess(pet)) return false;

        pet.setEquipmentUnlocked(!pet.isEquipmentUnlocked());
        return true;
    }

    /**
     * Moves all pet equipment to the owner's inventory (Java: right-click on the Equipment button).
     */
    public boolean unequipAll(@NonNull Player player, @NonNull ActivePet pet) {
        if (!this.hasEquipmentAccess(pet)) return false;

        LivingEntity entity = pet.getEntity();
        EntityEquipment equipment = entity.getEquipment();
        if (equipment == null) return false;

        for (EquipmentSlot slot : EquipmentSlot.values()) {
            ItemStack wear = equipment.getItem(slot);
            if (wear.getType().isAir()) continue;

            Players.addItem(player, wear);
            equipment.setItem(slot, null);
        }

        pet.saveData();
        PetUser user = this.plugin.getUserManager().getOrFetch(player);
        this.plugin.getUserManager().save(user);
        return true;
    }

    public void toggleCombatMode(@NonNull ActivePet pet) {
        pet.toggleCombatMode();
    }

    /**
     * Sets a specific combat mode by cycling with {@link ActivePet#toggleCombatMode()},
     * so the same code path as the Java GUI is used.
     */
    public void setCombatMode(@NonNull ActivePet pet, @NonNull CombatMode mode) {
        int guard = CombatMode.values().length;
        while (pet.getCombatMode() != mode && guard-- > 0) {
            pet.toggleCombatMode();
        }
    }

    public void toggleSilent(@NonNull ActivePet pet) {
        pet.setSilent(!pet.isSilent());
    }

    public boolean canUpgradeAspect(@NonNull ActivePet pet, @NonNull Aspect aspect) {
        return pet.getAspectPoints() > 0 && pet.getAspectValue(aspect) < pet.getTier().getAspectMax(aspect);
    }

    /**
     * Spends one aspect point on the given aspect (Java: click on an aspect in the Aspects GUI).
     */
    public boolean upgradeAspect(@NonNull ActivePet pet, @NonNull Aspect aspect) {
        if (!this.canUpgradeAspect(pet, aspect)) return false;

        pet.setAspectValue(aspect, pet.getAspectValue(aspect) + 1);
        pet.setAspectPoints(pet.getAspectPoints() - 1);
        pet.update();
        return true;
    }

    /**
     * Resets all aspects and refunds the points (Java: Reallocate button in the Aspects GUI).
     */
    public boolean reallocateAspects(@NonNull ActivePet pet) {
        if (!Config.PET_REALLOCATE_ASPECTS.get()) return false;

        pet.reallocateAspects();
        pet.update();
        return true;
    }

    public boolean canDamage(@NonNull LivingEntity damager, @NonNull LivingEntity victim) {
        ActivePet damagerPet = this.getPetByMob(damager);
        if (damagerPet != null) {
            return this.canBeDamaged(damagerPet, victim);
        }

        ActivePet victimPet = this.getPetByMob(victim);
        if (victimPet != null) {
            return this.canBeDamaged(victimPet, damager);
        }

        return true;
    }

    public boolean canBeDamaged(@NonNull ActivePet pet, @NonNull LivingEntity entity) {
        if (entity == pet.getOwner()) return false;
        if (entity == pet.getEntity()) return true;
        if (Config.PET_PVP_ALLOWED.get()) return true;

        return (!(entity instanceof Player) && !this.isPetEntity(entity));
    }
}
