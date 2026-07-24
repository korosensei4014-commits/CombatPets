package su.nightexpress.combatpets.wardrobe.handler;

import org.bukkit.entity.Fox;
import org.bukkit.entity.LivingEntity;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import su.nightexpress.combatpets.config.Lang;

@NullMarked
public class FoxTypeVariantHandler extends EnumVariantHandler<Fox.Type> {

    public FoxTypeVariantHandler() {
        super(Fox.Type.class);
    }

    @Override
    public Fox.@Nullable Type read(LivingEntity entity) {
        if (!(entity instanceof Fox fox)) return null;

        return fox.getFoxType();
    }

    @Override

    public String getLocalized(Fox.Type value) {
        return Lang.FOX_TYPE.getLocalized(value);
    }

    @Override

    public boolean apply(LivingEntity entity, Fox.@Nullable Type value) {
        if (value == null) return false;
        if (!(entity instanceof Fox fox)) return false;

        fox.setFoxType(value);
        return true;
    }
}
