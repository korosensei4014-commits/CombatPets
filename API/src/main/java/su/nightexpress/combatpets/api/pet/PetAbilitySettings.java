package su.nightexpress.combatpets.api.pet;

/**
 * Settings for special pet abilities. Set by the plugin from config.yml and read by the version-specific pet AI.
 */
public final class PetAbilitySettings {

    private static volatile boolean wardenSonicBoom       = true;
    private static volatile double  wardenSonicBoomDamage = 10D;

    private PetAbilitySettings() {
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
