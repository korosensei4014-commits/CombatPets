package su.nightexpress.combatpets.data;

import org.jspecify.annotations.NonNull;
import su.nightexpress.combatpets.PetsPlugin;
import su.nightexpress.combatpets.data.impl.PetUser;
import su.nightexpress.nightcore.db.AbstractUserManager;

import java.util.UUID;

public class UserManager extends AbstractUserManager<PetsPlugin, PetUser> {

    public UserManager(@NonNull PetsPlugin plugin, @NonNull DataHandler dataHandler) {
        super(plugin, dataHandler);
    }

    @Override
    @NonNull
    public PetUser create(@NonNull UUID uuid, @NonNull String name) {
        return PetUser.create(uuid, name);
    }
}
