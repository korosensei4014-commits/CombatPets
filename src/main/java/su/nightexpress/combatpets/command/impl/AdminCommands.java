package su.nightexpress.combatpets.command.impl;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NonNull;
import su.nightexpress.combatpets.PetsPlugin;
import su.nightexpress.combatpets.Placeholders;
import su.nightexpress.combatpets.api.pet.ActivePet;
import su.nightexpress.combatpets.api.pet.Template;
import su.nightexpress.combatpets.api.pet.Tier;
import su.nightexpress.combatpets.command.CommandArguments;
import su.nightexpress.combatpets.config.Lang;
import su.nightexpress.combatpets.config.Perms;
import su.nightexpress.combatpets.data.impl.PetData;
import su.nightexpress.combatpets.data.impl.PetUser;
import su.nightexpress.combatpets.pet.duel.DuelManager;
import su.nightexpress.nightcore.commands.Arguments;
import su.nightexpress.nightcore.commands.Commands;
import su.nightexpress.nightcore.commands.builder.HubNodeBuilder;
import su.nightexpress.nightcore.commands.context.CommandContext;
import su.nightexpress.nightcore.commands.context.ParsedArguments;
import su.nightexpress.nightcore.util.Players;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

/**
 * Support tools under /pets admin: list, info, transfer, heal, despawn, duel stop/resetstats, egg store,
 * cooldown reset. (level set is in LevelingCommands, egg store is registered in the egg hub of BaseCommands.)
 */
public class AdminCommands {

    private static final String FROM = "from";
    private static final String TO   = "to";

    public static void load(@NonNull PetsPlugin plugin, @NonNull HubNodeBuilder adminNode) {
        adminNode.branch(Commands.literal("list")
            .description(Lang.COMMAND_LIST_DESC)
            .permission(Perms.COMMAND_LIST)
            .withArguments(Arguments.playerName(CommandArguments.PLAYER))
            .executes((context, arguments) -> listPets(plugin, context, arguments))
        );

        adminNode.branch(Commands.literal("info")
            .description(Lang.COMMAND_INFO_DESC)
            .permission(Perms.COMMAND_INFO)
            .withArguments(
                Arguments.playerName(CommandArguments.PLAYER),
                CommandArguments.tierArgument(plugin),
                CommandArguments.templateArgument(plugin)
            )
            .executes((context, arguments) -> petInfo(plugin, context, arguments))
        );

        adminNode.branch(Commands.literal("transfer")
            .description(Lang.COMMAND_TRANSFER_DESC)
            .permission(Perms.COMMAND_TRANSFER)
            .withArguments(
                Arguments.playerName(FROM),
                Arguments.playerName(TO),
                CommandArguments.tierArgument(plugin),
                CommandArguments.templateArgument(plugin)
            )
            .executes((context, arguments) -> transferPet(plugin, context, arguments))
        );

        adminNode.branch(Commands.literal("heal")
            .description(Lang.COMMAND_HEAL_DESC)
            .permission(Perms.COMMAND_HEAL)
            .withArguments(Arguments.player(CommandArguments.PLAYER))
            .executes((context, arguments) -> healPet(plugin, context, arguments))
        );

        adminNode.branch(Commands.literal("despawn")
            .description(Lang.COMMAND_DESPAWN_DESC)
            .permission(Perms.COMMAND_DESPAWN)
            .withArguments(Arguments.player(CommandArguments.PLAYER))
            .executes((context, arguments) -> despawnPet(plugin, context, arguments))
        );

        adminNode.branch(Commands.hub("cooldown")
            .description(Lang.COMMAND_COOLDOWN_DESC)
            .branch(Commands.literal("reset")
                .description(Lang.COMMAND_COOLDOWN_RESET_DESC)
                .permission(Perms.COMMAND_COOLDOWN_RESET)
                .withArguments(Arguments.player(CommandArguments.PLAYER))
                .executes((context, arguments) -> {
                    Player target = arguments.getPlayer(CommandArguments.PLAYER);
                    boolean had = plugin.getPetManager().resetStoreCooldown(target.getUniqueId());
                    context.send(had ? Lang.COMMAND_COOLDOWN_RESET_DONE : Lang.COMMAND_COOLDOWN_RESET_NONE,
                        replacer -> replacer.replace(Placeholders.PLAYER_NAME, target.getName()));
                    return true;
                })
            )
        );

        DuelManager duelManager = plugin.getDuelManager();
        if (duelManager != null) {
            adminNode.branch(Commands.hub("duel")
                .description(Lang.COMMAND_ADMIN_DUEL_DESC)
                .branch(Commands.literal("stop")
                    .description(Lang.COMMAND_DUEL_STOP_DESC)
                    .permission(Perms.COMMAND_DUEL_STOP)
                    .withArguments(Arguments.player(CommandArguments.PLAYER))
                    .executes((context, arguments) -> {
                        Player target = arguments.getPlayer(CommandArguments.PLAYER);
                        boolean stopped = duelManager.adminStop(target);
                        context.send(stopped ? Lang.COMMAND_DUEL_STOP_DONE : Lang.COMMAND_DUEL_STOP_NONE,
                            replacer -> replacer.replace(Placeholders.PLAYER_NAME, target.getName()));
                        return true;
                    })
                )
                .branch(Commands.literal("resetstats")
                    .description(Lang.COMMAND_DUEL_RESETSTATS_DESC)
                    .permission(Perms.COMMAND_DUEL_RESETSTATS)
                    .withArguments(
                        Arguments.playerName(CommandArguments.PLAYER),
                        CommandArguments.tierArgument(plugin),
                        CommandArguments.templateArgument(plugin)
                    )
                    .executes((context, arguments) -> resetDuelStats(plugin, context, arguments))
                )
            );
        }
    }

    public static boolean listPets(@NonNull PetsPlugin plugin, @NonNull CommandContext context,
                                   @NonNull ParsedArguments arguments) {
        plugin.getUserManager().manageUserSynchronized(arguments.getString(CommandArguments.PLAYER), user -> {
            if (user == null) {
                context.errorBadPlayer();
                return;
            }
            if (!user.isLoaded()) return;

            List<PetData> pets = new ArrayList<>(user.getPets().values());
            if (pets.isEmpty()) {
                context.send(Lang.COMMAND_LIST_EMPTY, replacer -> replacer.replace(Placeholders.PLAYER_NAME, user.getName()));
                return;
            }

            pets.sort(Comparator.comparing((PetData data) -> data.getTier().getId()).thenComparing(data -> data.getTemplate().getId()));
            context.send(Lang.COMMAND_LIST_HEADER, replacer -> replacer
                .replace(Placeholders.PLAYER_NAME, user.getName())
                .replace(Placeholders.GENERIC_AMOUNT, String.valueOf(pets.size())));

            Player owner = user.getPlayer();
            for (PetData data : pets) {
                boolean summoned = owner != null && plugin.getPetManager().isSummoned(owner, data);
                String status = summoned ? Lang.COMMAND_LIST_STATUS_SUMMONED.text()
                    : data.isDead() ? Lang.COMMAND_LIST_STATUS_DEAD.text()
                    : Lang.COMMAND_LIST_STATUS_RESTING.text();

                context.send(Lang.COMMAND_LIST_ENTRY, replacer -> replacer
                    .replace(data.replacePlaceholders())
                    .replace(data.getTier().replacePlaceholders())
                    .replace(data.getTemplate().replacePlaceholders())
                    .replace(Placeholders.GENERIC_TYPE, status));
            }
        });
        return true;
    }

    public static boolean petInfo(@NonNull PetsPlugin plugin, @NonNull CommandContext context,
                                  @NonNull ParsedArguments arguments) {
        plugin.getUserManager().manageUserSynchronized(arguments.getString(CommandArguments.PLAYER), user -> {
            PetData data = findPet(context, arguments, user);
            if (data == null) return;

            Player owner = user.getPlayer();
            boolean summoned = owner != null && plugin.getPetManager().isSummoned(owner, data);
            int spent = data.getAspects().values().stream().mapToInt(Integer::intValue).sum();
            long items = data.getInventory().stream().filter(item -> item != null && !item.getType().isAir()).count();
            long equipment = data.getEquipment().values().stream().filter(item -> item != null && !item.getType().isAir()).count();
            String status = summoned ? Lang.COMMAND_LIST_STATUS_SUMMONED.text()
                : data.isDead() ? Lang.COMMAND_LIST_STATUS_DEAD.text()
                : Lang.COMMAND_LIST_STATUS_RESTING.text();

            context.send(Lang.COMMAND_INFO, replacer -> replacer
                .replace(data.replacePlaceholders())
                .replace(data.getTier().replacePlaceholders())
                .replace(data.getTemplate().replacePlaceholders())
                .replace(Placeholders.PLAYER_NAME, user.getName())
                .replace(Placeholders.GENERIC_TYPE, status)
                .replace(Placeholders.GENERIC_TOTAL, String.valueOf(spent))
                .replace(Placeholders.GENERIC_ITEM, String.valueOf(items))
                .replace(Placeholders.GENERIC_AMOUNT, String.valueOf(equipment)));
        });
        return true;
    }

    public static boolean transferPet(@NonNull PetsPlugin plugin, @NonNull CommandContext context,
                                      @NonNull ParsedArguments arguments) {
        String fromName = arguments.getString(FROM);
        String toName = arguments.getString(TO);
        Tier tier = arguments.get(CommandArguments.TIER, Tier.class);
        Template template = arguments.get(CommandArguments.PET, Template.class);

        if (fromName.equalsIgnoreCase(toName)) {
            context.send(Lang.COMMAND_TRANSFER_ERROR_SAME);
            return false;
        }

        plugin.getUserManager().manageUserSynchronized(fromName, from -> {
            if (from == null) {
                context.errorBadPlayer();
                return;
            }
            if (!from.isLoaded()) return;

            PetData data = from.getPet(template, tier);
            if (data == null) {
                Lang.PET_USER_ERROR_NOT_COLLECTED.message().send(context.getSender());
                return;
            }

            Player fromPlayer = from.getPlayer();
            DuelManager duelManager = plugin.getDuelManager();
            if (fromPlayer != null && duelManager != null && duelManager.isInDuel(fromPlayer)
                && plugin.getPetManager().isSummoned(fromPlayer, data)) {
                context.send(Lang.COMMAND_TRANSFER_ERROR_DUEL, replacer -> replacer.replace(Placeholders.PLAYER_NAME, from.getName()));
                return;
            }

            plugin.getUserManager().manageUserSynchronized(toName, to -> {
                if (to == null) {
                    context.errorBadPlayer();
                    return;
                }
                if (!to.isLoaded()) return;

                if (to.hasPet(template, tier)) {
                    context.send(Lang.COMMAND_TRANSFER_ERROR_HAS, replacer -> replacer
                        .replace(Placeholders.PLAYER_NAME, to.getName())
                        .replace(tier.replacePlaceholders())
                        .replace(template.replacePlaceholders()));
                    return;
                }
                // The pet could have been removed while the second player was loading.
                if (from.getPet(template, tier) != data) return;

                // Despawns the pet if it's summoned (saving its inventory into the data) and removes it.
                plugin.getPetManager().removeFromCollection(from, tier, template);
                to.addPet(data);
                plugin.getUserManager().save(to);

                context.send(Lang.COMMAND_TRANSFER_DONE, replacer -> replacer
                    .replace(data.replacePlaceholders())
                    .replace(tier.replacePlaceholders())
                    .replace(template.replacePlaceholders())
                    .replace(Placeholders.GENERIC_ITEM, from.getName())
                    .replace(Placeholders.PLAYER_NAME, to.getName()));
            });
        });
        return true;
    }

    public static boolean healPet(@NonNull PetsPlugin plugin, @NonNull CommandContext context,
                                  @NonNull ParsedArguments arguments) {
        Player target = arguments.getPlayer(CommandArguments.PLAYER);
        ActivePet pet = plugin.getPetManager().getPlayerPet(target);
        if (pet == null) {
            context.send(Lang.COMMAND_ERROR_NO_ACTIVE_PET, replacer -> replacer.replace(Placeholders.PLAYER_NAME, target.getName()));
            return false;
        }

        LivingEntity entity = pet.getEntity();
        AttributeInstance maxHealth = entity.getAttribute(Attribute.MAX_HEALTH);
        if (maxHealth != null) entity.setHealth(maxHealth.getValue());
        pet.doFeed(pet.getMaxSaturation());
        pet.updateHealthBar();

        context.send(Lang.COMMAND_HEAL_DONE, replacer -> replacer
            .replace(Placeholders.PLAYER_NAME, target.getName())
            .replace(Placeholders.PET_NAME, pet.getName()));
        return true;
    }

    public static boolean despawnPet(@NonNull PetsPlugin plugin, @NonNull CommandContext context,
                                     @NonNull ParsedArguments arguments) {
        Player target = arguments.getPlayer(CommandArguments.PLAYER);
        ActivePet pet = plugin.getPetManager().getPlayerPet(target);
        if (pet == null) {
            context.send(Lang.COMMAND_ERROR_NO_ACTIVE_PET, replacer -> replacer.replace(Placeholders.PLAYER_NAME, target.getName()));
            return false;
        }

        String petName = pet.getName();
        DuelManager duelManager = plugin.getDuelManager();
        if (duelManager != null) duelManager.adminStop(target); // No winner if it was dueling.
        plugin.getPetManager().despawnPet(pet);

        context.send(Lang.COMMAND_DESPAWN_DONE, replacer -> replacer
            .replace(Placeholders.PLAYER_NAME, target.getName())
            .replace(Placeholders.PET_NAME, petName));
        return true;
    }

    public static boolean resetDuelStats(@NonNull PetsPlugin plugin, @NonNull CommandContext context,
                                         @NonNull ParsedArguments arguments) {
        plugin.getUserManager().manageUserSynchronized(arguments.getString(CommandArguments.PLAYER), user -> {
            PetData data = findPet(context, arguments, user);
            if (data == null) return;

            data.setDuelWins(0);
            data.setDuelLosses(0);
            plugin.getUserManager().save(user);

            context.send(Lang.COMMAND_DUEL_RESETSTATS_DONE, replacer -> replacer
                .replace(Placeholders.PLAYER_NAME, user.getName())
                .replace(Placeholders.PET_NAME, data.getName()));
        });
        return true;
    }

    /**
     * /pets admin egg store: turns a player's pet into a stored egg for the admin, free and without cooldown.
     * The pet's items go to the admin too, so nothing is lost.
     */
    public static boolean storeEgg(@NonNull PetsPlugin plugin, @NonNull CommandContext context,
                                   @NonNull ParsedArguments arguments) {
        Player admin = context.getPlayerOrThrow();

        plugin.getUserManager().manageUserSynchronized(arguments.getString(CommandArguments.PLAYER), user -> {
            PetData data = findPet(context, arguments, user);
            if (data == null) return;

            Player owner = user.getPlayer();
            if (owner != null) {
                ActivePet active = plugin.getPetManager().getPlayerPet(owner);
                if (active != null && active.getTier() == data.getTier() && active.getTemplate() == data.getTemplate()) {
                    DuelManager duelManager = plugin.getDuelManager();
                    if (duelManager != null) duelManager.adminStop(owner);
                    plugin.getPetManager().removePet(active); // Saves the live inventory into the data.
                }
            }

            List<ItemStack> items = new ArrayList<>(data.getInventory());
            items.addAll(data.getEquipment().values());
            items.removeIf(item -> item == null || item.getType().isAir());
            data.setInventory(new ArrayList<>());
            data.setEquipment(new HashMap<>());

            ItemStack egg = plugin.getItemManager().createStoredEgg(data);
            plugin.getPetManager().removeFromCollection(user, data.getTier(), data.getTemplate());

            Players.addItem(admin, egg);
            items.forEach(item -> Players.addItem(admin, item));

            context.send(Lang.COMMAND_EGG_STORE_DONE, replacer -> replacer
                .replace(Placeholders.PLAYER_NAME, user.getName())
                .replace(Placeholders.PET_NAME, data.getName())
                .replace(Placeholders.GENERIC_AMOUNT, String.valueOf(items.size())));
        });
        return true;
    }

    private static PetData findPet(@NonNull CommandContext context, @NonNull ParsedArguments arguments, PetUser user) {
        if (user == null) {
            context.errorBadPlayer();
            return null;
        }
        if (!user.isLoaded()) return null;

        Tier tier = arguments.get(CommandArguments.TIER, Tier.class);
        Template template = arguments.get(CommandArguments.PET, Template.class);
        PetData data = user.getPet(template, tier);
        if (data == null) {
            Lang.PET_USER_ERROR_NOT_COLLECTED.message().send(context.getSender());
        }
        return data;
    }
}
