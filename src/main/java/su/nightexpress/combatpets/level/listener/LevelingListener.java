package su.nightexpress.combatpets.level.listener;

import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.jspecify.annotations.NonNull;
import su.nightexpress.combatpets.PetsPlugin;
import su.nightexpress.combatpets.api.pet.ActivePet;
import su.nightexpress.combatpets.level.LevelingConfig;
import su.nightexpress.combatpets.level.LevelingManager;
import su.nightexpress.nightcore.manager.AbstractListener;
import su.nightexpress.nightcore.util.EntityUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LevelingListener extends AbstractListener<PetsPlugin> {

    private final LevelingManager              manager;
    private final Map<UUID, Map<UUID, Double>> damageMap;

    public LevelingListener(@NonNull PetsPlugin plugin, @NonNull LevelingManager manager) {
        super(plugin);
        this.manager = manager;
        this.damageMap = new HashMap<>();
    }

    @NonNull
    private Map<UUID, Double> getDealtDamageMap(@NonNull LivingEntity victim) {
        return this.damageMap.computeIfAbsent(victim.getUniqueId(), k -> new HashMap<>());
    }

    private double getDealtDamage(@NonNull LivingEntity victim, @NonNull LivingEntity damager) {
        return this.getDealtDamageMap(victim).getOrDefault(damager.getUniqueId(), 0D);
    }

    private void addDealtDamage(@NonNull LivingEntity victim, @NonNull LivingEntity damager, double damage) {
        this.getDealtDamageMap(victim).put(damager.getUniqueId(), this.getDealtDamage(victim, damager) + damage);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onXPAbuseSpawnReason(CreatureSpawnEvent event) {
        if (LevelingConfig.DISABLE_XP_BY_SPAWN_REASON.get().contains(event.getSpawnReason())) {
            this.manager.setDropXP(event.getEntity(), false);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onExpGainHit(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof LivingEntity victim)) return;
        if (!(event.getDamageSource().getCausingEntity() instanceof LivingEntity causing)) return;
        // Damage by Vexes summoned by an Evoker pet counts as the Evoker's, so their kills give the Evoker XP.
        LivingEntity damager = causing instanceof org.bukkit.entity.Vex vex && vex.getSummoner() != null ? vex.getSummoner() : causing;
        if (!this.manager.shouldDropXP(victim)) return;
        // Vexes summoned by pets never give XP (otherwise an Evoker's Vexes could be farmed).
        if (victim instanceof org.bukkit.entity.Vex summoned && summoned.getSummoner() != null
            && this.plugin.getPetManager().getPetByMob(summoned.getSummoner()) != null) return;
        if (this.manager.isDisabledWorld(victim.getWorld())) return;

        double damage = event.getFinalDamage();
        if (damage <= 0D) return;

        ActivePet activePet = this.plugin.getPetManager().getPetByMob(damager);
        if (activePet == null) return;

        if (damage > victim.getHealth()) damage = victim.getHealth();

        this.addDealtDamage(victim, damager, damage);
    }

    /**
     * Forget the damage dealt to an entity once it leaves the world (death, despawn, chunk unload),
     * so the damage records don't pile up in memory.
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityRemove(EntityRemoveFromWorldEvent event) {
        this.damageMap.remove(event.getEntity().getUniqueId());
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onExpGainKill(EntityDeathEvent event) {
        LivingEntity victim = event.getEntity();

        // Take (and remove) the damage record of this mob: it's no longer needed after its death.
        Map<UUID, Double> damagers = this.damageMap.remove(victim.getUniqueId());
        if (damagers == null || damagers.isEmpty()) return;

        if (!this.manager.shouldDropXP(victim)) return;
        if (this.manager.isDisabledWorld(victim.getWorld())) return;

        double totalHealth = EntityUtil.getAttribute(victim, Attribute.MAX_HEALTH);

        damagers.forEach((damagerId, damageDealt) -> {
            Entity entity = this.plugin.getServer().getEntity(damagerId);
            if (!(entity instanceof LivingEntity damager)) return;

            ActivePet pet = this.plugin.getPetManager().getPetByMob(damager);
            if (pet == null) return;

            double damagePercent = Math.min(1D, damageDealt / totalHealth);
            if (damagePercent <= 0) return;

            int xpReward = this.manager.rewardXP(pet, victim, damagePercent, event.getDroppedExp());

            if (!this.manager.useCustomXPTable()) {
                event.setDroppedExp(Math.max(0, event.getDroppedExp() - xpReward));
            }
        });
    }
}
