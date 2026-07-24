package su.nightexpress.combatpets.nms.paper26p12.ai;

import java.util.UUID;

import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.entity.EntityTeleportEvent;
import org.jspecify.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import su.nightexpress.combatpets.api.pet.PetEntity;

public final class MobUtils {

    private MobUtils() {
    }

    public static @Nullable ServerPlayer getOwner(PetEntity entity) {
        return getOwner(entity.getSharedData().getOwnerId());
    }

    public static @Nullable ServerPlayer getOwner(UUID ownerId) {
        return DedicatedServer.getServer().getPlayerList().getPlayer(ownerId);
    }

    public static void teleportToAround(Mob mob, BlockPos targetPos) {
        for (int attempt = 0; attempt < 10; attempt++) {
            int xd = mob.random.nextIntBetweenInclusive(-3, 3);
            int zd = mob.random.nextIntBetweenInclusive(-3, 3);
            if (Math.abs(xd) >= 2 || Math.abs(zd) >= 2) {
                int yd = mob.random.nextIntBetweenInclusive(-1, 1);
                if (maybeTeleportTo(mob, targetPos.getX() + xd, targetPos.getY() + yd, targetPos.getZ() + zd)) {
                    return;
                }
            }
        }
    }

    private static boolean maybeTeleportTo(Mob mob, int x, int y, int z) {
        if (!canTeleportTo(mob, new BlockPos(x, y, z))) return false;

        EntityTeleportEvent event = CraftEventFactory.callEntityTeleportEvent(mob, x + 0.5, y, z + 0.5);
        if (event.isCancelled() || event.getTo() == null) return false;

        org.bukkit.Location to = event.getTo();
        mob.snapTo(to.getX(), to.getY(), to.getZ(), to.getYaw(), to.getPitch());
        mob.getNavigation().stop();
        return true;
    }

    private static boolean canTeleportTo(Mob mob, BlockPos pos) {
        PathType pathType = WalkNodeEvaluator.getPathTypeStatic(mob, pos);
        if (pathType != PathType.WALKABLE) return false;

        BlockState blockStateBelow = mob.level().getBlockState(pos.below());
        if (blockStateBelow.getBlock() instanceof LeavesBlock) return false;

        BlockPos delta = pos.subtract(mob.blockPosition());
        return mob.level().noCollision(mob, mob.getBoundingBox().move(delta));
    }
}
