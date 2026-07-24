package su.nightexpress.combatpets.wardrobe.handler;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Sheep;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import su.nightexpress.combatpets.config.Lang;

@NullMarked
public class SheepShearVariantHandler extends EnumVariantHandler<SheepShearVariantHandler.Type> {

    public SheepShearVariantHandler() {
        super(Type.class);
    }

    @Override
    public SheepShearVariantHandler.@Nullable Type read(LivingEntity entity) {
        return entity instanceof Sheep sheep ? (sheep.isSheared() ? Type.SHEARED : Type.NORMAL) : null;
    }

    @Override

    public String getLocalized(Type value) {
        return Lang.SHEEP_SHEAR.getLocalized(value);
    }

    @Override
    public boolean apply(LivingEntity entity, @Nullable Type value) {
        if (!(entity instanceof Sheep sheep)) return false;

        sheep.setSheared(value == Type.SHEARED);
        return true;
    }

    public enum Type {
        NORMAL,
        SHEARED
    }
}
