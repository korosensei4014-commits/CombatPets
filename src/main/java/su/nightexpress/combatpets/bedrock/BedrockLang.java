package su.nightexpress.combatpets.bedrock;

import su.nightexpress.nightcore.locale.LangContainer;
import su.nightexpress.nightcore.locale.LangEntry;
import su.nightexpress.nightcore.locale.entry.MessageLocale;
import su.nightexpress.nightcore.locale.entry.TextLocale;

import static su.nightexpress.combatpets.Placeholders.*;
import static su.nightexpress.combatpets.pet.AttributeRegistry.ARMOR;
import static su.nightexpress.combatpets.pet.AttributeRegistry.ATTACK_DAMAGE;
import static su.nightexpress.combatpets.pet.AttributeRegistry.ATTACK_SPEED;
import static su.nightexpress.combatpets.pet.AttributeRegistry.FLYING_SPEED;
import static su.nightexpress.combatpets.pet.AttributeRegistry.HEALTH_REGENEATION_FORCE;
import static su.nightexpress.combatpets.pet.AttributeRegistry.HEALTH_REGENEATION_SPEED;
import static su.nightexpress.combatpets.pet.AttributeRegistry.MAX_HEALTH;
import static su.nightexpress.combatpets.pet.AttributeRegistry.MAX_SATURATION;
import static su.nightexpress.combatpets.pet.AttributeRegistry.MOVEMENT_SPEED;
import static su.nightexpress.nightcore.util.text.night.wrapper.TagWrappers.*;

/**
 * Texts of the Bedrock Edition forms.
 * <p>
 * Bedrock forms only support the 16 classic colors plus bold/italic, so the defaults use plain colors.
 * Hex colors (e.g. from tier names) are automatically converted to the closest classic color.
 * Multi-line entries are joined with {@code <br>} and shown as separate lines.
 */
public class BedrockLang implements LangContainer {

    /** Placeholder for the current page number (1-based). */
    public static final String PAGE  = "%page%";
    /** Placeholder for the total amount of pages. */
    public static final String PAGES = "%pages%";

    // ------------------------------------------------------------------------------------------
    // Shared buttons
    // ------------------------------------------------------------------------------------------

    public static final TextLocale BUTTON_BACK      = LangEntry.builder("Bedrock.Button.Back").text(DARK_GRAY.wrap("« Back"));
    public static final TextLocale BUTTON_PREV_PAGE = LangEntry.builder("Bedrock.Button.PreviousPage").text(DARK_GRAY.wrap("« Previous Page"));
    public static final TextLocale BUTTON_NEXT_PAGE = LangEntry.builder("Bedrock.Button.NextPage").text(DARK_GRAY.wrap("Next Page »"));
    public static final TextLocale BUTTON_CONFIRM   = LangEntry.builder("Bedrock.Button.Confirm").text("Confirm");
    public static final TextLocale BUTTON_CANCEL    = LangEntry.builder("Bedrock.Button.Cancel").text("Cancel");

    // ------------------------------------------------------------------------------------------
    // Pet collection: tiers
    // ------------------------------------------------------------------------------------------

    public static final TextLocale TIERS_TITLE   = LangEntry.builder("Bedrock.Tiers.Title").text("Pet Collection");
    public static final TextLocale TIERS_CONTENT = LangEntry.builder("Bedrock.Tiers.Content").text(
        GRAY.wrap("Choose a tier to browse the pets you own.")
    );
    public static final TextLocale TIERS_BUTTON  = LangEntry.builder("Bedrock.Tiers.Button").text(
        TIER_NAME,
        DARK_GRAY.wrap(GENERIC_AMOUNT + " pet(s)")
    );

    // ------------------------------------------------------------------------------------------
    // Pet collection: pets of a tier
    // ------------------------------------------------------------------------------------------

    public static final TextLocale COLLECTION_TITLE         = LangEntry.builder("Bedrock.Collection.Title").text(
        TIER_NAME + " Pets (" + PAGE + "/" + PAGES + ")"
    );
    public static final TextLocale COLLECTION_CONTENT       = LangEntry.builder("Bedrock.Collection.Content").text(
        GRAY.wrap("Tap a pet to summon, despawn, revive or release it.")
    );
    public static final TextLocale COLLECTION_CONTENT_EMPTY = LangEntry.builder("Bedrock.Collection.Content_Empty").text(
        GRAY.wrap("You don't have any pets of this tier yet.")
    );
    public static final TextLocale COLLECTION_BUTTON        = LangEntry.builder("Bedrock.Collection.Button").text(
        BLACK.wrap(PET_NAME) + DARK_GRAY.wrap(" (Lv. " + PET_LEVEL + ")"),
        GENERIC_TYPE
    );
    public static final TextLocale STATUS_SUMMONED          = LangEntry.builder("Bedrock.Collection.Status.Summoned").text(DARK_GREEN.wrap("Summoned"));
    public static final TextLocale STATUS_IDLE              = LangEntry.builder("Bedrock.Collection.Status.Idle").text(DARK_GRAY.wrap("Idle"));
    public static final TextLocale STATUS_DEAD              = LangEntry.builder("Bedrock.Collection.Status.Dead").text(DARK_RED.wrap("Dead"));
    public static final TextLocale STATUS_DEAD_AUTO         = LangEntry.builder("Bedrock.Collection.Status.Dead_Auto").text(DARK_RED.wrap("Dead - revives in " + GENERIC_TIME));

    // ------------------------------------------------------------------------------------------
    // Pet actions (replaces left-click / Q-drop on a pet in the collection GUI)
    // ------------------------------------------------------------------------------------------

    public static final TextLocale PET_TITLE   = LangEntry.builder("Bedrock.Pet.Title").text(PET_NAME);
    public static final TextLocale PET_CONTENT = LangEntry.builder("Bedrock.Pet.Content").text(
        GRAY.wrap("Tier: ") + TIER_NAME,
        GRAY.wrap("Status: ") + GENERIC_TYPE,
        "",
        YELLOW.wrap(BOLD.wrap("STATS")),
        GRAY.wrap("Level: ") + WHITE.wrap(PET_LEVEL),
        GRAY.wrap("XP: ") + WHITE.wrap(PET_XP + "/" + PET_REQUIRED_XP),
        GRAY.wrap("Saturation: ") + WHITE.wrap(PET_SATURATION + "/" + PET_MAX_SATURATION),
        GRAY.wrap("Food: ") + WHITE.wrap(PET_FOOD),
        "",
        YELLOW.wrap(BOLD.wrap("ATTRIBUTES")),
        GRAY.wrap("Damage: ") + WHITE.wrap(PET_ATTRIBUTE.apply(ATTACK_DAMAGE)),
        GRAY.wrap("Attack Speed: ") + WHITE.wrap(PET_ATTRIBUTE.apply(ATTACK_SPEED) + "/sec."),
        GRAY.wrap("Health: ") + WHITE.wrap(PET_ATTRIBUTE.apply(MAX_HEALTH)),
        GRAY.wrap("Regen: ") + WHITE.wrap(PET_ATTRIBUTE.apply(HEALTH_REGENEATION_FORCE) + " x " + PET_ATTRIBUTE.apply(HEALTH_REGENEATION_SPEED) + "/sec."),
        GRAY.wrap("Defense: ") + WHITE.wrap(PET_ATTRIBUTE.apply(ARMOR)),
        GRAY.wrap("Speed: ") + WHITE.wrap("W: " + PET_ATTRIBUTE.apply(MOVEMENT_SPEED) + " / F: " + PET_ATTRIBUTE.apply(FLYING_SPEED))
    );
    public static final TextLocale PET_CONTENT_DUELS = LangEntry.builder("Bedrock.Pet.Content_Duels").text(
        GRAY.wrap("Duels: ") + DARK_GREEN.wrap(PET_DUEL_WINS + " won") + GRAY.wrap(" / ") + DARK_RED.wrap(PET_DUEL_LOSSES + " lost")
    );
    public static final TextLocale PET_HINT_RELEASE_ACTIVE = LangEntry.builder("Bedrock.Pet.Hint.Release_Active").text(
        GRAY.wrap("Return this pet to your collection to be able to release it.")
    );

    public static final TextLocale PET_BUTTON_SUMMON  = LangEntry.builder("Bedrock.Pet.Button.Summon").text(DARK_GREEN.wrap("Summon Pet"));
    public static final TextLocale PET_BUTTON_DESPAWN = LangEntry.builder("Bedrock.Pet.Button.Despawn").text("Return to Collection");
    public static final TextLocale PET_BUTTON_MANAGE  = LangEntry.builder("Bedrock.Pet.Button.Manage").text("Open Pet Menu");
    public static final TextLocale PET_BUTTON_REVIVE  = LangEntry.builder("Bedrock.Pet.Button.Revive").text(
        DARK_GREEN.wrap("Revive Pet"),
        DARK_GRAY.wrap("Cost: " + TIER_DEATH_REVIVE_COST)
    );
    public static final TextLocale PET_BUTTON_RELEASE = LangEntry.builder("Bedrock.Pet.Button.Release").text(DARK_RED.wrap("Release Pet"));

    // ------------------------------------------------------------------------------------------
    // Confirmations
    // ------------------------------------------------------------------------------------------

    public static final TextLocale REVIVE_TITLE   = LangEntry.builder("Bedrock.Revive.Title").text("Revive the pet?");
    public static final TextLocale REVIVE_CONTENT = LangEntry.builder("Bedrock.Revive.Content").text(
        GRAY.wrap("Revive " + WHITE.wrap(PET_NAME) + " (" + TIER_NAME + GRAY.wrap(")?")),
        "",
        GRAY.wrap("It will cost you " + WHITE.wrap(TIER_DEATH_REVIVE_COST) + ".")
    );
    public static final TextLocale REVIVE_BUTTON  = LangEntry.builder("Bedrock.Revive.Button").text("Revive");

    public static final TextLocale RELEASE_TITLE   = LangEntry.builder("Bedrock.Release.Title").text("Release the pet?");
    public static final TextLocale RELEASE_CONTENT = LangEntry.builder("Bedrock.Release.Content").text(
        GRAY.wrap("Release " + WHITE.wrap(PET_NAME) + " (" + TIER_NAME + GRAY.wrap(") into the wild?")),
        "",
        RED.wrap(BOLD.wrap("The pet and all its progress will be lost forever!"))
    );
    public static final TextLocale RELEASE_BUTTON  = LangEntry.builder("Bedrock.Release.Button").text("Release");

    // 'Store in Egg' mode (Pets.Release.Mode: STORE)
    public static final TextLocale PET_BUTTON_STORE = LangEntry.builder("Bedrock.Pet.Button.Store").text(GOLD.wrap("Store in Egg"));
    public static final TextLocale STORE_TITLE      = LangEntry.builder("Bedrock.Store.Title").text("Store the pet in an egg?");
    public static final TextLocale STORE_CONTENT    = LangEntry.builder("Bedrock.Store.Content").text(
        GRAY.wrap("Store " + WHITE.wrap(PET_NAME) + " (" + TIER_NAME + GRAY.wrap(") in an egg item? It keeps its name, level, aspects and stats, and anyone can hatch it. Its inventory and equipment drop at your feet.") + "\n\n" + GRAY.wrap("Cost: ") + WHITE.wrap(PET_STORE_COST)));
    public static final TextLocale STORE_BUTTON     = LangEntry.builder("Bedrock.Store.Button").text("Store in Egg");

    // ------------------------------------------------------------------------------------------
    // Active pet menu (replaces the Pet Menu GUI)
    // ------------------------------------------------------------------------------------------

    public static final TextLocale MENU_TITLE   = LangEntry.builder("Bedrock.Menu.Title").text(PET_NAME);
    public static final TextLocale MENU_CONTENT = LangEntry.builder("Bedrock.Menu.Content").text(
        YELLOW.wrap(BOLD.wrap("STATS")),
        GRAY.wrap("Level: ") + WHITE.wrap(PET_LEVEL),
        GRAY.wrap("XP: ") + WHITE.wrap(PET_XP + "/" + PET_REQUIRED_XP),
        GRAY.wrap("Saturation: ") + WHITE.wrap(PET_SATURATION + "/" + PET_MAX_SATURATION),
        GRAY.wrap("Food: ") + WHITE.wrap(PET_FOOD),
        "",
        YELLOW.wrap(BOLD.wrap("ATTRIBUTES")),
        GRAY.wrap("Damage: ") + WHITE.wrap(PET_ATTRIBUTE.apply(ATTACK_DAMAGE)),
        GRAY.wrap("Attack Speed: ") + WHITE.wrap(PET_ATTRIBUTE.apply(ATTACK_SPEED) + "/sec."),
        GRAY.wrap("Health: ") + WHITE.wrap(PET_ATTRIBUTE.apply(MAX_HEALTH)),
        GRAY.wrap("Regen: ") + WHITE.wrap(PET_ATTRIBUTE.apply(HEALTH_REGENEATION_FORCE) + " x " + PET_ATTRIBUTE.apply(HEALTH_REGENEATION_SPEED) + "/sec."),
        GRAY.wrap("Defense: ") + WHITE.wrap(PET_ATTRIBUTE.apply(ARMOR)),
        GRAY.wrap("Speed: ") + WHITE.wrap("W: " + PET_ATTRIBUTE.apply(MOVEMENT_SPEED) + " / F: " + PET_ATTRIBUTE.apply(FLYING_SPEED)),
        "",
        YELLOW.wrap(BOLD.wrap("SETTINGS")),
        GRAY.wrap("Combat Mode: ") + WHITE.wrap(PET_COMBAT_MODE),
        GRAY.wrap("Silent Mode: ") + WHITE.wrap(PET_SILENT)
    );
    public static final TextLocale MENU_CONTENT_DUELS = LangEntry.builder("Bedrock.Menu.Content_Duels").text(
        GRAY.wrap("Duels: ") + WHITE.wrap(PET_DUEL_WINS + " won / " + PET_DUEL_LOSSES + " lost")
    );
    public static final TextLocale MENU_CONTENT_EQUIPMENT = LangEntry.builder("Bedrock.Menu.Content_Equipment").text(
        GRAY.wrap("Equipment Unlocked: ") + WHITE.wrap(PET_EQUIPMENT_UNLOCKED),
        GRAY.wrap("When unlocked, interact with the pet while holding an item to equip it.")
    );

    public static final TextLocale MENU_BUTTON_INVENTORY = LangEntry.builder("Bedrock.Menu.Button.Inventory").text(
        "Pet Inventory",
        DARK_GRAY.wrap(PET_INVENTORY_FILLED + "/" + TIER_INVENTORY_SIZE + " slots used")
    );
    public static final TextLocale MENU_BUTTON_UNEQUIP   = LangEntry.builder("Bedrock.Menu.Button.Unequip").text("Unequip All Items");
    public static final TextLocale MENU_BUTTON_ASPECTS   = LangEntry.builder("Bedrock.Menu.Button.Aspects").text(
        "Aspects",
        DARK_GRAY.wrap(PET_ASPECT_POINTS + " point(s) available")
    );
    public static final TextLocale MENU_BUTTON_SETTINGS  = LangEntry.builder("Bedrock.Menu.Button.Settings").text(
        "Pet Settings",
        DARK_GRAY.wrap("Combat mode, silent mode, equipment")
    );
    public static final TextLocale MENU_BUTTON_RENAME    = LangEntry.builder("Bedrock.Menu.Button.Rename").text("Rename Pet");
    public static final TextLocale MENU_BUTTON_DESPAWN   = LangEntry.builder("Bedrock.Menu.Button.Despawn").text("Return to Collection");

    // ------------------------------------------------------------------------------------------
    // Settings (replaces Combat Mode / Silent / Equipment toggle buttons)
    // ------------------------------------------------------------------------------------------

    public static final TextLocale SETTINGS_TITLE            = LangEntry.builder("Bedrock.Settings.Title").text("Pet Settings");
    public static final TextLocale SETTINGS_LABEL            = LangEntry.builder("Bedrock.Settings.Label").text(
        YELLOW.wrap("Passive: ") + GRAY.wrap("Never attacks."),
        YELLOW.wrap("Protective: ") + GRAY.wrap("Defends owner when attacked."),
        YELLOW.wrap("Supportive: ") + GRAY.wrap("Supports owner's attacks.")
    );
    public static final TextLocale SETTINGS_COMBAT_MODE      = LangEntry.builder("Bedrock.Settings.CombatMode").text("Combat Mode");
    public static final TextLocale SETTINGS_SILENT           = LangEntry.builder("Bedrock.Settings.Silent").text("Silent Mode (disables pet sounds)");
    public static final TextLocale SETTINGS_EQUIPMENT        = LangEntry.builder("Bedrock.Settings.Equipment").text("Equipment Unlocked");
    public static final TextLocale SETTINGS_DUELS            = LangEntry.builder("Bedrock.Settings.Duels").text("Accept Pet Duel Challenges");

    // ------------------------------------------------------------------------------------------
    // Rename
    // ------------------------------------------------------------------------------------------

    public static final TextLocale RENAME_TITLE             = LangEntry.builder("Bedrock.Rename.Title").text("Rename Pet");
    public static final TextLocale RENAME_LABEL             = LangEntry.builder("Bedrock.Rename.Label").text(
        GRAY.wrap("Current name: ") + WHITE.wrap(PET_NAME),
        GRAY.wrap("Name length: ") + WHITE.wrap(GENERIC_AMOUNT + " - " + GENERIC_MAX) + GRAY.wrap(" characters.")
    );
    public static final TextLocale RENAME_LABEL_NAMETAG     = LangEntry.builder("Bedrock.Rename.Label_NameTag").text(
        YELLOW.wrap("Renaming costs 1 Name Tag from your inventory.")
    );
    public static final TextLocale RENAME_INPUT             = LangEntry.builder("Bedrock.Rename.Input").text("New name");
    public static final TextLocale RENAME_INPUT_PLACEHOLDER = LangEntry.builder("Bedrock.Rename.Input_Placeholder").text("Type a new name...");

    // ------------------------------------------------------------------------------------------
    // Aspects (replaces the Aspects GUI)
    // ------------------------------------------------------------------------------------------

    public static final TextLocale ASPECTS_TITLE   = LangEntry.builder("Bedrock.Aspects.Title").text("Pet Aspects");
    public static final TextLocale ASPECTS_CONTENT = LangEntry.builder("Bedrock.Aspects.Content").text(
        GRAY.wrap("Aspect Points: ") + WHITE.wrap(PET_ASPECT_POINTS),
        "",
        GRAY.wrap("Tap an aspect to see its bonuses and upgrade it.")
    );
    public static final TextLocale ASPECTS_BUTTON  = LangEntry.builder("Bedrock.Aspects.Aspect_Button").text(
        ASPECT_NAME + DARK_GRAY.wrap(" (" + GENERIC_VALUE + "/" + GENERIC_MAX + ")"),
        GENERIC_TYPE
    );
    public static final TextLocale ASPECTS_STATE_UPGRADE   = LangEntry.builder("Bedrock.Aspects.State.Upgrade").text(DARK_GREEN.wrap("Can be upgraded"));
    public static final TextLocale ASPECTS_STATE_MAX       = LangEntry.builder("Bedrock.Aspects.State.Max").text(DARK_GRAY.wrap("Max level"));
    public static final TextLocale ASPECTS_STATE_NO_POINTS = LangEntry.builder("Bedrock.Aspects.State.NoPoints").text(DARK_GRAY.wrap("No points available"));
    public static final TextLocale ASPECTS_BUTTON_REALLOCATE = LangEntry.builder("Bedrock.Aspects.Reallocate_Button").text(
        DARK_RED.wrap("Reallocate Points"),
        DARK_GRAY.wrap("Reset all aspects & refund points")
    );

    public static final TextLocale ASPECT_TITLE     = LangEntry.builder("Bedrock.Aspect.Title").text(ASPECT_NAME);
    public static final TextLocale ASPECT_CONTENT   = LangEntry.builder("Bedrock.Aspect.Content").text(
        GRAY.wrap("Level: ") + WHITE.wrap(GENERIC_VALUE + "/" + GENERIC_MAX),
        GRAY.wrap("Aspect Points: ") + WHITE.wrap(PET_ASPECT_POINTS),
        "",
        YELLOW.wrap(BOLD.wrap("BONUSES PER LEVEL")),
        GENERIC_TYPE
    );
    public static final TextLocale ASPECT_ATTRIBUTE = LangEntry.builder("Bedrock.Aspect.Attribute").text(
        GRAY.wrap(GENERIC_NAME + ": ") + WHITE.wrap("+" + GENERIC_VALUE) + GRAY.wrap(" (total: " + GENERIC_TOTAL + ")")
    );
    public static final TextLocale ASPECT_BUTTON_UPGRADE = LangEntry.builder("Bedrock.Aspect.Button.Upgrade").text(
        DARK_GREEN.wrap("Upgrade (+1)"),
        DARK_GRAY.wrap("Costs 1 aspect point")
    );

    public static final TextLocale REALLOCATE_TITLE   = LangEntry.builder("Bedrock.Reallocate.Title").text("Reallocate points?");
    public static final TextLocale REALLOCATE_CONTENT = LangEntry.builder("Bedrock.Reallocate.Content").text(
        GRAY.wrap("All aspects of " + WHITE.wrap(PET_NAME) + " will be reset"),
        GRAY.wrap("and the spent points will be refunded.")
    );
    public static final TextLocale REALLOCATE_BUTTON  = LangEntry.builder("Bedrock.Reallocate.Button").text("Reallocate");

    // ------------------------------------------------------------------------------------------
    // Egg shop
    // ------------------------------------------------------------------------------------------

    public static final TextLocale SHOP_TIERS_TITLE   = LangEntry.builder("Bedrock.Shop.Tiers.Title").text("Egg Shop");
    public static final TextLocale SHOP_TIERS_CONTENT = LangEntry.builder("Bedrock.Shop.Tiers.Content").text(
        GRAY.wrap("Select a tier to see the available pet eggs.")
    );
    public static final TextLocale SHOP_TIERS_BUTTON  = LangEntry.builder("Bedrock.Shop.Tiers.Button").text(TIER_NAME);

    public static final TextLocale SHOP_EGGS_TITLE         = LangEntry.builder("Bedrock.Shop.Eggs.Title").text(
        TIER_NAME + " Eggs (" + PAGE + "/" + PAGES + ")"
    );
    public static final TextLocale SHOP_EGGS_CONTENT       = LangEntry.builder("Bedrock.Shop.Eggs.Content").text(
        GRAY.wrap("Tap an egg to see its details and buy it.")
    );
    public static final TextLocale SHOP_EGGS_CONTENT_EMPTY = LangEntry.builder("Bedrock.Shop.Eggs.Content_Empty").text(
        GRAY.wrap("There are no eggs for sale in this tier.")
    );
    public static final TextLocale SHOP_EGGS_BUTTON        = LangEntry.builder("Bedrock.Shop.Eggs.Button").text(
        TEMPLATE_DEFAULT_NAME,
        DARK_GREEN.wrap(GENERIC_PRICE)
    );

    public static final TextLocale SHOP_CONFIRM_TITLE   = LangEntry.builder("Bedrock.Shop.Confirm.Title").text("Buy this egg?");
    public static final TextLocale SHOP_CONFIRM_CONTENT = LangEntry.builder("Bedrock.Shop.Confirm.Content").text(
        GRAY.wrap("Pet: ") + WHITE.wrap(TEMPLATE_DEFAULT_NAME),
        GRAY.wrap("Tier: ") + TIER_NAME,
        GRAY.wrap("Price: ") + GREEN.wrap(GENERIC_PRICE),
        "",
        YELLOW.wrap(BOLD.wrap("TIER BONUSES")),
        GRAY.wrap("Inventory: ") + WHITE.wrap(TIER_INVENTORY_HAS) + GRAY.wrap(" (" + TIER_INVENTORY_SIZE + " slots)"),
        GRAY.wrap("Equipment: ") + WHITE.wrap(TIER_EQUIPMENT_HAS),
        "",
        YELLOW.wrap(BOLD.wrap("BASE STATS")),
        GRAY.wrap("Max. Saturation: ") + WHITE.wrap(PET_CONFIG_ATTRIBUTE_START.apply(MAX_SATURATION)),
        GRAY.wrap("Damage: ") + WHITE.wrap(PET_CONFIG_ATTRIBUTE_START.apply(ATTACK_DAMAGE)),
        GRAY.wrap("Health: ") + WHITE.wrap(PET_CONFIG_ATTRIBUTE_START.apply(MAX_HEALTH)),
        GRAY.wrap("Defense: ") + WHITE.wrap(PET_CONFIG_ATTRIBUTE_START.apply(ARMOR)),
        GRAY.wrap("Speed: ") + WHITE.wrap(PET_CONFIG_ATTRIBUTE_START.apply(MOVEMENT_SPEED))
    );
    public static final TextLocale SHOP_CONFIRM_BUTTON  = LangEntry.builder("Bedrock.Shop.Confirm.Button").text("Buy");

    // ------------------------------------------------------------------------------------------
    // Messages
    // ------------------------------------------------------------------------------------------

    public static final MessageLocale ERROR_INPUT_EMPTY = LangEntry.builder("Bedrock.Error.InputEmpty").chatMessage(
        RED.wrap("Please enter a value.")
    );

    public static final MessageLocale ERROR_PET_GONE = LangEntry.builder("Bedrock.Error.PetGone").chatMessage(
        RED.wrap("That pet is no longer available.")
    );

    public static final MessageLocale SETTINGS_SAVED = LangEntry.builder("Bedrock.Settings.Saved").chatMessage(
        GRAY.wrap("Pet settings saved.")
    );
}
