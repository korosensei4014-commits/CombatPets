package su.nightexpress.combatpets.wardrobe.util;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.*;

public class VariantRegistry {

    private static final Map<String, EntityVariant<?>> VARIANT_MAP = new HashMap<>();

    public static void register(@NonNull EntityVariant<?> variant) {
        VARIANT_MAP.put(variant.getName(), variant);
    }

    public static void clear() {
        VARIANT_MAP.clear();
    }

    @Nullable
    public static EntityVariant<?> getVariant(@NonNull String name) {
        return VARIANT_MAP.get(name.toLowerCase());
    }

    @NonNull
    public static Set<EntityVariant<?>> getVariants() {
        return new HashSet<>(VARIANT_MAP.values());
    }

    @NonNull
    public static List<String> getVariantNames() {
        return new ArrayList<>(VARIANT_MAP.keySet());
    }
}
