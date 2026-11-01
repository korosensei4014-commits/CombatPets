package su.nightexpress.combatpets.api.pet;

/**
 * Settings for special pet abilities. Set by the plugin from config.yml and read by the version-specific pet AI.
 */
public final class PetAbilitySettings {

    private static volatile boolean wardenSonicBoom       = true;
    private static volatile double  wardenSonicBoomDamage = 10D;

    private static volatile boolean evokerVexes       = false;
    private static volatile int     evokerMaxVexes    = 3;
    private static volatile int     evokerVexLifetime = 30;

    private PetAbilitySettings() {
    }

    /**
     * @param summonVexes whether Evoker pets summon Vexes (Pets.Evoker.Summon_Vexes).
     * @param maxVexes    max Vexes alive per Evoker (Pets.Evoker.Max_Vexes).
     * @param lifetime    Vex lifetime in seconds (Pets.Evoker.Vex_Lifetime).
     */
    public static void setEvoker(boolean summonVexes, int maxVexes, int lifetime) {
        evokerVexes = summonVexes;
        evokerMaxVexes = Math.max(1, maxVexes);
        evokerVexLifetime = Math.max(5, lifetime);
    }

    public static boolean isEvokerVexes() {
        return evokerVexes;
    }

    public static int getEvokerMaxVexes() {
        return evokerMaxVexes;
    }

    public static int getEvokerVexLifetime() {
        return evokerVexLifetime;
    }

    /**
     * @param sonicBoom whether Warden pets use their sonic boom attack (Pets.Warden.Sonic_Boom).
     * @param damage    sonic boom damage (Pets.Warden.Sonic_Boom_Damage).
     */
    public static void setWarden(boolean sonicBoom, double damage) {
        wardenSonicBoom = sonicBoom;
        wardenSonicBoomDamage = Math.max(0D, damage);
    }

    public static boolean isWardenSonicBoom() {
        return wardenSonicBoom;
    }

    public static double getWardenSonicBoomDamage() {
        return wardenSonicBoomDamage;
    }
}
