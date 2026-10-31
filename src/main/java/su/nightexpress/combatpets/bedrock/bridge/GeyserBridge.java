package su.nightexpress.combatpets.bedrock.bridge;

import org.geysermc.cumulus.form.Form;
import org.jspecify.annotations.NonNull;

import java.lang.reflect.Method;
import java.util.UUID;

/**
 * Bedrock bridge backed by the Geyser base API ({@code org.geysermc.api.Geyser}).
 * <p>
 * Used when Geyser-Spigot runs on this server without Floodgate. The API is accessed reflectively so
 * CombatPets does not need to compile against a specific Geyser version. Only stable base-API methods
 * ({@code isBedrockPlayer(UUID)} and {@code sendForm(UUID, Form)}) are used.
 */
public final class GeyserBridge implements BedrockBridge {

    private final Method apiGetter;
    private final Method isBedrockPlayer;
    private final Method sendForm;

    private volatile Object api;

    public GeyserBridge() throws ReflectiveOperationException {
        Class<?> geyserClass = Class.forName("org.geysermc.api.Geyser");
        Class<?> apiClass = Class.forName("org.geysermc.api.GeyserApiBase");

        this.apiGetter = geyserClass.getMethod("api");
        this.isBedrockPlayer = apiClass.getMethod("isBedrockPlayer", UUID.class);
        this.sendForm = apiClass.getMethod("sendForm", UUID.class, Form.class);
    }

    /**
     * Geyser may register its API after CombatPets is enabled, so it is resolved lazily.
     */
    private Object api() throws ReflectiveOperationException {
        Object current = this.api;
        if (current == null) {
            try {
                current = this.apiGetter.invoke(null);
            }
            catch (java.lang.reflect.InvocationTargetException exception) {
                // Geyser.api() throws while the API is not registered yet.
                return null;
            }
            this.api = current;
        }
        return current;
    }

    @Override
    @NonNull
    public String getName() {
        return "Geyser";
    }

    @Override
    public boolean isBedrockPlayer(@NonNull UUID playerId) {
        try {
            Object current = this.api();
            return current != null && (boolean) this.isBedrockPlayer.invoke(current, playerId);
        }
        catch (ReflectiveOperationException exception) {
            return false;
        }
    }

    @Override
    public boolean sendForm(@NonNull UUID playerId, @NonNull Form form) {
        try {
            Object current = this.api();
            return current != null && (boolean) this.sendForm.invoke(current, playerId, form);
        }
        catch (ReflectiveOperationException exception) {
            return false;
        }
    }
}
