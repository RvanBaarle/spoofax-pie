package mb.statix.common;

/**
 * Meta solution fields.
 */
public final class SolutionMeta {

    private final int expandedQueries;
    private final int expandedRules;

    public SolutionMeta(int expandedQueries, int expandedRules) {
        this.expandedQueries = expandedQueries;
        this.expandedRules = expandedRules;
    }

    public SolutionMeta() {
        this(0, 0);
    }

    public int getExpandedQueries() {
        return expandedQueries;
    }

    public int getExpandedRules() {
        return expandedRules;
    }

    public SolutionMeta withExpandedQueries(int expandedQueries) {
        return new SolutionMeta(expandedQueries, expandedRules);
    }
    public SolutionMeta withExpandedRules(int expandedRules) {
        return new SolutionMeta(expandedQueries, expandedRules);
    }

    @Override public String toString() {
        return "meta {\n" +
            "expandedQueries=" + expandedQueries + ",\n" +
            "expandedRules=" + expandedRules + "\n" +
            "}";
    }
}
