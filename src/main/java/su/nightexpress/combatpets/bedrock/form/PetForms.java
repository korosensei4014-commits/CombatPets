package su.nightexpress.combatpets.bedrock.form;

import org.bukkit.entity.Player;
import org.geysermc.cumulus.form.CustomForm;
import org.jspecify.annotations.NonNull;
import su.nightexpress.combatpets.PetsPlugin;
import su.nightexpress.combatpets.api.pet.ActivePet;
import su.nightexpress.combatpets.api.pet.Aspect;
import su.nightexpress.combatpets.api.pet.Stat;
import su.nightexpress.combatpets.api.pet.Tier;
import su.nightexpress.combatpets.api.pet.type.CombatMode;
import su.nightexpress.combatpets.bedrock.BedrockLang;
import su.nightexpress.combatpets.bedrock.BedrockManager;
import su.nightexpress.combatpets.bedrock.BedrockText;
import su.nightexpress.combatpets.config.Config;
import su.nightexpress.combatpets.config.Lang;
import su.nightexpress.combatpets.data.impl.PetData;
import su.nightexpress.combatpets.data.impl.PetUser;
import su.nightexpress.combatpets.pet.AttributeRegistry;
import su.nightexpress.combatpets.pet.PetManager;
import su.nightexpress.combatpets.pet.duel.DuelManager;
import su.nightexpress.nightcore.util.NumberUtil;
import su.nightexpress.nightcore.util.TimeUtil;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;

import static su.nightexpress.combatpets.Placeholders.*;

/**
 * Bedrock forms for the pet collection, pet management, settings, rename and aspects.
 * <p>
 * Mapping of the Java chest GUI interactions:
 * <ul>
 *     <li>Collection, left-click on a pet: "Summon Pet", "Return to Collection" or "Revive Pet" buttons.</li>
 *     <li>Collection, Q/Drop key on a pet: "Release Pet" button + confirmation.</li>
 *     <li>Pet Menu, Equipment left-click (toggle lock): "Equipment Unlocked" toggle in Pet Settings.</li>
 *     <li>Pet Menu, Equipment right-click (unequip all): "Unequip All Items" button.</li>
 *     <li>Pet Menu, Combat Mode / Silent buttons: dropdown / toggle in Pet Settings.</li>
 *     <li>Pet Menu, Rename (chat prompt): text input form.</li>
 *     <li>Aspects, click on an aspect: aspect details + "Upgrade" button.</li>
 * </ul>
 */
public class PetForms extends FormSupport {

    public PetForms(@NonNull PetsPlugin plugin, @NonNull BedrockManager manager) {
        super(plugin, manager);
    }

    @NonNull
    private PetManager pets() {
        return this.plugin.getPetManager();
    }

    // ------------------------------------------------------------------------------------------
    // Collection
    // ------------------------------------------------------------------------------------------

    public boolean openTiers(@NonNull Player player) {
        PetUser user = this.plugin.getUserManager().getOrFetch(player);

        List<FormButton> buttons = new ArrayList<>();
        this.pets().getTiers().stream().sorted(Comparator.comparingDouble(Tier::getWeight).reversed()).forEach(tier -> {
            String label = this.text(player, BedrockLang.TIERS_BUTTON,
                tier.replacePlaceholders(),
                replace(GENERIC_AMOUNT, NumberUtil.format(user.getPets(tier).size()))
            );
            buttons.add(this.button(label, Config.BEDROCK_ICON_TIER, () -> this.openCollection(player, tier, 0)));
        });

        return this.manager.sendButtons(player,
            this.text(player, BedrockLang.TIERS_TITLE),
            this.text(player, BedrockLang.TIERS_CONTENT),
            buttons
        );
    }

    public boolean openCollection(@NonNull Player player, @NonNull Tier tier, int requestedPage) {
        PetUser user = this.plugin.getUserManager().getOrFetch(player);
        List<PetData> owned = user.getPets(tier).stream().sorted(Comparator.comparing(data -> data.getTemplate().getId())).toList();

        int pages = BedrockManager.getPages(owned.size());
        int page = BedrockManager.clampPage(requestedPage, owned.size());

        List<FormButton> buttons = new ArrayList<>();
        for (PetData petData : BedrockManager.getPage(owned, page)) {
            String label = this.text(player, BedrockLang.COLLECTION_BUTTON,
                replace(GENERIC_TYPE, this.getStatus(player, petData)),
                petData.replacePlaceholders(),
                petData.getTier().replacePlaceholders(),
                petData.getTemplate().replacePlaceholders()
            );
            buttons.add(this.button(label, Config.BEDROCK_ICON_PET, () -> this.openPet(player, petData, page)));
        }

        this.addPagination(player, buttons, page, pages, newPage -> this.openCollection(player, tier, newPage));
        buttons.add(this.back(player, () -> this.pets().openTierCollection(player)));

        return this.manager.sendButtons(player,
            this.text(player, BedrockLang.COLLECTION_TITLE, tier.replacePlaceholders(), pages(page, pages)),
            this.text(player, owned.isEmpty() ? BedrockLang.COLLECTION_CONTENT_EMPTY : BedrockLang.COLLECTION_CONTENT),
            buttons
        );
    }

    @NonNull
    private String getStatus(@NonNull Player player, @NonNull PetData petData) {
        if (petData.isDead()) {
            if (petData.isAutoRevivable()) {
                return BedrockLang.STATUS_DEAD_AUTO.text().replace(GENERIC_TIME, TimeUtil.formatDuration(petData.getReviveDate()));
            }
            return BedrockLang.STATUS_DEAD.text();
        }
        return this.pets().isSummoned(player, petData) ? BedrockLang.STATUS_SUMMONED.text() : BedrockLang.STATUS_IDLE.text();
    }

    /**
     * @return true if the pet is still part of the player's collection.
     */
    private boolean isOwned(@NonNull Player player, @NonNull PetData petData) {
        PetUser user = this.plugin.getUserManager().getOrFetch(player);
        return user.getPet(petData.getTemplate(), petData.getTier()) == petData;
    }

    /**
     * Pet actions. Replaces the click types of a pet in the collection GUI with separate buttons.
     */
    public boolean openPet(@NonNull Player player, @NonNull PetData petData, int page) {
        Tier tier = petData.getTier();
        if (!this.isOwned(player, petData)) {
            BedrockLang.ERROR_PET_GONE.message().send(player);
            return this.openCollection(player, tier, page);
        }

        PetManager petManager = this.pets();
        boolean summoned = petManager.isSummoned(player, petData);
        Runnable backToPet = () -> this.openPet(player, petData, page);

        List<FormButton> buttons = new ArrayList<>();
        if (petData.isDead()) {
            String label = this.text(player, BedrockLang.PET_BUTTON_REVIVE, tier.replacePlaceholders());
            buttons.add(this.button(label, Config.BEDROCK_ICON_REVIVE, () -> this.openReviveConfirm(player, petData, backToPet, backToPet)));
        }
        else if (summoned) {
            buttons.add(this.button(this.text(player, BedrockLang.PET_BUTTON_MANAGE), Config.BEDROCK_ICON_MANAGE, () -> petManager.openOverviewMenu(player)));
            buttons.add(this.button(this.text(player, BedrockLang.PET_BUTTON_DESPAWN), Config.BEDROCK_ICON_DESPAWN, () -> {
                if (!this.isOwned(player, petData)) return;
                if (petManager.isSummoned(player, petData)) {
                    petManager.despawnPet(player);
                }
                this.openCollection(player, tier, page);
            }));
        }
        else {
            buttons.add(this.button(this.text(player, BedrockLang.PET_BUTTON_SUMMON), Config.BEDROCK_ICON_SUMMON, () -> {
                if (!this.isOwned(player, petData)) {
                    BedrockLang.ERROR_PET_GONE.message().send(player);
                    return;
                }
                // Same as the Java GUI: return the current pet (if any), then summon the selected one.
                if (petManager.hasActivePet(player)) {
                    petManager.despawnPet(player);
                }
                petManager.spawnPet(player, petData);
            }));
        }

        boolean releaseHint = false;
        if (petManager.canOpenRelease(player, petData)) {
            buttons.add(this.button(this.text(player, BedrockLang.PET_BUTTON_RELEASE), Config.BEDROCK_ICON_RELEASE,
                () -> this.openReleaseConfirm(player, petData, () -> this.openCollection(player, tier, page), backToPet)));
        }
        else if (petManager.isReleaseAllowed(player) && summoned) {
            releaseHint = true;
        }

        buttons.add(this.back(player, () -> this.openCollection(player, tier, page)));

        String content = this.text(player, BedrockLang.PET_CONTENT,
            replace(GENERIC_TYPE, this.getStatus(player, petData)),
            petData.replacePlaceholders(),
            tier.replacePlaceholders(),
            petData.getTemplate().replacePlaceholders()
        );
        content = content + "\n" + this.text(player, BedrockLang.PET_CONTENT_DUELS, petData.replacePlaceholders());
        if (releaseHint) {
            content = content + "\n\n" + this.text(player, BedrockLang.PET_HINT_RELEASE_ACTIVE);
        }

        return this.manager.sendButtons(player, this.text(player, BedrockLang.PET_TITLE, petData.replacePlaceholders()), content, buttons);
    }

    /**
     * Revive confirmation (Java: Revive GUI).
     * @param afterRevive executed after a successful revive.
     * @param onCancel    executed when the player declines.
     */
    public boolean openReviveConfirm(@NonNull Player player, @NonNull PetData petData, @NonNull Runnable afterRevive, @NonNull Runnable onCancel) {
        return this.manager.sendConfirm(player,
            this.text(player, BedrockLang.REVIVE_TITLE),
            this.text(player, BedrockLang.REVIVE_CONTENT, petData.replacePlaceholders(), petData.getTier().replacePlaceholders()),
            this.text(player, BedrockLang.REVIVE_BUTTON),
            this.text(player, BedrockLang.BUTTON_CANCEL),
            () -> {
                if (!this.isOwned(player, petData)) {
                    BedrockLang.ERROR_PET_GONE.message().send(player);
                    return;
                }
                if (!petData.isDead()) {
                    afterRevive.run();
                    return;
                }
                if (this.pets().revivePet(player, petData)) {
                    afterRevive.run();
                }
            },
            onCancel
        );
    }

    /**
     * Release confirmation (Java: Release GUI, opened with the Q/Drop key).
     * @param afterRelease executed after a successful release.
     * @param onCancel     executed when the player declines or the release fails.
     */
    public boolean openReleaseConfirm(@NonNull Player player, @NonNull PetData petData, @NonNull Runnable afterRelease, @NonNull Runnable onCancel) {
        return this.manager.sendConfirm(player,
            this.text(player, BedrockLang.RELEASE_TITLE),
            this.text(player, BedrockLang.RELEASE_CONTENT, petData.replacePlaceholders(), petData.getTier().replacePlaceholders()),
            this.text(player, BedrockLang.RELEASE_BUTTON),
            this.text(player, BedrockLang.BUTTON_CANCEL),
            () -> {
                if (!this.isOwned(player, petData)) {
                    BedrockLang.ERROR_PET_GONE.message().send(player);
                    return;
                }
                // Re-check the same condition as the Java GUI (e.g. pet summoned meanwhile).
                if (!this.pets().canOpenRelease(player, petData)) {
                    onCancel.run();
                    return;
                }
                if (this.pets().releasePet(player, petData)) {
                    afterRelease.run();
                }
            },
            onCancel
        );
    }

    // ------------------------------------------------------------------------------------------
    // Active pet menu
    // ------------------------------------------------------------------------------------------

    private void withActivePet(@NonNull Player player, @NonNull Consumer<ActivePet> action) {
        ActivePet pet = this.pets().getPlayerPet(player);
        if (pet == null) {
            Lang.PET_ERROR_NO_ACTIVE_PET.message().send(player);
            return;
        }
        action.accept(pet);
    }

    /**
     * Pet Menu (Java: Pet Menu GUI).
     */
    public boolean openPetMenu(@NonNull Player player) {
        ActivePet pet = this.pets().getPlayerPet(player);
        if (pet == null) {
            Lang.PET_ERROR_NO_ACTIVE_PET.message().send(player);
            return true;
        }

        PetManager petManager = this.pets();
        List<FormButton> buttons = new ArrayList<>();

        if (petManager.hasInventoryAccess(pet)) {
            String label = this.text(player, BedrockLang.MENU_BUTTON_INVENTORY, pet.replacePlaceholders(), pet.getTier().replacePlaceholders());
            buttons.add(this.button(label, Config.BEDROCK_ICON_INVENTORY, () -> this.withActivePet(player, current -> {
                if (!petManager.hasInventoryAccess(current)) return;
                player.openInventory(current.getInventory());
            })));
        }

        buttons.add(this.button(this.text(player, BedrockLang.MENU_BUTTON_SETTINGS), Config.BEDROCK_ICON_SETTINGS, () -> this.openSettings(player)));
        buttons.add(this.button(this.text(player, BedrockLang.MENU_BUTTON_RENAME), Config.BEDROCK_ICON_RENAME, () -> this.openRename(player)));
        buttons.add(this.button(this.text(player, BedrockLang.MENU_BUTTON_ASPECTS, pet.replacePlaceholders()), Config.BEDROCK_ICON_ASPECTS,
            () -> petManager.openAspectsMenu(player)));

        if (petManager.hasEquipmentAccess(pet)) {
            buttons.add(this.button(this.text(player, BedrockLang.MENU_BUTTON_UNEQUIP), Config.BEDROCK_ICON_UNEQUIP, () -> this.withActivePet(player, current -> {
                petManager.unequipAll(player, current);
                this.openPetMenu(player);
            })));
        }

        buttons.add(this.button(this.text(player, BedrockLang.MENU_BUTTON_DESPAWN), Config.BEDROCK_ICON_DESPAWN, () -> petManager.despawnPet(player)));

        String content = this.text(player, BedrockLang.MENU_CONTENT, pet.replacePlaceholders(), pet.getTier().replacePlaceholders(), pet.getTemplate().replacePlaceholders());
        if (petManager.hasEquipmentAccess(pet)) {
            content = content + "\n" + this.text(player, BedrockLang.MENU_CONTENT_EQUIPMENT, pet.replacePlaceholders());
        }
        content = content + "\n" + this.text(player, BedrockLang.MENU_CONTENT_DUELS, pet.replacePlaceholders());

        return this.manager.sendButtons(player, this.text(player, BedrockLang.MENU_TITLE, pet.replacePlaceholders()), content, buttons);
    }

    /**
     * Pet settings: combat mode, silent mode and equipment lock in a single form.
     */
    public boolean openSettings(@NonNull Player player) {
        ActivePet pet = this.pets().getPlayerPet(player);
        if (pet == null) {
            Lang.PET_ERROR_NO_ACTIVE_PET.message().send(player);
            return true;
        }

        PetManager petManager = this.pets();
        CombatMode[] modes = CombatMode.values();
        List<String> modeNames = new ArrayList<>();
        for (CombatMode mode : modes) {
            modeNames.add(BedrockText.plain(Lang.COMBAT_MODE.getLocalized(mode)));
        }

        // Response indexes include labels, so track the component index manually.
        int index = 0;
        CustomForm.Builder builder = CustomForm.builder().title(this.text(player, BedrockLang.SETTINGS_TITLE));

        builder.label(this.text(player, BedrockLang.SETTINGS_LABEL));
        index++;

        builder.dropdown(this.text(player, BedrockLang.SETTINGS_COMBAT_MODE), modeNames, pet.getCombatMode().ordinal());
        int modeIndex = index++;

        builder.toggle(this.text(player, BedrockLang.SETTINGS_SILENT), pet.isSilent());
        int silentIndex = index++;

        int equipmentIndex = -1;
        if (petManager.hasEquipmentAccess(pet)) {
            builder.toggle(this.text(player, BedrockLang.SETTINGS_EQUIPMENT), pet.isEquipmentUnlocked());
            equipmentIndex = index++;
        }
        int finalEquipmentIndex = equipmentIndex;

        // Player-wide duel setting (shown only when duels are enabled on the server).
        DuelManager duels = this.plugin.getDuelManager();
        int duelIndex = -1;
        if (duels != null) {
            builder.toggle(this.text(player, BedrockLang.SETTINGS_DUELS), duels.isAcceptingDuels(player));
            duelIndex = index++;
        }
        int finalDuelIndex = duelIndex;

        return this.manager.sendCustom(player, builder, response -> this.withActivePet(player, current -> {
            int modeChoice;
            boolean silent;
            boolean equipment;
            boolean acceptDuels;
            try {
                modeChoice = response.asDropdown(modeIndex);
                silent = response.asToggle(silentIndex);
                equipment = finalEquipmentIndex >= 0 && response.asToggle(finalEquipmentIndex);
                acceptDuels = finalDuelIndex >= 0 && response.asToggle(finalDuelIndex);
            }
            catch (IllegalArgumentException | IllegalStateException exception) {
                this.openSettings(player);
                return;
            }

            if (modeChoice >= 0 && modeChoice < modes.length) {
                petManager.setCombatMode(current, modes[modeChoice]);
            }
            if (silent != current.isSilent()) {
                petManager.toggleSilent(current);
            }
            if (finalEquipmentIndex >= 0 && petManager.hasEquipmentAccess(current) && equipment != current.isEquipmentUnlocked()) {
                petManager.toggleEquipmentLock(current);
            }

            if (finalDuelIndex >= 0 && duels != null && acceptDuels != duels.isAcceptingDuels(player)) {
                duels.setAcceptingDuels(player, acceptDuels);
            }

            BedrockLang.SETTINGS_SAVED.message().send(player);
            this.openPetMenu(player);
        }), () -> this.openPetMenu(player));
    }

    /**
     * Rename form (Java: chat prompt after clicking Rename).
     * The Name Tag (if required) is only taken when the player submits a name.
     */
    public boolean openRename(@NonNull Player player) {
        ActivePet pet = this.pets().getPlayerPet(player);
        if (pet == null) {
            Lang.PET_ERROR_NO_ACTIVE_PET.message().send(player);
            return true;
        }

        PetManager petManager = this.pets();
        boolean nametagRequired = PetManager.isRenameNametagRequired();
        if (nametagRequired && !petManager.hasRenameNametag(player)) {
            return true; // Message sent.
        }

        String label = this.text(player, BedrockLang.RENAME_LABEL,
            pet.replacePlaceholders(),
            replace(GENERIC_AMOUNT, String.valueOf(Config.PET_NAME_LENGTH_MIN.get())),
            replace(GENERIC_MAX, String.valueOf(Config.PET_NAME_LENGTH_MAX.get()))
        );
        if (nametagRequired) {
            label = label + "\n" + this.text(player, BedrockLang.RENAME_LABEL_NAMETAG);
        }

        CustomForm.Builder builder = CustomForm.builder()
            .title(this.text(player, BedrockLang.RENAME_TITLE))
            .label(label)                                       // index 0
            .input(this.text(player, BedrockLang.RENAME_INPUT),  // index 1
                BedrockText.plain(BedrockLang.RENAME_INPUT_PLACEHOLDER.text()),
                BedrockText.plain(pet.getName()));

        return this.manager.sendCustom(player, builder, response -> {
            String input;
            try {
                input = response.asInput(1);
            }
            catch (IllegalArgumentException | IllegalStateException exception) {
                input = null;
            }

            if (input == null || input.isBlank()) {
                BedrockLang.ERROR_INPUT_EMPTY.message().send(player);
                this.openRename(player);
                return;
            }

            String name = input.trim();
            if (petManager.submitRename(player, name)) {
                this.openPetMenu(player);
            }
            else if (petManager.hasActivePet(player)) {
                this.openRename(player); // Error message sent by the rename logic.
            }
        }, () -> this.openPetMenu(player));
    }

    // ------------------------------------------------------------------------------------------
    // Aspects
    // ------------------------------------------------------------------------------------------

    @NonNull
    private List<Aspect> getAspects() {
        return this.pets().getAspects().stream().sorted(Comparator.comparing(Aspect::getName)).toList();
    }

    /**
     * Aspects list (Java: Aspects GUI).
     */
    public boolean openAspects(@NonNull Player player) {
        ActivePet pet = this.pets().getPlayerPet(player);
        if (pet == null) {
            Lang.PET_ERROR_NO_ACTIVE_PET.message().send(player);
            return true;
        }

        PetManager petManager = this.pets();
        List<FormButton> buttons = new ArrayList<>();

        for (Aspect aspect : this.getAspects()) {
            String state;
            if (pet.getAspectPoints() <= 0) state = BedrockLang.ASPECTS_STATE_NO_POINTS.text();
            else if (pet.getAspectValue(aspect) < pet.getTier().getAspectMax(aspect)) state = BedrockLang.ASPECTS_STATE_UPGRADE.text();
            else state = BedrockLang.ASPECTS_STATE_MAX.text();

            String label = this.text(player, BedrockLang.ASPECTS_BUTTON,
                replace(GENERIC_TYPE, state),
                replace(GENERIC_VALUE, NumberUtil.format(pet.getAspectValue(aspect))),
                replace(GENERIC_MAX, NumberUtil.format(pet.getTier().getAspectMax(aspect))),
                aspect.replacePlaceholders()
            );
            buttons.add(new FormButton(label, null, () -> this.openAspect(player, aspect)));
        }

        if (Config.PET_REALLOCATE_ASPECTS.get()) {
            buttons.add(this.button(this.text(player, BedrockLang.ASPECTS_BUTTON_REALLOCATE), Config.BEDROCK_ICON_REALLOCATE,
                () -> this.openReallocateConfirm(player)));
        }

        buttons.add(this.back(player, () -> petManager.openOverviewMenu(player)));

        return this.manager.sendButtons(player,
            this.text(player, BedrockLang.ASPECTS_TITLE),
            this.text(player, BedrockLang.ASPECTS_CONTENT, pet.replacePlaceholders()),
            buttons
        );
    }

    /**
     * Details of a single aspect with the upgrade button (Java: click on an aspect).
     */
    public boolean openAspect(@NonNull Player player, @NonNull Aspect aspect) {
        ActivePet pet = this.pets().getPlayerPet(player);
        if (pet == null) {
            Lang.PET_ERROR_NO_ACTIVE_PET.message().send(player);
            return true;
        }

        PetManager petManager = this.pets();

        List<String> attributes = new ArrayList<>();
        for (String name : aspect.getAttributes()) {
            Stat attribute = AttributeRegistry.getById(name);
            if (attribute == null) continue;

            double perAspect = pet.getTemplate().getAttributePerAspect(attribute);
            if (perAspect == 0D) continue;

            double total = pet.getAttribute(attribute);
            attributes.add(BedrockLang.ASPECT_ATTRIBUTE.text()
                .replace(GENERIC_NAME, attribute.getDisplayName())
                .replace(GENERIC_VALUE, NumberUtil.format(perAspect))
                .replace(GENERIC_TOTAL, NumberUtil.format(total))
            );
        }
        String attributesText = attributes.isEmpty() ? "-" : String.join("\n", attributes);

        List<FormButton> buttons = new ArrayList<>();
        if (petManager.canUpgradeAspect(pet, aspect)) {
            buttons.add(this.button(this.text(player, BedrockLang.ASPECT_BUTTON_UPGRADE), Config.BEDROCK_ICON_ASPECTS, () -> this.withActivePet(player, current -> {
                petManager.upgradeAspect(current, aspect);
                this.openAspect(player, aspect);
            })));
        }
        buttons.add(this.back(player, () -> this.openAspects(player)));

        String content = this.text(player, BedrockLang.ASPECT_CONTENT,
            replace(GENERIC_VALUE, NumberUtil.format(pet.getAspectValue(aspect))),
            replace(GENERIC_MAX, NumberUtil.format(pet.getTier().getAspectMax(aspect))),
            pet.replacePlaceholders(),
            replace(GENERIC_TYPE, attributesText)
        );

        return this.manager.sendButtons(player, this.text(player, BedrockLang.ASPECT_TITLE, aspect.replacePlaceholders()), content, buttons);
    }

    public boolean openReallocateConfirm(@NonNull Player player) {
        ActivePet pet = this.pets().getPlayerPet(player);
        if (pet == null) {
            Lang.PET_ERROR_NO_ACTIVE_PET.message().send(player);
            return true;
        }

        return this.manager.sendConfirm(player,
            this.text(player, BedrockLang.REALLOCATE_TITLE),
            this.text(player, BedrockLang.REALLOCATE_CONTENT, pet.replacePlaceholders()),
            this.text(player, BedrockLang.REALLOCATE_BUTTON),
            this.text(player, BedrockLang.BUTTON_CANCEL),
            () -> this.withActivePet(player, current -> {
                this.pets().reallocateAspects(current);
                this.openAspects(player);
            }),
            () -> this.openAspects(player)
        );
    }
}
