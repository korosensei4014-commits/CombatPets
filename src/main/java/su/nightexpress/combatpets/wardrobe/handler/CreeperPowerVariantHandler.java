package su.nightexpress.combatpets.wardrobe.handler;

import org.bukkit.entity.Creeper;
import org.bukkit.entity.LivingEntity;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import su.nightexpress.combatpets.config.Lang;

@NullMarked
public class CreeperPowerVariantHandler extends EnumVariantHandler<CreeperPowerVariantHandler.Type> {

    public CreeperPowerVariantHandler() {
        super(Type.class);
    }

    @Override
    public CreeperPowerVariantHandler.@Nullable Type read(LivingEntity entity) {
        return entity instanceof Creeper creeper ? (creeper.isPowered() ? Type.POWERED : Type.DEFAULT) : null;
    }

    @Override

    public String getLocalized(CreeperPowerVariantHandler.Type value) {
        return Lang.POWER_TYPE.getLocalized(value);
    }

    @Override
    public boolean apply(LivingEntity entity, CreeperPowerVariantHandler.@Nullable Type value) {
        if (!(entity instanceof Creeper creeper)) return false;

        creeper.setPowered(value == Type.POWERED);
        return true;
    }

    public enum Type {
        DEFAULT,
        POWERED
    }
}
