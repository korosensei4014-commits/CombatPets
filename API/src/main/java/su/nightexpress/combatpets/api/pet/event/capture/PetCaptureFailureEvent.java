package su.nightexpress.combatpets.api.pet.event.capture;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jspecify.annotations.NonNull;
import su.nightexpress.combatpets.api.pet.Template;
import su.nightexpress.combatpets.api.pet.Tier;

public class PetCaptureFailureEvent extends PetCaptureEvent {

    public static final HandlerList HANDLER_LIST = new HandlerList();

    public PetCaptureFailureEvent(@NonNull Player player, @NonNull LivingEntity entity, @NonNull Template template,
                                  @NonNull Tier tier) {
        super(player, entity, template, tier);
    }

    @NonNull
    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}
