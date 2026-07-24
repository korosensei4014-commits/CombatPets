package su.nightexpress.combatpets.api.pet;

import org.bukkit.Keyed;
import org.bukkit.attribute.Attribute;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public interface Stat extends Keyed {

    @NonNull
    String getId();

    @NonNull
    String getDisplayName();

    void setDisplayName(@NonNull String displayName);

    @Nullable
    Attribute getVanillaMirror();
}
