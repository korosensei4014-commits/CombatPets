package su.nightexpress.combatpets.pet.duel;

import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;
import su.nightexpress.combatpets.PetsPlugin;
import su.nightexpress.combatpets.command.CommandArguments;
import su.nightexpress.combatpets.config.Lang;
import su.nightexpress.combatpets.config.Perms;
import su.nightexpress.nightcore.commands.Arguments;
import su.nightexpress.nightcore.commands.Commands;
import su.nightexpress.nightcore.commands.builder.HubNodeBuilder;

public class DuelCommands {

    private static final String DUEL = "duel";

    /**
     * {@code /pets duel <player>}: challenges the player, or accepts their pending challenge.
     */
    public static void load(@NonNull PetsPlugin plugin, @NonNull DuelManager manager, @NonNull HubNodeBuilder rootNode) {
        rootNode.branch(Commands.literal(DUEL)
            .description(Lang.COMMAND_DUEL_DESC)
            .permission(Perms.COMMAND_DUEL)
            .playerOnly()
            .withArguments(Arguments.player(CommandArguments.PLAYER))
            .executes((context, arguments) -> {
                Player player = context.getPlayerOrThrow();
                Player target = arguments.getPlayer(CommandArguments.PLAYER);
                manager.handleCommand(player, target);
                return true;
            })
        );
    }
}
