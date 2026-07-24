package su.nightexpress.combatpets.api.pet;

import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NonNull;
import su.nightexpress.nightcore.util.placeholder.Placeholder;

import java.util.Set;

public interface Aspect extends Placeholder {

    @NonNull
    String getId();

    @NonNull
    String getName();

    @NonNull
    ItemStack getIcon();

    @NonNull
    Set<String> getAttributes();
}
