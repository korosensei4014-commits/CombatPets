package su.nightexpress.combatpets.pet.duel;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jspecify.annotations.NonNull;
import su.nightexpress.combatpets.PetsPlugin;
import su.nightexpress.combatpets.api.pet.event.generic.PetDeathEvent;
import su.nightexpress.nightcore.manager.AbstractListener;

public class DuelListener extends AbstractListener<PetsPlugin> {

    private final DuelManager manager;

    public DuelListener(@NonNull PetsPlugin plugin, @NonNull DuelManager manager) {
        super(plugin);
        this.manager = manager;
    }

    // No healing during duels (pet regeneration, Wither self-heal, ...), so fights actually end.
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onDuelPetHeal(EntityRegainHealthEvent event) {
        if (event.getEntity() instanceof LivingEntity entity && this.manager.isHealingBlocked(entity)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPetDeath(PetDeathEvent event) {
        this.manager.handlePetDeath(event.getPet());
    }

    // LOWEST: before the plugin despawns the dead player's pet.
    @EventHandler(priority = EventPriority.LOWEST)
    public void onOwnerDeath(PlayerDeathEvent event) {
        this.manager.handleOwnerDeath(event.getEntity());
    }

    // LOWEST: before the plugin removes the quitting player's pet.
    @EventHandler(priority = EventPriority.LOWEST)
    public void onQuit(PlayerQuitEvent event) {
        this.manager.handleQuit(event.getPlayer());
    }
}
