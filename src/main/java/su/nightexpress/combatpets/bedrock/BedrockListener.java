package su.nightexpress.combatpets.bedrock;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jspecify.annotations.NonNull;
import su.nightexpress.combatpets.PetsPlugin;
import su.nightexpress.nightcore.manager.AbstractListener;

public class BedrockListener extends AbstractListener<PetsPlugin> {

    private final BedrockManager manager;

    public BedrockListener(@NonNull PetsPlugin plugin, @NonNull BedrockManager manager) {
        super(plugin);
        this.manager = manager;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent event) {
        this.manager.clearSession(event.getPlayer());
    }
}
