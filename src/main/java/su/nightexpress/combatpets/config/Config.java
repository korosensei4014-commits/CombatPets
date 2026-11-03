package su.nightexpress.combatpets.config;

import org.bukkit.Material;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.inventory.ItemStack;
import su.nightexpress.combatpets.Placeholders;
import su.nightexpress.combatpets.util.PetCreator;
import su.nightexpress.nightcore.config.ConfigValue;
import su.nightexpress.nightcore.util.*;

import java.util.Map;
import java.util.Set;

import static su.nightexpress.combatpets.Placeholders.*;
import static su.nightexpress.nightcore.util.text.tag.Tags.*;

public class Config {

    public static final String DIR_MENU  = "/menu/";
    public static final String DIR_TIERS = "/pets/tiers/";
    public static final String DIR_PETS  = "/pets/configs/";

    @Deprecated
    public static final ConfigValue<Boolean> GENERAL_PLACEHOLDER_API_GUI = ConfigValue.create("General.PlaceholderAPI_In_GUI",
        false,
        "When enabled, applies PlaceholderAPI placeholders to GUI title and items."
    );

    public static final ConfigValue<Boolean> GENERAL_COLLECTION_DEFAULT_COMMAND = ConfigValue.create("General.Collection_As_Default_Command",
        false,
        "Sets whether Pets Collection GUI should open if no command argument specified.");

    public static final ConfigValue<Boolean> FEATURES_LEVELING_ENABLED = ConfigValue.create("Features.Leveling",
        true,
        "Sets whether or not Leveling feature is enabled."
    );

    public static final ConfigValue<Boolean> FEATURES_CAPTURE = ConfigValue.create("Features.Capture",
        true,
        "Sets whether or not Capture feature is enabled."
    );

    public static final ConfigValue<Boolean> FEATURES_WARDROBE = ConfigValue.create("Features.Accessories",
        true,
        "Sets whether or not Accessories feature is enabled."
    );

    public static final ConfigValue<Boolean> FEATURES_SHOP = ConfigValue.create("Features.Shop",
        true,
        "Sets whether or not Shop feature is enabled."
    );



    public static final ConfigValue<ItemStack> ITEM_MYSTERY_EGG = ConfigValue.create("Items.Mystery_Egg",
        PetCreator.getDefaultMysteryEgg(),
        "Mystery egg item."
    );



    public static final ConfigValue<ItemStack> PET_EGG_ITEM = ConfigValue.create("Pets.Egg_Item",
        PetCreator.getDefaultEgg(),
        "Egg item.",
        "[*] You should use '" + BukkitThing.toString(Material.PLAYER_HEAD) + "' as item Material to apply pet's egg texture."
    );

    public static final ConfigValue<Set<String>> PET_DISABLED_WORLDS = ConfigValue.create("Pets.Disabled_Worlds",
        Lists.newSet("custom_world"),
        "List of worlds, where pets are disabled completely."
    );

    public static final ConfigValue<Boolean> PET_RELEASE_ALLOWED = ConfigValue.create("Pets.Release.Allowed",
        true,
        "Sets whether or not players can release their pets."
    );

    public static final ConfigValue<Boolean> PET_RELEASE_NATURAL = ConfigValue.create("Pets.Release.Natural",
        true,
        "When enabled, spawns similar to pet entity when releasing."
    );

    public static final ConfigValue<Set<String>> PET_RELEASE_DISABLED_WORLDS = ConfigValue.create("Pets.Release.BadWorlds",
        Lists.newSet("spawn", "other_world"),
        "List of worlds, where players can not release their pets."
    );

    public static final ConfigValue<Boolean> PET_PVP_ALLOWED = ConfigValue.create("Pets.PVP_Allowed",
        true,
        "Sets whether or not player's pets can attack each other."
    );

    public static final ConfigValue<Double> PET_FOLLOW_START_DISTANCE = ConfigValue.create("Pets.Follow.Start_Distance",
        6D,
        "Pets only start walking to their owner when they are farther than this many blocks.",
        "Closer than this, the pet stays where it is instead of constantly walking up to you.");

    public static final ConfigValue<Double> PET_FOLLOW_STOP_DISTANCE = ConfigValue.create("Pets.Follow.Stop_Distance",
        3D,
        "While following, pets stop once they are this close to their owner (in blocks).",
        "Must be lower than Start_Distance. Big pets keep a little more space automatically.");

    public static final ConfigValue<Boolean> PET_FIREBALLS_IGNORE_FIRE_IMMUNITY = ConfigValue.create("Pets.Fireballs_Ignore_Fire_Immunity",
        true,
        "When enabled, fireballs shot by pets (Ghast, Blaze) also hurt fire-immune targets they're allowed to attack",
        "(Wither, Warden, Ghast, Blaze, ...). Their hit counts as normal projectile damage instead of fire damage.",
        "Fire immunity still works for everything else (lava, burning, vanilla mobs' fireballs)."
    );

    public static final ConfigValue<Boolean> PET_WITHER_DESTROY_BLOCKS = ConfigValue.create("Pets.Wither.Destroy_Blocks",
        false,
        "When disabled, Wither pets don't break blocks around them after taking damage (vanilla Wither behaviour).",
        "Explosions from pets never break blocks either way."
    );

    public static final ConfigValue<Boolean> PET_EVOKER_SUMMON_VEXES = ConfigValue.create("Pets.Evoker.Summon_Vexes",
        false,
        "When enabled, Evoker pets also use the vanilla Vex summoning spell (they always use the fang spell).",
        "Summoned Vexes only attack the Evoker's target, follow the same damage rules as the Evoker",
        "(Pets.PVP_Allowed, WorldGuard pet-pvp, no friendly fire, duels), disappear when the Evoker is gone",
        "and are never saved to the world."
    );

    public static final ConfigValue<Integer> PET_EVOKER_MAX_VEXES = ConfigValue.create("Pets.Evoker.Max_Vexes",
        3,
        "Max. Vexes alive at once per Evoker pet. One spell summons up to 3 (vanilla)."
    );

    public static final ConfigValue<Integer> PET_EVOKER_VEX_LIFETIME = ConfigValue.create("Pets.Evoker.Vex_Lifetime",
        30,
        "Seconds before a summoned Vex starts dying on its own. Vanilla: 30-120."
    );

    public static final ConfigValue<Boolean> PET_RAVAGER_DESTROY_BLOCKS = ConfigValue.create("Pets.Ravager.Destroy_Blocks",
        false,
        "When disabled, Ravager pets don't break leaves they walk into (vanilla Ravager behaviour)."
    );

    // ---- Mounts: right-click to ride, shift + right-click opens the menu (like horses). ----
    public static final ConfigValue<Integer> PET_MOUNT_SYNC_INTERVAL = ConfigValue.create("Pets.Mounts.Sync_Interval",
        2,
        "Ticks between movement updates sent to players while a mount (Ravager, Pig, ...) is ridden.",
        "1 = smoothest, 2 = same rate as players, 3 = vanilla mob rate (least network traffic).",
        "Only affects mounts that are being ridden. Horses and Camels are not affected.");

    public static final ConfigValue<Boolean> PET_MOUNT_RAVAGER = ConfigValue.create("Pets.Mounts.Ravager",
        true,
        "When enabled, the owner can ride this pet (right-click) and steer it like a horse.");

    public static final ConfigValue<Boolean> PET_MOUNT_POLAR_BEAR = ConfigValue.create("Pets.Mounts.Polar_Bear",
        true,
        "When enabled, the owner can ride this pet (right-click) and steer it like a horse.");

    public static final ConfigValue<Boolean> PET_MOUNT_HOGLIN = ConfigValue.create("Pets.Mounts.Hoglin",
        true,
        "When enabled, the owner can ride this pet (right-click) and steer it like a horse.");

    public static final ConfigValue<Boolean> PET_MOUNT_ZOGLIN = ConfigValue.create("Pets.Mounts.Zoglin",
        true,
        "When enabled, the owner can ride this pet (right-click) and steer it like a horse.");

    public static final ConfigValue<Boolean> PET_MOUNT_STRIDER = ConfigValue.create("Pets.Mounts.Strider",
        true,
        "When enabled, the owner can ride this pet (right-click) and steer it like a horse.");

    public static final ConfigValue<Boolean> PET_MOUNT_PIG = ConfigValue.create("Pets.Mounts.Pig",
        true,
        "When enabled, the owner can ride this pet (right-click) and steer it like a horse.");

    public static final ConfigValue<Boolean> PET_MOUNT_SNIFFER = ConfigValue.create("Pets.Mounts.Sniffer",
        true,
        "When enabled, the owner can ride this pet (right-click) and steer it like a horse.");

    public static final ConfigValue<Boolean> PET_MOUNT_SPIDER = ConfigValue.create("Pets.Mounts.Spider",
        true,
        "When enabled, the owner can ride this pet (right-click) and steer it like a horse.");

    public static final ConfigValue<Boolean> PET_MOUNT_IRON_GOLEM = ConfigValue.create("Pets.Mounts.Iron_Golem",
        true,
        "When enabled, the owner can ride this pet (right-click) and steer it like a horse.");

    /**
     * @return Pet types that can be ridden according to Pets.Mounts.*.
     */
    public static java.util.Set<org.bukkit.entity.EntityType> getEnabledMounts() {
        java.util.Set<org.bukkit.entity.EntityType> types = new java.util.HashSet<>();
        java.util.Map.ofEntries(
            java.util.Map.entry(org.bukkit.entity.EntityType.RAVAGER, PET_MOUNT_RAVAGER),
            java.util.Map.entry(org.bukkit.entity.EntityType.POLAR_BEAR, PET_MOUNT_POLAR_BEAR),
            java.util.Map.entry(org.bukkit.entity.EntityType.HOGLIN, PET_MOUNT_HOGLIN),
            java.util.Map.entry(org.bukkit.entity.EntityType.ZOGLIN, PET_MOUNT_ZOGLIN),
            java.util.Map.entry(org.bukkit.entity.EntityType.STRIDER, PET_MOUNT_STRIDER),
            java.util.Map.entry(org.bukkit.entity.EntityType.PIG, PET_MOUNT_PIG),
            java.util.Map.entry(org.bukkit.entity.EntityType.SNIFFER, PET_MOUNT_SNIFFER),
            java.util.Map.entry(org.bukkit.entity.EntityType.SPIDER, PET_MOUNT_SPIDER),
            java.util.Map.entry(org.bukkit.entity.EntityType.IRON_GOLEM, PET_MOUNT_IRON_GOLEM)
        ).forEach((type, option) -> {
            if (option.get()) types.add(type);
        });
        return types;
    }

    public static final ConfigValue<Boolean> PET_WARDEN_SONIC_BOOM = ConfigValue.create("Pets.Warden.Sonic_Boom",
        true,
        "When enabled, Warden pets use the vanilla sonic boom when they can't hit their target in melee",
        "(e.g. a flying pet, or a player who pillared up). Range: 15 blocks sideways, 20 up/down.",
        "Like vanilla it goes through blocks and ignores armor and shields. Normal pet damage rules still apply",
        "(Pets.PVP_Allowed, WorldGuard pet-pvp, no friendly fire, duels)."
    );

    public static final ConfigValue<Double> PET_WARDEN_SONIC_BOOM_DAMAGE = ConfigValue.create("Pets.Warden.Sonic_Boom_Damage",
        10D,
        "Damage of a Warden pet's sonic boom. Vanilla: 10."
    );

    public static final ConfigValue<Boolean> PET_DUEL_ENABLED = ConfigValue.create("Pets.Duel.Enabled",
        true,
        "Enables pet duels: '/pets duel <player>' challenges another player's summoned pet.",
        "The losing pet really dies (Pets death rules apply) and must be revived.");

    public static final ConfigValue<Boolean> PET_DUEL_DEFAULT_ENABLED = ConfigValue.create("Pets.Duel.Default_Enabled",
        true,
        "Whether players accept duel challenges until they change it with '/pets duels on|off'.",
        "Players who already changed the setting keep their choice.");

    public static final ConfigValue<Boolean> PET_DUEL_DISABLE_HEALING = ConfigValue.create("Pets.Duel.Disable_Healing",
        true,
        "Disables all healing of the two pets during a duel (pet regeneration, the Wither's self-heal, etc.).",
        "Pets are healed to full when the duel starts."
    );

    public static final ConfigValue<Boolean> PET_DUEL_SURRENDER_KILLS_PET = ConfigValue.create("Pets.Duel.Surrender_Kills_Pet",
        false,
        "When false, '/pets surrender' ends the duel as a loss but the surrendering pet survives.",
        "When true, the surrendering pet dies like it lost the fight."
    );

    public static final ConfigValue<String> PET_DUEL_SOUND_COUNTDOWN = ConfigValue.create("Pets.Duel.Sounds.Countdown",
        "minecraft:block.note_block.pling",
        "Sound played on each countdown number (pitch rises: 3, 2, 1). Leave empty to disable.");

    public static final ConfigValue<String> PET_DUEL_SOUND_START = ConfigValue.create("Pets.Duel.Sounds.Start",
        "minecraft:block.note_block.pling",
        "Sound played when the fight starts (high pitch). Leave empty to disable.");

    public static final ConfigValue<String> PET_DUEL_SOUND_WIN = ConfigValue.create("Pets.Duel.Sounds.Win",
        "minecraft:ui.toast.challenge_complete",
        "Sound played to the winner. Leave empty to disable.");

    public static final ConfigValue<String> PET_DUEL_SOUND_LOSE = ConfigValue.create("Pets.Duel.Sounds.Lose",
        "minecraft:block.note_block.didgeridoo",
        "Sound played to the loser (also when surrendering). Leave empty to disable.");

    public static final ConfigValue<String> PET_DUEL_SOUND_DRAW = ConfigValue.create("Pets.Duel.Sounds.Draw",
        "minecraft:block.note_block.bell",
        "Sound played to both players on a draw. Leave empty to disable.");

    public static final ConfigValue<Integer> PET_DUEL_REQUEST_TIMEOUT = ConfigValue.create("Pets.Duel.Request_Timeout",
        30,
        "Seconds a duel challenge stays valid.");

    public static final ConfigValue<Integer> PET_DUEL_COUNTDOWN = ConfigValue.create("Pets.Duel.Countdown",
        3,
        "Countdown in seconds before the pets start fighting.");

    public static final ConfigValue<Integer> PET_DUEL_MAX_DURATION = ConfigValue.create("Pets.Duel.Max_Duration",
        120,
        "Maximum fight length in seconds. When time runs out the duel ends in a draw (no pet dies).");

    public static final ConfigValue<Double> PET_DUEL_MAX_DISTANCE = ConfigValue.create("Pets.Duel.Max_Distance",
        24D,
        "Players must be within this many blocks of each other to start a duel.",
        "The duel is cancelled (no winner) if the pets get more than twice this far apart.");

    public static final ConfigValue<Boolean> PET_SNEAK_TO_OPEN_MENU = ConfigValue.create("Pets.Sneak_To_Open_Menu",
        false,
        "Sets whether or not player should sneak to open pet menu on right click."
    );

    public static final ConfigValue<Boolean> PET_ATTACK_DAMAGE_FOR_PROJECTILES = ConfigValue.create("Pets.Attack_Damage_For_Projectiles",
        true,
        "Sets whether or not pet's 'Attack Damage' attribute will override damage for ranged (non-melee) attacks.",
        "By default, game does not uses 'Attack Damage' attribute when you (or mob) shoot arrows, fireballs, tridents, etc.",
        "This setting will change that."
    );

    public static final ConfigValue<Boolean> PET_PROJECTILE_DAMAGE_NOT_BELOW_ORIGINAL = ConfigValue.create("Pets.Projectile_Damage_Not_Below_Original",
        true,
        "[ONLY WHEN 'Attack_Damage_For_Projectiles' IS ENABLED]",
        "When enabled, pet projectiles never deal less than their normal (vanilla) damage.",
        "Without this, pets with no melee attack (Ghast, Wither, Blaze, ...) have Attack Damage 1,",
        "so e.g. a Ghast fireball (6 damage) only deals ~2.5."
    );

    public static final ConfigValue<Double> PET_ORIGINAL_DAMAGE_FROM_PROJECTILES = ConfigValue.create("Pets.Original_Damage_From_Projectiles",
        0.25D,
        "[ONLY WHEN 'Attack_Damage_For_Projectiles' IS ENABLED]",
        "Sets multiplier for original damage caused by pet's ranged attacks to be added to 'Attack Damage' attribute.",
        "This will help in balancing ranged attacks damage especially for enchanted items, so they inflicts more damage."
    );

    public static final ConfigValue<Integer> PET_SATURATION_PERCENT_TO_REGEN = ConfigValue.create("Pets.Saturation_Percent_To_Regen",
        70,
        "Sets for how many percents a pet should be satiated to be able for natural health regen.");

    public static final ConfigValue<Double> PET_DAMAGE_REGEN_COOLDOWN = ConfigValue.create("Pets.Damage_Regen_Cooldown",
        5D,
        "Sets natural regeneration cooldown (in seconds) when a pet was damaged.");

    public static final ConfigValue<Boolean> PET_PERMANENT_DEATH = ConfigValue.create("Pets.Permanent_Deaths",
        false,
        "Sets whether or not pet will be removed from player's collection when pet dies.");

    public static final ConfigValue<Boolean> PET_DROP_INVENTORY = ConfigValue.create("Pets.Drop_Inventory",
        true,
        "Sets whether or not pet will drop items from their inventories on death.");

    public static final ConfigValue<Boolean> PET_DROP_EQUIPMENT = ConfigValue.create("Pets.Drop_Equipment",
        true,
        "Sets whether or not pet will drop their armors on death.");

    public static final ConfigValue<Boolean> PET_REALLOCATE_ASPECTS = ConfigValue.create("Pets.Allow_Reallocate_Aspect_Points",
        false,
        "Sets whether or not players can reallocate pet's aspects by using the Reallocate Points button in the GUI.");

    //public static final ConfigValue<Boolean> PET_DAMAGE_EQUIPMENT = ConfigValue.create("Pets.Damage_Equipment", true,
    //    "Sets whether or not pet equipment can be damaged.");

    public static final ConfigValue<Boolean> PET_AUTO_FOOD_ENABLED = ConfigValue.create("Pets.AutoFoodUsage.Enabled",
        false,
        "Sets whether or not pets will auto use food from their inventories.");

    public static final ConfigValue<Double> PET_AUTO_FOOD_PERCENT = ConfigValue.create("Pets.AutoFoodUsage.At_Saturation",
        75D,
        "Sets saturation percent for a pet to start use food.",
        "Default is 75, so pet will auto-eat when saturation drops to 75% or lower.");

    public static final ConfigValue<Boolean> PET_POP_DEFAULT_EQUIPMENT = ConfigValue.create("Pets.Populate_Default_Equipment",
        true,
        "Sets whether or not captured pets will get vanilla's mob equipment instead of being empty.",
        "This option is useful to make skeleton pets with bows by default, piglins with swords, etc."
    );



    public static final ConfigValue<Integer> PET_NAME_LENGTH_MAX = ConfigValue.create("Pets.Name.Max_Length",
        16,
        "Sets the characters limit for pet names.");

    public static final ConfigValue<Integer> PET_NAME_LENGTH_MIN = ConfigValue.create("Pets.Name.Min_Length",
        3,
        "Sets min characters amount for pet names.");

    public static final ConfigValue<Set<String>> PET_NAME_BLOCKED_WORDS = ConfigValue.create("Pets.Name.ForbiddenWords",
        Lists.newSet("asshole", "dick", "penis", "ass", "bitch"),
        "A list of words that are forbidden to be in pet names.");

    public static final ConfigValue<Boolean> PET_NAME_RENAME_ALLOW_NAMETAGS = ConfigValue.create("Pets.Name.Rename.Allow_NameTags",
        true,
        "Sets whether or not Name tag items can be used on pets to rename them.");

    public static final ConfigValue<Boolean> PET_NAME_RENAME_USE_DIALOG = ConfigValue.create("Pets.Name.Rename.Use_Dialog",
        true,
        "When enabled, Java Edition players rename pets in a dialog window (text box + Save/Back buttons).",
        "When disabled, the old chat prompt is used. Bedrock players always get a Bedrock form.");

    public static final ConfigValue<Boolean> PET_NAME_RENAME_MENU_REQUIRES_NAMETAG = ConfigValue.create("Pets.Name.Rename.Menu_Requires_NameTag",
        true,
        "Sets whether or not Rename button in Pet Menu will require Name tag item in player's inventory.");



    public static final ConfigValue<Boolean>  PET_HEALTHBAR_ENABLED = ConfigValue.create("Pets.Healthbar.Enabled",
        true,
        "Sets whether or not to use boss bar indicating pet health.");

    public static final ConfigValue<String> PET_HEALTHBAR_TITLE = ConfigValue.create("Pets.Healthbar.Title",
        LIGHT_YELLOW.wrap(PET_NAME) + "   " + GRAY.wrap("Lv. ") + WHITE.wrap(PET_LEVEL) + "   " + LIGHT_RED.wrap(PET_HEALTH) + GRAY.wrap("/") + LIGHT_RED.wrap(PET_MAX_HEALTH + " ❤"),
        "Sets healthbar title.",
        "You can use 'Pet' placeholders: " + WIKI_PLACEHOLDERS_URL
    );

    public static final ConfigValue<BarStyle> PET_HEALTHBAR_STYLE = ConfigValue.create("Pets.Healthbar.Style",
        BarStyle.class, BarStyle.SOLID,
        "Sets healthbar style.",
        "Available values: " + StringUtil.inlineEnum(BarStyle.class, ", ")
    );

    public static final ConfigValue<BarColor> PET_HEALTHBAR_COLOR = ConfigValue.create("Pets.Healthbar.Color",
        BarColor.class, BarColor.GREEN,
        "Sets healthbar color.",
        "Available values: " + StringUtil.inlineEnum(BarColor.class, ", ")
    );

    public static final ConfigValue<RankMap<Integer>> PET_AMOUNT_PER_RANK = ConfigValue.create("Pets.Amount_Per_Rank",
        (cfg, path, def) -> RankMap.readInt(cfg, path, -1),
        (cfg, path, map) -> map.write(cfg, path),
        () -> new RankMap<>(
            RankMap.Mode.RANK,
            "pets.amount.",
            -1,
            Map.of(Placeholders.DEFAULT, 10, "vip", 20, "admin", -1)
        ),
        "Sets how many pets (per each tier) players can have depends on their permission group.",
        "Use '" + DEFAULT + "' for any groups not listed here.",
        "Use '-1' for unlimited amount."
    );

    // ------------------------------------------------------------------------------------------
    // Bedrock Edition (Geyser / Floodgate) support
    // ------------------------------------------------------------------------------------------

    public static final ConfigValue<Boolean> BEDROCK_ENABLED = ConfigValue.create("Bedrock.Enabled",
        true,
        "When enabled, Bedrock Edition players (joining through Geyser) get native Bedrock forms",
        "instead of the chest GUIs. Java Edition players are not affected.",
        "Requires Floodgate (recommended) or Geyser-Spigot installed on THIS server.",
        "If neither is installed, this option does nothing and everyone gets the chest GUIs."
    );

    public static final ConfigValue<String> BEDROCK_PROVIDER = ConfigValue.create("Bedrock.Provider",
        "auto",
        "Which API is used to detect Bedrock players and send them forms.",
        "Available values: auto, floodgate, geyser",
        "'auto' prefers Floodgate, then Geyser.",
        "Behind a Velocity/BungeeCord proxy, install Floodgate on BOTH the proxy and this server."
    );

    public static final ConfigValue<Integer> BEDROCK_PAGE_SIZE = ConfigValue.create("Bedrock.Page_Size",
        10,
        "Maximum number of pets / eggs listed on a single page of a Bedrock form."
    );

    public static final ConfigValue<Long> BEDROCK_SUBMIT_COOLDOWN = ConfigValue.create("Bedrock.Submit_Cooldown_Millis",
        300L,
        "Minimal time (in milliseconds) between two form submissions of a player.",
        "Protects against accidental double submissions on laggy connections. Set to 0 to disable."
    );

    public static final ConfigValue<Boolean> BEDROCK_ICONS_ENABLED = ConfigValue.create("Bedrock.Icons.Enabled",
        true,
        "Sets whether form buttons show icons. Icons are Bedrock texture paths (e.g. 'textures/items/lead')",
        "or full image URLs starting with 'http'. Leave an icon empty to hide it."
    );

    public static final ConfigValue<String> BEDROCK_ICON_TIER       = bedrockIcon("Tier", "textures/items/egg");
    public static final ConfigValue<String> BEDROCK_ICON_PET        = bedrockIcon("Pet", "textures/items/name_tag");
    public static final ConfigValue<String> BEDROCK_ICON_SUMMON     = bedrockIcon("Summon", "textures/items/lead");
    public static final ConfigValue<String> BEDROCK_ICON_DESPAWN    = bedrockIcon("Despawn", "textures/items/ender_pearl");
    public static final ConfigValue<String> BEDROCK_ICON_REVIVE     = bedrockIcon("Revive", "textures/items/totem");
    public static final ConfigValue<String> BEDROCK_ICON_RELEASE    = bedrockIcon("Release", "textures/items/feather");
    public static final ConfigValue<String> BEDROCK_ICON_MANAGE     = bedrockIcon("Manage", "textures/items/book_writable");
    public static final ConfigValue<String> BEDROCK_ICON_INVENTORY  = bedrockIcon("Inventory", "textures/blocks/chest_front");
    public static final ConfigValue<String> BEDROCK_ICON_UNEQUIP    = bedrockIcon("Unequip", "textures/items/iron_chestplate");
    public static final ConfigValue<String> BEDROCK_ICON_ASPECTS    = bedrockIcon("Aspects", "textures/items/experience_bottle");
    public static final ConfigValue<String> BEDROCK_ICON_REALLOCATE = bedrockIcon("Reallocate", "textures/items/clock_item");
    public static final ConfigValue<String> BEDROCK_ICON_RENAME     = bedrockIcon("Rename", "textures/items/name_tag");
    public static final ConfigValue<String> BEDROCK_ICON_SETTINGS   = bedrockIcon("Settings", "textures/items/redstone_dust");
    public static final ConfigValue<String> BEDROCK_ICON_EGG        = bedrockIcon("Egg", "textures/items/egg");
    public static final ConfigValue<String> BEDROCK_ICON_NAVIGATION = bedrockIcon("Navigation", "");

    private static ConfigValue<String> bedrockIcon(String name, String def) {
        return ConfigValue.create("Bedrock.Icons." + name, def);
    }

    @Deprecated
    public static boolean isGUIPlaceholdersEnabled() {
        return GENERAL_PLACEHOLDER_API_GUI.get();
    }

    public static boolean isLevelingEnabled() {
        return FEATURES_LEVELING_ENABLED.get();
    }

    public static boolean isCapturingEnabled() {
        return FEATURES_CAPTURE.get();
    }

    public static boolean isShopEnabled() {
        return FEATURES_SHOP.get();
    }

    public static boolean isWardrobeEnabled() {
        return FEATURES_WARDROBE.get();
    }
}
