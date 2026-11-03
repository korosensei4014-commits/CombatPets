package su.nightexpress.combatpets.item;

import org.jspecify.annotations.NonNull;
import su.nightexpress.combatpets.PetsPlugin;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Anti-duplication for "Store in Egg": every stored-pet egg has a unique id, recorded here when the egg is
 * created and removed when it hatches. An egg whose id isn't listed (a duplicated copy, or one that already
 * hatched) is refused.
 * <p>
 * Checks use the in-memory set. The file (plugins/CombatPets/stored_eggs.txt) is written right after every
 * change, but on one dedicated background thread, so the main thread never waits for the disk. Writes keep their
 * order and replace the file in one step (temp file + move), so it is never left half-written.
 */
public class StoredEggRegistry {

    private final PetsPlugin      plugin;
    private final Path            file;
    private final Set<String>     validIds;
    private final ExecutorService writer;

    public StoredEggRegistry(@NonNull PetsPlugin plugin) {
        this.plugin = plugin;
        this.file = plugin.getDataFolder().toPath().resolve("stored_eggs.txt");
        this.validIds = new HashSet<>();
        this.writer = Executors.newSingleThreadExecutor(runnable -> {
            Thread thread = new Thread(runnable, "CombatPets-StoredEggs-Writer");
            thread.setDaemon(true);
            return thread;
        });
    }

    public void load() {
        this.validIds.clear();
        if (!Files.exists(this.file)) return;
        try {
            for (String line : Files.readAllLines(this.file, StandardCharsets.UTF_8)) {
                String id = line.trim();
                if (!id.isEmpty()) this.validIds.add(id);
            }
        }
        catch (IOException exception) {
            this.plugin.error("Could not read " + this.file.getFileName() + ": " + exception.getMessage());
        }
    }

    /**
     * Finishes pending writes (waits up to 10 seconds) and stops the writer thread. Called on shutdown / reload.
     */
    public void shutdown() {
        this.writer.shutdown();
        try {
            if (!this.writer.awaitTermination(10, TimeUnit.SECONDS)) {
                this.plugin.error("Timed out saving " + this.file.getFileName() + ".");
            }
        }
        catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
        }
    }

    public boolean isValid(@NonNull String id) {
        return this.validIds.contains(id);
    }

    public void add(@NonNull String id) {
        this.validIds.add(id);
        this.save();
    }

    /**
     * @return true if the id was valid and is now used up.
     */
    public boolean consume(@NonNull String id) {
        if (!this.validIds.remove(id)) return false;
        this.save();
        return true;
    }

    private void save() {
        List<String> snapshot = new ArrayList<>(this.validIds); // Copied on the main thread.
        if (this.writer.isShutdown()) {
            this.write(snapshot); // Plugin is stopping: write directly.
            return;
        }
        this.writer.execute(() -> this.write(snapshot));
    }

    private void write(@NonNull List<String> ids) {
        try {
            Files.createDirectories(this.file.getParent());
            Path temp = this.file.resolveSibling(this.file.getFileName() + ".tmp");
            Files.write(temp, ids, StandardCharsets.UTF_8);
            try {
                Files.move(temp, this.file, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            }
            catch (AtomicMoveNotSupportedException exception) {
                Files.move(temp, this.file, StandardCopyOption.REPLACE_EXISTING);
            }
        }
        catch (IOException exception) {
            this.plugin.error("Could not save " + this.file.getFileName() + ": " + exception.getMessage());
        }
    }
}
