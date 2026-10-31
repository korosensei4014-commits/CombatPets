package su.nightexpress.combatpets.api.pet;

/**
 * Distances used by pets when following their owner. Set by the plugin from config.yml
 * ({@code Pets.Follow.*}) and read by the version-specific pet AI.
 * <p>
 * A pet starts walking to its owner only when it is farther than {@link #getStartDistance()} and stops once it is
 * within {@link #getStopDistance()}. The gap between the two keeps the pet from constantly crowding the owner.
 */
public final class PetFollowSettings {

    private static volatile double startDistance = 6D;
    private static volatile double stopDistance  = 3D;

    private PetFollowSettings() {
    }

    /**
     * @param start distance (blocks) above which the pet starts following.
     * @param stop  distance (blocks) at which the pet stops following.
     */
    public static void set(double start, double stop) {
        double safeStop = Math.max(1D, stop);
        double safeStart = Math.max(safeStop + 0.5D, start);

        stopDistance = safeStop;
        startDistance = safeStart;
    }

    public static double getStartDistance() {
        return startDistance;
    }

    public static double getStopDistance() {
        return stopDistance;
    }
}
