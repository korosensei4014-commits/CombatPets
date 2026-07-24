package su.nightexpress.combatpets.pet.listener;

import io.github.arcaneplugins.levelledmobs.events.MobPreLevelEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.jspecify.annotations.NonNull;
import su.nightexpress.combatpets.PetAPI;
import su.nightexpress.combatpets.PetsPlugin;
import su.nightexpress.nightcore.manager.AbstractListener;

public class LevelledMobsListener extends AbstractListener<PetsPlugin> {

    public LevelledMobsListener(@NonNull PetsPlugin plugin) {
        super(plugin);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onLevel(MobPreLevelEvent event) {
        if (PetAPI.getPetManager().isPetEntity(event.getEntity())) {
            event.setCancelled(true);
        }
    }
}
