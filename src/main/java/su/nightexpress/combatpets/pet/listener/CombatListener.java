package su.nightexpress.combatpets.pet.listener;

import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.LargeFireball;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.SmallFireball;
import org.bukkit.entity.Vex;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.jspecify.annotations.Nullable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.tag.DamageTypeTags;
import org.jspecify.annotations.NonNull;
import su.nightexpress.combatpets.PetsPlugin;
import su.nightexpress.combatpets.api.pet.ActivePet;
import su.nightexpress.combatpets.config.Config;
import su.nightexpress.combatpets.pet.PetManager;
import su.nightexpress.combatpets.util.PetUtils;
import su.nightexpress.nightcore.manager.AbstractListener;

public class CombatListener extends AbstractListener<PetsPlugin> {

    private final PetManager petManager;

    public CombatListener(@NonNull PetsPlugin plugin, @NonNull PetManager petManager) {
        super(plugin);
        this.petManager = petManager;
    }

    //@SuppressWarnings({"deprecation"})
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPetDamageInUpdate(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof LivingEntity victim)) return;

        ActivePet activePet = this.petManager.getPetByMob(victim);
        if (activePet == null) return;

        //        DamageSource source = event.getDamageSource();
        //        if (!DamageTypeTags.BYPASSES_ARMOR.isTagged(source.getDamageType())) {
        //            float amount = (float) (event.getDamage() + event.getDamage(EntityDamageEvent.DamageModifier.BLOCKING) + event.getDamage(EntityDamageEvent.DamageModifier.HARD_HAT));
        //            PetUtils.hurtArmor(victim, source, amount);
        //        }

        activePet.onIncomingDamage();

        this.plugin.runTask(task -> {
            activePet.updateName();
            activePet.updateHealthBar();
        });
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPetDamageInFriendly(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof LivingEntity victim)) return;
        LivingEntity damager = getAttacker(event);
        if (damager == null) return;

        // Vexes summoned by an Evoker pet follow the Evoker's damage rules, both when they hit something and when
        // they are hit (so the owner and the owner's pets can't hurt them, and they can't hurt each other).
        LivingEntity damagerSummoner = getPetSummoner(damager);
        LivingEntity victimSummoner = getPetSummoner(victim);
        if (damagerSummoner != null && damagerSummoner == victimSummoner) {
            event.setCancelled(true);
            return;
        }
        if (damagerSummoner != null) damager = damagerSummoner;
        if (victimSummoner != null) {
            if (damager == victimSummoner) {
                event.setCancelled(true);
                return;
            }
            victim = victimSummoner;
        }

        if (!this.petManager.canDamage(damager, victim)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPetDamageOutAdjust(EntityDamageByEntityEvent event) {
        if (!Config.PET_ATTACK_DAMAGE_FOR_PROJECTILES.get()) return;

        // Non-hand attacks only.
        LivingEntity damager = getAttacker(event);
        if (damager == null || damager == event.getDamager()) return;

        ActivePet activePet = this.petManager.getPetByMob(damager);
        if (activePet == null) return;

        double origin = event.getDamage();
        double mod = Config.PET_ORIGINAL_DAMAGE_FROM_PROJECTILES.get();
        double add = origin * mod;
        double atkDamage = activePet.getAttackDamage() + add;

        // Pets without a melee attack (Ghast, Wither, ...) have Attack Damage 1, which made their projectiles
        // much weaker than vanilla. Optionally never go below the projectile's normal damage.
        if (Config.PET_PROJECTILE_DAMAGE_NOT_BELOW_ORIGINAL.get()) {
            atkDamage = Math.max(origin, atkDamage);
        }

        event.setDamage(atkDamage);
    }

    /**
     * Vanilla treats a fireball's hit as fire damage, so fire-immune mobs (Wither, Warden, Ghast, Blaze, ...) take
     * none of it. For pet fireballs hitting a target the pet may attack, deal that hit as normal projectile damage.
     */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPetFireballHit(ProjectileHitEvent event) {
        if (!Config.PET_FIREBALLS_IGNORE_FIRE_IMMUNITY.get()) return;

        Projectile projectile = event.getEntity();
        if (!(projectile instanceof LargeFireball) && !(projectile instanceof SmallFireball)) return;
        if (!(projectile.getShooter() instanceof LivingEntity shooter)) return;
        if (!(event.getHitEntity() instanceof LivingEntity victim) || victim == shooter || victim.isDead()) return;
        if (this.petManager.getPetByMob(shooter) == null) return;
        if (!this.plugin.getPetNMS().isFireImmune(victim)) return;
        if (!this.petManager.canDamage(shooter, victim)) return;

        double damage = projectile instanceof LargeFireball ? 6D : 5D; // vanilla direct-hit damage
        victim.damage(damage, DamageSource.builder(DamageType.MOB_PROJECTILE)
            .withCausingEntity(shooter)
            .withDirectEntity(projectile)
            .build());
    }

    /**
     * @return The Evoker pet that summoned this Vex, or null if it's not a Vex summoned by a pet.
     */
    @Nullable
    private LivingEntity getPetSummoner(@NonNull LivingEntity entity) {
        if (entity instanceof Vex vex && vex.getSummoner() instanceof LivingEntity summoner && this.petManager.getPetByMob(summoner) != null) {
            return summoner;
        }
        return null;
    }

    /**
     * The attacker behind a hit. Some pets drop the causing entity from damage they take,
     * so fall back to the projectile's shooter or the direct damager.
     */
    @Nullable
    private static LivingEntity getAttacker(@NonNull EntityDamageByEntityEvent event) {
        Entity attacker = event.getDamageSource().getCausingEntity();
        if (attacker == null) {
            Entity direct = event.getDamager();
            attacker = direct instanceof Projectile projectile && projectile.getShooter() instanceof Entity shooter ? shooter : direct;
        }
        return attacker instanceof LivingEntity living ? living : null;
    }
}
