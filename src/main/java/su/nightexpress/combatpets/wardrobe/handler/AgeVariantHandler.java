package su.nightexpress.combatpets.wardrobe.handler;

import org.bukkit.entity.Ageable;
import org.bukkit.entity.LivingEntity;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import su.nightexpress.combatpets.config.Lang;

public class AgeVariantHandler extends EnumVariantHandler<AgeVariantHandler.Type> {

    public AgeVariantHandler() {
        super(Type.class);
    }

    @Override
    public AgeVariantHandler.@Nullable Type read(@NonNull LivingEntity entity) {
        if (entity instanceof Ageable ageable) return ageable.isAdult() ? Type.ADULT : Type.BABY;

        return null;
    }

    @Override
    @NonNull
    public String getLocalized(@NonNull Type value) {
        return Lang.AGE_TYPE.getLocalized(value);
    }

    @Override
    public boolean apply(@NonNull LivingEntity entity, @Nullable Type value) {
        if (!(entity instanceof Ageable ageable)) return false;

        if (value == Type.ADULT) ageable.setAdult();
        else ageable.setBaby();

        return true;
    }

    public enum Type {
        ADULT,
        BABY
    }
}
