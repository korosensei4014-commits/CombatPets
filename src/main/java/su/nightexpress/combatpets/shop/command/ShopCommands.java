package su.nightexpress.combatpets.shop.command;

import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;
import su.nightexpress.combatpets.PetsPlugin;
import su.nightexpress.combatpets.config.Lang;
import su.nightexpress.combatpets.config.Perms;
import su.nightexpress.combatpets.shop.ShopManager;
import su.nightexpress.nightcore.commands.Commands;
import su.nightexpress.nightcore.commands.builder.HubNodeBuilder;
import su.nightexpress.nightcore.commands.context.CommandContext;
import su.nightexpress.nightcore.commands.context.ParsedArguments;

public class ShopCommands {

    private static final String SHOP = "shop";

    public static void load(@NonNull PetsPlugin plugin, @NonNull ShopManager manager,
                            @NonNull HubNodeBuilder rootNode) {
        rootNode.branch(Commands.literal(SHOP)
            .description(Lang.COMMAND_SHOP_DESC)
            .permission(Perms.COMMAND_SHOP)
            .executes((context, arguments) -> openShop(plugin, manager, context, arguments))
        );
    }

    public static boolean openShop(@NonNull PetsPlugin plugin, @NonNull ShopManager manager,
                                   @NonNull CommandContext context, @NonNull ParsedArguments arguments) {
        Player player = context.getPlayerOrThrow();
        manager.openTiersMenu(player);
        return true;
    }
}
