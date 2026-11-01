package su.nightexpress.combatpets.pet.duel;

import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

import java.util.List;
import su.nightexpress.combatpets.PetsPlugin;
import su.nightexpress.combatpets.command.CommandArguments;
import su.nightexpress.combatpets.config.Lang;
import su.nightexpress.combatpets.config.Perms;
import su.nightexpress.nightcore.commands.Arguments;
import su.nightexpress.nightcore.commands.Commands;
import su.nightexpress.nightcore.commands.builder.HubNodeBuilder;

public class DuelCommands {

    private static final String DUEL  = "duel";
    private static final String DUELS     = "duels";
    private static final String SURRENDER = "surrender";
    private static final String STATE = "state";

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

        // /pets surrender - give up the current duel.
        rootNode.branch(Commands.literal(SURRENDER)
            .description(Lang.COMMAND_SURRENDER_DESC)
            .permission(Perms.COMMAND_DUEL)
            .playerOnly()
            .executes((context, arguments) -> {
                manager.surrender(context.getPlayerOrThrow());
                return true;
            })
        );

        // /pets duels [on|off] - turn duel challenges on/off (toggles without an argument).
        rootNode.branch(Commands.literal(DUELS)
            .description(Lang.COMMAND_DUELS_DESC)
            .permission(Perms.COMMAND_DUEL)
            .playerOnly()
            .withArguments(Arguments.string(STATE).optional().suggestions((reader, context) -> List.of("on", "off")))
            .executes((context, arguments) -> {
                Player player = context.getPlayerOrThrow();
                String state = arguments.contains(STATE) ? arguments.getString(STATE) : null;
                manager.handleToggleCommand(player, state);
                return true;
            })
        );
    }
}
