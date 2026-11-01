package su.nightexpress.combatpets.nms.paper26p12;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.entity.EntityType;
import org.jspecify.annotations.NullMarked;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;
import su.nightexpress.combatpets.api.pet.PetEntity;
import su.nightexpress.combatpets.api.pet.PetSharedData;
import su.nightexpress.combatpets.nms.paper26p12.pets.animal.BeePet;
import su.nightexpress.combatpets.nms.paper26p12.pets.animal.CatPet;
import su.nightexpress.combatpets.nms.paper26p12.pets.animal.ChickenPet;
import su.nightexpress.combatpets.nms.paper26p12.pets.animal.CowPet;
import su.nightexpress.combatpets.nms.paper26p12.pets.animal.DonkeyPet;
import su.nightexpress.combatpets.nms.paper26p12.pets.animal.FoxPet;
import su.nightexpress.combatpets.nms.paper26p12.pets.animal.HorsePet;
import su.nightexpress.combatpets.nms.paper26p12.pets.animal.IronGolemPet;
import su.nightexpress.combatpets.nms.paper26p12.pets.animal.LlamaPet;
import su.nightexpress.combatpets.nms.paper26p12.pets.animal.MulePet;
import su.nightexpress.combatpets.nms.paper26p12.pets.animal.MushroomCowPet;
import su.nightexpress.combatpets.nms.paper26p12.pets.animal.OcelotPet;
import su.nightexpress.combatpets.nms.paper26p12.pets.animal.PandaPet;
import su.nightexpress.combatpets.nms.paper26p12.pets.animal.PigPet;
import su.nightexpress.combatpets.nms.paper26p12.pets.animal.PolarBearPet;
import su.nightexpress.combatpets.nms.paper26p12.pets.animal.RabbitPet;
import su.nightexpress.combatpets.nms.paper26p12.pets.animal.SheepPet;
import su.nightexpress.combatpets.nms.paper26p12.pets.animal.SnowGolemPet;
import su.nightexpress.combatpets.nms.paper26p12.pets.animal.TraderLlamaPet;
import su.nightexpress.combatpets.nms.paper26p12.pets.animal.TurtlePet;
import su.nightexpress.combatpets.nms.paper26p12.pets.animal.VillagerPet;
import su.nightexpress.combatpets.nms.paper26p12.pets.animal.WanderingTraderPet;
import su.nightexpress.combatpets.nms.paper26p12.pets.animal.WolfPet;
import su.nightexpress.combatpets.nms.paper26p12.pets.brained.AllayPet;
import su.nightexpress.combatpets.nms.paper26p12.pets.brained.ArmadilloPet;
import su.nightexpress.combatpets.nms.paper26p12.pets.brained.BreezePet;
import su.nightexpress.combatpets.nms.paper26p12.pets.brained.CreakingPet;
import su.nightexpress.combatpets.nms.paper26p12.pets.brained.GoatPet;
import su.nightexpress.combatpets.nms.paper26p12.pets.brained.HoglinPet;
import su.nightexpress.combatpets.nms.paper26p12.pets.brained.PiglinBrutePet;
import su.nightexpress.combatpets.nms.paper26p12.pets.brained.PiglinPet;
import su.nightexpress.combatpets.nms.paper26p12.pets.brained.SnifferPet;
import su.nightexpress.combatpets.nms.paper26p12.pets.brained.WardenPet;
import su.nightexpress.combatpets.nms.paper26p12.pets.brained.ZoglinPet;
import su.nightexpress.combatpets.nms.paper26p12.pets.monster.BlazePet;
import su.nightexpress.combatpets.nms.paper26p12.pets.monster.BoggedPet;
import su.nightexpress.combatpets.nms.paper26p12.pets.monster.CaveSpiderPet;
import su.nightexpress.combatpets.nms.paper26p12.pets.monster.CreeperPet;
import su.nightexpress.combatpets.nms.paper26p12.pets.monster.DrownedPet;
import su.nightexpress.combatpets.nms.paper26p12.pets.monster.EndermanPet;
import su.nightexpress.combatpets.nms.paper26p12.pets.monster.EndermitePet;
import su.nightexpress.combatpets.nms.paper26p12.pets.monster.EvokerPet;
import su.nightexpress.combatpets.nms.paper26p12.pets.monster.GhastPet;
import su.nightexpress.combatpets.nms.paper26p12.pets.monster.HuskPet;
import su.nightexpress.combatpets.nms.paper26p12.pets.monster.IllusionerPet;
import su.nightexpress.combatpets.nms.paper26p12.pets.monster.MagmaCubePet;
import su.nightexpress.combatpets.nms.paper26p12.pets.monster.PhantomPet;
import su.nightexpress.combatpets.nms.paper26p12.pets.monster.PillagerPet;
import su.nightexpress.combatpets.nms.paper26p12.pets.monster.RavagerPet;
import su.nightexpress.combatpets.nms.paper26p12.pets.monster.SilverfishPet;
import su.nightexpress.combatpets.nms.paper26p12.pets.monster.SkeletonHorsePet;
import su.nightexpress.combatpets.nms.paper26p12.pets.monster.SkeletonPet;
import su.nightexpress.combatpets.nms.paper26p12.pets.monster.SlimePet;
import su.nightexpress.combatpets.nms.paper26p12.pets.monster.SpiderPet;
import su.nightexpress.combatpets.nms.paper26p12.pets.monster.StrayPet;
import su.nightexpress.combatpets.nms.paper26p12.pets.monster.VexPet;
import su.nightexpress.combatpets.nms.paper26p12.pets.monster.VindicatorPet;
import su.nightexpress.combatpets.nms.paper26p12.pets.monster.WitchPet;
import su.nightexpress.combatpets.nms.paper26p12.pets.monster.WitherPet;
import su.nightexpress.combatpets.nms.paper26p12.pets.monster.WitherSkeletonPet;
import su.nightexpress.combatpets.nms.paper26p12.pets.monster.ZombieHorsePet;
import su.nightexpress.combatpets.nms.paper26p12.pets.monster.ZombiePet;
import su.nightexpress.combatpets.nms.paper26p12.pets.monster.ZombieVillagerPet;
import su.nightexpress.combatpets.nms.paper26p12.pets.monster.ZombifiedPiglinPet;
import su.nightexpress.nightcore.util.BukkitThing;

@NullMarked
public final class EntityInjector {

    private static final Map<EntityType, Creator2<?>> CREATORS = new HashMap<>();

    private EntityInjector() {
    }

    public static void register() {
        // Companion-only pets (never fight).
        addCreator(EntityType.CAMEL, su.nightexpress.combatpets.nms.paper26p12.pets.companion.CamelPet::new);
        addCreator(EntityType.CAMEL_HUSK, su.nightexpress.combatpets.nms.paper26p12.pets.companion.CamelHuskPet::new);
        addCreator(EntityType.COPPER_GOLEM, su.nightexpress.combatpets.nms.paper26p12.pets.companion.CopperGolemPet::new);
        addCreator(EntityType.STRIDER, su.nightexpress.combatpets.nms.paper26p12.pets.companion.StriderPet::new);
        addCreator(EntityType.PARROT, su.nightexpress.combatpets.nms.paper26p12.pets.companion.ParrotPet::new);
        addCreator(EntityType.FROG, su.nightexpress.combatpets.nms.paper26p12.pets.companion.FrogPet::new);
        addCreator(EntityType.AXOLOTL, su.nightexpress.combatpets.nms.paper26p12.pets.companion.AxolotlPet::new);

        addCreator(EntityType.ALLAY, AllayPet::new);
        addCreator(EntityType.ARMADILLO, ArmadilloPet::new);
        addCreator(EntityType.BREEZE, BreezePet::new);
        addCreator(EntityType.GOAT, GoatPet::new);
        addCreator(EntityType.HOGLIN, HoglinPet::new);
        addCreator(EntityType.PIGLIN, PiglinPet::new);
        addCreator(EntityType.PIGLIN_BRUTE, PiglinBrutePet::new);
        addCreator(EntityType.SNIFFER, SnifferPet::new);
        addCreator(EntityType.ZOGLIN, ZoglinPet::new);
        addCreator(EntityType.WARDEN, WardenPet::new);
        addCreator(EntityType.CREAKING, CreakingPet::new);

        addCreator(EntityType.BEE, BeePet::new);
        addCreator(EntityType.BLAZE, BlazePet::new);
        addCreator(EntityType.BOGGED, BoggedPet::new);
        addCreator(EntityType.CAT, CatPet::new);
        addCreator(EntityType.CAVE_SPIDER, CaveSpiderPet::new);
        addCreator(EntityType.CHICKEN, ChickenPet::new);
        addCreator(EntityType.COW, CowPet::new);
        addCreator(EntityType.CREEPER, CreeperPet::new);
        addCreator(EntityType.DONKEY, DonkeyPet::new);
        addCreator(EntityType.DROWNED, DrownedPet::new);
        addCreator(EntityType.ENDERMITE, EndermitePet::new);
        addCreator(EntityType.ENDERMAN, EndermanPet::new);
        addCreator(EntityType.EVOKER, EvokerPet::new);
        addCreator(EntityType.FOX, FoxPet::new);
        addCreator(EntityType.GHAST, GhastPet::new);
        addCreator(EntityType.HORSE, HorsePet::new);
        addCreator(EntityType.HUSK, HuskPet::new);
        addCreator(EntityType.IRON_GOLEM, IronGolemPet::new);
        addCreator(EntityType.ILLUSIONER, IllusionerPet::new);
        addCreator(EntityType.LLAMA, LlamaPet::new);
        addCreator(EntityType.MAGMA_CUBE, MagmaCubePet::new);
        addCreator(EntityType.MULE, MulePet::new);
        addCreator(EntityType.MOOSHROOM, MushroomCowPet::new);
        addCreator(EntityType.OCELOT, OcelotPet::new);
        addCreator(EntityType.PANDA, PandaPet::new);
        addCreator(EntityType.PHANTOM, PhantomPet::new);
        addCreator(EntityType.PIG, PigPet::new);
        addCreator(EntityType.PILLAGER, PillagerPet::new);
        addCreator(EntityType.POLAR_BEAR, PolarBearPet::new);
        addCreator(EntityType.RABBIT, RabbitPet::new);
        addCreator(EntityType.RAVAGER, RavagerPet::new);
        addCreator(EntityType.SHEEP, SheepPet::new);
        addCreator(EntityType.SILVERFISH, SilverfishPet::new);
        addCreator(EntityType.SKELETON, SkeletonPet::new);
        addCreator(EntityType.SKELETON_HORSE, SkeletonHorsePet::new);
        addCreator(EntityType.SLIME, SlimePet::new);
        addCreator(EntityType.SNOW_GOLEM, SnowGolemPet::new);
        addCreator(EntityType.SPIDER, SpiderPet::new);
        addCreator(EntityType.STRAY, StrayPet::new);
        addCreator(EntityType.TRADER_LLAMA, TraderLlamaPet::new);
        addCreator(EntityType.TURTLE, TurtlePet::new);
        addCreator(EntityType.VEX, VexPet::new);
        addCreator(EntityType.VILLAGER, VillagerPet::new);
        addCreator(EntityType.VINDICATOR, VindicatorPet::new);
        addCreator(EntityType.WANDERING_TRADER, WanderingTraderPet::new);
        addCreator(EntityType.WITCH, WitchPet::new);
        addCreator(EntityType.WITHER, WitherPet::new);
        addCreator(EntityType.WITHER_SKELETON, WitherSkeletonPet::new);
        addCreator(EntityType.WOLF, WolfPet::new);
        addCreator(EntityType.ZOMBIE, ZombiePet::new);
        addCreator(EntityType.ZOMBIE_HORSE, ZombieHorsePet::new);
        addCreator(EntityType.ZOMBIE_VILLAGER, ZombieVillagerPet::new);
        addCreator(EntityType.ZOMBIFIED_PIGLIN, ZombifiedPiglinPet::new);
    }

    @Deprecated
    private static <T extends Mob & PetEntity> void addCreator(EntityType type, Creator<T> creator) {
        addCreator(type, (level, data) -> creator.create(level));
    }

    private static <T extends Mob & PetEntity> void addCreator(EntityType type, Creator2<T> creator) {
        CREATORS.put(type, creator);
    }

    public interface Creator<T extends Mob & PetEntity> {


        T create(ServerLevel level);
    }

    public interface Creator2<T extends Mob & PetEntity> {


        T create(ServerLevel level, PetSharedData data);
    }

    public static Set<EntityType> getTypes() {
        return new HashSet<>(CREATORS.keySet());
    }


    public static Mob spawn(EntityType type, ServerLevel level, PetSharedData sharedData) {
        Creator2<?> creator = CREATORS.get(type);
        if (creator == null) throw new IllegalStateException("No mob creator exists for the '" + BukkitThing
            .getAsString(type) + "' entity type.");

        return creator.create(level, sharedData);
    }
}
