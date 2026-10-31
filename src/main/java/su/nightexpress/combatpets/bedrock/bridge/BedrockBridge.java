package su.nightexpress.combatpets.bedrock.bridge;

import org.geysermc.cumulus.form.Form;
import org.jspecify.annotations.NonNull;

import java.util.UUID;

/**
 * Abstraction over the Geyser / Floodgate APIs used to detect Bedrock players and send them forms.
 * <p>
 * Implementations are only loaded when the backing plugin is installed, so CombatPets has no hard
 * dependency on Geyser or Floodgate.
 */
public interface BedrockBridge {

    /**
     * @return Human readable name of the API in use (for logs).
     */
    @NonNull String getName();

    /**
     * @return true if the player with the given UUID is connected through Geyser (Bedrock Edition).
     */
    boolean isBedrockPlayer(@NonNull UUID playerId);

    /**
     * Sends a Cumulus form to the Bedrock player.
     * @return true if the form was sent.
     */
    boolean sendForm(@NonNull UUID playerId, @NonNull Form form);
}
