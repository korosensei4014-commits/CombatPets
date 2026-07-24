package su.nightexpress.combatpets.api.pet.event;

import org.bukkit.event.HandlerList;
import org.jspecify.annotations.NonNull;
import su.nightexpress.combatpets.api.pet.ActivePet;

public class PetLevelUpEvent extends PetEvent {

    public static final HandlerList HANDLER_LIST = new HandlerList();

    public PetLevelUpEvent(@NonNull ActivePet pet) {
        super(pet);
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
