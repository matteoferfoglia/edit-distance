package edit_distance.entities;

/**
 * Each value of this enum represents an edit operation with its associated cost.
 */
public enum EditOperation {

    // Note: usually, twiddle and kill operations have very high costs and are not used

    /**
     * Copy operation.
     */
    COPY(0),

    /**
     * Replace operation.
     */
    REPLACE(1),

    /**
     * Twiddle operation.
     */
    TWIDDLE(100),

    /**
     * Delete operation.
     */
    DELETE(1),

    /**
     * Insert operation.
     */
    INSERT(1),

    /**
     * Kill operation.
     */
    KILL(100);

    /**
     * Cost of the operation.
     */
    private final int cost;

    /**
     * Constructor.
     *
     * @param operationCost The cost of the operation.
     */
    EditOperation(int operationCost) {
        this.cost = operationCost;
    }

    /**
     * @return The Cost of the operation.
     */
    public int getCost() {
        return cost;
    }

    @Override
    public String toString() {
        return this.name() + "[cost=" + cost + "]";
    }
}
