package su.nightexpress.combatpets.wardrobe.handler;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Rabbit;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import su.nightexpress.combatpets.config.Lang;

@NullMarked
public class RabbitVariantHandler extends EnumVariantHandler<Rabbit.Type> {

    public RabbitVariantHandler() {
        super(Rabbit.Type.class);
    }

    @Override
    public Rabbit.@Nullable Type read(LivingEntity entity) {
        return entity instanceof Rabbit rabbit ? rabbit.getRabbitType() : null;
    }

    @Override

    public String getLocalized(Rabbit.Type value) {
        return Lang.RABBIT_TYPE.getLocalized(value);
    }

    @Override
    public boolean apply(LivingEntity entity, Rabbit.@Nullable Type value) {
        if (value != null && entity instanceof Rabbit rabbit) {
            rabbit.setRabbitType(value);
            return true;
        }
        return false;
    }
}
