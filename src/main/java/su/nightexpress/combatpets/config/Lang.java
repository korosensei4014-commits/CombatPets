package su.nightexpress.combatpets.config;

import org.bukkit.DyeColor;
import org.bukkit.Sound;
import org.bukkit.entity.Fox;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Llama;
import org.bukkit.entity.Rabbit;
import su.nightexpress.combatpets.api.pet.type.CombatMode;
import su.nightexpress.combatpets.wardrobe.handler.AgeVariantHandler;
import su.nightexpress.combatpets.wardrobe.handler.CreeperPowerVariantHandler;
import su.nightexpress.combatpets.wardrobe.handler.SheepShearVariantHandler;
import su.nightexpress.nightcore.locale.LangContainer;
import su.nightexpress.nightcore.locale.LangEntry;
import su.nightexpress.nightcore.locale.entry.ButtonLocale;
import su.nightexpress.nightcore.locale.entry.EnumLocale;
import su.nightexpress.nightcore.locale.entry.MessageLocale;
import su.nightexpress.nightcore.locale.entry.TextLocale;

import static su.nightexpress.nightcore.util.text.night.wrapper.TagWrappers.*;
import static su.nightexpress.combatpets.Placeholders.*;

public class Lang implements LangContainer {

    public static final EnumLocale<AgeVariantHandler.Type>          AGE_TYPE   = LangEntry.builder("PetVariant.AgeType").enumeration(AgeVariantHandler.Type.class);
    public static final EnumLocale<CreeperPowerVariantHandler.Type> POWER_TYPE = LangEntry.builder("PetVariant.CreeperPower").enumeration(CreeperPowerVariantHandler.Type.class);
    public static final EnumLocale<DyeColor>                        DYE_COLOR   = LangEntry.builder("PetVariant.DyeColor").enumeration(DyeColor.class);
    public static final EnumLocale<Fox.Type>                        FOX_TYPE    = LangEntry.builder("PetVariant.FoxType").enumeration(Fox.Type.class);
    public static final EnumLocale<Horse.Color>                     HORSE_COLOR = LangEntry.builder("PetVariant.HorseColor").enumeration(Horse.Color.class);
    public static final EnumLocale<Horse.Style>                     HORSE_STYLE = LangEntry.builder("PetVariant.HorseStyle").enumeration(Horse.Style.class);
    public static final EnumLocale<Llama.Color>                     LLAMA_COLOR = LangEntry.builder("PetVariant.LlamaColor").enumeration(Llama.Color.class);
    public static final EnumLocale<SheepShearVariantHandler.Type>   SHEEP_SHEAR = LangEntry.builder("PetVariant.SheepShear").enumeration(SheepShearVariantHandler.Type.class);
    public static final EnumLocale<Rabbit.Type> RABBIT_TYPE = LangEntry.builder("PetVariant.RabbitType").enumeration(Rabbit.Type.class);

    public static final EnumLocale<CombatMode> COMBAT_MODE = LangEntry.builder("CombatMode").enumeration(CombatMode.class);

    public static final TextLocale COMMAND_ARGUMENT_NAME_TIER = LangEntry.builder("Command.Argument.Name.Tier").text("tier");
    public static final TextLocale COMMAND_ARGUMENT_NAME_PET  = LangEntry.builder("Command.Argument.Name.Pet").text("pet");

    public static final TextLocale COMMAND_COLLECTION_DESC            = LangEntry.builder("Command.Collection.Desc").text("List of pets you have claimed.");
    public static final TextLocale COMMAND_ADD_ALL_DESC               = LangEntry.builder("Command.AddAll.Desc").text("Add all pets to collection.");
    public static final TextLocale COMMAND_ADD_DESC                   = LangEntry.builder("Command.Add.Desc").text("Add pet to collection.");
    public static final TextLocale COMMAND_REMOVE_DESC                = LangEntry.builder("Command.Remove.Desc").text("Remove pet from collection.");
    public static final TextLocale COMMAND_REMOVE_ALL_DESC            = LangEntry.builder("Command.RemoveAll.Desc").text("Remove all pets from collection.");
    public static final TextLocale COMMAND_CAPTURE_ITEM_DESC          = LangEntry.builder("Command.CatchItem.Desc").text("Give capture item.");
    public static final TextLocale COMMAND_FOOD_DESC        = LangEntry.builder("Command.Food.Desc").text("Give food item.");
    public static final TextLocale COMMAND_EGG_DESC         = LangEntry.builder("Command.Egg.Desc").text("Give pet egg.");
    public static final TextLocale COMMAND_MYSTERY_EGG_DESC = LangEntry.builder("Command.MysteryEgg.Desc").text("Give mystery egg.");
    public static final TextLocale COMMAND_RENAME_DESC                = LangEntry.builder("Command.Rename.Desc").text("Rename player's pet.");
    public static final TextLocale COMMAND_REVIVE_DESC                = LangEntry.builder("Command.Revive.Desc").text("Revive player's pet.");
    public static final TextLocale COMMAND_CLEAR_INVENTORY_DESC       = LangEntry.builder("Command.ClearInventory.Desc").text("Clear player's pet inventory.");
    public static final TextLocale COMMAND_MENU_DESC                  = LangEntry.builder("Command.Menu.Desc").text("Open pet menu.");
    public static final TextLocale COMMAND_SHOP_DESC                  = LangEntry.builder("Command.Shop.Desc").text("Open shop.");
    public static final TextLocale COMMAND_ACCESSORY_DESC             = LangEntry.builder("Command.Accessory.Desc").text("Give accessory item.");
    public static final TextLocale COMMAND_RESET_PROGRESS_DESC        = LangEntry.builder("Command.ResetProgress.Desc").text("Reset leveling for player's pet.");
    public static final TextLocale COMMAND_XP_DESC                    = LangEntry.builder("Command.XP.Desc").text("XP commands.");
    public static final TextLocale COMMAND_XP_ADD_DESC                = LangEntry.builder("Command.XP.Add.Desc").text("Add XP to a pet.");
    public static final TextLocale COMMAND_XP_SET_DESC                = LangEntry.builder("Command.XP.Set.Desc").text("Set XP for a pet.");
    public static final TextLocale COMMAND_XP_REMOVE_DESC             = LangEntry.builder("Command.XP.Remove.Desc").text("Remove XP off a pet.");
    public static final TextLocale COMMAND_XP_REWARD_DESC             = LangEntry.builder("Command.XP.Reward.Desc").text("Add XP to a player's active pet.");
    public static final TextLocale COMMAND_XP_PENALTY_DESC            = LangEntry.builder("Command.XP.Penalty.Desc").text("Remove XP off a player's active pet.");
    public static final TextLocale COMMAND_APOINTS_DESC               = LangEntry.builder("Command.AspectPoints.Desc").text("Aspect Points commands.");
    public static final TextLocale COMMAND_ASPECT_POINTS_ADD_DESC     = LangEntry.builder("Command.AspectPoints.Add.Desc").text("Add Aspect Points to a pet.");
    public static final TextLocale COMMAND_ASPECT_POINTS_SET_DESC     = LangEntry.builder("Command.AspectPoints.Set.Desc").text("Set Aspect Points for a pet.");
    public static final TextLocale COMMAND_ASPECT_POINTS_REMOVE_DESC  = LangEntry.builder("Command.AspectPoints.Remove.Desc").text("Remove Aspect Points off a pet.");
    public static final TextLocale COMMAND_ASPECT_POINTS_REWARD_DESC  = LangEntry.builder("Command.AspectPoints.Reward.Desc").text("Add Aspect Points to a player's active pet.");
    public static final TextLocale COMMAND_ASPECT_POINTS_PENALTY_DESC = LangEntry.builder("Command.AspectPoints.Penalty.Desc").text("Remove Aspect Points off a player's active pet.");


    public static final MessageLocale COMMAND_XP_ADD_DONE = LangEntry.builder("Command.XP.Add.Done").chatMessage(
        GRAY.wrap("Added " + SOFT_YELLOW.wrap(GENERIC_AMOUNT) + " XP to " + SOFT_YELLOW.wrap(PLAYER_NAME) + "'s " + SOFT_YELLOW.wrap(PET_NAME) + " pet.")
    );

    public static final MessageLocale COMMAND_XP_SET_DONE = LangEntry.builder("Command.XP.Set.Done").chatMessage(
        GRAY.wrap("Set " + SOFT_YELLOW.wrap(GENERIC_AMOUNT) + " XP for " + SOFT_YELLOW.wrap(PLAYER_NAME) + "'s " + SOFT_YELLOW.wrap(PET_NAME) + " pet.")
    );

    public static final MessageLocale COMMAND_XP_REMOVE_DONE = LangEntry.builder("Command.XP.Remove.Done").chatMessage(
        GRAY.wrap("Removed " + SOFT_YELLOW.wrap(GENERIC_AMOUNT) + " XP off " + SOFT_YELLOW.wrap(PLAYER_NAME) + "'s " + SOFT_YELLOW.wrap(PET_NAME) + " pet.")
    );

    public static final MessageLocale COMMAND_XP_REWARD_DONE = LangEntry.builder("Command.XP.Reward.Done").chatMessage(
        GRAY.wrap("Added " + SOFT_YELLOW.wrap(GENERIC_AMOUNT) + " XP to " + SOFT_YELLOW.wrap(PLAYER_NAME) + "'s " + SOFT_YELLOW.wrap(PET_NAME) + " pet.")
    );

    public static final MessageLocale COMMAND_XP_PENALTY_DONE = LangEntry.builder("Command.XP.Penalty.Done").chatMessage(
        GRAY.wrap("Removed " + SOFT_YELLOW.wrap(GENERIC_AMOUNT) + " XP off " + SOFT_YELLOW.wrap(PLAYER_NAME) + "'s " + SOFT_YELLOW.wrap(PET_NAME) + " pet.")
    );


    public static final MessageLocale COMMAND_ASPECT_POINTS_ADD_DONE = LangEntry.builder("Command.AspectPoints.Add.Done").chatMessage(
        GRAY.wrap("Added " + SOFT_YELLOW.wrap(GENERIC_AMOUNT) + " Aspect Points to " + SOFT_YELLOW.wrap(PLAYER_NAME) + "'s " + SOFT_YELLOW.wrap(PET_NAME) + " pet.")
    );

    public static final MessageLocale COMMAND_ASPECT_POINTS_SET_DONE = LangEntry.builder("Command.AspectPoints.Set.Done").chatMessage(
        GRAY.wrap("Set " + SOFT_YELLOW.wrap(GENERIC_AMOUNT) + " Aspect Points for " + SOFT_YELLOW.wrap(PLAYER_NAME) + "'s " + SOFT_YELLOW.wrap(PET_NAME) + " pet.")
    );

    public static final MessageLocale COMMAND_ASPECT_POINTS_REMOVE_DONE = LangEntry.builder("Command.AspectPoints.Remove.Done").chatMessage(
        GRAY.wrap("Removed " + SOFT_YELLOW.wrap(GENERIC_AMOUNT) + " Aspect Points off " + SOFT_YELLOW.wrap(PLAYER_NAME) + "'s " + SOFT_YELLOW.wrap(PET_NAME) + " pet.")
    );

    public static final MessageLocale COMMAND_ASPECT_POINTS_REWARD_DONE = LangEntry.builder("Command.AspectPoints.Reward.Done").chatMessage(
        GRAY.wrap("Added " + SOFT_YELLOW.wrap(GENERIC_AMOUNT) + " Aspect Points to " + SOFT_YELLOW.wrap(PLAYER_NAME) + "'s " + SOFT_YELLOW.wrap(PET_NAME) + " pet.")
    );

    public static final MessageLocale COMMAND_ASPECT_POINTS_PENALTY_DONE = LangEntry.builder("Command.AspectPoints.Penalty.Done").chatMessage(
        GRAY.wrap("Removed " + SOFT_YELLOW.wrap(GENERIC_AMOUNT) + " Aspect Points off " + SOFT_YELLOW.wrap(PLAYER_NAME) + "'s " + SOFT_YELLOW.wrap(PET_NAME) + " pet.")
    );


    public static final MessageLocale COMMAND_ADD_DONE = LangEntry.builder("Command.Add.Done").chatMessage(
        GRAY.wrap("Added " + SOFT_YELLOW.wrap(TIER_NAME + " " + TEMPLATE_NAME) + " to " + SOFT_YELLOW.wrap(PLAYER_NAME) + "'s collection!")
    );

    public static final MessageLocale COMMAND_ADD_ALL_DONE = LangEntry.builder("Command.AddAll.Done").chatMessage(
        GRAY.wrap("Added all " + SOFT_YELLOW.wrap(TIER_NAME) + " pets to " + SOFT_YELLOW.wrap(PLAYER_NAME) + "'s collection!")
    );

    public static final MessageLocale COMMAND_REMOVE_DONE = LangEntry.builder("Command.Remove.Done").chatMessage(
        GRAY.wrap("Removed " + SOFT_YELLOW.wrap(TIER_NAME + " " + TEMPLATE_NAME) + " from " + SOFT_YELLOW.wrap(PLAYER_NAME) + "'s collection!")
    );

    public static final MessageLocale COMMAND_REMOVE_ALL_DONE = LangEntry.builder("Command.RemoveAll.Done").chatMessage(
        GRAY.wrap("Removed all " + SOFT_YELLOW.wrap(TIER_NAME) + " pets from " + SOFT_YELLOW.wrap(PLAYER_NAME) + "'s collection!")
    );


    public static final MessageLocale COMMAND_CATCH_ITEM_DONE = LangEntry.builder("Command.CatchItem.Done").chatMessage(
        GRAY.wrap("Given " + SOFT_YELLOW.wrap("x" + GENERIC_AMOUNT + " " + GENERIC_ITEM) + " to " + SOFT_YELLOW.wrap(PLAYER_NAME) + "!")
    );


    public static final MessageLocale COMMAND_FOOD_DONE = LangEntry.builder("Command.Food.Done").chatMessage(
        GRAY.wrap("Given " + SOFT_YELLOW.wrap("x" + GENERIC_AMOUNT + " " + GENERIC_ITEM) + " to " + SOFT_YELLOW.wrap(PLAYER_NAME) + "!")
    );


    public static final MessageLocale COMMAND_EGG_DONE = LangEntry.builder("Command.Egg.Done").chatMessage(
        GRAY.wrap("Given " + SOFT_YELLOW.wrap(TIER_NAME + " " + TEMPLATE_NAME) + " pet egg to " + SOFT_YELLOW.wrap(PLAYER_NAME) + ".")
    );

    public static final MessageLocale COMMAND_MYSTERY_EGG_DONE = LangEntry.builder("Command.MysteryEgg.Done").chatMessage(
        GRAY.wrap("Given " + SOFT_YELLOW.wrap(TEMPLATE_NAME) + " mystery egg to " + SOFT_YELLOW.wrap(PLAYER_NAME) + ".")
    );


    public static final MessageLocale COMMAND_RENAME_DONE = LangEntry.builder("Command.Rename.Done").chatMessage(
        GRAY.wrap("Renamed " + SOFT_YELLOW.wrap(PLAYER_NAME) + "'s pet to " + SOFT_YELLOW.wrap(PET_NAME) + ".")
    );



    public static final MessageLocale COMMAND_REVIVE_ERROR_ALIVE = LangEntry.builder("Command.Revive.Error.Alive").chatMessage(
        GRAY.wrap(SOFT_YELLOW.wrap(PLAYER_NAME) + "'s " + SOFT_YELLOW.wrap(PET_NAME) + " pet is not dead!")
    );

    public static final MessageLocale COMMAND_REVIVE_DONE = LangEntry.builder("Command.Revive.Done").chatMessage(
        GRAY.wrap("Revived " + SOFT_YELLOW.wrap(PLAYER_NAME) + "'s " + SOFT_YELLOW.wrap(PET_NAME) + " pet.")
    );



    public static final MessageLocale COMMAND_CLEAR_INVENTORY_DONE = LangEntry.builder("Command.ClearInventory.Done").chatMessage(
        GRAY.wrap("Cleared " + SOFT_YELLOW.wrap(PLAYER_NAME) + "'s " + SOFT_YELLOW.wrap(PET_NAME) + " pet inventory.")
    );


    public static final MessageLocale COMMAND_RESET_PROGRESS_DONE = LangEntry.builder("Command.ResetProgress.Done").chatMessage(
        GRAY.wrap("Reset leveling progress for " + SOFT_YELLOW.wrap(PLAYER_NAME) + "'s " + SOFT_YELLOW.wrap(PET_NAME) + " pet.")
    );


    public static final MessageLocale COMMAND_ACCESSORY_DONE = LangEntry.builder("Command.Accessory.Done").chatMessage(
        GRAY.wrap("Given " + SOFT_YELLOW.wrap(GENERIC_AMOUNT + " x " + GENERIC_NAME) + " to " + SOFT_YELLOW.wrap(PLAYER_DISPLAY_NAME) + ".")
    );



    public static final MessageLocale PET_DESPAWN_DEATH = LangEntry.builder("Pet.Despawn.Death").titleMessage(
        SOFT_RED.wrap(BOLD.wrap("Your Pet Died!")),
        GRAY.wrap("Revive it in your collection."),
        Sound.ENTITY_GENERIC_DEATH
    );

    public static final MessageLocale PET_DESPAWN_DEFAULT = LangEntry.builder("Pet.Despawn.Default").chatMessage(
        GRAY.wrap("Pet despawned.")
    );


    public static final MessageLocale PET_CLAIM_ERROR_ALREADY_HAVE = LangEntry.builder("Pet.Claim.Error.AlreadyHave").chatMessage(
        SOFT_RED.wrap("You already have this pet!"));

    public static final MessageLocale PET_CLAIM_ERROR_REACHED_LIMIT = LangEntry.builder("Pet.Claim.Error.ReachedLimit").chatMessage(
        SOFT_RED.wrap("You can not claim more than " + ORANGE.wrap(GENERIC_AMOUNT) + " " + TIER_NAME + " pets!"));

    public static final MessageLocale PET_CLAIM_SUCCESS = LangEntry.builder("Pet.Claim.Success").titleMessage(
        SOFT_GREEN.wrap(BOLD.wrap("Pet Claimed!")),
        GRAY.wrap("Check out your collection: " + SOFT_GREEN.wrap("/pet collection")),
        Sound.ENTITY_PLAYER_LEVELUP
    );

    public static final MessageLocale PET_MYSTERY_EGG_HATCH = LangEntry.builder("Pet.MysteryEgg.Hatch").chatMessage(
        Sound.ENTITY_EVOKER_PREPARE_ATTACK,
        GRAY.wrap("You hatched " + LIGHT_PURPLE.wrap("Mystery Egg") + " into " + LIGHT_PURPLE.wrap(TIER_NAME + " " + TEMPLATE_DEFAULT_NAME) + " !")
    );


    public static final MessageLocale CAPTURE_PROGRESS = LangEntry.builder("Pet.Catch.Process.Progress").titleMessage(
        SOFT_YELLOW.wrap(BOLD.wrap("Capturing...")),
        GRAY.wrap(GREEN.wrap(BOLD.wrap("Success ")) + "→ " + GREEN.wrap(GENERIC_SUCCESS + "% ") + "| " + RED.wrap(GENERIC_FAILURE + "% ") + "← " + RED.wrap(BOLD.wrap("Failure"))),
        0, 20
    );

    public static final MessageLocale CAPTURE_SUCCESS = LangEntry.builder("Pet.Catch.Process.Success").titleMessage(
        SOFT_GREEN.wrap(BOLD.wrap("Mob Captured!")),
        GRAY.wrap("You captured " + SOFT_GREEN.wrap(TEMPLATE_DEFAULT_NAME) + " " + GRAY.wrap("(" + WHITE.wrap(TIER_NAME) + ")")),
        0, 50, Sound.ENTITY_PLAYER_LEVELUP
    );

    public static final MessageLocale CAPTURE_FAIL_ESCAPED = LangEntry.builder("Pet.Catch.Process.Escaped").titleMessage(
        ORANGE.wrap(BOLD.wrap("Mob Escaped!")),
        GRAY.wrap("You have to look for another one..."),
        0, 50, Sound.ENTITY_VILLAGER_NO
    );

    public static final MessageLocale CAPTURE_FAIL_UNLUCK = LangEntry.builder("Pet.Catch.Process.Failure").titleMessage(
        SOFT_RED.wrap(BOLD.wrap("Capture Failed!")),
        GRAY.wrap("You were unlucky. Give it another try!"),
        0, 50, Sound.ENTITY_VILLAGER_NO
    );

    public static final MessageLocale CAPTURE_FAIL_DISTANCE = LangEntry.builder("Pet.Catch.Process.TooFarAway").titleMessage(
        SOFT_RED.wrap(BOLD.wrap("Capture Failed!")),
        GRAY.wrap("Stay closer next time!"),
        0, 50, Sound.ENTITY_VILLAGER_NO
    );

    public static final MessageLocale CAPTURE_ERROR_NOT_CAPTURABLE = LangEntry.builder("Pet.Catch.Error.NotCatchable").chatMessage(
        GRAY.wrap("This " + SOFT_RED.wrap(GENERIC_NAME) + " can not be captured.")
    );

    public static final MessageLocale CAPTURE_ERROR_PERMISSION = LangEntry.builder("Pet.Catch.Error.NotPermitted").chatMessage(
        GRAY.wrap("You don't have permissions to capture " + SOFT_RED.wrap(GENERIC_NAME) + ".")
    );

    public static final MessageLocale CAPTURE_ERROR_NOT_READY = LangEntry.builder("Pet.Catch.Error.Conditions").chatMessage(
        GRAY.wrap(SOFT_RED.wrap(GENERIC_NAME) + " is not ready for capture.")
    );



    public static final MessageLocale LEVELING_LEVEL_UP = LangEntry.builder("Pet.Leveling.LevelUp").titleMessage(
        SOFT_GREEN.wrap(BOLD.wrap("Pet Level Up!")),
        GRAY.wrap(SOFT_GREEN.wrap(PET_NAME) + " is now level " + SOFT_GREEN.wrap(PET_LEVEL) + "!"),
        Sound.ENTITY_PLAYER_LEVELUP
    );

    public static final MessageLocale LEVELING_LEVEL_DOWN = LangEntry.builder("Pet.Leveling.LevelDown").titleMessage(
        SOFT_RED.wrap(BOLD.wrap("Pet Level Down!")),
        GRAY.wrap(SOFT_RED.wrap(PET_NAME) + " is now level " + SOFT_RED.wrap(PET_LEVEL) + " :("),
        Sound.ENTITY_BLAZE_DEATH
    );

    public static final MessageLocale LEVELING_XP_LOSE_DEATH = LangEntry.builder("Pet.XP.Lose.ByDeath").chatMessage(
        SOFT_RED.wrap("[!] " + "Your pet lost " + GENERIC_AMOUNT + " XP for death.")
    );

    public static final MessageLocale LEVELING_XP_GAIN = LangEntry.builder("Pet.XP.Gain.ByMob").actionBarMessage(
        SOFT_YELLOW.wrap(PET_NAME + ": ") + ORANGE.wrap("+" + GENERIC_AMOUNT + " XP")
    );



    public static final MessageLocale PET_RENAME_PROMPT = LangEntry.builder("Pet.Rename.Prompt").titleMessage(
        SOFT_YELLOW.wrap(BOLD.wrap("Renaming")),
        GRAY.wrap("Enter new pet name."),
        Sound.BLOCK_LAVA_POP
    );

    public static final MessageLocale PET_RENAME_ERROR_NO_NAMETAG = LangEntry.builder("Pet.Rename.Error.NoNametag").titleMessage(
        SOFT_RED.wrap(BOLD.wrap("Unable to Rename!")),
        GRAY.wrap("You need a " + SOFT_RED.wrap("Name Tag") + " item."),
        Sound.ENTITY_VILLAGER_NO
    );

    public static final MessageLocale PET_RENAME_ERROR_TOO_LONG = LangEntry.builder("Pet.Rename.Error.TooLong").titleMessage(
        SOFT_RED.wrap(BOLD.wrap("Name Too Long!")),
        GRAY.wrap("Max name length is " + SOFT_RED.wrap(GENERIC_AMOUNT) + " characters!"),
        Sound.ENTITY_VILLAGER_NO
    );

    public static final MessageLocale PET_RENAME_ERROR_TOO_SHORT = LangEntry.builder("Pet.Rename.Error.TooShort").titleMessage(
        SOFT_RED.wrap(BOLD.wrap("Name Too Short!")),
        GRAY.wrap("Min name length is " + SOFT_RED.wrap(GENERIC_AMOUNT) + " characters!"),
        Sound.ENTITY_VILLAGER_NO
    );

    public static final MessageLocale PET_RENAME_ERROR_FORBIDDEN = LangEntry.builder("Pet.Rename.Error.Forbidden").titleMessage(
        SOFT_RED.wrap(BOLD.wrap("Unacceptable Name!")),
        GRAY.wrap("Name contains " + SOFT_RED.wrap("forbidden") + " characters!"),
        Sound.ENTITY_VILLAGER_NO
    );

    public static final TextLocale PET_RENAME_DIALOG_TITLE = LangEntry.builder("Pet.Rename.Dialog.Title").text(
        YELLOW.wrap(BOLD.wrap("RENAME PET")) + DARK_GRAY.wrap(" » ") + WHITE.wrap(PET_NAME)
    );

    public static final TextLocale PET_RENAME_DIALOG_BODY = LangEntry.builder("Pet.Rename.Dialog.Body").text(
        GRAY.wrap("Enter a " + GREEN.wrap("new name") + " for " + WHITE.wrap(PET_NAME) + "."),
        GRAY.wrap("Length: " + WHITE.wrap(GENERIC_AMOUNT + " - " + GENERIC_MAX) + " characters.")
    );

    public static final TextLocale PET_RENAME_DIALOG_BODY_NAMETAG = LangEntry.builder("Pet.Rename.Dialog.Body_NameTag").text(
        YELLOW.wrap("Renaming costs 1 Name Tag, taken when you press Save.")
    );

    public static final TextLocale PET_RENAME_DIALOG_INPUT = LangEntry.builder("Pet.Rename.Dialog.Input").text("New Name");

    public static final ButtonLocale PET_RENAME_DIALOG_SAVE = LangEntry.builder("Pet.Rename.Dialog.Save").button(
        GREEN.wrap("✔") + " Save"
    );

    public static final ButtonLocale PET_RENAME_DIALOG_BACK = LangEntry.builder("Pet.Rename.Dialog.Back").button(
        SOFT_YELLOW.wrap("←") + " Go Back"
    );

    public static final MessageLocale PET_RENAME_ERROR_EMPTY = LangEntry.builder("Pet.Rename.Error.Empty").chatMessage(
        SOFT_RED.wrap("Please enter a name.")
    );

    public static final MessageLocale PET_RENAME_SUCCESS = LangEntry.builder("Pet.Rename.Success").titleMessage(
        SOFT_GREEN.wrap(BOLD.wrap("Pet Renamed!")),
        GRAY.wrap("You renamed your pet to " + SOFT_GREEN.wrap(PET_NAME)),
        Sound.BLOCK_NOTE_BLOCK_BELL
    );

    public static final MessageLocale PET_REVIVE_ERROR_NOT_ENOUGH_FUNDS = LangEntry.builder("Pet.Revive.Error.NotEnoughFunds").titleMessage(
        SOFT_RED.wrap(BOLD.wrap("Not Enough Funds!")),
        GRAY.wrap("You need " + SOFT_RED.wrap(GENERIC_AMOUNT) + " to revive " + SOFT_RED.wrap(PET_NAME) + "."),
        Sound.BLOCK_ANVIL_PLACE
    );

    public static final MessageLocale PET_REVIVE_SUCCESS = LangEntry.builder("Pet.Revive.Success").titleMessage(
        SOFT_GREEN.wrap(BOLD.wrap("Pet Revived!")),
        GRAY.wrap("You revived " + SOFT_GREEN.wrap(PET_NAME) + " for " + SOFT_GREEN.wrap(GENERIC_AMOUNT) + "."),
        Sound.ITEM_TOTEM_USE
    );

    public static final MessageLocale PET_ERROR_NO_ACTIVE_PET = LangEntry.builder("Pet.Error.NoActivePet").titleMessage(
        SOFT_RED.wrap(BOLD.wrap("No Active Pet!")),
        GRAY.wrap("You have to summon a pet to do that."),
        Sound.ENTITY_VILLAGER_NO
    );

    public static final MessageLocale PET_ERROR_NOT_YOUR = LangEntry.builder("Pet.Error.NotYour").chatMessage(
        Sound.ENTITY_VILLAGER_NO,
        SOFT_RED.wrap("You're not owning this pet!")
    );

    public static final MessageLocale PET_SPAWN_ERROR_DEAD = LangEntry.builder("Pet.Spawn.Error.Dead").titleMessage(
        SOFT_RED.wrap(BOLD.wrap("Pet is Dead!")),
        GRAY.wrap("You have to revive it before summon!"),
        Sound.ENTITY_VILLAGER_NO
    );

    public static final MessageLocale PET_SPAWN_ERROR_ALREADY = LangEntry.builder("Pet.Spawn.Error.Already").titleMessage(
        SOFT_RED.wrap(BOLD.wrap("Already Summoned!")),
        GRAY.wrap("You can have only one summoned pet."),
        Sound.ENTITY_VILLAGER_NO
    );

    public static final MessageLocale PET_SPAWN_ERROR_BAD_PLACE = LangEntry.builder("Pet.Spawn.Error.BadPlace").titleMessage(
        SOFT_RED.wrap(BOLD.wrap("Bad Place!")),
        GRAY.wrap("There is not enough space for a pet."),
        Sound.ENTITY_VILLAGER_NO
    );

    public static final MessageLocale PET_ERROR_REGION_SUMMON = LangEntry.builder("Pet.Error.RegionSummon").chatMessage(
        SOFT_RED.wrap("Pets are not allowed in this area.")
    );

    public static final MessageLocale PET_ERROR_BAD_WORLD = LangEntry.builder("Pet.Error.BadWorld").chatMessage(
        SOFT_RED.wrap("Pets are disabled in this world.")
    );

    // ------------------------------------------------------------------------------------------
    // Pet duels
    // ------------------------------------------------------------------------------------------

    public static final TextLocale COMMAND_DUEL_DESC = LangEntry.builder("Command.Duel.Desc").text("Challenge a player's pet to a duel.");

    public static final TextLocale COMMAND_DUELS_DESC = LangEntry.builder("Command.Duels.Desc").text("Turn pet duel challenges on or off.");

    public static final TextLocale DUEL_STATUS_ON  = LangEntry.builder("Duel.Status.On").text("On");
    public static final TextLocale DUEL_STATUS_OFF = LangEntry.builder("Duel.Status.Off").text("Off");

    public static final MessageLocale DUEL_TOGGLE_ON = LangEntry.builder("Duel.Toggle.On").chatMessage(
        GRAY.wrap("Pet duels " + SOFT_GREEN.wrap("on") + ": other players can challenge your pet."));

    public static final MessageLocale DUEL_TOGGLE_OFF = LangEntry.builder("Duel.Toggle.Off").chatMessage(
        GRAY.wrap("Pet duels " + SOFT_RED.wrap("off") + ": all challenges are declined automatically."));

    public static final MessageLocale DUEL_ERROR_SELF_DISABLED = LangEntry.builder("Duel.Error.SelfDisabled").chatMessage(
        SOFT_RED.wrap("Your pet duels are turned off. Turn them on with " + SOFT_YELLOW.wrap("/pets duels on") + " first."));

    public static final MessageLocale DUEL_ERROR_TARGET_DISABLED = LangEntry.builder("Duel.Error.TargetDisabled").chatMessage(
        SOFT_RED.wrap(SOFT_YELLOW.wrap(PLAYER_NAME) + " isn't accepting pet duels."));

    public static final MessageLocale DUEL_ERROR_SELF = LangEntry.builder("Duel.Error.Self").chatMessage(
        SOFT_RED.wrap("You can't duel yourself."));

    public static final MessageLocale DUEL_ERROR_NO_PET = LangEntry.builder("Duel.Error.NoPet").chatMessage(
        SOFT_RED.wrap("You need a summoned pet to duel."));

    public static final MessageLocale DUEL_ERROR_COMPANION = LangEntry.builder("Duel.Error.Companion").chatMessage(
        SOFT_RED.wrap("Your pet is a companion and can't fight in duels."));

    public static final MessageLocale DUEL_ERROR_TARGET_COMPANION = LangEntry.builder("Duel.Error.TargetCompanion").chatMessage(
        SOFT_RED.wrap(SOFT_YELLOW.wrap(PLAYER_NAME) + "'s pet is a companion and can't fight in duels."));

    public static final MessageLocale DUEL_ERROR_TARGET_NO_PET = LangEntry.builder("Duel.Error.TargetNoPet").chatMessage(
        SOFT_RED.wrap(SOFT_YELLOW.wrap(PLAYER_NAME) + " doesn't have a summoned pet."));

    public static final MessageLocale DUEL_ERROR_IN_DUEL = LangEntry.builder("Duel.Error.InDuel").chatMessage(
        SOFT_RED.wrap("You are already in a pet duel."));

    public static final MessageLocale DUEL_ERROR_TARGET_IN_DUEL = LangEntry.builder("Duel.Error.TargetInDuel").chatMessage(
        SOFT_RED.wrap(SOFT_YELLOW.wrap(PLAYER_NAME) + " is already in a pet duel."));

    public static final MessageLocale DUEL_ERROR_TOO_FAR = LangEntry.builder("Duel.Error.TooFar").chatMessage(
        SOFT_RED.wrap(SOFT_YELLOW.wrap(PLAYER_NAME) + " is too far away. Get within " + SOFT_YELLOW.wrap(GENERIC_AMOUNT) + " blocks."));

    public static final MessageLocale DUEL_ERROR_ALREADY_SENT = LangEntry.builder("Duel.Error.AlreadySent").chatMessage(
        SOFT_RED.wrap("You already challenged " + SOFT_YELLOW.wrap(PLAYER_NAME) + ". Wait for an answer."));

    public static final TextLocale COMMAND_SURRENDER_DESC = LangEntry.builder("Command.Surrender.Desc").text("Surrender your current pet duel.");

    public static final MessageLocale DUEL_ERROR_NOT_IN_DUEL = LangEntry.builder("Duel.Error.NotInDuel").chatMessage(
        SOFT_RED.wrap("You are not in a pet duel."));

    public static final MessageLocale DUEL_SURRENDER_LOSE = LangEntry.builder("Duel.Surrender.Lose").titleMessage(
        SOFT_RED.wrap(BOLD.wrap("SURRENDERED")),
        GRAY.wrap("You gave up the duel against " + SOFT_YELLOW.wrap(PLAYER_NAME) + "."));

    public static final MessageLocale DUEL_SURRENDER_WIN = LangEntry.builder("Duel.Surrender.Win").titleMessage(
        SOFT_GREEN.wrap(BOLD.wrap("VICTORY!")),
        GRAY.wrap(SOFT_YELLOW.wrap(PLAYER_NAME) + " surrendered the duel."));

    public static final MessageLocale DUEL_ERROR_CANT_DESPAWN = LangEntry.builder("Duel.Error.CantDespawn").chatMessage(
        SOFT_RED.wrap("You can't return your pet during a duel."));

    public static final MessageLocale DUEL_ERROR_CANT_RIDE = LangEntry.builder("Duel.Error.CantRide").chatMessage(
        SOFT_RED.wrap("You can't ride your pet during a duel."));

    public static final MessageLocale DUEL_ERROR_CANT_FEED = LangEntry.builder("Duel.Error.CantFeed").chatMessage(
        SOFT_RED.wrap("You can't feed your pet during a duel."));

    public static final MessageLocale DUEL_REQUEST_SENT = LangEntry.builder("Duel.Request.Sent").chatMessage(
        GRAY.wrap("Duel challenge sent to " + SOFT_YELLOW.wrap(PLAYER_NAME) + ". It expires in " + SOFT_YELLOW.wrap(GENERIC_AMOUNT) + " seconds."));

    public static final MessageLocale DUEL_REQUEST_RECEIVED = LangEntry.builder("Duel.Request.Received").chatMessage(
        GRAY.wrap(SOFT_YELLOW.wrap(PLAYER_NAME) + " challenged your pet to a duel! " +
            "Type " + SOFT_YELLOW.wrap("/pets duel " + PLAYER_NAME) + " to accept."),
        SOFT_RED.wrap("The losing pet dies and must be revived."));

    public static final MessageLocale DUEL_REQUEST_EXPIRED = LangEntry.builder("Duel.Request.Expired").chatMessage(
        GRAY.wrap("The pet duel challenge between you and " + SOFT_YELLOW.wrap(PLAYER_NAME) + " expired."));

    public static final MessageLocale DUEL_REQUEST_DECLINED = LangEntry.builder("Duel.Request.Declined").chatMessage(
        GRAY.wrap(SOFT_YELLOW.wrap(PLAYER_NAME) + " declined the pet duel."));

    public static final MessageLocale DUEL_COUNTDOWN = LangEntry.builder("Duel.Countdown").titleMessage(
        SOFT_YELLOW.wrap(BOLD.wrap(GENERIC_TIME)),
        GRAY.wrap("Pet duel vs " + SOFT_YELLOW.wrap(PLAYER_NAME)), 0, 25);

    public static final MessageLocale DUEL_START = LangEntry.builder("Duel.Start").titleMessage(
        SOFT_RED.wrap(BOLD.wrap("FIGHT!")),
        GRAY.wrap(SOFT_YELLOW.wrap(PET_NAME) + " vs " + SOFT_YELLOW.wrap(PLAYER_NAME) + "'s pet"), 0, 30);

    public static final MessageLocale DUEL_WIN = LangEntry.builder("Duel.Win").titleMessage(
        SOFT_GREEN.wrap(BOLD.wrap("VICTORY!")),
        GRAY.wrap(SOFT_GREEN.wrap(PET_NAME) + " defeated " + SOFT_YELLOW.wrap(PLAYER_NAME) + "'s pet!"));

    public static final MessageLocale DUEL_LOSE = LangEntry.builder("Duel.Lose").titleMessage(
        SOFT_RED.wrap(BOLD.wrap("DEFEAT")),
        GRAY.wrap(SOFT_RED.wrap(PET_NAME) + " was defeated by " + SOFT_YELLOW.wrap(PLAYER_NAME) + "'s pet."));

    public static final MessageLocale DUEL_DRAW = LangEntry.builder("Duel.Draw").titleMessage(
        SOFT_YELLOW.wrap(BOLD.wrap("DRAW")),
        GRAY.wrap("Time is up! Both pets survived."));

    public static final MessageLocale DUEL_CANCELLED = LangEntry.builder("Duel.Cancelled").chatMessage(
        GRAY.wrap("The pet duel with " + SOFT_YELLOW.wrap(PLAYER_NAME) + " was cancelled: " + SOFT_RED.wrap(GENERIC_TYPE)));

    public static final TextLocale DUEL_CANCEL_LEFT     = LangEntry.builder("Duel.CancelReason.Left").text("a player left");
    public static final TextLocale DUEL_CANCEL_PET_GONE = LangEntry.builder("Duel.CancelReason.PetGone").text("a pet is no longer summoned");
    public static final TextLocale DUEL_CANCEL_DIED     = LangEntry.builder("Duel.CancelReason.OwnerDied").text("a player died");
    public static final TextLocale DUEL_CANCEL_TOO_FAR  = LangEntry.builder("Duel.CancelReason.TooFar").text("the pets got too far apart");

    public static final TextLocale DUEL_DIALOG_TITLE = LangEntry.builder("Duel.Dialog.Title").text(
        YELLOW.wrap(BOLD.wrap("PET DUEL")) + DARK_GRAY.wrap(" » ") + WHITE.wrap(PLAYER_NAME));

    public static final TextLocale DUEL_DIALOG_BODY = LangEntry.builder("Duel.Dialog.Body").text(
        SOFT_YELLOW.wrap(PLAYER_NAME) + GRAY.wrap(" challenges your pet to a duel!"),
        "",
        GRAY.wrap("Their pet: ") + WHITE.wrap(GENERIC_NAME),
        GRAY.wrap("Your pet: ") + WHITE.wrap(PET_NAME),
        "",
        SOFT_RED.wrap("The losing pet dies and must be revived."));

    public static final ButtonLocale DUEL_DIALOG_ACCEPT = LangEntry.builder("Duel.Dialog.Accept").button(
        GREEN.wrap("✔") + " Accept");

    public static final ButtonLocale DUEL_DIALOG_DECLINE = LangEntry.builder("Duel.Dialog.Decline").button(
        RED.wrap("✘") + " Decline");

    public static final TextLocale DUEL_PET_INFO = LangEntry.builder("Duel.PetInfo").text(
        PET_NAME + GRAY.wrap(" (Lv. " + PET_LEVEL + ", " + TIER_NAME + GRAY.wrap(", ") + GENERIC_AMOUNT + GRAY.wrap(" HP)")));

    public static final MessageLocale PET_STORE_SUCCESS = LangEntry.builder("Pet.Store.Success").actionBarMessage(
        SOFT_GREEN.wrap("Stored " + SOFT_YELLOW.wrap(PET_NAME) + " in an egg") + GRAY.wrap(" (" + GENERIC_AMOUNT + ")"));

    public static final MessageLocale PET_STORE_ERROR_NOT_ENOUGH_FUNDS = LangEntry.builder("Pet.Store.Error.NotEnoughFunds").chatMessage(
        SOFT_RED.wrap("You need " + SOFT_YELLOW.wrap(GENERIC_AMOUNT) + " to store " + SOFT_YELLOW.wrap(PET_NAME) + " in an egg."));

    public static final MessageLocale PET_STORE_ERROR_DEAD = LangEntry.builder("Pet.Store.Error.Dead").chatMessage(
        SOFT_RED.wrap("Revive " + SOFT_YELLOW.wrap(PET_NAME) + " before storing it in an egg."));

    public static final MessageLocale PET_STORED_EGG_HATCH = LangEntry.builder("Pet.StoredEgg.Hatch").actionBarMessage(
        SOFT_GREEN.wrap(SOFT_YELLOW.wrap(PET_NAME) + " hatched into your collection!"));

    public static final MessageLocale PET_STORED_EGG_ERROR_USED = LangEntry.builder("Pet.StoredEgg.Error.Used").chatMessage(
        SOFT_RED.wrap("This egg has already hatched or isn't valid."));

    public static final MessageLocale PET_STORED_EGG_ERROR_INVALID = LangEntry.builder("Pet.StoredEgg.Error.Invalid").chatMessage(
        SOFT_RED.wrap("This pet's type or tier doesn't exist on this server anymore."));

    public static final MessageLocale PET_RELEASE_SUCCESS = LangEntry.builder("Pet.Release.Success").chatMessage(
        GRAY.wrap("You released " + SOFT_YELLOW.wrap(PET_NAME) + "!")
    );

    public static final MessageLocale PET_RELEASE_ERROR_BAD_WORLD = LangEntry.builder("Pet.Release.Error.BadWorld").chatMessage(
        SOFT_RED.wrap("You can not release pets in this world!")
    );

    public static final MessageLocale PET_RELEASE_ERROR_ACTIVE = LangEntry.builder("Pet.Release.Error.Active").chatMessage(
        SOFT_RED.wrap("Return " + SOFT_YELLOW.wrap(PET_NAME) + " to your collection before releasing it!")
    );

    public static final MessageLocale PET_RELEASE_ERROR_PROTECTED_AREA = LangEntry.builder("Pet.Release.Error.ProtectedArea").chatMessage(
        SOFT_RED.wrap("You can't release pets here!")
    );

    public static final MessageLocale PET_USER_ERROR_ALREADY_COLLECTED = LangEntry.builder("Pet.Error.AlreadyCollected").chatMessage(
        SOFT_RED.wrap(PLAYER_NAME + " already have " + TIER_NAME + " " + TEMPLATE_NAME + "!")
    );

    public static final MessageLocale PET_USER_ERROR_NOT_COLLECTED = LangEntry.builder("Pet.Error.NotCollected").chatMessage(
        SOFT_RED.wrap("Player does not have such pet in his collection!")
    );

    public static final MessageLocale PET_USER_ERROR_NOT_SUMMONED = LangEntry.builder("Pet.Error.NotSummoned").chatMessage(
        SOFT_RED.wrap("Player does not have a pet summoned!")
    );

    public static final MessageLocale SHOP_PURCHASE_ERROR_NOT_ENOUGH_FUNDS = LangEntry.builder("Shop.Purchase.Error.NotEnoughFunds").titleMessage(
        SOFT_RED.wrap(BOLD.wrap("Not Enough Funds!")),
        GRAY.wrap("You need " + SOFT_RED.wrap(GENERIC_PRICE) + " to purchase " + SOFT_RED.wrap(TEMPLATE_DEFAULT_NAME) + "."),
        Sound.ENTITY_VILLAGER_NO
    );

    public static final MessageLocale SHOP_PURCHASE_SUCCESS = LangEntry.builder("Shop.Purchase.Success").titleMessage(
        SOFT_GREEN.wrap(BOLD.wrap("Successful Purchase!")),
        GRAY.wrap("You bought " + SOFT_GREEN.wrap(TEMPLATE_DEFAULT_NAME) + " egg for " + SOFT_GREEN.wrap(GENERIC_PRICE) + "."),
        Sound.BLOCK_NOTE_BLOCK_BELL
    );


    public static final MessageLocale ACCESSORY_APPLY_ERROR_NOT_YOURS = LangEntry.builder("Customizer.Interact.Pet.Error.NotYour").chatMessage(
        SOFT_RED.wrap("You can't customize other player pets!")
    );

    public static final MessageLocale ACCESSORY_APPLY_ERROR_WRONG_TYPE = LangEntry.builder("Customizer.Interact.Pet.Error.WrongType").chatMessage(
        SOFT_RED.wrap("This accessory is not applicable to this pet!")
    );

    public static final MessageLocale ACCESSORY_APPLY_ERROR_ALREADY_HAS = LangEntry.builder("Customizer.Interact.Pet.Error.AlreadyHas").chatMessage(
        SOFT_RED.wrap("This accessory is already applied to this pet!")
    );


    public static final MessageLocale ERROR_COMMAND_INVALID_TIER_ARGUMENT = LangEntry.builder("Command.Syntax.InvalidTier").chatMessage(
        GRAY.wrap(SOFT_RED.wrap(GENERIC_INPUT) + " is not a valid tier!")
    );

    public static final MessageLocale ERROR_COMMAND_INVALID_PET_ARGUMENT = LangEntry.builder("Command.Syntax.InvalidPet").chatMessage(
        GRAY.wrap(SOFT_RED.wrap(GENERIC_INPUT) + " is not a valid pet!")
    );

    public static final MessageLocale ERROR_COMMAND_INVALID_FOOD_CATEGORY_ARGUMENT = LangEntry.builder("Command.Syntax.InvalidFoodCategory").chatMessage(
        GRAY.wrap(SOFT_RED.wrap(GENERIC_INPUT) + " is not a valid food category!")
    );

    public static final MessageLocale ERROR_COMMAND_INVALID_FOOD_ITEM_ARGUMENT = LangEntry.builder("Command.Syntax.InvalidFoodItem").chatMessage(
        GRAY.wrap(SOFT_RED.wrap(GENERIC_INPUT) + " is not a valid food item!")
    );

    public static final MessageLocale ERROR_COMMAND_INVALID_VARIANT_ARGUMENT = LangEntry.builder("Command.Syntax.InvalidCustomization").chatMessage(
        GRAY.wrap(SOFT_RED.wrap(GENERIC_INPUT) + " is not a valid customization type!")
    );
}
