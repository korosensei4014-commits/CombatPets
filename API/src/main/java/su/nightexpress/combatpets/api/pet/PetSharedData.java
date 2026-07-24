package su.nightexpress.combatpets.api.pet;

import java.util.Optional;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import su.nightexpress.combatpets.api.pet.type.CombatMode;
import su.nightexpress.combatpets.api.pet.type.ExhaustReason;

@NullMarked
public interface PetSharedData {

    UUID getOwnerId();

    @Nullable
    Player getOwner();

    Optional<Player> owner();

    CombatMode getCombatMode();

    void setCombatMode(CombatMode mode);

    void doExhaust(ExhaustReason reason);

    void doExhaust(double amount);
}
