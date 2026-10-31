package su.nightexpress.combatpets.pet.duel;

import org.jspecify.annotations.NonNull;

import java.util.UUID;

/**
 * A pending duel challenge from one player to another.
 */
public record DuelRequest(@NonNull UUID challengerId, @NonNull UUID targetId, long expireDate) {

    public boolean isExpired() {
        return System.currentTimeMillis() >= this.expireDate;
    }
}
