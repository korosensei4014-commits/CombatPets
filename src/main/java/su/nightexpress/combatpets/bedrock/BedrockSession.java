package su.nightexpress.combatpets.bedrock;

/**
 * Per-player state of the Bedrock form system.
 * <p>
 * Every form sent to a player gets a new id. A response is only accepted if it belongs to the most recent
 * form and that form was not answered yet. This drops duplicated / late responses (e.g. a response that
 * arrives after another form was already opened) and prevents double submissions.
 */
public final class BedrockSession {

    private long    formId;
    private boolean answered = true;
    private long    lastSubmit;

    /**
     * Registers a new form and returns its id.
     */
    public synchronized long nextForm() {
        this.formId++;
        this.answered = false;
        return this.formId;
    }

    /**
     * Marks the given form as answered.
     * @param id          id of the form the response belongs to.
     * @param submission  whether the response is a submission (button press / submit) and not a close.
     * @param cooldownMs  minimal time between two submissions.
     * @return true if the response should be processed.
     */
    public synchronized boolean accept(long id, boolean submission, long cooldownMs) {
        if (id != this.formId || this.answered) return false;
        this.answered = true;

        if (!submission) return true;

        long now = System.currentTimeMillis();
        if (cooldownMs > 0 && now - this.lastSubmit < cooldownMs) {
            return false;
        }
        this.lastSubmit = now;
        return true;
    }

    /**
     * Invalidates the currently open form (e.g. when a Java inventory is opened instead).
     */
    public synchronized void invalidate() {
        this.answered = true;
    }
}
