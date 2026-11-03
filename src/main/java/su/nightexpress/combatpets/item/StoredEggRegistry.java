package su.nightexpress.combatpets.item;

import org.jspecify.annotations.NonNull;
import su.nightexpress.combatpets.PetsPlugin;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Anti-duplication for "Store in Egg": every stored-pet egg has a unique id, recorded here when the egg is
 * created and removed when it hatches. An egg whose id isn't listed (a duplicated copy, or one that already
 * hatched) is refused. Saved to plugins/CombatPets/stored_eggs.txt right after every change.
 */
public class StoredEggRegistry {

    private final PetsPlugin  plugin;
    private final Path        file;
    private final Set<String> validIds;

    public StoredEggRegistry(@NonNull PetsPlugin plugin) {
        this.plugin = plugin;
        this.file = plugin.getDataFolder().toPath().resolve("stored_eggs.txt");
        this.validIds = new HashSet<>();
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
        try {
            Files.createDirectories(this.file.getParent());
            Files.write(this.file, new ArrayList<>(this.validIds), StandardCharsets.UTF_8);
        }
        catch (IOException exception) {
            this.plugin.error("Could not save " + this.file.getFileName() + ": " + exception.getMessage());
        }
    }
}
