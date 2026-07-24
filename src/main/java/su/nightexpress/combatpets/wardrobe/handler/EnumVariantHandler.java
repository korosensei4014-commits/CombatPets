package su.nightexpress.combatpets.wardrobe.handler;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import su.nightexpress.combatpets.wardrobe.util.VariantHandler;
import su.nightexpress.nightcore.util.Lists;
import su.nightexpress.nightcore.util.StringUtil;

import java.util.List;

public abstract class EnumVariantHandler<E extends Enum<E>> extends VariantHandler<E> {

    protected final Class<E> clazz;

    public EnumVariantHandler(@NonNull Class<E> clazz) {
        this.clazz = clazz;
    }

    @Override
    @NonNull
    public List<E> values() {
        return Lists.newList(clazz.getEnumConstants());
    }

    @Override
    @Nullable
    public E parse(@NonNull String raw) {
        return StringUtil.getEnum(raw, this.clazz).orElse(null);
    }

    @Override
    @NonNull
    public String getRaw(@NonNull E value) {
        return value.name().toLowerCase();
    }
}
