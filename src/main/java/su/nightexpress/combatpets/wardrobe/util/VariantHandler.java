package su.nightexpress.combatpets.wardrobe.util;

import org.bukkit.entity.LivingEntity;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.List;

public abstract class VariantHandler<T> {

    public VariantHandler() {

    }

    @NonNull
    public abstract List<T> values();

    @Nullable
    public abstract T parse(@NonNull String raw);

    @Nullable
    public abstract T read(@NonNull LivingEntity entity);

    @NonNull
    public abstract String getLocalized(@NonNull T value);

    @NonNull
    public abstract String getRaw(@NonNull T value);

    public abstract boolean apply(@NonNull LivingEntity entity, @Nullable T value);

    @NonNull
    public List<String> rawValues() {
        return this.values().stream().map(this::getRaw).toList();
    }

    @NonNull
    public String getLocalized(@NonNull String raw) {
        T value = this.parse(raw);
        return value == null ? raw : this.getLocalized(value);
    }

    @Nullable
    public String getRaw(@NonNull LivingEntity entity) {
        T value = this.read(entity);
        return value == null ? null : this.getRaw(value);
    }

    public boolean apply(@NonNull LivingEntity entity, @NonNull String raw) {
        T value = this.parse(raw);
        return value != null && this.apply(entity, value);
    }

    public boolean alreadyHas(@NonNull LivingEntity entity, @NonNull String raw) {
        return this.alreadyHas(entity, this.parse(raw));
    }

    public boolean alreadyHas(@NonNull LivingEntity entity, @Nullable T value) {
        return this.read(entity) == value;
    }
}
