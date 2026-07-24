package su.nightexpress.combatpets.nms.paper26p12.util;

import org.jspecify.annotations.NullMarked;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import su.nightexpress.nightcore.util.Reflex;

@NullMarked
public class AttributeInjector {

    private static final String FIELD_SUPPLIER = "supplier";
    //private static final String FIELD_INSTANCES = "instances";

    public void injectFor(LivingEntity entity) {
        AttributeMap attributes = entity.getAttributes();
        AttributeSupplier.Builder builder = AttributeSupplier.builder();

        BuiltInRegistries.ATTRIBUTE.listElements().forEach(holder -> {
            double defaultValue;
            if (attributes.hasAttribute(holder)) {
                defaultValue = attributes.getBaseValue(holder);
            }
            else defaultValue = 0D; // TODO From Pet config

            builder.add(holder, defaultValue);
        });

        Reflex.setFieldValue(attributes, FIELD_SUPPLIER, builder.build());
    }

    /* private void registerAttribute(LivingEntity entity, Holder<Attribute> holder) throws IllegalStateException {
        AttributeSupplier supplier = (AttributeSupplier) Reflex.getFieldValue(attributes, FIELD_SUPPLIER);
        if (supplier == null) {
            throw new IllegalStateException("AttributeSupplier is null or '%s' field does not exist."
                .formatted(FIELD_SUPPLIER));
        }
    
        Map<Holder<Attribute>, AttributeInstance> instances = (Map<Holder<Attribute>, AttributeInstance>) Reflex
            .getFieldValue(supplier, "a");
        if (instances == null) return;
        
        Map<Holder<Attribute>, AttributeInstance> instances2 = new HashMap<>(instances); // Copy because it's immutable.
        instances2.put(holder, new AttributeInstance(holder, insta -> {
        }));
        
        Reflex.setFieldValue(supplier, "a", instances2); 
    }*/

    /* private void register(LivingEntity entity,
                          Holder<Attribute> holder,
                          AttributeSupplier.Builder builder,
                          double defValue) throws IllegalStateException {
        AttributeMap attributes = entity.getAttributes();
    
        Holder<Attribute> holder = BuiltInRegistries.ATTRIBUTE.get(key).orElse(null);
        if (holder == null) return;
        if (attributes.hasAttribute(holder)) return;
        this.logger.log(Level.WARNING,
            "Attribute %s has no base value and no default value were provided. Ignoring."
                .formatted(holder.unwrapKey().map(ResourceKey::toString).orElse(String.valueOf(holder)))
        );
        return;
    } */
}
