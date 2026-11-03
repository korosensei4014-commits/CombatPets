package su.nightexpress.combatpets.item;

import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import su.nightexpress.combatpets.PetsPlugin;
import su.nightexpress.combatpets.api.item.ItemType;
import su.nightexpress.combatpets.api.pet.Template;
import su.nightexpress.combatpets.api.pet.Tier;
import su.nightexpress.combatpets.config.Config;
import su.nightexpress.combatpets.config.Keys;
import su.nightexpress.combatpets.config.Lang;
import su.nightexpress.combatpets.Placeholders;
import su.nightexpress.combatpets.data.DataHandler;
import su.nightexpress.combatpets.data.impl.PetData;
import su.nightexpress.combatpets.data.impl.PetUser;
import su.nightexpress.combatpets.pet.PetManager;
import su.nightexpress.combatpets.util.PetUtils;
import su.nightexpress.nightcore.manager.AbstractManager;
import su.nightexpress.nightcore.util.*;
import su.nightexpress.nightcore.util.wrapper.UniParticle;

public class ItemManager extends AbstractManager<PetsPlugin> {

    private final StoredEggRegistry storedEggs;

    public ItemManager(@NonNull PetsPlugin plugin) {
        super(plugin);
        this.storedEggs = new StoredEggRegistry(plugin);
    }

    @Override
    protected void onLoad() {
        this.storedEggs.load();
        this.addListener(new ItemListener(this.plugin, this));
    }

    // ------------------------------------------------------------------------------------------
    // Store in Egg
    // ------------------------------------------------------------------------------------------

    /**
     * Creates an egg item holding this pet (name, level, XP, aspects, wardrobe, duel stats, ...) and registers its
     * unique id. The pet's inventory and equipment must already be emptied.
     */
    @NonNull
    public ItemStack createStoredEgg(@NonNull PetData petData) {
        String id = java.util.UUID.randomUUID().toString();
        String json = DataHandler.GSON.toJson(petData, PetData.class);

        ItemStack item = PetUtils.getRawEggItem(petData.getTemplate());
        ItemUtil.editMeta(item, meta -> {
            meta.setDisplayName(Config.PET_STORED_EGG_NAME.get());
            meta.setLore(Config.PET_STORED_EGG_LORE.get());
            meta.setMaxStackSize(1);
            PDCUtil.set(meta, Keys.storedPet, json);
            PDCUtil.set(meta, Keys.storedEggId, id);
            setItemType(meta, ItemType.STORED_EGG);
        });

        ItemReplacer.create(item).readMeta()
            .replace(petData.replacePlaceholders())
            .replace(petData.getTier().replacePlaceholders())
            .replace(petData.getTemplate().replacePlaceholders())
            .writeMeta();

        this.storedEggs.add(id);
        return item;
    }

    private void handleStoredEggUse(@NonNull Player player, @NonNull ItemStack itemStack) {
        String id = PDCUtil.getString(itemStack, Keys.storedEggId).orElse(null);
        String json = PDCUtil.getString(itemStack, Keys.storedPet).orElse(null);
        if (id == null || json == null) return;

        // A copy of an egg that already hatched, or a duplicated one.
        if (!this.storedEggs.isValid(id)) {
            Lang.PET_STORED_EGG_ERROR_USED.message().send(player);
            this.plugin.warn("Player " + player.getName() + " used a stored pet egg that already hatched or was duplicated (id " + id + ").");
            return;
        }

        PetData parsed;
        try {
            parsed = DataHandler.GSON.fromJson(json, PetData.class);
        }
        catch (RuntimeException exception) {
            parsed = null;
        }
        if (parsed == null) {
            Lang.PET_STORED_EGG_ERROR_INVALID.message().send(player);
            return;
        }
        PetData petData = parsed;

        PetUser user = this.plugin.getUserManager().getOrFetch(player);
        if (!user.isLoaded()) return;

        Template template = petData.getTemplate();
        Tier tier = petData.getTier();
        if (user.hasPet(template, tier)) {
            Lang.PET_CLAIM_ERROR_ALREADY_HAVE.message().send(player);
            return;
        }
        int petLimit = PetManager.getPetsLimit(player);
        if (petLimit > 0 && user.getPets(tier).size() >= petLimit) {
            Lang.PET_CLAIM_ERROR_REACHED_LIMIT.message().send(player, replacer -> replacer
                .replace(tier.replacePlaceholders())
                .replace(Placeholders.GENERIC_AMOUNT, petLimit));
            return;
        }

        // Use the id up first: even if something below fails, this egg can never hatch twice.
        if (!this.storedEggs.consume(id)) {
            Lang.PET_STORED_EGG_ERROR_USED.message().send(player);
            return;
        }
        itemStack.setAmount(itemStack.getAmount() - 1);

        user.addPet(petData);
        this.plugin.getUserManager().save(user);

        Lang.PET_STORED_EGG_HATCH.message().send(player, replacer -> replacer.replace(petData.replacePlaceholders()));
        PetUtils.playSound(player, Config.PET_HATCH_SOUND.get());
    }

    @Override
    protected void onShutdown() {

    }

    private void setItemType(@NonNull ItemStack itemStack, @NonNull ItemType type) {
        ItemUtil.editMeta(itemStack, meta -> setItemType(meta, type));
    }

    private void setItemType(@NonNull ItemMeta meta, @NonNull ItemType type) {
        PDCUtil.set(meta, Keys.itemType, type.name());
    }

    @Nullable
    public ItemType getItemType(@NonNull ItemStack itemStack) {
        String name = PDCUtil.getString(itemStack, Keys.itemType).orElse(null);
        return name == null ? null : StringUtil.getEnum(name, ItemType.class).orElse(null);
    }

    public boolean isItemOfType(@NonNull ItemStack itemStack, @NonNull ItemType itemType) {
        return this.getItemType(itemStack) == itemType;
    }

    @NonNull
    public ItemStack createEgg(@NonNull Template template, @NonNull Tier tier) {
        ItemStack item = PetUtils.getRawEggItem(template);
        ItemUtil.editMeta(item, meta -> {
            PDCUtil.set(meta, Keys.eggPetId, template.getId());
            PDCUtil.set(meta, Keys.eggTierId, tier.getId());
            setItemType(meta, ItemType.EGG);
        });

        ItemReplacer.create(item).readMeta()
            .replace(template.getPlaceholders())
            .replace(tier.getPlaceholders())
            .writeMeta();

        return item;
    }

    @NonNull
    public ItemStack createMysteryEgg(@NonNull Template template) {
        ItemStack item = PetUtils.getRawMysteryEgg(template);
        ItemUtil.editMeta(item, meta -> {
            PDCUtil.set(meta, Keys.eggPetId, template.getId());
            setItemType(meta, ItemType.MYSTERY_EGG);
        });
        ItemReplacer.create(item).readMeta().replace(template.getPlaceholders()).writeMeta();

        return item;
    }

    public boolean isEgg(@NonNull ItemStack itemStack) {
        return this.isItemOfType(itemStack, ItemType.EGG);
    }

    public boolean isMysteryEgg(@NonNull ItemStack itemStack) {
        return this.isItemOfType(itemStack, ItemType.MYSTERY_EGG);
    }

    @Nullable
    public Template getEggTemplate(@NonNull ItemStack item) {
        String id = PDCUtil.getString(item, Keys.eggPetId).orElse(null);
        return id == null ? null : this.plugin.getPetManager().getTemplate(id);
    }

    @Nullable
    public Tier getEggTier(@NonNull ItemStack item) {
        String tierId = PDCUtil.getString(item, Keys.eggTierId).orElse(null);
        return tierId == null ? null : this.plugin.getPetManager().getTier(tierId);
    }

    public void onItemUse(@NonNull Player player, @NonNull ItemStack itemStack, @NonNull ItemType type) {
        switch (type) {
            case EGG -> this.handleEggUse(player, itemStack);
            case MYSTERY_EGG -> this.handleMysteryEggUse(player, itemStack);
            case STORED_EGG -> this.handleStoredEggUse(player, itemStack);
            case XP_ORB -> {
            }
            case REBIRTH -> {
            }
            default -> {
            }
        }
    }

    private void handleEggUse(@NonNull Player player, @NonNull ItemStack itemStack) {
        Template template = this.getEggTemplate(itemStack);
        if (template == null) return;

        Tier tier = this.getEggTier(itemStack);
        if (tier == null) return;

        PetData petData = plugin.getPetManager().tryClaimPet(player, tier, template);
        if (petData == null) return;

        if (Config.isWardrobeEnabled()) {
            petData.setWardrobe(plugin.getWardrobeManager().readAccessoryData(itemStack));
        }

        itemStack.setAmount(itemStack.getAmount() - 1);
    }

    private void handleMysteryEggUse(@NonNull Player player, @NonNull ItemStack itemStack) {
        Tier tier = this.plugin.getPetManager().getTierByWeight();

        Template template = this.getEggTemplate(itemStack);
        if (template == null) return;

        itemStack.setAmount(itemStack.getAmount() - 1);

        Players.addItem(player, this.createEgg(template, tier));

        UniParticle.of(Particle.WITCH).play(player.getLocation().add(0, 0.5, 0), 0.1, 0.5, 50);

        Lang.PET_MYSTERY_EGG_HATCH.message().send(player, replacer -> replacer
            .replace(tier.replacePlaceholders())
            .replace(template.replacePlaceholders())
        );
    }
}
