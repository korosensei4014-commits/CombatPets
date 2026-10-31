package su.nightexpress.combatpets.nms.paper26p3;

import java.util.Set;
import java.util.function.Function;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.craftbukkit.CraftEquipmentSlot;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftLivingEntity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EntityEquipment;
import org.jspecify.annotations.NullMarked;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import su.nightexpress.combatpets.api.pet.ActivePet;
import su.nightexpress.combatpets.api.pet.PetEntity;
import su.nightexpress.combatpets.api.pet.PetEntityBridge;
import su.nightexpress.combatpets.api.pet.PetNMS;
import su.nightexpress.combatpets.api.pet.PetSharedData;
import su.nightexpress.combatpets.api.pet.Template;
import su.nightexpress.combatpets.nms.paper26p3.goals.follow.PetLookAtOwnerGoal;
import su.nightexpress.combatpets.nms.paper26p3.util.AttributeInjector;

@NullMarked
public class NMS26p3 implements PetNMS {

    //private final Logger            logger;
    private final AttributeInjector attributeInjector;

    public NMS26p3() {
        //this.logger = logger;
        this.attributeInjector = new AttributeInjector();
        EntityInjector.register();
    }

    @Override
    public Set<EntityType> getSupportedEntities() {
        return EntityInjector.getTypes();
    }

    @Override
    public ActivePet spawnPet(Template template,
                              PetSharedData sharedData,
                              Location location,
                              Function<LivingEntity, ActivePet> holderFunction) {
        World bukkitWorld = location.getWorld();
        if (bukkitWorld == null) {
            throw new IllegalStateException("World can not be null!");
        }

        EntityType type = template.getEntityType();
        ServerLevel level = ((CraftWorld) bukkitWorld).getHandle();
        Mob mob = EntityInjector.spawn(type, level, sharedData);
        PetEntity petEntity = (PetEntity) mob;

        mob.getGoalSelector().getAvailableGoals().clear();
        mob.targetSelector.getAvailableGoals().clear();

        // Register all attributes. They will be updated with actual values on spawn.
        this.attributeInjector.injectFor(mob);

        // TODO FOLLOW_RANGE defines NEARBY_PLAYERS sensor and therefore owner reach distance

        LivingEntity bukkitEntity = (LivingEntity) mob.getBukkitEntity();
        ActivePet holder = holderFunction.apply(bukkitEntity);
        PetEntityBridge.addHolder(petEntity, holder);

        petEntity.setGoals();
        mob.getGoalSelector().addGoal(8, new PetLookAtOwnerGoal(mob));
        mob.getGoalSelector().addGoal(9, new RandomLookAroundGoal(mob));

        level.addFreshEntity(mob, null);
        mob.snapTo(location.getX(), location.getY(), location.getZ());

        return holder;
    }

    @Override
    public void setSaddle(LivingEntity entity) {
        EntityEquipment equipment = entity.getEquipment();
        if (equipment == null) return;

        equipment.setItem(
            CraftEquipmentSlot.getSlot(EquipmentSlot.SADDLE), new org.bukkit.inventory.ItemStack(Material.SADDLE));
    }

    /* @SuppressWarnings("unchecked")
    private void registerAttribute(net.minecraft.world.entity.LivingEntity handle, Holder<Attribute> holder) {
        AttributeInstance instance = handle.getAttribute(holder);
        if (instance != null) return;
    
        AttributeMap attributes = handle.getAttributes();
        AttributeSupplier supplier = (AttributeSupplier) Reflex.getFieldValue(attributes, "supplier");
        if (supplier == null) {
            this.logger.log(Level.SEVERE, "Could not find 'supplier' register %s attribute for %s: ");
            return;
        }
    
        Map<Holder<Attribute>, AttributeInstance> instances = (Map<Holder<Attribute>, AttributeInstance>) Reflex
            .getFieldValue(supplier, "a");
        if (instances == null) return;
    
        Map<Holder<Attribute>, AttributeInstance> instances2 = new HashMap<>(instances); // Copy because it's immutable.
        instances2.put(holder, new AttributeInstance(holder, insta -> {
        }));
    
        Reflex.setFieldValue(supplier, "a", instances2);
    } */

    @Override
    public void sneak(LivingEntity entity, boolean value) {
        CraftLivingEntity craftEntity = (CraftLivingEntity) entity;
        craftEntity.getHandle().setShiftKeyDown(value);
    }

    @Override
    public boolean hasNavigationPath(LivingEntity entity) {
        CraftLivingEntity craftEntity = (CraftLivingEntity) entity;

        net.minecraft.world.entity.LivingEntity nmsEntity = craftEntity.getHandle();
        if (!(nmsEntity instanceof Mob mob)) return false;

        return mob.getNavigation().isInProgress();
    }

    @Override
    public void setAttackTarget(LivingEntity entity, @org.jspecify.annotations.Nullable LivingEntity target) {
        if (!(((CraftLivingEntity) entity).getHandle() instanceof Mob mob)) return;

        net.minecraft.world.entity.LivingEntity handle = target == null ? null : ((CraftLivingEntity) target).getHandle();

        // Goal-based pets use the mob target, brain-based pets use the ATTACK_TARGET memory.
        mob.setTarget(handle, org.bukkit.event.entity.EntityTargetEvent.TargetReason.CUSTOM);
        if (handle == null) {
            mob.getBrain().eraseMemory(net.minecraft.world.entity.ai.memory.MemoryModuleType.ATTACK_TARGET);
        }
        else {
            mob.getBrain().setMemory(net.minecraft.world.entity.ai.memory.MemoryModuleType.ATTACK_TARGET, handle);
        }
    }
}
